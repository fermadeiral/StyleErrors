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

import opennlp.tools.util.InvalidFormatException;
import opennlp.tools.util.featuregen.AdaptiveFeatureGenerator;
import opennlp.tools.util.featuregen.FeatureGeneratorResourceProvider;
import opennlp.tools.util.featuregen.GeneratorFactory.XmlFeatureGeneratorFactory;

/**
 * Factory for {@link SpecialCharacterFeatureGenerator}.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SpecialCharacterFeatureGeneratorFactory implements XmlFeatureGeneratorFactory {

  private static final String ELEMENT_NAME = "specchar";

  @Override
  public AdaptiveFeatureGenerator create(Element generatorElement, FeatureGeneratorResourceProvider resourceManager)
      throws InvalidFormatException {

    return new SpecialCharacterFeatureGenerator();

  }

  public static void register(Map<String, XmlFeatureGeneratorFactory> factoryMap) {
    factoryMap.put(ELEMENT_NAME, new SpecialCharacterFeatureGeneratorFactory());
  }

}