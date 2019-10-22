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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Layer {

  @SerializedName("LearningRate")
  @Expose
  private double learningRate = 1e-1;

  @SerializedName("BiasLearningRate")
  @Expose
  private double biasLearningRate = Double.NaN;

  @SerializedName("LearningRateDecayPolicy")
  @Expose
  private String learningRateDecayPolicy = "schedule";

  @SerializedName("LearningRateSchedule")
  @Expose
  private Map<String, Double> learningRateSchedule;

  public Layer() {

  }

  public Layer(double learningRate) {

    this.learningRate = learningRate;

  }

  public Layer(double learningRate, double biasLearningRate, Map<String, Double> learningRateSchedule) {

    this.learningRate = learningRate;
    this.biasLearningRate = biasLearningRate;
    this.learningRateSchedule = learningRateSchedule;

  }

  public Map<Integer, Double> getLearningRateScheduleParam() {

    Map<Integer, Double> param = new HashMap<Integer, Double>();

    if(learningRateSchedule != null) {

      for(String key : learningRateSchedule.keySet()) {

        param.put(Integer.valueOf(key), learningRateSchedule.get(key));

      }

    }

    return param;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public double getLearningRate() {
    return learningRate;
  }

  public void setLearningRate(double learningRate) {
    this.learningRate = learningRate;
  }

  public double getBiasLearningRate() {
    return biasLearningRate;
  }

  public void setBiasLearningRate(double biasLearningRate) {
    this.biasLearningRate = biasLearningRate;
  }

  public Map<String, Double> getLearningRateSchedule() {
    return learningRateSchedule;
  }

  public void setLearningRateSchedule(Map<String, Double> learningRateSchedule) {
    this.learningRateSchedule = learningRateSchedule;
  }

  public String getLearningRateDecayPolicy() {
    return learningRateDecayPolicy;
  }

  public void setLearningRateDecayPolicy(String learningRateDecayPolicy) {
    this.learningRateDecayPolicy = learningRateDecayPolicy;
  }

}
