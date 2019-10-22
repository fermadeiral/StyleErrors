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

public class SparkTraining {

  @SerializedName("AveragingFrequency")
  @Expose
  private int averagingFrequency = 5;

  @SerializedName("BatchSizePerWorker")
  @Expose
  private int batchSizePerWorker = 32;

  @SerializedName("WorkerPrefetchNumBatches")
  @Expose
  private int workerPrefetchNumBatches = 2;

  @SerializedName("EnableSparkTraining")
  @Expose
  private boolean enableSparkTraining = false;

  @SerializedName("Master")
  @Expose
  private String master = "local[*]";

  public SparkTraining() {

  }

  public SparkTraining(int averagingFrequency, int batchSizePerWorker, int workerPrefetchNumBatches,
      String master) {

    this.averagingFrequency = averagingFrequency;
    this.batchSizePerWorker = batchSizePerWorker;
    this.workerPrefetchNumBatches = workerPrefetchNumBatches;
    this.enableSparkTraining = true;
    this.master = master;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public int getAveragingFrequency() {
    return averagingFrequency;
  }

  public void setAveragingFrequency(int averagingFrequency) {
    this.averagingFrequency = averagingFrequency;
  }

  public int getBatchSizePerWorker() {
    return batchSizePerWorker;
  }

  public void setBatchSizePerWorker(int batchSizePerWorker) {
    this.batchSizePerWorker = batchSizePerWorker;
  }

  public int getWorkerPrefetchNumBatches() {
    return workerPrefetchNumBatches;
  }

  public void setWorkerPrefetchNumBatches(int workerPrefetchNumBatches) {
    this.workerPrefetchNumBatches = workerPrefetchNumBatches;
  }

  public boolean isEnableSparkTraining() {
    return enableSparkTraining;
  }

  public void setEnableSparkTraining(boolean enableSparkTraining) {
    this.enableSparkTraining = enableSparkTraining;
  }

  public String getMaster() {
    return master;
  }

  public void setMaster(String master) {
    this.master = master;
  }

}
