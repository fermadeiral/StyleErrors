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
package ai.idylnlp.model.language;

import com.neovisionaries.i18n.LanguageCode;

public class Language {

  private Language() {
    // Utility class.
  }

  /**
   * Determines if a string is a valid language code.
   * @param languageCode A language code.
   * @return <code>true</code> if the string is a valid 3-letter language code;
   * otherwise <code>false</code>.
   */
  public static boolean validate(String languageCode) {

    LanguageCode code = LanguageCode.getByCodeIgnoreCase(languageCode);

    if(code == null) {

      return false;

    } else {

      return true;

    }

  }

}
