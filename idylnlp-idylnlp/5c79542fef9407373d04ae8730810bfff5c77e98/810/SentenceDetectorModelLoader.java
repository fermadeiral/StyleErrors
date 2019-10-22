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
package ai.idylnlp.nlp.sentence;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.opennlp.custom.modelloader.ModelLoader;
import ai.idylnlp.zoo.IdylNLPModelZoo;
import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.manifest.StandardModelManifest;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * Model loader for sentence detection models.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SentenceDetectorModelLoader extends ModelLoader<SentenceModel> {

  private static final Logger LOGGER = LogManager.getLogger(SentenceDetectorModelLoader.class);

  private StandardModelManifest modelManifest;

  /**
   * Create a new {@link SentenceDetectorModelLoader}.
   * @param modelValidator A {@link ModelValidator} to validate the model.
   * @param modelDirectory The full path to the directory containing the model.
   * @param modelManifest The model's {@link StandardModelManifest}.
   */
  public SentenceDetectorModelLoader(ModelValidator modelValidator, String modelDirectory, StandardModelManifest modelManifest) {

    super(modelValidator);

    super.setModelDirectory(modelDirectory);

    this.modelManifest = modelManifest;

  }

  /**
   * Create a new {@link SentenceDetectorModelLoader}.
   * @param modelValidator A {@link ModelValidator} to validate the model.
   * @param modelDirectory The full path to the directory containing the model.
   * @param modelManifest The model's {@link StandardModelManifest}.
   * @param idylNlpModelZoo A {@link IdylNLPModelZoo} client.
   */
  public SentenceDetectorModelLoader(ModelValidator modelValidator, String modelDirectory, StandardModelManifest modelManifest,
      IdylNLPModelZoo idylNlpModelZoo) {

    super(modelValidator);

    super.setModelDirectory(modelDirectory);
    super.setIdylNLPModelZoo(idylNlpModelZoo);

    this.modelManifest = modelManifest;

  }

  /**
   * Gets the full path to the model.
   * @return The full path (directory and filename) of the model.
   */
  public String getFullModelPath() {
    return super.getModelDirectory() + modelManifest.getModelFileName();
  }

  /**
   * Gets the model's manifest.
   * @return The model's {@link StandardModelManifest}..
   */
  public StandardModelManifest getModelManifest() {
    return modelManifest;
  }

}
