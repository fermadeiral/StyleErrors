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
package ai.idylnlp.models;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.inference.TestUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.training.FMeasure;
import ai.idylnlp.model.training.FMeasureModelValidationResult;

/**
 * Utility functions for model evaluations.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class ModelEvaluationUtils {

  private static final Logger LOGGER = LogManager.getLogger(ModelEvaluationUtils.class);

  private ModelEvaluationUtils() {
    // This is a utility class.
  }

  /**
   * Perform a paired T-test on the F-Measures of two cross validation results
   * to determine if the F-Measures are significantly different.
   * @param result1 A {@link FMeasureModelValidationResult}.
   * @param result2 A {@link FMeasureModelValidationResult}.
   * @param alpha The alpha value.
   * @return <code>true</code> iff the null hypothesis can be rejected with confidence <code>1 - alpha</code>.
   */
  public static boolean performPairedTTest(FMeasureModelValidationResult result1, FMeasureModelValidationResult result2, double alpha) {

    // Null hypothesis - the hypothesis that there is no significant difference between specified populations,
    // any observed difference being due to sampling or experimental error.

    final List<Double> f1 = new LinkedList<Double>();

    for(FMeasure fmeasure : result1.getFmeasures()) {
      LOGGER.trace("Adding F-Measure for feature set 1: {}", fmeasure.getFmeasure());
      LOGGER.trace("\t{}", fmeasure.toString());
      f1.add(fmeasure.getFmeasure());
    }

    final List<Double> f2 = new LinkedList<Double>();

    for(FMeasure fmeasure : result2.getFmeasures()) {
      LOGGER.trace("Adding F-Measure for feature set 2: {}", fmeasure.getFmeasure());
      LOGGER.trace("\t{}", fmeasure.toString());
      f2.add(fmeasure.getFmeasure());
    }

    final double[] pa = f1.stream().mapToDouble(Double::doubleValue).toArray();
    final double[] ra = f2.stream().mapToDouble(Double::doubleValue).toArray();

    return performPairedTTest(pa, ra, alpha);

  }

  /**
   * Perform a paired T-test on the F-Measures of two sets of values.
   * @param result1 The first value set.
   * @param result2 The second value set.
   * @param alpha The alpha value.
   * @return <code>true</code> iff the null hypothesis can be rejected with confidence <code>1 - alpha</code>.
   */
  public static boolean performPairedTTest(double[] result1, double[] result2, double alpha) {

    // Null hypothesis - the hypothesis that there is no significant difference between specified populations,
    // any observed difference being due to sampling or experimental error.

    // Returns true iff the null hypothesis can be rejected with confidence 1 - alpha.
    return TestUtils.pairedTTest(result1, result2, alpha);

  }

}
