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
package ai.idylnlp.models.opennlp.training.model;

import opennlp.tools.ml.maxent.quasinewton.QNTrainer;
import opennlp.tools.ml.perceptron.PerceptronTrainer;

/**
 * A training algorithm.
 *
 * @author Mountain Fog, Inc.
 *
 */
public enum TrainingAlgorithm {

  /**
   * Uses the maxent algorithm with L-BFGFS.
   */
  MAXENT_QN(QNTrainer.MAXENT_QN_VALUE, "maxent-qn"),

  /**
   * Uses the perceptron algorithm.
   */
  PERCEPTRON(PerceptronTrainer.PERCEPTRON_VALUE, "perceptron");

  private String algorithm;
  private String name;

  private TrainingAlgorithm(String algorithm, String name) {
    this.algorithm = algorithm;
    this.name = name;
  }

  /**
   * Gets the algorithm.
   * @return The algorithm.
   */
  public String getAlgorithm() {
    return algorithm;
  }

  /**
   * Gets the name of the algorithm.
   * @return The name of the algorithm.
   */
  public String getName() {
    return name;
  }

  /**
   * Gets the default {@link TrainingAlgorithm}.
   * @return The default {@link TrainingAlgorithm}.
   */
  public static TrainingAlgorithm getDefaultAlgorithm() {
    return PERCEPTRON;
  }

  @Override
  public String toString() {
    return algorithm;
  }

  /**
   * Gets the {@link TrainingAlgorithm} from a string value or
   * throws an {@link IllegalArgumentException} if the algorithm
   * string value is not a valid algorithm.
   * @param algorithm The algorithm.
   * @return A {@link TrainingAlgorithm}.
   */
  public static TrainingAlgorithm fromValue(String algorithm) {

    if(algorithm.equalsIgnoreCase(MAXENT_QN.getName())) {
      return MAXENT_QN;
    } else if(algorithm.equalsIgnoreCase(PERCEPTRON.getName())) {
      return PERCEPTRON;
    } else {
      throw new IllegalArgumentException("Invalid algorithm.");
    }

  }

}
