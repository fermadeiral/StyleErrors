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
package ai.idylnlp.test.model.entity.comparators;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.entity.comparators.EntityComparator;

public class EntityComparatorTest {

  @Test
  public void sortByConfidence() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("jeffrey", 40, "person", "[0,2)"));
    entities.add(new Entity("george", 20, "person", "[0,2)"));
    entities.add(new Entity("bob", 70, "person", "[0,2)"));

    Set<Entity> sortedEntities = EntityComparator.sort(entities, EntityComparator.Order.CONFIDENCE);

    List<Entity> list = new ArrayList<Entity>(sortedEntities);

    assertTrue(list.get(0).getConfidence() == 20);
    assertTrue(list.get(1).getConfidence() == 40);
    assertTrue(list.get(2).getConfidence() == 70);

  }

  @Test
  public void sortByText() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("jeffrey", 40, "person", "[0,2)"));
    entities.add(new Entity("george", 20, "person", "[0,2)"));
    entities.add(new Entity("bob", 70, "person", "[0,2)"));

    Set<Entity> sortedEntities = EntityComparator.sort(entities, EntityComparator.Order.TEXT);

    List<Entity> list = new ArrayList<Entity>(sortedEntities);

    assertTrue(list.get(0).getText().equals("bob"));
    assertTrue(list.get(1).getText().equals("george"));
    assertTrue(list.get(2).getText().equals("jeffrey"));

  }

  @Test(expected=IllegalArgumentException.class)
  public void invalidSort() {

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("jeffrey", 40, "person", "[0,2)"));
    entities.add(new Entity("george", 20, "person", "[0,2)"));
    entities.add(new Entity("bob", 70, "person", "[0,2)"));

    EntityComparator.sort(entities, EntityComparator.Order.valueOf("test"));

  }

}