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
package ai.idylnlp.test.model.manifest;

import static org.junit.Assert.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.ModelManifestUtils;
import ai.idylnlp.model.manifest.StandardModelManifest;

public class ModelManifestUtilsTest {

  private static final String MODEL_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String BAD_MANIFEST = MODEL_PATH + "/bad.manifest";

  @Test
  public void generateModelManifestTest1() throws IOException {

    File manifestFile = File.createTempFile("model", "manifest");

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder("modelid", "name", "filename", LanguageCode.en,
        "type", "entity", "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    ModelManifestUtils.generateStandardModelManifest(modelManifest, manifestFile);

    InputStream is = new FileInputStream(manifestFile);

    List<String> lines = IOUtils.readLines(is);

    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_MODEL_ID + "=" + "modelid"));
    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_MODEL_TYPE + "=" + "type"));
    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_MODEL_FILENAME + "=" + "filename"));
    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_LANGUAGE_CODE + "=" + "en"));

    IOUtils.closeQuietly(is);

  }

  @Test
  public void generateModelManifestTest2() throws IOException {

    File manifestFile = File.createTempFile("model", "manifest");

    ModelManifestUtils.generateStandardModelManifest(manifestFile, "modelid", "name", "type", "entity", "filename", LanguageCode.en,
        "1.0.0", "");

    InputStream is = new FileInputStream(manifestFile);

    List<String> lines = IOUtils.readLines(is);

    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_MODEL_ID + "=" + "modelid"));
    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_MODEL_TYPE + "=" + "type"));
    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_MODEL_FILENAME + "=" + "filename"));
    assertTrue(lines.contains(ModelManifestUtils.MANIFEST_LANGUAGE_CODE + "=" + "eng"));

    IOUtils.closeQuietly(is);

  }

  @Test
  public void readManifest() throws Exception {

    File manifestFile = File.createTempFile("model", "manifest");

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder("modelid", "name", "filename", LanguageCode.en,
        "type", "entity", "1.0.0", "source", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();;

    ModelManifestUtils.generateStandardModelManifest(modelManifest, manifestFile);

    ModelManifest readModelManifest = ModelManifestUtils.readManifest(manifestFile.getAbsolutePath());
    
    for(Object o : readModelManifest.getProperties().keySet()) {
      assertEquals(readModelManifest.getProperties().getProperty((String) o), readModelManifest.getProperties().getProperty((String) o));
    }

    assertEquals(modelManifest, readModelManifest);

  }

  @Test
  public void readManifestNoName() throws Exception {

    File manifestFile = File.createTempFile("model", "manifest");

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder("modelid", "", "filename", LanguageCode.en,
        "type", "entity", "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();;

    ModelManifestUtils.generateStandardModelManifest(modelManifest, manifestFile);

    ModelManifest readModelManifest = ModelManifestUtils.readManifest(manifestFile.getAbsolutePath());

    assertEquals("modelid", readModelManifest.getName());

  }

  @Test
  public void getModelManifestsBadPath() throws Exception {

    List<ModelManifest> modelManifests = ModelManifestUtils.getModelManifests("/bad/path");

    assertEquals(0, modelManifests.size());

  }

  @Test
  public void getModelManifests() throws Exception {

    List<ModelManifest> modelManifests = ModelManifestUtils.getModelManifests(MODEL_PATH);

    // One of the manifests is bad and two are good.
    // So there should two returned.
    assertEquals(2, modelManifests.size());

  }

  @Test
  public void getModelManifestsForType() throws Exception {

    List<ModelManifest> modelManifests = ModelManifestUtils.getModelManifests(MODEL_PATH, "sentence");

    // One of the manifests is bad and one is good.
    // So there should only be one returned.
    assertEquals(1, modelManifests.size());

  }

  @Test
  public void readManifestWithoutModelId() throws Exception {

    ModelManifest readModelManifest = ModelManifestUtils.readManifest(BAD_MANIFEST);

    assertNull(readModelManifest);

  }

}
