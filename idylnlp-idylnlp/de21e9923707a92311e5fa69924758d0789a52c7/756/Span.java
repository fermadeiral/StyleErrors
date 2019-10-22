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
package ai.idylnlp.model.entity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * A span in text identified by token and character indexes.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class Span {

  private int tokenStart;
  private int tokenEnd;

  /**
   * Creates a new span.
   * @param tokenStart The position of the start of the token's span.
   *
   */
  public Span(int tokenStart, int tokenEnd) {

    this.tokenStart = tokenStart;
    this.tokenEnd = tokenEnd;

  }

  /**
   * Returns the token-based span as a formatted string.
   * An example is: [3..5)
   */
  @Override
  public String toString() {

    return "[" + tokenStart + ".." + tokenEnd + ")";

  }

  /**
     * {@inheritDoc}
     */
  @Override
  public int hashCode() {

    return new HashCodeBuilder(17, 31)
      .append(tokenStart)
            .append(tokenEnd)
            .toHashCode();

  }

  /**
     * {@inheritDoc}
     */
  @Override
  public boolean equals(Object obj) {

      if(obj != null && obj instanceof Span) {

          final Span other = (Span) obj;

          return new EqualsBuilder()
              .append(tokenStart, other.tokenStart)
              .append(tokenEnd, other.tokenEnd)
              .isEquals();

      }

      return false;

  }

  /**
   * Gets the token's start of the span.
   * @return The token's start of the span.
   */
  public int getTokenStart() {
    return tokenStart;
  }

  /**
   * Gets the end of the span.
   * @return The end of the span.
   */
  public int getTokenEnd() {
    return tokenEnd;
  }

}