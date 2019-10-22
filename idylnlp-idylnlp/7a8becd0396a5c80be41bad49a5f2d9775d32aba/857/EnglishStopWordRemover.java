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
package ai.idylnlp.nlp.utils;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import ai.idylnlp.model.nlp.language.StopWordRemover;

/**
 * Class to remove English stop words from a given text.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class EnglishStopWordRemover implements StopWordRemover {

  private Set<String> words;

  public EnglishStopWordRemover() {

    // List linked from Wikipedia article on stop words: http://www.textfixer.com/resources/common-english-words.txt
    String stopWords = "a,able,about,across,after,all,almost,also,am,among,an,and,any,are,as,at,be,because,been,but,by,can,cannot,could,dear,did,do,does,either,else,ever,every,for,from,get,got,had,has,have,he,her,hers,him,his,how,however,i,if,in,into,is,it,its,just,least,let,like,likely,may,me,might,most,must,my,neither,new,no,nor,not,of,off,often,on,only,or,other,our,own,rather,said,say,says,she,should,since,so,some,than,that,the,their,them,then,there,these,they,this,tis,to,too,twas,us,wants,was,we,were,what,when,where,which,while,who,whom,why,will,with,would,yet,you,your";
    words = new HashSet<String>(Arrays.asList(stopWords.split(",")));

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isStopWord(String input) {

    if(words.contains(input.trim().toLowerCase())) {

      return true;

    } else {

      return false;

    }

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Collection<String> removeStopWords(Collection<String> input) {

    Collection<String> stemmedWords = new LinkedList<String>();

    for(String word : input) {

      if(words.contains(word.toLowerCase()) == false) {
        stemmedWords.add(word.replace(".", "").replace(",", ""));
      }

    }

    return stemmedWords;

  }

}
