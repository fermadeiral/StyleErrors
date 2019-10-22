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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.nlp.recognizer.PhoneNumberEntityRecognizer;

public class PhoneNumberEntityRecognizerTest {

  private static final Logger LOGGER = LogManager.getLogger(PhoneNumberEntityRecognizerTest.class);

  @Test
  public void phone() {

    PhoneNumberEntityRecognizer recognizer = new PhoneNumberEntityRecognizer();

    String input = "George Washington and @jeff was president of the United States at (203) 753-5678";
    //String input = "George Washington and @jeff was president of the United States at +6403 331 6005";

    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    for(Entity entity : response.getEntities()) {

      LOGGER.info("Number: " + entity.getText());

    }

    assertEquals(3, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();

    assertEquals("2037535678", entity.getText());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

}
