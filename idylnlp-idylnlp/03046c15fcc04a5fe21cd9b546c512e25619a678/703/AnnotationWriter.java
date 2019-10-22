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

import java.util.Collection;

import ai.idylnlp.model.entity.Entity;

/**
 * Interface for writing annotated input text. The annotated
 * text can be written anywhere, such as a simple text file,
 * a database, or memory.
 *
 * @author Mountain Fog, Inc.
 *
 */
@FunctionalInterface
public interface AnnotationWriter {

  /**
   * Annotate the entities in the text.
   * @param entities The set of {@link Entity entities}.
   * @param text The text containing the entities.
   * @return Text containing the annotated entities.
   */
  public String annotateText(Collection<Entity> entities, String text);

}
