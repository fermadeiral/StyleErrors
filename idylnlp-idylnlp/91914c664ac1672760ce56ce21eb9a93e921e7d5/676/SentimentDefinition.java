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
package ai.idylnlp.model.nlp.sentiment;

import ai.idylnlp.model.nlp.Stemmer;

/**
 * A definition of a sentiment.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SentimentDefinition {

  private String sentimentLabel;
  private String fileName;
  private String fullFilePath;
  private boolean enableFuzzyMatching;
  private boolean enableStemming;
  private Stemmer stemmer;

  public SentimentDefinition(String sentimentLabel, String fileName, String fullFilePath) {

    this.sentimentLabel = sentimentLabel;
    this.fileName = fileName;
    this.fullFilePath = fullFilePath;
    this.enableFuzzyMatching = false;
    this.enableStemming = false;

  }

  public SentimentDefinition(String sentimentLabel, String fileName, String fullFilePath, boolean enableFuzzyMatching, boolean enableStemming, Stemmer stemmer) {

    this.sentimentLabel = sentimentLabel;
    this.fileName = fileName;
    this.fullFilePath = fullFilePath;
    this.enableFuzzyMatching = enableFuzzyMatching;
    this.enableStemming = enableStemming;
    this.stemmer = stemmer;

  }

  public SentimentDefinition(String sentimentLabel, String fileName, String fullFilePath, boolean enableFuzzyMatching, int maxFuzziness, boolean enableStemming, Stemmer stemmer) {

    this.sentimentLabel = sentimentLabel;
    this.fileName = fileName;
    this.fullFilePath = fullFilePath;
    this.enableFuzzyMatching = enableFuzzyMatching;
    this.enableStemming = enableStemming;
    this.stemmer = stemmer;

  }

  @Override
  public String toString() {

    return String.format("Sentiment Label: %s; Definition File: %s", sentimentLabel, fileName);

  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public boolean isEnableFuzzyMatching() {
    return enableFuzzyMatching;
  }

  public void setEnableFuzzyMatching(boolean enableFuzzyMatching) {
    this.enableFuzzyMatching = enableFuzzyMatching;
  }

  public String getSentimentLabel() {
    return sentimentLabel;
  }

  public void setSentimentLabel(String sentimentLabel) {
    this.sentimentLabel = sentimentLabel;
  }

  public String getFullFilePath() {
    return fullFilePath;
  }

  public void setFullFilePath(String fullFilePath) {
    this.fullFilePath = fullFilePath;
  }

  public boolean isEnableStemming() {
    return enableStemming;
  }

  public void setEnableStemming(boolean enableStemming) {
    this.enableStemming = enableStemming;
  }

  public Stemmer getStemmer() {
    return stemmer;
  }

  public void setStemmer(Stemmer stemmer) {
    this.stemmer = stemmer;
  }

}
