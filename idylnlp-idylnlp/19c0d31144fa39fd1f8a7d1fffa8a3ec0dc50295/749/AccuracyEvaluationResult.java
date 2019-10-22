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
package ai.idylnlp.model.training;

/**
 * Contains evaluation results for models whose evaluations
 * are based on accuracy (such as lemma and part of speech models).
 * This class extends {@link EvaluationResult}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class AccuracyEvaluationResult extends EvaluationResult {

  private double wordAccuracy;
  private long wordCount;

  /**
   * Creates a new result.
   * @param wordAccuracy The word accuracy value.
   * @param wordCount The count of words.
   */
  public AccuracyEvaluationResult(double wordAccuracy, long wordCount) {

    this.wordAccuracy = wordAccuracy;
    this.wordCount = wordCount;

  }

  @Override
  public String toString() {

    return "Word Accuracy: " + wordAccuracy + "; Word Count: " + wordCount;

  }

  /**
   * Gets the word accuracy.
   * @return The word accuracy.
   */
  public double getWordAccuracy() {
    return wordAccuracy;
  }

  /**
   * Sets the word accuracy.
   * @param wordAccuracy The word accuracy.
   */
  public void setWordAccuracy(double wordAccuracy) {
    this.wordAccuracy = wordAccuracy;
  }

  /**
   * Gets the word count.
   * @return The word count.
   */
  public long getWordCount() {
    return wordCount;
  }

  /**
   * Sets the word count.
   * @param wordCount The word count.
   */
  public void setWordCount(long wordCount) {
    this.wordCount = wordCount;
  }

}
