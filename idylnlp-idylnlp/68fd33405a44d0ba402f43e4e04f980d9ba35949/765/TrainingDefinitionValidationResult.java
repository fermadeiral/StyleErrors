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
package ai.idylnlp.training.definition.model;

import java.util.List;

public class TrainingDefinitionValidationResult {

  private boolean valid;
  private List<String> messages;

  public TrainingDefinitionValidationResult() {

    this.valid = true;

  }

  public TrainingDefinitionValidationResult(boolean valid, List<String> messages) {

    this.valid = valid;
    this.messages = messages;

  }

  public boolean isValid() {
    return valid;
  }

  public List<String> getMessages() {
    return messages;
  }

}
