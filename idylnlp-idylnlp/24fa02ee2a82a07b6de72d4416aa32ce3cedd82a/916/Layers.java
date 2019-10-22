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

public class Layers {

  @SerializedName("Layer1")
  @Expose
  private Layer layer1;

  @SerializedName("Layer2")
  @Expose
  private Layer layer2;

  public Layers() {

  }

  public Layers(Layer layer1, Layer layer2) {

    this.layer1 = layer1;
    this.layer2 = layer2;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public Layer getLayer1() {
    return layer1;
  }

  public void setLayer1(Layer layer1) {
    this.layer1 = layer1;
  }

  public Layer getLayer2() {
    return layer2;
  }

  public void setLayer2(Layer layer2) {
    this.layer2 = layer2;
  }

}
