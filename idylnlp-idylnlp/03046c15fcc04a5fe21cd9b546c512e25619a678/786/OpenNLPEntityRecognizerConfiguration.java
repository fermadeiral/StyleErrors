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
package ai.idylnlp.nlp.recognizer.configuration;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import ai.idylnlp.opennlp.custom.modelloader.ModelLoader;
import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.ConfidenceFilter;
import ai.idylnlp.model.nlp.configuration.AbstractEntityRecognizerConfiguration;
import ai.idylnlp.nlp.filters.confidence.SimpleConfidenceFilter;
import ai.idylnlp.nlp.recognizer.OpenNLPEntityRecognizer;
import opennlp.tools.namefind.TokenNameFinderModel;

/**
 * Configuration sets the required parameters in order to
 * initialize the {@link OpenNLPEntityRecognizer}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLPEntityRecognizerConfiguration extends AbstractEntityRecognizerConfiguration<StandardModelManifest> {

  private ModelLoader<TokenNameFinderModel> entityModelLoader;

  private OpenNLPEntityRecognizerConfiguration(
      ModelLoader<TokenNameFinderModel> entityModelLoader,
      ConfidenceFilter confidenceFilter,
      Map<String, Map<LanguageCode, Set<StandardModelManifest>>> entityModels,
      Set<String> blacklistedModelIDs) {

    super(blacklistedModelIDs);

    this.entityModelLoader = entityModelLoader;
    this.confidenceFilter = confidenceFilter;
    this.entityModels = entityModels;

  }

  public static class Builder {

    private ModelLoader<TokenNameFinderModel> entityModelLoader;
    private ConfidenceFilter confidenceFilter;
    private Map<String, Map<LanguageCode, Set<StandardModelManifest>>> entityModels;
    private Set<String> blacklistedModelIDs;

    public Builder withEntityModels(Map<String, Map<LanguageCode, Set<StandardModelManifest>>> entityModels) {
      this.entityModels = entityModels;
      return this;
    }

    public Builder withEntityModelLoader(ModelLoader<TokenNameFinderModel> entityModelLoader) {
      this.entityModelLoader = entityModelLoader;
      return this;
    }

    public Builder withConfidenceFilter(ConfidenceFilter confidenceFilter) {
      this.confidenceFilter = confidenceFilter;
      return this;
    }

    public Builder withBlacklistedModelIDs(Set<String> blacklistedModelIDs) {
      this.blacklistedModelIDs = blacklistedModelIDs;
      return this;
    }

    public OpenNLPEntityRecognizerConfiguration build() {

      if(confidenceFilter == null) {
        confidenceFilter = new SimpleConfidenceFilter();
      }

      if(entityModels == null) {
        entityModels = new HashMap<String, Map<LanguageCode, Set<StandardModelManifest>>>();
      }

      if(blacklistedModelIDs == null) {
        blacklistedModelIDs = new LinkedHashSet<String>();
      }

      return new OpenNLPEntityRecognizerConfiguration(
        entityModelLoader, confidenceFilter, entityModels, blacklistedModelIDs
      );

    }

  }

  /**
   * Gets the entity model loader.
   * @return A {@link ModelLoader} for a {@link TokenNameFinderModel}.
   */
  public ModelLoader<TokenNameFinderModel> getEntityModelLoader() {
    return entityModelLoader;
  }

}
