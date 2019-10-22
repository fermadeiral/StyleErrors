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
package ai.idylnlp.test.nlp.translation;

import static org.junit.Assert.*;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import ai.idylnlp.model.nlp.translation.LanguageTranslationRequest;
import ai.idylnlp.model.nlp.translation.LanguageTranslationResponse;
import ai.idylnlp.nlp.translation.JoshuaTranslator;
import ai.idylnlp.testing.markers.ExternalData;
import ai.idylnlp.testing.markers.HighMemoryUsage;

public class JoshuaTranslatorTest {

  private static final Logger LOGGER = LogManager.getLogger(JoshuaTranslatorTest.class);

  private static final String TRAINING_DATA_PATH = System.getProperty("testDataPath");

  @Ignore
  @Category({ExternalData.class, HighMemoryUsage.class})
  @Test
  public void translate() throws IOException {

    JoshuaTranslator translator = new JoshuaTranslator(TRAINING_DATA_PATH + "/apache-joshua-en-de-2017-01-31/");
    LanguageTranslationResponse response = translator.translate(new LanguageTranslationRequest("birthday"));

    assertEquals("geburtstag", response.getTranslated());

  }

}
