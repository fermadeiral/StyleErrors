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
package ai.idylnlp.model.nlp.documents;

import java.util.Collections;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class DocumentClassificationScores {

  private Map<String, Double> scores;

  public DocumentClassificationScores(Map<String, Double> scores) {

    this.scores = scores;

  }

  public Pair<String, Double> getPredictedCategory() {

    final String maxEntry = Collections.max(scores.entrySet(), Map.Entry.comparingByValue()).getKey();

    return new ImmutablePair<String, Double>(maxEntry, scores.get(maxEntry));

  }


  public Map<String, Double> getScores() {
    return scores;
  }

  public void setScores(Map<String, Double> scores) {
    this.scores = scores;
  }

}
