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
package ai.idylnlp.test.nlp.recognizer;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.nlp.recognizer.DictionaryEntityRecognizer;

public class DictionaryEntityRecognizerTest {

  private static final Logger LOGGER = LogManager.getLogger(DictionaryEntityRecognizerTest.class);

  @Test
  public void extractEntitiesTest() throws EntityFinderException, IOException {

    Set<String> dictionary = new LinkedHashSet<>();
    dictionary.add("united states".toLowerCase());
    dictionary.add("George Washington".toLowerCase());

    DictionaryEntityRecognizer recognizer = new DictionaryEntityRecognizer(LanguageCode.en, dictionary, "place", 0.1, false);

    String[] tokens = {"george", "washington", "was", "president", "of", "the", "United", "states"};

    EntityExtractionRequest request = new EntityExtractionRequest(tokens);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(2, response.getEntities().size());

    for(Entity entity : response.getEntities()) {
      LOGGER.info("Entity: " + entity.toString());
    }

  }

  @Test
  public void extractEntitiesCaseSensitiveTest() throws EntityFinderException, IOException {

    Set<String> dictionary = new LinkedHashSet<>();
    dictionary.add("United States".toLowerCase());
    dictionary.add("George Washington".toLowerCase());

    DictionaryEntityRecognizer recognizer = new DictionaryEntityRecognizer(LanguageCode.en, dictionary, "place", 0.1, true);

    String[] tokens = {"george", "washington", "was", "president", "of", "the", "United", "States"};

    EntityExtractionRequest request = new EntityExtractionRequest(tokens);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    for(Entity entity : response.getEntities()) {
      LOGGER.info("Entity: " + entity.toString());
    }

  }

}
