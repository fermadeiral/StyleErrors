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
package ai.idylnlp.test.models;

import static org.junit.Assert.*;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

import ai.idylnlp.model.training.FMeasure;
import ai.idylnlp.model.training.FMeasureModelValidationResult;
import ai.idylnlp.models.ModelEvaluationUtils;

public class ModelEvaluationUtilsTest {

  @Test
  public void performPairedTTestSameValues() {

    double alpha = 0.05;

    final FMeasure fmeasure1 = new FMeasure(0, 0, 0.85);

    List<FMeasure> measures1 = new LinkedList<FMeasure>();
    measures1.add(new FMeasure(0, 0, 0.85));
    measures1.add(new FMeasure(0, 0, 0.85));
    measures1.add(new FMeasure(0, 0, 0.85));
    measures1.add(new FMeasure(0, 0, 0.85));
    measures1.add(new FMeasure(0, 0, 0.85));

    FMeasureModelValidationResult result1 = new FMeasureModelValidationResult(fmeasure1, measures1);

    final FMeasure fmeasure2 = new FMeasure(055, 0.60, 0.78);

    List<FMeasure> measures2 = new LinkedList<FMeasure>();
    measures2.add(new FMeasure(0, 0, 0.85));
    measures2.add(new FMeasure(0, 0, 0.85));
    measures2.add(new FMeasure(0, 0, 0.85));
    measures2.add(new FMeasure(0, 0, 0.85));
    measures2.add(new FMeasure(0, 0, 0.85));

    FMeasureModelValidationResult result2 = new FMeasureModelValidationResult(fmeasure2, measures2);

    boolean result = ModelEvaluationUtils.performPairedTTest(result1, result2, alpha);

    assertFalse(result);

  }

  @Test
  public void performPairedTTest() {

    double alpha = 0.05;

    final FMeasure fmeasure1 = new FMeasure(0, 0, 0.85);

    List<FMeasure> measures1 = new LinkedList<FMeasure>();
    measures1.add(new FMeasure(0, 0, 0.85));
    measures1.add(new FMeasure(0, 0, 0.77));
    measures1.add(new FMeasure(0, 0, 0.55));
    measures1.add(new FMeasure(0, 0, 0.62));
    measures1.add(new FMeasure(0, 0, 0.88));

    FMeasureModelValidationResult result1 = new FMeasureModelValidationResult(fmeasure1, measures1);

    final FMeasure fmeasure2 = new FMeasure(055, 0.60, 0.78);

    List<FMeasure> measures2 = new LinkedList<FMeasure>();
    measures2.add(new FMeasure(0, 0, 0.19));
    measures2.add(new FMeasure(0, 0, 0.29));
    measures2.add(new FMeasure(0, 0, 0.24));
    measures2.add(new FMeasure(0, 0, 0.33));
    measures2.add(new FMeasure(0, 0, 0.35));

    FMeasureModelValidationResult result2 = new FMeasureModelValidationResult(fmeasure2, measures2);

    boolean result = ModelEvaluationUtils.performPairedTTest(result1, result2, alpha);

    assertTrue(result);

  }

}
