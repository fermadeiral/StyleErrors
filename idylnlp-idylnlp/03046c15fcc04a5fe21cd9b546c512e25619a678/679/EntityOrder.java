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
package ai.idylnlp.model.nlp;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Entity sort orders.
 *
 * @author Mountain Fog, Inc.
 *
 */
public enum EntityOrder {

  CONFIDENCE("confidence"),

  TEXT("text"),

  OCCURRENCE("occurrence");

  private static final Logger LOGGER = LogManager.getLogger(EntityOrder.class);

  private String order;

  private EntityOrder(String order) {
    this.order = order;
  }

  /**
   * Gets an {@link EntityOrder} by value.
   * @param order The sort order.
   * @return An {@link EntityOrder}.
   */
  public static EntityOrder fromValue(String order) {

    if(order.equalsIgnoreCase("confidence")) {
      return CONFIDENCE;
    } else if(order.equalsIgnoreCase("text")) {
      return TEXT;
    } else if(order.equalsIgnoreCase("occurrence")) {
      return OCCURRENCE;
    }

    // Default to confidence when it is an invalid order.
    LOGGER.warn("No entity sort order for {}. Entities will be sorted by confidence.", order);
    return CONFIDENCE;

  }

  /**
   * Gets the allowed sort orders.
   * @return A list of allowed sort orders.
   */
  public static List<String> getOrders() {

    List<String> orders = new LinkedList<String>();

    for(EntityOrder order : EntityOrder.values()) {
      orders.add(order.toString());
    }

    return orders;

  }

  @Override
  public String toString() {
    return order;
  }

}
