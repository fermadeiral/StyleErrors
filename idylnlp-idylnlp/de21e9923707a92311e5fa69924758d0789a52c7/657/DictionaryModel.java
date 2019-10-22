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

package ai.idylnlp.opennlp.custom.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import ai.idylnlp.opennlp.custom.encryption.OpenNLPEncryptionFactory;
import ai.idylnlp.model.manifest.StandardModelManifest;
import opennlp.tools.dictionary.Dictionary;
import opennlp.tools.namefind.DictionaryNameFinder;
import opennlp.tools.namefind.TokenNameFinder;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.StringList;
import opennlp.tools.util.model.BaseModel;

/**
 * A model that uses a dictionary to identify entities.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DictionaryModel extends BaseModel {

  private static final long serialVersionUID = 1L;

  private StandardModelManifest modelManifest;
  private String modelDirectory;

  /**
   * Creates a new dictionary model and initializes the {@link TokenNameFinder}.
   * @param modelManifest The {@link StandardModelManifest}.
   * @param modelDirectory The model directory.
   * @throws IOException
   */
  public DictionaryModel(StandardModelManifest modelManifest, String modelDirectory) throws Exception {
    this.modelManifest = modelManifest;
    this.modelDirectory = modelDirectory;
  }

  @Override
  public String getModelId() {
    return modelManifest.getModelId();
  }

  /**
   * Gets the configured {@link TokenNameFinder} for the dictionary model.
   * @param tokenizer A {@link Tokenizer} used to tokenize each line in the dictionary file.
   * @return A {@link TokenNameFinder}.
   * @throws Exception
   */
  public TokenNameFinder getDictionaryNameFinder(Tokenizer tokenizer) throws Exception {

    final boolean caseSensitive = false;

    final Dictionary dictionary = new Dictionary(caseSensitive);

    File modelFile = new File(modelDirectory + File.separator + modelManifest.getModelFileName());

    try (BufferedReader br = new BufferedReader(new FileReader(modelFile))) {

        String line;
        while ((line = br.readLine()) != null) {

          if(!StringUtils.isEmpty(modelManifest.getEncryptionKey())) {
            line = OpenNLPEncryptionFactory.getDefault().decrypt(line, modelManifest.getEncryptionKey());
          }

          final String[] tokenized = tokenizer.tokenize(line);

          // StringList tokens = new StringList("George", "Washington");
          StringList tokens = new StringList(tokenized);

          dictionary.put(tokens);

        }

    }

    return new DictionaryNameFinder(dictionary, modelManifest.getType());

  }

}