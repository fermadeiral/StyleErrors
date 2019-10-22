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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ai.idylnlp.model.nlp.Span;
import ai.idylnlp.model.nlp.Stemmer;
import ai.idylnlp.model.nlp.Tokenizer;
import opennlp.tools.util.StringUtil;

/**
 * This tokenizer uses whitespace to tokenize the input text.
 *
 * To obtain an instance of this tokenizer use the static final
 * <code>INSTANCE</code> field.
 */
public class WhitespaceTokenizer implements Tokenizer {

  public static final WhitespaceTokenizer INSTANCE = new WhitespaceTokenizer();

  private WhitespaceTokenizer() {

  }

  @Override
  public List<String> getLanguageCodes() {

    // This tokenizer is not language-dependent so return an empty list.
    return Collections.emptyList();

  }

  @Override
  public String[] tokenize(String s) {
    return Span.spansToStrings(tokenizePos(s), s);
  }

  @Override
  public String[] tokenize(String s, Stemmer stemmer) {

    String[] tokens = tokenize(s);

    for (int i = 0; i < tokens.length; i++) {

      tokens[i] = stemmer.stem(tokens[i]);

    }

    return tokens;

  }

  @Override
  public Span[] tokenizePos(String d) {
    int tokStart = -1;
    List<Span> tokens = new ArrayList<>();
    boolean inTok = false;

    // gather up potential tokens
    int end = d.length();
    for (int i = 0; i < end; i++) {
      if (StringUtil.isWhitespace(d.charAt(i))) {
        if (inTok) {
          tokens.add(new Span(tokStart, i));
          inTok = false;
          tokStart = -1;
        }
      } else {
        if (!inTok) {
          tokStart = i;
          inTok = true;
        }
      }
    }

    if (inTok) {
      tokens.add(new Span(tokStart, end));
    }

    return tokens.toArray(new Span[tokens.size()]);
  }
}
