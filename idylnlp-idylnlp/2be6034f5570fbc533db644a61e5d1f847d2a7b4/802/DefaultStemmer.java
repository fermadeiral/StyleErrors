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
package ai.idylnlp.nlp.stemming;

import ai.idylnlp.model.nlp.Stemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer;
import opennlp.tools.stemmer.snowball.SnowballStemmer.ALGORITHM;

public class DefaultStemmer implements Stemmer {

  private opennlp.tools.stemmer.Stemmer stemmer;

  public DefaultStemmer() {

    stemmer = new SnowballStemmer(ALGORITHM.ENGLISH);

  }

  @Override
  public String stem(String text) {

    return stemmer.stem(text).toString();

  }

}
