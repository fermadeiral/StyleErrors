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

import com.neovisionaries.i18n.LanguageCode;

/**
 * An abstract model manifest.
 *
 * @author Mountain Fog, Inc.
 *
 */
public abstract class ModelManifest {

  /**
   * First generation MaxEnt model.
   */
  public static final int FIRST_GENERATION = 1;

  /**
   * Second generation deep learning model.
   */
  public static final int SECOND_GENERATION = 2;

  protected String modelId;
  protected String name;
  protected String modelFileName;
  protected LanguageCode languageCode;
  protected String type;
  protected String creatorVersion;
  protected String source;

  // The "generation" property of the manifest allows
  // us to tell the generation of the model that the
  // manifest describes.
  protected int generation;

  protected ModelManifest(String modelId, String modelFileName, LanguageCode languageCode,
      String type, String name, String creatorVersion, String source, int generation) {

    this.modelId = modelId;
    this.modelFileName = modelFileName;
    this.languageCode = languageCode;
    this.type = type;
    this.name = name;
    this.creatorVersion = creatorVersion;
    this.source = source;
    this.generation = generation;

  }

  public String getModelId() {
    return modelId;
  }

  public String getName() {
    return name;
  }

  public String getModelFileName() {
    return modelFileName;
  }

  public LanguageCode getLanguageCode() {
    return languageCode;
  }

  public String getType() {
    return type;
  }

  public String getCreatorVersion() {
    return creatorVersion;
  }

  public int getGeneration() {
    return generation;
  }

  public String getSource() {
    return source;
  }

  public void setSource(String source) {
    this.source = source;
  }

}
