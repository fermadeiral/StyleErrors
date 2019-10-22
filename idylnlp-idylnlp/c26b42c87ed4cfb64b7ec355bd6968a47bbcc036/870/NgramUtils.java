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
package ai.idylnlp.nlp.utils.ngrams;

import java.util.LinkedList;
import java.util.List;

/**
 * Utility functions for N-Grams.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class NgramUtils {

  private NgramUtils() {
    // This is a utility class.
  }

  /**
   * Returns the N-Grams for a string of a given length.
   * @param tokens An array of tokens.
   * @param len The length of the n-grams.
   * @return A collection of N-Grams for the input string.
   */
  public static String[] getNgrams(String[] tokens, int len) {

    final List<String> ngrams = new LinkedList<>();

      for(int i = 0; i < tokens.length - len + 1; i++) {

        StringBuilder sb = new StringBuilder();

         for(int k = 0; k < len; k++) {
             if(k > 0) sb.append(' ');
             sb.append(tokens[i+k]);
         }

         ngrams.add(sb.toString());

      }

      final String[] n = new String[ngrams.size()];
      return ngrams.toArray(n);

  }

  /**
   * Gets all n-grams for a given set of tokens.
   * @param tokens The tokens.
   * @return All n-grams for a given set of tokens.
   */
  public static String[] getNgrams(String[] tokens) {

    final List<String> ngrams = new LinkedList<>();

    for(int len = 1; len <= tokens.length; len++) {

        for(int i = 0; i < tokens.length - len + 1; i++) {

          StringBuilder sb = new StringBuilder();

           for(int k = 0; k < len; k++) {
               if(k > 0) sb.append(' ');
               sb.append(tokens[i+k]);
           }

           ngrams.add(sb.toString());

      }

    }

      final String[] n = new String[ngrams.size()];
      return ngrams.toArray(n);

  }

}
