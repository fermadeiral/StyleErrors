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

package ai.idylnlp.opennlp.custom.features;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechToken;

import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

/**
 * Generates features for tokens based on the token's part of speech.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class TokenPartOfSpeechFeatureGenerator implements AdaptiveFeatureGenerator {

  private static final String POS_PREFIX = "tpos";

  private PartsOfSpeechTagger tagger;
  private Map<String, String> tokPosMap;

  public TokenPartOfSpeechFeatureGenerator(PartsOfSpeechTagger tagger) {

    this.tagger = tagger;
    tokPosMap = new HashMap<String, String>();

  }

  @Override
  public void createFeatures(List<String> features, String[] tokens, int index, String[] previousOutcomes) {

    String[] postags = getPostags(tokens);
    features.add(POS_PREFIX + "=" + postags[index]);

  }

  private String[] getPostags(String[] tokens) {

    String text = StringUtils.join(tokens, " ");

    if (tokPosMap.containsKey(text)) {

      return tokPosMap.get(text).split(" ");

    } else {

      List<PartsOfSpeechToken> partsOfSpeechTokens = tagger.tag(tokens);
      String[] tags = PartsOfSpeechToken.getTokens(partsOfSpeechTokens);

      tokPosMap.put(text, StringUtils.join(tags, " "));

      return tags;

    }

  }

}