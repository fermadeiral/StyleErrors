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
package ai.idylnlp.test.nlp.pos;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import java.io.File;
import java.util.List;
import java.util.UUID;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.mockito.Mockito;
import ai.idylnlp.opennlp.custom.nlp.pos.DefaultPartsOfSpeechTagger;
import com.neovisionaries.i18n.LanguageCode;
import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.exceptions.ValidationException;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.manifest.StandardModelManifest.ModelManifestBuilder;
import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechToken;

public class DefaultPartsOfSpeechTaggerTest {

  private static final Logger LOGGER = LogManager.getLogger(DefaultPartsOfSpeechTaggerTest.class);

  private static final String MODEL_PATH = new File("src/test/resources/models/").getAbsolutePath();
  private static final String POS_MODEL = "en-pos-maxent.bin";

  @Test
  public void tag1() throws ValidationException, ModelLoaderException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    ModelManifestBuilder builder = new StandardModelManifest.ModelManifestBuilder();
    builder.setModelId(UUID.randomUUID().toString());
    builder.setLanguageCode(LanguageCode.en);
    builder.setModelFileName(POS_MODEL);
    builder.setEncryptionKey("");
    builder.setType(StandardModelManifest.POS);

    StandardModelManifest modelManifest = builder.build();

    final String text = "George Washington was president.";

    SentenceDetector sentenceDetector = Mockito.mock(SentenceDetector.class);
    when(sentenceDetector.sentDetect(text)).thenReturn(new String[]{text});

    Tokenizer tokenizer = Mockito.mock(Tokenizer.class);
    when(tokenizer.tokenize(text)).thenReturn(new String[]{"George", "Washington", "was", "president"});

    DefaultPartsOfSpeechTagger tagger = new DefaultPartsOfSpeechTagger(MODEL_PATH, modelManifest, modelValidator);
    List<PartsOfSpeechToken> partsOfSpeechTokens = tagger.tag("George Washington was president.", sentenceDetector, tokenizer);

    for(PartsOfSpeechToken token : partsOfSpeechTokens) {

      LOGGER.info(token.getToken() + " : " + token.getPos());

    }

  }

  @Test
  public void tag2() throws ValidationException, ModelLoaderException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    ModelManifestBuilder builder = new StandardModelManifest.ModelManifestBuilder();
    builder.setModelId(UUID.randomUUID().toString());
    builder.setLanguageCode(LanguageCode.en);
    builder.setModelFileName(POS_MODEL);
    builder.setEncryptionKey("");
    builder.setType(StandardModelManifest.POS);

    StandardModelManifest modelManifest = builder.build();

    final String text = "George Washington was president.";

    Tokenizer tokenizer = Mockito.mock(Tokenizer.class);
    when(tokenizer.tokenize(text)).thenReturn(new String[]{"George", "Washington", "was", "president"});

    String[] sentence = new String[]{"George Washington was president."};

    DefaultPartsOfSpeechTagger tagger = new DefaultPartsOfSpeechTagger(MODEL_PATH, modelManifest, modelValidator);
    List<PartsOfSpeechToken> partsOfSpeechTokens = tagger.tag(sentence, tokenizer);

    for(PartsOfSpeechToken token : partsOfSpeechTokens) {

      LOGGER.info(token.getToken() + " : " + token.getPos());

    }

  }

  @Test
  public void tag3() throws ValidationException, ModelLoaderException {

    ModelValidator modelValidator = Mockito.mock(ModelValidator.class);

    when(modelValidator.validate(any(ModelManifest.class))).thenReturn(true);

    ModelManifestBuilder builder = new StandardModelManifest.ModelManifestBuilder();
    builder.setModelId(UUID.randomUUID().toString());
    builder.setLanguageCode(LanguageCode.en);
    builder.setModelFileName(POS_MODEL);
    builder.setEncryptionKey("");
    builder.setType(StandardModelManifest.POS);

    StandardModelManifest modelManifest = builder.build();

    String[] tokens = new String[]{"George", "Washington", "was", "president"};

    DefaultPartsOfSpeechTagger tagger = new DefaultPartsOfSpeechTagger(MODEL_PATH, modelManifest, modelValidator);
    List<PartsOfSpeechToken> partsOfSpeechTokens = tagger.tag(tokens);

    for(PartsOfSpeechToken token : partsOfSpeechTokens) {

      LOGGER.info(token.getToken() + " : " + token.getPos());

    }

  }

}
