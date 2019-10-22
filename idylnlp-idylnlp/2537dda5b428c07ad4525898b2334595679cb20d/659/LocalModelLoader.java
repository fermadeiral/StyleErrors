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

package ai.idylnlp.opennlp.custom.modelloader;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.zoo.IdylNLPModelZoo;
import opennlp.tools.util.model.BaseModel;

/**
 * A model loader for using models from the local file system.
 * @author Mountain Fog, Inc.
 *
 */
public final class LocalModelLoader<T extends BaseModel> extends ModelLoader<T> {

  private static final Logger LOGGER = LogManager.getLogger(LocalModelLoader.class);

  /**
   * Creates a local model loader.
   * @param modelValidator A {@link ModelValidator} to validate the model prior to loading.
   * @param modelDirectory The directory on the local file system that contains the models.
   */
  public LocalModelLoader(ModelValidator modelValidator, String modelDirectory) {

    super(modelValidator);

    if(!modelDirectory.endsWith(File.separator)) {
      modelDirectory = modelDirectory + File.separator;
    }

    LOGGER.info("Using local model loader directory {}", modelDirectory);

    super.setModelDirectory(modelDirectory);

  }

  /**
   * Creates a local model loader.
   * @param modelValidator A {@link ModelValidator} to validate the model prior to loading.
   * @param modelDirectory The directory on the local file system that contains the models.
   * @param idylNlpModelZoo A {@link IdylNLPModelZoo} client.
   */
  public LocalModelLoader(ModelValidator modelValidator, String modelDirectory, IdylNLPModelZoo idylNlpModelZoo) {

    this(modelValidator, modelDirectory);

    super.setIdylNLPModelZoo(idylNlpModelZoo);

  }

}