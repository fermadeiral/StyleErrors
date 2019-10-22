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
 ******************************************************************************/

package ai.idylnlp.opennlp.custom.features;

import java.util.List;

import ai.idylnlp.model.nlp.lemma.Lemmatizer;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechToken;

import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

/**
 * Generates features for normalized words.
 * For example, 'IL' is normalized to 'AA', 'IL-2' is normalized to 'AA-0'
 * and 'IL-8' is also normalized to 'AA-0'.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class WordNormalizationFeatureGenerator implements AdaptiveFeatureGenerator {

  private Lemmatizer modelLemmatizer;
  private Lemmatizer dictionaryLemmatizer;
  private PartsOfSpeechTagger partsOfSpeechTagger;

  public WordNormalizationFeatureGenerator(PartsOfSpeechTagger partsOfSpeechTagger, Lemmatizer modelLemmatizer, Lemmatizer dictionaryLemmatizer) {

    this.modelLemmatizer = modelLemmatizer;
    this.dictionaryLemmatizer = dictionaryLemmatizer;
    this.partsOfSpeechTagger = partsOfSpeechTagger;

  }

  @Override
  public void createFeatures(List<String> features, String[] tokens, int index, String[] previousOutcomes) {

    // A partsOfSpeechTagger is required for both lemmatizers.

    if(partsOfSpeechTagger != null) {

      List<PartsOfSpeechToken> partsOfSpeechTokens = partsOfSpeechTagger.tag(tokens);

      String[] tags = PartsOfSpeechToken.getTokens(partsOfSpeechTokens);

      if(modelLemmatizer != null) {
        tokens = modelLemmatizer.lemmatize(tokens, tags);
      }

      if(dictionaryLemmatizer != null) {
        tokens = dictionaryLemmatizer.lemmatize(tokens, tags);
      }

    }

    features.add("wnormal=" + normalize(tokens[index]));

  }

  private String normalize(String token) {

    String normalizedToken = token.replaceAll("([A-Z])", "A");
    normalizedToken = normalizedToken.replaceAll("([a-z])", "a");
    normalizedToken = normalizedToken.replaceAll("([0-9])", "0");

    return normalizedToken;

  }


}