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
package ai.idylnlp.test.models.opennlp.training.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ai.idylnlp.models.opennlp.training.model.TrainingAlgorithm;

public class TrainingAlgorithmTest {

  @Test
  public void defaultAlgorithm() {

    assertEquals(TrainingAlgorithm.PERCEPTRON, TrainingAlgorithm.getDefaultAlgorithm());

  }

  @Test
  public void fromValue() {

    assertEquals(TrainingAlgorithm.MAXENT_QN, TrainingAlgorithm.fromValue(TrainingAlgorithm.MAXENT_QN.getName()));
    assertEquals(TrainingAlgorithm.PERCEPTRON, TrainingAlgorithm.fromValue(TrainingAlgorithm.PERCEPTRON.getName()));

  }

  @Test(expected=IllegalArgumentException.class)
  public void fromValueIllegal() {

    TrainingAlgorithm.fromValue("invalid");

  }

}
