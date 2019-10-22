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

import com.neovisionaries.i18n.LanguageCode;

/**
 * Base class for document classification requests.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class AbstractDocumentClassificationRequest {

  private String text;
  private LanguageCode languageCode;

  /**
   * Creates a new document classification request.
   * @param text The tokenized text to classify.
   * @param languageCode The {@link LanguageCode} of the language.
   */
  public AbstractDocumentClassificationRequest(String text, LanguageCode languageCode) {

    this.text = text;
    this.languageCode = languageCode;

  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public LanguageCode getLanguageCode() {
    return languageCode;
  }

  public void setLanguageCode(LanguageCode languageCode) {
    this.languageCode = languageCode;
  }

}
