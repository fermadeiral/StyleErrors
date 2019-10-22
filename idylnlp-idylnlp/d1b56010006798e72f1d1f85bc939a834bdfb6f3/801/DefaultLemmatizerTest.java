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
package ai.idylnlp.test.nlp.lemmatization;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import ai.idylnlp.opennlp.custom.nlp.lemmatization.DefaultLemmatizer;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest.ModelManifestBuilder;
import ai.idylnlp.model.nlp.lemma.Lemmatizer;

public class DefaultLemmatizerTest {

  private static final Logger LOGGER = LogManager.getLogger(DefaultLemmatizerTest.class);

  private static final String MODEL_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String EN_LEMMATIZER_DICT = "/english-lemmatizer.txt";
  private static final String LEMMA_MODEL = "en-lemmatizer.bin";

  final String[] tokens = new String[] { "Rockwell", "International", "Corp.", "'s",
        "Tulsa", "unit", "said", "it", "signed", "a", "tentative", "agreement",
        "extending", "its", "contract", "with", "Boeing", "Co.", "to",
        "provide", "structural", "parts", "for", "Boeing", "'s", "747",
        "jetliners", "." };

  final String[] posTags = new String[] { "NNP", "NNP", "NNP", "POS", "NNP", "NN",
      "VBD", "PRP", "VBD", "DT", "JJ", "NN", "VBG", "PRP$", "NN", "IN",
      "NNP", "NNP", "TO", "VB", "JJ", "NNS", "IN", "NNP", "POS", "CD", "NNS",
      "." };

  @Test
  public void lemmaDictionary() throws Exception {

    Lemmatizer lemmatizer = new DefaultLemmatizer(MODEL_PATH + EN_LEMMATIZER_DICT);
    String[] lemmas = lemmatizer.lemmatize(tokens, posTags);

    for(String lemma : lemmas) {
      LOGGER.info("Lemma: {}", lemma);
    }

  }

  @Test
  public void lemmaModel() throws Exception {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    ModelManifestBuilder builder = new StandardModelManifest.ModelManifestBuilder();
    builder.setModelId(UUID.randomUUID().toString());
    builder.setLanguageCode(LanguageCode.en);
    builder.setModelFileName(LEMMA_MODEL);
    builder.setType(StandardModelManifest.LEMMA);

    StandardModelManifest modelManifest = builder.build();

    Lemmatizer lemmatizer = new DefaultLemmatizer(MODEL_PATH, modelManifest, modelValidator);
    String[] lemmas = lemmatizer.lemmatize(tokens, posTags);

    for(String lemma : lemmas) {
      LOGGER.info("Lemma: {}", lemma);
    }

  }

}
