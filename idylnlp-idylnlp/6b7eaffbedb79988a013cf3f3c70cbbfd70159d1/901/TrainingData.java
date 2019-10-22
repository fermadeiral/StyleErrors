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

public class TrainingData {

  @SerializedName("Format")
  @Expose
  private String format;

  @SerializedName("InputFile")
  @Expose
  private String inputFile;

  @SerializedName("AnnotationsFile")
  @Expose
  private String annotationsFile;

  @SerializedName("WordVectorsFile")
  @Expose
  private String wordVectorsFile;

  public TrainingData() {

  }

  public TrainingData(String format, String inputFile, String wordVectorsFile) {

    this.format = format;
    this.inputFile = inputFile;
    this.wordVectorsFile = wordVectorsFile;

  }

  public TrainingData(String format, String inputFile, String annotationsFile, String wordVectorsFile) {

    this.format = format;
    this.inputFile = inputFile;
    this.annotationsFile = annotationsFile;
    this.wordVectorsFile = wordVectorsFile;

  }

  @Override
    public boolean equals(Object o) {
      return EqualsBuilder.reflectionEquals(this, o);
    }

    @Override
    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }

  public String getFormat() {
    return format;
  }

  public void setFormat(String format) {
    this.format = format;
  }

  public String getInputFile() {
    return inputFile;
  }

  public void setInputFile(String inputFile) {
    this.inputFile = inputFile;
  }

  public String getAnnotationsFile() {
    return annotationsFile;
  }

  public void setAnnotationsFile(String annotationsFile) {
    this.annotationsFile = annotationsFile;
  }

  public String getWordVectorsFile() {
    return wordVectorsFile;
  }

  public void setWordVectorsFile(String wordVectorsFile) {
    this.wordVectorsFile = wordVectorsFile;
  }

}
