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
package ai.idylnlp.nlp.sentence;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Span;

public class BreakIteratorSentenceDetector implements SentenceDetector {

  private BreakIterator breakIterator;

  public BreakIteratorSentenceDetector(String languageCode) {

    Locale locale = new Locale.Builder().setLanguage(languageCode).build();

    breakIterator = BreakIterator.getSentenceInstance(locale);

  }

  public BreakIteratorSentenceDetector(LanguageCode languageCode) {

    breakIterator = BreakIterator.getSentenceInstance(languageCode.toLocale());

  }

  /**
   * Creates a sentence detector.
   *
   * @param locale
   *            The {@link Locale} for the sentence detector.
   */
  public BreakIteratorSentenceDetector(Locale locale) {
    breakIterator = BreakIterator.getSentenceInstance(locale);
  }

  @Override
  public List<String> getLanguageCodes() {

    List<String> languageCodes = new LinkedList<>();

    for(Locale locale : BreakIterator.getAvailableLocales()) {
      languageCodes.add(LanguageCode.getByLocale(locale).getAlpha3().toString());
    }

    return languageCodes;

  }

  @Override
  public String[] sentDetect(String s) {
    return Span.spansToStrings(sentPosDetect(s), s);
  }

  @Override
  public Span[] sentPosDetect(String s) {

    List<Span> sentences = new ArrayList<>();

    breakIterator.setText(s);

    int lastIndex = breakIterator.first();

    while (lastIndex != BreakIterator.DONE) {

      int firstIndex = lastIndex;
      lastIndex = breakIterator.next();

      if (lastIndex != BreakIterator.DONE) {
        sentences.add(new Span(firstIndex, lastIndex));
      }

    }

    return sentences.toArray(new Span[sentences.size()]);

  }

}
