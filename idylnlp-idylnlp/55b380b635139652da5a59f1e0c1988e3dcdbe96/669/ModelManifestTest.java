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
package ai.idylnlp.test.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest.ModelManifestBuilder;
import ai.idylnlp.testing.ObjectTest;

public class ModelManifestTest extends ObjectTest<StandardModelManifest> {

  @Test
  public void builderTest() {

    ModelManifestBuilder builder = new ModelManifestBuilder();
    builder.setBeamSize(5);
    builder.setCreatorVersion("version-1.2.0");
    builder.setEncryptionKey("encryption");
    builder.setLanguageCode(LanguageCode.de);
    builder.setModelFileName("model.bin");
    builder.setModelId("model-id");
    builder.setName("model-name");
    builder.setType("model-type");

    StandardModelManifest modelManifest = builder.build();

    assertEquals(5, modelManifest.getBeamSize());
    assertEquals("version-1.2.0", modelManifest.getCreatorVersion());
    assertEquals("encryption", modelManifest.getEncryptionKey());
    assertEquals("deu", modelManifest.getLanguageCode().getAlpha3().toString());
    assertEquals("model.bin", modelManifest.getModelFileName());
    assertEquals("model-id", modelManifest.getModelId());
    assertEquals("model-name", modelManifest.getName());
    assertEquals("model-type", modelManifest.getType());

  }

  @Test
  public void builderWithoutBeamSizeTest() {

    ModelManifestBuilder builder = new ModelManifestBuilder();
    builder.setCreatorVersion("version-1.2.0");
    builder.setEncryptionKey("encryption");
    builder.setLanguageCode(LanguageCode.de);
    builder.setModelFileName("model.bin");
    builder.setModelId("model-id");
    builder.setName("model-name");
    builder.setType("model-type");

    StandardModelManifest modelManifest = builder.build();

    assertEquals(StandardModelManifest.DEFAULT_BEAM_SIZE, modelManifest.getBeamSize());
    assertEquals("version-1.2.0", modelManifest.getCreatorVersion());
    assertEquals("encryption", modelManifest.getEncryptionKey());
    assertEquals("deu", modelManifest.getLanguageCode().getAlpha3().toString());
    assertEquals("model.bin", modelManifest.getModelFileName());
    assertEquals("model-id", modelManifest.getModelId());
    assertEquals("model-name", modelManifest.getName());
    assertEquals("model-type", modelManifest.getType());

  }

}
