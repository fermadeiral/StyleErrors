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
package ai.idylnlp.nlp.recognizer;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.util.ModelSerializer;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.manifest.SecondGenModelManifest;
import ai.idylnlp.model.nlp.AbstractEntityRecognizer;
import ai.idylnlp.model.nlp.SentenceSanitizer;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;
import ai.idylnlp.nlp.sentence.sanitizers.DefaultSentenceSanitizer;
import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.nlp.recognizer.configuration.DeepLearningEntityRecognizerConfiguration;
import ai.idylnlp.nlp.recognizer.deep.DeepLearningTokenNameFinder;
import opennlp.tools.namefind.TokenNameFinder;

/**
 * An {@link EntityRecognizer} that is powered by the
 * deeplearning4j framework. It uses a neural network
 * to perform entity extraction.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningEntityRecognizer extends AbstractEntityRecognizer<DeepLearningEntityRecognizerConfiguration> implements EntityRecognizer {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningEntityRecognizer.class);

  // Language -> (Type, [Network, Vectors])
  private Map<LanguageCode, Map<String, ImmutablePair<MultiLayerNetwork, WordVectors>>> loadedModels;

  public DeepLearningEntityRecognizer(DeepLearningEntityRecognizerConfiguration configuration) {

    super(configuration);

    loadedModels = new HashMap<LanguageCode, Map<String, ImmutablePair<MultiLayerNetwork, WordVectors>>>();

    for(String type : configuration.getEntityModels().keySet()) {

      Map<LanguageCode, Set<SecondGenModelManifest>> types = configuration.getEntityModels().get(type);

      for(LanguageCode language : types.keySet()) {

        for(SecondGenModelManifest modelManifest : types.get(language)) {

          if(!configuration.getBlacklistedModelIDs().contains(modelManifest.getModelId())) {

            try {

              final String modelFileName = new File(configuration.getEntityModelDirectory(), modelManifest.getModelFileName()).getAbsolutePath();

              // Load the network from the model file.
              LOGGER.info("Loading {} {} model from file: {}", language.getAlpha3().toString(), type, modelFileName);

              final File modelFile = new File(modelFileName);

              final MultiLayerNetwork multiLayerNetwork = ModelSerializer.restoreMultiLayerNetwork(modelFile.getAbsolutePath());

              final String vectorsFileName = new File(configuration.getEntityModelDirectory(), modelManifest.getVectorsFileName()).getAbsolutePath();

              // Verify the vectors file exists.
              final File vectorsFile = new File(vectorsFileName);

              // Load the word vectors from the file.
              LOGGER.info("Loading vectors from file: {}", vectorsFileName);
              final WordVectors wordVectors = WordVectorSerializer.loadStaticModel(vectorsFile);

              final Map<String, ImmutablePair<MultiLayerNetwork, WordVectors>> t = new HashMap<>();
              t.put(type, new ImmutablePair<MultiLayerNetwork, WordVectors>(multiLayerNetwork, wordVectors));

              loadedModels.put(language, t);

            } catch (Exception ex) {

              LOGGER.error("Unable to load model: " + modelManifest.getModelFileName(), ex);

              getConfiguration().getBlacklistedModelIDs().add(modelManifest.getModelId());
              LOGGER.warn("Model {} is blacklisted. Loading will not be attempted until restart.", modelManifest.getModelFileName());

              // TODO: This should probably be made visible to the user somehow - maybe through the API?

            }

          } else {

            LOGGER.info("Model {} is blacklisted. Loading will not be attempted until restart.", modelManifest.getModelFileName());

          }

        }

      }

    }

  }

  @Override
  public EntityExtractionResponse extractEntities(EntityExtractionRequest request)
      throws EntityFinderException, ModelLoaderException {

    if(request.getText().length == 0) {

      throw new IllegalArgumentException("Input text cannot be empty.");

    }

    if(request.getConfidenceThreshold() < 0 || request.getConfidenceThreshold() > 100) {

      throw new IllegalArgumentException("Confidence threshold must be an integer between 0 and 100.");

    }

    SentenceSanitizer sentenceSanitizer = new DefaultSentenceSanitizer.Builder().lowerCase().removePunctuation().consolidateSpaces().build();

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
        Set<SecondGenModelManifest> modelManifests = new HashSet<SecondGenModelManifest>();

        if(request.getLanguage() == null) {

          // TODO: Run all languages to support multilingual documents.
          Set<LanguageCode> languages = getConfiguration().getEntityModels().get(type).keySet();

          for(LanguageCode l : languages) {
            modelManifests.addAll(getConfiguration().getEntityModels().get(type).get(l));
          }

        } else {

          // We are doing a single language.

          Map<LanguageCode, Set<SecondGenModelManifest>> models = getConfiguration().getEntityModels().get(type);

          // If there are not any models for this entity type <code>models</code> will be null.
          if(models != null) {

            Set<SecondGenModelManifest> manifests = models.get(language);

            // If <code>manifests</code> is not null add those manifests to the set.
            if(manifests != null) {

              modelManifests.addAll(manifests);

            }

          }

        }

        if(CollectionUtils.isNotEmpty(modelManifests)) {

          for(SecondGenModelManifest modelManifest : modelManifests) {

            LOGGER.debug("{} has {} entity models.", type, modelManifests.size());

            String t = modelManifest.getType();

            // Get the network and word vectors for this language.
            LOGGER.info("Getting model for type {}, language {}", modelManifest.getLanguageCode().getAlpha3().toString(), t);

            ImmutablePair<MultiLayerNetwork, WordVectors> pair = loadedModels.get(modelManifest.getLanguageCode()).get(t);

            MultiLayerNetwork multiLayerNetwork = pair.getLeft();
            WordVectors wordVectors = pair.getRight();

            // Get the nameFinder for this model if it exists.
            TokenNameFinder nameFinder = nameFinders.get(modelManifest);

            if(nameFinder == null) {

              // Create a new namefinder and put it in the map.
              nameFinder = new DeepLearningTokenNameFinder(multiLayerNetwork, wordVectors,
                  modelManifest.getWindowSize(), getLabels(request.getType()));

              nameFinders.put(modelManifest, nameFinder);

            }

            Collection<Entity> extractedEntities = findEntities(nameFinder, request, modelManifest, sentenceSanitizer);

            entities.addAll(extractedEntities);

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

  private String[] getLabels(String entityType) {

    return new String[] { entityType + "-start", entityType + "-cont", "other" };

  }

}
