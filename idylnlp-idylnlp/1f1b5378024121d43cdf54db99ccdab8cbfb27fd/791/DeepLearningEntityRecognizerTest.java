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
package ai.idylnlp.test.nlp.recognizer;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import java.util.Properties;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.mockito.Mockito;
import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.SecondGenModelManifest;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.nlp.recognizer.DeepLearningEntityRecognizer;
import ai.idylnlp.nlp.recognizer.configuration.DeepLearningEntityRecognizerConfiguration;
import ai.idylnlp.testing.markers.ExternalData;

public class DeepLearningEntityRecognizerTest {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningEntityRecognizerTest.class);

  private static final String TRAINING_DATA_PATH = System.getProperty("testDataPath");
  //private static final String MODEL_PATH = new File("src/test/resources/models/").getAbsolutePath();

  private static final String NETWORK = "network.zip";
  private static final String VECTORS = "glove.6B.300d.glv";

  @Ignore
  @Category(ExternalData.class)
  @Test
  public void extract() throws Exception {

    LOGGER.info("Training data path: {}", TRAINING_DATA_PATH);

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    SecondGenModelManifest manifest = new SecondGenModelManifest(UUID.randomUUID().toString(), NETWORK, LanguageCode.en, "person", "model", "2", VECTORS, 5, "", "", new Properties());
    LOGGER.info("Model file: " + manifest.getModelFileName());

    DeepLearningEntityRecognizerConfiguration configuration = new DeepLearningEntityRecognizerConfiguration.Builder()
        .build(TRAINING_DATA_PATH);

    configuration.addEntityModel("person", LanguageCode.en, manifest);

    DeepLearningEntityRecognizer recognizer = new DeepLearningEntityRecognizer(configuration);

    String input = "george washington was president";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text)
      .withLanguage(LanguageCode.en)
      .withType("person");

    EntityExtractionResponse response = recognizer.extractEntities(request);

    for(Entity entity : response.getEntities()) {
      LOGGER.info(entity.toString());
    }

  }

}
