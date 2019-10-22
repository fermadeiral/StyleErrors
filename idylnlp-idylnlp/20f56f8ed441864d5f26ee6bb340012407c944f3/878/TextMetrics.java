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

package ai.idylnlp.nlp.text.metrics;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.nlp.text.metrics.model.TextMetricsResult;
import eu.crydee.syllablecounter.SyllableCounter;

/**
 * Calculates various metrics of text.
 * @author Mountain Fog, Inc.
 *
 */
public class TextMetrics {
	
	private static final Logger LOGGER = LogManager.getLogger(TextMetrics.class);
	
	private SentenceDetector sentenceDetector;
	private Tokenizer tokenizer;
	
	/**
	 * Creates a new instance.
	 * @param sentenceDetector The {@link SentenceDetector} for extracting sentences
	 * from the text.
	 * @param tokenizer The {@link Tokenizer} for extracting tokens
	 * from the text.
	 */
	public TextMetrics(SentenceDetector sentenceDetector, Tokenizer tokenizer) {
		
		this.sentenceDetector = sentenceDetector;
		this.tokenizer = tokenizer;

	}

	/**
	 * Calculates metrics of text.
	 * @param text The text.
	 * @return A {@link TextMetricsResult} containing the metrics.
	 */
	public TextMetricsResult calculate(String text) {
		
		SummaryStatistics stats = new SummaryStatistics();
		
		SyllableCounter sc = new SyllableCounter();
		
		Set<String> uniqueWords = new LinkedHashSet<>();
		int tokenCount = 0;
		int maxSentenceLength = 0;
		int totalSyllables = 0;
		
		String[] sentences = sentenceDetector.sentDetect(text);
		
		for(String sentence : sentences) {
			
			String[] tokens = tokenizer.tokenize(sentence);

			for(String token : tokens) {
				
				uniqueWords.add(token);
				
				totalSyllables += sc.count(token);
				
			}
			
			stats.addValue(tokens.length);
			
			tokenCount += tokens.length;
			
			if(sentence.length() > maxSentenceLength) {
				maxSentenceLength = sentence.length();
			}
			
		}
		
		TextMetricsResult result = new TextMetricsResult(uniqueWords.size(), tokenCount, text.length(), sentences.length,
				maxSentenceLength, stats.getMean(), totalSyllables);
		
		return result;
		
	}
	
}
