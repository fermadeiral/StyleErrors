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
package ai.idylnlp.model.exceptions;

/**
 * An exception that is thrown if an error is encountered
 * during text extraction from a document.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class ExtractorException extends Exception {

  private static final long serialVersionUID = 2325000410259951206L;

  /**
   * Creates a new exception.
   * @param message The message of the exception.
   */
  public ExtractorException(String message) {
    super(message);
  }

  /**
   * Creates a new exception.
   * @param message The message of the exception.
   * @param throwable The exception.
   */
  public ExtractorException(String message, Throwable throwable) {
    super(message, throwable);
  }

}
