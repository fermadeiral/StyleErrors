/*******************************************************************************
 * Copyright 2019 Mountain Fog, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package ai.idylnlp.models.deeplearning.training;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.api.storage.StatsStorage;
import org.deeplearning4j.earlystopping.EarlyStoppingConfiguration;
import org.deeplearning4j.earlystopping.EarlyStoppingResult;
import org.deeplearning4j.earlystopping.saver.LocalFileModelSaver;
import org.deeplearning4j.earlystopping.scorecalc.DataSetLossCalculator;
import org.deeplearning4j.earlystopping.termination.MaxTimeIterationTerminationCondition;
import org.deeplearning4j.earlystopping.termination.ScoreImprovementEpochTerminationCondition;
import org.deeplearning4j.earlystopping.trainer.EarlyStoppingTrainer;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.LSTM;
import org.deeplearning4j.nn.conf.layers.RnnOutputLayer;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.api.TrainingListener;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.ui.stats.StatsListener;
import org.deeplearning4j.ui.storage.FileStatsStorage;
import org.deeplearning4j.util.ModelSerializer;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import ai.idylnlp.model.nlp.annotation.AnnotationTypes;
import ai.idylnlp.model.nlp.subjects.CoNLL2003SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.IdylNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.OpenNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.models.ObjectStreamUtils;
import ai.idylnlp.models.deeplearning.training.model.DeepLearningTrainingDefinition;
import ai.idylnlp.models.deeplearning.training.model.HyperParameters;
import ai.idylnlp.nlp.recognizer.deep.NameSampleDataSetIterator;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.ObjectStream;

public class DeepLearningEntityModelOperations {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningEntityModelOperations.class);

  private Gson gson;

  public DeepLearningEntityModelOperations() {

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeSpecialFloatingPointValues();
    gson = gsonBuilder.setPrettyPrinting().create();

  }

  /**
   * Trains a deep learning model.
   * @param parameters The {@link BasicDeepLearningTrainingParameters parameters} for the training.
   * @param serializedModelFile The output trained model {@link File file}.
   * @return The {@link MultiLayerNetwork network}.
   * @throws IOException Thrown if the cross-validation encounters an error.
   */
  public String train(DeepLearningTrainingDefinition definition) throws IOException {

    LOGGER.info("Starting training.");

    GsonBuilder gsonBuilder = new GsonBuilder();
    gsonBuilder.serializeSpecialFloatingPointValues();
    Gson gson = gsonBuilder.setPrettyPrinting().create();
    String jsonString = gson.toJson(definition, DeepLearningTrainingDefinition.class);
    LOGGER.debug(jsonString);

    final File wordVectorsFile = new File(definition.getTrainingData().getWordVectorsFile());
    final WordVectors wordVectors = WordVectorSerializer.loadStaticModel(wordVectorsFile);
    final int vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;
    final String[] labels = getLabels(definition.getEntityType());

    LOGGER.debug("Using vector size: {}", vectorSize);

    // Dataset for training.
    final ObjectStream<NameSample> trainingSampleStream = ObjectStreamUtils.getObjectStream(getSubjectOfTraining(definition));
    final DataSetIterator trainDataSetIterator = new NameSampleDataSetIterator(trainingSampleStream, wordVectors, vectorSize, definition.getHyperParameters().getWindowSize(), labels, definition.getHyperParameters().getBatchSize());

    // Dataset for evaluation.
    final ObjectStream<NameSample> evaluationSampleStream = ObjectStreamUtils.getObjectStream(getSubjectOfEvaluation(definition));
    final DataSetIterator evaluationDataSetIterator = new NameSampleDataSetIterator(evaluationSampleStream, wordVectors, vectorSize, definition.getHyperParameters().getWindowSize(), labels, definition.getHyperParameters().getBatchSize());

    // Build the networks.
    final MultiLayerConfiguration multiLayerConfiguration = buildNetworkConfiguration(definition.getHyperParameters(), vectorSize);
    MultiLayerNetwork multiLayerNetwork = buildNetwork(multiLayerConfiguration, definition);

    // Get the early stopping parameters.
    if(definition.getEarlyTermination() != null) {

      LOGGER.info("Enabling early-termination training.");

      EarlyStoppingConfiguration.Builder<MultiLayerNetwork> esConf = new EarlyStoppingConfiguration.Builder<MultiLayerNetwork>();

      if(definition.getEarlyTermination().getMaxEpochs() != null) {
        esConf.epochTerminationConditions(new ScoreImprovementEpochTerminationCondition(definition.getEarlyTermination().getMaxEpochs()));
      }

      if(definition.getEarlyTermination().getMaxMinutes() != null) {
        esConf.iterationTerminationConditions(new MaxTimeIterationTerminationCondition(definition.getEarlyTermination().getMaxMinutes(), TimeUnit.MINUTES));
      }

      esConf.scoreCalculator(new DataSetLossCalculator(evaluationDataSetIterator, true));
      esConf.evaluateEveryNEpochs(1);
      esConf.modelSaver(new LocalFileModelSaver(System.getProperty("java.io.tmpdir")));
      EarlyStoppingConfiguration<MultiLayerNetwork> earlyStoppingConfiguration = esConf.build();

      EarlyStoppingTrainer trainer = new EarlyStoppingTrainer(earlyStoppingConfiguration, multiLayerNetwork, trainDataSetIterator);
      EarlyStoppingResult<MultiLayerNetwork> result = trainer.fit();

      multiLayerNetwork = result.getBestModel();

      LOGGER.info("Termination reason: " + result.getTerminationReason());
      LOGGER.info("Termination details: " + result.getTerminationDetails());
      LOGGER.info("Total epochs: " + result.getTotalEpochs());
      LOGGER.info("Best epoch number: " + result.getBestModelEpoch());
      LOGGER.info("Score at best epoch: " + result.getBestModelScore());

    } else {

      LOGGER.info("Doing single node training.");

      for (int i = 1; i <= definition.getHyperParameters().getEpochs(); i++) {

        multiLayerNetwork.fit(trainDataSetIterator);
        trainDataSetIterator.reset();
        LOGGER.info("Finished epoch {}", i);

        Evaluation evaluation = new Evaluation();

        while (evaluationDataSetIterator.hasNext()) {

          DataSet t = evaluationDataSetIterator.next();

          INDArray features = t.getFeatures();
          INDArray lables = t.getLabels();
          INDArray inMask = t.getFeaturesMaskArray();
          INDArray outMask = t.getLabelsMaskArray();
          INDArray predicted = multiLayerNetwork.output(features, false, inMask, outMask);

          evaluation.evalTimeSeries(lables, predicted, outMask);

        }

        evaluationDataSetIterator.reset();

        LOGGER.info("Evaluation statistics:\n{}", evaluation.stats());

      }

    }

    // Serialize the model to a file.
    final File serializedModelFile = new File(definition.getOutput().getOutputFile());
    ModelSerializer.writeModel(multiLayerNetwork, serializedModelFile, false);
    LOGGER.info("Model serialized to {}", serializedModelFile.getAbsolutePath());

    return UUID.randomUUID().toString();

  }

  public Gson getGson() {
    return gson;
  }

  public DeepLearningTrainingDefinition deserializeDefinition(String json) throws IOException {
    return gson.fromJson(json, DeepLearningTrainingDefinition.class);
  }

  private MultiLayerNetwork buildNetwork(MultiLayerConfiguration multiLayerConfiguration, DeepLearningTrainingDefinition definition) {

    MultiLayerNetwork multiLayerNetwork = new MultiLayerNetwork(multiLayerConfiguration);
    multiLayerNetwork.init();

    List<TrainingListener> listeners = new ArrayList<>();

    if(StringUtils.isNotEmpty(definition.getOutput().getStatsFile())) {

      File statsFile = new File(definition.getOutput().getStatsFile());
      StatsStorage statsStorage = new FileStatsStorage(statsFile);

      listeners.add(new StatsListener(statsStorage));

    }

    listeners.add(new ScoreIterationListener(definition.getMonitoring().getScoreIteration()));

    multiLayerNetwork.setListeners(listeners);

    return multiLayerNetwork;

  }

  private MultiLayerConfiguration buildNetworkConfiguration(HyperParameters hp, int vectorSize) {

    NeuralNetConfiguration.Builder builder = new NeuralNetConfiguration.Builder();

    builder.seed(hp.getSeed());
    builder.biasInit(hp.getNetworkConfigurationParameters().getBiasInit());
    builder.convolutionMode(hp.getConvolutionModeParam());
    builder.dropOut(hp.getNetworkConfigurationParameters().getDropOut());

    if(hp.getNetworkConfigurationParameters().getRegularizationParameters().getRegularization()) {
	    builder.l1(hp.getNetworkConfigurationParameters().getRegularizationParameters().getL1());
	    builder.l1Bias(hp.getNetworkConfigurationParameters().getRegularizationParameters().getL1Bias());
	    builder.l2(hp.getNetworkConfigurationParameters().getRegularizationParameters().getL2());
	    builder.l2Bias(hp.getNetworkConfigurationParameters().getRegularizationParameters().getL2Bias());
    }

    builder.updater(hp.getNetworkConfigurationParameters().getUpdaterParameters().getUpdaterParam());
    builder.optimizationAlgo(hp.getNetworkConfigurationParameters().getOptimizationAlgorithmParam());
    builder.gradientNormalization(hp.getNetworkConfigurationParameters().getGradientNormalizationParam());
    builder.gradientNormalizationThreshold(hp.getNetworkConfigurationParameters().getGradientNormalizationThreshold());
    builder.weightInit(hp.getNetworkConfigurationParameters().getWeightInitParam());

    MultiLayerConfiguration multiLayerConfiguration = builder.list()
        .layer(0, new LSTM.Builder()
            .nIn(vectorSize)
            .nOut(256)
            .activation(Activation.TANH)
            .build())
        .layer(1, new RnnOutputLayer.Builder()
            .nIn(256)
            .nOut(3) // Equal to the number of labels (START, CONT, END)
            // The softmax function is often used in the final layer of a neural
            // network-based classifier. Such networks are commonly trained under
            // a log loss (or cross-entropy) regime, giving a non-linear variant
            // of multinomial logistic regression.
            .activation(Activation.SOFTMAX)
            .lossFunction(LossFunctions.LossFunction.MCXENT)
        .build())
        
        .pretrain(hp.getNetworkConfigurationParameters().isPretrain())
        .backprop(hp.getNetworkConfigurationParameters().isBackprop())
        .build();

    return multiLayerConfiguration;

  }

  private String[] getLabels(String entityType) {

    return new String[] { entityType + "-start", entityType + "-cont", "other" };

  }

  private SubjectOfTrainingOrEvaluation getSubjectOfTraining(DeepLearningTrainingDefinition definition) {

    final String trainingInputFile = definition.getTrainingData().getInputFile();

    if(definition.getTrainingData().getFormat().equalsIgnoreCase(AnnotationTypes.IDYLNLP.getName())) {
      return new IdylNLPSubjectOfTrainingOrEvaluation(trainingInputFile, definition.getTrainingData().getAnnotationsFile());
    } else if(definition.getTrainingData().getFormat().equalsIgnoreCase(AnnotationTypes.CONLL2003.getName())) {
      return new CoNLL2003SubjectOfTrainingOrEvaluation(trainingInputFile);
    } else {
      LOGGER.info("Defaulting to OpenNLP subject of training.");
      return new OpenNLPSubjectOfTrainingOrEvaluation(trainingInputFile);
    }

  }

  private SubjectOfTrainingOrEvaluation getSubjectOfEvaluation(DeepLearningTrainingDefinition definition) {

    final String trainingInputFile = definition.getEvaluationData().getInputFile();

    if(definition.getEvaluationData().getFormat().equalsIgnoreCase(AnnotationTypes.IDYLNLP.getName())) {
      return new IdylNLPSubjectOfTrainingOrEvaluation(trainingInputFile, definition.getTrainingData().getAnnotationsFile());
    } else if(definition.getEvaluationData().getFormat().equalsIgnoreCase(AnnotationTypes.CONLL2003.getName())) {
      return new CoNLL2003SubjectOfTrainingOrEvaluation(trainingInputFile);
    } else {
      LOGGER.info("Defaulting to OpenNLP subject of training.");
      return new OpenNLPSubjectOfTrainingOrEvaluation(trainingInputFile);
    }

  }

}
