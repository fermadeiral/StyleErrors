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
package ai.idylnlp.nlp.documents.dl4j.model;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.manifest.DocumentModelManifest;
import ai.idylnlp.model.nlp.documents.AbstractDocumentClassifierConfiguration;

/**
 * Configuration for the OpenNLP document classifier.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningDocumentClassifierConfiguration extends AbstractDocumentClassifierConfiguration {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningDocumentClassifierConfiguration.class);

  private Collection<DocumentModelManifest> models;

  private DeepLearningDocumentClassifierConfiguration(Collection<DocumentModelManifest> models) {

    this.models = models;

  }

  /**
   * Builder class to construct {@link DeepLearningDocumentClassifierConfiguration}.
   *
   * @author Mountain Fog, Inc.
   *
   */
  public static class Builder {

    private Collection<DocumentModelManifest> models;

    public Builder withModels(Collection<DocumentModelManifest> models) {
      this.models = models;
      return this;
    }

    /**
     * Creates a configured {@link DeepLearningDocumentClassifierConfiguration}.
     * @return A configured {@link DeepLearningDocumentClassifierConfiguration}.
     */
    public DeepLearningDocumentClassifierConfiguration build() {

      return new DeepLearningDocumentClassifierConfiguration(models);

    }

  }

  public Collection<DocumentModelManifest> getModels() {
    return models;
  }

}
