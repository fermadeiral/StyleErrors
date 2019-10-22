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
package ai.idylnlp.model.nlp.sentiment;

/**
 * Represents a sentiment.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class Sentiment {

  private String sentimentName;
  private int sentimentValue;

  /**
   * Creates a new Sentiment.
   * @param sentimentName The name the sentiment.
   * @param sentimentValue The value of the sentiment.
   */
  public Sentiment(String sentimentName, int sentimentValue) {

    this.sentimentName = sentimentName;
    this.sentimentValue = sentimentValue;

  }

  /**
   * Gets the name of the sentiment.
   * @return The name of the sentiment.
   */
  public String getSentimentName() {
    return sentimentName;
  }

  /**
   * Gets the integer value of the sentiment.
   * @return The integer value of the sentiment.
   */
  public int getSentimentValue() {
    return sentimentValue;
  }

}
