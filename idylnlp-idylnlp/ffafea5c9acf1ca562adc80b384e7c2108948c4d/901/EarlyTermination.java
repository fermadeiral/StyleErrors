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

public class EarlyTermination {

  @SerializedName("MaxEpochs")
  @Expose
  private Integer maxEpochs;

  @SerializedName("MaxMinutes")
  @Expose
  private Integer maxMinutes;

  public EarlyTermination() {

  }

  public EarlyTermination(Integer maxEpochs, Integer maxMinutes) {

    this.maxEpochs = maxEpochs;
    this.maxMinutes = maxMinutes;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public Integer getMaxEpochs() {
    return maxEpochs;
  }

  public void setMaxEpochs(Integer maxEpochs) {
    this.maxEpochs = maxEpochs;
  }

  public Integer getMaxMinutes() {
    return maxMinutes;
  }

  public void setMaxMinutes(Integer maxMinutes) {
    this.maxMinutes = maxMinutes;
  }

}
