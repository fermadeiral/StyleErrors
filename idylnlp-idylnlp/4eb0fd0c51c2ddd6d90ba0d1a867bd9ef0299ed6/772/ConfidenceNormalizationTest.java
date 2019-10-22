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
package ai.idylnlp.test.model.nlp;

import static org.junit.Assert.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.model.nlp.ConfidenceNormalization;

public class ConfidenceNormalizationTest {

  private static final Logger LOGGER = LogManager.getLogger(ConfidenceNormalizationTest.class);

  @Test
  public void normalize1Test() {

    double normalized = ConfidenceNormalization.normalizeConfidence(99);

    assertEquals(0.99, normalized, 0);

  }

  @Test
  public void normalize2Test() {

    double normalized = ConfidenceNormalization.normalizeConfidence(0);

    assertEquals(0, normalized, 0);

  }

  @Test
  public void normalize3Test() {

    double normalized = ConfidenceNormalization.normalizeConfidence(100);

    assertEquals(1.0, normalized, 0);

  }

}
