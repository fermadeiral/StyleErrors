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
package ai.idylnlp.model.manifest;

import java.util.List;

import com.neovisionaries.i18n.LanguageCode;

public class DocumentModelManifest extends ModelManifest {

  public static final String TYPE = "document";

  private List<String> labels;

  // TODO: Change to a builder pattern like StandardModelManifest.

  public DocumentModelManifest(String modelId, String modelFileName, LanguageCode languageCode,
      String type, String name,
      String creatorVersion, String source, List<String> labels) {

    super(modelId, modelFileName, languageCode, type, name, creatorVersion, source, ModelManifest.SECOND_GENERATION);

    this.labels = labels;

  }

  public List<String> getLabels() {
    return labels;
  }

  public LanguageCode getLanguageCode() {
    return languageCode;
  }

}
