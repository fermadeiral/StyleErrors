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
package ai.idylnlp.test.nlp.utils.ngrams;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.LinkedList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.nlp.utils.ngrams.NgramIterator;

public class NgramIteratorTest {

  private static final Logger LOGGER = LogManager.getLogger(NgramIteratorTest.class);

  @Test
  public void test1() {

    String[] tokens = {"George", "Washington", "was", "president"};

    NgramIterator i = new NgramIterator(tokens, 2);

    Collection<String> ngrams = new LinkedList<String>();

    while(i.hasNext()) {

      String ngram = i.next();

      ngrams.add(ngram);

      LOGGER.info(ngram);

    }

    assertEquals(3, ngrams.size());

    assertTrue(ngrams.contains("George Washington"));
    assertTrue(ngrams.contains("Washington was"));
    assertTrue(ngrams.contains("was president"));

  }

}
