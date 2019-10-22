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
package ai.idylnlp.test.models.opennlp.training;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.subjects.CoNLL2003SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.DefaultSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.stats.StatsReporter;
import ai.idylnlp.model.training.FMeasureModelValidationResult;
import ai.idylnlp.models.opennlp.training.EntityModelOperations;
import ai.idylnlp.training.definition.TrainingDefinitionFileReader;
import ai.idylnlp.training.definition.model.TrainingDefinitionException;
import opennlp.tools.cmdline.namefind.TokenNameFinderModelLoader;
import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.namefind.TokenNameFinderModel;

public class EntityModelOperationsTest {

  private static final Logger LOGGER = LogManager.getLogger(EntityModelOperationsTest.class);

  private static final String TRAINING_DATA_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String DEFAULT_FEATURE_GENERATOR_XML = TRAINING_DATA_PATH + File.separator + "default-feature-generators.xml";
  private static final String SECOND_FEATURE_GENERATOR_XML = TRAINING_DATA_PATH + File.separator + "second-feature-generators.xml";
  private static final String ENGLISH_PERSON_TRAINING_FILE = TRAINING_DATA_PATH + File.separator + "person-train.txt";
  private static final String ENGLISH_PERSON_SEPARATE_DATA_EVALUATION_FILE = TRAINING_DATA_PATH + File.separator + "person-separate-evaluation.txt";
  private static final String CONLL2003_SEPARATE_DATA_EVALUATION_FILE = TRAINING_DATA_PATH + File.separator + "conll2003-eng.testa";
  private static final String ARABIC_PERSON_TRAINING_FILE = TRAINING_DATA_PATH + File.separator + "arabic_person.train";
  private static final String CROSS_VALIDATION_FILE = TRAINING_DATA_PATH + File.separator + "en-person-cross-validation.txt";

  private static final StatsReporter statsReporter = Mockito.mock(StatsReporter.class);

  @Test(expected = TrainingDefinitionException.class)
  public void missingDefinition() throws TrainingDefinitionException {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "doesnt-exist.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

  }

  @Test
  public void perceptronCoNLL2003TrainingDefinition() throws Exception {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "training-definition-perceptron-conll2003.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    reader.getTrainingDefinition().getTrainingdata().setFile(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getFile());
    reader.getTrainingDefinition().getModel().setFile(File.createTempFile("model", ".bin").getAbsolutePath());

    final String modelId = EntityModelOperations.train(reader);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    TokenNameFinderModel model = new TokenNameFinderModelLoader().load(new File(reader.getTrainingDefinition().getModel().getFile()));
    assertEquals(modelId, model.getManifestProperty("model.id"));

    // -------------

    CoNLL2003SubjectOfTrainingOrEvaluation subjectOfTrainingOrEvaluationEvaluation = new CoNLL2003SubjectOfTrainingOrEvaluation(CONLL2003_SEPARATE_DATA_EVALUATION_FILE);

    final String modelOutputFile = reader.getTrainingDefinition().getModel().getFile();

    // Do the cross validation.
    EntityModelOperations ops = new EntityModelOperations("person", null);
    FMeasureModelValidationResult result = ops.separateDataEvaluate(subjectOfTrainingOrEvaluationEvaluation, modelOutputFile);

    assertTrue(result.getFmeasure() != null);
    assertTrue(result.getFmeasure().getPrecision() > 0);
    assertTrue(result.getFmeasure().getRecall() > 0);
    assertTrue(result.getFmeasure().getFmeasure() > 0);

    assertEquals(0.916720, result.getFmeasure().getPrecision(), 0.001);
    assertEquals(0.776873, result.getFmeasure().getRecall(), 0.001);
    assertEquals(0.841023, result.getFmeasure().getFmeasure(), 0.001);

    // Output will be like: Precision: 0.7; Recall: 0.30434782608695654; F-Measure: 0.42424242424242425
    LOGGER.info(result.getFmeasure().toString());
    LOGGER.info("Model file: {}", modelOutputFile);

  }

  @Test
  public void perceptronTrainingDefinition() throws Exception {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "training-definition-perceptron-idylnlp-annotations.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    reader.getTrainingDefinition().getTrainingdata().setFile(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getFile());
    reader.getTrainingDefinition().getTrainingdata().setAnnotations(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getAnnotations());
    reader.getTrainingDefinition().getModel().setFile(File.createTempFile("model", ".bin").getAbsolutePath());

    String modelId = EntityModelOperations.train(reader);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    TokenNameFinderModel model = new TokenNameFinderModelLoader().load(new File(reader.getTrainingDefinition().getModel().getFile()));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

  @Test
  public void perceptronTrainingDefinitionOpenNLPAnnotations() throws Exception {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "training-definition-perceptron-opennlp-annotations.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    reader.getTrainingDefinition().getTrainingdata().setFile(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getFile());
    reader.getTrainingDefinition().getModel().setFile(File.createTempFile("model", ".bin").getAbsolutePath());

    String modelId = EntityModelOperations.train(reader);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    TokenNameFinderModel model = new TokenNameFinderModelLoader().load(new File(reader.getTrainingDefinition().getModel().getFile()));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

  @Test
  public void maxentQNTrainingDefinition() throws Exception {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "training-definition-maxent-qn.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    reader.getTrainingDefinition().getTrainingdata().setFile(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getFile());
    reader.getTrainingDefinition().getModel().setFile(File.createTempFile("model", "bin").getAbsolutePath());

    String modelId = EntityModelOperations.train(reader);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    TokenNameFinderModel model = new TokenNameFinderModelLoader().load(new File(reader.getTrainingDefinition().getModel().getFile()));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

  @Test
  public void crossValidatePerceptronIdylNLPAnnotations() throws Exception {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "training-definition-perceptron-idylnlp-annotations.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    reader.getTrainingDefinition().getTrainingdata().setFile(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getFile());
    reader.getTrainingDefinition().getTrainingdata().setAnnotations(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getAnnotations());

    FMeasureModelValidationResult result = EntityModelOperations.crossValidate(reader, 5);

    assertNotNull(result);

    LOGGER.info(result.getFmeasure().toString());

  }

  @Test
  public void crossValidatePerceptronOpenNLPAnnotations() throws Exception {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "training-definition-perceptron-opennlp-annotations.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    reader.getTrainingDefinition().getTrainingdata().setFile(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getFile());

    FMeasureModelValidationResult result = EntityModelOperations.crossValidate(reader, 5);

    assertNotNull(result);

    LOGGER.info(result.getFmeasure().toString());

  }

  @Test
  public void crossValidateMaxEntQN() throws Exception {

    final String DEFINITION_FILE = TRAINING_DATA_PATH + File.separator + "training-definition-maxent-qn.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    reader.getTrainingDefinition().getTrainingdata().setFile(TRAINING_DATA_PATH + File.separator + reader.getTrainingDefinition().getTrainingdata().getFile());

    FMeasureModelValidationResult result = EntityModelOperations.crossValidate(reader, 5);

    assertNotNull(result);

    LOGGER.info(result.getFmeasure().toString());

  }

  @Test
  public void perceptronModelSeparateDataEvaluateTest() throws IOException {

    String xmlFeatureGenerators = FileUtils.readFileToString(new File(DEFAULT_FEATURE_GENERATOR_XML));

    EntityModelOperations ops = new EntityModelOperations("person", xmlFeatureGenerators);

    File temp = File.createTempFile("model", ".bin");

    final String modelOutputFile = temp.getAbsolutePath();

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(ENGLISH_PERSON_TRAINING_FILE);

    // First, create a model.
    ops.trainPerceptron(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, 0, 5);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluationEvaluation = new DefaultSubjectOfTrainingOrEvaluation(ENGLISH_PERSON_SEPARATE_DATA_EVALUATION_FILE);

    // Do the cross validation.
    FMeasureModelValidationResult result = ops.separateDataEvaluate(SubjectOfTrainingOrEvaluationEvaluation, modelOutputFile);

    // Output will be like: Precision: 0.7; Recall: 0.30434782608695654; F-Measure: 0.42424242424242425
    LOGGER.info(result.getFmeasure().toString());

    assertTrue(result.getFmeasure() != null);
    assertTrue(result.getFmeasure().getPrecision() > 0);
    assertTrue(result.getFmeasure().getRecall() > 0);
    assertTrue(result.getFmeasure().getFmeasure() > 0);

  }

  @Test
  public void maxEntQNModelSeparateDataEvaluateTest() throws IOException {

    String xmlFeatureGenerators = FileUtils.readFileToString(new File(DEFAULT_FEATURE_GENERATOR_XML));

    EntityModelOperations ops = new EntityModelOperations("person", xmlFeatureGenerators);

    File temp = File.createTempFile("model", ".bin");

    final String modelOutputFile = temp.getAbsolutePath();

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(ENGLISH_PERSON_TRAINING_FILE);

    // First, create a model.
    ops.trainMaxEntQN(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, 0, 5, 1,
        QNTrainer.L1COST_DEFAULT, QNTrainer.L2COST_DEFAULT, QNTrainer.M_DEFAULT, QNTrainer.MAX_FCT_EVAL_DEFAULT);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluationEvaluation = new DefaultSubjectOfTrainingOrEvaluation(ENGLISH_PERSON_SEPARATE_DATA_EVALUATION_FILE);

    // Do the cross validation.
    FMeasureModelValidationResult result = ops.separateDataEvaluate(SubjectOfTrainingOrEvaluationEvaluation, modelOutputFile);

    // Output will be like: Precision: 0.7; Recall: 0.30434782608695654; F-Measure: 0.42424242424242425
    LOGGER.info(result.getFmeasure().toString());

    assertTrue(result.getFmeasure() != null);
    assertTrue(result.getFmeasure().getPrecision() > 0);
    assertTrue(result.getFmeasure().getRecall() > 0);
    assertTrue(result.getFmeasure().getFmeasure() > 0);

  }

  @Test
  public void crossValidationEvaluatePerceptronTest() throws IOException {

    final String xmlFeatureGenerators = FileUtils.readFileToString(new File(DEFAULT_FEATURE_GENERATOR_XML));

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(CROSS_VALIDATION_FILE);

    EntityModelOperations ops = new EntityModelOperations("person", xmlFeatureGenerators);
    FMeasureModelValidationResult result = ops.crossValidationEvaluatePerceptron(SubjectOfTrainingOrEvaluation, LanguageCode.en, 25, 1, 3);

    // Output will be like: Precision: 0.7; Recall: 0.30434782608695654; F-Measure: 0.42424242424242425
    LOGGER.info(result.getFmeasure().toString());

    assertTrue(result.getFmeasure() != null);
    assertTrue(result.getFmeasure().getPrecision() > 0);
    assertTrue(result.getFmeasure().getRecall() > 0);
    assertTrue(result.getFmeasure().getFmeasure() > 0);

    // Look at all of the F-Measures.
    assertTrue(result.getFmeasures() != null);

  }

  @Test
  public void crossValidationEvaluateMaxEntQNTest() throws IOException {

    final String xmlFeatureGenerators = FileUtils.readFileToString(new File(DEFAULT_FEATURE_GENERATOR_XML));

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(CROSS_VALIDATION_FILE);

    EntityModelOperations ops = new EntityModelOperations("person", xmlFeatureGenerators);
    FMeasureModelValidationResult result = ops.crossValidationEvaluateMaxEntQN(SubjectOfTrainingOrEvaluation, LanguageCode.en, 25, 5, 3,
        QNTrainer.L1COST_DEFAULT, QNTrainer.L2COST_DEFAULT, QNTrainer.M_DEFAULT, QNTrainer.MAX_FCT_EVAL_DEFAULT);

    // Output will be like: Precision: 0.7; Recall: 0.30434782608695654; F-Measure: 0.42424242424242425
    LOGGER.info(result.getFmeasure().toString());

    assertTrue(result.getFmeasure() != null);
    assertTrue(result.getFmeasure().getPrecision() > 0);
    assertTrue(result.getFmeasure().getRecall() > 0);
    assertTrue(result.getFmeasure().getFmeasure() > 0);

    // Look at all of the F-Measures.
    assertTrue(result.getFmeasures() != null);

  }

  @Test
  public void trainPerceptron() throws IOException {

    String xmlFeatureGenerators = FileUtils.readFileToString(new File(DEFAULT_FEATURE_GENERATOR_XML));

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(ENGLISH_PERSON_TRAINING_FILE);

    EntityModelOperations ops = new EntityModelOperations("person", xmlFeatureGenerators);
    String modelId = ops.trainPerceptron(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, 0, 5);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    TokenNameFinderModel model = new TokenNameFinderModelLoader().load(new File(modelOutputFile));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

}
