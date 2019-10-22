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
package ai.idylnlp.test.nlp.tokenizers;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.tokenize.TokenSample;
import opennlp.tools.tokenize.TokenSampleStream;
import opennlp.tools.tokenize.TokenizerFactory;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.CollectionObjectStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

public class ModelTokenizerTest {

  // NOTE: these tests were taken from OpenNLP's WhitespaceTokenizerTest class since
  // our WhitespaceTokenizer is just a pass-through to OpenNLP's WhitespaceTokenizer.

  @Test
  public void testTokenizerSimpleModel() throws IOException {

    TokenizerModel model = createSimpleMaxentTokenModel();

    TokenizerME tokenizer = new TokenizerME(model);

    String tokens[] = tokenizer.tokenize("test,");

    assertEquals(2, tokens.length);
    assertEquals("test", tokens[0]);
    assertEquals(",", tokens[1]);

  }

  @Test
  public void testTokenizer() throws IOException {

    TokenizerModel model = createMaxentTokenModel();

    TokenizerME tokenizer = new TokenizerME(model);

    String tokens[] = tokenizer.tokenize("Sounds like it's not properly thought through!");

    assertEquals(9, tokens.length);
    assertEquals("Sounds", tokens[0]);
    assertEquals("like", tokens[1]);
    assertEquals("it", tokens[2]);
    assertEquals("'s", tokens[3]);
    assertEquals("not", tokens[4]);
    assertEquals("properly", tokens[5]);
    assertEquals("thought", tokens[6]);
    assertEquals("through", tokens[7]);
    assertEquals("!", tokens[8]);

  }

  private TokenizerModel createSimpleMaxentTokenModel() throws IOException {

    List<TokenSample> samples = new ArrayList<TokenSample>();

    samples.add(new TokenSample("year", new Span[] { new Span(0, 4) }));
    samples.add(new TokenSample("year,", new Span[] { new Span(0, 4), new Span(4, 5) }));
    samples.add(new TokenSample("it,", new Span[] { new Span(0, 2), new Span(2, 3) }));
    samples.add(new TokenSample("it", new Span[] { new Span(0, 2) }));
    samples.add(new TokenSample("yes", new Span[] { new Span(0, 3) }));
    samples.add(new TokenSample("yes,", new Span[] { new Span(0, 3), new Span(3, 4) }));

    TokenizerFactory tokenizerFactory = new TokenizerFactory("en", new Dictionary(), false, null);

    TrainingParameters mlParams = new TrainingParameters();
    mlParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(100));
    mlParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(0));

    return TokenizerME.train(new CollectionObjectStream<TokenSample>(samples), tokenizerFactory, mlParams);

  }

  private TokenizerModel createMaxentTokenModel() throws IOException {

    final String trainingData = new File("src/test/resources/token.train").getAbsolutePath();

    InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(trainingData));
    ObjectStream<String> lineStream =  new PlainTextByLineStream(inputStreamFactory, "UTF-8");
    ObjectStream<TokenSample> sampleStream = new TokenSampleStream(lineStream);

    TrainingParameters mlParams = new TrainingParameters();
    mlParams.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(100));
    mlParams.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(0));

    TokenizerFactory tokenizerFactory = new TokenizerFactory("en", new Dictionary(), false, null);

    return TokenizerME.train(sampleStream, tokenizerFactory, mlParams);

  }

}
