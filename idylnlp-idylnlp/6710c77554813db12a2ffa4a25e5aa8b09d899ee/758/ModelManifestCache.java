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
package ai.idylnlp.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.manifest.StandardModelManifest;

/**
 * Caches model manifests in memory per language and type.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class ModelManifestCache {

  private Map<String, Map<LanguageCode, Set<StandardModelManifest>>> cache;

  public ModelManifestCache() {

    cache = new HashMap<String, Map<LanguageCode, Set<StandardModelManifest>>>();

  }

  public void add(String type, LanguageCode language, Set<StandardModelManifest> modelManifests) {

    Map<LanguageCode, Set<StandardModelManifest>> models = new HashMap<>();
    models.put(language, modelManifests);

    cache.put(type, models);

  }

  public Set<StandardModelManifest> get(String type, LanguageCode language) {

    Set<StandardModelManifest> modelManifests = null;

    Map<LanguageCode, Set<StandardModelManifest>> models = cache.get(type);

    if(models != null) {

      modelManifests = models.get(language);

    }

    return modelManifests;

  }

}
