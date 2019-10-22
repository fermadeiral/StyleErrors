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
package ai.idylnlp.nlp.sentence;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;

import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Span;

/**
 * An implementation of {@link SentenceDetector} that identifies
 * sentences based on the presence of periods. Usage is not
 * generally recommended.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SimpleSentenceDetector implements SentenceDetector {

  @Override
  public List<String> getLanguageCodes() {
    return Collections.emptyList();
  }

  @Override
  public Span[] sentPosDetect(String text) {

    List<Span> spans = new LinkedList<Span>();

    List<String> sentences = Arrays.asList(text.split("."));

    if(CollectionUtils.isEmpty(sentences)) {

      spans.add(new Span(0, text.length() - 1));

    } else {

      int lastPeriod = 0;

      for(String sentence : sentences) {

        int period = sentence.indexOf(".");

        spans.add(new Span(lastPeriod, period));

        lastPeriod = period + 1;

      }

    }

    return spans.toArray(new Span[spans.size()]);

  }

  @Override
  public String[] sentDetect(String text) {

    return text.split("\\.");

  }

}
