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
package ai.idylnlp.nlp.utils.distance;

import java.util.Locale;

import org.apache.commons.text.similarity.FuzzyScore;

import ai.idylnlp.model.nlp.strings.Distance;

/**
 * Calculates fuzzy score distances.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class FuzzyScoreDistance implements Distance {

  private Locale locale;

  public static Distance INSTANCE(Locale locale) {
    return new FuzzyScoreDistance(locale);
  }

  /**
   * Creates a new instance.
   * @param locale The {@link locale} (used to normalize strings to lower case).
   */
  public FuzzyScoreDistance(Locale locale) {

    this.locale = locale;

  }

  @Override
  public double calculate(CharSequence s, CharSequence t) {

    FuzzyScore distance = new FuzzyScore(locale);

    return distance.fuzzyScore(s, t);

  }

}
