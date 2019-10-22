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

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.manifest.DocumentModelManifest;
import ai.idylnlp.model.nlp.documents.DeepLearningDocumentClassificationRequest;
import ai.idylnlp.model.nlp.documents.DeepLearningDocumentClassifierTrainingRequest;
import ai.idylnlp.model.nlp.documents.DocumentClassificationEvaluationRequest;
import ai.idylnlp.model.nlp.documents.DocumentClassificationEvaluationResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassificationFile;
import ai.idylnlp.model.nlp.documents.DocumentClassificationResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassificationTrainingResponse;
import ai.idylnlp.model.nlp.documents.DocumentClassifierException;
import ai.idylnlp.model.nlp.documents.DocumentModelTrainingException;
import ai.idylnlp.nlp.documents.dl4j.DeepLearningDocumentClassifier;
import ai.idylnlp.nlp.documents.dl4j.DeepLearningDocumentModelOperations;
import ai.idylnlp.nlp.documents.dl4j.model.DeepLearningDocumentClassifierConfiguration;
import ai.idylnlp.testing.markers.ExternalData;

public class DeepLearningDocumentClassifierTest {

  private static final Logger LOGGER = LogManager.getLogger(DeepLearningDocumentClassifierTest.class);

  private static final String TRAINING_DATA_PATH = System.getProperty("testDataPath");

  @Ignore("Goes between pos and neg. Needs better training to be consistent.")
  @Test
  public void classify() throws DocumentClassifierException, IOException, DocumentModelTrainingException {

    // Make a model to evaluate with.
    Collection<DocumentModelManifest> models = train();

    DeepLearningDocumentClassifierConfiguration configuration = new DeepLearningDocumentClassifierConfiguration.Builder()
        .withModels(models)
        .build();

    final String positiveText = "This movie, with all its complexity and subtlety, makes for one of the most thought-provoking short films I have ever seen. The topics it addresses are ugly, cynical, and at times, even macabre, but the film remains beautiful in its language, artful with its camera angles, and gorgeous in its style, skillfully recreating the short story of the same name written by a master of short stories, Tobias Wolff.<br /><br />Not wishing to spoil anything of the movie, I won't go into any details, other than to say that this movie is magnificent in and of itself. It takes pride in what it does, and does it well. It shows the most important memories of life, all of which can be topped by the single most elusive feeling: unexpected bliss. This movie, of its own volition, has created in me the same feelings the main character (Tom Noonan) felt when words transformed his very existence, and that is one impressive feat.";

    DeepLearningDocumentClassificationRequest request = new DeepLearningDocumentClassificationRequest(positiveText, LanguageCode.en);

    DeepLearningDocumentClassifier classifier = new DeepLearningDocumentClassifier(configuration);
    DocumentClassificationResponse response = classifier.classify(request);

    LOGGER.info("Predicted category: {}", response.getScores().getPredictedCategory().getLeft());

    assertNotNull(response.getScores());

    assertEquals("pos", response.getScores().getPredictedCategory().getLeft());

  }

  @Ignore
  @Category(ExternalData.class)
  @Test
  public void evaluate() throws DocumentClassifierException, IOException, DocumentModelTrainingException {

    // Make a model to evaluate with.
    Collection<DocumentModelManifest> models = train();

    DeepLearningDocumentClassifierConfiguration configuration = new DeepLearningDocumentClassifierConfiguration.Builder()
        .withModels(models)
        .build();

    DocumentClassificationEvaluationRequest request = new DocumentClassificationEvaluationRequest();
    request.setLanguageCode(LanguageCode.en);
    request.setDirectory(new File(TRAINING_DATA_PATH, "aclImdb/test").getAbsolutePath());

    DeepLearningDocumentClassifier classifier = new DeepLearningDocumentClassifier(configuration);
    DocumentClassificationEvaluationResponse response = classifier.evaluate(request);

    Map<String, Map<String, AtomicInteger>> results = response.getResults();

    assertEquals(2, results.keySet().size());

    for(String actualClass : results.keySet()) {

      LOGGER.info("Actual class: {}", actualClass);

      for(String predictedClass : results.get(actualClass).keySet()) {

        LOGGER.info("\tPredicted as: {}, times: {}", predictedClass, results.get(actualClass).get(predictedClass).intValue());

      }

    }

    // TODO: Calculate precision, recall, and F1.
    // https://en.wikipedia.org/wiki/Precision_and_recall
    // https://stats.stackexchange.com/questions/21551/how-to-compute-precision-recall-for-multiclass-multilabel-classification

  }

  private List<DocumentModelManifest> train() throws IOException, DocumentModelTrainingException {

    List<String> directories = Arrays.asList(new File(TRAINING_DATA_PATH, "aclImdb/train").getAbsolutePath());

    DeepLearningDocumentClassifierTrainingRequest request = new DeepLearningDocumentClassifierTrainingRequest();
    request.setLanguageCode(LanguageCode.en);
    request.setDirectories(directories);

    DeepLearningDocumentModelOperations ops = new DeepLearningDocumentModelOperations();
    DocumentClassificationTrainingResponse response = ops.train(request);

    final File modelFile = response.getFiles().get(DocumentClassificationFile.MODEL_FILE);

    DocumentModelManifest manifest = new DocumentModelManifest(response.getModelId(), modelFile.getAbsolutePath(),
        LanguageCode.en, "document", "name",
        "1.0.0", "https://source/", Arrays.asList("neg", "pos"), new Properties());

    List<DocumentModelManifest> models = new LinkedList<>();
    models.add(manifest);

    return models;

  }

}
