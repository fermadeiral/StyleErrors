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
package ai.idylnlp.models.opennlp.training.model;

import java.io.IOException;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.training.EvaluationResult;
import ai.idylnlp.model.training.FMeasureModelValidationResult;

/**
 * Provides model cross validation operations.
 *
 * @author Mountain Fog, Inc.
 */
public interface ModelCrossValidationOperations<T extends EvaluationResult> {

  /**
   * Performs model cross validation using the perceptron algorithm.
   * @param subjectOfTraining The {@link SubjectOfTrainingOrEvaluation}.
   * @param language The language of the model.
   * @param iterations The number of iterations.
   * @param cutOff The value of the cutoff.
   * @param folds The number of cross validation folds.
   * @return A {@link FMeasureModelValidationResult}.
   * @throws IOException Thrown if the cross validation fails.
   */
  public FMeasureModelValidationResult crossValidationEvaluatePerceptron(SubjectOfTrainingOrEvaluation subjectOfTraining, LanguageCode language, int iterations, int cutOff, int folds) throws IOException;

  /**
   * Performs model cross validation using the maxent QN algorithm.
   * @param subjectOfTraining The {@link SubjectOfTrainingOrEvaluation}.
   * @param language The language of the model.
   * @param iterations The number of iterations.
   * @param cutOff The value of the cutoff.
   * @param folds The number of cross validation folds.
   * @param l1
   * @param l2
   * @param m
   * @param max
   * @return A {@link FMeasureModelValidationResult}.
   * @throws IOException Thrown if the cross validation fails.
   */
  public T crossValidationEvaluateMaxEntQN(SubjectOfTrainingOrEvaluation subjectOfTraining, LanguageCode language, int iterations, int cutOff, int folds, double l1, double l2, int m, int max) throws IOException;

}
