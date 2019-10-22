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
package ai.idylnlp.zoo;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.zoo.model.Model;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Client for the Idyl NLP model zoo that facilitates downloading
 * NLP models from the zoo.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class IdylNLPModelZoo {

  private static final String IDYLNLP_MODEL_ZOO_ENDPOINT = "https://zoo.idylnlp.ai";

  private ModelZooClient client;
  private String token;

  /**
   * Creates a new client using the default Idyl NLP endpoint.
   * @param token The client token.
   */
  public IdylNLPModelZoo(String token) {

    this.token = token;

    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(IDYLNLP_MODEL_ZOO_ENDPOINT)
      .addConverterFactory(GsonConverterFactory.create())
      .build();

    client = retrofit.create(ModelZooClient.class);

  }

  /**
   * Creates a new client.
   * @param endpoint The Idyl NLP model zoo endpoint.
   * @param token The client token.
   */
  public IdylNLPModelZoo(String endpoint, String token) {

    this.token = token;

    final Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(endpoint)
      .addConverterFactory(GsonConverterFactory.create())
      .build();

    client = retrofit.create(ModelZooClient.class);

  }

  /**
   * Downloads a model from the zoo.
   * @param modelId The model's ID.
   * @param destination A {@link File} to hold the downloaded model.
   * @throws IOException Thrown if the model file cannot be downloaded.
   */
  public void downloadModel(String modelId, File destination) throws IOException {

    final ResponseBody responseBody = client.getModelUrl(token, modelId).execute().body();

    FileUtils.copyInputStreamToFile(responseBody.byteStream(), destination);

  }

  /**
   * Finds all available models for a given language.
   * @param language The language.
   * @return
   * @throws IOException
   */
  public List<Model> getModelsByLanguage(LanguageCode languageCode) throws IOException {

    return client.getModelsForLanguage(token, languageCode.getAlpha3().toString().toLowerCase()).execute().body();

  }

}
