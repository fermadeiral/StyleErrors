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
package ai.idylnlp.model.nlp.annotation;

import java.util.Collection;

/**
 * Provides access to annotations stored in an external source (file, database, etc.).
 *
 * @author Mountain Fog, Inc.
 *
 */
@FunctionalInterface
public interface AnnotationReader {

  /**
   * Retrieves the annotations for a given line number.
   * @param lineNumber The line number in the text.
   * @return A collection of {@link IdylNLPAnnotation annotations}.
   */
  public Collection<IdylNLPAnnotation> getAnnotations(int lineNumber);

}
