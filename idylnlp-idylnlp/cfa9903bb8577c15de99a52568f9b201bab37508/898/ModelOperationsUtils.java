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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.nlp.annotation.AnnotationTypes;
import ai.idylnlp.model.nlp.subjects.BratSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.CoNLL2003SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.IdylNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.OpenNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.training.definition.model.TrainingDefinitionReader;

public class ModelOperationsUtils {

  private static final Logger LOGGER = LogManager.getLogger(ModelOperationsUtils.class);

  /**
   * Gets a {@link SubjectOfTraining} based on the training definition.
   * @param reader A {@link TrainingDefinitionReader}.
   * @return A {@link SubjectOfTraining}.
   */
  public static SubjectOfTrainingOrEvaluation getSubjectOfTrainingOrEvaluation(TrainingDefinitionReader reader) {

    final String inputFile = reader.getTrainingDefinition().getTrainingdata().getFile();

    SubjectOfTrainingOrEvaluation subjectOfTraining = null;

    // Set this based on what's in the training definition file.
    if(reader.getTrainingDefinition().getTrainingdata().getFormat().equalsIgnoreCase(AnnotationTypes.IDYLNLP.getName())) {

      LOGGER.debug("Using Idyl NLP data format.");
      subjectOfTraining = new IdylNLPSubjectOfTrainingOrEvaluation(inputFile, reader.getTrainingDefinition().getTrainingdata().getAnnotations());

    } else if(reader.getTrainingDefinition().getTrainingdata().getFormat().equalsIgnoreCase(AnnotationTypes.CONLL2003.getName())) {

      LOGGER.debug("Using CoNLL2003 data format.");
      subjectOfTraining = new CoNLL2003SubjectOfTrainingOrEvaluation(inputFile);

    } else if(reader.getTrainingDefinition().getTrainingdata().getFormat().equalsIgnoreCase(AnnotationTypes.BRAT.getName())) {

      LOGGER.debug("Using Brat data format.");
      subjectOfTraining = new BratSubjectOfTrainingOrEvaluation(inputFile);

    } else {

      LOGGER.debug("Using OpenNLP data format.");
      subjectOfTraining = new OpenNLPSubjectOfTrainingOrEvaluation(inputFile);

    }

    return subjectOfTraining;

  }

}
