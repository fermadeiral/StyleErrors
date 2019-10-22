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

import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.exceptions.ModelLoaderException;

/**
 * Interface for entity recognizers.
 * @author Mountain Fog, Inc.
 */
public interface EntityRecognizer {

  /**
   * Extracts entities. Implementations of this function likely need to be <code>synchronized</code>.
   * @param request {@link EntityExtractionRequest} that contains the text to process and input parameters.
   * @return {@link EntityExtractionResponse} that contains the extracted entities.
   * @throws EntityFinderException Thrown when entity extraction fails.
   * @throws ModelLoaderException Thrown when the model cannot be loaded.
   */
  public EntityExtractionResponse extractEntities(EntityExtractionRequest request) throws EntityFinderException, ModelLoaderException;

}
