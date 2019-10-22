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
package ai.idylnlp.model.training;

import java.util.List;

/**
 * Contains the {@link FMeasure} values resulting from
 * model evaluations that produce F-measures. This class
 * extends {@link EvaluationResult}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class FMeasureModelValidationResult extends EvaluationResult  {

  private FMeasure fmeasure;
  private List<FMeasure> fmeasures;

  /**
   * Creates a new result.
   * @param fmeasure A {@link FMeasure}.
   */
  public FMeasureModelValidationResult(FMeasure fmeasure) {
    this.fmeasure = fmeasure;
  }

  /**
   * Creates a new result.
   * @param fmeasure A {@link FMeasure}.
   * @param fmeasures A list of {@link FMeasure}.
   */
  public FMeasureModelValidationResult(FMeasure fmeasure, List<FMeasure> fmeasures) {
    this.fmeasure = fmeasure;
    this.fmeasures = fmeasures;
  }

  /**
   * Gets the F-measure.
   * @return The {@link FMeasure}.
   */
  public FMeasure getFmeasure() {
    return fmeasure;
  }

  /**
   * Sets the F-measure.
   * @param fmeasure The {@link FMeasure}.
   */
  public void setFmeasure(FMeasure fmeasure) {
    this.fmeasure = fmeasure;
  }

  /**
   * Gets the F-measures.
   * @return The {@link FMeasure}.
   */
  public List<FMeasure> getFmeasures() {
    return fmeasures;
  }

  /**
   * Sets the F-measures.
   * @param fmeasures A list of {@link FMeasure}.
   */
  public void setFmeasures(List<FMeasure> fmeasures) {
    this.fmeasures = fmeasures;
  }

}
