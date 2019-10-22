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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Span;
import net.loomchild.segment.TextIterator;
import net.loomchild.segment.srx.SrxDocument;
import net.loomchild.segment.srx.SrxParser;
import net.loomchild.segment.srx.SrxTextIterator;
import net.loomchild.segment.srx.io.Srx2SaxParser;

/**
 * An implementation of {@link SentenceDetector} that performs
 * sentence detection using segmentation. 
 * 
 * Uses https://github.com/loomchild/segment.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SegmentedSentenceDetector implements SentenceDetector {

  private LanguageCode languageCode;
  private SrxDocument srxDocument;

  public SegmentedSentenceDetector(String srx, LanguageCode languageCode) throws UnsupportedEncodingException {

    this.languageCode = languageCode;

    final InputStream inputStream = new ByteArrayInputStream(srx.getBytes(StandardCharsets.UTF_8));

    BufferedReader srxReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

    Map<String, Object> parserParameters = new HashMap<>();
    parserParameters.put(Srx2SaxParser.VALIDATE_PARAMETER, true);
    SrxParser srxParser = new Srx2SaxParser(parserParameters);

    srxDocument = srxParser.parse(srxReader);

  }

  @Override
  public List<String> getLanguageCodes() {
    return Arrays.asList(languageCode.getAlpha3().toString());
  }

  @Override
  public String[] sentDetect(String s) {

    return Span.spansToStrings(sentPosDetect(s), s);

  }

  @Override
  public Span[] sentPosDetect(String s) {

    List<Span> spans = new ArrayList<>();

    List<String> sentences = tokenize(s);

    for(String sentence : sentences) {

      String trimmedSentence = sentence.trim();

      final int start = s.indexOf(trimmedSentence);

      Span span = new Span(start, start + trimmedSentence.length());
      spans.add(span);

    }

    return spans.toArray(new Span[spans.size()]);

  }

  private List<String> tokenize(String text) {

    List<String> segments = new ArrayList<>();

    TextIterator textIterator = new SrxTextIterator(srxDocument, languageCode.getAlpha3().toString(), text);

    while(textIterator.hasNext()) {

      segments.add(textIterator.next().trim());

    }

    return segments;

  }

}
