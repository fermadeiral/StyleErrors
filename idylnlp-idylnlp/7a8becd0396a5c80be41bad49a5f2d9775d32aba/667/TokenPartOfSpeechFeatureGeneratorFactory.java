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

import org.w3c.dom.Element;

import ai.idylnlp.model.manifest.ModelManifest;
import ai.idylnlp.model.manifest.ModelManifestUtils;
import ai.idylnlp.model.manifest.StandardModelManifest;
import ai.idylnlp.model.nlp.pos.PartsOfSpeechTagger;
import ai.idylnlp.opennlp.custom.nlp.pos.DefaultPartsOfSpeechTagger;
import ai.idylnlp.opennlp.custom.validators.TrueValidator;

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;
import opennlp.tools.util.featuregen.GeneratorFactory.XmlFeatureGeneratorFactory;

/**
 * Factory for {@link TokenPartOfSpeechFeatureGenerator}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class TokenPartOfSpeechFeatureGeneratorFactory implements XmlFeatureGeneratorFactory {

  private static final String ELEMENT_NAME = "tokenpos";

  @Override
  public AdaptiveFeatureGenerator create(Element generatorElement, FeatureGeneratorResourceProvider resourceManager)
      throws InvalidFormatException {

    final String modelPath = generatorElement.getAttribute("modelPath");
    final String modelManifestPath = generatorElement.getAttribute("modelManifest");

    try {

      ModelManifest modelManifest = ModelManifestUtils.readManifest(modelPath + modelManifestPath);

      StandardModelManifest standardModelManifest = (StandardModelManifest) modelManifest;

      PartsOfSpeechTagger tagger = new DefaultPartsOfSpeechTagger(modelPath, standardModelManifest, new TrueValidator());

      return new TokenPartOfSpeechFeatureGenerator(tagger);

    } catch (Exception ex) {

      throw new InvalidFormatException("Unable to load the parts-of-speech model.", ex);

    }

  }

  public static void register(Map<String, XmlFeatureGeneratorFactory> factoryMap) {
    factoryMap.put(ELEMENT_NAME, new TokenPartOfSpeechFeatureGeneratorFactory());
  }

}