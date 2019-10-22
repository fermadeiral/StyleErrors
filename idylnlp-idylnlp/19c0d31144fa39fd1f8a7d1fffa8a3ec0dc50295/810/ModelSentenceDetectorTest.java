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
package ai.idylnlp.test.nlp.sentence;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.nlp.sentence.ModelSentenceDetector;
import ai.idylnlp.nlp.sentence.SentenceDetectorModelLoader;

public class ModelSentenceDetectorTest {

  private static final Logger LOGGER = LogManager.getLogger(ModelSentenceDetectorTest.class);

  @Test
  public void testWithPeriod() throws Exception {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);
    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    String sentenceModelPath = new File("src/test/resources/").getAbsolutePath();

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", "/en-sent.bin",
        LanguageCode.en, "", StandardModelManifest.SENTENCE, "", "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    SentenceDetectorModelLoader modelLoader = new SentenceDetectorModelLoader(modelValidator, sentenceModelPath, modelManifest);

    SentenceDetector sentenceDetector = new ModelSentenceDetector(modelLoader);

    String sentence = "Bob Ross was a painter. Abraham Lincoln was President of the United States.blue";

    Collection<String> sentences = Arrays.asList(sentenceDetector.sentDetect(sentence));

    for(String s : sentences) {

      LOGGER.debug("SENTENCE: " + s);

    }

    assertTrue(sentences.contains("Bob Ross was a painter."));
    assertTrue(sentences.contains("Abraham Lincoln was President of the United States.blue"));

  }

  @Test
  public void testSentence() throws Exception {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);
    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    String sentenceModelPath = new File("src/test/resources/").getAbsolutePath();

    StandardModelManifest modelManifest = new StandardModelManifest.ModelManifestBuilder(UUID.randomUUID().toString(), "name", "/en-sent.bin",
        LanguageCode.en, "", StandardModelManifest.SENTENCE, "", "1.0.0", "", StandardModelManifest.DEFAULT_BEAM_SIZE, new Properties()).build();

    SentenceDetectorModelLoader modelLoader = new SentenceDetectorModelLoader(modelValidator, sentenceModelPath, modelManifest);

    SentenceDetector sentenceDetector = new ModelSentenceDetector(modelLoader);

    Collection<String> sentences = Arrays.asList(sentenceDetector.sentDetect("This is a sentence. This is another sentence. This is a third sentence."));

    for(String s : sentences) {

      LOGGER.debug(s);

    }

    assertTrue(sentences.contains("This is a sentence."));
    assertTrue(sentences.contains("This is another sentence."));
    assertTrue(sentences.contains("This is a third sentence."));

  }

}
