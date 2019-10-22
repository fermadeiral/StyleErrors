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
package ai.idylnlp.model.nlp.configuration;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.stats.StatsReporter;
import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.ConfidenceFilter;

public abstract class AbstractEntityRecognizerConfiguration<T extends ModelManifest> {

  private static final Logger LOGGER = LogManager.getLogger(AbstractEntityRecognizerConfiguration.class);

  protected boolean preloadModels = true;
  protected Set<String> blacklistedModelIDs;
  protected ConfidenceFilter confidenceFilter;
  protected StatsReporter statsReporter;

  protected Map<String, Map<LanguageCode, Set<T>>> entityModels = new HashMap<>();

  public AbstractEntityRecognizerConfiguration(Set<String> blacklistedModelIDs) {

    this.blacklistedModelIDs = blacklistedModelIDs;

  }

  /**
   * Adds a model to the list of entityModels to use during the extraction.
   * @param entityType The {@link String class} of the model's type.
   * @param language The {@link Language language} supported by the model.
   * @param modelManifest The {@link StandardModelManifest manifest} of the model.
   */
  public void addEntityModel(String entityType, LanguageCode language, T modelManifest) {

    if(entityModels.containsKey(entityType)) {

      // There already exists an entity class so add this model to it.

      Map<LanguageCode, Set<T>> m = entityModels.get(entityType);

      if(m.containsKey(language)) {

        LOGGER.trace("Adding manifest for model {}.", modelManifest.getModelId());

        entityModels.get(entityType).get(language).add(modelManifest);

      } else {

        Set<T> manifests = new HashSet<T>();
        manifests.add(modelManifest);

        m.put(language, manifests);

        entityModels.put(entityType, m);

      }

    } else {

      // This entity class does not exist so just add it.

      Map<LanguageCode, Set<T>> m = new HashMap<>();

      Set<T> modelManifests = new HashSet<T>();
      modelManifests.add(modelManifest);

      m.put(language, modelManifests);

      LOGGER.trace("Adding manifest for model {}.", modelManifest.getModelId());

      entityModels.put(entityType, m);

    }

  }

  /**
   * Gets the {@link ConfidenceFilter}.
   * @return The {@link ConfidenceFilter}.
   */
  public ConfidenceFilter getConfidenceFilter() {
    return confidenceFilter;
  }

  /**
   * Sets the {@link ConfidenceFilter}.
   * @param confidenceFilter The {@link ConfidenceFilter}.
   */
  public void setConfidenceFilter(ConfidenceFilter confidenceFilter) {
    this.confidenceFilter = confidenceFilter;
  }

  /**
   * Gets the {@link StatsReporter}.
   * @return The {@link StatsReporter}.
   */
  public StatsReporter getStatsReporter() {
    return statsReporter;
  }

  /**
   * Sets the {@link StatsReporter}.
   * @param confidenceFilter The {@link StatsReporter}.
   */
  public void setStatsReporter(StatsReporter statsReporter) {
    this.statsReporter = statsReporter;
  }

  /**
   * Gets a boolean indicating if entityModels will be preloaded.
   * @return A boolean indicating if entityModels will be preloaded.
   */
  public boolean isPreloadModels() {
    return preloadModels;
  }

  /**
   * Sets if entityModels will be preloaded.
   * @param preloadModels Set to true to enable model preloading.
   */
  public void setPreloadModels(boolean preloadModels) {
    this.preloadModels = preloadModels;
  }

  /**
   * Gets the entity models used during the entity extraction.
   * @return A map of entity models to their corresponding language and file names.
   */
  public Map<String, Map<LanguageCode, Set<T>>> getEntityModels() {
    return entityModels;
  }

  /**
   * Sets the entityModels used during the entity extraction.
   * @param entityModels A map of entityModels to their corresponding language and file names.
   */
  public void setEntityModels(Map<String, Map<LanguageCode, Set<T>>> entityModels) {
    this.entityModels = entityModels;
  }

  /**
   * Gets the set of blacklisted model IDs.
   * @return The set of blacklisted model IDs.
   */
  public Set<String> getBlacklistedModelIDs() {
    return blacklistedModelIDs;
  }

  /**
   * Sets the set of blacklisted model IDs.
   * @param blacklistedModelIDs The set of blacklisted model IDs.
   */
  public void setBlacklistedModelIDs(Set<String> blacklistedModelIDs) {
    this.blacklistedModelIDs = blacklistedModelIDs;
  }

}
