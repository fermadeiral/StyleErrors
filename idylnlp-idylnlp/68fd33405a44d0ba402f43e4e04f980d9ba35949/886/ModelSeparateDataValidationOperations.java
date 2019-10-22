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

import java.io.IOException;

import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.training.EvaluationResult;
import opennlp.tools.util.eval.FMeasure;

/**
 * Provides operations for performing model validation
 * using separate data.
 *
 * @author Mountain Fog, Inc.
 */
public interface ModelSeparateDataValidationOperations<T extends EvaluationResult> {

  /**
   * Performs model validation using separate data. This validation requires a built model file.
   * @param subjectOfTraining The {@link SubjectOfTrainingOrEvaluation}.
   * @param modelFileName The full path to the model file.
   * @return The results of the validation as an {@link FMeasure}.
   * @throws IOException Thrown if any of the input files cannot be read.
   */
  public T separateDataEvaluate(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFileNam) throws IOException;

}
