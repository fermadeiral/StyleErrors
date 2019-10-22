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
package ai.idylnlp.model.entity.comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import ai.idylnlp.model.entity.Entity;

/**
 * {@link Comparator Comparator} for {@link Entity entities}.
 * Supports sorting by entity confidence and entity text.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class EntityComparator implements Comparator<Entity> {

  private Order sortingBy;

  /**
   * Creates a new {@link EntityComparator} with the given
   * sorting method.
   * @param sortingBy The {@link Order} to sort the entities.
   */
  public EntityComparator(Order sortingBy) {
    this.sortingBy = sortingBy;
  }

  public enum Order {
    CONFIDENCE, TEXT
  }

  @Override
  public int compare(Entity entity1, Entity entity2) {

    switch (sortingBy) {

    case CONFIDENCE:
      return Double.compare(entity1.getConfidence(), entity2.getConfidence());

    case TEXT:
      return entity1.getText().compareTo(entity2.getText());

    }

    throw new IllegalArgumentException("Invalid sort type.");

  }

  /**
   * Gets the sorting order.
   * @return The {@link Order}.
   */
  public Order getSortingBy() {
    return sortingBy;
  }

  /**
   * Sort the entities.
   * @param entities The set of {@link Entity entities}.
   * @param sortingBy The sorting method.
   * @return A set of sorted {@link Entity entities}.
   */
  public static Set<Entity> sort(Set<Entity> entities, Order sortingBy) {

    EntityComparator comparator = new EntityComparator(sortingBy);
    List<Entity> list = new ArrayList<Entity>(entities);
    Collections.sort(list, comparator);
    Set<Entity> sortedEntities = new LinkedHashSet<>(list);

    return sortedEntities;

  }

}