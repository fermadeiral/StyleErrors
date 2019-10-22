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
package ai.idylnlp.model.nlp;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.util.Precision;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.manifest.ModelManifest;

import ai.idylnlp.model.nlp.configuration.AbstractEntityRecognizerConfiguration;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;
import ai.idylnlp.model.nlp.sentiment.Sentiment;
import ai.idylnlp.model.nlp.sentiment.SentimentAnalysisException;
import ai.idylnlp.model.nlp.sentiment.SentimentAnalysisRequest;
import ai.idylnlp.model.nlp.sentiment.SentimentAnalyzer;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinder;

/**
 * Base class for entity recognizers.
 *
 * @author Mountain Fog, Inc.
 *
 */
public abstract class AbstractEntityRecognizer<T extends AbstractEntityRecognizerConfiguration> implements EntityRecognizer {

  private static final Logger LOGGER = LogManager.getLogger(AbstractEntityRecognizer.class);

  /**
   * The key for the model filename in the entity metadata.
   */
  public static final String METADATA_MODEL_FILENAME_KEY = "x-model-filename";

  public T configuration;

  // A map that contains the name finders to prevent reinstantiating them for every request.
  protected Map<ModelManifest, TokenNameFinder> nameFinders;

  public AbstractEntityRecognizer(T configuration) {

    this.configuration = configuration;
    this.nameFinders = new HashMap<ModelManifest, TokenNameFinder>();

  }

  /**
   * Finds entities.
   * @param nameFinder The {@link TokenNameFinder} for entity extraction.
   * @param tokenizer The {@link Tokenizer} to tokenize the text.
   * @param sentenceDetector The {@link SentenceDetector} to detect sentences.
   * @param entityExtractionRequest The entity extraction {@link EntityExtractionRequest}.
   * @param modelManifest The entity model's {@link ModelManifest manifest}.
   * @param sentenceSanitizer The {@link SentenceSanitizer}.
   * @return A collection of {@link Entity entities}.
   * @throws EntityFinderException Thrown if the entity extraction encounters an error.
   */
  protected Collection<Entity> findEntities(TokenNameFinder nameFinder, EntityExtractionRequest entityExtractionRequest,
      ModelManifest modelManifest, SentenceSanitizer sentenceSanitizer) throws EntityFinderException {

    LOGGER.trace("Identifying entities of type {} with confidence limit {}.", modelManifest.getType(),
        entityExtractionRequest.getConfidenceThreshold());

    Collection<Entity> entities = new LinkedList<Entity>();

    final String tokens[] = entityExtractionRequest.getText();

    try {

            // find the location names in the tokenized text
          // the values used in these Spans are NOT string character offsets, they are indices into the 'tokens' array
          opennlp.tools.util.Span names[] = nameFinder.find(tokens);

          // Simple way to drop intersecting spans, otherwise the NameSample is invalid
          opennlp.tools.util.Span reducedNames[] = NameFinderME.dropOverlappingSpans(names);

          // Get the text of the entities.
          String[] extractedEntities = opennlp.tools.util.Span.spansToStrings(reducedNames, tokens);

          double[] probabilities;

          if(nameFinder instanceof NameFinderME) {
            probabilities = ((NameFinderME) nameFinder).probs(reducedNames);
          } else {
            // All probabilities are 100 since a dictionary or regex name finder was used.
            probabilities = new double[reducedNames.length];
            Arrays.fill(probabilities, 1.0);
          }

          double normalizedConfidenceThreshold = ConfidenceNormalization.normalizeConfidence(entityExtractionRequest.getConfidenceThreshold());

          // Index for looping over the spans returned by OpenNLP.
          int x = 0;

          //for each name that got found, create our corresponding occurrence
          for (opennlp.tools.util.Span name : reducedNames) {

            // Check the confidence threshold for extraction.
              if(configuration.getConfidenceFilter().test(
                  modelManifest.getModelId(), probabilities[x], normalizedConfidenceThreshold)) {

                String entityText = extractedEntities[x];

                  // Sanitize the entity.
                  entityText = sentenceSanitizer.sanitize(entityText);

                  // Round the confidence value.
          double roundedConfidence = Precision.round(probabilities[x], 2, BigDecimal.ROUND_HALF_DOWN);

                  // Create a new entity object.
          Entity entity = new Entity(entityText, roundedConfidence, modelManifest.getType(), modelManifest.getLanguageCode().getAlpha3().toString());

          // TODO: Remove last two arguments.
          entity.setSpan(new ai.idylnlp.model.entity.Span(name.getStart(), name.getEnd()));
          entity.setContext(entityExtractionRequest.getContext());
          entity.setExtractionDate(System.currentTimeMillis());

          if(entityExtractionRequest.isIncludeModelFileNameInMetadata()) {

            // TODO: Put the model filename in the entity metadata.
            entity.getMetadata().put(METADATA_MODEL_FILENAME_KEY, modelManifest.getModelFileName());

          }

          LOGGER.debug("Found entity with text: " + entityText + "; confidence: " + probabilities[x] + "; language: " + modelManifest.getLanguageCode());

          // Process the statistics for the entity.
          if(configuration.getStatsReporter() != null) {
            configuration.getStatsReporter().recordEntityExtraction(entity, modelManifest);
          }

          entities.add(entity);

        }

          }

    } catch (Exception ex) {

      LOGGER.error("Unable to find entities.", ex);

      throw new EntityFinderException("Unable to find entities.", ex);

    }

        LOGGER.trace("Returning {} entities.", entities.size());

    return entities;

  }

  protected Map<String, String> getSentiments(String text, List<SentimentAnalyzer> sentimentAnalyzers) {

      Map<String, String> sentiments = new LinkedHashMap<String, String>();

      // Run sentiment analysis on the sentence.
      for(SentimentAnalyzer sentimentAnalyzer : sentimentAnalyzers) {

        try {

          Sentiment sentiment = sentimentAnalyzer.analyze(new SentimentAnalysisRequest(text));

          sentiments.put("Sentiment", String.valueOf(sentiment.getSentimentValue()));

        } catch (SentimentAnalysisException ex) {

          LOGGER.error("Unable to run sentiment analysis using analyzer: " + sentimentAnalyzer.getName(), ex);

        }

      }

      return sentiments;

  }

  /**
   * Gets the configuration.
   * @return The configuration.
   */
  protected T getConfiguration() {
    return configuration;
  }

}
