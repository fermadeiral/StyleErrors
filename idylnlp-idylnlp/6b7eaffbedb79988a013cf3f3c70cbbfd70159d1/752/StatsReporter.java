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
package ai.idylnlp.model.stats;

import ai.idylnlp.model.entity.Entity;

import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest;

/**
 * Interface for statistics reporters. Note that Idyl NLP
 * does not care how statistics are reported. Any implementations
 * of this class are allowed. This allows the user flexibility
 * to use any statistics reporting methods available to them.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface StatsReporter {

  /**
   * The count of entity extraction requests.
   */
  public final String EXTRACTION_REQUESTS = "extraction.requests";

  /**
   * The total count of extracted entities.
   */
  public final String ENTITY_COUNT = "entity.count";

  /**
   * Record an entity extraction.
   * @param entity The extracted {@link Entity entity}.
   * @param modelManifest The {@link StandardModelManifest} that extracted the entity.
   */
  public void recordEntityExtraction(Entity entity, ModelManifest modelManifest);

  /**
   * Increment a value.
   * @param metricName The name of the metric.
   */
  public void increment(String metricName);

  /**
   * Increments a value.
   * @param metricName The name of the metric.
   * @param value The value.
   */
  public void increment(String metricName, long value);

  /**
   * Report an elapsed time.
   * @param metricName The name of the metric.
   * @param elapsedTime The elapsed time.
   */
  public void time(String metricName, long elapsedTime);

}
