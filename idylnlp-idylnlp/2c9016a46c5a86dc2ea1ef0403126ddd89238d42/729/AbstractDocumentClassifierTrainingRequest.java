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
package ai.idylnlp.model.nlp.documents;

import java.io.File;

import com.neovisionaries.i18n.LanguageCode;

/**
 * Base class for a document classifier training request.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class AbstractDocumentClassifierTrainingRequest {

  private File trainingFile;
  private String encryptionKey;
  private LanguageCode languageCode;

  public AbstractDocumentClassifierTrainingRequest(File trainingFile, LanguageCode languageCode) {

    this.trainingFile = trainingFile;
    this.languageCode = languageCode;

  }

  public File getTrainingFile() {
    return trainingFile;
  }

  public void setTrainingFile(File trainingFile) {
    this.trainingFile = trainingFile;
  }

  public String getEncryptionKey() {
    return encryptionKey;
  }

  public void setEncryptionKey(String encryptionKey) {
    this.encryptionKey = encryptionKey;
  }

  public LanguageCode getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(LanguageCode languageCode) {
    this.languageCode = languageCode;
  }

}
