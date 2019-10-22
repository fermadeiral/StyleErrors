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
package ai.idylnlp.model.nlp.documents;

import java.util.List;

import com.neovisionaries.i18n.LanguageCode;

/**
 * A deep learning document classifier training request.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningDocumentClassifierTrainingRequest extends DocumentClassifierTrainingRequest {

  // Defines minimal word frequency in training corpus. All words below this threshold will be removed prior model training.
  private int minWordFrequency = 1;

  // Defines number of dimensions for output vectors
  private int layerSize = 100;

  // Number of examples in each minibatch
    private int batchSize = 64;

  // Number of epochs (full passes of training data) to train on
    private int epochs = 1;

  // Truncate reviews with length (# words) greater than this
    private int truncateToLength = 256;

    private double learningRate = 0.025;
    private double minLearningRate = 0.001;

    private LanguageCode languageCode;
  private List<String> directories;

  public DeepLearningDocumentClassifierTrainingRequest() {

  }

  public DeepLearningDocumentClassifierTrainingRequest(LanguageCode languageCode, List<String> directories) {

    this.languageCode = languageCode;
    this.setDirectories(directories);

  }

  public LanguageCode getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(LanguageCode languageCode) {
    this.languageCode = languageCode;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  public int getTruncateToLength() {
    return truncateToLength;
  }

  public void setTruncateToLength(int truncateToLength) {
    this.truncateToLength = truncateToLength;
  }

  public int getEpochs() {
    return epochs;
  }

  public void setEpochs(int epochs) {
    this.epochs = epochs;
  }

  public List<String> getDirectories() {
    return directories;
  }

  public void setDirectories(List<String> directories) {
    this.directories = directories;
  }

  public double getLearningRate() {
    return learningRate;
  }

  public void setLearningRate(double learningRate) {
    this.learningRate = learningRate;
  }

  public double getMinLearningRate() {
    return minLearningRate;
  }

  public void setMinLearningRate(double minLearningRate) {
    this.minLearningRate = minLearningRate;
  }

  public int getMinWordFrequency() {
    return minWordFrequency;
  }

  public void setMinWordFrequency(int minWordFrequency) {
    this.minWordFrequency = minWordFrequency;
  }

  public int getLayerSize() {
    return layerSize;
  }

  public void setLayerSize(int layerSize) {
    this.layerSize = layerSize;
  }

}
