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
package ai.idylnlp.model.nlp;

/**
 * Interface for confidence filters.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface ConfidenceFilter {

  /**
   * Test the entity's confidence.
   * @param modelId The ID of the model that extracted the entity.
   * @param entityConfidence The entity's confidence.
   * @param confidenceThreshold The confidence threshold.
   * @return <code>true</code> if the entity should not be filtered out; otherwise <code>false</code>.
   */
  public boolean test(String modelId, double entityConfidence, double confidenceThreshold);

  /**
   * Serialize the confidence values.
   * @return The number of entries serialized.
   * @throws Exception Thrown if the serialization fails.
   */
  public int serialize() throws Exception;

  /**
   * Deserialize the confidence values.
   * @return The number of entries deserialized.
   * @throws Exception Thrown if the deserialization fails.
   */
  public int deserialize() throws Exception;

  /**
   * Resets all stored confidence values.
   */
  public void resetAll();

  /**
   * Resets all stored confidence values for a single model.
   * @param modelId The model for which to reset values.
   */
  public void reset(String modelId);

  /**
   * Gets if the values are dirty.
   * @return <code>true</code> if the values are dirty.
   */
  public boolean isDirty();
}
