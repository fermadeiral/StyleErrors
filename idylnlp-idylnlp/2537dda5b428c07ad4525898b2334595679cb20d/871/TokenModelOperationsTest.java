/*******************************************************************************
 * Copyright 2018 Mountain Fog, Inc.
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
import ai.idylnlp.models.opennlp.training.TokenModelOperations;
import opennlp.tools.cmdline.tokenizer.TokenizerModelLoader;
import opennlp.tools.tokenize.TokenizerModel;

public class TokenModelOperationsTest {

  private static final Logger LOGGER = LogManager.getLogger(TokenModelOperationsTest.class);

  private static final String TRAINING_DATA_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String INPUT_FILE = TRAINING_DATA_PATH + File.separator + "token-train.txt";

  @Test
  public void crossValidateMaxEntQN() throws IOException {

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    TokenModelOperations ops = new TokenModelOperations();
    FMeasureModelValidationResult result = ops.crossValidationEvaluateMaxEntQN(SubjectOfTrainingOrEvaluation, LanguageCode.en, 5, 1, 1, 1, 1, 1, 3);

    assertNotNull(result);

  }

  @Test
  public void crossValidatePerceptron() throws IOException {

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    TokenModelOperations ops = new TokenModelOperations();
    FMeasureModelValidationResult result = ops.crossValidationEvaluatePerceptron(SubjectOfTrainingOrEvaluation, LanguageCode.en, 5, 1, 3);

    assertNotNull(result);

  }

  @Test
  public void trainMaxEntQN() throws IOException {

    String encryptionKey = "";

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    TokenModelOperations ops = new TokenModelOperations();
    String modelId = ops.trainMaxEntQN(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, encryptionKey, 5, 1, 1, 1, 1, 1, 1);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    TokenizerModel model = new TokenizerModelLoader().load(new File(modelOutputFile));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

  @Test
  public void trainPerceptron() throws IOException {

    String encryptionKey = "";

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    TokenModelOperations ops = new TokenModelOperations();
    String modelId = ops.trainPerceptron(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, encryptionKey, 0, 1);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    TokenizerModel model = new TokenizerModelLoader().load(new File(modelOutputFile));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

}
