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
package ai.idylnlp.model.training;

public class FMeasure {

  private double precision;
  private double recall;
  private double fmeasure;

  public FMeasure(double precision, double recall, double fmeasure) {

    this.precision = precision;
    this.recall = recall;
    this.fmeasure = fmeasure;

  }

  public double getPrecision() {
    return precision;
  }

  public double getRecall() {
    return recall;
  }

  public double getFmeasure() {
    return fmeasure;
  }

  @Override
  public String toString() {

    return String.format("Precision: %f, Recall: %f, F-Measure: %f", precision, recall, fmeasure);

  }

}
