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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;
import ai.idylnlp.nlp.utils.ngrams.NgramUtils;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.neovisionaries.i18n.LanguageCode;

/**
 * Implementation of {@link EntityRecognizer} that uses a dictionary.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class DictionaryEntityRecognizer implements EntityRecognizer {

  private static final Logger LOGGER = LogManager.getLogger(DictionaryEntityRecognizer.class);

  private LanguageCode languageCode;
  private Set<String> dictionary;
  private String type;
  private double fpp = 0.1;
  private boolean caseSensitive;

  /**
   * Creates a new dictionary entity recognizer.
   * @param dictionary A list of strings, one per line, for the dictionary.
   * @param type The type of entity being extracted. There is a one-to-one relationship
   * between dictionary and entity type.
   * @param fpp The desired false positive probability. Use this to tune the performance.
   */
  public DictionaryEntityRecognizer(LanguageCode languageCode, Set<String> dictionary,
      String type, double fpp, boolean caseSensitive) {

    this.languageCode = languageCode;
    this.dictionary = dictionary;
    this.type = type;
    this.fpp = fpp;
    this.caseSensitive = caseSensitive;

  }

  /**
   * Creates a new dictionary entity recognizer.
   * @param dictionaryFile The {@link File file} defining the dictionary.
   * @param type The type of entity being extracted. There is a one-to-one relationship
   * between dictionary and entity type.
   * @param fpp The desired false positive probability. Use this to tune the performance.
   * @throws IOException Thrown if the dictionary file cannot be accessed.
   */
  public DictionaryEntityRecognizer(LanguageCode languageCode, File dictionaryFile, String type,
      double fpp, boolean caseSensitive) throws IOException {

    this.languageCode = languageCode;
    this.type = type;
    this.fpp = fpp;
    this.caseSensitive = caseSensitive;

    try(BufferedReader br = Files.newBufferedReader(dictionaryFile.toPath(), StandardCharsets.UTF_8)) {

        for(String line = null; (line = br.readLine()) != null;) {

          if(!line.startsWith("#")) {

            if(!caseSensitive) {
              dictionary.add(line.toLowerCase());
            } else {
              dictionary.add(line);
            }

          }

        }

    }

  }

  @Override
  public EntityExtractionResponse extractEntities(EntityExtractionRequest request) throws EntityFinderException {

    final Set<Entity> entities = new LinkedHashSet<Entity>();

    long startTime = System.currentTimeMillis();

    final String[] tokens = request.getText();

    try {

      final BloomFilter<String> filter = BloomFilter.create(
          Funnels.stringFunnel(Charset.defaultCharset()), dictionary.size(), fpp);

      for(String entry : dictionary) {

        if(!caseSensitive) {
          filter.put(entry.toLowerCase());
        } else {
          filter.put(entry);
        }

      }

      // Break the tokens into n-grams because some dictionary entries
      // may be more than one token.
      final String[] ngrams = NgramUtils.getNgrams(tokens);

      for(String ngram : ngrams) {

        boolean mightContain;

        if(!caseSensitive) {
          mightContain = filter.mightContain(ngram.toLowerCase());
        } else {
          mightContain = filter.mightContain(ngram);
        }

        if(mightContain) {

          // Make sure it does exist in the dictionary.
          boolean contains;

          if(!caseSensitive) {
            contains = dictionary.contains(ngram.toLowerCase());
          } else {
            contains = dictionary.contains(ngram);
          }

          if(contains) {

            // Find the span for this entity.
            String[] d = ngram.split(" ");
            int start = Collections.indexOfSubList(Arrays.asList(ngrams), Arrays.asList(d));

            // Create a new entity object.
            final Entity entity = new Entity(ngram, 100.0, type, languageCode.getAlpha3().toString(),
            		request.getContext(), request.getDocumentId());
            entity.setSpan(new ai.idylnlp.model.entity.Span(start, start + d.length - 1));
            entity.setExtractionDate(System.currentTimeMillis());

            LOGGER.debug("Found entity with text: {}", ngram);

            entities.add(entity);

          }

        }

      }

      final long extractionTime = (System.currentTimeMillis() - startTime);

      return new EntityExtractionResponse(entities, extractionTime, true);

    } catch (Exception ex) {

      LOGGER.error("Unable to find entities with the DictionaryEntityRecognizer.", ex);

      throw new EntityFinderException("Unable to find entities with the DictionaryEntityRecognizer.", ex);

    }

  }

}
