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
package ai.idylnlp.test.nlp.annotation.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.model.nlp.annotation.IdylNLPAnnotation;
import ai.idylnlp.nlp.annotation.reader.IdylNLPFileAnnotationReader;

public class IdylNLPFileAnnotationReaderTest {

  private static final Logger LOGGER = LogManager.getLogger(IdylNLPFileAnnotationReaderTest.class);

  private static final String TRAINING_DATA_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String ANNOTATION_FILE = TRAINING_DATA_PATH + File.separator + "annotations.txt";

  @Test
  public void read() throws IOException {

    IdylNLPFileAnnotationReader reader = new IdylNLPFileAnnotationReader(ANNOTATION_FILE);

    Collection<IdylNLPAnnotation> annotations = reader.getAnnotations(1);
    assertEquals(1, annotations.size());

    annotations = reader.getAnnotations(3);
    assertEquals(2, annotations.size());

    annotations = reader.getAnnotations(7);
    assertTrue(annotations.isEmpty());

  }

}
