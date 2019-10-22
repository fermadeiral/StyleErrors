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
package ai.idylnlp.test.nlp.filters.confidence;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.nlp.filters.confidence.HeuristicConfidenceFilter;
import ai.idylnlp.nlp.filters.confidence.serializers.LocalConfidenceFilterSerializer;

public class HeuristicConfidenceFilterTest {

  private static final Logger LOGGER = LogManager.getLogger(HeuristicConfidenceFilterTest.class);

  @Test
  public void test1() throws IOException {

    File file = File.createTempFile("confidences", "dat");
    LocalConfidenceFilterSerializer serializer = new LocalConfidenceFilterSerializer(file);

    HeuristicConfidenceFilter f = new HeuristicConfidenceFilter(serializer);

    assertTrue(f.test("id1", 88, 99));

    assertTrue(f.test("id1", 92, 99));

    assertTrue(f.test("id1", 5, 99));

    assertTrue(f.test("id1", 5, 99));

    assertTrue(f.test("id1", 5, 99));

    assertTrue(f.test("id2", 18, 99));

    assertTrue(f.test("id3", 18, 99));

    assertTrue(f.test("id3", 18, 99));

  }

  @Test
  public void test2() throws IOException {

    File file = File.createTempFile("confidences", "dat");
    LocalConfidenceFilterSerializer serializer = new LocalConfidenceFilterSerializer(file);

    HeuristicConfidenceFilter f = new HeuristicConfidenceFilter(serializer);

    assertTrue(f.test("id1", 88, 99));

    assertTrue(f.test("id1", 92, 99));

    assertTrue(f.test("id1", 85, 99));

  }

  @Test
  public void test3() throws IOException {

    File file = File.createTempFile("confidences", "dat");
    LocalConfidenceFilterSerializer serializer = new LocalConfidenceFilterSerializer(file);

    HeuristicConfidenceFilter f = new HeuristicConfidenceFilter(serializer);

    assertTrue(f.test("id1", 88, 75));

    assertTrue(f.test("id1", 15, 75));

    assertTrue(f.test("id1", 85, 75));

  }

}
