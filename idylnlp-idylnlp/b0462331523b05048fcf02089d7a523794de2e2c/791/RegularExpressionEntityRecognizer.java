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
package ai.idylnlp.nlp.recognizer;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;

import com.neovisionaries.i18n.LanguageCode;

import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.Span;

/**
 * An {@link EntityRecognizer} that identifies entities based
 * on regular expressions.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class RegularExpressionEntityRecognizer implements EntityRecognizer {

  private static final Logger LOGGER = LogManager.getLogger(RegularExpressionEntityRecognizer.class);

  private Pattern pattern;
  private String type;

  /**
   * Creates a regular expression entity recognizer.
   * @param pattern The regular expression {@link Pattern pattern}.
   * @param type The {@link String class} of the entities to identify.
   */
  public RegularExpressionEntityRecognizer(Pattern pattern, String type) {

    this.pattern = pattern;
    this.type = type;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityExtractionResponse extractEntities(EntityExtractionRequest request) throws EntityFinderException {

    long startTime = System.currentTimeMillis();

    Set<Entity> entities = new LinkedHashSet<>();

    try {

      // TODO: Surround all patterns with spaces.
      final String text = StringUtils.join(request.getText(), " ").replaceAll(pattern.pattern(), " $1 ");

      Pattern[] patterns = {pattern};

      // TODO: This recognizer must use the WhitespaceTokenizer.
      Tokenizer tokenizer = WhitespaceTokenizer.INSTANCE;

      // tokenize the text into the required OpenNLP format
            String[] tokens = tokenizer.tokenize(text);

            //the values used in these Spans are string character offsets of each token from the sentence beginning
            Span[] tokenPositionsWithinSentence = tokenizer.tokenizePos(text);

            // find the location names in the tokenized text
            // the values used in these Spans are NOT string character offsets, they are indices into the 'tokens' array
            RegexNameFinder regexNameFinder = new RegexNameFinder(patterns);
            Span names[] = regexNameFinder.find(tokens);

            //for each name that got found, create our corresponding occurrence
            for (Span name : names) {

                //find offsets relative to the start of the sentence
                int beginningOfFirstWord = tokenPositionsWithinSentence[name.getStart()].getStart();

                // -1 because the high end of a Span is noninclusive
                int endOfLastWord = tokenPositionsWithinSentence[name.getEnd() - 1].getEnd();

                //to get offsets relative to the document as a whole, just add the offset for the sentence itself
                //int startOffsetInDoc = sentenceSpan.getStart() + beginningOfFirstWord;
                //int endOffsetInDoc = sentenceSpan.getStart() + endOfLastWord;

                //look back into the original input string to figure out what the text is that I got a hit on
                String nameInDocument = text.substring(beginningOfFirstWord, endOfLastWord);

                // Create a new entity object.
        Entity entity = new Entity(nameInDocument, 100.0, type, LanguageCode.undefined.getAlpha3().toString());
        entity.setSpan(new ai.idylnlp.model.entity.Span(name.getStart(), name.getEnd()));
        entity.setContext(request.getContext());
        entity.setExtractionDate(System.currentTimeMillis());

        LOGGER.debug("Found entity with text: {}", nameInDocument);

        // Add the entity to the list.
        entities.add(entity);

        LOGGER.trace("Found entity [{}] as a {} with span {}.", nameInDocument, type, name.toString());

            }

      long extractionTime = (System.currentTimeMillis() - startTime);

      EntityExtractionResponse response = new EntityExtractionResponse(entities, extractionTime, true);

      return response;

    } catch (Exception ex) {

      LOGGER.error("Unable to find entities with the RegularExpressionEntityRecognizer.", ex);

      throw new EntityFinderException("Unable to find entities with the RegularExpressionEntityRecognizer.", ex);

    }

  }

}
