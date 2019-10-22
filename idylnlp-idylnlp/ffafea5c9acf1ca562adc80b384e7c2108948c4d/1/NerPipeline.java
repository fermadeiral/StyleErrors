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
package ai.idylnlp.pipeline;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.entity.Entity;

import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.ModelManifestUtils;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.DuplicateEntityStrategy;
import ai.idylnlp.model.nlp.EntityComparator;
import ai.idylnlp.model.nlp.EntityOrder;
import ai.idylnlp.model.nlp.EntitySanitizer;
import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;
import ai.idylnlp.model.nlp.pipeline.Pipeline;
import ai.idylnlp.model.stats.StatsReporter;
import ai.idylnlp.nlp.recognizer.OpenNLPEntityRecognizer;
import ai.idylnlp.nlp.recognizer.configuration.OpenNLPEntityRecognizerConfiguration;
import ai.idylnlp.nlp.sentence.BreakIteratorSentenceDetector;
import ai.idylnlp.nlp.tokenizers.BreakIteratorTokenizer;
import ai.idylnlp.opennlp.custom.modelloader.LocalModelLoader;
import ai.idylnlp.opennlp.custom.modelloader.ModelLoader;
import ai.idylnlp.opennlp.custom.validators.TrueModelValidator;
import ai.idylnlp.zoo.IdylNLPModelZoo;
import opennlp.tools.namefind.TokenNameFinderModel;

/**
 * An NLP pipeline for named-entity recognition (NER). The pipeline performs
 * all required operations for extracting entities from natural language text.
 * An implementation of {@link Pipeline}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class NerPipeline implements Pipeline<EntityExtractionResponse> {

  private static final Logger LOGGER = LogManager.getLogger(NerPipeline.class);

  private SentenceDetector sentenceDetector;
  private Tokenizer tokenizer;
  private List<EntityRecognizer> entityRecognizers;
  private List<EntitySanitizer> entitySanitizers;
  private StatsReporter statsReporter;
  private DuplicateEntityStrategy duplicateEntityStrategy;
  private LanguageCode languageCode;
  private EntityOrder entityOrder;
  private IdylNLPModelZoo zoo;
  private Set<String> entityTypes;

  private NerPipeline(
      SentenceDetector sentenceDetector,
      Tokenizer tokenizer,
      List<EntityRecognizer> entityRecognizers,
      List<EntitySanitizer> entitySanitizers,
      StatsReporter statsReporter,
      DuplicateEntityStrategy duplicateEntityStrategy,
      LanguageCode languageCode,
      EntityOrder entityOrder,
      IdylNLPModelZoo zoo,
      Set<String> entityTypes) {

    this.sentenceDetector = sentenceDetector;
    this.tokenizer = tokenizer;
    this.entityRecognizers = entityRecognizers;
    this.entitySanitizers = entitySanitizers;
    this.statsReporter = statsReporter;
    this.duplicateEntityStrategy = duplicateEntityStrategy;
    this.languageCode = languageCode;
    this.entityOrder = entityOrder;
    this.zoo = zoo;
    this.entityTypes = entityTypes;

  }

  /**
   * Facilitates the construction of an {@link NerPipeline}.
   *
   * @author Mountain Fog, Inc.
   *
   */
  public static class NerPipelineBuilder {

    private SentenceDetector sentenceDetector;
    private Tokenizer tokenizer;
    private List<EntityRecognizer> entityRecognizers;
    private List<EntitySanitizer> entitySanitizers;
    private StatsReporter statsReporter;
    private DuplicateEntityStrategy duplicateEntityStrategy = DuplicateEntityStrategy.USE_HIGHEST_CONFIDENCE;
    private EntityOrder entityOrder = EntityOrder.CONFIDENCE;
    private IdylNLPModelZoo zoo;
    private Set<String> entityTypes;

    /**
     * Sets the {@link SentenceDetector} for the pipeline.
     * @param sentenceDetector The {@link SentenceDetector} for the pipeline.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withSentenceDetector(SentenceDetector sentenceDetector) {
      this.sentenceDetector = sentenceDetector;
      return this;
    }

    /**
     * Sets the {@link Tokenizer} for the pipeline.
     * @param tokenizer The {@link Tokenizer} for the pipeline.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withTokenizer(Tokenizer tokenizer) {
      this.tokenizer = tokenizer;
      return this;
    }

    /**
     * Sets the entity recognizers for the pipeline.
     * @param entityRecognizers The entity {@link EntityRecognizer recognizers}.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withEntityRecognizers(List<EntityRecognizer> entityRecognizers) {
      this.entityRecognizers = entityRecognizers;
      return this;
    }

    /**
     * Sets the entity sanitizers for the pipeline.
     * @param entitySanitizers The entity {@link EntitySanitzer sanitzers}.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withEntitySanitizers(List<EntitySanitizer> entitySanitizers) {
      this.entitySanitizers = entitySanitizers;
      return this;
    }

    /**
     * Sets the {@link StatsReporter}.
     * @param statsReporter The {@link StatsReporter}.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withStatsReporter(StatsReporter statsReporter) {
      this.statsReporter = statsReporter;
      return this;
    }

    /**
     * Sets the duplicate entity strategy for the pipeline.
     * @param duplicateEntityStrategy The duplicate entity {@link DuplicateEntityStrategy strategy}.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withDuplicateEntityStrategy(DuplicateEntityStrategy duplicateEntityStrategy) {
      this.duplicateEntityStrategy = duplicateEntityStrategy;
      return this;
    }

    /**
     * Sets the return order for extracted entities.
     * @param entityOrder The return {@link EntityOrder} for extracted entities.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withEntityOrder(EntityOrder entityOrder) {
      this.entityOrder = entityOrder;
      return this;
    }

    /**
     * Sets the model zoo client.
     * @param zoo A {@link IdylNLPModelZoo}.
     * @return The {@link NerPipeline pipeline} so calls can be chained.
     */
    public NerPipelineBuilder withIdylNLPModelZoo(IdylNLPModelZoo zoo) {
      this.zoo = zoo;
      return this;
    }

    /**
     * Sets the entity types to extract.
     * @param entityTypes The entity types to extract.
     * @return The entity types to extract.
     */
    public NerPipelineBuilder withEntityTypes(Set<String> entityTypes) {
      this.entityTypes = entityTypes;
      return this;
    }

    /**
     * Builds the pipeline.
     * @return A {@link NerPipeline pipeline}.
     */
    public NerPipeline build(LanguageCode languageCode) {

      if(sentenceDetector == null) {
        // Get a default sentence detector for the given language.
        sentenceDetector = new BreakIteratorSentenceDetector(languageCode);
      }

      if(tokenizer == null) {
        // Get a default tokenizer for the given language.
        tokenizer = new BreakIteratorTokenizer(languageCode);
      }

      if(entityRecognizers == null) {

        // Get a default entity recognizer for the given language.

        final File file = new File(NerPipeline.class.getResource("/models/" + languageCode.getAlpha3().toString().toLowerCase() + "/").getFile());
        final String modelDirectory = file.getAbsolutePath();

        LOGGER.info("Using model directory {}", modelDirectory);

        // TODO: Let the validator be passed in.
        final ModelValidator modelValidator = new TrueModelValidator();

        final ModelLoader<TokenNameFinderModel> modelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, modelDirectory);
        final List<ModelManifest> modelManifests = ModelManifestUtils.getModelManifests(modelDirectory);
        final Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<>();

        for(ModelManifest modelManifest : modelManifests) {

          Set<StandardModelManifest> englishModelManifests = new HashSet<StandardModelManifest>();
          englishModelManifests.add((StandardModelManifest) modelManifest);

          Map<LanguageCode, Set<StandardModelManifest>> languagesToManifests = new HashMap<>();
          languagesToManifests.put(languageCode, englishModelManifests);

          models.put(modelManifest.getType(), languagesToManifests);

        }

        OpenNLPEntityRecognizerConfiguration config = new OpenNLPEntityRecognizerConfiguration.Builder()
            .withEntityModelLoader(modelLoader)
            .withEntityModels(models)
            .build();

        OpenNLPEntityRecognizer entityRecognizer = new OpenNLPEntityRecognizer(config);

        entityRecognizers = new ArrayList<EntityRecognizer>();
        entityRecognizers.add(entityRecognizer);

      }

      if(entitySanitizers == null) {
        entitySanitizers = new ArrayList<EntitySanitizer>();
      }

      if(entityTypes == null) {

        // All entity types.
        entityTypes = new HashSet<>();

      }

      NerPipeline pipeline = new NerPipeline(
          sentenceDetector, tokenizer, entityRecognizers, entitySanitizers,
          statsReporter, duplicateEntityStrategy, languageCode, entityOrder, zoo, entityTypes);

      return pipeline;

    }

  }

  @Override
  public EntityExtractionResponse run(String text) {

    Set<Entity> entities = new HashSet<Entity>();

    boolean successful = true;

    long extractionTime = 0;

    try {

      final String[] sentences = sentenceDetector.sentDetect(text);

      for(String sentence : sentences) {

        final String[] tokens = tokenizer.tokenize(sentence);

        // Extract the entities using all of the NERs in the list.
        for(EntityRecognizer entityRecognizer : entityRecognizers) {

          LOGGER.debug("Processing tokenized text with entity recognizer {}.", entityRecognizer.toString());

          EntityExtractionRequest request = new EntityExtractionRequest(tokens);
          request.setDuplicateEntityStrategy(duplicateEntityStrategy);
          // TODO: Expose other parameters of the EntityExtractionRequest such as entity confidence.

          EntityExtractionResponse response = entityRecognizer.extractEntities(request);
          entities.addAll(response.getEntities());
          extractionTime += response.getExtractionTime();

        }

        if(statsReporter != null) {
          // Increment the count of entity extraction requests.
          statsReporter.increment(StatsReporter.EXTRACTION_REQUESTS, entities.size());
        }

        // Sanitize the entities.
        for(EntitySanitizer sanitizer : entitySanitizers) {
          entities = sanitizer.sanitizeEntities(entities);
        }

        // Handle the duplicate entities per the strategy.
        if(duplicateEntityStrategy == DuplicateEntityStrategy.USE_HIGHEST_CONFIDENCE) {

          // Remove duplicate entities having a lower confidence.
          entities = removeDuplicateEntities(entities);

        }

        // Sort the entities before returning.
        entities = EntityComparator.sort(entities, entityOrder);

      }

    } catch (ModelLoaderException | EntityFinderException ex) {

      LOGGER.error("Unable to process through the Idyl pipeline.", ex);

      // Return null on receipt of an error. This is here
      // because otherwise an incomplete list of
      // entities could potentially be returned when an
      // exception is thrown.

      entities = null;

      successful = false;

    }

    return new EntityExtractionResponse(entities, extractionTime, successful);

  }

  /**
   * Remove duplicate entities having a lower confidence.
   * @param entities A set of {@link Entity} objects.
   * @return A set of {@link Entity} objects without duplicate entities.
   */
  public static Set<Entity> removeDuplicateEntities(Set<Entity> entities) {

    Set<Entity> removedDuplicateEntities = new LinkedHashSet<>();

    for(Entity entity : entities) {

      Set<Entity> entitiesWithSameText = new HashSet<Entity>();

      // Is there another entity in the set that has this entity's text?
      for(Entity entity2 : entities) {

        if(entity.getText().equalsIgnoreCase(entity2.getText())) {

          entitiesWithSameText.add(entity2);

        }

      }

      // Should always be at least one (the same entity).
      if(entitiesWithSameText.size() == 1) {

        removedDuplicateEntities.addAll(entitiesWithSameText);

      } else {

        // Find the one with the highest confidence.
        double highestConfidence = 0;
        Entity entityWithHighestConfidence = null;

        for(Entity entity3 : entitiesWithSameText) {

          if(entity3.getConfidence() > highestConfidence) {

            highestConfidence = entity3.getConfidence();
            entityWithHighestConfidence = entity3;

          }

        }

        removedDuplicateEntities.add(entityWithHighestConfidence);

      }

    }

    return removedDuplicateEntities;

  }


  /**
   * Gets the sentence detector used by the pipeline.
   * @return The {@link SentenceDetector} used by the pipeline.
   */
  public SentenceDetector getSentenceDetector() {
    return sentenceDetector;
  }

  /**
   * Gets the tokenizer used by the pipeline.
   * @return The {@link Tokenizer} used by the pipeline.
   */
  public Tokenizer getTokenizer() {
    return tokenizer;
  }

  /**
   * Gets the entity recognizers.
   * @return A list of entity {@link EntityRecognizer recognizers}.
   */
  public List<EntityRecognizer> getEntityRecognizers() {
    return entityRecognizers;
  }

  /**
   * Gets the entity sanitizers.
   * @return A list of entity {@link EntitySanizer sanitizers}.
   */
  public List<EntitySanitizer> getEntitySanitiziers() {
    return entitySanitizers;
  }

  /**
   * Gets the {@link StatsReporter}.
   * @return The {@link StatsReporter}.
   */
  public StatsReporter getStatsReporter() {
    return statsReporter;
  }

  /**
   * Gets the duplicate entity strategy.
   * @return The duplicate entity {@link DuplicateEntityStrategy strategy}.
   */
  public DuplicateEntityStrategy getDuplicateEntityStrategy() {
    return duplicateEntityStrategy;
  }

  /**
   * Gets the language code for the pipeline.
   * @return The {@link LanguageCode} for the pipeline.
   */
  public LanguageCode getLanguageCode() {
    return languageCode;
  }

  /**
   * Gets the entity order for the pipeline.
   * @return The {@link EntityOrder} for the pipeline.
   */
  public EntityOrder getEntityOrder() {
    return entityOrder;
  }

  /**
   * Gets the model zoo client for the pipeline.
   * @return The {@link IdylNLPModelZoo} client for the pipeline.
   */
  public IdylNLPModelZoo getZoo() {
    return zoo;
  }

  /**
   * Gets the entity types for the pipeline.
   * @return The entity types for the pipeline.
   */
  public Set<String> getEntityTypes() {
    return entityTypes;
  }

}
