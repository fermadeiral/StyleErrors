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
package ai.idylnlp.model.nlp.documents;

/**
 * A file produced as the result of training a document
 * classification model.
 *
 * @author Mountain Fog, Inc.
 *
 */
public enum DocumentClassificationFile {

  /**
   * A model file.
   */
  MODEL_FILE("Model"),
  MODEL_MANIFEST("Model Manifest");

  private String description;

  private DocumentClassificationFile(String description) {

    this.description = description;

  }

  public String getDescription() {
    return description;
  }

}
