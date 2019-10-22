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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.nlp.language.LanguageDetectionException;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.nlp.recognizer.DateEntityRecognizer;

public class DateEntityRecognizerTest {

  private static final Logger LOGGER = LogManager.getLogger(DateEntityRecognizerTest.class);

  @Test
  public void test1() throws EntityFinderException, IOException, LanguageDetectionException {

    DateEntityRecognizer recognizer = new DateEntityRecognizer();

    String input = "The appointment is a week from Tuesday at 2pm. The next meeting is Friday.";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text).withLanguage(LanguageCode.en);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    for(Entity entity : response.getEntities()) {

      LOGGER.info("Entity: {}, Span: {}", entity.getText(), entity.getSpan().toString());

      assertTrue(entity.getMetadata().containsKey("time"));

    }

    assertEquals(2, response.getEntities().size());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void test2() throws EntityFinderException, IOException, LanguageDetectionException {

    DateEntityRecognizer recognizer = new DateEntityRecognizer();

    String input = "The appointment is a week from Tuesday at 2pm. The next meeting is Friday.";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text).withLanguage(LanguageCode.en);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    for(Entity entity : response.getEntities()) {

      LOGGER.info("Entity: {}, Span: {}", entity.getText(), entity.getSpan().toString());

      assertTrue(entity.getMetadata().containsKey("time"));

    }

    assertEquals(2, response.getEntities().size());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

}
