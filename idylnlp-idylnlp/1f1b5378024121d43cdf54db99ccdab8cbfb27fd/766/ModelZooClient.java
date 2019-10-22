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

import java.util.List;

import ai.idylnlp.zoo.model.Model;

import okhttp3.ResponseBody;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Streaming;

public interface ModelZooClient {

  @GET("/model/{id}")
  @Streaming
    Call<ResponseBody> getModelUrl(@Header("X-Token") String token, @Path("id") String modelId);

  @GET("/models/{language}")
    Call<List<Model>> getModelsForLanguage(@Header("X-Token") String token, @Path("language") String language);

}
