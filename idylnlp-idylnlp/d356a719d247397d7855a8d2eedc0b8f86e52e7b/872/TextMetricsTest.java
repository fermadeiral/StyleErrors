package ai.idylnlp.test.nlp.text.metrics;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

import ai.idylnlp.model.nlp.SentenceDetector;
import ai.idylnlp.model.nlp.Tokenizer;
import ai.idylnlp.nlp.sentence.SimpleSentenceDetector;
import ai.idylnlp.nlp.text.metrics.TextMetrics;
import ai.idylnlp.nlp.text.metrics.model.TextMetricsResult;
import ai.idylnlp.nlp.tokenizers.WhitespaceTokenizer;

public class TextMetricsTest {
	
	private static final Logger LOGGER = LogManager.getLogger(TextMetricsTest.class);

	@Test
	public void calculate1() {
		
		SentenceDetector sd = new SimpleSentenceDetector();
		Tokenizer t = WhitespaceTokenizer.INSTANCE;
		
		TextMetrics metrics = new TextMetrics(sd, t);
		TextMetricsResult result = metrics.calculate("This is some sample text.");
		
		LOGGER.info(result);
		
		assertEquals(result.getUniqueWords(), 5, 0);
		assertEquals(result.getTotalWords(), 5, 0);
		assertEquals(result.getFleschKincaidGradeLevel(), -1, 0);
		
	}
	
	@Test
	public void calculate2() {
		
		SentenceDetector sd = new SimpleSentenceDetector();
		Tokenizer t = WhitespaceTokenizer.INSTANCE;
		
		TextMetrics metrics = new TextMetrics(sd, t);
		TextMetricsResult result = metrics.calculate("This is some sample text. This is a second sentence.");
		
		LOGGER.info(result);
		
		assertEquals(result.getUniqueWords(), 8, 0);
		assertEquals(result.getTotalWords(), 10, 0);
		assertEquals(result.getFleschKincaidGradeLevel(), -1, 0);
		
	}
	
	@Test
	public void calculate3() {
		
		SentenceDetector sd = new SimpleSentenceDetector();
		Tokenizer t = WhitespaceTokenizer.INSTANCE;
		
		TextMetrics metrics = new TextMetrics(sd, t);
		TextMetricsResult result = metrics.calculate("This is some some sample text.");
		
		LOGGER.info(result);
		
		assertEquals(result.getUniqueWords(), 5, 0);
		assertEquals(result.getTotalWords(), 6, 0);
		assertEquals(result.getFleschKincaidGradeLevel(), -1, 0);
		
	}
	
}
