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

/**
 * Interface for sentiment analyzers.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface SentimentAnalyzer {

  /**
   * Determine the sentiment of the input text.
   * @param request The sentiment analysis {@link SentimentAnalysisRequest request}.
   * @return A {@link Sentiment}.
   * @throws SentimentAnalysisException Thrown if the sentiment analysis fails.
   */
  public Sentiment analyze(SentimentAnalysisRequest request) throws SentimentAnalysisException;

  /**
   * Gets the label of the sentiment.
   * @return The label of the sentiment.
   */
  public String getSentimentLabel();

  /**
   * Gets the name of the sentiment analyzer.
   * @return The name of the sentiment analyzer.
   */
  public String getName();

}
