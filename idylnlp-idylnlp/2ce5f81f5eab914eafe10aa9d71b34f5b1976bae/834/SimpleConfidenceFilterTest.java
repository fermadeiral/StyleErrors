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
package ai.idylnlp.test.nlp.filters.confidence;

import static org.junit.Assert.*;

import org.junit.Test;

import ai.idylnlp.nlp.filters.confidence.SimpleConfidenceFilter;

public class SimpleConfidenceFilterTest {

  @Test
  public void test1() {

    SimpleConfidenceFilter f = new SimpleConfidenceFilter();

    assertFalse(f.test("id1", 88, 99));

    assertFalse(f.test("id1", 92, 99));

    assertFalse(f.test("id1", 5, 99));

    assertFalse(f.test("id1", 5, 99));

    assertFalse(f.test("id1", 5, 99));

    assertFalse(f.test("id2", 18, 99));

    assertFalse(f.test("id3", 18, 99));

    assertFalse(f.test("id3", 18, 99));

  }

  @Test
  public void test2() {

    SimpleConfidenceFilter f = new SimpleConfidenceFilter();

    assertFalse(f.test("id1", 88, 99));

    assertFalse(f.test("id1", 92, 99));

    assertFalse(f.test("id1", 85, 99));

  }

  @Test
  public void test3() {

    SimpleConfidenceFilter f = new SimpleConfidenceFilter();

    assertTrue(f.test("id1", 88, 75));

    assertFalse(f.test("id1", 15, 75));

    assertTrue(f.test("id1", 85, 75));

  }

}
