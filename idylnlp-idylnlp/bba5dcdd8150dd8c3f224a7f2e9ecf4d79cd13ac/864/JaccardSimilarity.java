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
package ai.idylnlp.nlp.utils.distance;

import ai.idylnlp.model.nlp.strings.Similarity;

/**
 * Calculates Jaccard similarity.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class JaccardSimilarity implements Similarity {

  public static Similarity INSTANCE() {
    return new JaccardSimilarity();
  }

  @Override
  public double calculate(CharSequence s, CharSequence t) {

    org.apache.commons.text.similarity.JaccardSimilarity similatity = new org.apache.commons.text.similarity.JaccardSimilarity();

    return similatity.apply(s, t);

  }

}
