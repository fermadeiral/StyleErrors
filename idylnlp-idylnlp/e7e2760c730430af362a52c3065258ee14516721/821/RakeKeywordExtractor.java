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
package ai.idylnlp.nlp.keywords.rake;

import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Stemmer;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.model.nlp.keywords.KeywordExtractionException;
import ai.idylnlp.model.nlp.keywords.KeywordExtractionResponse;
import ai.idylnlp.model.nlp.keywords.KeywordExtractor;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.nlp.keywords.model.rake.RakeParams;
import ai.idylnlp.nlp.keywords.model.rake.Result;

public class RakeKeywordExtractor implements KeywordExtractor {
  
  private RakeAlgorithm rake;
  
  public RakeKeywordExtractor(RakeParams rakeParams, Stemmer stemmer, PartsOfSpeechTagger posTagger, SentenceDetector sentenceDetector, Tokenizer tokenizer) {

    rake = new RakeAlgorithm(rakeParams, stemmer, posTagger, sentenceDetector, tokenizer);
    
  }
  
  @Override
  public KeywordExtractionResponse extract(String text) throws KeywordExtractionException {

    Result result = rake.rake(text);
    
    // Print the result
    System.out.println(result.distinct());

    KeywordExtractionResponse response = new KeywordExtractionResponse();
    return response;
      
  }

}
