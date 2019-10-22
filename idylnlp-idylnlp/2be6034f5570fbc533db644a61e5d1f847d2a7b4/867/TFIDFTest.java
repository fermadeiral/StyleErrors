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
package ai.idylnlp.test.nlp.features;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import ai.idylnlp.nlp.features.TFIDF;

public class TFIDFTest {

  @Test
  public void test() {

    String[] doc1 = new String[] {"Lorem", "ipsum", "dolor", "ipsum", "sit", "ipsum"};
    String[] doc2 = new String[] {"Vituperata", "incorrupte", "at", "ipsum", "pro", "quo"};
    String[] doc3 = new String[] {"Has", "persius", "disputationi", "id", "simul"};
    List<String[]> documents = Arrays.asList(doc1, doc2, doc3);

    TFIDF calculator = new TFIDF();
    double tfidf = calculator.tfIdf(doc1, documents, "ipsum");
    System.out.println("TF-IDF (ipsum) = " + tfidf);

  }
  
}
