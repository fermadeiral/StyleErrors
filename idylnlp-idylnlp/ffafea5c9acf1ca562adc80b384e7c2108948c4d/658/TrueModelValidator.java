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
package ai.idylnlp.opennlp.custom.validators;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.exceptions.ValidationException;
import ai.idylnlp.model.manifest.ModelManifest;

/**
 * A model validator that always returns true. Useful for testing or
 * when no validation is needed.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class TrueModelValidator implements ModelValidator {

  @Override
  public boolean validate(ModelManifest manifest) throws ValidationException {
    return true;
  }

}