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
package ai.idylnlp.nlp.language.opennlp;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.nlp.language.LanguageDetectionException;
import ai.idylnlp.model.nlp.language.LanguageDetectionResponse;
import ai.idylnlp.model.nlp.language.LanguageDetector;
import opennlp.tools.langdetect.Language;
import opennlp.tools.langdetect.LanguageDetectorME;
import opennlp.tools.langdetect.LanguageDetectorModel;

/**
 * An implementation of {@link LanguageDetector} that
 * uses Apache OpenNLP's language detector.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLPLanguageDetector implements LanguageDetector {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPLanguageDetector.class);

  private opennlp.tools.langdetect.LanguageDetector detector;

  public OpenNLPLanguageDetector() throws IOException {

    InputStream in = ClassLoader.getSystemResourceAsStream("langdetect-183.bin");

    LanguageDetectorModel m = new LanguageDetectorModel(in);
    detector = new LanguageDetectorME(m);

    in.close();

  }

  public OpenNLPLanguageDetector(InputStream in) throws IOException {

    LanguageDetectorModel m = new LanguageDetectorModel(in);
    detector = new LanguageDetectorME(m);

  }

  @Override
  public LanguageDetectionResponse detectLanguage(String text, int limit) throws LanguageDetectionException {

    List<Pair<String, Double>> pairs = new LinkedList<Pair<String, Double>>();

    int count = 0;

    for(Language language : detector.predictLanguages(text)) {

      pairs.add(new ImmutablePair<String, Double>(language.getLang(), language.getConfidence()));

      count++;

      if(count == limit) {
        break;
      }

    }

    Collections.sort(pairs, new Comparator<Pair<String, Double>>() {

      @Override
      public int compare(Pair<String, Double> arg0, Pair<String, Double> arg1) {

        if(arg1.getRight() > arg0.getRight()) {
          return 1;
        } else if(arg0.getRight() > arg1.getRight()) {
          return -1;
        } else {
          return 0;
        }

      }

    });

    return new LanguageDetectionResponse(pairs);

  }

}
