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

/**
 * Provides model training operations.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface ModelTrainingOperations {

  /**
   * Train a maxent model using quasi-newton.
   * @param subjectOfTraining The {@link SubjectOfTrainingOrEvaluation}.
   * @param modelFile The output model file.
   * @param language The language of the model.
   * @param encryptionKey The model's encryption key.
   * @param cutOff The training cutoff.
   * @param iterations The training iterations.
   * @param threads The number of training threads.
   * @param l1
   * @param l2
   * @param m
   * @param max
   * @return The generated model's ID.
   * @throws IOException Thrown if the model cannot be trained.
   */
  public String trainMaxEntQN(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFile, LanguageCode language, String encryptionKey, int cutOff, int iterations, int threads, double l1, double l2, int m, int max) throws IOException;

  /**
   * Train a perceptron model using.
   * @param subjectOfTraining The {@link SubjectOfTrainingOrEvaluation}.
   * @param modelFile The output model file.
   * @param language The language of the model.
   * @param encryptionKey The model's encryption key.
   * @param cutOff The training cutoff.
   * @param iterations The training iterations.
   * @return The generated model's ID.
   * @throws IOException Thrown if the model cannot be trained.
   */
  public String trainPerceptron(SubjectOfTrainingOrEvaluation subjectOfTraining, String modelFile, LanguageCode language, String encryptionKey, int cutOff, int iterations) throws IOException;

}
