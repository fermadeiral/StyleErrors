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
package ai.idylnlp.nlp.utils;

import java.util.Arrays;
import java.util.Collections;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

/**
 * Utility functions for OpenNLP's {@link Span}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SpanUtils {

  private SpanUtils() {
    // This is a utility class.
  }

  /**
   * Gets a {@link Span span} for an entity in a text.
   * @param tokenizer The {@link Tokenizer tokenizer}.
   * @param entity The text of the entity.
   * @param text The text containing the entity.
   * @return A {@link Span span} of the entity in the text, or <code>null</code> if no span is found.
   */
  public static Span getSpan(Tokenizer tokenizer, String entity, String text) {

    // TODO: If the entity appears more than once in the text only the first one will be found.

    // Tokenize the entity.
    final String entityTokenizerLine[] = tokenizer.tokenize(entity);

    // Tokenize the text.
    final String whitespaceTokenizerLine[] = tokenizer.tokenize(text);

    // Find the entity tokens in the text tokens.
    final int start = Collections.indexOfSubList(Arrays.asList(whitespaceTokenizerLine), Arrays.asList(entityTokenizerLine));

    if(start > -1) {

      int end = start + entityTokenizerLine.length;

      return new Span(start, end);

    } else {

      return null;

    }

  }

}
