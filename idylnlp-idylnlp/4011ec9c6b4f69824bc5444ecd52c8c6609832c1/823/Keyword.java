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

import java.util.Map;

/**
 * An n-gram that doesn't contain stop words or phrase delimiters.
 */
public class Keyword {
    
  private String keyString; 
  private String[] keyStringAry; 
  private String[] keyStemmedStringAry; 
  private String keyStemmedString; 
  private float score;
  
  public String[] getKeyStringAry() {
    return keyStringAry;
  }
  
  public String[] getKeyStemmedAry() {
    return keyStemmedStringAry;
  }
  
  public String getKeyString() {
    return keyString;
  }
  
  public String getStemmedString() {
    return keyStemmedString;
  }
  
  public float getScore() {
    return score;
  }
  
  /**
   * Constructor.
   * 
   * @param keyString the full form (i.e., not tokenized) of the keyword (e.g., "good dogs")
   * @param keyStringAry the tokenized version of the keyword (e.g., {"good", "dogs"})
   * @param keyStemmedString the stemmed version of <code>keyString</code> (e.g., "good dogs")
   * @param keyStemmedStringAry the stemmed version of <code>keyStringAry</code> (e.g., {"good", "dog"})
   */
  public Keyword(String keyString, String[] keyStringAry, String keyStemmedString, String[] keyStemmedStringAry) {
    this.keyString = keyString;
    this.keyStringAry = keyStringAry;
    this.keyStemmedString = keyStemmedString;
    this.keyStemmedStringAry = keyStemmedStringAry;
  }
  
  public Keyword(String keyString, String[] keyStringAry) {
    this.keyString = keyString;
    this.keyStringAry = keyStringAry;
  }
  
  /**
   * Sum the scores of each token belonging to a given keyword.
   * 
   * @param scoreVec a document-level collection of key-value pairs, where the keys are the distinct tokens across all 
   *        keywords and the values are the document-level scores associated with them
   * @param rakeParams the parameters that RAKE will use
   * @see RakeParams
   */
  public void sumScore(Map<String, Float> scoreVec, RakeParams rakeParams) {
    
    String[] ary;
    if (rakeParams.shouldStem()) {
      ary = keyStemmedStringAry;
    } else {
      ary = keyStringAry;
    }
    
    float sum = 0;
    for (int i = 0; i < ary.length; i++) {
      String oneToken = ary[i];
      float val = scoreVec.get(oneToken);
      sum = val + sum;
    }
    score = sum;
  }
  
}