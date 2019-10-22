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
package ai.idylnlp.test.training.definition;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.training.definition.TrainingDefinitionFileReader;
import ai.idylnlp.training.definition.model.TrainingDefinitionException;
import ai.idylnlp.training.definition.model.TrainingDefinitionValidationResult;

public class TrainingDefinitionFileReaderTest {

  private static final Logger LOGGER = LogManager.getLogger(TrainingDefinitionFileReaderTest.class);

  private static final String PATH = new File("src/test/resources/").getAbsolutePath();

  @Test
  public void defaultValuesTest() throws TrainingDefinitionException {

    final String DEFINITION_FILE = PATH + File.separator + "valid-definition-2.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader = new TrainingDefinitionFileReader(file);

    // Test the default values.
    assertEquals("perceptron", reader.getTrainingDefinition().getAlgorithm().getName());
    assertEquals(0, reader.getTrainingDefinition().getAlgorithm().getCutoff().intValue());
    assertEquals(100, reader.getTrainingDefinition().getAlgorithm().getIterations().intValue());
    assertEquals(2, reader.getTrainingDefinition().getAlgorithm().getThreads().intValue());
    assertEquals("opennlp", reader.getTrainingDefinition().getTrainingdata().getFormat());

  }

  @Test(expected = TrainingDefinitionException.class)
  public void invalidDefinition1Test() throws TrainingDefinitionException {

    final String DEFINITION_FILE = PATH + File.separator + "invalid-definition-1.xml";
    final File file = new File(DEFINITION_FILE);

    new TrainingDefinitionFileReader(file);

  }

  @Test
  public void validate() throws TrainingDefinitionException {

    final String DEFINITION_FILE = PATH + File.separator + "valid-definition-1.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader1 = new TrainingDefinitionFileReader(file);
    assertTrue(reader1.validate().isValid());

    TrainingDefinitionFileReader reader2 = new TrainingDefinitionFileReader(file);
    reader2.getTrainingDefinition().getModel().setFile("");
    assertFalse(reader2.validate().isValid());

    TrainingDefinitionFileReader reader3 = new TrainingDefinitionFileReader(file);
    reader3.getTrainingDefinition().getModel().setLanguage("");
    assertFalse(reader3.validate().isValid());

    TrainingDefinitionFileReader reader4 = new TrainingDefinitionFileReader(file);
    reader4.getTrainingDefinition().getModel().setType("");
    assertFalse(reader4.validate().isValid());

  }

  @Test
  public void validateNonExistentTrainingDefinitionFile() throws TrainingDefinitionException {

    final String DEFINITION_FILE = PATH + File.separator + "valid-definition-with-invalid-training-definition-file.xml";
    final File file = new File(DEFINITION_FILE);

    TrainingDefinitionFileReader reader1 = new TrainingDefinitionFileReader(file);
    TrainingDefinitionValidationResult result = reader1.validate();

    assertFalse(result.isValid());
    assertTrue(result.getMessages().contains("The training definition file does not exist."));

  }


}
