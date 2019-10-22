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

import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.nlp.recognizer.RegularExpressionEntityRecognizer;

public class RegularExpressionEntityRecognizerTest {

  private static final Logger LOGGER = LogManager.getLogger(RegularExpressionEntityRecognizerTest.class);

  public static String EMAIL_REGULAR_EXPRESSION = "([a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+)";
  public static String HASHTAG_REGULAR_EXPRESSION = "((\\s|\\A)#(\\w+))";
  public static String TWITTER_USERNAME_REGULAR_EXPRESSION = "(@\\w+)";

  @Test
  public void emailAddress() throws EntityFinderException {

    Pattern pattern = Pattern.compile(EMAIL_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE);

    RegularExpressionEntityRecognizer recognizer = new RegularExpressionEntityRecognizer(pattern, "email");

    String input = "George Washington george@washington.com was president of the United States.";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();

    assertEquals("george@washington.com", entity.getText());
    assertEquals("[2..3)", entity.getSpan().toString());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void emailAddress2() throws EntityFinderException {

    Pattern pattern = Pattern.compile(EMAIL_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE);

    RegularExpressionEntityRecognizer recognizer = new RegularExpressionEntityRecognizer(pattern, "email");

    String input = "George's email is george@washington.com!";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();

    assertEquals("george@washington.com", entity.getText());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void emailAddress3() throws EntityFinderException {

    Pattern pattern = Pattern.compile(EMAIL_REGULAR_EXPRESSION, Pattern.CASE_INSENSITIVE);

    RegularExpressionEntityRecognizer recognizer = new RegularExpressionEntityRecognizer(pattern, "email");

    String input = "George's email is none in this sentence.";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(0, response.getEntities().size());

  }

  @Test
  public void hashtag() throws EntityFinderException {

    Pattern pattern = Pattern.compile(HASHTAG_REGULAR_EXPRESSION);

    RegularExpressionEntityRecognizer recognizer = new RegularExpressionEntityRecognizer(pattern, "hashtag");

    String input = "George Washington was #president of the United States.";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();

    assertEquals("#president", entity.getText());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void hashtag2() throws EntityFinderException {

    Pattern pattern = Pattern.compile(HASHTAG_REGULAR_EXPRESSION);

    RegularExpressionEntityRecognizer recognizer = new RegularExpressionEntityRecognizer(pattern, "hashtag");

    String input = "George Washington was #president.";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();

    assertEquals("#president", entity.getText());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void username() throws EntityFinderException {

    Pattern pattern = Pattern.compile(TWITTER_USERNAME_REGULAR_EXPRESSION);

    RegularExpressionEntityRecognizer recognizer = new RegularExpressionEntityRecognizer(pattern, "twitterusername");

    String input = "George Washington @george was #president of the United States.";
    String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();

    assertEquals("@george", entity.getText());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

}
