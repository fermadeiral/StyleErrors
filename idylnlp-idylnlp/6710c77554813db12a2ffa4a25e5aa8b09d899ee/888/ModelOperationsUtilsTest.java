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
package ai.idylnlp.test.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;

import ai.idylnlp.model.nlp.subjects.IdylNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.OpenNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import ai.idylnlp.models.ModelOperationsUtils;
import ai.idylnlp.training.definition.model.TrainingDefinitionReader;
import ai.idylnlp.training.definition.xml.Trainingdefinition;
import ai.idylnlp.training.definition.xml.Trainingdefinition.Trainingdata;

public class ModelOperationsUtilsTest {

  private static final Logger LOGGER = LogManager.getLogger(ModelOperationsUtilsTest.class);

  @Test
  public void getSubjectOfTrainingOrEvaluationOpenNLP() {

    Trainingdata data = new Trainingdata();
    data.setFile("training-input-file.txt");
    data.setFormat("opennlp");

    Trainingdefinition def = new Trainingdefinition();
    def.setTrainingdata(data);

    TrainingDefinitionReader reader = Mockito.mock(TrainingDefinitionReader.class);
    when(reader.getTrainingDefinition()).thenReturn(def);

    SubjectOfTrainingOrEvaluation sot = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    assertTrue(sot instanceof OpenNLPSubjectOfTrainingOrEvaluation);
    assertEquals(sot.getInputFile(), "training-input-file.txt");

  }

  @Test
  public void getSubjectOfTrainingOrEvaluationIdylNLP() {

    Trainingdata data = new Trainingdata();
    data.setFile("training-input-file.txt");
    data.setFormat("idylnlp");
    data.setAnnotations("annotations.txt");

    Trainingdefinition def = new Trainingdefinition();
    def.setTrainingdata(data);

    TrainingDefinitionReader reader = Mockito.mock(TrainingDefinitionReader.class);
    when(reader.getTrainingDefinition()).thenReturn(def);

    SubjectOfTrainingOrEvaluation sot = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    assertTrue(sot instanceof IdylNLPSubjectOfTrainingOrEvaluation);
    assertEquals(sot.getInputFile(), "training-input-file.txt");

    IdylNLPSubjectOfTrainingOrEvaluation nf = (IdylNLPSubjectOfTrainingOrEvaluation) sot;
    assertEquals(nf.getAnnotationsFile(), "annotations.txt");

  }

  @Test
  public void getSubjectOfTrainingOrEvaluationInvalid() {

    Trainingdata data = new Trainingdata();
    data.setFile("training-input-file.txt");
    data.setFormat("INVALID");

    Trainingdefinition def = new Trainingdefinition();
    def.setTrainingdata(data);

    TrainingDefinitionReader reader = Mockito.mock(TrainingDefinitionReader.class);
    when(reader.getTrainingDefinition()).thenReturn(def);

    SubjectOfTrainingOrEvaluation sot = ModelOperationsUtils.getSubjectOfTrainingOrEvaluation(reader);

    assertTrue(sot instanceof OpenNLPSubjectOfTrainingOrEvaluation);
    assertEquals(sot.getInputFile(), "training-input-file.txt");

  }

}
