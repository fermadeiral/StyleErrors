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
package ai.idylnlp.test.nlp.recognizer;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import com.google.gson.Gson;
import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.opennlp.custom.modelloader.LocalModelLoader;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.exceptions.ValidationException;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.AbstractEntityRecognizer;
import ai.idylnlp.model.nlp.ConfidenceFilter;
import ai.idylnlp.model.nlp.language.LanguageDetectionException;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.nlp.filters.confidence.HeuristicConfidenceFilter;
import ai.idylnlp.nlp.filters.confidence.serializers.LocalConfidenceFilterSerializer;
import ai.idylnlp.nlp.recognizer.OpenNLPEntityRecognizer;
import ai.idylnlp.nlp.recognizer.configuration.OpenNLPEntityRecognizerConfiguration;
import ai.idylnlp.nlp.recognizer.configuration.OpenNLPEntityRecognizerConfiguration.Builder;
import opennlp.tools.namefind.TokenNameFinderModel;

public class OpenNLPEntityRecognizerTest {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPEntityRecognizerTest.class);

  private static final String MODEL_PATH = new File("src/test/resources/models/").getAbsolutePath();
  private static final String MTNFOG_EN_PERSON_MODEL = "mtnfog-en-person-test.bin";
  private static final String MTNFOG_DE_PERSON_MODEL = "mtnfog-de-person-test.bin";

  private static final LocalConfidenceFilterSerializer serializer = new LocalConfidenceFilterSerializer();
  private static final ConfidenceFilter confidenceFilter = new HeuristicConfidenceFilter(serializer);

  @Test
  public void extract() throws EntityFinderException, ModelLoaderException, LanguageDetectionException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    // Adding two language models here but should only get an English entity back.

    StandardModelManifest englishModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_EN_PERSON_MODEL, LanguageCode.en, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();
    StandardModelManifest germanModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_DE_PERSON_MODEL, LanguageCode.de, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> englishModelManifests = new HashSet<StandardModelManifest>();
    englishModelManifests.add(englishModelManifest);

    Set<StandardModelManifest> germanModelManifests = new HashSet<StandardModelManifest>();
    germanModelManifests.add(germanModelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<>();

    Map<LanguageCode, Set<StandardModelManifest>> languagesToManifests = new HashMap<>();
    languagesToManifests.put(LanguageCode.en, englishModelManifests);
    languagesToManifests.put(LanguageCode.de, germanModelManifests);

    models.put("person", languagesToManifests);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    final String input = "George Washington was president.";
    final String[] text = input.split(" ");

    // EntityExtractionRequest defaults to English if not explicity set.
    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();
    assertEquals("[0..2)", entity.getSpan().toString());
    assertEquals(LanguageCode.en.getAlpha3().toString(), entity.getLanguageCode());
    assertEquals(englishModelManifest.getModelFileName(), entity.getMetadata().get(AbstractEntityRecognizer.METADATA_MODEL_FILENAME_KEY));

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void extractPersonAndPlace() throws EntityFinderException, ModelLoaderException, LanguageDetectionException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    StandardModelManifest englishModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_EN_PERSON_MODEL, LanguageCode.en, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    // Not really a place model but we just want to make sure both person and place models are looked at.
    StandardModelManifest germanModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_DE_PERSON_MODEL, LanguageCode.de, "idylami589012347", "place", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> englishModelManifests = new HashSet<StandardModelManifest>();
    englishModelManifests.add(englishModelManifest);

    Set<StandardModelManifest> germanModelManifests = new HashSet<StandardModelManifest>();
    germanModelManifests.add(germanModelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<>();

    Map<LanguageCode, Set<StandardModelManifest>> personLanguagesToManifests = new HashMap<>();
    personLanguagesToManifests.put(LanguageCode.en, englishModelManifests);
    models.put("person", personLanguagesToManifests);

    Map<LanguageCode, Set<StandardModelManifest>> placeLanguagesToManifests = new HashMap<>();
    placeLanguagesToManifests.put(LanguageCode.de, germanModelManifests);
    models.put("place", placeLanguagesToManifests);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    final String input = "George Washington was president.";
    final String[] text = input.split(" ");

    // EntityExtractionRequest defaults to English if not explicity set.
    EntityExtractionRequest request = new EntityExtractionRequest(text);
    request.setType("person,place");

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();
    assertEquals("[0..2)", entity.getSpan().toString());
    assertEquals(LanguageCode.en.getAlpha3().toString(), entity.getLanguageCode());
    assertEquals(englishModelManifest.getModelFileName(), entity.getMetadata().get(AbstractEntityRecognizer.METADATA_MODEL_FILENAME_KEY));

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void extractNullContext() throws EntityFinderException, ModelLoaderException, LanguageDetectionException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    // Adding two language models here but should only get an English entity back.

    StandardModelManifest englishModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_EN_PERSON_MODEL, LanguageCode.en, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> englishModelManifests = new HashSet<StandardModelManifest>();
    englishModelManifests.add(englishModelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<>();

    Map<LanguageCode, Set<StandardModelManifest>> languagesToManifests = new HashMap<>();
    languagesToManifests.put(LanguageCode.en, englishModelManifests);

    models.put("person", languagesToManifests);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    final String input = "George Washington was president.";
    final String[] text = input.split(" ");

    // EntityExtractionRequest defaults to English if not explicity set.
    EntityExtractionRequest request = new EntityExtractionRequest(text);
    request.setContext(null);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(1, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();
    assertEquals("[0..2)", entity.getSpan().toString());
    assertEquals(LanguageCode.en.getAlpha3().toString(), entity.getLanguageCode());
    assertEquals(englishModelManifest.getModelFileName(), entity.getMetadata().get(AbstractEntityRecognizer.METADATA_MODEL_FILENAME_KEY));

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test
  public void extractNoLanguage() throws EntityFinderException, ModelLoaderException, LanguageDetectionException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    // Adding two language models here but should only get an English entity back.

    StandardModelManifest englishModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_EN_PERSON_MODEL, LanguageCode.en, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> englishModelManifests = new HashSet<StandardModelManifest>();
    englishModelManifests.add(englishModelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<String, Map<LanguageCode, Set<StandardModelManifest>>>();

    Map<LanguageCode, Set<StandardModelManifest>> languagesToManifests = new HashMap<LanguageCode, Set<StandardModelManifest>>();
    languagesToManifests.put(LanguageCode.en, englishModelManifests);

    models.put("person", languagesToManifests);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    final String input = "George Washington was president.";
    final String[] text = input.split(" ");

    // EntityExtractionRequest defaults to English if not explicity set.
    EntityExtractionRequest request = new EntityExtractionRequest(text).withLanguage(LanguageCode.fr);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    assertEquals(0, response.getEntities().size());

  }

  @Test
  public void extractDuplicateEntities() throws EntityFinderException, ModelLoaderException, LanguageDetectionException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    // Adding two language models here but should only get an English entity back.

    StandardModelManifest englishModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_EN_PERSON_MODEL, LanguageCode.en, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();
    StandardModelManifest germanModelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_DE_PERSON_MODEL, LanguageCode.de, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> englishModelManifests = new HashSet<StandardModelManifest>();
    englishModelManifests.add(englishModelManifest);

    Set<StandardModelManifest> germanModelManifests = new HashSet<StandardModelManifest>();
    germanModelManifests.add(germanModelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<String, Map<LanguageCode, Set<StandardModelManifest>>>();

    Map<LanguageCode, Set<StandardModelManifest>> languagesToManifests = new HashMap<LanguageCode, Set<StandardModelManifest>>();
    languagesToManifests.put(LanguageCode.en, englishModelManifests);
    languagesToManifests.put(LanguageCode.de, germanModelManifests);

    models.put("person", languagesToManifests);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    final String input = "George Washington and George Washington were friends.";
    final String[] text = input.split(" ");

    // EntityExtractionRequest defaults to English if not explicitly set.
    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

    for(Entity entity : response.getEntities()) {

      LOGGER.info("Entity: " + entity.toString());

    }

    assertEquals(2, response.getEntities().size());

    Entity entity = response.getEntities().iterator().next();
    assertEquals("[0..2)", entity.getSpan().toString());
    assertEquals(LanguageCode.en.getAlpha3().toString(), entity.getLanguageCode());

    // Show the response as JSON.
    Gson gson = new Gson();
    String json = gson.toJson(response);

    LOGGER.info(json);

  }

  @Test(expected = IllegalArgumentException.class)
  public void extractWithEmptyText() throws EntityFinderException, ModelLoaderException, LanguageDetectionException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MTNFOG_EN_PERSON_MODEL, LanguageCode.en, "idylami589012347", "person", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> manifests = new HashSet<StandardModelManifest>();
    manifests.add(modelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<String, Map<LanguageCode, Set<StandardModelManifest>>>();

    Map<LanguageCode, Set<StandardModelManifest>> lang = new HashMap<LanguageCode, Set<StandardModelManifest>>();
    lang.put(LanguageCode.en, manifests);

    models.put("person", lang);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    String text[] = {};

    EntityExtractionRequest request = new EntityExtractionRequest(text);

    EntityExtractionResponse response = recognizer.extractEntities(request);

  }

  @Test(expected = IllegalArgumentException.class)
  public void extractBadConfidence() throws EntityFinderException, ModelLoaderException, LanguageDetectionException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name",  MTNFOG_EN_PERSON_MODEL, LanguageCode.en, "idylami589012347", "person", "", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> manifests = new HashSet<StandardModelManifest>();
    manifests.add(modelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<String, Map<LanguageCode, Set<StandardModelManifest>>>();

    Map<LanguageCode, Set<StandardModelManifest>> lang = new HashMap<LanguageCode, Set<StandardModelManifest>>();
    lang.put(LanguageCode.en, manifests);

    models.put("person", lang);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    final String input = "George Washington was president.";
    final String[] text = input.split(" ");

    EntityExtractionRequest request = new EntityExtractionRequest(text);
    request.setConfidenceThreshold(150);

    EntityExtractionResponse response = recognizer.extractEntities(request);

  }

  @Test
  public void extractModeFileNotExist() throws EntityFinderException, ModelLoaderException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", "not-exist", LanguageCode.en, "idylami589012347", "person", "", StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    Set<StandardModelManifest> manifests = new HashSet<StandardModelManifest>();
    manifests.add(modelManifest);

    LocalModelLoader<TokenNameFinderModel> entityModelLoader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, "f:\\invalidpath");

    Map<String, Map<LanguageCode, Set<StandardModelManifest>>> models = new HashMap<String, Map<LanguageCode, Set<StandardModelManifest>>>();

    Map<LanguageCode, Set<StandardModelManifest>> lang = new HashMap<LanguageCode, Set<StandardModelManifest>>();
    lang.put(LanguageCode.en, manifests);
    models.put("person", lang);

    OpenNLPEntityRecognizerConfiguration config = new Builder()
      .withEntityModelLoader(entityModelLoader)
      .withConfidenceFilter(confidenceFilter)
      .withEntityModels(models)
      .build();

    OpenNLPEntityRecognizer recognizer = new OpenNLPEntityRecognizer(config);

    assertEquals(1, config.getBlacklistedModelIDs().size());

  }

}
