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
package ai.idylnlp.test.pipeline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.pipeline.NerPipeline;
import ai.idylnlp.pipeline.NerPipeline.NerPipelineBuilder;

public class NerPipelineTest {

  private static final Logger LOGGER = LogManager.getLogger(NerPipelineTest.class);

  @Test
  public void run() {

    // Make a NER pipeline with all defaults.
    NerPipelineBuilder builder = new NerPipeline.NerPipelineBuilder();
    NerPipeline pipeline = builder.build(LanguageCode.en);

    EntityExtractionResponse response = pipeline.run("George Washington was president.");

    assertEquals(1, response.getEntities().size());
    assertTrue(response.getExtractionTime() > 0);

    for(Entity e : response.getEntities()) {
      LOGGER.info(e.toString());
    }

  }

  @Test
  public void removeDuplicateEntities() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("George Washington", 1.0, "person", "eng"));
    entities.add(new Entity("George Washington", 0.9607598536046513, "person", "eng"));

    Set<Entity> noDuplicates = NerPipeline.removeDuplicateEntities(entities);

    assertEquals(1, noDuplicates.size());

    for(Entity entity : noDuplicates) {

      LOGGER.info(entity.toString());

    }

  }

  @Test
  public void removeDuplicateEntities1() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("George", 10, "person", "en"));
    entities.add(new Entity("George", 25, "person", "en"));
    entities.add(new Entity("Tom", 50, "person", "en"));

    Set<Entity> noDuplicates = NerPipeline.removeDuplicateEntities(entities);

    assertEquals(2, noDuplicates.size());

    for(Entity entity : noDuplicates) {

      assertTrue(entity.getText().equals("George") || entity.getText().equalsIgnoreCase("Tom"));

      if(entity.getText().equals("George")) {

        assertEquals(25, entity.getConfidence(), 0);

      }

    }

  }

  @Test
  public void removeDuplicateEntities2() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("George", 10, "person", "en"));
    entities.add(new Entity("George", 5, "person", "en"));
    entities.add(new Entity("Tom", 50, "person", "en"));

    Set<Entity> noDuplicates = NerPipeline.removeDuplicateEntities(entities);

    assertEquals(2, noDuplicates.size());

    for(Entity entity : noDuplicates) {

      assertTrue(entity.getText().equals("George") || entity.getText().equalsIgnoreCase("Tom"));

      if(entity.getText().equals("George")) {

        assertEquals(10, entity.getConfidence(), 0);

      }

    }

  }

  @Test
  public void removeDuplicateEntities3() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("Bill", 10, "person", "en"));
    entities.add(new Entity("George", 5, "person", "en"));
    entities.add(new Entity("Tom", 50, "person", "en"));

    Set<Entity> noDuplicates = NerPipeline.removeDuplicateEntities(entities);

    assertEquals(3, noDuplicates.size());

  }

  @Test
  public void removeDuplicateEntities4() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("George", 10, "person", "en"));
    entities.add(new Entity("George", 5, "person", "en"));
    entities.add(new Entity("Tom", 50, "person", "en"));
    entities.add(new Entity("George", 50, "person", "en"));

    Set<Entity> noDuplicates = NerPipeline.removeDuplicateEntities(entities);

    assertEquals(2, noDuplicates.size());

    for(Entity entity : noDuplicates) {

      assertTrue(entity.getText().equals("George") || entity.getText().equalsIgnoreCase("Tom"));

      if(entity.getText().equals("George")) {

        assertEquals(50, entity.getConfidence(), 0);

      }

    }

  }

}
