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
package ai.idylnlp.model.manifest;

import java.util.Properties;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import com.neovisionaries.i18n.LanguageCode;

/**
 * A model manifest for a second generation (deep learning) model.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class SecondGenModelManifest extends ModelManifest implements Comparable<SecondGenModelManifest> {

  public static final String ENTITY = "entity";

  private String vectorsFileName;
  private int windowSize;
  private String vectorsSource;

  public SecondGenModelManifest(String modelId, String modelFileName,
      LanguageCode languageCode, String type, String name,
      String creatorVersion, String vectorsFileName, int windowSize, String source,
      String vectorsSource, Properties properties) {

    super(modelId, modelFileName, languageCode, type, name, creatorVersion, source,
        ModelManifest.SECOND_GENERATION, properties);

    this.vectorsFileName = vectorsFileName;
    this.windowSize = windowSize;
    this.vectorsSource = vectorsSource;

  }

  @Override
  public final int hashCode() {

    return new HashCodeBuilder(17, 31)
      .append(modelId)
      .append(modelFileName)
      .append(vectorsFileName)
      .append(windowSize)
      .append(name)
      .append(languageCode)
      .append(type)
      .append(creatorVersion)
      .append(source)
      .append(vectorsSource)
      .append(generation)
      .toHashCode();

  }

  @Override
  public final boolean equals(Object obj) {

    if(obj instanceof SecondGenModelManifest){

          final SecondGenModelManifest other = (SecondGenModelManifest) obj;

          return new EqualsBuilder()
            .append(modelId, other.getModelId())
            .append(modelFileName, other.getModelFileName())
            .append(name, other.getName())
            .append(vectorsFileName, other.getVectorsFileName())
            .append(windowSize, windowSize)
            .append(languageCode, other.getLanguageCode())
            .append(type, other.getType())
            .append(creatorVersion, other.getCreatorVersion())
            .append(source, other.getSource())
            .append(vectorsSource, other.getVectorsSource())
            .append(generation, other.getGeneration())
            .isEquals();

      } else {
          return false;
      }

  }

  @Override
  public int compareTo(SecondGenModelManifest modelManifest) {
    return modelManifest.getType().compareTo(type);
  }

  public String getVectorsFileName() {
    return vectorsFileName;
  }

  public int getWindowSize() {
    return windowSize;
  }

  public String getVectorsSource() {
    return vectorsSource;
  }

}
