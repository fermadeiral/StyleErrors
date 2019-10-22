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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;

/**
 * Generates features for tokens containing hyphens.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SpecialCharacterFeatureGenerator implements AdaptiveFeatureGenerator {

  private Pattern p;

  public SpecialCharacterFeatureGenerator() {

    p = Pattern.compile("[^a-z0-9 ]", Pattern.CASE_INSENSITIVE);

  }

  @Override
  public void createFeatures(List<String> features, String[] tokens, int index, String[] previousOutcomes) {

    Matcher m = p.matcher(tokens[index]);
    boolean containsSpecialCharacters = m.find();

    if(containsSpecialCharacters) {

      features.add("specchar=" + tokens[index]);

    }

  }

}