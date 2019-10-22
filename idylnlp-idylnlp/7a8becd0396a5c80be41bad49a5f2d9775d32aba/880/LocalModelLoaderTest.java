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
package ai.idylnlp.test.models.loaders;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.UUID;

import opennlp.tools.namefind.TokenNameFinderModel;

import org.junit.Test;
import org.mockito.Mockito;

import ai.idylnlp.opennlp.custom.modelloader.LocalModelLoader;
import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.exceptions.ValidationException;
import ai.idylnlp.model.manifest.StandardModelManifest;

public class LocalModelLoaderTest {

  private static final String MODEL_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String MODEL_FILE_NAME = "/model3635140961782910400.bin";
  private static final String MODEL_BAD_FILE_NAME = "/model-bad.bin";

  @Test
  public void validModelTest() throws ModelLoaderException, ValidationException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);
    when(modelValidator.validateVersion(any(String.class))).thenReturn(true);

    final String modelId = "53121889-2d0f-412a-be67-476ce4a69843";

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", MODEL_FILE_NAME, LanguageCode.en, "", "person", "entity", "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE).build();

    LocalModelLoader<TokenNameFinderModel> loader = new LocalModelLoader<TokenNameFinderModel>(modelValidator, MODEL_PATH);
    TokenNameFinderModel model = loader.getModel(modelManifest, TokenNameFinderModel.class);

    assertEquals(model.getManifestProperty("model.id"), modelId);

  }

}
