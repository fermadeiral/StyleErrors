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
package ai.idylnlp.model.nlp.strings;

/**
 * Interface for string metrics calculations.
 *
 * @author Mountain Fog, Inc.
 *
 */
public abstract interface StringMetrics {

  /**
   * Calculate the distance or similarity between two strings.
   * @param s An input string.
   * @param t An input string.
   * @return The distance or similarity between the two input strings.
   */
  double calculate(CharSequence s, CharSequence t);

}
