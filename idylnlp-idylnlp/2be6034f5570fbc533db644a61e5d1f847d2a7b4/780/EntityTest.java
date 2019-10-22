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
package ai.idylnlp.test.nlp.recognizer.model;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

import ai.idylnlp.model.entity.Entity;

public class EntityTest {

  private static final Logger LOGGER = LogManager.getLogger(EntityTest.class);

  @Test
  public void equalsTest() {

    Set<Entity> entities = new HashSet<Entity>();

    Entity e1 = new Entity();
    e1.setText("jeffrey");
    e1.setConfidence(0.5);
    e1.setType("person");

    Entity e2 = new Entity();
    e2.setText("jeffrey");
    e2.setConfidence(0.6);
    e2.setType("person");

    LOGGER.info("Equals: " + e1.equals(e2));
    assertNotEquals(e1, e2);

    entities.add(e1);
    entities.add(e2);

    LOGGER.info("Set size: " + entities.size());

    assertEquals(2, entities.size());

    for(Entity e : entities) {
      LOGGER.info(e.toString());
    }

  }

  @Test
  public void equalsTest2() {

    Set<Entity> entities = new HashSet<Entity>();

    Entity e1 = new Entity();
    e1.setText("jeffrey");
    e1.setConfidence(0.5);
    e1.setType("person");

    Entity e2 = new Entity();
    e2.setText("jeffrey");
    e2.setConfidence(0.5);
    e2.setType("person");

    LOGGER.info("Equals: " + e1.equals(e2));
    assertEquals(e1, e2);

    entities.add(e1);
    entities.add(e2);

    assertEquals(1, entities.size());

  }

  @Test
  public void equalsTest3() {

    Set<Entity> entities = new HashSet<Entity>();

    Entity e1 = new Entity();
    e1.setText("jeffrey");
    e1.setConfidence(0.5);

    Entity e2 = new Entity();
    e2.setText("jeffrey");
    e2.setConfidence(0.5);

    LOGGER.info("Equals: " + e1.equals(e2));
    assertEquals(e1, e2);

    entities.add(e1);
    entities.add(e2);

    assertEquals(1, entities.size());

  }

  @Test
  public void equalsTest4() {

    Entity e1 = new Entity();
    e1.setText("jeffrey");
    e1.setConfidence(0.5);
    e1.setType("person");

    Entity e2 = new Entity();
    e2.setText("jeffrey");
    e2.setConfidence(0.5);

    LOGGER.info("Equals: " + e1.equals(e2));
    assertFalse(e1.equals(e2));

  }

  @Test
  public void equalsTest5() {

    Set<Entity> entities = new HashSet<Entity>();

    Entity e1 = new Entity("jeffrey", 0.5, "person", "[0, 2)", "context", "documentid");
    Entity e2 = new Entity("jeffrey", 0.5, "person", "[0, 2)", "context", "documentid");

    entities.add(e1);
    entities.add(e2);

    LOGGER.info("Equals: " + e1.equals(e2));
    assertTrue(e1.equals(e2));

  }

  @Test
  public void equalsTest6() {

    Set<Entity> entities = new HashSet<Entity>();

    Entity e1 = new Entity("jeffrey", 0.5, "person", "[0, 2)", "context", "documentid");
    Entity e2 = new Entity("john", 0.5, "person", "[0, 2)", "context", "documentid");

    entities.add(e1);
    entities.add(e2);

    LOGGER.info("Equals: " + e1.equals(e2));
    assertFalse(e1.equals(e2));

  }

}
