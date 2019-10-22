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
package ai.idylnlp.model.nlp.documents;

/**
 * An exception thrown during the classification of a document.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DocumentClassifierException extends Exception {

  private static final long serialVersionUID = -8390739685887961751L;

  /**
   * Creates a new exception.
   * @param message The exception message.
   */
  public DocumentClassifierException(String message) {
    super(message);
  }

  /**
   * Creates a new exception.
   * @param message The exception message.
   * @param ex The underlying {@link Exception}.
   */
  public DocumentClassifierException(String message, Exception ex) {
    super(message, ex);
  }

}
