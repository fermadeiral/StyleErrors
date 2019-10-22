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

package ai.idylnlp.nlp.text.metrics.model;

import java.util.Map;

public class TextMetricsResult {

	private double uniqueWords;
	private double totalWords;
	private double characterCount;
	private double totalSentences;
	private double maxSentenceLength;
	private double avgSentenceLength;
	private double totalSyllables;
	private Map<String, Integer> ngrams;
	
	public TextMetricsResult(double uniqueWords, double totalWords, double characterCount, double totalSentences,
		double maxSentenceLength, double avgSentenceLength, double totalSyllables, Map<String, Integer> ngrams) {
		
		this.uniqueWords = uniqueWords;
		this.totalWords = totalWords;
		this.characterCount = characterCount;
		this.totalSentences = totalSentences;
		this.maxSentenceLength = maxSentenceLength;
		this.avgSentenceLength = avgSentenceLength;
		this.totalSyllables = totalSyllables;
		this.ngrams = ngrams;

	}
	
	/**
	 * Calculates the Flesch-Kincaid grade level for the text.
	 * See https://en.wikipedia.org/wiki/Flesch%E2%80%93Kincaid_readability_tests.
	 * @return The Flesch-Kincaid readability value of the text, or <code>-1</code>
	 * if there are less than 100 words in the text.
	 */
	public double getFleschKincaidGradeLevel() {
		
		if(totalWords >= 100) {
			return 0.39 * (totalWords / totalSentences) + 11.8 * (totalSyllables / totalWords) - 15.59;
		} else {
			return -1;
		}
		
	}

	public double getUniqueWords() {
		return uniqueWords;
	}

	public void setUniqueWords(double uniqueWords) {
		this.uniqueWords = uniqueWords;
	}

	public double getTotalWords() {
		return totalWords;
	}

	public void setTotalWords(double totalWords) {
		this.totalWords = totalWords;
	}

	public double getCharacterCount() {
		return characterCount;
	}

	public void setCharacterCount(double characterCount) {
		this.characterCount = characterCount;
	}

	public double getTotalSentences() {
		return totalSentences;
	}

	public void setTotalSentences(double totalSentences) {
		this.totalSentences = totalSentences;
	}

	public double getMaxSentenceLength() {
		return maxSentenceLength;
	}

	public void setMaxSentenceLength(double maxSentenceLength) {
		this.maxSentenceLength = maxSentenceLength;
	}

	public double getAvgSentenceLength() {
		return avgSentenceLength;
	}

	public void setAvgSentenceLength(double avgSentenceLength) {
		this.avgSentenceLength = avgSentenceLength;
	}

	public double getTotalSyllables() {
		return totalSyllables;
	}

	public void setTotalSyllables(double totalSyllables) {
		this.totalSyllables = totalSyllables;
	}
	
    public Map<String, Integer> getNgrams() {
        return ngrams;
    }

    public void setNgrams(Map<String, Integer> ngrams) {
        this.ngrams = ngrams;
    }
	
	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("Unique words: " + uniqueWords + "\n");
		sb.append("Total words: " + totalWords + "\n");
		sb.append("Character count: " + characterCount + "\n");
		sb.append("Total sentence: " + totalSentences + "\n");
		sb.append("Max sentence length: " + maxSentenceLength + "\n");
		sb.append("Avg sentence length: " + avgSentenceLength + "\n");
		sb.append("Total syllables: " + totalSyllables + "\n");
		sb.append("Flesch-Kincaid Grade Level: " + getFleschKincaidGradeLevel());
		return sb.toString();
		
	}
}
