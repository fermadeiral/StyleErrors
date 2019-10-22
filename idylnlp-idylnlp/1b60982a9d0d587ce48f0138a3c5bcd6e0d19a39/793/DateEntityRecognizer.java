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

import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.joestelmach.natty.DateGroup;
import com.joestelmach.natty.Parser;
import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.exceptions.EntityFinderException;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;
import ai.idylnlp.nlp.utils.SpanUtils;
import com.neovisionaries.i18n.LanguageCode;

import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

/**
 * An {@link EntityRecognizer} that identifies dates.
 * This only works for English text.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class DateEntityRecognizer implements EntityRecognizer {

  private static final Logger LOGGER = LogManager.getLogger(DateEntityRecognizer.class);

  private static final String ENTITY_TYPE = "date";

  /**
   * {@inheritDoc} 
   */
  @Override
  public EntityExtractionResponse extractEntities(EntityExtractionRequest entityExtractionRequest) throws EntityFinderException {

    Set<Entity> entities = new LinkedHashSet<>();
    
    long startTime = System.currentTimeMillis();

    Parser parser = new Parser();
    
    final String text = StringUtils.join(entityExtractionRequest.getText(), " ");

    List<DateGroup> groups = parser.parse(text);

    for (DateGroup group : groups) {
      
      List<Date> dates = group.getDates();
      
      for(Date date : dates) {
        
        final String entityText = date.toString() + " (" + group.getText() + ")"; 
        
        Entity entity = new Entity(entityText);
        entity.setConfidence(100);
        entity.setType(ENTITY_TYPE);
        entity.getMetadata().put("time", String.valueOf(date.getTime()));
        entity.setContext(entityExtractionRequest.getContext());
        entity.setExtractionDate(System.currentTimeMillis());
        entity.setLanguageCode(LanguageCode.en.getAlpha3().toString());
        
        // TODO: Set the token-based span correctly.
        Span span = SpanUtils.getSpan(SimpleTokenizer.INSTANCE, group.getText(), text);
        entity.setSpan(new ai.idylnlp.model.entity.Span(span.getStart(), span.getEnd()));
        
        entities.add(entity);
        
      }
      
    }
    
    long elapsedTime = System.currentTimeMillis() - startTime;
    
    return new EntityExtractionResponse(entities, elapsedTime, true);

  }

}
