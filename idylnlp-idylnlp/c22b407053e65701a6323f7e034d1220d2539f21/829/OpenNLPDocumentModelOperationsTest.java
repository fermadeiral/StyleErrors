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
package ai.idylnlp.test.nlp.documents.opennlp;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.documents.DocumentClassificationFile;
import ai.idylnlp.model.nlp.documents.DocumentClassificationTrainingResponse;
import ai.idylnlp.model.nlp.documents.DocumentModelTrainingException;
import ai.idylnlp.model.nlp.documents.OpenNLPDocumentClassifierTrainingRequest;
import ai.idylnlp.nlp.documents.opennlp.OpenNLPDocumentModelOperations;

public class OpenNLPDocumentModelOperationsTest {

  private static final Logger LOGGER = LogManager.getLogger(OpenNLPDocumentModelOperationsTest.class);

  private static final String RESOURCES = new File("src/test/resources/").getAbsolutePath();

  @Test
  public void train() throws DocumentModelTrainingException {

    OpenNLPDocumentModelOperations ops = new OpenNLPDocumentModelOperations();

    File trainingFile = new File(RESOURCES + "/training.txt");

    OpenNLPDocumentClassifierTrainingRequest request = new OpenNLPDocumentClassifierTrainingRequest(trainingFile, LanguageCode.en);

    DocumentClassificationTrainingResponse response = ops.train(request);

    assertNotNull(response.getModelId());
    assertNotNull(response.getFiles());
    assertFalse(response.getFiles().isEmpty());
    assertTrue(response.getFiles().containsKey(DocumentClassificationFile.MODEL_FILE));

  }

}
