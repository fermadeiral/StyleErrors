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

package ai.idylnlp.opennlp.custom.formats;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Span;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import ai.idylnlp.model.nlp.annotation.AnnotationReader;
import ai.idylnlp.model.nlp.annotation.IdylNLPAnnotation;

/**
 * Implementation of {@link ObjectStream} that reads text that is annotated
 * in the Idyl NLP format.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class IdylNLPNameSampleStream implements ObjectStream<NameSample> {

  private final ObjectStream<String> lineStream;
  private final AnnotationReader annotationReader;

  private int lineNumber = 1;

  public IdylNLPNameSampleStream(ObjectStream<String> lineStream, AnnotationReader annotationReader) {

    this.lineStream = lineStream;
    this.annotationReader = annotationReader;

  }

  @Override
  public NameSample read() throws IOException {

    final List<String> sentences = new LinkedList<>();

    final String line = lineStream.read();

    lineNumber++;

    if(line != null && !StringUtils.isEmpty(line.trim())) {

      // TODO: Should this tokenizer be customizable?
      for(String token : WhitespaceTokenizer.INSTANCE.tokenize(line)) {
        sentences.add(token);
      }

    }

    if (sentences.size() > 0) {

      final List<Span> names = new LinkedList<>();

      // It is lineNumber - 1 here because we have already incremented the line number above.
      Collection<IdylNLPAnnotation> annotations = annotationReader.getAnnotations(lineNumber - 1);

      if(CollectionUtils.isNotEmpty(annotations)) {

        for(IdylNLPAnnotation annotation : annotations) {

          Span span = new Span(annotation.getTokenStart(), annotation.getTokenEnd(), annotation.getType());
          names.add(span);

        }

      }

      return new NameSample(sentences.toArray(new String[sentences.size()]), names.toArray(new Span[names.size()]), true);

    } else if (line != null) {
        // Just filter out empty events, if two lines in a row are empty
        return read();
      }
      else {
        // source stream is not returning anymore lines
        return null;
      }

  }

  @Override
  public void reset() throws IOException, UnsupportedOperationException {
    lineStream.reset();
    lineNumber = 1;
  }

  @Override
  public void close() throws IOException {
    lineStream.close();
  }

}
