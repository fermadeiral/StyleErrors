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
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.Constants;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.training.FMeasure;
import ai.idylnlp.model.training.FMeasureModelValidationResult;
import ai.idylnlp.models.ModelOperationsUtils;
import ai.idylnlp.models.ObjectStreamUtils;
import ai.idylnlp.models.opennlp.training.model.ModelCrossValidationOperations;
import ai.idylnlp.models.opennlp.training.model.ModelSeparateDataValidationOperations;
import ai.idylnlp.models.opennlp.training.model.ModelTrainingOperations;
import ai.idylnlp.models.opennlp.training.model.TrainingAlgorithm;
import ai.idylnlp.training.definition.model.TrainingDefinitionReader;
import opennlp.tools.cmdline.namefind.NameEvaluationErrorListener;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.TokenNameFinderCrossValidator;
import opennlp.tools.namefind.TokenNameFinderEvaluationMonitor;
import opennlp.tools.namefind.TokenNameFinderEvaluator;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.SequenceCodec;
import opennlp.tools.util.TrainingParameters;

/**
 * Operations for training and validating entity models.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class EntityModelOperations implements ModelTrainingOperations, ModelSeparateDataValidationOperations<FMeasureModelValidationResult>, ModelCrossValidationOperations<FMeasureModelValidationResult> {

  private static final Logger LOGGER = LogManager.getLogger(EntityModelOperations.class);

  private String type;
  private String featureGeneratorXml;

  /**
   * Performs model training using a training definition file.
   * @param reader A {@link TrainingDefinitionReader}.
   * @return The generated model's ID.
   * @throws IOException Thrown if the model creation fails.
   */
  public static String train(TrainingDefinitionReader reader) throws IOException {

    final String type = reader.getTrainingDefinition().getModel().getType();
    final String featureGeneratorXml = reader.getFeatures();

    final EntityModelOperations ops = new EntityModelOperations(type, featureGeneratorXml);

    final SubjectOfTrainingOrEvaluation subjectOfTraining = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    final String modelFile = reader.getTrainingDefinition().getModel().getFile();
    final String language = reader.getTrainingDefinition().getModel().getLanguage();
    final int cutOff = reader.getTrainingDefinition().getAlgorithm().getCutoff().intValue();
    final int iterations = reader.getTrainingDefinition().getAlgorithm().getIterations().intValue();
    final int threads = reader.getTrainingDefinition().getAlgorithm().getThreads().intValue();
    final String algorithm = reader.getTrainingDefinition().getAlgorithm().getName();

    LanguageCode languageCode = LanguageCode.getByCodeIgnoreCase(language);

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
   * Performs cross-validation of an entity model.
   * @param reader A {@link TrainingDefinitionReader}.
   * @param folds The number of cross-validation folds.
   * @return A {@link FMeasureModelValidationResult}.
   * @throws IOException Thrown if the model cannot be validated.
   */
  public static FMeasureModelValidationResult crossValidate(TrainingDefinitionReader reader, int folds) throws IOException {

    final String language = reader.getTrainingDefinition().getModel().getLanguage();
    final int iterations = reader.getTrainingDefinition().getAlgorithm().getIterations().intValue();
    final int cutoff = reader.getTrainingDefinition().getAlgorithm().getCutoff().intValue();
    final String featureGeneratorXml = reader.getFeatures();
    final String type = reader.getTrainingDefinition().getModel().getType();
    final String algorithm = reader.getTrainingDefinition().getAlgorithm().getName();
    final double l1 = reader.getTrainingDefinition().getAlgorithm().getL1().doubleValue();
    final double l2 = reader.getTrainingDefinition().getAlgorithm().getL2().doubleValue();
    final int m = reader.getTrainingDefinition().getAlgorithm().getM().intValue();
    final int max = reader.getTrainingDefinition().getAlgorithm().getMax().intValue();

    final LanguageCode languageCode = LanguageCode.getByCodeIgnoreCase(language);

    // Get the subject of training based on what's specified in the training definition file.
    final SubjectOfTrainingOrEvaluation subjectOfTraining = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    // Now we can set up the entity model operations.
    final EntityModelOperations entityModelOperations = new EntityModelOperations(type, featureGeneratorXml);

    FMeasureModelValidationResult result = null;

    if(StringUtils.equalsIgnoreCase(algorithm, TrainingAlgorithm.PERCEPTRON.getName())) {

      result = entityModelOperations.crossValidationEvaluatePerceptron(subjectOfTraining, languageCode, iterations, cutoff, folds);

    } else if(StringUtils.equalsIgnoreCase(algorithm, TrainingAlgorithm.MAXENT_QN.getName())) {

      result = entityModelOperations.crossValidationEvaluateMaxEntQN(subjectOfTraining, languageCode, iterations, cutoff, folds, l1, l2, m, max);

    } else {

      throw new IOException("Invalid algorithm specified in the training definition file: " + algorithm);

    }

    return result;

  }

  /**
   * Creates a new instance.
   * @param type The entity type.
   * @param featureGeneratorXml The XML of the feature generators.
   */
  public EntityModelOperations(String type, String featureGeneratorXml) {

    this.type = type;
    this.featureGeneratorXml = featureGeneratorXml;

  }

  @Override
  public FMeasureModelValidationResult crossValidationEvaluateMaxEntQN(SubjectOfTrainingOrEvaluation subjectOfTraining, LanguageCode language, int iterations, int cutOff, int folds, double l1, double l2, int m, int max) throws IOException {

    LOGGER.info("Doing model evaluation using cross-validation with {} folds.", folds);

    ObjectStream<NameSample> sampleStream = ObjectStreamUtils.getObjectStream(subjectOfTraining);

    TrainingParameters trainParams = new TrainingParameters();

    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.MAXENT_QN.getAlgorithm());

    trainParams.put(QNTrainer.L1COST_PARAM, String.valueOf(l1));
    trainParams.put(QNTrainer.L2COST_PARAM, String.valueOf(l2));
    trainParams.put(QNTrainer.M_PARAM, String.valueOf(m));
    trainParams.put(QNTrainer.MAX_FCT_EVAL_PARAM, String.valueOf(max));

    byte[] featureGeneratorBytes = featureGeneratorXml.getBytes(Charset.forName(Constants.ENCODING_UTF8));
    Map<String, Object> resources = new HashMap<String, Object>();

    TokenNameFinderEvaluationMonitor monitor = new NameEvaluationErrorListener();

    TokenNameFinderCrossValidator evaluator = new TokenNameFinderCrossValidator(language.getAlpha3().toString(), type, trainParams, featureGeneratorBytes, resources, monitor);
    evaluator.evaluate(sampleStream, folds);

    // TODO: The code to get the F-measures is duplicated in the 3 cross-validation functions.
    // Move the code somewhere so it is not duplicated.

    final List<FMeasure> fmeasures = new LinkedList<FMeasure>();

    for(opennlp.tools.util.eval.FMeasure f : evaluator.getFMeasures()) {
      fmeasures.add(new FMeasure(f.getPrecisionScore(), f.getRecallScore(), f.getFMeasure()));
    }

    final FMeasure fmeasure = new FMeasure(evaluator.getFMeasure().getPrecisionScore(),
        evaluator.getFMeasure().getRecallScore(), evaluator.getFMeasure().getFMeasure());

    return new FMeasureModelValidationResult(fmeasure, fmeasures);

  }

  @Override
  public FMeasureModelValidationResult crossValidationEvaluatePerceptron(SubjectOfTrainingOrEvaluation subjectOfTraining, LanguageCode language, int iterations, int cutOff, int folds) throws IOException {

    LOGGER.info("Doing model evaluation using cross-validation with {} folds.", folds);

    ObjectStream<NameSample> sampleStream = ObjectStreamUtils.getObjectStream(subjectOfTraining);

    TrainingParameters trainParams = new TrainingParameters();

    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.PERCEPTRON.getAlgorithm());

    byte[] featureGeneratorBytes = featureGeneratorXml.getBytes(Charset.forName(Constants.ENCODING_UTF8));
    Map<String, Object> resources = new HashMap<String, Object>();

    TokenNameFinderEvaluationMonitor monitor = new NameEvaluationErrorListener();

    TokenNameFinderCrossValidator evaluator = new TokenNameFinderCrossValidator(language.getAlpha3().toString(), type, trainParams, featureGeneratorBytes, resources, monitor);
    evaluator.evaluate(sampleStream, folds);

    final List<FMeasure> fmeasures = new LinkedList<FMeasure>();

    for(opennlp.tools.util.eval.FMeasure f : evaluator.getFMeasures()) {
      fmeasures.add(new FMeasure(f.getPrecisionScore(), f.getRecallScore(), f.getFMeasure()));
    }

    final FMeasure fmeasure = new FMeasure(evaluator.getFMeasure().getPrecisionScore(),
        evaluator.getFMeasure().getRecallScore(), evaluator.getFMeasure().getFMeasure());

    return new FMeasureModelValidationResult(fmeasure, fmeasures);

  }

  @Override
  public FMeasureModelValidationResult separateDataEvaluate(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFileName) throws IOException {

    LOGGER.info("Doing model evaluation using separate training data.");

    ObjectStream<NameSample> sampleStream = ObjectStreamUtils.getObjectStream(subjectOfTraining);

    TokenNameFinderModel model = new TokenNameFinderModel(new File(modelFileName));
    NameFinderME nameFinderME = new NameFinderME(model);

    TokenNameFinderEvaluator evaluator = new TokenNameFinderEvaluator(nameFinderME);

    evaluator.evaluate(sampleStream);

    final FMeasure fmeasure = new FMeasure(evaluator.getFMeasure().getPrecisionScore(),
        evaluator.getFMeasure().getRecallScore(), evaluator.getFMeasure().getFMeasure());

    return new FMeasureModelValidationResult(fmeasure);

  }

  @Override
  public String trainPerceptron(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFile, LanguageCode language, int cutOff, int iterations) throws IOException {

    LOGGER.info("Beginning entity model training. Output model will be: {}", modelFile);

    ObjectStream<NameSample> sampleStream = ObjectStreamUtils.getObjectStream(subjectOfTraining);

    TrainingParameters trainParams = new TrainingParameters();

    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.PERCEPTRON.getAlgorithm());

    // Use null to use the standard Bio codec.
    SequenceCodec<String> sequenceCodec = new BioCodec();// TokenNameFinderFactory.instantiateSequenceCodec(null);
    byte[] featureGeneratorBytes = featureGeneratorXml.getBytes(Charset.forName(Constants.ENCODING_UTF8));
    Map<String, Object> resources = new HashMap<String, Object>();

    TokenNameFinderFactory tokenNameFinderFactory = TokenNameFinderFactory.create(
            TokenNameFinderFactory.class.getName(), featureGeneratorBytes, resources, sequenceCodec);

    // Create the model.
    TokenNameFinderModel model = NameFinderME.train(language.getAlpha3().toString(), type, sampleStream, trainParams, tokenNameFinderFactory);

    BufferedOutputStream modelOut = null;

    // The generated model's ID. Assigned during the training process.
    String modelId = "";

    try {

      modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
      modelId = model.serialize(modelOut);

    } catch (Exception ex) {

      LOGGER.error("Unable to create the model.", ex);

    } finally {

      if (modelOut != null) {
        modelOut.close();
      }

    }

    return modelId;

  }

  @Override
  public String trainMaxEntQN(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFile, LanguageCode language, int cutOff, int iterations, int threads, double l1, double l2, int m, int max) throws IOException {

    LOGGER.info("Beginning entity model training with {} threads. Output model will be: {}", threads, modelFile);

    ObjectStream<NameSample> sampleStream = ObjectStreamUtils.getObjectStream(subjectOfTraining);

    TrainingParameters trainParams = new TrainingParameters();

    trainParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutOff));
    trainParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iterations));
    trainParams.put(TrainingParameters.ALGORITHM_PARAM, TrainingAlgorithm.MAXENT_QN.getAlgorithm());
    trainParams.put(TrainingParameters.THREADS_PARAM, Integer.toString(threads));

    trainParams.put(QNTrainer.L1COST_PARAM, String.valueOf(l1));
    trainParams.put(QNTrainer.L2COST_PARAM, String.valueOf(l2));
    trainParams.put(QNTrainer.M_PARAM, String.valueOf(m));
    trainParams.put(QNTrainer.MAX_FCT_EVAL_PARAM, String.valueOf(max));

    // Use null to use the standard Bio codec.
    SequenceCodec<String> sequenceCodec = TokenNameFinderFactory.instantiateSequenceCodec(null);
    byte[] featureGeneratorBytes = featureGeneratorXml.getBytes(Charset.forName(Constants.ENCODING_UTF8));
    Map<String, Object> resources = new HashMap<String, Object>();

    TokenNameFinderFactory tokenNameFinderFactory = TokenNameFinderFactory.create(
            TokenNameFinderFactory.class.getName(), featureGeneratorBytes, resources, sequenceCodec);

    // Create the model.
    TokenNameFinderModel model = NameFinderME.train(language.getAlpha3().toString(), type, sampleStream, trainParams, tokenNameFinderFactory);

    BufferedOutputStream modelOut = null;

    // The generated model's ID. Assigned during the training process.
    String modelId = "";

    try {

      modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
      modelId = model.serialize(modelOut);

    } catch (Exception ex) {

      LOGGER.error("Unable to create the model.", ex);

    } finally {

      if (modelOut != null) {
        modelOut.close();
      }

    }

    return modelId;

  }

}
