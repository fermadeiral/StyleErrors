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
package ai.idylnlp.nlp.tokenizers;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.NotImplementedException;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.Span;
import ai.idylnlp.model.nlp.Stemmer;
import ai.idylnlp.model.nlp.Tokenizer;

/**
 * A {@link Tokenizer} that uses a {@link BreakIterator}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class BreakIteratorTokenizer implements Tokenizer {

  private BreakIterator breakIterator;

  public BreakIteratorTokenizer(String languageCode) {

    Locale locale = new Locale.Builder().setLanguage(languageCode).build();

    breakIterator = BreakIterator.getWordInstance(locale);

  }

  public BreakIteratorTokenizer(LanguageCode languageCode) {

    breakIterator = BreakIterator.getWordInstance(languageCode.toLocale());

  }

  /**
   * Creates a tokenizer.
   *
   * @param locale The {@link Locale} for the tokenizer.
   */
  public BreakIteratorTokenizer(Locale locale) {
    breakIterator = BreakIterator.getWordInstance(locale);
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
  public String[] tokenize(String s) {
    return Span.spansToStrings(tokenizePos(s), s);
  }

  @Override
  public Span[] tokenizePos(String d) {

    List<Span> tokens = new ArrayList<>();

    breakIterator.setText(d);

    int lastIndex = breakIterator.first();

    while (lastIndex != BreakIterator.DONE) {

      int firstIndex = lastIndex;
      lastIndex = breakIterator.next();

      if (lastIndex != BreakIterator.DONE
          && Character.isLetterOrDigit(d.charAt(firstIndex))) {
        tokens.add(new Span(firstIndex, lastIndex));
      }

    }

    return tokens.toArray(new Span[tokens.size()]);

  }

  @Override
  public String[] tokenize(String s, Stemmer stemmer) {
    // TODO: Implement this.
    throw new NotImplementedException("Not yet implemented.");
  }

}
