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
package ai.idylnlp.model.nlp.documents;

/**
 * An interface for document classifiers. A document classifier takes a document
 * and assigns it to one or more known classes based on the document's contents.
 *
 * @author Mountain Fog, Inc.
 *
 */
public interface DocumentClassifier<T extends AbstractDocumentClassifierConfiguration, U extends AbstractDocumentClassificationRequest> {

  /**
   * Classifies a document.
   * @param request A {@link AbstractDocumentClassificationRequest request} containing the details of the
   * document classification request.
   * @return A {@link DocumentClassificationResponse response} containing the results of the
   * document classification.
   * @throws DocumentClassifierException Thrown if the document classification fails.
   */
  public DocumentClassificationResponse classify(U request) throws DocumentClassifierException;

  /**
   * Evaluates a document classification model.
   * @param request The {@link DocumentClassificationEvaluationRequest request} to evaluate.
   * @return The {@link DocumentClassificationEvaluationResponse results} of the evaluation.
   * @throws DocumentClassifierException Thrown if the evaluation fails.
   */
  public DocumentClassificationEvaluationResponse evaluate(DocumentClassificationEvaluationRequest request) throws DocumentClassifierException;

}
