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
package ai.idylnlp.nlp.translation;

import java.io.IOException;
import java.util.List;

import org.apache.joshua.decoder.Decoder;
import org.apache.joshua.decoder.JoshuaConfiguration;
import org.apache.joshua.decoder.StructuredTranslation;
import org.apache.joshua.decoder.Translation;
import org.apache.joshua.decoder.segment_file.Sentence;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.nlp.translation.LanguageTranslationRequest;
import ai.idylnlp.model.nlp.translation.LanguageTranslationResponse;
import ai.idylnlp.model.nlp.translation.Translator;

/**
 * Implementation of {@link Translator} that uses Apache Joshua
 * to perform translation of natural language text.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class JoshuaTranslator implements Translator {

  private static final Logger LOGGER = LogManager.getLogger(JoshuaTranslator.class);

  private Decoder decoder;
  private int counter = 0;

  /**
   * Creates a new translator.
   * @param joshuaLanguagePackPath The full path to the Apache Joshua language pack.
   * The joshua.config file is expected to be located in this path.
   * @throws IOException Thrown if the language pack cannot be loaded.
   */
  public JoshuaTranslator(final String joshuaLanguagePackPath) throws IOException {

    LOGGER.info("Initialize Apache Joshua translator from {}.", joshuaLanguagePackPath);

    String deEnJoshuaConfigFile = joshuaLanguagePackPath + "/joshua.config";
    JoshuaConfiguration deEnConf = new JoshuaConfiguration();
    deEnConf.readConfigFile(deEnJoshuaConfigFile);
    deEnConf.use_structured_output = true;
    deEnConf.modelRootPath = joshuaLanguagePackPath;

    decoder = new Decoder(deEnConf, deEnJoshuaConfigFile);

  }

  @Override
  public LanguageTranslationResponse translate(LanguageTranslationRequest request) {

    final String input = request.getInput();

    Sentence sentence = new Sentence(input, counter++, decoder.getJoshuaConfiguration());
    Translation translation = decoder.decode(sentence);
    List<StructuredTranslation> structuredTranslations = translation.getStructuredTranslations();

    StringBuilder sb = new StringBuilder();

    for (StructuredTranslation st : structuredTranslations) {
      sb.append(st.getTranslationString());
    }

    return new LanguageTranslationResponse(sb.toString());

  }

}
