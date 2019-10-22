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
package ai.idylnlp.nlp.recognizer.configuration;

import java.io.File;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.manifest.SecondGenModelManifest;
import ai.idylnlp.model.nlp.ConfidenceFilter;
import ai.idylnlp.model.nlp.configuration.AbstractEntityRecognizerConfiguration;
import ai.idylnlp.nlp.filters.confidence.SimpleConfidenceFilter;

import ai.idylnlp.nlp.recognizer.DeepLearningEntityRecognizer;

/**
 * Configuration for a {@link DeepLearningEntityRecognizer}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningEntityRecognizerConfiguration extends AbstractEntityRecognizerConfiguration<SecondGenModelManifest> {

  private String entityModelDirectory;

  public static class Builder {

    private ConfidenceFilter confidenceFilter;
    private Set<String> blacklistedModelIDs;
    private Map<String, Map<LanguageCode, Set<SecondGenModelManifest>>> entityModels;
    
    public Builder withConfidenceFilter(ConfidenceFilter confidenceFilter) {
      this.confidenceFilter = confidenceFilter;
      return this;
    }

    public Builder withBlacklistedModelIDs(Set<String> blacklistedModelIDs) {
      this.blacklistedModelIDs = blacklistedModelIDs;
      return this;
    }
    
    public Builder withEntityModels(Map<String, Map<LanguageCode, Set<SecondGenModelManifest>>> entityModels) {
        this.entityModels = entityModels;
        return this;
      }

    /**
     * Creates the configuration.
     * @param entityModelDirectory The full path to the models directory.
     * @return A configured {@link DeepLearningEntityRecognizerConfiguration}.
     */
    public DeepLearningEntityRecognizerConfiguration build(String entityModelDirectory) {

      if(!entityModelDirectory.endsWith(File.separator)) {
        entityModelDirectory = entityModelDirectory + File.separator;
      }

      if(confidenceFilter == null) {
        confidenceFilter = new SimpleConfidenceFilter();
      }

      if(blacklistedModelIDs == null) {
        blacklistedModelIDs = new LinkedHashSet<String>();
      }
      
      return new DeepLearningEntityRecognizerConfiguration(entityModelDirectory, confidenceFilter, blacklistedModelIDs, entityModels);

    }

  }

  private DeepLearningEntityRecognizerConfiguration(
      String entityModelDirectory,
      ConfidenceFilter confidenceFilter,
      Set<String> blacklistedModelIDs,
      Map<String, Map<LanguageCode, Set<SecondGenModelManifest>>> entityModels) {

    super(blacklistedModelIDs);

    this.entityModelDirectory = entityModelDirectory;
    this.blacklistedModelIDs = blacklistedModelIDs;
    this.confidenceFilter = confidenceFilter;
    this.entityModels = entityModels;
    
  }

  public String getEntityModelDirectory() {
    return entityModelDirectory;
  }

}
