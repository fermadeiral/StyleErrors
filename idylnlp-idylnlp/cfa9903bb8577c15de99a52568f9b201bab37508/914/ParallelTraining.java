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

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ParallelTraining {

  @SerializedName("PrefetchBuffer")
  @Expose
  private int prefetchBuffer = 2;

  @SerializedName("Workers")
  @Expose
  private int workers = 2;

  @SerializedName("AveragingFrequency")
  @Expose
  private int averagingFrequency = 3;

  @SerializedName("ReportScoreAfterAveraging")
  @Expose
  private boolean reportScoreAfterAveraging = true;

  @SerializedName("LegacyAveraging")
  @Expose
  private boolean legacyAveraging = false;

  public ParallelTraining() {

  }

  public ParallelTraining(int prefetchBuffer, int workers, int averagingFrequency,
      boolean reportScoreAfterAveraging, boolean legacyAveraging) {

    this.prefetchBuffer = prefetchBuffer;
    this.workers = workers;
    this.averagingFrequency = averagingFrequency;
    this.reportScoreAfterAveraging = reportScoreAfterAveraging;
    this.legacyAveraging = legacyAveraging;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public int getPrefetchBuffer() {
    return prefetchBuffer;
  }

  public void setPrefetchBuffer(int prefetchBuffer) {
    this.prefetchBuffer = prefetchBuffer;
  }

  public int getWorkers() {
    return workers;
  }

  public void setWorkers(int workers) {
    this.workers = workers;
  }

  public int getAveragingFrequency() {
    return averagingFrequency;
  }

  public void setAveragingFrequency(int averagingFrequency) {
    this.averagingFrequency = averagingFrequency;
  }

  public boolean isReportScoreAfterAveraging() {
    return reportScoreAfterAveraging;
  }

  public void setReportScoreAfterAveraging(boolean reportScoreAfterAveraging) {
    this.reportScoreAfterAveraging = reportScoreAfterAveraging;
  }

  public boolean isLegacyAveraging() {
    return legacyAveraging;
  }

  public void setLegacyAveraging(boolean legacyAveraging) {
    this.legacyAveraging = legacyAveraging;
  }

}
