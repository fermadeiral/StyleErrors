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
import ai.idylnlp.models.opennlp.training.LemmatizerModelOperations;
import opennlp.tools.cmdline.lemmatizer.LemmatizerModelLoader;
import opennlp.tools.lemmatizer.LemmatizerModel;

public class LemmatizerModelOperationsTest {

  private static final Logger LOGGER = LogManager.getLogger(LemmatizerModelOperationsTest.class);

  private static final String TRAINING_DATA_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String INPUT_FILE = TRAINING_DATA_PATH + File.separator + "lemma-train.txt";

  @Test
  public void trainMaxEntQN() throws IOException {

    String encryptionKey = "";

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    LemmatizerModelOperations ops = new LemmatizerModelOperations();
    String modelId = ops.trainMaxEntQN(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, encryptionKey, 5, 1, 1, 1, 1, 1, 1);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    LemmatizerModel model = new LemmatizerModelLoader().load(new File(modelOutputFile));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

  @Test
  public void trainPerceptron() throws IOException {

    String encryptionKey = "";

    File temp = File.createTempFile("model", ".bin");
    String modelOutputFile = temp.getAbsolutePath();

    LOGGER.info("Generating output model file to: {}", modelOutputFile);

    SubjectOfTrainingOrEvaluation SubjectOfTrainingOrEvaluation = new DefaultSubjectOfTrainingOrEvaluation(INPUT_FILE);

    LemmatizerModelOperations ops = new LemmatizerModelOperations();
    String modelId = ops.trainPerceptron(SubjectOfTrainingOrEvaluation, modelOutputFile, LanguageCode.en, encryptionKey, 0, 1);

    LOGGER.info("The generated model's ID is {}.", modelId);

    try {

      UUID uuid = UUID.fromString(modelId);

    } catch (IllegalArgumentException ex) {

      fail("The generated model ID is not a valid UUID.");

    }

    // Verify that the UUID returned matches what's in the model's properties.
    LemmatizerModel model = new LemmatizerModelLoader().load(new File(modelOutputFile));
    assertEquals(modelId, model.getManifestProperty("model.id"));

  }

}
