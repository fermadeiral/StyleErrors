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
package ai.idylnlp.models.opennlp.training;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.opennlp.custom.encryption.OpenNLPEncryptionFactory;
import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.Constants;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.training.AccuracyEvaluationResult;
import ai.idylnlp.models.ModelOperationsUtils;
import ai.idylnlp.models.opennlp.training.model.ModelSeparateDataValidationOperations;
import ai.idylnlp.models.opennlp.training.model.ModelTrainingOperations;
import ai.idylnlp.models.opennlp.training.model.TrainingAlgorithm;
import ai.idylnlp.training.definition.model.TrainingDefinitionReader;
import opennlp.tools.cmdline.postag.POSEvaluationErrorListener;
import opennlp.tools.cmdline.postag.POSTaggerFineGrainedReportListener;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.postag.POSEvaluator;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSSample;
import opennlp.tools.postag.POSTaggerEvaluationMonitor;
import opennlp.tools.postag.POSTaggerFactory;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.postag.WordTagSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Operations for training and validating part of speech models.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class PartOfSpeechModelOperations implements ModelTrainingOperations, ModelSeparateDataValidationOperations<AccuracyEvaluationResult> {

  private static final Logger LOGGER = LogManager.getLogger(PartOfSpeechModelOperations.class);

  /**
   * Performs part-of-speech model training using a training definition file.
   * @param reader A {@link TrainingDefinitionReader}.
   * @return The generated model's ID.
   * @throws IOException Thrown if the model creation fails.
   */
  public static String train(TrainingDefinitionReader reader) throws IOException {

    final PartOfSpeechModelOperations ops = new PartOfSpeechModelOperations();

    final SubjectOfTrainingOrEvaluation subjectOfTraining = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    final String modelFile = reader.getTrainingDefinition().getModel().getFile();
    final String language = reader.getTrainingDefinition().getModel().getLanguage();
    final String encryptionKey = reader.getTrainingDefinition().getModel().getEncryptionkey();
    final int cutOff = reader.getTrainingDefinition().getAlgorithm().getCutoff().intValue();
    final int iterations = reader.getTrainingDefinition().getAlgorithm().getIterations().intValue();
    final int threads = reader.getTrainingDefinition().getAlgorithm().getThreads().intValue();
    final String algorithm = reader.getTrainingDefinition().getAlgorithm().getName();

    final LanguageCode languageCode = LanguageCode.getByCodeIgnoreCase(language);

    if(algorithm.equalsIgnoreCase(TrainingAlgorithm.PERCEPTRON.getName())) {

      return ops.trainPerceptron(subjectOfTraining, modelFile, languageCode, encryptionKey, cutOff, iterations);

    } else if(algorithm.equalsIgnoreCase(TrainingAlgorithm.MAXENT_QN.getName())) {

      final double l1 = reader.getTrainingDefinition().getAlgorithm().getL1().doubleValue();
      final double l2 = reader.getTrainingDefinition().getAlgorithm().getL2().doubleValue();
      int m = reader.getTrainingDefinition().getAlgorithm().getM().intValue();
      int max = reader.getTrainingDefinition().getAlgorithm().getMax().intValue();

      return ops.trainMaxEntQN(subjectOfTraining, modelFile, languageCode, encryptionKey, cutOff, iterations, threads, l1, l2, m, max);

    } else {

      throw new IOException("Invalid algorithm specified in the training definition file: " + algorithm);

    }

  }

  @Override
  public AccuracyEvaluationResult separateDataEvaluate(SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation, String modelFileName, String encryptionKey) throws IOException {

    LOGGER.info("Doing model evaluation using separate training data.");

    // Set the encryption key.
    OpenNLPEncryptionFactory.getDefault().setKey(encryptionKey);

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(SubjectOfTrainingOrEvaluation.getInputFile()));
    ObjectStream<POSSample> sample = new WordTagSampleStream(new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8));

    POSModel model = new POSModel(new File(modelFileName));
    POSTaggerME posTaggerME = new POSTaggerME(model);

    POSTaggerEvaluationMonitor missclassifiedListener = new POSEvaluationErrorListener();
    POSTaggerFineGrainedReportListener reportListener = new POSTaggerFineGrainedReportListener(System.out);

    POSEvaluator evaluator = new POSEvaluator(posTaggerME, missclassifiedListener, reportListener);

    evaluator.evaluate(sample);

    // Clear the encryption key.
    OpenNLPEncryptionFactory.getDefault().clearKey();

    return new AccuracyEvaluationResult(evaluator.getWordAccuracy(), evaluator.getWordCount());

  }

  @Override
  public String trainMaxEntQN(SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation, String modelFile, LanguageCode language, String encryptionKey, int cutOff, int iterations, int threads, double l1, double l2, int m, int max) throws IOException {

    LOGGER.info("Beginning parts-of-speech model training. Output model will be: " + modelFile);

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(SubjectOfTrainingOrEvaluation.getInputFile()));
    ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8);
    ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);

    TrainingParameters trainParams = new TrainingParameters();
    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.MAXENT_QN.getAlgorithm());
    trainParams.put(TrainingParameters.THREADS_PARAM, Integer.toString(threads));

    trainParams.put(QNTrainer.L1COST_PARAM, String.valueOf(l1));
    trainParams.put(QNTrainer.L2COST_PARAM, String.valueOf(l2));
    trainParams.put(QNTrainer.M_PARAM, String.valueOf(m));
    trainParams.put(QNTrainer.MAX_FCT_EVAL_PARAM, String.valueOf(max));

    POSTaggerFactory posTaggerFactory = new POSTaggerFactory();

    // Set the encryption key.
    OpenNLPEncryptionFactory.getDefault().setKey(encryptionKey);

    POSModel model = POSTaggerME.train(language.getAlpha3().toString(), sampleStream, trainParams, posTaggerFactory);

    BufferedOutputStream modelOut = null;

    String modelId = "";

    try {

      modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
      modelId = model.serialize(modelOut);

    } finally {

      if (modelOut != null) {
        modelOut.close();
      }

      lineStream.close();

      // Clear the encryption key.
      OpenNLPEncryptionFactory.getDefault().clearKey();

    }

    return modelId;

  }

  @Override
  public String trainPerceptron(SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation, String modelFile, LanguageCode language, String encryptionKey, int cutOff, int iterations) throws IOException {

    LOGGER.info("Beginning parts-of-speech model training. Output model will be: " + modelFile);

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(SubjectOfTrainingOrEvaluation.getInputFile()));
    ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8);
    ObjectStream<POSSample> sampleStream = new WordTagSampleStream(lineStream);

    TrainingParameters trainParams = new TrainingParameters();
    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.PERCEPTRON.getAlgorithm());

    POSTaggerFactory posTaggerFactory = new POSTaggerFactory();

    // Set the encryption key.
    OpenNLPEncryptionFactory.getDefault().setKey(encryptionKey);

    POSModel model = POSTaggerME.train(language.getAlpha3().toString(), sampleStream, trainParams, posTaggerFactory);

    BufferedOutputStream modelOut = null;

    String modelId = "";

    try {

      modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
      modelId = model.serialize(modelOut);

    } finally {

      if (modelOut != null) {
        modelOut.close();
      }

      lineStream.close();

      // Clear the encryption key.
      OpenNLPEncryptionFactory.getDefault().clearKey();

    }

    return modelId;

  }

}
