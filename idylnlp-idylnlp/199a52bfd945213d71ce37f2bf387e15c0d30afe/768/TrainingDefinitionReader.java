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
package ai.idylnlp.training.definition.model;

import ai.idylnlp.training.definition.xml.Trainingdefinition;

/**
 * Interface for training definition readers.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface TrainingDefinitionReader {

  /**
   * Validates the training definition.
   * @return A {@link TrainingDefinitionValidationResult}.
   */
  public TrainingDefinitionValidationResult validate();

  /**
   * Gets the feature generators from the training definition file.
   * @return The feature generators.
   */
  public String getFeatures();

  /**
   * Gets the feature generators.
   * @return The XML describing the feature generators.
   */
  public Trainingdefinition getTrainingDefinition();

}
