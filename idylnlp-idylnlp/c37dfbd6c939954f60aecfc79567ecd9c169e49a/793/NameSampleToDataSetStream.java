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
package ai.idylnlp.nlp.recognizer.deep;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.dataset.DataSet;

import opennlp.tools.namefind.NameSample;
import opennlp.tools.util.FilterObjectStream;
import opennlp.tools.util.ObjectStream;

public class NameSampleToDataSetStream extends FilterObjectStream<NameSample, DataSet> {

    private final WordVectors wordVectors;
    private final String[] labels;
    private int windowSize;
    private int vectorSize;

    private Iterator<DataSet> dataSets = Collections.emptyListIterator();

    public NameSampleToDataSetStream(ObjectStream<NameSample> samples, WordVectors wordVectors, int windowSize, int vectorSize, String[] labels) {

      super(samples);

      this.wordVectors = wordVectors;
      this.windowSize = windowSize;
      this.vectorSize = vectorSize;
      this.labels = labels;

    }

    @Override
    public final DataSet read() throws IOException {

      if(dataSets.hasNext()) {

        return dataSets.next();

      } else {

        NameSample sample;

        while (!dataSets.hasNext() && (sample = samples.read()) != null) {
          dataSets = createDataSets(sample);
        }

        if(dataSets.hasNext()) {
          return read();
        }

      }

      return null;

    }

    private Iterator<DataSet> createDataSets(NameSample sample) {

      TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

        String s = String.join(" ", sample.getSentence());
        List<String> tokens = tokenizerFactory.create(s).getTokens();

        String[] t = tokens.toArray(new String[tokens.size()]);

        // sample and t are different tokens at this point due to removing punctuation

        /*System.out.println("t = " + t.length);
        System.out.println(String.join(" ", t));
        System.out.println("sample = " + sample.getSentence().length);
        System.out.println(String.join(" ", sample.getSentence()));
        System.out.println("--------");*/

        List<INDArray> features = DeepLearningUtils.mapToFeatureMatrices(wordVectors, t, windowSize);
        List<INDArray> labels = DeepLearningUtils.mapToLabelVectors(sample, windowSize, this.labels);

        List<DataSet> dataSetList = new ArrayList<>();

        for (int i = 0; i < features.size(); i++) {
          dataSetList.add(new DataSet(features.get(i), labels.get(i)));
        }

        return dataSetList.iterator();

  }

}
