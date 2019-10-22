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
package ai.idylnlp.test.model.entity.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.entity.Span;

public class RandomEntityUtils {

  /**
   * Creates a list of 10 random person entities.
   * @return A list of 10 random person entities.
   */
  public static Collection<Entity> createRandomPersonEntities() {

    Collection<Entity> entities = new ArrayList<Entity>();

    for(int i = 0; i < 10; i++) {

      entities.add(createRandomPersonEntity());

    }

    return entities;

  }

  /**
   * Creates a list of random person entities.
   * @return A list of random person entities.
   */
  public static Collection<Entity> createRandomPersonEntities(int count) {

    Collection<Entity> entities = new ArrayList<Entity>();

    for(int i = 0; i < count; i++) {

      entities.add(createRandomPersonEntity());

    }

    return entities;

  }

  /**
   * Create random entity metadata.
   * @return Random entity metadata.
   */
  public static Map<String, String> createRandomMetadata() {

    Map<String, String> metadata = new HashMap<String, String>();

    for(int x=0; x<5; x++) {

      metadata.put(RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10));

    }

    return metadata;

  }

  /**
   * Creates a random entity.
   * @param type The entity's {@link String class}.
   * @return A random entity.
   */
  public static Entity createRandomEntity(String type) {

    final int entityLength = RandomUtils.nextInt(4, 25);

    Entity entity = new Entity();
    entity.setText(RandomStringUtils.randomAlphabetic(entityLength));
    entity.setConfidence(RandomUtils.nextDouble(0, 1));
    entity.setLanguageCode("en");
    entity.setSpan(new Span(3, 5));
    entity.setType(type);

    return entity;

  }

  /**
   * Creates a random entity.
   * @param type The entity's {@link String class}.
   * @param minConfidence The minimum value of the confidence range.
   * @param maxConfidence The maximum value of the confidence range.
   * @return A random entity.
   */
  public static Entity createRandomEntity(String type, double minConfidence, double maxConfidence) {

    final int entityLength = RandomUtils.nextInt(4, 25);

    Entity entity = new Entity();
    entity.setText(RandomStringUtils.randomAlphabetic(entityLength));
    entity.setConfidence(RandomUtils.nextDouble(minConfidence, maxConfidence));
    entity.setLanguageCode("en");
    entity.setSpan(new Span(3, 5));
    entity.setType(type);

    return entity;

  }

  /**
   * Creates a random person entity.
   * @return A random person {@link Entity entity}.
   */
  public static Entity createRandomPersonEntity() {

    final int entityLength = RandomUtils.nextInt(4, 25);

    Entity entity = new Entity();
    entity.setText(RandomStringUtils.randomAlphabetic(entityLength));
    entity.setConfidence(RandomUtils.nextDouble(0, 1));
    entity.setLanguageCode("en");
    entity.setSpan(new Span(3, 5));
    entity.setType("person");
    entity.setMetadata(createRandomMetadata());
    entity.setContext("context");
    entity.setDocumentId("documentId");

    return entity;

  }

  /**
   * Creates a random person entity.
   * @param text The text of the entity.
   * @return A random person {@link Entity entity}.
   */
  public static Entity createRandomPersonEntity(String text) {

    Entity entity = new Entity();
    entity.setText(text);
    entity.setConfidence(RandomUtils.nextDouble(0, 1));
    entity.setLanguageCode("en");
    entity.setSpan(new Span(3, 5));
    entity.setType("person");

    return entity;

  }

}