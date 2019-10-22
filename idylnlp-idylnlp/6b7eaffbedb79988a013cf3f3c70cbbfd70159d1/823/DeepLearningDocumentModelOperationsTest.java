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
package ai.idylnlp.test.nlp.documents.deeplearning4j;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.manifest.DocumentModelManifest;
import ai.idylnlp.model.nlp.documents.DeepLearningDocumentClassificationRequest;
import ai.idylnlp.model.nlp.documents.DeepLearningDocumentClassifierTrainingRequest;
import ai.idylnlp.model.nlp.documents.DocumentClassificationFile;
import ai.idylnlp.model.nlp.documents.DocumentClassificationResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassificationTrainingResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassifierException;
import ai.idylnlp.model.nlp.documents.DocumentModelTrainingException;
import ai.idylnlp.nlp.documents.dl4j.DeepLearningDocumentClassifier;
import ai.idylnlp.nlp.documents.dl4j.DeepLearningDocumentModelOperations;
import ai.idylnlp.nlp.documents.dl4j.model.DeepLearningDocumentClassifierConfiguration;
import ai.idylnlp.testing.markers.ExternalData;

public class DeepLearningDocumentModelOperationsTest {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningDocumentModelOperationsTest.class);

  private static final String TRAINING_DATA_PATH = System.getProperty("testDataPath");

  @Ignore
  @Category(ExternalData.class)
  @Test
  public void train() throws DocumentModelTrainingException, DocumentClassifierException, IOException {

    List<String> directories = Arrays.asList(TRAINING_DATA_PATH, "aclImdb/train");

    DeepLearningDocumentClassifierTrainingRequest request = new DeepLearningDocumentClassifierTrainingRequest();
    request.setLanguageCode(LanguageCode.en);
    request.setDirectories(directories);

    DeepLearningDocumentModelOperations ops = new DeepLearningDocumentModelOperations();
    DocumentClassificationTrainingResponse response = ops.train(request);

    final File modelFile = response.getFiles().get(DocumentClassificationFile.MODEL_FILE);

    DocumentModelManifest manifest = new DocumentModelManifest(response.getModelId(), modelFile.getAbsolutePath(),
        LanguageCode.en, "document", "name",
        "1.0.0", "https://source/", Arrays.asList("pos", "neg"), new Properties());

    List<DocumentModelManifest> models = new LinkedList<>();
    models.add(manifest);

    DeepLearningDocumentClassifierConfiguration config = new DeepLearningDocumentClassifierConfiguration
        .Builder()
          .withModels(models)
          .build();

    // TODO: Fix file name.
    final String text = FileUtils.readFileToString(new File("negative.txt"));

    DeepLearningDocumentClassificationRequest deepLearningDocumentClassificationRequest = new DeepLearningDocumentClassificationRequest(text, LanguageCode.en);

    DeepLearningDocumentClassifier classifier = new DeepLearningDocumentClassifier(config);
    DocumentClassificationResponse documentClassificationResponse = classifier.classify(deepLearningDocumentClassificationRequest);

    LOGGER.info("Predicted category: " + documentClassificationResponse.getScores().getPredictedCategory().getLeft());

  }

}
