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
package ai.idylnlp.model.nlp.pos;

import java.util.List;

import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Tokenizer;

/**
 * A part of speech (POS) tagger.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface PartsOfSpeechTagger {

  /**
   * Tags the tokens in the input. The input is broken into sentences and tokenized.
   * @param input The input.
   * @param sentenceDetector A {@link SentenceDetector}.
   * @param tokenizer A {@link Tokenizer}.
   * @return A list of {@link PartsOfSpeechToken}.
   */
  public List<PartsOfSpeechToken> tag(String input, SentenceDetector sentenceDetector, Tokenizer tokenizer);

  /**
   * Tags the tokens in the sentences. Each sentence will be tokenized.
   * @param sentences An array of sentences.
   * @param tokenizer A {@link Tokenizer}.
   * @return A list of {@link PartsOfSpeechToken}.
   */
  public List<PartsOfSpeechToken> tag(String[] sentences, Tokenizer tokenizer);

  /**
   * Tags the tokens in the tokenized sentence.
   * @param tokenizedSentence A single tokenized sentence.
   * @return A list of {@link PartsOfSpeechToken}.
   */
  public List<PartsOfSpeechToken> tag(String[] tokenizedSentence);

}
