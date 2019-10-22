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
package ai.idylnlp.test.models.deeplearning.training.model;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import ai.idylnlp.model.nlp.annotation.AnnotationTypes;
import ai.idylnlp.models.deeplearning.training.DeepLearningEntityModelOperations;
import ai.idylnlp.models.deeplearning.training.model.DeepLearningTrainingDefinition;
import ai.idylnlp.models.deeplearning.training.model.EarlyTermination;
import ai.idylnlp.models.deeplearning.training.model.EvaluationData;
import ai.idylnlp.models.deeplearning.training.model.HyperParameters;
import ai.idylnlp.models.deeplearning.training.model.Monitoring;
import ai.idylnlp.models.deeplearning.training.model.NetworkConfigurationParameters;
import ai.idylnlp.models.deeplearning.training.model.Output;
import ai.idylnlp.models.deeplearning.training.model.RegularizationParameters;
import ai.idylnlp.models.deeplearning.training.model.TrainingData;
import ai.idylnlp.models.deeplearning.training.model.UpdaterParameters;
import ai.idylnlp.testing.markers.ExternalData;

public class DeepLearningTrainingDefinitionTest {

  private static final String TRAINING_DATA_PATH = System.getProperty("testDataPath");

  @Category(ExternalData.class)
  @Test
  public void serialize() throws IOException {

    DeepLearningEntityModelOperations ops = new DeepLearningEntityModelOperations();

    DeepLearningTrainingDefinition definition = getDefinition();
    final String json = ops.getGson().toJson(definition);

    DeepLearningTrainingDefinition deserialized = ops.getGson().fromJson(json, DeepLearningTrainingDefinition.class);

    assertEquals(definition, deserialized);

  }

  private DeepLearningTrainingDefinition getDefinition() throws IOException {

    UpdaterParameters updaterParameters = new UpdaterParameters();
    updaterParameters.setUpdater("rmsprop");

    RegularizationParameters regularizationParameters = new RegularizationParameters();
    regularizationParameters.setRegularization(true);
    regularizationParameters.setL2(1e-5);

    Map<String, Double> learningRateSchedule = new HashMap<String, Double>();
    learningRateSchedule.put("0", 0.01);
    learningRateSchedule.put("1000", 0.005);
    learningRateSchedule.put("2000", 0.001);
    learningRateSchedule.put("3000", 0.0001);
    learningRateSchedule.put("4000", 0.00001);

    NetworkConfigurationParameters networkConfigurationParameters = new NetworkConfigurationParameters();
    networkConfigurationParameters.setOptimizationAlgorithm("stochastic_gradient_descent");
    networkConfigurationParameters.setGradientNormalization("clipelementwiseabsolutevalue");
    networkConfigurationParameters.setGradientNormalizationThreshold(1.0);
    networkConfigurationParameters.setUpdaterParameters(updaterParameters);
    networkConfigurationParameters.setRegularizationParameters(regularizationParameters);
    networkConfigurationParameters.setPretrain(false);
    networkConfigurationParameters.setBackprop(true);
    networkConfigurationParameters.setWeightInit("xavier");

    HyperParameters hyperParameters = new HyperParameters();
    hyperParameters.setEpochs(1);
    hyperParameters.setWindowSize(5);
    hyperParameters.setSeed(1497630814976308L);
    hyperParameters.setBatchSize(32);
    hyperParameters.setNetworkConfigurationParameters(networkConfigurationParameters);

    Monitoring monitoring = new Monitoring();
    monitoring.setScoreIteration(100);

    String wordVectorsFile = TRAINING_DATA_PATH + "/reuters-vectors.txt";

    DeepLearningTrainingDefinition definition = new DeepLearningTrainingDefinition();
    definition.setTrainingData(new TrainingData(AnnotationTypes.CONLL2003.getName(), TRAINING_DATA_PATH + "/conll2003-eng.train", wordVectorsFile));
    definition.setEvaluationData(new EvaluationData(AnnotationTypes.CONLL2003.getName(), TRAINING_DATA_PATH + "/conll2003-eng.testa"));
    definition.setOutput(new Output(File.createTempFile("multilayernetwork", ".zip").getAbsolutePath(), "/tmp/stats.dl4j"));
    definition.setEarlyTermination(new EarlyTermination(20, 180));
    definition.setEntityType("person");
    definition.setHyperParameters(hyperParameters);
    definition.setMonitoring(monitoring);

    return definition;

  }

}
