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
package ai.idylnlp.test.nlp.language.opennlp;

import static org.junit.Assert.assertEquals;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.model.nlp.language.LanguageDetectionException;
import ai.idylnlp.model.nlp.language.LanguageDetectionResponse;
import ai.idylnlp.nlp.language.tika.TikaLanguageDetector;

public class TikaLanguageDetectorTest {

  private static final Logger LOGGER = LogManager.getLogger(TikaLanguageDetectorTest.class);

  private TikaLanguageDetector languageDetection = new TikaLanguageDetector();

  @Test
  public void detectEnglish() throws LanguageDetectionException {

    String input = "This is my text.";

    LanguageDetectionResponse result = languageDetection.detectLanguage(input, 5);

    assertEquals(1, result.getLanguages().size());

    for(Pair<String, Double> pair : result.getLanguages()) {

      LOGGER.debug("Language code: " + pair.getLeft());

      assertEquals("en", pair.getLeft());

    }

  }

}
