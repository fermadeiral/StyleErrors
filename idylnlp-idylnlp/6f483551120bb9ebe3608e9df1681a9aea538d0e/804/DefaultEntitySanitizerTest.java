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
package ai.idylnlp.test.nlp.entity.sanitizers;

import static org.junit.Assert.*;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

import ai.idylnlp.model.entity.Entity;

import ai.idylnlp.nlp.entity.sanitizers.DefaultEntitySanitizer;

public class DefaultEntitySanitizerTest {

  @Test
  public void sanitizeEntities() {

    DefaultEntitySanitizer sanitizer = new DefaultEntitySanitizer();

    Set<Entity> entities = new HashSet<Entity>();
    entities.add(new Entity("George."));
    entities.add(new Entity("Abe,"));

    Set<Entity> sanitizedEntities = sanitizer.sanitizeEntities(entities);

    for(Entity entity : sanitizedEntities) {

      assertFalse(entity.getText().endsWith("."));
      assertFalse(entity.getText().endsWith(","));
      assertFalse(entity.getText().contains("."));
      assertFalse(entity.getText().contains(","));

    }

  }

}
