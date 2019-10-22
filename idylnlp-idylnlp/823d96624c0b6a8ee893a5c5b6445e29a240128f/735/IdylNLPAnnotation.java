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
package ai.idylnlp.model.nlp.annotation;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * An Idyl NLP annotation.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class IdylNLPAnnotation {

  private int lineNumber;
  private int tokenStart;
  private int tokenEnd;
  private String type;

  /**
   * Creates a new annotation.
   * @param lineNumber The line number in the text that contains the entity.
   * @param tokenStart The token-based start position of the entity.
   * @param tokenEnd THe token-based end position of the entity.
   * @param type The type of entity.
   */
  public IdylNLPAnnotation(int lineNumber, int tokenStart, int tokenEnd, String type) {

    this.lineNumber = lineNumber;
    this.tokenStart = tokenStart;
    this.tokenEnd = tokenEnd;
    this.type = type;

  }

  /**
   * Creates a new annotation.
   */
  public IdylNLPAnnotation() {

  }

  @Override
  public String toString() {

    return ToStringBuilder.reflectionToString(this);

  }

  public int getTokenStart() {
    return tokenStart;
  }

  public void setTokenStart(int tokenStart) {
    this.tokenStart = tokenStart;
  }

  public int getTokenEnd() {
    return tokenEnd;
  }

  public void setTokenEnd(int tokenEnd) {
    this.tokenEnd = tokenEnd;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public int getLineNumber() {
    return lineNumber;
  }

  public void setLineNumber(int lineNumber) {
    this.lineNumber = lineNumber;
  }

}
