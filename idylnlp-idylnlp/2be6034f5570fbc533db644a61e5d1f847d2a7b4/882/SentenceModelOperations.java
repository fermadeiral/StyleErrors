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

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.Constants;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.training.FMeasure;
import ai.idylnlp.model.training.FMeasureModelValidationResult;
import ai.idylnlp.models.ModelOperationsUtils;
import ai.idylnlp.models.opennlp.training.model.ModelCrossValidationOperations;
import ai.idylnlp.models.opennlp.training.model.ModelSeparateDataValidationOperations;
import ai.idylnlp.models.opennlp.training.model.ModelTrainingOperations;
import ai.idylnlp.models.opennlp.training.model.TrainingAlgorithm;
import ai.idylnlp.training.definition.model.TrainingDefinitionReader;
import opennlp.tools.cmdline.sentdetect.SentenceEvaluationErrorListener;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.sentdetect.SDCrossValidator;
import opennlp.tools.sentdetect.SentenceDetectorEvaluationMonitor;
import opennlp.tools.sentdetect.SentenceDetectorEvaluator;
import opennlp.tools.sentdetect.SentenceDetectorFactory;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.sentdetect.SentenceSample;
import opennlp.tools.sentdetect.SentenceSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Operations for training and validating sentence models.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SentenceModelOperations implements ModelTrainingOperations, ModelSeparateDataValidationOperations<FMeasureModelValidationResult>, ModelCrossValidationOperations<FMeasureModelValidationResult> {

  private static final Logger LOGGER = LogManager.getLogger(SentenceModelOperations.class);

  /**
   * Performs sentence model training using a training definition file.
   * @param reader A {@link TrainingDefinitionReader}.
   * @return The generated model's ID.
   * @throws IOException Thrown if the model creation fails.
   */
  public static String train(TrainingDefinitionReader reader) throws IOException {

    final SentenceModelOperations ops = new SentenceModelOperations();

    final SubjectOfTrainingOrEvaluation subjectOfTraining = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    final String modelFile = reader.getTrainingDefinition().getModel().getFile();
    final String language = reader.getTrainingDefinition().getModel().getLanguage();
    final int cutOff = reader.getTrainingDefinition().getAlgorithm().getCutoff().intValue();
    final int iterations = reader.getTrainingDefinition().getAlgorithm().getIterations().intValue();
    final int threads = reader.getTrainingDefinition().getAlgorithm().getThreads().intValue();
    final String algorithm = reader.getTrainingDefinition().getAlgorithm().getName();

    final LanguageCode languageCode = LanguageCode.getByCodeIgnoreCase(language);

    if(algorithm.equalsIgnoreCase(TrainingAlgorithm.PERCEPTRON.getName())) {

      return ops.trainPerceptron(subjectOfTraining, modelFile, languageCode, cutOff, iterations);

    } else if(algorithm.equalsIgnoreCase(TrainingAlgorithm.MAXENT_QN.getName())) {

      final double l1 = reader.getTrainingDefinition().getAlgorithm().getL1().doubleValue();
      final double l2 = reader.getTrainingDefinition().getAlgorithm().getL2().doubleValue();
      int m = reader.getTrainingDefinition().getAlgorithm().getM().intValue();
      int max = reader.getTrainingDefinition().getAlgorithm().getMax().intValue();

      return ops.trainMaxEntQN(subjectOfTraining, modelFile, languageCode, cutOff, iterations, threads, l1, l2, m, max);

    } else {

      throw new IOException("Invalid algorithm specified in the training definition file: " + algorithm);

    }

  }

  /**
   * Performs cross-validation of a sentence model.
   * @param reader A {@link TrainingDefinitionReader}.
   * @param folds The number of cross-validation folds.
   * @return A {@link FMeasureModelValidationResult}.
   * @throws IOException Thrown if the model cannot be validated.
   */
  public static FMeasureModelValidationResult crossValidate(TrainingDefinitionReader reader, int folds) throws IOException {

    final String language = reader.getTrainingDefinition().getModel().getLanguage();
    final int iterations = reader.getTrainingDefinition().getAlgorithm().getIterations().intValue();
    final int cutoff = reader.getTrainingDefinition().getAlgorithm().getCutoff().intValue();
    final String algorithm = reader.getTrainingDefinition().getAlgorithm().getName();
    final double l1 = reader.getTrainingDefinition().getAlgorithm().getL1().doubleValue();
    final double l2 = reader.getTrainingDefinition().getAlgorithm().getL2().doubleValue();
    final int m = reader.getTrainingDefinition().getAlgorithm().getM().intValue();
    final int max = reader.getTrainingDefinition().getAlgorithm().getMax().intValue();

    final LanguageCode languageCode = LanguageCode.getByCodeIgnoreCase(language);

    // Get the subject of training based on what's specified in the training definition file.
    final SubjectOfTrainingOrEvaluation subjectOfTraining = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    // Now we can set up the entity model operations.
    final SentenceModelOperations sentenceModelOperations = new SentenceModelOperations();

    FMeasureModelValidationResult result = null;

    if(StringUtils.equalsIgnoreCase(algorithm, TrainingAlgorithm.PERCEPTRON.getName())) {

      result = sentenceModelOperations.crossValidationEvaluatePerceptron(subjectOfTraining, languageCode, iterations, cutoff, folds);

    } else if(StringUtils.equalsIgnoreCase(algorithm, TrainingAlgorithm.MAXENT_QN.getName())) {

      result = sentenceModelOperations.crossValidationEvaluateMaxEntQN(subjectOfTraining, languageCode, iterations, cutoff, folds, l1, l2, m, max);

    } else {

      throw new IOException("Invalid algorithm specified in the training definition file: " + algorithm);

    }

    return result;

  }

  @Override
  public FMeasureModelValidationResult crossValidationEvaluateMaxEntQN(SubjectOfTrainingOrEvaluation subjectOfTraining, LanguageCode language, int iterations, int cutOff, int folds, double l1, double l2, int m, int max) throws IOException {

    LOGGER.info("Doing model evaluation using cross-validation with {} folds using input {}.", folds, subjectOfTraining.getInputFile());

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(subjectOfTraining.getInputFile()));
    ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8);
    ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);

    TrainingParameters trainParams = new TrainingParameters();
    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.MAXENT_QN.getAlgorithm());
    trainParams.put(TrainingParameters.THREADS_PARAM, Integer.toString(1));

    SentenceDetectorFactory sentenceDetectorFactory = new SentenceDetectorFactory(language.getAlpha3().toString(), true, new Dictionary(), null);
    SentenceDetectorEvaluationMonitor monitor = new SentenceEvaluationErrorListener();

    SDCrossValidator evaluator = new SDCrossValidator(language.getAlpha3().toString(), trainParams, sentenceDetectorFactory, monitor);
    evaluator.evaluate(sampleStream, folds);

    final FMeasure fmeasure = new FMeasure(evaluator.getFMeasure().getPrecisionScore(),
        evaluator.getFMeasure().getRecallScore(), evaluator.getFMeasure().getFMeasure());

    return new FMeasureModelValidationResult(fmeasure);

  }

  @Override
  public FMeasureModelValidationResult crossValidationEvaluatePerceptron(SubjectOfTrainingOrEvaluation subjectOfTraining, LanguageCode language, int iterations, int cutOff, int folds) throws IOException {

    LOGGER.info("Doing model evaluation using cross-validation with {} folds using input {}.", folds, subjectOfTraining.getInputFile());

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(subjectOfTraining.getInputFile()));
    ObjectStream<String> lineStream = new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8);
    ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);

    TrainingParameters trainParams = new TrainingParameters();
    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.PERCEPTRON.getAlgorithm());
    trainParams.put(TrainingParameters.THREADS_PARAM, Integer.toString(1));

    SentenceDetectorFactory sentenceDetectorFactory = new SentenceDetectorFactory(language.getAlpha3().toString(), true, new Dictionary(), null);
    SentenceDetectorEvaluationMonitor monitor = new SentenceEvaluationErrorListener();

    SDCrossValidator evaluator = new SDCrossValidator(language.getAlpha3().toString(), trainParams, sentenceDetectorFactory, monitor);
    evaluator.evaluate(sampleStream, folds);

    final FMeasure fmeasure = new FMeasure(evaluator.getFMeasure().getPrecisionScore(),
        evaluator.getFMeasure().getRecallScore(), evaluator.getFMeasure().getFMeasure());

    return new FMeasureModelValidationResult(fmeasure);

  }

  @Override
  public FMeasureModelValidationResult separateDataEvaluate(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFileName) throws IOException {

    LOGGER.info("Doing model evaluation using separate training data.");

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(subjectOfTraining.getInputFile()));
    ObjectStream<SentenceSample> sample = new SentenceSampleStream(new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8));

    SentenceModel model = new SentenceModel(new File(modelFileName));
    SentenceDetectorME nameFinderME = new SentenceDetectorME(model);

    SentenceDetectorEvaluator evaluator = new SentenceDetectorEvaluator(nameFinderME);

    evaluator.evaluate(sample);

    final FMeasure fmeasure = new FMeasure(evaluator.getFMeasure().getPrecisionScore(),
        evaluator.getFMeasure().getRecallScore(), evaluator.getFMeasure().getFMeasure());

    return new FMeasureModelValidationResult(fmeasure);

  }

  @Override
  public String trainMaxEntQN(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFile, LanguageCode language, int cutOff, int iterations, int threads, double l1, double l2, int m, int max) throws IOException {

    LOGGER.info("Beginning sentence model training. Output model will be: " + modelFile);

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(subjectOfTraining.getInputFile()));
    ObjectStream<String> lineStream =  new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8);
    ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);

    TrainingParameters trainParams = new TrainingParameters();
    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.MAXENT_QN.getAlgorithm());
    trainParams.put(TrainingParameters.THREADS_PARAM, Integer.toString(threads));

    trainParams.put(QNTrainer.L1COST_PARAM, String.valueOf(l1));
    trainParams.put(QNTrainer.L2COST_PARAM, String.valueOf(l2));
    trainParams.put(QNTrainer.M_PARAM, String.valueOf(m));
    trainParams.put(QNTrainer.MAX_FCT_EVAL_PARAM, String.valueOf(max));

    SentenceDetectorFactory sentenceDetectorFactory = new SentenceDetectorFactory(language.getAlpha3().toString(), true, new Dictionary(), null);

    SentenceModel model = SentenceDetectorME.train(language.getAlpha3().toString(), sampleStream, sentenceDetectorFactory, trainParams);

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

    }

    return modelId;

  }

  @Override
  public String trainPerceptron(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFile, LanguageCode language, int cutOff, int iterations) throws IOException {

    LOGGER.info("Beginning sentence model training. Output model will be: " + modelFile);

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(subjectOfTraining.getInputFile()));
    ObjectStream<String> lineStream =  new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8);
    ObjectStream<SentenceSample> sampleStream = new SentenceSampleStream(lineStream);

    TrainingParameters trainParams = new TrainingParameters();
    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.PERCEPTRON.getAlgorithm());

    SentenceDetectorFactory sentenceDetectorFactory = new SentenceDetectorFactory(language.getAlpha3().toString(), true, new Dictionary(), null);

    SentenceModel model = SentenceDetectorME.train(language.getAlpha3().toString(), sampleStream, sentenceDetectorFactory, trainParams);

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

    }

    return modelId;

  }

}
