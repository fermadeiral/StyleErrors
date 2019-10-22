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
package ai.idylnlp.model.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * An entity.
 *
 * @author Mountain Fog, Inc.
 */
public class Entity implements Serializable {

  private static final long serialVersionUID = 5297145844547340358L;

  private String text;
  private double confidence;
  private Span span;
  private String type;
  private String uri;
  private String languageCode;
  private String context;
  private String documentId;
  private long extractionDate;
  private Map<String, String> metadata;

  /**
   * Create a new entity.
   */
  public Entity() {

    this.metadata = new HashMap<String, String>();

  }

  /**
   * Create a new entity from an existing entity.
   * @param entity An existing entity.
   */
  public Entity(Entity entity) {

	this.confidence = entity.getConfidence();
    this.metadata = entity.getMetadata();
	this.type = entity.getType();
	this.languageCode = entity.getLanguageCode();
	this.span = entity.getSpan();
	this.text = entity.getText();
	this.uri = entity.getUri();
	this.context = entity.getContext();
	this.documentId = entity.getDocumentId();

  }

  /**
   * Create a new entity given the attributes of the entity.
   * @param text The text of the entity.
   * @param confidence The confidence the text as determined during the extraction. This will be a decimal value between 0 and 100. A higher value indicates a higher level of confidence.
   * @param type The type of entity.
   * @param languageCode The two-letter ISO language code of the entity.
   * @param context The context.
   * @param documentId The document ID.
   */
  public Entity(String text, double confidence, String type, String languageCode, String context, String documentId) {

    this.text = text;
    this.confidence = confidence;
    this.type = type;
    this.metadata = new HashMap<String, String>();
    this.languageCode = languageCode;
    this.context = context;
    this.documentId = documentId;
    
  }

  /**
   * Create a new entity given the attributes of the entity.
   * @param text The text of the entity.
   * @param confidence The confidence the text as determined during the extraction. This will be a decimal value between 0 and 100. A higher value indicates a higher level of confidence.
   * @param span The location of the entity in the text.
   * @param type The type of entity.
   * @param languageCode The two-letter ISO language code of the entity.
   * @param context The context.
   * @param documentId The document ID.
   */
  public Entity(String text, double confidence, String type, Span span, String languageCode,
		  String context, String documentId) {

    this.text = text;
    this.confidence = confidence;
    this.span = span;
    this.type = type;
    this.metadata = new HashMap<String, String>();
    this.languageCode = languageCode;
    this.context = context;
    this.documentId = documentId;
    
  }

  /**
   * Create a new entity given the attributes of the entity.
   * @param text The text of the entity.
   */
  public Entity(String text) {

    this.text = text;
    this.metadata = new HashMap<String, String>();

  }

  /**
   * Create a new entity given the attributes of the entity.
   * @param text The text of the entity.
   * @param type The type of entity.
   */
  public Entity(String text, String type) {

    this.text = text;
    this.type = type;
    this.metadata = new HashMap<String, String>();

  }

  /**
     * {@inheritDoc}
     */
  @Override
  public int hashCode() {

    return new HashCodeBuilder(17, 31)
      .append(text)
            .append(confidence)
            .append(span)
            .append(type)
            .append(uri)
            .append(languageCode)
            .append(context)
            .append(documentId)
            .toHashCode();

  }

  /**
     * {@inheritDoc}
     */
  @Override
  public boolean equals(Object obj) {

      if(obj != null && obj instanceof Entity) {

          final Entity other = (Entity) obj;

          return new EqualsBuilder()
              .append(text, other.text)
              .append(confidence, other.confidence)
              .append(span, other.span)
              .append(type, other.type)
              .append(uri, other.uri)
              .append(languageCode, other.languageCode)
              .append(context, other.context)
              .append(documentId, other.documentId)
              .isEquals();

      }

      return false;

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {

    StringBuilder sb = new StringBuilder();
    sb.append("Text: " + text + "; ");
    sb.append("Confidence: " + confidence + "; ");
    sb.append("Type: " + type + "; ");
    sb.append("Language Code: " + languageCode + "; ");

    if(span != null) {
      sb.append("Span: " + span.toString() + "; ");
    }

    return sb.toString();

  }

  /**
   * Gets the text of the entity.
   * @return The text of the entity.
   */
  public String getText() {
    return text;
  }

  /**
   * Sets the text of the entity.
   * @param text The text of the entity.
   */
  public void setText(String text) {
    this.text = text;
  }

  /**
   * Gets the confidence the entity actually is an entity.
   * @return The confidence the entity is actually an entity. This will be a value between 0 and 100.
   */
  public double getConfidence() {
    return confidence;
  }

  /**
   * Sets the confidence the entity actually is an entity.
   * @param confidence The confidence of the entity. This should be a value between 0 and 100.
   */
  public void setConfidence(double confidence) {
    this.confidence = confidence;
  }

  /**
   * Gets the URI of the entity.
   * @return The entity URI.
   */
  public String getUri() {
    return uri;
  }

  /**
   * Sets the URI of the entity.
   * @param uri The entity URI.
   */
  public void setUri(String uri) {
    this.uri = uri;
  }

  /**
   * Gets the entity {@link Span} in the text.
   * @return The entity {@link Span} in the text.
   */
  public Span getSpan() {
    return span;
  }

  /**
   * Sets the entity {@link Span} in the text.
   * @param span The entity {@link Span} in the text.
   */
  public void setSpan(Span span) {
    this.span = span;
  }

  /**
   * Gets the language code of the entity.
   * @return The language code.
   */
  public String getLanguageCode() {
    return languageCode;
  }

  /**
   * Sets the language code for the entity.
   * @param languageCode The language code.
   */
  public void setLanguageCode(String languageCode) {
    this.languageCode = languageCode;
  }

  /**
   * Gets the type of the entity.
   * @return The type of the entity.
   */
  public String getType() {
    return type;
  }

  /**
   * Sets the type of the entity.
   * @param type The type of the entity.
   */
  public void setType(String type) {
    this.type = type;
  }

  /**
   * Gets the context of the entity.
   * @return The context of the entity.
   */
  public String getContext() {
    return context;
  }

  /**
   * Sets the context of the entity.
   * @param context The context of the entity.
   */
  public void setContext(String context) {
    this.context = context;
  }

  /**
   * Gets the document ID of the entity.
   * @return The document ID.
   */
  public String getDocumentId() {
    return documentId;
  }

  /**
   * Sets the document ID of the entity.
   * @param documentId The document ID.
   */
  public void setDocumentId(String documentId) {
    this.documentId = documentId;
  }

  /**
   * Gets the extraction date.
   * @return The extraction date.
   */
  public long getExtractionDate() {
    return extractionDate;
  }

  /**
   * Sets the extraction date.
   * @param extractionDate The extraction date.
   */
  public void setExtractionDate(long extractionDate) {
    this.extractionDate = extractionDate;
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

}