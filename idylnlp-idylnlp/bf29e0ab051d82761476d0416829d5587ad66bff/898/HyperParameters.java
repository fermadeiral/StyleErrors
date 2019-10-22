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
import org.deeplearning4j.nn.conf.ConvolutionMode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HyperParameters {

  @SerializedName("WindowSize")
  @Expose
  private int windowSize = 15;

  @SerializedName("Seed")
  @Expose
  private long seed = 1497630814976308L;

  @SerializedName("Epochs")
  @Expose
  private int epochs = 1;

  @SerializedName("BatchSize")
  @Expose
  private int batchSize = 1;

  @SerializedName("ConvolutionMode")
  @Expose
  private String convolutionMode = "truncate";

  @SerializedName("NetworkConfigurationParameters")
  @Expose
  private NetworkConfigurationParameters networkConfigurationParameters;

  public ConvolutionMode getConvolutionModeParam() {

    if(StringUtils.equalsIgnoreCase(convolutionMode, "truncate")) {

      return ConvolutionMode.Truncate;

    } else if(StringUtils.equalsIgnoreCase(convolutionMode, "same")) {

      return ConvolutionMode.Same;

    } else if(StringUtils.equalsIgnoreCase(convolutionMode, "strict")) {

      return ConvolutionMode.Strict;

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

  public int getWindowSize() {
    return windowSize;
  }

  public void setWindowSize(int windowSize) {
    this.windowSize = windowSize;
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }

  public NetworkConfigurationParameters getNetworkConfigurationParameters() {
    return networkConfigurationParameters;
  }

  public void setNetworkConfigurationParameters(NetworkConfigurationParameters networkConfigurationParameters) {
    this.networkConfigurationParameters = networkConfigurationParameters;
  }

  public int getEpochs() {
    return epochs;
  }

  public void setEpochs(int eoochs) {
    this.epochs = eoochs;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public String getConvolutionMode() {
    return convolutionMode;
  }

  public void setConvolutionMode(String convolutionMode) {
    this.convolutionMode = convolutionMode;
  }

}
