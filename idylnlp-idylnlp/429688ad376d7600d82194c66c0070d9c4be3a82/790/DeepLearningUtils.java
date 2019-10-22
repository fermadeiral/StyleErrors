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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.indexing.INDArrayIndex;
import org.nd4j.linalg.indexing.NDArrayIndex;

import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameSample;

/**
 * Utility functions for deep learning model training and evaluation.
 *
 * Note: The functions in this class are not thread-safe.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningUtils {

  public synchronized static List<INDArray> mapToLabelVectors(NameSample sample, int windowSize, String[] labelStrings) {
	  
    Map<String, Integer> labelToIndex = IntStream.range(0, labelStrings.length).boxed()
        .collect(Collectors.toMap(i -> labelStrings[i], i -> i));

    List<INDArray> vectors = new ArrayList<INDArray>();

    // encode the outcome as one-hot-representation
    String outcomes[] = new BioCodec().encode(sample.getNames(), sample.getSentence().length);
    
    for (int i = 0; i < sample.getSentence().length; i++) {

      INDArray labels = Nd4j.create(1, labelStrings.length, windowSize);
      labels.putScalar(new int[] { 0, labelToIndex.get(outcomes[i]), windowSize - 1 }, 1.0d);
      vectors.add(labels);

    }

    return vectors;

  }

  public synchronized static List<INDArray> mapToFeatureMatrices(WordVectors wordVectors, String[] tokens, int windowSize) {

    List<INDArray> matrices = new ArrayList<>();

    final int vectorSize = wordVectors.getWordVector(wordVectors.vocab().wordAtIndex(0)).length;

    for (int i = 0; i < tokens.length; i++) {

      INDArray features = Nd4j.create(1, vectorSize, windowSize);

      for(int vectorIndex = 0; vectorIndex < windowSize; vectorIndex++) {

        int tokenIndex = i + vectorIndex - ((windowSize - 1) / 2);

        if (tokenIndex >= 0 && tokenIndex < tokens.length) {

          String token = tokens[tokenIndex];

          if (wordVectors.hasWord(token)) {

            INDArray vector = wordVectors.getWordVectorMatrix(token);
            features.put(new INDArrayIndex[] { NDArrayIndex.point(0), NDArrayIndex.all(),
                NDArrayIndex.point(vectorIndex) }, vector);

          }

        }

      }

      matrices.add(features);

    }

    return matrices;

  }

}
