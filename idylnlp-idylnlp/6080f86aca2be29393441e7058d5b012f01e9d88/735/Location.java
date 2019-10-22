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

import ai.idylnlp.model.nlp.ner.EntityExtractionRequest;

/**
 * A geographic location. Used by {@link EntityExtractionRequest}
 * to add location to extracted entities.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class Location {

  private double latitude;
  private double longitude;

  /**
   * Creates a new location.
   * @param latitude The location's latitude.
   * @param longitude THe location's longitude.
   */
  public Location(double latitude, double longitude) {

    this.latitude = latitude;
    this.longitude = longitude;

  }

  /**
   * Gets the location's latitude.
   * @return The location's latitude.
   */
  public double getLatitude() {

    return latitude;

  }

  /**
   * Gets the location's longitude.
   * @return The location's longitude.
   */
  public double getLongitude() {

    return longitude;

  }

}
