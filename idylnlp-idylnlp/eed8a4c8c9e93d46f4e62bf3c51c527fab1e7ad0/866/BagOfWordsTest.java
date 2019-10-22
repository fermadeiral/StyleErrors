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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.junit.Test;
import ai.idylnlp.nlp.features.BagOfWords;

public class BagOfWordsTest {

  @Test
  public void bag() {
    
    String[] tokens = {"George", "Washington", "was", "president"};
    BagOfWords bag = new BagOfWords(tokens);
    
    assertTrue(bag.contains("George"));
    assertEquals(1, bag.getCount("George"));
    
  }
  
  @Test
  public void removeBelow() {
    
    String[] tokens = {"George", "Washington", "was", "was", "president"};
    BagOfWords bag = new BagOfWords(tokens, 2);
    
    assertTrue(bag.contains("was"));
    assertEquals(2, bag.size());
    assertEquals(2, bag.getCount("was"));
    
  }
  
  @Test
  public void normalize() {
    
    BagOfWords bag1 = new BagOfWords(new String[]{"george", "washington", "was", "president", "of", "the", "united", "states", "states"});
    BagOfWords bag2 = new BagOfWords(new String[]{"abraham", "lincoln", "was", "president"});
    BagOfWords bag3 = new BagOfWords(new String[]{"bill", "clinton", "was", "president", "states"});
    
    Set<BagOfWords> bags = new HashSet<>();
    bags.add(bag1);
    bags.add(bag2);
    bags.add(bag3);
    
    Map<String, double[]> tokens = BagOfWords.normalize(bags);
    
    /*for(String s : tokens.keySet()) {
      
      System.out.println("Token: " + s);
      System.out.println("Values: " + Arrays.toString(tokens.get(s)));
      System.out.println("-----------");
      
    }*/
    
    double[] vals = tokens.get("president");
    assertEquals((double) 1 / 3, vals[0], 0);
    
  }
  
}
