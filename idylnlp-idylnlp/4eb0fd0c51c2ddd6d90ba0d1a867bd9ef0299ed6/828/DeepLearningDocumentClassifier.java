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
package ai.idylnlp.nlp.documents.dl4j;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.berkeley.Pair;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.documentiterator.LabelledDocument;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.api.ndarray.INDArray;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.manifest.DocumentModelManifest;
import ai.idylnlp.model.nlp.documents.DeepLearningDocumentClassificationRequest;
import ai.idylnlp.model.nlp.documents.DocumentClassificationEvaluationRequest;
import ai.idylnlp.model.nlp.documents.DocumentClassificationEvaluationResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassificationResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassificationScores;
import ai.idylnlp.model.nlp.documents.DocumentClassifier;
import ai.idylnlp.model.nlp.documents.DocumentClassifierException;
import ai.idylnlp.nlp.documents.dl4j.model.DeepLearningDocumentClassifierConfiguration;
import ai.idylnlp.nlp.documents.dl4j.utils.LabelSeeker;
import ai.idylnlp.nlp.documents.dl4j.utils.MeansBuilder;

/**
 * Implementation of {@link DocumentClassifier} that performs
 * document classification using DeepLearning4J.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningDocumentClassifier
  implements DocumentClassifier<DeepLearningDocumentClassifierConfiguration, DeepLearningDocumentClassificationRequest> {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningDocumentClassifier.class);

  private DeepLearningDocumentClassifierConfiguration configuration;
  private Map<LanguageCode, ParagraphVectors> models;

  /**
   * Creates a new deep learning document classifier.
   * @param configuration A {@link DeepLearningDocumentClassifierConfiguration}.
   * @throws DocumentClassifierException Thrown if the models cannot be preloaded.
   * @throws IOException
   */
  public DeepLearningDocumentClassifier(DeepLearningDocumentClassifierConfiguration configuration) throws DocumentClassifierException {

    this.configuration = configuration;
    models = new HashMap<>();

    for(DocumentModelManifest model : configuration.getModels()) {

      // If the file is not found an IOException will be thrown.
      final File modelFile = new File(model.getModelFileName());

      try {

        LOGGER.info("Loading model {}", modelFile.getAbsolutePath());
        final ParagraphVectors paragraphVectors = WordVectorSerializer.readParagraphVectors(modelFile);
        models.put(model.getLanguageCode(), paragraphVectors);

      } catch (IOException ex) {

        LOGGER.error("Unable to load document classification model {}. Verify the file exists.", ex, model.getModelFileName());

      }

    }

  }

  @Override
  public DocumentClassificationResponse classify(DeepLearningDocumentClassificationRequest request) throws DocumentClassifierException {

    try {

      // TODO: Allow the user to pass in a String[] instead of a String in the request.
      // The String[] is tokens.

      ParagraphVectors paragraphVectors = models.get(request.getLanguageCode());

      if(paragraphVectors != null) {

        // Get the matching manifest for this model.
        // TODO: Should the manifest be the object in the map's key? I don't think so.
        Optional<DocumentModelManifest> matchingObjects = configuration.getModels().stream()
              .filter(p -> p.getLanguageCode().equals(request.getLanguageCode()))
              .findFirst();

        // Should never return null.
        final DocumentModelManifest model = matchingObjects.get();

            final TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
            tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

            final InMemoryLookupTable<VocabWord> tab = (InMemoryLookupTable<VocabWord>) paragraphVectors.getLookupTable();

            final MeansBuilder meansBuilder = new MeansBuilder(tab, tokenizerFactory);

            final LabelSeeker seeker = new LabelSeeker(model.getLabels(), tab);

            final LabelledDocument document = new LabelledDocument();
              document.setContent(request.getText());

              final INDArray documentAsCentroid = meansBuilder.documentAsVector(document);
              final List<Pair<String, Double>> scores = seeker.getScores(documentAsCentroid);

              final Map<String, Double> sc = new HashMap<>();

              for(Pair<String, Double> score : scores) {

                sc.put(score.getFirst(), score.getSecond());

              }

            return new DocumentClassificationResponse(new DocumentClassificationScores(sc));

      } else {

        throw new DocumentClassifierException("No model for language " + request.getLanguageCode().getAlpha3().toString() + ".");

      }

    } catch (Exception ex) {

      throw new DocumentClassifierException("Unable to classify document.", ex);

    }

  }

  @Override
  public DocumentClassificationEvaluationResponse evaluate(DocumentClassificationEvaluationRequest request) throws DocumentClassifierException {

    // Actual class -> <Predicted class, Number of times>, for example:
    // positive -> negative, 10
    // Means, documents from the positive class were classified as negative 10 times.
    Map<String, Map<String, AtomicInteger>> results = new LinkedHashMap<>();

    final FileLabelAwareIterator iterator = new FileLabelAwareIterator.Builder().addSourceFolder(new File(request.getDirectory())).build();

    LOGGER.info("Beginning model evaluation using directory {}", request.getDirectory());

    while(iterator.hasNext()) {

      final LabelledDocument document = iterator.nextDocument();

      final String text = document.getContent();

      final DocumentClassificationResponse response = classify(new DeepLearningDocumentClassificationRequest(text, request.getLanguageCode()));

      // TODO: Is it possible to not be assigned to any category?
      final String actualCategory = document.getLabels().get(0);
      final String predictedCategory = response.getScores().getPredictedCategory().getLeft();

      // LOGGER.trace("Actual: " + actualCategory + "; Predicted: " + predictedCategory);

      results.putIfAbsent(actualCategory, new HashMap<String, AtomicInteger>());
      results.get(actualCategory).putIfAbsent(predictedCategory, new AtomicInteger(0));
      results.get(actualCategory).get(predictedCategory).incrementAndGet();

    }

    return new DocumentClassificationEvaluationResponse(results);

  }

}
