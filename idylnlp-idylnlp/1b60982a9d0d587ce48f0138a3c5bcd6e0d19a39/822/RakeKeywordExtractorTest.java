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
package ai.idylnlp.test.nlp.keywords;

import java.io.File;
import java.util.UUID;
import org.junit.Test;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest.ModelManifestBuilder;
import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Stemmer;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.model.nlp.keywords.KeywordExtractionException;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.nlp.keywords.model.rake.RakeParams;
import ai.idylnlp.nlp.keywords.model.rake.SmartWords;
import ai.idylnlp.nlp.keywords.rake.RakeKeywordExtractor;
import ai.idylnlp.nlp.sentence.BreakIteratorSentenceDetector;
import ai.idylnlp.nlp.stemming.DefaultStemmer;
import ai.idylnlp.nlp.tokenizers.WhitespaceTokenizer;
import ai.idylnlp.opennlp.custom.nlp.pos.DefaultPartsOfSpeechTagger;
import ai.idylnlp.opennlp.custom.validators.TrueModelValidator;

public class RakeKeywordExtractorTest {

  private final String[] stopPOS = {"VB", "VBD", "VBG", "VBN", "VBP", "VBZ"};
  
  @Test
  public void extract() throws ModelLoaderException, KeywordExtractionException {
    
    String[] stopWords = new SmartWords().getSmartWords(); 
    
    int minWordChar = 1;
    boolean shouldStem = true;
    String phraseDelims = "[-,.?():;\"!/]"; 
    RakeParams params = new RakeParams(stopWords, stopPOS, minWordChar, shouldStem, phraseDelims);
    
    ModelManifestBuilder builder = new StandardModelManifest.ModelManifestBuilder();
    builder.setModelId(UUID.randomUUID().toString());
    builder.setLanguageCode(LanguageCode.en);
    builder.setModelFileName("en-pos-perceptron.bin");
    builder.setEncryptionKey("");
    builder.setType(StandardModelManifest.POS);

    StandardModelManifest modelManifest = builder.build();
    
    File resourcesDirectory = new File("src/test/resources");
    
    Stemmer stemmer = new DefaultStemmer();
    PartsOfSpeechTagger posTagger = new DefaultPartsOfSpeechTagger(resourcesDirectory.getAbsolutePath(), modelManifest, new TrueModelValidator());
    SentenceDetector sentenceDetector = new BreakIteratorSentenceDetector(LanguageCode.en);
    Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;
    
    RakeKeywordExtractor extractor = new RakeKeywordExtractor(params, stemmer, posTagger, sentenceDetector, tokenizer);
    
    extractor.extract("dogs are great, don't you agree? I love dogs, especially big dogs");
    
  }
  
}
