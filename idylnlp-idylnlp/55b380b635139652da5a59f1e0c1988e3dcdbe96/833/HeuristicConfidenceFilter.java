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
package ai.idylnlp.nlp.filters.confidence;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.apache.commons.math3.stat.inference.TTest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.nlp.ConfidenceFilter;
import ai.idylnlp.model.nlp.ConfidenceFilterSerializer;

/**
 * Implementation of {@link ConfidenceFilter} that uses a T-test to
 * determine if an entity should be filtered or not.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class HeuristicConfidenceFilter implements ConfidenceFilter {

  private static final Logger LOGGER = LogManager.getLogger(HeuristicConfidenceFilter.class);

  protected Map<String, SynchronizedSummaryStatistics> statistics  = new HashMap<String, SynchronizedSummaryStatistics>();
  private TTest ttest = new TTest();

  private ConfidenceFilterSerializer serializer;
  private int minSampleSize = 50;
  private double alpha = 0.05;

  private boolean dirty = false;

  /**
   * Creates a new filter.
   */
  public HeuristicConfidenceFilter(ConfidenceFilterSerializer serializer) {

    this.serializer = serializer;

  }

  /**
   * Creates a new filter.
   * @param minSampleSize The minimum sample size before tests are used
   * to filter the entities.
   * @param alpha The alpha value for the test. 0.5 for 95% is recommended.
   */
  public HeuristicConfidenceFilter(ConfidenceFilterSerializer serializer, int minSampleSize, double alpha) {

    this.serializer = serializer;
    this.minSampleSize = minSampleSize;
    this.alpha = alpha;

    ttest = new TTest();

  }

  @Override
  public boolean test(String modelId, double entityConfidence, double confidenceThreshold) {

    SynchronizedSummaryStatistics confidences = statistics.get(modelId);

    if(confidences == null) {

      confidences = new SynchronizedSummaryStatistics();
      statistics.put(modelId, confidences);

    }

    boolean filter = false;

    if(entityConfidence >= confidenceThreshold) {

      // If the entity's confidence is greater than the threshold
      // always return the entity.

      filter = true;

    } else {

      if(confidences.getN() >= minSampleSize) {

        // Null hypothesis: The confidence of the entity is not in the ballpark.

        // Performs a two-sided t-test evaluating the null hypothesis that the mean of
        // the population from which the dataset described by stats is drawn equals mu.
        // Returns true iff the null hypothesis can be rejected with confidence 1 - ALPHA.
        // To perform a 1-sided test, use ALPHA * 2.
        filter = !ttest.tTest(entityConfidence, confidences, alpha * 2);

        // true means do NOT return the entity.
        // false means return the entity.

      } else {

        filter = true;

      }

    }

    // Add this value to the statistics after doing the T-test.
    confidences.addValue(entityConfidence);

    // Mark it as dirty.
    dirty = true;

    return filter;

  }

  @Override
  public int serialize() throws Exception {
    dirty = false;
    return serializer.serialize(statistics);
  }

  @Override
  public int deserialize() throws Exception {
    dirty = false;
    return serializer.deserialize(statistics);
  }

  @Override
  public void resetAll() {
    statistics.clear();
  }

  @Override
  public void reset(String modelId) {
    statistics.get(modelId).clear();
  }

  @Override
  public boolean isDirty() {
    return dirty;
  }

}
