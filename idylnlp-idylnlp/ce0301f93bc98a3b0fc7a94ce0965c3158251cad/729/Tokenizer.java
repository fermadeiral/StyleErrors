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
package ai.idylnlp.model.nlp;

import java.util.List;

/**
 * The interface for tokenizers, which segment a string into its tokens.
 * <p>
 * Tokenization is a necessary step before more complex NLP tasks can be applied,
 * these usually process text on a token level. The quality of tokenization is
 * important because it influences the performance of high-level task applied to it.
 * <p>
 * In segmented languages like English most words are segmented by white spaces
 * expect for punctuations, etc. which is directly attached to the word without a white space
 * in between, it is not possible to just split at all punctuations because in abbreviations dots
 * are a part of the token itself. A tokenizer is now responsible to split these tokens
 * correctly.
 * <p>
 * In non-segmented languages like Chinese tokenization is more difficult since words
 * are not segmented by a whitespace.
 * <p>
 * Tokenizers can also be used to segment already identified tokens further into more
 * atomic parts to get a deeper understanding. This approach helps more complex task
 * to gain insight into tokens which do not represent words like numbers, units or tokens
 * which are part of a special notation.
 * <p>
 * For most further task it is desirable to over tokenize rather than under tokenize.
 * <p>
 * This class is based on OpenNLP's <code>opennlp.tools.tokenize.Tokenizer</code>.
 */
public interface Tokenizer {

  /**
   * Gets the ISO 639-2 language codes supported by this tokenizer.
   * @return A list of ISO 639-2 language codes supported by the tokenizer,
   * or an empty list if the tokenizer is not language-dependent.
   */
  List<String> getLanguageCodes();

    /**
     * Splits a string into its atomic parts
     *
     * @param s The string to be tokenized.
     * @return  The String[] with the individual tokens as the array
     *          elements.
     */
    String[] tokenize(String s);

    /**
     * Finds the boundaries of atomic parts in a string.
     *
     * @param s The string to be tokenized.
     * @return The Span[] with the spans (offsets into s) for each
     * token as the individuals array elements.
     */
    Span[] tokenizePos(String s);

    /**
     * Splits a string into its atomic parts and stems the parts.
     *
     * @param s The string to be tokenized.
     * @param stemmer The {@link Stemmer} to be used during tokenization.
     * @return  The String[] with the individual stemmed tokens as the array
     *          elements.
     */
    String[] tokenize(String s, Stemmer stemmer);

}
