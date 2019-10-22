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
package ai.idylnlp.test.nlp.language.opennlp;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.model.nlp.language.LanguageDetectionException;
import ai.idylnlp.model.nlp.language.LanguageDetectionResponse;
import ai.idylnlp.nlp.language.opennlp.OpenNLPLanguageDetector;

public class OpenNLPLanguageDetectorTest {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPLanguageDetectorTest.class);

  @Test
  public void testSpecifyModel() throws IOException, LanguageDetectionException {

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("langdetect-183.bin");
    OpenNLPLanguageDetector d = new OpenNLPLanguageDetector(in);

    LanguageDetectionResponse response = d.detectLanguage("Washington was born into the provincial gentry of Colonial Virginia to a family of wealthy planters who owned tobacco plantations and slaves, which he inherited. In his youth, he became a senior officer in the colonial militia during the first stages of the French and Indian War.", 5);

    assertTrue(response.getLanguages().size() <= 5);

    for(Pair<String, Double> pair : response.getLanguages()) {

      LOGGER.info("Detected language: {} - {}", pair.getLeft(), pair.getRight());

    }

  }

  @Test
  public void testModel() throws IOException, LanguageDetectionException {

    OpenNLPLanguageDetector d = new OpenNLPLanguageDetector();

    LanguageDetectionResponse response = d.detectLanguage("Washington was born into the provincial gentry of Colonial Virginia to a family of wealthy planters who owned tobacco plantations and slaves, which he inherited. In his youth, he became a senior officer in the colonial militia during the first stages of the French and Indian War.", 5);

    assertTrue(response.getLanguages().size() <= 5);

    for(Pair<String, Double> pair : response.getLanguages()) {

      LOGGER.info("Detected language: {} - {}", pair.getLeft(), pair.getRight());

    }

  }

}
