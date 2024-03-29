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
package ai.idylnlp.model.exceptions;

/**
 * This exception is thrown if a license does not validate.
 * @author Mountain Fog, Inc.
 */
public class ValidationException extends Exception {

  private static final long serialVersionUID = 5161843297983375847L;

  /**
   * Creates a new exception.
   * @param message The message of the exception.
   */
  public ValidationException(String message) {
    super(message);
  }

  /**
   * Creates a new exception.
   * @param message The message of the exception.
   * @param ex The exception.
   */
  public ValidationException(String message, Exception ex) {
    super(message, ex);
  }

}
