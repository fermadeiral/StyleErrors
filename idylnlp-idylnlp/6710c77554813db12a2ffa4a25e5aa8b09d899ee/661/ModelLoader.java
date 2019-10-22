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
package ai.idylnlp.opennlp.custom.modelloader;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3URI;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.exceptions.ValidationException;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.opennlp.custom.encryption.OpenNLPEncryptionFactory;
import ai.idylnlp.opennlp.custom.model.DictionaryModel;
import ai.idylnlp.zoo.IdylNLPModelZoo;
import opennlp.tools.cmdline.namefind.TokenNameFinderModelLoader;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.model.BaseModel;

/**
 * Abstract superclass for model loaders. Extend this class to implement
 * custom model loaders to support different environment needs.
 * @author Mountain Fog, Inc.
 * @param <T> Class extending {@link BaseModel}.
 *
 */
public abstract class ModelLoader<T extends BaseModel> {

  private static final Logger LOGGER = LogManager.getLogger(ModelLoader.class);

  // This map holds models for all types - tokenizer, sentences, and entity models.
  private Map<StandardModelManifest, T> models = new HashMap<StandardModelManifest, T>();

  private String modelDirectory;

  private Class<T> typeParameterClass;

  private ModelValidator modelValidator;
  private IdylNLPModelZoo idylNlpModelZoo;

  public ModelLoader(ModelValidator modelValidator) {
    this.modelValidator = modelValidator;
  }

  /**
   * Gets the model.
   * @param modelManifest The model's {@link StandardModelManifest manifest}.
   * @param typeParameterClass The class of the model.
   * @return A class extending {@link BaseModel}.
   * @throws Exception An IOException is thrown if the model can not be loaded. This will happen
   * in cases where the model does not exist, the model is corrupted, or the model file can not
   * be read by the process.
   */
  public T getModel(StandardModelManifest modelManifest, Class<T> typeParameterClass) throws ModelLoaderException {

    this.typeParameterClass = typeParameterClass;

    T tnfm = null;

    if(models.get(modelManifest) != null) {

      // We have previously loaded this model.
      // Just get it and return it.
      tnfm = models.get(modelManifest);

    } else {

      LOGGER.debug("Model has not been loaded - going to load.");

      try {

        // We need to load this model first.
        tnfm = loadModel(modelManifest);

      } catch (Exception ex) {

        LOGGER.error("Unable to load model: " + modelManifest.getModelFileName(), ex);

        throw new ModelLoaderException("Unable to load model: " + modelManifest.getModelFileName(), ex);

      }

      // Null?
      if(tnfm == null) {

        throw new ModelLoaderException("Unable to load model: " + modelManifest.getModelFileName());

      } else {

        // Now put the model into the map.
        models.put(modelManifest, tnfm);

      }

    }

    return tnfm;

  }

  private T loadModel(StandardModelManifest modelManifest) throws Exception {

    final String fullModelFileName = modelDirectory + modelManifest.getModelFileName();

    LOGGER.debug("Loading model from: " + fullModelFileName);

    // Does this file exist?
    final File modelFile = new File(fullModelFileName);

    if(!modelFile.exists()) {

      if(idylNlpModelZoo != null) {

        LOGGER.info("Attempting to download model {} from the Idyl NLP zoo.", modelManifest.getModelId());

        // Try to download the model from the zoo.
        idylNlpModelZoo.downloadModel(modelManifest.getModelId(), modelFile);

      } else if(fullModelFileName.startsWith("s3://")) {
    	  
    	  LOGGER.info("Downloading model from S3 {}", fullModelFileName);
    	  
    	  AmazonS3URI s3Uri = new AmazonS3URI(fullModelFileName);
    	  
    	  AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                  .withRegion(s3Uri.getRegion())
                  .withCredentials(new ProfileCredentialsProvider())
                  .build();
    	  
    	  GetObjectRequest request = new GetObjectRequest(s3Uri.getBucket(), s3Uri.getKey());
    	  S3Object object = s3Client.getObject(request);
    	  S3ObjectInputStream objectContent = object.getObjectContent();
    	  IOUtils.copy(objectContent, new FileOutputStream(modelFile.getAbsolutePath()));
    			  
      } else if(fullModelFileName.startsWith("http://") || fullModelFileName.startsWith("https://")) {
    	  
    	  LOGGER.info("Downloading model from web {}", fullModelFileName);
    	  FileUtils.copyURLToFile(new URL(fullModelFileName), modelFile, 60 * 1000, 60 * 1000);
    	  
      }

    }

    // Model loading will always take a decent amount of time
    // so milliseconds is a decent measure here.
    long startTime = System.currentTimeMillis();

    T tnfm = loadModelFromDisk(modelManifest, modelFile);

    long endTime = System.currentTimeMillis();
    long duration = endTime - startTime;

    LOGGER.debug("Model(s) loaded in " + duration + " milliseconds.");

    return tnfm;

  }

  /**
   * Load the model from the given path.
   * @param modelFilePath The file path to the model.
   * @return The TokenNameFinderModel of the model.
   */
  @SuppressWarnings("unchecked")
  private T loadModelFromDisk(StandardModelManifest modelManifest, File modelFile) throws Exception {

    LOGGER.debug("Loading model from disk: " + modelFile.getAbsolutePath());

    OpenNLPEncryptionFactory.getDefault().setKey(modelManifest.getEncryptionKey());

    T model = null;

    // Load the model into memory based on the type.

    if(typeParameterClass.isAssignableFrom(TokenNameFinderModel.class)) {

      // Load a token name finder model.
      model = (T) new TokenNameFinderModelLoader().load(modelFile);

    } else if(typeParameterClass.isAssignableFrom(SentenceModel.class)) {

      // Load a sentence model.
      model = (T) new SentenceModel(modelFile);

    } else if(typeParameterClass.isAssignableFrom(TokenizerModel.class)) {

      // Load a tokenizer model.
      model = (T) new TokenizerModel(modelFile);

    } else if(typeParameterClass.isAssignableFrom(POSModel.class)) {

      // Load a part-of-speech model.
      model = (T) new POSModel(modelFile);

    } else if(typeParameterClass.isAssignableFrom(LemmatizerModel.class)) {

      // Load a lemmatizer model.
      model = (T) new LemmatizerModel(modelFile);

    } else if(typeParameterClass.isAssignableFrom(DictionaryModel.class)) {

      // Load a dictionary model.
      model = (T) new DictionaryModel(modelManifest, this.getModelDirectory());

    } else {

      LOGGER.warn("Invalid class of model: {}", typeParameterClass.toString());

    }

    try {

      // Make sure the model.id in the model matches the model.id in the manifest.
      if(StringUtils.equals(model.getModelId(), modelManifest.getModelId())) {

        if(modelValidator != null) {

          // Validate the model.
          if(!modelValidator.validate(modelManifest)) {

            LOGGER.warn("Version verification failed.");

            // Since version validation failed we will set the model to null.
            model = null;

          }

        } else {

          // Even though the validator is null validation is allowed to be successful.
          LOGGER.warn("The model validator was null.");

        }

      } else {

        LOGGER.warn("The model manifest for model {} is not valid.", modelManifest.getModelFileName());

      }

    } catch (ValidationException ex) {

      LOGGER.error("Idyl NLP license key validation failed loading model.", ex);
      model = null;

    }

    OpenNLPEncryptionFactory.getDefault().clearKey();

    return model;

  }

  /**
   * Gets the directory containing the models.
   * @return The directory containing the models.
   */
  public String getModelDirectory() {
    return modelDirectory;
  }

  /**
   * Sets the model directory.
   * @param modelDirectory The directory containing the models. This should be a
   * directory on the local file system.
   */
  public void setModelDirectory(String modelDirectory) {
    this.modelDirectory = modelDirectory;
  }

  /**
   * Sets the {@link IdylNLPModelZoo} client.
   * @param idylNLPModelZoo A {@link IdylNLPModelZoo client}.
   */
  public void setIdylNLPModelZoo(IdylNLPModelZoo idylNLPModelZoo) {
    this.idylNlpModelZoo = idylNLPModelZoo;
  }

  /**
   * Gets the map of models to file names.
   * @return A map of models to file names.
   */
  public Map<StandardModelManifest, T> getModels() {
    return models;
  }

  /**
   * Sets the model map.
   * @param modelMap The model map which is a map of
   * models to file names.
   */
  public void setModels(Map<StandardModelManifest, T> modelMap) {
    this.models = modelMap;
  }

}
