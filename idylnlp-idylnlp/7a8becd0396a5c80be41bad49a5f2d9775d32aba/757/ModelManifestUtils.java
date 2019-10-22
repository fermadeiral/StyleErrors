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
package ai.idylnlp.model.manifest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neovisionaries.i18n.LanguageCode;

/**
 * Utility methods for working with model manifest files.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class ModelManifestUtils {

  private static final Logger LOGGER = LogManager.getLogger(ModelManifestUtils.class);

  public static final String MANIFEST_MODEL_ID = "model.id";
  public static final String MANIFEST_MODEL_NAME = "model.name";
  public static final String MANIFEST_MODEL_TYPE = "model.type";
  public static final String MANIFEST_MODEL_SUBTYPE = "model.subtype";
  public static final String MANIFEST_MODEL_FILENAME = "model.filename";
  public static final String MANIFEST_LANGUAGE_CODE = "language.code";
  public static final String MANIFEST_ENCRYPTION_KEY = "encryption.key";
  public static final String MANIFEST_CREATOR_VERSION = "creator.version";
  public static final String MANIFEST_MODEL_SOURCE = "model.source";
  public static final String MANIFEST_VECTORS_SOURCE = "vectors.source";
  public static final String MANIFEST_BEAM_SIZE = "beam.size";
  public static final String MANIFEST_GENERATION = "generation";

  // Used for second generation NER model manifests.
  public static final String MANIFEST_VECTORS_FILENAME = "vectors.file";
  public static final String MANIFEST_WINDOW_SIZE = "window.size";

  /**
   * Generates a model manifest file. If the manifest file already exists it will be overwritten.
   * @param manifestFile The {@link File} to write.
   * @param modelManifest The {@link StandardModelManifest}.
   * @throws IOException Thrown if an existing file cannot be deleted or if the manifest file cannot be written.
   */
  public static void generateStandardModelManifest(StandardModelManifest modelManifest, File manifestFile) throws IOException {

    // If the manifest file already exists delete it.
    if(manifestFile.exists()) {

      LOGGER.info("Removing existing manifest file {}.", manifestFile.getAbsolutePath());
      manifestFile.delete();

    }

    List<String> lines = new LinkedList<String>();
    lines.add(MANIFEST_MODEL_ID + "=" + modelManifest.getModelId());
    lines.add(MANIFEST_MODEL_NAME + "=" + modelManifest.getName());
    lines.add(MANIFEST_MODEL_TYPE + "=" + modelManifest.getType().toLowerCase());
    lines.add(MANIFEST_MODEL_SUBTYPE + "=" + modelManifest.getSubtype().toLowerCase());
    lines.add(MANIFEST_MODEL_FILENAME + "=" + modelManifest.getModelFileName());
    lines.add(MANIFEST_LANGUAGE_CODE + "=" + modelManifest.getLanguageCode());
    lines.add(MANIFEST_ENCRYPTION_KEY + "=" + modelManifest.getEncryptionKey());
    lines.add(MANIFEST_CREATOR_VERSION + "=" + modelManifest.getCreatorVersion());
    lines.add(MANIFEST_MODEL_SOURCE + "=" + modelManifest.getSource());
    lines.add(MANIFEST_GENERATION + "=" + 1);

    // Only write the beam size to the manifest if it is not the default.
    if(modelManifest.getBeamSize() != StandardModelManifest.DEFAULT_BEAM_SIZE) {
      lines.add(MANIFEST_BEAM_SIZE + "=" + modelManifest.getBeamSize());
    }

    FileUtils.writeLines(manifestFile, lines);

  }

  /**
   * Generates a model manifest file. If the manifest file already exists it will be overwritten.
   * This function is used by the user-generated models tool.
   * @param manifestFile The {@link File} to write.
   * @param modelId The model ID.
   * @param name The name of the model. This is not a file name.
   * @param type The entity class or "sentence" or "token".
   * @param subtype The model subtype (dictionary).
   * @param modelFile The filename of the model without any path.
   * @param languageCode The {@link LanguageCode}.
   * @param encryptionKey The model's encryption key.
   * @param creatorVersion The version of the implementing application.
   * @param source A URL where the model can be downloaded.
   * @throws IOException Thrown if an existing file cannot be deleted or if the manifest file cannot be written.
   */
  public static void generateStandardModelManifest(File manifestFile, String modelId, String name, String type, String subtype, String modelFile, LanguageCode languageCode, String encryptionKey, String creatorVersion, String source) throws IOException {

    // If the manifest file already exists delete it.
    if(manifestFile.exists()) {

      LOGGER.info("Removing existing manifest file {}.", manifestFile.getAbsolutePath());
      manifestFile.delete();

    }

    List<String> lines = new LinkedList<String>();
    lines.add(MANIFEST_MODEL_ID + "=" + modelId);
    lines.add(MANIFEST_MODEL_NAME + "=" + name);
    lines.add(MANIFEST_MODEL_TYPE + "=" + type.toLowerCase());
    lines.add(MANIFEST_MODEL_SUBTYPE + "=" + subtype.toLowerCase());
    lines.add(MANIFEST_MODEL_FILENAME + "=" + modelFile);
    lines.add(MANIFEST_LANGUAGE_CODE + "=" + languageCode.getAlpha3().toString().toLowerCase());
    lines.add(MANIFEST_ENCRYPTION_KEY + "=" + encryptionKey);
    lines.add(MANIFEST_CREATOR_VERSION + "=" + creatorVersion);
    lines.add(MANIFEST_MODEL_SOURCE + "=" + source);
    lines.add(MANIFEST_GENERATION + "=" + ModelManifest.FIRST_GENERATION);

    // The beam size is assumed to be the default so it is not written to the manifest.

    FileUtils.writeLines(manifestFile, lines);

  }

  /**
   * Generates a model manifest for a second generation model.
   * @param manifestFile The {@link File} to write.
   * @param modelManifest The {@link SecondGenModelManifest manifest} to write.
   * @throws IOException Thrown if an existing file cannot be deleted or if the manifest file cannot be written.
   */
  public static void generateSecondGenModelManifest(File manifestFile, SecondGenModelManifest modelManifest) throws IOException {

    // If the manifest file already exists delete it.
    if(manifestFile.exists()) {

      LOGGER.info("Removing existing manifest file {}.", manifestFile.getAbsolutePath());
      manifestFile.delete();

    }

    List<String> lines = new LinkedList<String>();
    lines.add(MANIFEST_MODEL_ID + "=" + modelManifest.getModelId());
    lines.add(MANIFEST_MODEL_FILENAME + "=" + modelManifest.getModelFileName());
    lines.add(MANIFEST_MODEL_NAME + "=" + modelManifest.getName());
    lines.add(MANIFEST_MODEL_TYPE + "=" + modelManifest.getType().toLowerCase());
    lines.add(MANIFEST_LANGUAGE_CODE + "=" + modelManifest.getLanguageCode());
    lines.add(MANIFEST_CREATOR_VERSION + "=" + modelManifest.getCreatorVersion());
    lines.add(MANIFEST_VECTORS_FILENAME + "=" + modelManifest.getVectorsFileName());
    lines.add(MANIFEST_WINDOW_SIZE + "=" + modelManifest.getWindowSize());
    lines.add(MANIFEST_MODEL_SOURCE + "=" + modelManifest.getSource());
    lines.add(MANIFEST_VECTORS_SOURCE + "=" + modelManifest.getVectorsSource());
    lines.add(MANIFEST_GENERATION + "=" + ModelManifest.SECOND_GENERATION);

    FileUtils.writeLines(manifestFile, lines);

  }

  /**
   * Get the {@link ModelManifest manifests} for NER models.
   * @param modelsDirectory The directory containing the model manifests.
   * @param type The type of model manifests to look for.
   * @return A list of {@link StandardModelManifest manifests}.
   */
  public static List<ModelManifest> getModelManifests(String modelsDirectory, String type) {

    final List<ModelManifest> modelManifests = new LinkedList<ModelManifest>();

    final String[] extensions = {"manifest"};
    final File file = new File(modelsDirectory);

    if(!file.exists() || !file.isDirectory()) {

      LOGGER.warn("Model directory {} is not a valid directory.", modelsDirectory);
      return Collections.emptyList();

    }

    final Collection<File> modelManifestFiles = FileUtils.listFiles(file, extensions, true);

    for(File modelManifestFile : modelManifestFiles) {

      final String fullManifestPath = modelsDirectory + File.separator + modelManifestFile.getName();

      LOGGER.info("Found model manifest {}.", fullManifestPath);

      try {

        final ModelManifest modelManifest = readManifest(fullManifestPath);

        if(modelManifest != null) {

          // Return all models or only return certain types.
          if(StringUtils.isEmpty(type) || StringUtils.equalsIgnoreCase(type, modelManifest.getType())) {

            LOGGER.info("Entity Class: {}, Model File Name: {}, Language Code: {}",
                modelManifest.getType(), modelManifest.getModelFileName(), modelManifest.getLanguageCode());

            modelManifests.add(modelManifest);

          }

        }

      } catch (Exception ex) {

        LOGGER.error("Unable to read model manifest: " + fullManifestPath, ex);

      }

    }

    return modelManifests;

  }

  /**
   * Get the {@link ModelManifest manifests} for NER models.
   * @param modelsDirectory The directory containing the model manifests.
   * @return A list of {@link StandardModelManifest manifests}.
   */
  public static List<ModelManifest> getModelManifests(String modelsDirectory) {

    return getModelManifests(modelsDirectory, null);

  }

  /**
   * Reads a model manifest.
   * @param fullManifestPath The full path to the model manifest.
   * @return A {@link StandardModelManifest manifest}, or <code>null</code> if the
   * model manifest is not properly configured or is not a NER model manifest.
   * @throws Exception Thrown if the model manifest cannot be read.
   */
  public static ModelManifest readManifest(String fullManifestPath) throws Exception {

    LOGGER.info("Validating model manifest {}.", fullManifestPath);

    final InputStream inputStream = new FileInputStream(fullManifestPath);
    final Properties properties = new Properties();
      properties.load(inputStream);

      // Get the model generation from the manifest.
      int generation = Integer.valueOf(properties.getProperty(MANIFEST_GENERATION, String.valueOf(ModelManifest.FIRST_GENERATION)));

      // Get properties common to all generations.
      String modelId = properties.getProperty(MANIFEST_MODEL_ID);
      String name = properties.getProperty(MANIFEST_MODEL_NAME);
      String modelFileName = properties.getProperty(MANIFEST_MODEL_FILENAME);
      String languageCode = properties.getProperty(MANIFEST_LANGUAGE_CODE);
      String modelType = properties.getProperty(MANIFEST_MODEL_TYPE);
      String creatorVersion = properties.getProperty(MANIFEST_CREATOR_VERSION);
      String source = properties.getProperty(MANIFEST_MODEL_SOURCE);

      // Validate the property values.
      if(StringUtils.isEmpty(modelId)) {
        LOGGER.warn("The model.id in {} is missing.", fullManifestPath);
        return null;
      }
      if(StringUtils.isEmpty(modelType)) {
        LOGGER.warn("The model.type in {} is missing.", fullManifestPath);
        return null;
      }
      if(StringUtils.isEmpty(modelFileName)) {
        LOGGER.warn("The model.filename in {} is missing.", fullManifestPath);
        return null;
      }
      if(StringUtils.isEmpty(languageCode)) {
        LOGGER.warn("The language.code in {} is missing.", fullManifestPath);
        return null;
      }
      if(StringUtils.isEmpty(creatorVersion)) {
        LOGGER.warn("The creator.version in {} is missing.", fullManifestPath);
        // No reason to stop model loading here.
      }
      if(StringUtils.isEmpty(name)) {
        // If there is no name just use the modelId as the name.
        name = modelId;
      }

      // Get the LanguageCode for the language.
     final LanguageCode code = LanguageCode.getByCodeIgnoreCase(languageCode);

      if(generation == ModelManifest.FIRST_GENERATION) {

        // Read the contents of the manifest.
        final String modelSubtype = properties.getProperty(MANIFEST_MODEL_SUBTYPE, StandardModelManifest.DEFAULT_SUBTYPE);
        final String encryptionKey = properties.getProperty(MANIFEST_ENCRYPTION_KEY);

        int beamSize = Integer.valueOf(properties.getProperty(MANIFEST_BEAM_SIZE, String.valueOf(StandardModelManifest.DEFAULT_BEAM_SIZE)));

        inputStream.close();

        if(!StringUtils.isEmpty(modelType)) {
          modelType = modelType.toLowerCase();
        }

        // Validate properties specific to a standard model manifest.
        // All properties except encryption.key are required. Make sure each one is present.

        if(beamSize <= 0) {
          LOGGER.warn("The beam size value of {} in {} is invalid.", beamSize, fullManifestPath);
          return null;
        }

        return new StandardModelManifest.ModelManifestBuilder(modelId, name, modelFileName, code, encryptionKey, modelType, modelSubtype, creatorVersion, source, beamSize).build();

      } else if(generation == ModelManifest.SECOND_GENERATION) {

        // Is it a document model?
        if(StringUtils.equalsIgnoreCase(modelType, DocumentModelManifest.TYPE)) {

          final String modelSubtype = properties.getProperty(MANIFEST_MODEL_SUBTYPE, StandardModelManifest.DEFAULT_SUBTYPE);
          final List<String> labels = Arrays.asList(modelSubtype.split(","));

          return new DocumentModelManifest(modelId, modelFileName, code, modelType, name, creatorVersion, source, labels);

        } else {

          final String vectorsFile = properties.getProperty(MANIFEST_VECTORS_FILENAME);
          final String vectorsSource = properties.getProperty(MANIFEST_VECTORS_SOURCE);
          final int windowSize = Integer.valueOf(properties.getProperty(MANIFEST_WINDOW_SIZE, "5"));

          LOGGER.info("Initializing second generation model manifest with widow size of {}.", windowSize);

          // Validate properties specific to a second generation model manifest.

          if(StringUtils.isEmpty(vectorsFile)) {
            LOGGER.warn("The vectors.file in {} is invalid.", fullManifestPath);
            return null;
          }

          return new SecondGenModelManifest(modelId, modelFileName, code, modelType, name, creatorVersion, vectorsFile, windowSize, source, vectorsSource);

        }

      } else {

        throw new IllegalArgumentException("Invalid model generation value of: " + generation);

      }

  }

}
