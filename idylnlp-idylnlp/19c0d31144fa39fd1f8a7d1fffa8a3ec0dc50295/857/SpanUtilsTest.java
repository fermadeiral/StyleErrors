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
package ai.idylnlp.test.nlp.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import ai.idylnlp.nlp.utils.SpanUtils;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.Span;

public class SpanUtilsTest {

  private final Tokenizer tokenizer = SimpleTokenizer.INSTANCE;

  @Test
  public void span1() {

    Span span = SpanUtils.getSpan(tokenizer, "Keanu Reeves", "The Matrix starred Keanu Reeves.");
    Span expectdSpan = new Span(3, 5);

    assertEquals(expectdSpan, span);

  }

  @Test
  public void span2() {

    Span span = SpanUtils.getSpan(tokenizer, "pontiac", "He was driving a blue pontiac.");
    Span expectdSpan = new Span(5, 6);

    assertEquals(expectdSpan, span);

  }

  @Test
  public void span3() {

    Span span = SpanUtils.getSpan(tokenizer, "was", "He was driving a blue pontiac.");
    Span expectdSpan = new Span(1, 2);

    assertEquals(expectdSpan, span);

  }

  @Test
  public void span4() {

    Span span = SpanUtils.getSpan(tokenizer, "He", "He was driving a blue pontiac.");
    Span expectdSpan = new Span(0, 1);

    assertEquals(expectdSpan, span);

  }

}
