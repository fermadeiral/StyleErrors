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
package ai.idylnlp.model;

import ai.idylnlp.model.exceptions.ValidationException;
import ai.idylnlp.model.manifest.ModelManifest;

public interface ModelValidator {

  /**
   * Validates the model against some logic. For example, you may want to validate the
   * model's version against your current code for compatibility.
   * @param manifest The {@link ModelManifest} for the model to validate.
   * @return <code>true</code> if validation is successful.
   * @throws ValidationException Thrown if the validation cannot make a
   * determination of valid or not. In this case the validation should
   * likely be treated as failed.
   */
  boolean validate(ModelManifest manifest) throws ValidationException;
  
}
