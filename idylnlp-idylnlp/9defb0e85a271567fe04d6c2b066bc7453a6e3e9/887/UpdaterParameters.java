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
import org.deeplearning4j.nn.conf.Updater;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdaterParameters {

  @SerializedName("Updater")
  @Expose
  private String updater;

  @SerializedName("Epsilon")
  @Expose
  private Integer epsilon;

  @SerializedName("Decay")
  @Expose
  private Integer decay;

  public UpdaterParameters() {

  }

  public UpdaterParameters(String updater, int epsilon, int decay, int learningRate) {

    this.updater = updater;
    this.epsilon = epsilon;
    this.decay = decay;

  }

  public Updater getUpdaterParam() {

    if(StringUtils.equalsIgnoreCase(updater, "adadelta")) {

      return Updater.ADADELTA;

    } else if(StringUtils.equalsIgnoreCase(updater, "adagrad")) {

      return Updater.ADAGRAD;

    } else if(StringUtils.equalsIgnoreCase(updater, "adam")) {

      return Updater.ADAM;

    } else if(StringUtils.equalsIgnoreCase(updater, "nesterovs")) {

      return Updater.NESTEROVS;

    } else if(StringUtils.equalsIgnoreCase(updater, "none")) {

      return Updater.NONE;

    } else if(StringUtils.equalsIgnoreCase(updater, "rmsprop")) {

      return Updater.RMSPROP;

    } else if(StringUtils.equalsIgnoreCase(updater, "sgd")) {

      return Updater.SGD;

    } else {

      return Updater.NONE;

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

  public String getUpdater() {
    return updater;
  }

  public void setUpdater(String updater) {
    this.updater = updater;
  }

  public Integer getEpsilon() {
    return epsilon;
  }

  public void setEpsilon(Integer epsilon) {
    this.epsilon = epsilon;
  }

  public Integer getDecay() {
    return decay;
  }

  public void setDecay(Integer decay) {
    this.decay = decay;
  }

}
