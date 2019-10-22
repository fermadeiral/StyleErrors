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
package ai.idylnlp.nlp.annotation.writers;

import java.util.Collection;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.nlp.AnnotationWriter;

/**
 * Writes the annotated text to a file.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLPFileAnnotationWriter implements AnnotationWriter {

  /**
   * Creates a new {@link OpenNLPFileAnnotationWriter}.
   */
  public OpenNLPFileAnnotationWriter() {

  }

  @Override
  public String annotateText(Collection<Entity> entities, String text) {

    String annotatedText = text;

    for(Entity entity : entities) {

      annotatedText = annotatedText.replaceAll(entity.getText(), "<START:" + entity.getType().toLowerCase() + "> " + entity.getText() + " <END>");

    }

    return annotatedText;

  }

}
