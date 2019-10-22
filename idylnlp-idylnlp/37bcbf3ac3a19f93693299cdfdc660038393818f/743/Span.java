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
package ai.idylnlp.model.nlp;

public class Span {

  private int start;
  private int end;
  private double prob = 0;
  private String type;

  /**
   * Initializes a new Span Object. Sets the prob to 0 as default.
   *
   * @param s
   *            start of span.
   * @param e
   *            end of span, which is +1 more than the last element in the
   *            span.
   * @param type
   *            the type of the span
   */
  public Span(int s, int e, String type) {

    if (s < 0) {
      throw new IllegalArgumentException("start index must be zero or greater: " + s);
    }
    if (e < 0) {
      throw new IllegalArgumentException("end index must be zero or greater: " + e);
    }
    if (s > e) {
      throw new IllegalArgumentException(
          "start index must not be larger than end index: " + "start=" + s + ", end=" + e);
    }

    start = s;
    end = e;
    this.type = type;
    this.prob = 0d;
  }

  public Span(int s, int e, String type, double prob) {

    if (s < 0) {
      throw new IllegalArgumentException("start index must be zero or greater: " + s);
    }
    if (e < 0) {
      throw new IllegalArgumentException("end index must be zero or greater: " + e);
    }
    if (s > e) {
      throw new IllegalArgumentException(
          "start index must not be larger than end index: " + "start=" + s + ", end=" + e);
    }

    start = s;
    end = e;
    this.prob = prob;
    this.type = type;
  }

  /**
   * Initializes a new Span Object. Sets the prob to 0 as default
   *
   * @param s
   *            start of span.
   * @param e
   *            end of span.
   */
  public Span(int s, int e) {
    this(s, e, null, 0d);
  }

  /**
   *
   * @param s
   *            the start of the span (the token index, not the char index)
   * @param e
   *            the end of the span (the token index, not the char index)
   * @param prob The probability of the span.
   */
  public Span(int s, int e, double prob) {
    this(s, e, null, prob);
  }

  /**
   * Initializes a new Span object with an existing Span which is shifted by
   * an offset.
   *
   * @param span The {@link Span}.
   * @param offset Shift the span by this offset.
   */
  public Span(Span span, int offset) {
    this(span.start + offset, span.end + offset, span.getType(), span.getProb());
  }

  /**
   * Creates a new immutable span based on an existing span, where the
   * existing span did not include the prob
   *
   * @param span
   *            the span that has no prob or the prob is incorrect and a new
   *            Span must be generated
   * @param prob
   *            the probability of the span
   */
  public Span(Span span, double prob) {
    this(span.start, span.end, span.getType(), prob);
  }

  public static String[] spansToStrings(Span[] spans, CharSequence s) {
    String[] tokens = new String[spans.length];

    for (int si = 0, sl = spans.length; si < sl; si++) {
      tokens[si] = spans[si].getCoveredText(s).toString();
    }

    return tokens;
  }

  public CharSequence getCoveredText(CharSequence text) {
    if (getEnd() > text.length()) {
      throw new IllegalArgumentException(
          "The span " + toString() + " is outside the given text which has length " + text.length() + "!");
    }

    return text.subSequence(getStart(), getEnd());
  }

  public int getStart() {
    return start;
  }

  public int getEnd() {
    return end;
  }

  public String getType() {
    return type;
  }

  public double getProb() {
    return prob;
  }

}
