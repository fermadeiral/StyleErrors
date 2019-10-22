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
package ai.idylnlp.test.nlp.recognizer.configuration;

import static org.junit.Assert.assertEquals;
import java.util.Properties;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.nlp.recognizer.configuration.OpenNLPEntityRecognizerConfiguration;
import ai.idylnlp.nlp.recognizer.configuration.OpenNLPEntityRecognizerConfiguration.Builder;

public class OpenNLPEntityRecognizerConfigurationTest {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPEntityRecognizerConfigurationTest.class);

  @Test
  public void test1() {

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder("modelId", "name", "modelFileName", LanguageCode.en, "", StandardModelManifest.ENTITY, StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    OpenNLPEntityRecognizerConfiguration config = new Builder().build();
    config.addEntityModel("person", LanguageCode.en, modelManifest);

    assertEquals(1, config.getEntityModels().size());
    assertEquals(1, config.getEntityModels().get("person").size());

    Set<StandardModelManifest> manifests = config.getEntityModels().get("person").get(LanguageCode.en);

    assertEquals(1, manifests.size());

    assertEquals(modelManifest, manifests.iterator().next());

  }

  @Test
  public void test2() {

    StandardModelManifest modelManifest1 = new StandardModelManifest.ModelManifestBuilder("modelId1", "name", "modelFileName1", LanguageCode.en, "", StandardModelManifest.ENTITY, StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();
    StandardModelManifest modelManifest2 = new StandardModelManifest.ModelManifestBuilder("modelId2", "name", "modelFileName2", LanguageCode.en, "", StandardModelManifest.ENTITY, StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    OpenNLPEntityRecognizerConfiguration config = new Builder().build();
    config.addEntityModel("person", LanguageCode.en, modelManifest1);
    config.addEntityModel("person", LanguageCode.en, modelManifest2);

    LOGGER.info(config.getEntityModels());

    assertEquals(1, config.getEntityModels().size());
    assertEquals(1, config.getEntityModels().get("person").size());

    Set<StandardModelManifest> manifests = config.getEntityModels().get("person").get(LanguageCode.en);

    assertEquals(2, manifests.size());

  }

  @Test
  public void test3() {

    StandardModelManifest modelManifest1 = new StandardModelManifest.ModelManifestBuilder("modelId1", "name", "modelFileName1", LanguageCode.en, "", StandardModelManifest.ENTITY, StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();
    StandardModelManifest modelManifest2 = new StandardModelManifest.ModelManifestBuilder("modelId1", "name", "modelFileName1", LanguageCode.en, "", StandardModelManifest.ENTITY, StandardModelManifest.DEFAULT_SUBTYPE, "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    OpenNLPEntityRecognizerConfiguration config = new Builder().build();
    config.addEntityModel("person", LanguageCode.en, modelManifest1);
    config.addEntityModel("person", LanguageCode.en, modelManifest2);

    LOGGER.info(config.getEntityModels());

    assertEquals(1, config.getEntityModels().size());
    assertEquals(1, config.getEntityModels().get("person").size());

    Set<StandardModelManifest> manifests = config.getEntityModels().get("person").get(LanguageCode.en);

    // Only 1 because the models is a set and the model manifests are identical in this test.
    assertEquals(1, manifests.size());

  }

}
