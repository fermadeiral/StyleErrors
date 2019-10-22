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
package ai.idylnlp.nlp.documents.opennlp.model;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.documents.AbstractDocumentClassifierConfiguration;

/**
 * Configuration for the OpenNLP document classifier.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLPDocumentClassifierConfiguration extends AbstractDocumentClassifierConfiguration {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPDocumentClassifierConfiguration.class);

  private Map<LanguageCode, File> doccatModels;
  private boolean preloadModels;

  private OpenNLPDocumentClassifierConfiguration(Map<LanguageCode, File> doccatModels, boolean preloadModels) {

    this.doccatModels = doccatModels;
    this.preloadModels = preloadModels;

  }

  /**
   * Builder class to construct {@link OpenNLPDocumentClassifierConfiguration}.
   *
   * @author Mountain Fog, Inc.
   *
   */
  public static class Builder {

    private Map<LanguageCode, File> doccatModels;
    private boolean preloadModels;

    public Builder withDoccatModels(Map<LanguageCode, File> doccatModels) {
      this.doccatModels = doccatModels;
      return this;
    }

    public Builder withPreloadModels(boolean preloadModels) {
      this.preloadModels = preloadModels;
      return this;
    }

    /**
     * Creates a configured {@link OpenNLPDocumentClassifierConfiguration}.
     * @return A configured {@link OpenNLPDocumentClassifierConfiguration}.
     */
    public OpenNLPDocumentClassifierConfiguration build() {

      return new OpenNLPDocumentClassifierConfiguration(doccatModels, preloadModels);

    }

  }

  public Map<LanguageCode, File> getDoccatModels() {
    return doccatModels;
  }

  /**
   * Gets whether or not to preload the document classification models.
   * @return Whether or not to preload the document classification models.
   */
  public boolean isPreloadModels() {
    return preloadModels;
  }

}
