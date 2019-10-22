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
package ai.idylnlp.nlp.documents.opennlp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.opennlp.custom.encryption.OpenNLPEncryptionFactory;

import ai.idylnlp.model.Constants;
import ai.idylnlp.model.nlp.documents.DocumentClassificationFile;
import ai.idylnlp.model.nlp.documents.DocumentClassificationTrainingResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassifier;
import ai.idylnlp.model.nlp.documents.DocumentClassifierModelOperations;
import ai.idylnlp.model.nlp.documents.DocumentModelTrainingException;
import ai.idylnlp.model.nlp.documents.OpenNLPDocumentClassifierTrainingRequest;
import opennlp.tools.doccat.DoccatFactory;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.doccat.DocumentSample;
import opennlp.tools.doccat.DocumentSampleStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * Implementation of {@link DocumentClassifier} that performs document classification
 * using OpenNLP.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLPDocumentModelOperations implements DocumentClassifierModelOperations<OpenNLPDocumentClassifierTrainingRequest> {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPDocumentModelOperations.class);

  @Override
  public DocumentClassificationTrainingResponse train(OpenNLPDocumentClassifierTrainingRequest request) throws DocumentModelTrainingException {

    try {

      InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(request.getTrainingFile());
      ObjectStream<DocumentSample> sample = new DocumentSampleStream(new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8));

      final String language = request.getLanguageCode().getAlpha3().toString();

      DoccatModel model = DocumentCategorizerME.train(language, sample, TrainingParameters.defaultParams(), new DoccatFactory());

      BufferedOutputStream modelOut = null;

      // Set the encryption key.
      OpenNLPEncryptionFactory.getDefault().setKey(request.getEncryptionKey());

      // The generated model's ID. Assigned during the training process.
      String modelId = "";

      File modelFile = File.createTempFile("model", ".bin");

      try {

        modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
        modelId = model.serialize(modelOut);

      } catch (Exception ex) {

        LOGGER.error("Unable to create the model.", ex);

      } finally {

        if (modelOut != null) {
          modelOut.close();
        }

        // Clear the encryption key.
        OpenNLPEncryptionFactory.getDefault().clearKey();

      }

      final Map<DocumentClassificationFile, File> files = new HashMap<>();
      files.put(DocumentClassificationFile.MODEL_FILE, modelFile);

      // TODO: Get the categories and return them.
      return new DocumentClassificationTrainingResponse(modelId, files, Collections.emptyList());

    } catch (IOException ex) {

      throw new DocumentModelTrainingException("Unable to train document classification model.", ex);

    }

  }

}
