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
package ai.idylnlp.model.nlp.ner;

import java.util.Set;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.nlp.pipeline.PipelineResponse;

/**
 * Response to an entity extraction request.
 * @author Mountain Fog, Inc.
 */
public class EntityExtractionResponse extends PipelineResponse {

  private Set<Entity> entities;
  private long extractionTime;
  private boolean successful;

  /**
   * Creates a response to a request to extract entities.
   * @param entities A collection of {@link Entity} objects.
   * @param extractionTime The extraction time in milliseconds.
   * @param successful Indicates if the extraction was successful.
   */
  public EntityExtractionResponse(Set<Entity> entities, long extractionTime, boolean successful) {

    this.entities = entities;
    this.extractionTime = extractionTime;
    this.successful = successful;

  }

  /**
   * Gets the extracted entities.
   * @return A collection of extracted entities.
   */
  public Set<Entity> getEntities() {
    return entities;
  }

  /**
   * Gets the time spent in milliseconds extracting the entities. This
   * value can be monitored to maintain a certain performance level.
   * @return The time spent in milliseconds to extract the entities.
   */
  public long getExtractionTime() {
    return extractionTime;
  }

  /**
   * Indicates if the extraction was successful.
   * @return <code>true</code> if the extraction was successful.
   */
  public boolean isSuccessful() {
    return successful;
  }

}
