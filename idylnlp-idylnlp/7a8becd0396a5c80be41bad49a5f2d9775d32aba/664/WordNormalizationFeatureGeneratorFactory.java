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

package ai.idylnlp.opennlp.custom.features;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import ai.idylnlp.model.ModelValidator;
import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.ModelManifestUtils;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.lemma.Lemmatizer;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.opennlp.custom.nlp.lemmatization.DefaultLemmatizer;
import ai.idylnlp.opennlp.custom.nlp.pos.DefaultPartsOfSpeechTagger;
import ai.idylnlp.opennlp.custom.validators.TrueValidator;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;
import opennlp.tools.util.featuregen.GeneratorFactory.XmlFeatureGeneratorFactory;

/**
 * Factory for {@link WordNormalizationFeatureGenerator}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class WordNormalizationFeatureGeneratorFactory implements XmlFeatureGeneratorFactory {

  private static final String ELEMENT_NAME = "wordnormalization";

  private Lemmatizer modelLemmatizer;
  private Lemmatizer dictonaryLemmatizer;
  private PartsOfSpeechTagger partsOfSpeechTagger;
  private ModelValidator validator;

  @Override
  public AdaptiveFeatureGenerator create(Element generatorElement, FeatureGeneratorResourceProvider resourceManager)
      throws InvalidFormatException {

    validator = new TrueValidator();

    try {

      loadLemmatizers(generatorElement);
      loadPartsOfSpeechTagger(generatorElement);

      return new WordNormalizationFeatureGenerator(partsOfSpeechTagger, modelLemmatizer, dictonaryLemmatizer);

    } catch (Exception ex) {

      throw new InvalidFormatException("Unable to load lemmatizer or parts-of-speech model.", ex);

    }

  }

  public static void register(Map<String, XmlFeatureGeneratorFactory> factoryMap) {
    factoryMap.put(ELEMENT_NAME, new WordNormalizationFeatureGeneratorFactory());
  }

  private void loadLemmatizers(Element generatorElement) throws Exception {

    final String lemmaModelPath = generatorElement.getAttribute("modelPath");
    final String lemmaModelManifest = generatorElement.getAttribute("modelManifest");
    final String lemmaDictionary = generatorElement.getAttribute("dictionary");

    ModelManifest modelManifest = ModelManifestUtils.readManifest(lemmaModelPath + lemmaModelManifest);

    StandardModelManifest standardModelManifest = (StandardModelManifest) modelManifest;

    if(StringUtils.isNotEmpty(lemmaModelPath) && StringUtils.isNotEmpty(lemmaModelManifest)) {

      modelLemmatizer = new DefaultLemmatizer(lemmaModelPath, standardModelManifest, validator);

    }

    if(StringUtils.isNotEmpty(lemmaDictionary)) {
      dictonaryLemmatizer = new DefaultLemmatizer(lemmaDictionary);
    }

  }

  private void loadPartsOfSpeechTagger(Element generatorElement) throws Exception {

    final String posModelpath = generatorElement.getAttribute("modelPath");
    final String posModelManfiest = generatorElement.getAttribute("modelManifest");

    ModelManifest modelManifest = ModelManifestUtils.readManifest(posModelpath + posModelManfiest);

    StandardModelManifest standardModelManifest = (StandardModelManifest) modelManifest;

    // TODO: Get a Validator in here.
    partsOfSpeechTagger = new DefaultPartsOfSpeechTagger(posModelpath, standardModelManifest, validator);

  }

}