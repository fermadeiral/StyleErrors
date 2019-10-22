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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RegularizationParameters {

  @SerializedName("Regularization")
  @Expose
  private boolean regularization = false;

  @SerializedName("L1")
  @Expose
  private Double l1 = Double.NaN;

  @SerializedName("L1Bias")
  @Expose
  private Double l1Bias = Double.NaN;

  @SerializedName("L2")
  @Expose
  private Double l2 = Double.NaN;

  @SerializedName("L2Bias")
  @Expose
  private Double l2Bias = Double.NaN;

  public RegularizationParameters() {

  }

  public RegularizationParameters(boolean regularization, Double l1, Double l1Bias, Double l2, Double l2Bias) {

    this.regularization = regularization;
    this.l1 = l1;
    this.l1Bias = l1Bias;
    this.l2 = l2;
    this.l2Bias = l2Bias;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public boolean getRegularization() {
    return regularization;
  }

  public void setRegularization(boolean regularization) {
    this.regularization = regularization;
  }

  public Double getL1() {
    return l1;
  }

  public void setL1(Double l1) {
    this.l1 = l1;
  }

  public Double getL1Bias() {
    return l1Bias;
  }

  public void setL1Bias(Double l1Bias) {
    this.l1Bias = l1Bias;
  }

  public Double getL2() {
    return l2;
  }

  public void setL2(Double l2) {
    this.l2 = l2;
  }

  public Double getL2Bias() {
    return l2Bias;
  }

  public void setL2Bias(Double l2Bias) {
    this.l2Bias = l2Bias;
  }

}
