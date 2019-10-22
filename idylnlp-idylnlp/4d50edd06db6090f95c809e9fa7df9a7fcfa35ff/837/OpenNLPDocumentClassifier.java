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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.documents.DocumentClassificationEvaluationRequest;
import ai.idylnlp.model.nlp.documents.DocumentClassificationEvaluationResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassificationResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassificationScores;
import ai.idylnlp.model.nlp.documents.DocumentClassifier;
import ai.idylnlp.model.nlp.documents.DocumentClassifierException;
import ai.idylnlp.model.nlp.documents.OpenNLPDocumentClassificationRequest;
import ai.idylnlp.nlp.documents.opennlp.model.OpenNLPDocumentClassifierConfiguration;
import opennlp.tools.doccat.DoccatModel;
import opennlp.tools.doccat.DocumentCategorizerME;
import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 * Implementation of {@link DocumentClassifier} that performs document classification
 * using OpenNLP.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class OpenNLPDocumentClassifier
  implements DocumentClassifier<OpenNLPDocumentClassifierConfiguration, OpenNLPDocumentClassificationRequest> {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPDocumentClassifier.class);

  private OpenNLPDocumentClassifierConfiguration configuration;
  private Map<LanguageCode, DocumentCategorizerME> doccatModelCache;

  /**
   * Creates a new OpenNLP document classifier.
   * @param configuration A {@link OpenNLPDocumentClassifierConfiguration}.
   * @throws DocumentClassifierException Thrown if the models cannot be preloaded.
   */
  public OpenNLPDocumentClassifier(OpenNLPDocumentClassifierConfiguration configuration) throws DocumentClassifierException {

    this.configuration = configuration;
    this.doccatModelCache = new HashMap<LanguageCode, DocumentCategorizerME>();

    if(configuration.isPreloadModels()) {

      LOGGER.info("Preloading the document classification models.");

      // Preload the models.
      for(LanguageCode languageCode : configuration.getDoccatModels().keySet()) {

        try {

          getDocumentCategorizer(languageCode);

        } catch (FileNotFoundException ex) {

          final String fileName = configuration.getDoccatModels().get(languageCode).getAbsolutePath();

          LOGGER.error("The model file {} was not found.", ex, fileName);

        }

      }

    }

  }

  @Override
  public DocumentClassificationResponse classify(OpenNLPDocumentClassificationRequest request) throws DocumentClassifierException {

    try {

      DocumentCategorizerME categorizer = getDocumentCategorizer(request.getLanguageCode());

      // TODO: Tokenize the text.
      final String tokens[] = WhitespaceTokenizer.INSTANCE.tokenize(request.getText());

      final double[] outcomes = categorizer.categorize(tokens);

      Map<String, Double> scores = new HashMap<>();

      for(int i = 0; i < outcomes.length; i++) {

        scores.put(categorizer.getCategory(i), outcomes[i]);

      }

      return new DocumentClassificationResponse(new DocumentClassificationScores(scores));

    } catch (Exception ex) {

      throw new DocumentClassifierException("Unable to classify document.", ex);

    }

  }

  private DocumentCategorizerME getDocumentCategorizer(LanguageCode languageCode) throws DocumentClassifierException, FileNotFoundException {

    LOGGER.info("Loading document classification model for language {}.", languageCode.getAlpha3().toString());

    // Has this model been loaded before?
    DocumentCategorizerME documentCategorizer = doccatModelCache.get(languageCode);

    if(documentCategorizer == null) {

      final File file = configuration.getDoccatModels().get(languageCode);

      if(file != null) {

        if(file.exists()) {

          // The model has not been loaded so we will load it now.
          final InputStream is = new FileInputStream(file);

          try {

            final DoccatModel doccatModel = new DoccatModel(is);

            documentCategorizer = new DocumentCategorizerME(doccatModel);

            doccatModelCache.put(languageCode, documentCategorizer);

          } catch (IOException ex) {

            LOGGER.error("Unable to perform document classification.", ex);

            throw new DocumentClassifierException("Unable to perform document classification.", ex);

          } finally {

            IOUtils.closeQuietly(is);

          }

        } else {

          throw new DocumentClassifierException("The model file for language " + languageCode.getAlpha3().toString() + " does not exist.");

        }

      } else {

        throw new DocumentClassifierException("No model file for language " + languageCode.getAlpha3().toString() + ".");

      }

    }

    return documentCategorizer;

  }

  @Override
  public DocumentClassificationEvaluationResponse evaluate(DocumentClassificationEvaluationRequest request)
      throws DocumentClassifierException {
    // TODO: Implement this.
    return null;
  }

}
