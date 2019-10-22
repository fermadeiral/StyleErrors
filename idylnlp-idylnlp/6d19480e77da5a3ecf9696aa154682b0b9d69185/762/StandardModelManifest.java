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
package ai.idylnlp.model.manifest;

import java.util.Properties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.neovisionaries.i18n.LanguageCode;

/**
 * A model manifest.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class StandardModelManifest extends ModelManifest implements Comparable<StandardModelManifest> {

  // The beam size controls the depth of the beam search in OpenNLP's context evaluation.
  public static final int DEFAULT_BEAM_SIZE = 3;

  public static final String DEFAULT_SUBTYPE = "none";
  public static final String DICTIONARY_SUBTYPE = "dictionary";

  public static final String ENTITY = "entity";
  public static final String SENTENCE = "sentence";
  public static final String TOKEN = "token";
  public static final String POS = "pos";
  public static final String LEMMA = "lemma";

  protected String encryptionKey = StringUtils.EMPTY;
  protected String subtype = DEFAULT_SUBTYPE;
  protected int beamSize = DEFAULT_BEAM_SIZE;

  public static class ModelManifestBuilder {

    private String modelId;
    private String name;
    private String modelFileName;
    private LanguageCode languageCode;
    private String encryptionKey;
    private String type;
    private String subtype;
    private String creatorVersion;
    private String source;
    private int beamSize = DEFAULT_BEAM_SIZE;
    private Properties properties;

    public ModelManifestBuilder() {

    }

    public ModelManifestBuilder(String modelId, String name, String modelFileName, LanguageCode languageCode,
        String encryptionKey, String type, String subtype, String creatorVersion, String source, int beamSize,
        Properties properties) {

      // TODO: Make sure none of these values are null.

      this.modelId = modelId;
      this.name = name;
      this.modelFileName = modelFileName;
      this.languageCode = languageCode;
      this.encryptionKey = encryptionKey;
      this.type = type;
      this.subtype = subtype;
      this.creatorVersion = creatorVersion;
      this.source = source;
      this.beamSize = beamSize;
      this.properties = properties;

    }

    public ModelManifestBuilder(String modelId, String name, String modelFileName, LanguageCode languageCode,
        String encryptionKey, String type, String subtype, String creatorVersion, String source,
        Properties properties) {

      // TODO: Make sure none of these values are null.

      this.modelId = modelId;
      this.name = name;
      this.modelFileName = modelFileName;
      this.languageCode = languageCode;
      this.encryptionKey = encryptionKey;
      this.type = type;
      this.subtype = subtype;
      this.creatorVersion = creatorVersion;
      this.source = source;
      this.properties = properties;

    }

    public ModelManifestBuilder(String modelId, String name, String modelFileName, LanguageCode languageCode,
        String encryptionKey, String type, String creatorVersion, String source,
        Properties properties) {

      // TODO: Make sure none of these values are null.

      this.modelId = modelId;
      this.name = name;
      this.modelFileName = modelFileName;
      this.languageCode = languageCode;
      this.encryptionKey = encryptionKey;
      this.type = type;
      this.subtype = DEFAULT_SUBTYPE;
      this.creatorVersion = creatorVersion;
      this.source = source;
      this.properties = properties;

    }

    public void setModelId(String modelId) {
      this.modelId = modelId;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setModelFileName(String modelFileName) {
      this.modelFileName = modelFileName;
    }

    public void setLanguageCode(LanguageCode languageCode) {
      this.languageCode = languageCode;
    }

    public void setEncryptionKey(String encryptionKey) {
      this.encryptionKey = encryptionKey;
    }

    public void setType(String type) {
      this.type = type;
    }

    public void setSubtype(String subtype) {
      this.subtype = subtype;
    }

    public void setCreatorVersion(String creatorVersion) {
      this.creatorVersion = creatorVersion;
    }

    public void setSource(String source) {
      this.source = source;
    }

    public void setBeamSize(int beamSize) {
      this.beamSize = beamSize;
    }

    public Properties getProperties() {
      return properties;
    }

    public void setProperties(Properties properties) {
      this.properties = properties;
    }    

    public StandardModelManifest build() {

      return new StandardModelManifest(modelId, modelFileName, languageCode, encryptionKey, type, subtype, name, creatorVersion, source, beamSize, properties);

    }

  }

  private StandardModelManifest(String modelId, String modelFileName, LanguageCode languageCode,
      String encryptionKey, String type, String subtype, String name, String creatorVersion, String source, int beamSize,
      Properties properties) {

    super(modelId, modelFileName, languageCode, type, name, creatorVersion, source, ModelManifest.FIRST_GENERATION, properties);

    this.encryptionKey = encryptionKey;
    this.subtype = subtype;
    this.beamSize = beamSize;
    this.properties = properties;

  }

  @Override
  public final int hashCode() {

    return new HashCodeBuilder(17, 31)
      .append(modelId)
      .append(modelFileName)
      .append(name)
      .append(encryptionKey)
      .append(languageCode)
      .append(type)
      .append(subtype)
      .append(creatorVersion)
      .append(source)
      .append(beamSize)
      .append(generation)
      .toHashCode();

  }

  @Override
  public final boolean equals(Object obj) {

    if(obj instanceof StandardModelManifest){

          final StandardModelManifest other = (StandardModelManifest) obj;

          return new EqualsBuilder()
            .append(modelId, other.getModelId())
            .append(modelFileName, other.getModelFileName())
            .append(name, other.getName())
            .append(encryptionKey, other.getEncryptionKey())
            .append(languageCode, other.getLanguageCode())
            .append(type, other.getType())
            .append(subtype, other.getSubtype())
            .append(creatorVersion, other.getCreatorVersion())
            .append(source,  other.getSource())
            .append(beamSize, other.getBeamSize())
            .append(generation, other.getGeneration())
            .isEquals();

      } else {
          return false;
      }

  }

  @Override
  public int compareTo(StandardModelManifest modelManifest) {
    return modelManifest.getType().compareTo(type);
  }

  public String getModelId() {
    return modelId;
  }

  public String getName() {
    return name;
  }

  public String getModelFileName() {
    return modelFileName;
  }

  public LanguageCode getLanguageCode() {
    return languageCode;
  }

  public String getEncryptionKey() {
    return encryptionKey;
  }

  public String getType() {
    return type;
  }

  public String getSubtype() {
    return subtype;
  }

  public String getCreatorVersion() {
    return creatorVersion;
  }

  public int getBeamSize() {
    return beamSize;
  }

}
