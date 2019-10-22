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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * A data object containing the results of running RAKE on a single document.
 * results 
 */
public class Result {
  
  private String[] fullKeywords;
  private String[] stemmedKeywords;
  private float[] scores;
  
  /**
   * Constructor.
   *
   * @param fullKeywords the keywords that RAKE found in the document 
   * @param stemmedKeywords the stemmed versions of <code>fullKeywords</code>
   * @param scores the scores assigned to the keywords
   */
  public Result(String[] fullKeywords, String[] stemmedKeywords, float[] scores) {
    this.fullKeywords = fullKeywords;
    this.stemmedKeywords = stemmedKeywords;
    this.scores = scores;
  }

  public String[] getFullKeywords() {
    return fullKeywords;
  }
  public String[] getStemmedKeywords() {
    return stemmedKeywords;
  }
  public float[] getScores() {
    return scores;
  }
  

  /**
  * Return a description of the Result. The Result is shown in the following format:
  * [keyword1 (score of keyword1), keyword2 (score of keyword2)].
  */
  @Override 
  public String toString() {
    
    DecimalFormat dFormat = new DecimalFormat("###.##");
    String[] keyScore = new String[fullKeywords.length];
    
    for (int i = 0; i < fullKeywords.length; i++) {
      keyScore[i] = fullKeywords[i] + " (" + dFormat.format(scores[i]) + ")";
    }
    
    return Arrays.toString(keyScore);
  }
  
  /**
  * Remove duplicate keywords.
  * 
  * @return a Result object with duplicate keywords removed 
  */
  public Result distinct() {
        
    ArrayList<String> fullKeywordsListOut = new ArrayList<String>();
    ArrayList<String> stemmedKeyListOut = new ArrayList<String>();
    ArrayList<Float> scoresListOut = new ArrayList<Float>();
        
    for (int i = 0; i < fullKeywords.length; i++) {
      String oneKey = fullKeywords[i];
      if (!fullKeywordsListOut.contains(oneKey)) {
        fullKeywordsListOut.add(oneKey);
        stemmedKeyListOut.add(stemmedKeywords[i]);
        scoresListOut.add(scores[i]);
      }   
    }

    this.fullKeywords = fullKeywordsListOut.toArray(new String[fullKeywordsListOut.size()]);
    this.stemmedKeywords = stemmedKeyListOut.toArray(new String[stemmedKeyListOut.size()]);
    
    float[] scores = new float[scoresListOut.size()];
    
    for (int i = 0; i < scores.length; i ++) {
      scores[i] = scoresListOut.get(i);
    }
    this.scores = scores;
    
    return this;
  }

}