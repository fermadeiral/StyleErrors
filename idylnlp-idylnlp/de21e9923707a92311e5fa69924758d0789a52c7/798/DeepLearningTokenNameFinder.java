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
package ai.idylnlp.nlp.recognizer.deep;

import java.util.Arrays;
import java.util.List;

import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.indexing.NDArrayIndex;

import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.util.Span;

/**
 * An implementation of OpenNLP's {@link TokenNameFinder} that
 * performs entity extraction via a deeplearning4j neural
 * network.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningTokenNameFinder implements TokenNameFinder {

  private final MultiLayerNetwork network;
  private final WordVectors wordVectors;
  private int windowSize;
  private String[] labels;

  /**
   * Creates a new token name finder.
   * @param network The neural {@link MultiLayerNetwork network}.
   * @param wordVectors The word {@link WordVectors vectors}.
   * @param windowSize The size of the window.
   * @param labels An array of outcome labels.
   */
  public DeepLearningTokenNameFinder(MultiLayerNetwork network, WordVectors wordVectors,
      int windowSize, String[] labels) {

    this.network = network;
    this.wordVectors = wordVectors;
    this.windowSize = windowSize;
    this.labels = labels;

  }

  @Override
  public Span[] find(String[] tokens) {

      List<INDArray> featureMatrices = DeepLearningUtils.mapToFeatureMatrices(wordVectors, tokens, windowSize);

      String[] outcomes = new String[tokens.length];
      for (int i = 0; i < tokens.length; i++) {
        INDArray predictionMatrix = network.output(featureMatrices.get(i), false);
        INDArray outcomeVector = predictionMatrix.get(NDArrayIndex.point(0), NDArrayIndex.all(),
            NDArrayIndex.point(windowSize - 1));

        outcomes[i] = labels[max(outcomeVector)];
      }

      // Delete invalid spans ...
      for (int i = 0; i < outcomes.length; i++) {
        if (outcomes[i].endsWith("cont") && (i == 0 || "other".equals(outcomes[i - 1]))) {
          outcomes[i] = "other";
        }
      }

      return new BioCodec().decode(Arrays.asList(outcomes));

  }

  @Override
  public void clearAdaptiveData() {
    // There is nothing to clear.
  }

  /**
   * Finds the index of the largest element in the {@link INDArray}.
   * @param array The {@link INDArray}.
   * @return The index of the item having the largest
   * element in the array.
   */
  // TODO: This function needs tested.
  private int max(INDArray array) {

    int best = 0;
    for (int i = 0; i < array.size(0); i++) {

      if (array.getDouble(i) > array.getDouble(best)) {
        best = i;
      }
    }

    return best;

  }

}
