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
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.subjects.DefaultSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.training.FMeasureModelValidationResult;
import ai.idylnlp.models.opennlp.training.SentenceModelOperations;
import opennlp.tools.cmdline.sentdetect.SentenceModelLoader;
import opennlp.tools.sentdetect.SentenceModel;

public class SentenceModelOperationsTest {

  private static final Logger LOGGER = LogManager.getLogger(SentenceModelOperationsTest.class);

  private static final String TRAINING_DATA_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String INPUT_FILE = TRAINING_DATA_PATH + File.separator + "sentence-train.txt";

  // Base64 encoded form of: 1zKNmjiwSM3WwuTkIFpKQeCqLQ8K7exonWa76SI2Vg0XeBoDaZ
  public static final String SALT = "MXpLTm1qaXdTTTNXd3VUa0lGcEtRZUNxTFE4SzdleG9uV2E3NlNJMlZnMFhlQm9EYVo=";

  @Test
  public void crossValidateMaxEntQN() throws IOException {

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    SentenceModelOperations ops = new SentenceModelOperations();
    FMeasureModelValidationResult result = ops.crossValidationEvaluateMaxEntQN(SubjectOfTrainingOrEvaluation, LanguageCode.en, 1, 1, 1, 1, 1, 1, 5);

    assertNotNull(result);

    LOGGER.info(result.getFmeasure().toString());

  }

  @Test
  public void crossValidatePerceptron() throws IOException {

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    SentenceModelOperations ops = new SentenceModelOperations();
    FMeasureModelValidationResult result = ops.crossValidationEvaluatePerceptron(SubjectOfTrainingOrEvaluation, LanguageCode.en, 1, 1, 3);

    assertNotNull(result);

    LOGGER.info(result.getFmeasure().toString());

  }

  @Test
  public void trainMaxEntQN() throws IOException {

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);
    LOGGER.info("Input file: {}", INPUT_FILE);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    SentenceModelOperations ops = new SentenceModelOperations();
    String modelId = ops.trainMaxEntQN(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, 1, 1, 1, 1, 1, 1, 1);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    SentenceModel model = new SentenceModelLoader().load(new File(modelOutputFile));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

  @Test
  public void trainPerceptron() throws IOException {

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);
    LOGGER.info("Input file: {}", INPUT_FILE);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    SentenceModelOperations ops = new SentenceModelOperations();
    String modelId = ops.trainPerceptron(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, 0, 1);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    SentenceModel model = new SentenceModelLoader().load(new File(modelOutputFile));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

}
