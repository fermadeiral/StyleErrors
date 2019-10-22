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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import ai.idylnlp.nlp.utils.EnglishStopWordRemover;

public class StopWordRemoverTest {

  @Test
  public void isStopWordTest() {

    EnglishStopWordRemover remover = new EnglishStopWordRemover();

    assertTrue(remover.isStopWord("the"));
    assertTrue(remover.isStopWord("a"));
    assertTrue(remover.isStopWord("is"));
    assertTrue(remover.isStopWord("an"));
    assertFalse(remover.isStopWord("couch"));
    assertFalse(remover.isStopWord("chair"));

  }

  @Test
  public void removeStopWordsTest() {

    Collection<String> input = new LinkedList<String>();
    input.add("This");
    input.add("is");
    input.add("the");
    input.add("best");
    input.add("day");
    input.add("ever");

    EnglishStopWordRemover remover = new EnglishStopWordRemover();
    Collection<String> output = remover.removeStopWords(input);

    assertFalse(output.contains("this"));
    assertFalse(output.contains("is"));
    assertFalse(output.contains("the"));

  }

}
