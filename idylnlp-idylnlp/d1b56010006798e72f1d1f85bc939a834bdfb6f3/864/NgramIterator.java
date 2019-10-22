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
package ai.idylnlp.nlp.utils.ngrams;

import java.util.Iterator;

/**
 * An implementation of {@link Iterator} that produces N-Grams.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class NgramIterator implements Iterator<String> {

  private String[] tokens;
  private int pos = 0, n;

  /**
   * Creates a new N-gram iterator.
   * @param tokens The tokens.
   * @param n The size of the n-grams.
   */
  public NgramIterator(String[] tokens, int n) {
    this.tokens = tokens;
    this.n = n;
  }

  @Override
  public boolean hasNext() {
    return pos < tokens.length - n + 1;
  }

  @Override
  public String next() {

    StringBuilder sb = new StringBuilder();

    for (int i = pos; i < pos + n; i++) {
      sb.append((i > pos ? " " : "") + tokens[i]);
    }

    pos++;

    return sb.toString();

  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}
