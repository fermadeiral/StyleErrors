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
package ai.idylnlp.nlp.language.tika;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tika.langdetect.OptimaizeLangDetector;
import org.apache.tika.language.detect.LanguageConfidence;
import org.apache.tika.language.detect.LanguageResult;

import ai.idylnlp.model.nlp.language.LanguageDetectionException;
import ai.idylnlp.model.nlp.language.LanguageDetectionResponse;
import ai.idylnlp.model.nlp.language.LanguageDetector;

/**
 * An implementation of {@link LanguageDetector} that
 * uses Apache Tika's language detector.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class TikaLanguageDetector implements LanguageDetector {

  private static final Logger LOGGER = LogManager.getLogger(TikaLanguageDetector.class);

  @Override
  public LanguageDetectionResponse detectLanguage(String text, int limit) throws LanguageDetectionException {

    List<Pair<String, Double>> pairs = new LinkedList<Pair<String, Double>>();

    try {

      org.apache.tika.language.detect.LanguageDetector languageDetector = new OptimaizeLangDetector().loadModels();
      List<LanguageResult> languageResults = languageDetector.detectAll(text);

      int x = 0;

      for(LanguageResult languageResult : languageResults) {

        final String code = languageResult.getLanguage();

        double confidence = 0;

        if(languageResult.getConfidence() == LanguageConfidence.HIGH) {

          confidence = 0.9;

        } else if(languageResult.getConfidence() == LanguageConfidence.MEDIUM) {

          confidence = 0.6;

        } else if(languageResult.getConfidence() == LanguageConfidence.LOW) {

          confidence = 0.3;

        } else if(languageResult.getConfidence() == LanguageConfidence.NONE) {

          confidence = 0;

        }

        pairs.add(new ImmutablePair<String, Double>(code, confidence));

        x++;

        if(x == limit) {
          break;
        }

      }

    } catch (Exception ex) {

      LOGGER.error("Unable to detect language for input: " + text);

      throw new LanguageDetectionException("Unable to detect language.");

    }

    return new LanguageDetectionResponse(pairs);

  }

}
