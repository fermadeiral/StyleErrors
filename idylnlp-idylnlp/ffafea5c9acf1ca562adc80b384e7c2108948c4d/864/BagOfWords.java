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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import org.apache.commons.collections4.Bag;
import org.apache.commons.collections4.bag.HashBag;
import ai.idylnlp.nlp.utils.ngrams.NgramUtils;

/**
 * A bag of words or a single document.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class BagOfWords {
  
  private Bag<String> bag;

  /**
   * Creates a new bag of words from the given tokens. For
   * best performance, the tokens should be pre-processed to
   * be lowercase and free of stopwords.
   * @param tokens The tokens.
   */
  public BagOfWords(String[] tokens) {
    
    bag = new HashBag<>();
    
    for(String token : tokens) {
      bag.add(token);
    }
    
  }
  
  /**
   * Creates a new bag of words from the given tokens. For
   * best performance, the tokens should be pre-processed to
   * be lowercase and free of stopwords.
   * @param tokens The tokens.
   * @param cutoff The minimum number of times a token
   * must appear in order to be included in the bag.
   */
  public BagOfWords(String[] tokens, int cutoff) {
    
    bag = new HashBag<>();
    
    for(String token : tokens) {
      bag.add(token);
    }
    
    removeBelowMinimum(cutoff);
    
  }
  
  /**
   * Creates a new bag of words from n-grams generated from
   * the given tokens. For best performance, the tokens should
   * be pre-processed to be lowercase and free of stopwords.
   * @param tokens The tokens.
   * @param cutoff The minimum number of times a token
   * must appear in order to be included in the bag.
   * @param ngramsLength The length of the n-grams. Must be
   * greater than or equal to 2.
   */
  public BagOfWords(String[] tokens, int cutoff, int ngramsLength) {
    
    if(ngramsLength < 2) {
      throw new IllegalArgumentException("Length of n-grams must be at least 2.");
    }
    
    bag = new HashBag<>();
    
    final String[] ngrams = NgramUtils.getNgrams(tokens, ngramsLength);
    
    for(String token : ngrams) {
      bag.add(token);
    }

    removeBelowMinimum(cutoff);
    
  }
  
  /**
   * Normalizes a set of bags. The normalization is done by
   * getting all unique tokens across the bags. The counts are
   * then normalized to values between 0 and 1 such that the
   * counts sum to 1.
   * @param bags A set of {@link BagOfWords bags}.
   * @return A map of tokens to normalized values.
   */
  public static Map<String, double[]> normalize(Set<BagOfWords> bags) {
    
    Map<String, double[]> tokens = new HashMap<>();
    
    Set<String> unique = new HashSet<>();
    
    for(BagOfWords bag : bags) {
      unique.addAll(bag.uniqueSet());
    }
    
    for(String token : unique) {
      
      // Make an array the size of the number of bags.
      int[] counts = new int[bags.size()];
      
      int x = 0;
      
      // Get the count of this token for each bag.
      for(BagOfWords bag : bags) {
        counts[x++] = bag.getCount(token);
      }
      
      int sum = IntStream.range(0, counts.length).map(i -> counts[i]).sum();
      
      // Normalize the counts to sum to 1.
      double[] normalized = new double[bags.size()];
      IntStream.range(0, counts.length).forEach(i -> normalized[i] = counts[i] / (double) sum);
     
      tokens.put(token, normalized);
      
    }
    
    return tokens;
    
  }
  
  public Set<String> uniqueSet() {
    return bag.uniqueSet();
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
  
  private void removeBelowMinimum(int cutoff) {    
    bag.removeIf(item -> bag.getCount(item) < cutoff);    
  }
  
}
