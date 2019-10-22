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
 * 
 * This file incorporates work covered by the following copyright and license
 * notice:
 * 
 * Copyright (c) 2018 Chris Baker - https://github.com/crew102/rapidrake-java
 * 
 *     Permission is hereby granted, free of charge, to any person obtaining a
 *     copy of this software and associated documentation files (the "Software"), 
 *     to deal in the Software without restriction, including without limitation 
 *     the rights to use, copy, modify, merge, publish, distribute, sublicense, 
 *     and/or sell copies of the Software, and to permit persons to whom the 
 *     Software is furnished to do so, subject to the following conditions:
 *     
 *     The above copyright notice and this permission notice shall be included 
 *     in all copies or substantial portions of the Software.
 *     
 *     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS 
 *     OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 *     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 *     THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR 
 *     OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, 
 *     ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR 
 *     OTHER DEALINGS IN THE SOFTWARE.
 * 
 ******************************************************************************/
package ai.idylnlp.nlp.keywords.model.rake;

import java.util.Arrays;
import java.util.List;

/**
 * A parameter object for RAKE settings.
 */
public class RakeParams {
  
  private final List<String> stopWords;
  private final List<String> stopPOS;
  private final int wordMinChar;
  private final boolean stem;
  private final String phraseDelims;
  
  /**
   * Constructor.
   *
   * @param stopWords an array of stopwords, which will be treated like phrase delimiters when RAKE is identifying
   *        candidate keywords 
   * @param stopPOS an array of part-of-speech (POS) tags that should be considered stopwords. Words that are tagged 
   *        with any of the parts-of-speech listed in <code>stopPOS</code> will be treated like delimiters. 
   *        See <a href="http://martinschweinberger.de/docs/articles/PosTagR.pdf">Part-Of-Speech Tagging with R</a> 
   *        for a list of acceptable POS tags and their meanings. 
   * @param wordMinChar the minimum number of characters that a token/word must have. Words below this threshold are 
   *        treated like phrase delimiters.
   * @param stem an indicator for whether you want to stem the tokens in each keyword
   * @param phraseDelims a character set containing the punctuation characters used to identify phrases
   */
  public RakeParams(String[] stopWords, String[] stopPOS, int wordMinChar, boolean stem, String phraseDelims) {
    this.stopWords = Arrays.asList(stopWords);
    this.stopPOS = Arrays.asList(stopPOS);
    this.wordMinChar = wordMinChar;
    this.stem = stem;
    this.phraseDelims = phraseDelims;
  }
  
  public List<String> getStopWords() {
    return stopWords;
  }
  public List<String> getStopPOS() {
    return stopPOS;
  }
  public int getWordMinChar() {
    return wordMinChar;
  }
  public boolean shouldStem() {
    return stem;
  }
  public String getPhraseDelmins() {
    return phraseDelims;
  }

}