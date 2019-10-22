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

import ai.idylnlp.model.nlp.Span;
import ai.idylnlp.model.nlp.Stemmer;
import ai.idylnlp.model.nlp.Tokenizer;

import java.util.*;

import org.apache.commons.lang3.NotImplementedException;

/**
 * This tokenizer uses ICU4J's unicode tokenization rules.
 */
public class UnicodeTokenizer implements Tokenizer {

  @Override
  public List<String> getLanguageCodes() {

    // This tokenizer is not language-dependent so return an empty list.
    return Collections.emptyList();

  }

  @Override
  public String[] tokenize(String s) {

    List<String> tokens = new LinkedList<String>();

    com.ibm.icu.util.StringTokenizer stringTokenizer = new com.ibm.icu.util.StringTokenizer(s);

    while (stringTokenizer.hasMoreTokens()) {
      tokens.add(stringTokenizer.nextToken());
    }

    String[] t = new String[tokens.size()];
    
    return tokens.toArray(t);

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
    
	  throw new NotImplementedException("This is not yet implemented.");
	  
  }

}
