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
package ai.idylnlp.training.definition;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.training.definition.model.TrainingDefinitionException;
import ai.idylnlp.training.definition.model.TrainingDefinitionReader;
import ai.idylnlp.training.definition.model.TrainingDefinitionValidationResult;
import ai.idylnlp.training.definition.xml.Trainingdefinition;

/**
 * Implementation of {@link TrainingDefinitionReader} that reads a training definition
 * XML file and exposes the deserialized {@link Trainingdefinition} object.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class TrainingDefinitionFileReader implements TrainingDefinitionReader {

  private static final Logger LOGGER = LogManager.getLogger(TrainingDefinitionFileReader.class);

  private Trainingdefinition trainingDefinition;
  private File file;

  /**
   * Creates a new training definition file reader.
   * @param file The {@link File file} containing the training definition.
   * @throws TrainingDefinitionException Thrown if the training definition file cannot be parsed.
   */
  public TrainingDefinitionFileReader(File file) throws TrainingDefinitionException {

    this.file = file;

    try {

      JAXBContext jaxbContext = JAXBContext.newInstance(Trainingdefinition.class);

      Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
      trainingDefinition = (Trainingdefinition) jaxbUnmarshaller.unmarshal(file);

    } catch (Exception ex) {

      throw new TrainingDefinitionException("Invalid training definition file.", ex);

    }

  }

  @Override
  public TrainingDefinitionValidationResult validate() {

    List<String> messages = new LinkedList<String>();
    boolean valid = true;

    if(StringUtils.isEmpty(trainingDefinition.getModel().getFile())) {
      valid = false;
      messages.add("The training definition is missing the output model's file name.");
    }

    if(StringUtils.isEmpty(trainingDefinition.getModel().getLanguage())) {
      valid = false;
      messages.add("The training definition is missing the model's language.");
    }

    if(StringUtils.isEmpty(trainingDefinition.getModel().getType())) {
      valid = false;
      messages.add("The training definition is missing the model's entity type.");
    }

    if(trainingDefinition.getTrainingdata().getFormat().equalsIgnoreCase("idyl")) {

      if(StringUtils.isEmpty(trainingDefinition.getTrainingdata().getAnnotations())) {

        valid = false;
        messages.add("The training definition is missing an annotations file name.");

      } else {

        // TODO: Verify that the file exists.
        File file = new File(trainingDefinition.getTrainingdata().getAnnotations());

        if(!file.exists()) {

          valid = false;
          messages.add("The training definition file does not exist.");

        }

      }

    } else {

      // Any value other than "idyl" will default to "opennlp".

    }

    return new TrainingDefinitionValidationResult(valid, messages);

  }

  @Override
  public String getFeatures() {

    String features = null;

    try {

      final String xml = FileUtils.readFileToString(file);

      if(xml.contains("<features>")) {

        int start = xml.indexOf("<features>") + 10;
        int end = xml.indexOf("</features>");

        return xml.substring(start, end);

      }

    } catch (Exception ex) {

      LOGGER.error("Unable to extract feature generators from training definition file. This will cause a default set of feature generators to be used which may not be ideal.", ex);

    }

    return features;

  }

  @Override
  public Trainingdefinition getTrainingDefinition() {
    return trainingDefinition;
  }

}
