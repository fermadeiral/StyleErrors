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
package ai.idylnlp.model.nlp.language;

import java.util.Collection;

/**
 * Interface for stop word removers.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface StopWordRemover {

  /**
   * Determines if the input string is a stop word.
   * @param input The input string.
   * @return True if the input is a stop word.
   */
  public boolean isStopWord(String input);

  /**
   * Removes all (if any) stop words from the input strings.
   * @param input A collection of input strings.
   * @return A subset of the input minus strings that were stop words.
   */
  public Collection<String> removeStopWords(Collection<String> input);

}
