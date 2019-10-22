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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.paragraphvectors.ParagraphVectors;
import org.deeplearning4j.text.documentiterator.FileLabelAwareIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.factory.Nd4j;

import ai.idylnlp.model.nlp.documents.DeepLearningDocumentClassifierTrainingRequest;
import ai.idylnlp.model.nlp.documents.DocumentClassificationFile;
import ai.idylnlp.model.nlp.documents.DocumentClassificationTrainingResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassifier;
import ai.idylnlp.model.nlp.documents.DocumentClassifierModelOperations;
import ai.idylnlp.model.nlp.documents.DocumentModelTrainingException;

/**
 * Implementation of {@link DocumentClassifier} that performs
 * document classification using DeepLearning4J.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DeepLearningDocumentModelOperations implements DocumentClassifierModelOperations<DeepLearningDocumentClassifierTrainingRequest> {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningDocumentModelOperations.class);

  @Override
  public DocumentClassificationTrainingResponse train(DeepLearningDocumentClassifierTrainingRequest request) throws DocumentModelTrainingException {

        // https://deeplearning4j.org/workspaces
        Nd4j.getMemoryManager().setAutoGcWindow(10000);

        try {

          LOGGER.info("Loading training iterator...");

          FileLabelAwareIterator.Builder builder = new FileLabelAwareIterator.Builder();

          for(String directory : request.getDirectories()) {

            final File d = new File(directory);

            // Make sure the directory exists and is a directory.
            if(d.exists() && d.isDirectory()) {

              LOGGER.info("Adding training directory {}", d.getAbsolutePath());

              builder.addSourceFolder(d);

            } else {

              LOGGER.warn("Training directory {} does not exist and will be skipped.", directory);

            }

          }

          final FileLabelAwareIterator iterator = builder.build();

          TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
          tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());

          final ParagraphVectors paragraphVectors = new ParagraphVectors.Builder()
                  .learningRate(request.getLearningRate())
                  .minLearningRate(request.getMinLearningRate())
                  .minWordFrequency(request.getMinWordFrequency())
                  .layerSize(request.getLayerSize())
                  .batchSize(request.getBatchSize())
                  .epochs(request.getEpochs())
                  .iterations(5)
                  .iterate(iterator)
                  .tokenizerFactory(tokenizerFactory)
                  .sampling(0)
                  .windowSize(5)
                  .build();

          LOGGER.info("Starting training...");
          paragraphVectors.fit();

          final File serializedModelFile = File.createTempFile("model", ".bin");
          WordVectorSerializer.writeParagraphVectors(paragraphVectors, serializedModelFile);
          LOGGER.info("Model serialized to {}", serializedModelFile.getAbsolutePath());

          Map<DocumentClassificationFile, File> files = new HashMap<>();
          files.put(DocumentClassificationFile.MODEL_FILE, serializedModelFile);

          final String modelId = UUID.randomUUID().toString();

          return new DocumentClassificationTrainingResponse(modelId, files, iterator.getLabelsSource().getLabels());

        } catch(Exception ex) {

          throw new DocumentModelTrainingException("Unable to train document classification model.", ex);

        }

  }

}
