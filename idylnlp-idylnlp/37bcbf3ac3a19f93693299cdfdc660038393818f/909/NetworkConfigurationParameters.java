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
package ai.idylnlp.models.deeplearning.training.model;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.deeplearning4j.nn.api.OptimizationAlgorithm;
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.weights.WeightInit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NetworkConfigurationParameters {

  @SerializedName("BiasInit")
  @Expose
  private double biasInit = 0.0;

  @SerializedName("BiasLearningRate")
  @Expose
  private Integer biasLearningRate = 0;

  @SerializedName("ConvolutionMode")
  @Expose
  private String convolutionMode;

  @SerializedName("UseDropConnect")
  @Expose
  private boolean useDropConnect = false;

  @SerializedName("DropOut")
  @Expose
  private double dropOut = 0.0;

  @SerializedName("Iterations")
  @Expose
  private int iterations = 1;

  @SerializedName("OptimizationAlgorithm")
  @Expose
  private String optimizationAlgorithm = "stochastic_gradient_descent";

  @SerializedName("WeightInit")
  @Expose
  private String weightInit;

  @SerializedName("Pretrain")
  @Expose
  private boolean pretrain = false;

  @SerializedName("Backprop")
  @Expose
  private boolean backprop = true;

  @SerializedName("RegularizationParameters")
  @Expose
  private RegularizationParameters regularizationParameters;

  @SerializedName("UpdaterParameters")
  @Expose
  private UpdaterParameters updaterParameters;

  @SerializedName("Layers")
  @Expose
  private Layers layers;

  @SerializedName("GradientNormalization")
  @Expose
  private String gradientNormalization;

  @SerializedName("GradientNormalizationThreshold")
  @Expose
  private double gradientNormalizationThreshold = 0.0;

  public NetworkConfigurationParameters() {

  }

  public NetworkConfigurationParameters(double biasInit, int biasLearningRate, String convolutionMode, boolean useDropConnect,
      int dropOut, int iterations, String optimizationAlgorithm, String weightInit, double learningRate,
      boolean pretrain, boolean backprop, RegularizationParameters regularizationParameters,
      UpdaterParameters updaterParameters, Layers layers, String gradientNormalization, double gradientNormalizationThreshold) {

    this.biasInit = biasInit;
    this.biasLearningRate = biasLearningRate;
    this.convolutionMode = convolutionMode;
    this.useDropConnect = useDropConnect;
    this.iterations = iterations;
    this.optimizationAlgorithm = optimizationAlgorithm;
    this.weightInit = weightInit;
    this.pretrain = pretrain;
    this.backprop = backprop;
    this.regularizationParameters = regularizationParameters;
    this.updaterParameters = updaterParameters;
    this.layers = layers;
    this.gradientNormalization = gradientNormalization;
    this.gradientNormalizationThreshold = gradientNormalizationThreshold;

  }

  public OptimizationAlgorithm getOptimizationAlgorithmParam() {

    if(StringUtils.equalsIgnoreCase(optimizationAlgorithm, "conjugate_gradient")) {

      return OptimizationAlgorithm.CONJUGATE_GRADIENT;

    } else if(StringUtils.equalsIgnoreCase(optimizationAlgorithm, "lbfgs")) {

      return OptimizationAlgorithm.LBFGS;

    } else if(StringUtils.equalsIgnoreCase(optimizationAlgorithm, "line_gradient_descent")) {

      return OptimizationAlgorithm.LINE_GRADIENT_DESCENT;

    } else if(StringUtils.equalsIgnoreCase(optimizationAlgorithm, "stochastic_gradient_descent")) {

      return OptimizationAlgorithm.STOCHASTIC_GRADIENT_DESCENT;

    } else {

      return null;

    }

  }

  public WeightInit getWeightInitParam() {

    if(StringUtils.equalsIgnoreCase(weightInit, "xavier")) {

      return WeightInit.XAVIER;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "distribution")) {

      return WeightInit.DISTRIBUTION;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "relu")) {

      return WeightInit.RELU;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "relu_uniform")) {

      return WeightInit.RELU_UNIFORM;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "sigmoid_uniform")) {

      return WeightInit.SIGMOID_UNIFORM;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "uniform")) {

      return WeightInit.UNIFORM;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "xavier_fan_in")) {

      return WeightInit.XAVIER_FAN_IN;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "xavier_legacy")) {

      return WeightInit.XAVIER_LEGACY;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "xavier_uniform")) {

      return WeightInit.XAVIER_UNIFORM;

    } else if(StringUtils.equalsIgnoreCase(weightInit, "zero")) {

      return WeightInit.ZERO;

    } else {

      return null;

    }

  }

  public GradientNormalization getGradientNormalizationParam() {

    if(StringUtils.equalsIgnoreCase(gradientNormalization, "clipelementwiseabsolutevalue")) {

      return GradientNormalization.ClipElementWiseAbsoluteValue;

    } else if(StringUtils.equalsIgnoreCase(gradientNormalization, "clipl2perlayer")) {

      return GradientNormalization.ClipL2PerLayer;

    } else if(StringUtils.equalsIgnoreCase(gradientNormalization, "clipl2perparamtype")) {

      return GradientNormalization.ClipL2PerParamType;

    } else if(StringUtils.equalsIgnoreCase(gradientNormalization, "none")) {

      return GradientNormalization.None;

    } else if(StringUtils.equalsIgnoreCase(gradientNormalization, "renormalizel2perlayer")) {

      return GradientNormalization.RenormalizeL2PerLayer;

    } else if(StringUtils.equalsIgnoreCase(gradientNormalization, "renormalizel2perparamtype")) {

      return GradientNormalization.RenormalizeL2PerParamType;

    } else {

      return null;

    }

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public double getBiasInit() {
    return biasInit;
  }

  public void setBiasInit(double biasInit) {
    this.biasInit = biasInit;
  }

  public Integer getBiasLearningRate() {
    return biasLearningRate;
  }

  public void setBiasLearningRate(Integer biasLearningRate) {
    this.biasLearningRate = biasLearningRate;
  }

  public String getConvolutionMode() {
    return convolutionMode;
  }

  public void setConvolutionMode(String convolutionMode) {
    this.convolutionMode = convolutionMode;
  }

  public Boolean isUseDropConnect() {
    return useDropConnect;
  }

  public void setUseDropConnect(boolean useDropConnect) {
    this.useDropConnect = useDropConnect;
  }

  public double getDropOut() {
    return dropOut;
  }

  public void setDropOut(double dropOut) {
    this.dropOut = dropOut;
  }

  public int getIterations() {
    return iterations;
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public String getOptimizationAlgorithm() {
    return optimizationAlgorithm;
  }

  public void setOptimizationAlgorithm(String optimizationAlgorithm) {
    this.optimizationAlgorithm = optimizationAlgorithm;
  }

  public String getWeightInit() {
    return weightInit;
  }

  public void setWeightInit(String weightInit) {
    this.weightInit = weightInit;
  }

  public boolean isPretrain() {
    return pretrain;
  }

  public void setPretrain(boolean pretrain) {
    this.pretrain = pretrain;
  }

  public boolean isBackprop() {
    return backprop;
  }

  public void setBackprop(boolean backprop) {
    this.backprop = backprop;
  }

  public RegularizationParameters getRegularizationParameters() {
    return regularizationParameters;
  }

  public void setRegularizationParameters(RegularizationParameters regularizationParameters) {
    this.regularizationParameters = regularizationParameters;
  }

  public UpdaterParameters getUpdaterParameters() {
    return updaterParameters;
  }

  public void setUpdaterParameters(UpdaterParameters updaterParameters) {
    this.updaterParameters = updaterParameters;
  }

  public Layers getLayers() {
    return layers;
  }

  public void setLayers(Layers layers) {
    this.layers = layers;
  }

  public String getGradientNormalization() {
    return gradientNormalization;
  }

  public void setGradientNormalization(String gradientNormalization) {
    this.gradientNormalization = gradientNormalization;
  }

  public double getGradientNormalizationThreshold() {
    return gradientNormalizationThreshold;
  }

  public void setGradientNormalizationThreshold(double gradientNormalizationThreshold) {
    this.gradientNormalizationThreshold = gradientNormalizationThreshold;
  }

}
