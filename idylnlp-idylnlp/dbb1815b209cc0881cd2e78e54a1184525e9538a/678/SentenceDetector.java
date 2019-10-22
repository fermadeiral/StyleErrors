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

public interface SentenceDetector {

  /**
   * Gets the ISO 639-2 language codes supported by this tokenizer.
   * @return A list of ISO 639-2 language codes supported by the tokenizer,
   * or an empty list if the tokenizer is not language-dependent.
   */
  List<String> getLanguageCodes();

    /**
     * Sentence detect a string.
     *
     * @param s The string to be sentence detected.
     * @return  The String[] with the individual sentences as the array
     *          elements.
     */
    public String[] sentDetect(String s);

    /**
     * Sentence detect a string.
     *
     * @param s The string to be sentence detected.
     *
     * @return The Span[] with the spans (offsets into s) for each
     * detected sentence as the individuals array elements.
     */
    public Span[] sentPosDetect(String s);

}
