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
package ai.idylnlp.nlp.filters.confidence;

import ai.idylnlp.model.nlp.ConfidenceFilter;

/**
 * An implementation of {@link ConfidenceFilter} that simply
 * compares the entity confidence with the confidence threshold.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SimpleConfidenceFilter implements ConfidenceFilter {

  @Override
  public boolean test(String modelId, double entityConfidence, double confidenceThreshold) {

    boolean filter = false;

    if(entityConfidence >= confidenceThreshold) {

      filter = true;

    } else {

      filter = false;

    }

    return filter;

  }

  @Override
  public int serialize() throws Exception {
    return 0;
  }

  @Override
  public int deserialize() throws Exception {
    return 0;
  }

  @Override
  public void resetAll() {

  }

  @Override
  public void reset(String modelId) {

  }

  @Override
  public boolean isDirty() {
    return false;
  }

}
