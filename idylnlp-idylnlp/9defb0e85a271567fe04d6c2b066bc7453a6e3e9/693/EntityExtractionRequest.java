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
package ai.idylnlp.model.nlp.ner;

import java.util.LinkedHashMap;
import java.util.Map;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.model.nlp.DuplicateEntityStrategy;
import ai.idylnlp.model.nlp.EntityOrder;
import ai.idylnlp.model.nlp.Location;
import ai.idylnlp.model.nlp.pipeline.PipelineRequest;

/**
 * Request to extract entities from text.
 *
 * @author Mountain Fog, Inc.
 */
public class EntityExtractionRequest extends PipelineRequest {

  private String[] text;
  private int confidenceThreshold = 0;
  private LanguageCode languageCode = null;
  private String context = "not-set";
  private EntityOrder order = EntityOrder.OCCURRENCE;
  private Location location;
  private DuplicateEntityStrategy duplicateEntityStrategy;
  private Map<String, String> metadata;
  private String type = null;
  private boolean includeModelFileNameInMetadata = true;

  /**
   * Create a request to extract entities. This request will use
   * a confidence threshold of zero unless manually set.
   * It is assumed the input language is English.
   * @param text The text to extract entities from.
   */
  public EntityExtractionRequest(String[] text) {

    this.text = text;
    this.metadata = new LinkedHashMap<String, String>();

  }

  /**
   * Sets the entity metadata.
   * @param metadata The entity metadata.
   * @return The {@link EntityExtractionRequest}.
   */
  public EntityExtractionRequest withMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
    return this;
  }

  /**
   * Sets the confidence threshold.
   * @param confidenceThreshold The confidence threshold.
   * @return The {@link EntityExtractionRequest}.
   */
  public EntityExtractionRequest withConfidenceThreshold(int confidenceThreshold) {
    this.confidenceThreshold = confidenceThreshold;
    return this;
  }

  /**
   * Sets the extraction context. This value does not have to be unique.
   * @param context The context for the extraction.
   * @return The {@link EntityExtractionRequest}.
   */
  public EntityExtractionRequest withContext(String context) {
    this.context = context;
    return this;
  }

  /**
   * Sets the type of entity to extract.
   * @param type The type of entity to extract.
   * @return The {@link EntityExtractionRequest}.
   */
  public EntityExtractionRequest withType(String type) {
    this.type = type;
    return this;
  }

  /**
   *
   * @param language The {@link LanguageCode} of the input text.
   * @return The {@link EntityExtractionRequest}.
   */
  public EntityExtractionRequest withLanguage(LanguageCode language) {
    this.languageCode = language;
    return this;
  }

  /**
   * The sort {@link EntityOrder order} for the returned entities.
   * @param order The sort {@link EntityOrder order} for the returned entities.
   * @return Returns a reference to this object so that method calls can be chained together.
   */
  public EntityExtractionRequest withOrder(EntityOrder order) {
    this.order = order;
    return this;
  }

  /**
   * Sets a {@link Location location} attribute for the entity extraction request.
   * @param location The {@link Location location}.
   * @return Returns a reference to this object so that method calls can be chained together.
   */
  public EntityExtractionRequest withLocation(Location location) {
    this.location = location;
    return this;
  }

  /**
   * Sets the duplicate entity {@link DuplicateEntityStrategy strategy}. If set this value overrides
   * the duplicate entity strategy set for the pipeline for this request only.
   * @param duplicateEntityStrategy The {@link DuplicateEntityStrategy strategy}.
   * @return Returns a reference to this object so that method calls can be chained together.
   */
  public EntityExtractionRequest withDuplicateEntityStrategy(DuplicateEntityStrategy duplicateEntityStrategy) {
    this.duplicateEntityStrategy = duplicateEntityStrategy;
    return this;
  }

  /**
   * Whether or not to include the filename of the model that extracted the entity in
   * the entity's metadata.
   * @param includeModelFileNameInMetadata A boolean indicating whether or not to include the
   * filename of the model that extracted this entity.
   * @return Returns a reference to this object so that method calls can be chained together.
   */
  public EntityExtractionRequest withIncludeModelFileNameInMetadata(boolean includeModelFileNameInMetadata) {
    this.includeModelFileNameInMetadata = includeModelFileNameInMetadata;
    return this;
  }

  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();

    sb.append("Text: " + text + "; Confidence Threshold: " + confidenceThreshold + "; Context: " + context + "; Language: " + languageCode.getAlpha3().toString() + "; ");

    return sb.toString();

  }

  /**
   * Gets the value of the confidence threshold.
   * @return The value of the confidence threshold.
   */
  public int getConfidenceThreshold() {
    return confidenceThreshold;
  }

  /**
   * Sets the value of the confidence threshold.
   * @param confidenceThreshold The value of the confidence threshold. Valid values are 0-100.
   */
  public void setConfidenceThreshold(int confidenceThreshold) {
    this.confidenceThreshold = confidenceThreshold;
  }

  /**
   * Gets the text to be processed.
   * @return The text to be processed.
   */
  public String[] getText() {
    return text;
  }

  /**
   * Gets the language of the input text.
   * @return The {@link LanguageCode} of the input text.
   */
  public LanguageCode getLanguage() {
    return languageCode;
  }

  /**
   * Sets the language of the input text.
   * @param languageCode The {@link LanguageCode}.
   */
  public void setLanguage(LanguageCode languageCode) {
    this.languageCode = languageCode;
  }

  /**
   * Gets the context used for the extraction.
   * @return The context.
   */
  public String getContext() {
    return context;
  }

  /**
   * Gets the sort {@link EntityOrder order} for the returned entities.
   * @return The sort {@link EntityOrder order} for the returned entities.
   */
  public EntityOrder getOrder() {
    return order;
  }

  /**
   * Sets the sort {@link EntityOrder order} for the returned entities.
   * @param order The {@link EntityOrder order} for the returned entities.
   */
  public void setOrder(EntityOrder order) {
    this.order = order;
  }

  /**
   * Sets the context for the extraction.
   * @param context The context for the extraction.
   */
  public void setContext(String context) {
    this.context = context;
  }

  /**
   * Gets the {@link Location location} attribute.
   * @return The {@link Location location}.
   */
  public Location getLocation() {
    return location;
  }

  /**
   * Sets the {@link Location location} attribute.
   * @param location The {@link Location location}.
   */
  public void setLocation(Location location) {
    this.location = location;
  }

  /**
   * Gets the duplicate entity strategy.
   * @return The duplicate entity {@link DuplicateEntityStrategy strategy}.
   */
  public DuplicateEntityStrategy getDuplicateEntityStrategy() {
    return duplicateEntityStrategy;
  }

  /**
   * Sets the duplicate entity strategy. This value overrides
   * the duplicate entity strategy set for the pipeline for this request only.
   * @param duplicateEntityStrategy The duplicate entity {@link DuplicateEntityStrategy strategy}.
   */
  public void setDuplicateEntityStrategy(DuplicateEntityStrategy duplicateEntityStrategy) {
    this.duplicateEntityStrategy = duplicateEntityStrategy;
  }

  /**
   * Gets the entity metadata.
   * @return The entity metadata.
   */
  public Map<String, String> getMetadata() {
    return metadata;
  }

  /**
   * Sets the entity metadata.
   * @param metadata The entity metadata.
   */
  public void setMetadata(Map<String, String> metadata) {
    this.metadata = metadata;
  }

  /**
   * Gets the type of entity to extract.
   * @return The type of entity to extract.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of entity to extract.
   * @param type The type of entity to extract.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets whether or not to include the model's filename that extracted the
   * entity in the entity metadata.
   * @return Whether or not to include the model's filename that extracted the
   * entity in the entity metadata.
   */
  public boolean isIncludeModelFileNameInMetadata() {
    return includeModelFileNameInMetadata;
  }

  /**
   * Sets whether or not to include the model's filename that extracted the
   * entity in the entity metadata.
   * @param includeModelFileNameInMetadata <code>true</code> to include the model's filename
   * in the entity's metadata; otherwise <code>false</code>.
   */
  public void setIncludeModelFileNameInMetadata(
      boolean includeModelFileNameInMetadata) {
    this.includeModelFileNameInMetadata = includeModelFileNameInMetadata;
  }

}
