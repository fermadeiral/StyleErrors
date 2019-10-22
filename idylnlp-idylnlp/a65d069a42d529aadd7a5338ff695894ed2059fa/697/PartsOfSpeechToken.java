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
package ai.idylnlp.model.nlp.pos;

import java.util.LinkedList;
import java.util.List;

public class PartsOfSpeechToken {

  private String token;
  private String pos;

  public PartsOfSpeechToken(String token, String pos) {

    this.token = token;
    this.pos = pos;

  }

  public static String[] getTokens(List<PartsOfSpeechToken> partsOfSpeechTokens) {

    List<String> tags = new LinkedList<String>();

    for(PartsOfSpeechToken partsOfSpeechToken : partsOfSpeechTokens) {

      tags.add(partsOfSpeechToken.getPos());

    }

    return tags.toArray(new String[tags.size()]);

  }

  public String getToken() {
    return token;
  }

  public String getPos() {
    return pos;
  }

}
