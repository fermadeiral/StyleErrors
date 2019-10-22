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
package ai.idylnlp.nlp.sentence.sanitizers;

import org.apache.commons.lang3.StringUtils;

import ai.idylnlp.model.nlp.SentenceSanitizer;

/**
 * Default implementation of {@link SentenceSanitizer}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DefaultSentenceSanitizer implements SentenceSanitizer {

  private boolean removePunctuation = false;
  private boolean lowerCase = false;
  private boolean consolidateSpaces = false;

  /**
   * Use the builder.
   */
  private DefaultSentenceSanitizer() {

  }

  public static class Builder {

    private boolean removePunctuation = false;
    private boolean lowerCase = false;
    private boolean consolidateSpaces = false;

    /**
     * Removes all punctuation from the text.
     * @return The text with all punctuation removed.
     */
    public Builder removePunctuation() {
      this.removePunctuation = true;
      return this;
    }

    /**
     * Lowercases the text.
     * @return The text lowercased.
     */
    public Builder lowerCase() {
      this.lowerCase = true;
      return this;
    }

    /**
     * Replaces all consecutive spaces with a single space.
     * @return The text with all consecutive spaces replaced
     * with a single space.
     */
    public Builder consolidateSpaces() {
      this.consolidateSpaces = true;
      return this;
    }

    /**
     * Builds the sentence sanitizer.
     * @return A configured {@link SentenceSanitizer}.
     */
    public SentenceSanitizer build() {

      DefaultSentenceSanitizer sanitizer = new DefaultSentenceSanitizer();
      sanitizer.removePunctuation = removePunctuation;
      sanitizer.lowerCase = lowerCase;
      sanitizer.consolidateSpaces = consolidateSpaces;

      return sanitizer;

    }

  }

  @Override
  public String sanitize(String sentence) {

    if(lowerCase) {
      sentence = sentence.toLowerCase();
    }

    if(removePunctuation) {
      sentence = sentence.replaceAll("\\p{Punct}+", "");
    }

    if(consolidateSpaces) {
      sentence = StringUtils.normalizeSpace(sentence);
    }

    return sentence;

  }

}
