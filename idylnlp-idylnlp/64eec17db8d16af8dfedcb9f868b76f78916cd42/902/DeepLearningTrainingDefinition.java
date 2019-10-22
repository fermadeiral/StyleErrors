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
package ai.idylnlp.models.deeplearning.training.model;

import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeepLearningTrainingDefinition {

  @SerializedName("Output")
  @Expose
  private Output output;

  @SerializedName("TrainingData")
  @Expose
  private TrainingData trainingData;

  @SerializedName("EvaluationData")
  @Expose
  private EvaluationData evaluationData;

  @SerializedName("HyperParameters")
  @Expose
  private HyperParameters hyperParameters;

  @SerializedName("EarlyTermination")
  @Expose
  private EarlyTermination earlyTermination;

  @SerializedName("EntityType")
  @Expose
  private String entityType;

  @SerializedName("Language")
  @Expose
  private String language = "en";

  @SerializedName("Name")
  @Expose
  private String name = "model";

  @SerializedName("Monitoring")
  @Expose
  private Monitoring monitoring;

  @SerializedName("ParallelTraining")
  @Expose
  private ParallelTraining parallelTraining;

  @SerializedName("SparkTraining")
  @Expose
  private SparkTraining sparkTraining = new SparkTraining();

  public DeepLearningTrainingDefinition() throws IOException {

    monitoring = new Monitoring();

  }

  public DeepLearningTrainingDefinition(Output output, TrainingData trainingData,
      EvaluationData evaluationData, HyperParameters hyperParameters, EarlyTermination earlyTermination,
      String entityType, SparkTraining sparkTraining, Monitoring monitoring, ParallelTraining parallelTraining,
      String name, String language) {

    this.output = output;
    this.trainingData = trainingData;
    this.evaluationData = evaluationData;
    this.hyperParameters = hyperParameters;
    this.earlyTermination = earlyTermination;
    this.entityType = entityType;
    this.sparkTraining = sparkTraining;
    this.monitoring = monitoring;
    this.parallelTraining = parallelTraining;
    this.name = name;
    this.language = language;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public Output getOutput() {
    return output;
  }

  public void setOutput(Output output) {
    this.output = output;
  }

  public HyperParameters getHyperParameters() {
    return hyperParameters;
  }

  public void setTraining(HyperParameters hyperParameters) {
    this.hyperParameters = hyperParameters;
  }

  public TrainingData getTrainingData() {
    return trainingData;
  }

  public void setTrainingData(TrainingData trainingData) {
    this.trainingData = trainingData;
  }

  public EvaluationData getEvaluationData() {
    return evaluationData;
  }

  public void setEvaluationData(EvaluationData evaluationData) {
    this.evaluationData = evaluationData;
  }

  public void setHyperParameters(HyperParameters hyperParameters) {
    this.hyperParameters = hyperParameters;
  }

  public EarlyTermination getEarlyTermination() {
    return earlyTermination;
  }

  public void setEarlyTermination(EarlyTermination earlyTermination) {
    this.earlyTermination = earlyTermination;
  }

  public String getEntityType() {
    return entityType;
  }

  public void setEntityType(String entityType) {
    this.entityType = entityType;
  }

  public SparkTraining getSparkTraining() {
    return sparkTraining;
  }

  public void setSparkTraining(SparkTraining sparkTraining) {
    this.sparkTraining = sparkTraining;
  }

  public Monitoring getMonitoring() {
    return monitoring;
  }

  public void setMonitoring(Monitoring monitoring) {
    this.monitoring = monitoring;
  }

  public ParallelTraining getParallelTraining() {
    return parallelTraining;
  }

  public void setParallelTraining(ParallelTraining parallelTraining) {
    this.parallelTraining = parallelTraining;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
