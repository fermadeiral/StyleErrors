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
package ai.idylnlp.nlp.features;

import java.util.Iterator;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
import ai.idylnlp.nlp.utils.ngrams.NgramUtils;

/**
 * A bag of words.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class BagOfWords {
  
  private Bag<String> bag;
  
  /**
   * Creates a new bag of words from the given tokens.
   * @param tokens The tokens.
   */
  public BagOfWords(String[] tokens) {
    
    bag = new HashBag<>();
    
    for(String token : tokens) {
      bag.add(token);
    }
    
  }
  
  /**
   * Creates a new bag of words from the given tokens.
   * @param tokens The tokens.
   * @param minOccurrences The minimum number of times a token
   * must appear in order to be included in the bag.
   */
  public BagOfWords(String[] tokens, int minOccurrences) {
    
    bag = new HashBag<>();
    
    for(String token : tokens) {
      bag.add(token);
    }
    
    removeBelowMinimum(minOccurrences);
    
  }
  
  /**
   * Creates a new bag of words from n-grams generated from
   * the given tokens.
   * @param tokens The tokens.
   * @param minOccurrences The minimum number of times a token
   * must appear in order to be included in the bag.
   * @param ngramsLength The length of the n-grams. Must be
   * greater than or equal to 2.
   */
  public BagOfWords(String[] tokens, int minOccurrences, int ngramsLength) {
    
    if(ngramsLength < 2) {
      throw new IllegalArgumentException("Length of n-grams must be at least 2.");
    }
    
    bag = new HashBag<>();
    
    final String[] ngrams = NgramUtils.getNgrams(tokens, ngramsLength);
    
    for(String token : ngrams) {
      bag.add(token);
    }

    removeBelowMinimum(minOccurrences);
    
  }

  public int getCount(String token) {
    return bag.getCount(token);
  }
  
  public int size() {
    return bag.size();
  }
  
  public Iterator<String> iterator() {
    return bag.iterator();
  }
  
  public boolean isEmpty() {
    return bag.isEmpty();
  }
  
  public boolean contains(String token) {
    return bag.contains(token);
  }

  public void clear() {
    bag.clear();
  }
  
  private void removeBelowMinimum(int minimum) {    
    bag.removeIf(item -> bag.getCount(item) < minimum);    
  }
  
}
