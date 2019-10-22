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
package ai.idylnlp.nlp.keywords.rake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Stemmer;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechToken;
import ai.idylnlp.nlp.keywords.model.rake.Keyword;
import ai.idylnlp.nlp.keywords.model.rake.RakeParams;
import ai.idylnlp.nlp.keywords.model.rake.Result;

/**
 * The logic/implementation of the Rapid Automatic Keyword Extraction (RAKE) algorithm. The class's API includes:
 * 
 * <ul>
 * <li> A constructor which sets the algorithm's parameters (stored in a {@link RakeParams} object) and specifies the 
 *      POS tagging and sentence detection models
 * <li> The {@link rake} method, which runs RAKE on a string
 * <li> The {@link getResult} method, which takes an array of {@link Keyword} objects and converts their relevant 
 *      instance variables to primitive arrays
 * </ul> 
 * 
 * @author Chris Baker
 */
public class RakeAlgorithm {
  
  private static final Logger LOGGER = LogManager.getLogger(RakeAlgorithm.class);
  
  private final RakeParams rakeParams;
  private Stemmer stemmer;
  private PartsOfSpeechTagger posTagger;
  private SentenceDetector sentenceDetector;
  private Tokenizer tokenizer;
  
  public RakeAlgorithm(RakeParams rakeParams, Stemmer stemmer, PartsOfSpeechTagger posTagger, SentenceDetector sentenceDetector, Tokenizer tokenizer) {
    
    this.rakeParams = rakeParams;
    this.stemmer = stemmer;
    this.posTagger = posTagger;
    this.sentenceDetector = sentenceDetector;
    this.tokenizer = tokenizer;
    
  }

  /**
   * Run RAKE on a single string.
   *
   * @param txtEl a string with the text that you want to run RAKE on
   * @return a data object containing the results of RAKE
   * @see Result
   */
  public Result rake(String txtEl) {
    String[] tokens = getTokens(txtEl);
    ArrayList<Keyword> keywords = idCandidateKeywords(tokens);
    ArrayList<Keyword> keywords2 = calcKeywordScores(keywords);   
    return getResult(keywords2);
  }
  
  private String[] getTokens(String txtEl) {
    
    // Have to pad punctuation chars with spaces so that tokenizer doesn't combine words with punctuation chars
    String txtPadded = txtEl.replaceAll("([-,.?():;\"!/])", " $1 ");
    
    ArrayList<String> tokenList = new ArrayList<String>();
    Pattern anyWordChar = Pattern.compile("[a-z]");
    
    String[] sents = sentenceDetector.sentDetect(txtPadded);
        
    for (String sentence : sents) {
      
      String[] tokenArray = tokenizer.tokenize(sentence);
      List<PartsOfSpeechToken> tags = posTagger.tag(tokenArray);
      
      for(PartsOfSpeechToken t : tags) {
        
        String token = t.getToken().trim().toLowerCase();
        String tag = t.getPos();
        
        if (token.matches("\\p{Punct}")) {
          // if the token is a punctuation char, leave it
        } else if (rakeParams.getStopPOS().contains(tag) || token.length() < rakeParams.getWordMinChar() || 
            rakeParams.getStopWords().contains(token) || !anyWordChar.matcher(token).find()) {
          // replace unwanted tokens with a period, which we can be confident will be used as a delimiter
          token = ".";
        }
        
        tokenList.add(token);
      }
    }
    
    String[] tokens = new String[tokenList.size()];
    return tokenList.toArray(tokens);
  }

  private ArrayList<Keyword> idCandidateKeywords(String[] tokens) {
    
    ArrayList<Keyword> keywords = new ArrayList<Keyword>();
    String cleanedTxt = collapseTokens(tokens);
    String[] aryKey = cleanedTxt.split(rakeParams.getPhraseDelmins());
    Pattern anyWordChar = Pattern.compile("[a-z]");
    
    for (int i = 0; i < aryKey.length; i++) {
      String oneKey = aryKey[i];
      Matcher myMatch = anyWordChar.matcher(oneKey);
      if (myMatch.find()) {
        String trimmedKey = oneKey.trim();
        String[] wordAr = trimmedKey.split(" ");
        if (rakeParams.shouldStem()) {
          String[] stemmedWordAr = new String[wordAr.length];
          for (int k = 0; k < wordAr.length; k++) {
            stemmedWordAr[k] = stemmer.stem(wordAr[k]).toString();
          }
          String stemedString = collapseTokens(stemmedWordAr);
          Keyword someKey = new Keyword(trimmedKey, wordAr, stemedString, stemmedWordAr);
          keywords.add(someKey);
        } else {
          Keyword someKey = new Keyword(trimmedKey, wordAr);
          keywords.add(someKey);
        }
      }
    }
    
    return keywords;
  }
  
  private String collapseTokens(String[] tokens) {
    
    StringBuilder fullBuff = new StringBuilder();
    
    for (int i = 0; i < tokens.length; i++) {
      String atok = tokens[i];
      String toAdd;
      if (i != tokens.length - 1) {
        toAdd = atok + " ";
      } else {
        toAdd = atok;
      }
      fullBuff.append(toAdd);
    }
  
    return fullBuff.toString();
  }
  
  private ArrayList<Keyword> calcKeywordScores(ArrayList<Keyword> candidateKeywords) {
     
     Map<String, Integer> wordfreq = new HashMap<String, Integer>();
     Map<String, Integer> worddegTemp = new HashMap<String, Integer>();
     Map<String, Float> tokenScores = new HashMap<String, Float>();
     
     for (int i = 0; i < candidateKeywords.size(); i++) {
       
       Keyword oneKey = candidateKeywords.get(i);
       String[] keysTokens;
       
       if (rakeParams.shouldStem()) {
         keysTokens = oneKey.getKeyStemmedAry();
       } else {
         keysTokens = oneKey.getKeyStringAry();
       }
       
       for (int z = 0; z < keysTokens.length; z++) {
         
         String aTok = keysTokens[z];
         int degTe = keysTokens.length - 1;
         
         if (!wordfreq.containsKey(aTok)) {
           wordfreq.put(aTok, 1);
           worddegTemp.put(aTok, degTe);
         } else {
           int valu2 = wordfreq.get(aTok) + 1;
           wordfreq.replace(aTok, valu2);
           int repdeg = worddegTemp.get(aTok) + degTe;
           worddegTemp.replace(aTok, repdeg);
         }
       }
     }

     for (Map.Entry<String, Integer> entry : wordfreq.entrySet()) {
       String aKey = entry.getKey();
       float freq = (float) wordfreq.get(aKey);
       float val = (worddegTemp.get(aKey) + freq) / freq;
       tokenScores.put(aKey, val);
    }
     
     for (int i = 0; i < candidateKeywords.size(); i++) {
       Keyword oneKey = candidateKeywords.get(i);
       oneKey.sumScore(tokenScores, rakeParams);
     }
     
     return candidateKeywords;
  }
  
  /**
   * Convert a list of keywords to a {@link Result}.
   * 
   * @param keywords a list of extracted keywords
   * @return a data object containing the results of RAKE
   * @see Keyword
   * @see Result
   */
  public Result getResult(ArrayList<Keyword> keywords) {
    
    String[] full = new String[keywords.size()];
    String[] stemmed = new String[keywords.size()];
    float[] scores = new float[keywords.size()];
    
    for (int i = 0; i < keywords.size(); i++) {
      Keyword oneKey = keywords.get(i);
      full[i] = oneKey.getKeyString();
      stemmed[i] = oneKey.getStemmedString();
      scores[i] = oneKey.getScore();      
    }
    
    return new Result(full, stemmed, scores);
  }
  
}