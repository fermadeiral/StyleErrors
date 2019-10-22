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
package ai.idylnlp.model.nlp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ConfidenceNormalization {

  private static final Logger LOGGER = LogManager.getLogger(ConfidenceNormalization.class);

  /**
   * Normalizes the confidence from an integer between 0 and 100
   * to a decimal value between 0 and 1.
   * @param confidence The confidence to normalize.
   * @return The normalized confidence.
   */
  public static double normalizeConfidence(int confidence) {

    // The confidence threshold comes in as an integer between 0 and 100. We need to divide it by 100 to make it between 0 and 1.
    double normalizedConfidenceThreshold = 0;

    if(confidence > 0) {
      normalizedConfidenceThreshold = ((double) confidence / 100);
    }

    LOGGER.debug("Normalized confidence from {} to {}", confidence, normalizedConfidenceThreshold);

    return normalizedConfidenceThreshold;

  }

}
