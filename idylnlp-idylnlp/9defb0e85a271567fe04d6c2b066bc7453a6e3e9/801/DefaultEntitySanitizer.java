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
package ai.idylnlp.nlp.entity.sanitizers;

import java.util.Set;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.nlp.EntitySanitizer;

public class DefaultEntitySanitizer implements EntitySanitizer {

  /**
   * Sanitize the entities by removing punctuation and other attributes.
   *
   * @param entities A collection of {@link Entity} objects.
   * @return A collection of sanitized {@link Entity} objects. This collection
   * will be equal in size to the input collection.
   */
  @Override
  public Set<Entity> sanitizeEntities(Set<Entity> entities) {

    for(Entity entity : entities) {

      if(entity.getText().endsWith(",") || entity.getText().endsWith(".")) {

        entity.setText(entity.getText().substring(0, entity.getText().length() - 1));

      }

      // Replace all punctuation.
      entity.setText(entity.getText().replaceAll("\\p{P}", ""));

    }

    return entities;

  }

}
