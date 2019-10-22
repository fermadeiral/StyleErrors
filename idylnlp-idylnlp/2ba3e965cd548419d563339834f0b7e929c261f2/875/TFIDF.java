/*******************************************************************************
 * Copyright 2019 Mountain Fog, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package ai.idylnlp.nlp.features;

import java.util.List;

/**
 * Implementation of TF-IDF.
 * 
 * Term Frequency - Inverse Document Frequency is a measure of how important a word is to a given
 * document in relation to a collection of documents.
 * 
 * Adapted from https://guendouz.wordpress.com/2015/02/17/implementation-of-tf-idf-in-java/.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class TFIDF {

  /**
   * Calculate the TF-IDF value for a given term.
   * 
   * @param doc The document in question.
   * @param docs A list of all documents.
   * @param term The term.
   * @return A value indicating the term's importance
   * to the document.
   */
  public double tfIdf(String[] doc, List<String[]> docs, String term) {
    return tf(doc, term) * idf(docs, term);
  }

  private double tf(String[] doc, String term) {
    double result = 0;
    for (String word : doc) {
      if (term.equalsIgnoreCase(word)) {
        result++;
      }
    }
    return result / doc.length;
  }

  private double idf(List<String[]> docs, String term) {
    double n = 0;
    for (String[] doc : docs) {
      for (String word : doc) {
        if (term.equalsIgnoreCase(word)) {
          n++;
          break;
        }
      }
    }
    return Math.log(docs.size() / n);
  }

}
