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
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.i18n.phonenumbers.PhoneNumberMatch;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import ai.idylnlp.model.entity.Entity;
import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;
import ai.idylnlp.model.nlp.ner.EntityExtractionResponse;
import ai.idylnlp.model.nlp.ner.EntityRecognizer;

public class PhoneNumberEntityRecognizer implements EntityRecognizer {

  private static final Logger LOGGER = LogManager.getLogger(PhoneNumberEntityRecognizer.class);

  private static final String ENTITY_TYPE = "phone";

  @Override
  public EntityExtractionResponse extractEntities(EntityExtractionRequest request) {

    LOGGER.trace("Finding entities with the phone number entity recognizer.");

    Set<Entity> entities = new LinkedHashSet<>();

    Set<String> regions = PhoneNumberUtil.getInstance().getSupportedRegions();

    long startTime = System.currentTimeMillis();

    final String text = StringUtils.join(request.getText(), " ");

    for(String region : regions) {

      Iterable<PhoneNumberMatch> iterable = PhoneNumberUtil.getInstance().findNumbers(text, region);
      List<PhoneNumberMatch> numbers = IteratorUtils.toList(iterable.iterator());

      for(PhoneNumberMatch phoneNumberMatch : numbers) {

        String phoneNumber = String.valueOf(phoneNumberMatch.number().getNationalNumber());

        Entity entity = new Entity();
        entity.setText(phoneNumber);
        entity.setType(ENTITY_TYPE);
        entity.setConfidence(100.0);
        entity.setContext(request.getContext());
        entity.setDocumentId(request.getDocumentId());;
        entity.setExtractionDate(System.currentTimeMillis());

        // TODO: Set the token-based span correctly.
        entity.setSpan(new ai.idylnlp.model.entity.Span(0, 0));

        entities.add(entity);

      }

    }

    long extractionTime = (System.currentTimeMillis() - startTime);

    EntityExtractionResponse response = new EntityExtractionResponse(entities, extractionTime, true);

    return response;

  }

}
