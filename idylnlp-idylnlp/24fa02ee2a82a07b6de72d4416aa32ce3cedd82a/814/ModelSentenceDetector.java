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
package ai.idylnlp.nlp.sentence;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.opennlp.custom.utils.SpansToSpans;
import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Span;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

/**
 * An implementation of a {@link SentenceDetector}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class ModelSentenceDetector implements SentenceDetector {

  private static final Logger LOGGER = LogManager.getLogger(ModelSentenceDetector.class);

  private SentenceDetectorME sentenceDetector;
  private LanguageCode languageCode;

  /**
   * Creates a new sentence detector.
   * @param modelLoader A {@link SentenceDetectorModelLoader}.
   * @throws SentenceDetectionException Thrown if the model cannot be loaded or read.
   */
  public ModelSentenceDetector(SentenceDetectorModelLoader modelLoader) throws ModelLoaderException {

    LOGGER.debug("Using sentence model directory: " + modelLoader.getModelDirectory());
    LOGGER.debug("Using sentence model file: " + modelLoader.getModelManifest().getModelFileName());

    // Load the model.
    SentenceModel model = modelLoader.getModel(modelLoader.getModelManifest(), SentenceModel.class);

    this.sentenceDetector = new SentenceDetectorME(model);
    this.languageCode = modelLoader.getModelManifest().getLanguageCode();

  }

  public ModelSentenceDetector(SentenceModel sentenceModel, LanguageCode languageCode) throws ModelLoaderException {

    this.sentenceDetector = new SentenceDetectorME(sentenceModel);
    this.languageCode = languageCode;

  }
  
  @Override
  public List<String> getLanguageCodes() {
    return Arrays.asList(languageCode.getAlpha3().toString());
  }

  @Override
  public Span[] sentPosDetect(String text) {

    opennlp.tools.util.Span[] sentenceSpans = sentenceDetector.sentPosDetect(text);

    return SpansToSpans.toSpans(sentenceSpans);

  }

  @Override
  public String[] sentDetect(String text) {

    String[] sentences = sentenceDetector.sentDetect(text);

    return sentences;

  }

}
