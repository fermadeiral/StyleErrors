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
package ai.idylnlp.nlp.tokenizers;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.nlp.Span;
import ai.idylnlp.model.nlp.Stemmer;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.opennlp.custom.utils.SpansToSpans;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

/**
 * A tokenizer that tokenizes using a maximum entropy trained model.
 * This tokenizer is a pass-through to OpenNLP's {@link TokenizerME}.
 *
 * The {@link TokenizerME} and hence this class is not thread-safe.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class ModelTokenizer implements Tokenizer {

  private TokenizerME tokenizer;
  private LanguageCode languageCode;

  /**
   * Creates a new model tokenizer.
   * @param modelInputStream The {@link InputStream stream} containing the model.
   * @param languageCode The {@link LanguageCode} for this tokenizer.
   * @throws IOException Thrown if the token model cannot be loaded.
   * @throws ModelLoaderException
   */
  public ModelTokenizer(InputStream modelInputStream, LanguageCode languageCode) throws ModelLoaderException {

    this.languageCode = languageCode;

    try {

      final TokenizerModel tokenModel = new TokenizerModel(modelInputStream);
      tokenizer = new TokenizerME(tokenModel);
      modelInputStream.close();

    } catch (IOException ex) {

      throw new ModelLoaderException("Unable to load token model.", ex);

    }

  }

  /**
   * Creates a new model tokenizer.
   * @param tokenModel A {@link TokenizerModel} for this tokenizer.
   * @param languageCode The {@link LanguageCode} for this tokenizer.
   */
  public ModelTokenizer(TokenizerModel tokenModel, LanguageCode languageCode) {

    this.languageCode = languageCode;
    this.tokenizer = new TokenizerME(tokenModel);

  }

  @Override
  public List<String> getLanguageCodes() {

    return Arrays.asList(languageCode.getAlpha3().toString());

  }

  @Override
  public String[] tokenize(String s) {

    return tokenizer.tokenize(s);

  }

  @Override
  public Span[] tokenizePos(String s) {

    opennlp.tools.util.Span[] tokenSpans = tokenizer.tokenizePos(s);

    return SpansToSpans.toSpans(tokenSpans);

  }

  @Override
  public String[] tokenize(String s, Stemmer stemmer) {

    String[] tokens = tokenizer.tokenize(s);

    for (int i = 0; i < tokens.length; i++) {

      tokens[i] = stemmer.stem(tokens[i]);

    }

    return tokens;

  }

}
