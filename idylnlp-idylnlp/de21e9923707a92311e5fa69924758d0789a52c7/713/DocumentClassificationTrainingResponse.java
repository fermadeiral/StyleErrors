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
package ai.idylnlp.model.nlp.documents;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * The response from training a document classification model.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DocumentClassificationTrainingResponse {

  private String modelId;
  private Map<DocumentClassificationFile, File> files;
  private List<String> categories;

  public DocumentClassificationTrainingResponse(String modelId, Map<DocumentClassificationFile, File> files,
      List<String> categories) {

    this.modelId = modelId;
    this.files = files;
    this.categories = categories;

  }

  public String getModelId() {
    return modelId;
  }

  public void setModelId(String modelId) {
    this.modelId = modelId;
  }

  public Map<DocumentClassificationFile, File> getFiles() {
    return files;
  }

  public void setFiles(Map<DocumentClassificationFile, File> files) {
    this.files = files;
  }

  public List<String> getCategories() {
    return categories;
  }

  public void setCategories(List<String> categories) {
    this.categories = categories;
  }

}
