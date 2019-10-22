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
package ai.idylnlp.nlp.recognizer;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.entity.Entity;
import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.AbstractEntityRecognizer;
import ai.idylnlp.model.nlp.SentenceSanitizer;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;
import ai.idylnlp.nlp.recognizer.configuration.OpenNLPEntityRecognizerConfiguration;
import ai.idylnlp.nlp.sentence.sanitizers.DefaultSentenceSanitizer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.namefind.TokenNameFinderModel;

/**
 * Implementation of {@link EntityRecognizer} for performing
 * named-entity recognition for natural language text using OpenNLP.
 *
 * Some code to get the character-based indexes for the spans
 * was adapted from CLAVIN (https://github.com/Berico-Technologies/CLAVIN)
 * and used under the Apache Software License, version 2.0.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLPEntityRecognizer extends AbstractEntityRecognizer<OpenNLPEntityRecognizerConfiguration> implements EntityRecognizer {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPEntityRecognizer.class);

  /**
   * Create a new ModelEntityRecognizer class that is configured
   * per the {@link OpenNLPEntityRecognizerConfiguration}.
   * @param configuration The {@link OpenNLPEntityRecognizerConfiguration configuration}.
   * @param tokenizer The {@link Tokenizer tokenizer} for the text.
   */
  public OpenNLPEntityRecognizer(OpenNLPEntityRecognizerConfiguration configuration) {

    super(configuration);

    if(configuration.isPreloadModels() && configuration.getEntityModels().size() > 0) {

      // Load the models into memory.
      LOGGER.debug("Preloading the models.");

      // Preload the entity models by looping over all of the entity model types.
      for(String type : configuration.getEntityModels().keySet()) {

        LOGGER.debug("Preloading models for entity type {}.", type);

        // Loop over all languages for this entity type.
        for(LanguageCode language : configuration.getEntityModels().get(type).keySet()) {

          // Get the model file name for this entity type for this language.
          Set<StandardModelManifest> modelManifests = configuration.getEntityModels().get(type).get(language);

          LOGGER.debug("There are {} model manifests to preload for entity type {}.", modelManifests.size(), type);

          for(StandardModelManifest modelManifest : modelManifests) {

            if(!configuration.getBlacklistedModelIDs().contains(modelManifest.getModelId())) {

              LOGGER.debug("Preloading model file {}.", modelManifest.getModelFileName());

              try {

                configuration.getEntityModelLoader().getModel(modelManifest, TokenNameFinderModel.class);

              } catch (ModelLoaderException ex) {

                LOGGER.error("Unable to load model: " + modelManifest.getModelFileName(), ex);
                LOGGER.warn("Model {} is blacklisted. Loading will not be attempted until restart.", modelManifest.getModelFileName());

                // Automatically blacklist this model.
                configuration.getBlacklistedModelIDs().add(modelManifest.getModelId());

              }

            }

          }

        }

      }

    } else {

      if(configuration.getEntityModels().size() > 0) {

        LOGGER.info("Model preloading is disabled.");

      } else {

        // Model preloading was enabled but no models were specified.

        LOGGER.warn("Model preloading was enabled but no entity models were specified.");

      }

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityExtractionResponse extractEntities(EntityExtractionRequest request) throws EntityFinderException, ModelLoaderException {

    if(request.getText().length == 0) {

      throw new IllegalArgumentException("Input text cannot be empty.");

    }

    if(request.getConfidenceThreshold() < 0 || request.getConfidenceThreshold() > 100) {

      throw new IllegalArgumentException("Confidence threshold must be an integer between 0 and 100.");

    }

    // The sanitizer without any properties set does not do anything.
    SentenceSanitizer sentenceSanitizer = new DefaultSentenceSanitizer.Builder().build();

    // All of the extracted entities.
    Set<Entity> entities = new LinkedHashSet<Entity>();

    // Keep track of the extraction time.
    long startTime = System.currentTimeMillis();

    String types[] = {};

    if(!StringUtils.isEmpty(request.getType())) {
      types = request.getType().split(",");
    }

    for(String type : getConfiguration().getEntityModels().keySet()) {

      if(types.length == 0 || ArrayUtils.contains(types, type)) {

        LOGGER.trace("Processing entity class {}.", type);

        LanguageCode language = request.getLanguage();

        // The manifests of the models that will be used for this extraction.
        Set<StandardModelManifest> modelManifests = new HashSet<StandardModelManifest>();

        if(request.getLanguage() == null) {

          // TODO: Run all languages to support multilingual documents.
          Set<LanguageCode> languages = getConfiguration().getEntityModels().get(type).keySet();

          for(LanguageCode l : languages) {
            modelManifests.addAll(getConfiguration().getEntityModels().get(type).get(l));
          }

        } else {

          // We are doing a single language.

          Map<LanguageCode, Set<StandardModelManifest>> models = getConfiguration().getEntityModels().get(type);

          // If there are not any models for this entity type <code>models</code> will be null.
          if(models != null) {

            Set<StandardModelManifest> manifests = models.get(language);

            // If <code>manifests</code> is not null add those manifests to the set.
            if(manifests != null) {

              modelManifests.addAll(manifests);

            }

          }

        }

        if(CollectionUtils.isNotEmpty(modelManifests)) {

          for(StandardModelManifest modelManifest : modelManifests) {

            LOGGER.trace("{} has {} entity models.", type, modelManifests.size());

            if(!configuration.getBlacklistedModelIDs().contains(modelManifest.getModelId())) {

              // Create the token name finder model.
              final TokenNameFinderModel tokenNameFinderModel = getConfiguration().getEntityModelLoader().getModel(modelManifest, TokenNameFinderModel.class);

              // The tokenNameFinderModel can be null in cases in which model validation failed.
              if(tokenNameFinderModel != null) {

                // Get the nameFinder for this model if it exists.
                TokenNameFinder nameFinderMe = nameFinders.get(modelManifest);

                if(nameFinderMe == null) {
                  // Create a new namefinder and put it in the map of model manifests to name finders.
                  nameFinderMe = new NameFinderME(tokenNameFinderModel);
                  nameFinders.put(modelManifest, nameFinderMe);
                }

                // Extract the entities.
                final Collection<Entity> extractedEntities = findEntities(nameFinderMe, request, modelManifest, sentenceSanitizer);

                // TODO: Clear the adaptive data after each entity extraction.
                // This really has no effect because the NameFinderME is reinstantiated for every entity extraction request.
                // Having a single NameFinderME would run into the threadsafe issue.
                nameFinderMe.clearAdaptiveData();

                // Want to return all entities.
                entities.addAll(extractedEntities);

              }

            } else {

              LOGGER.warn("Entity model {} is blacklisted. Reload will not be attempted until restart.", modelManifest.getModelFileName());

            }

          }

        } else {

          LOGGER.warn("No entity models available for language {}.", language.getAlpha3().toString());

        }

      }

    }

    long extractionTime = (System.currentTimeMillis() - startTime);

    // Create the response with the extracted entities and the time it took to extract them.
    EntityExtractionResponse response = new EntityExtractionResponse(entities, extractionTime, true);

    return response;

  }

}
