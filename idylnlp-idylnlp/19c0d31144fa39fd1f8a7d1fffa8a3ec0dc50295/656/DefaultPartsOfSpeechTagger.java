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

package ai.idylnlp.opennlp.custom.nlp.pos;

import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechToken;
import ai.idylnlp.opennlp.custom.modelloader.LocalModelLoader;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;

/**
 * A part of speech (POS) tagger that uses OpenNLP's tagging capabilities.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DefaultPartsOfSpeechTagger implements PartsOfSpeechTagger {

  private static final Logger LOGGER = LogManager.getLogger(DefaultPartsOfSpeechTagger.class);

  private POSTaggerME tagger;

  /**
   * Creates a tagger.
   * @param modelFile The part of speech model file.
   * @param sentenceDetector A {@link SentenceDetector}.
   * @param tokenizer A {@link Tokenizer}.
   * @throws ModelLoaderException
   */
  public DefaultPartsOfSpeechTagger(String modelPath, StandardModelManifest modelManifest, ModelValidator validator) throws ModelLoaderException {

    LocalModelLoader<POSModel> posModelLoader = new LocalModelLoader<POSModel>(validator, modelPath);

    POSModel model = posModelLoader.getModel(modelManifest, POSModel.class);

    tagger = new POSTaggerME(model);

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PartsOfSpeechToken> tag(String input, SentenceDetector sentenceDetector, Tokenizer tokenizer) {

    String[] sentences = sentenceDetector.sentDetect(input);

    List<PartsOfSpeechToken> partsOfSpeechTokens = new LinkedList<PartsOfSpeechToken>();

    for (String sentence : sentences) {

      String tokenizedSentence[] = tokenizer.tokenize(sentence);

      String[] tags = tagger.tag(tokenizedSentence);

      for (int i = 0; i < tokenizedSentence.length; i++) {

        final String token = tokenizedSentence[i].trim();
        final String tag = tags[i].trim();

        partsOfSpeechTokens.add(new PartsOfSpeechToken(token, tag));

      }

    }

    return partsOfSpeechTokens;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PartsOfSpeechToken> tag(String[] sentences, Tokenizer tokenizer) {

    List<PartsOfSpeechToken> partsOfSpeechTokens = new LinkedList<PartsOfSpeechToken>();

    for (String sentence : sentences) {

      String tokenizedSentence[] = tokenizer.tokenize(sentence);

      String[] tags = tagger.tag(tokenizedSentence);

      for (int i = 0; i < tokenizedSentence.length; i++) {

        final String token = tokenizedSentence[i].trim();
        final String tag = tags[i].trim();

        partsOfSpeechTokens.add(new PartsOfSpeechToken(token, tag));

      }

    }

    return partsOfSpeechTokens;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<PartsOfSpeechToken> tag(String[] tokenizedSentence) {

    List<PartsOfSpeechToken> partsOfSpeechTokens = new LinkedList<PartsOfSpeechToken>();

    String[] tags = tagger.tag(tokenizedSentence);

    for (int i = 0; i < tokenizedSentence.length; i++) {

      final String token = tokenizedSentence[i].trim();
      final String tag = tags[i].trim();

      partsOfSpeechTokens.add(new PartsOfSpeechToken(token, tag));

    }

    return partsOfSpeechTokens;

  }

}