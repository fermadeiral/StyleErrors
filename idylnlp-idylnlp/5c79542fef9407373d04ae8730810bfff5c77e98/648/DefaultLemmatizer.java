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

package ai.idylnlp.opennlp.custom.nlp.lemmatization;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.exceptions.ModelLoaderException;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.lemma.Lemmatizer;
import ai.idylnlp.opennlp.custom.modelloader.LocalModelLoader;

import opennlp.tools.lemmatizer.DictionaryLemmatizer;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;

/**
 * Default implementation of {@link Lemmatizer} that uses OpenNLP's
 * lemmatizing capabilities to provide dictionary-based and
 * model-based lemmatization.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DefaultLemmatizer implements Lemmatizer {

  private static final Logger LOGGER = LogManager.getLogger(DefaultLemmatizer.class);

  private opennlp.tools.lemmatizer.Lemmatizer lemmatizer;
  private boolean isModelBased;

  /**
   * Creates a new lemmatizer that uses a dictionary.
   * @param dictionary The full path to the dictionary file.
   * @throws IOException Thrown if the dictionary cannot be opened.
   */
  public DefaultLemmatizer(String dictionary) throws IOException {

    isModelBased = false;

    InputStream dictLemmatizer = new FileInputStream(dictionary);

    lemmatizer = new DictionaryLemmatizer(dictLemmatizer);

    dictLemmatizer.close();

  }

  /**
   * Creates a new model-based lemmatizer.
   * @param modelPath The full path to the directory containing the model.
   * @param modelManifest The {@link StandardModelManifest manifest} of the lemmatizer model.
   * @param validator The {@link ModelValidator} used to validate the model.
   * @throws ModelLoaderException Thrown if the model cannot be loaded.
   */
  public DefaultLemmatizer(String modelPath, StandardModelManifest modelManifest, ModelValidator validator) throws ModelLoaderException {

    isModelBased = true;

    LocalModelLoader<LemmatizerModel> lemmaModelLoader = new LocalModelLoader<LemmatizerModel>(validator, modelPath);

    LemmatizerModel model = lemmaModelLoader.getModel(modelManifest, LemmatizerModel.class);

    lemmatizer = new LemmatizerME(model);

  }

  /**
   * {@inheritDoc}
   * <p>
   * How the lemmatization is performed depends on which constructor
   * was used to create the class. The lemmatization could be
   * dictionary-based or model-based.
   */
  @Override
  public String[] lemmatize(String[] tokens, String[] posTags) {

    String[] lemmas = lemmatizer.lemmatize(tokens, posTags);

    if(isModelBased) {

      // Must call decodeLemmas for model-based lemmatization.
      lemmas = ((LemmatizerME) lemmatizer).decodeLemmas(tokens, lemmas);

    }

    return lemmas;

  }

}