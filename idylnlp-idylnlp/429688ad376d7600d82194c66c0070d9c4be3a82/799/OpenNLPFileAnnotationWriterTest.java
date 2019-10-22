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
package ai.idylnlp.test.nlp.annotation.writers;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.model.entity.Entity;

import ai.idylnlp.nlp.annotation.writers.OpenNLPFileAnnotationWriter;

public class OpenNLPFileAnnotationWriterTest {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPFileAnnotationWriterTest.class);

  @Test
  public void write() throws IOException {

    OpenNLPFileAnnotationWriter writer = new OpenNLPFileAnnotationWriter();

    Collection<Entity> entities = new ArrayList<Entity>();
    entities.add(new Entity("George Washington", "person"));
    entities.add(new Entity("Abraham Lincoln", "person"));

    String text = "George Washington and Abraham Lincoln were presidents.";

    String annotatedText = writer.annotateText(entities, text);

    String expectedText = "<START:person> George Washington <END> and <START:person> Abraham Lincoln <END> were presidents.";

    LOGGER.info("Expected: " + expectedText);
    LOGGER.info("Actual:   " + annotatedText);

    assertEquals(expectedText, annotatedText);

  }

  @Test
  public void writeEmpty() throws IOException {

    OpenNLPFileAnnotationWriter writer = new OpenNLPFileAnnotationWriter();

    Collection<Entity> entities = new ArrayList<Entity>();
    // No entities to annotate.

    String text = "George Washington and Abraham Lincoln were presidents.";

    String annotatedText = writer.annotateText(entities, text);

    LOGGER.info("Expected: " + text);
    LOGGER.info("Actual:   " + annotatedText);

    assertEquals(text, annotatedText);

  }

}
