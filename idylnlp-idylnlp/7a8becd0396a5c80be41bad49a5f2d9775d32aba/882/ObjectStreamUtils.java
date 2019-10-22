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
package ai.idylnlp.models;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.nlp.annotation.reader.IdylNLPFileAnnotationReader;
import ai.idylnlp.opennlp.custom.formats.IdylNLPNameSampleStream;
import ai.idylnlp.model.Constants;
import ai.idylnlp.model.nlp.annotation.AnnotationReader;
import ai.idylnlp.model.nlp.subjects.BratSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.CoNLL2003SubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.IdylNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.OpenNLPSubjectOfTrainingOrEvaluation;
import ai.idylnlp.model.nlp.subjects.SubjectOfTrainingOrEvaluation;
import opennlp.tools.formats.Conll02NameSampleStream;
import opennlp.tools.formats.Conll03NameSampleStream;
import opennlp.tools.formats.brat.AnnotationConfiguration;
import opennlp.tools.formats.brat.BratAnnotation;
import opennlp.tools.formats.brat.BratAnnotationStream;
import opennlp.tools.formats.brat.BratNameSampleStream;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;

public class ObjectStreamUtils {

  private static final Logger LOGGER = LogManager.getLogger(ObjectStreamUtils.class);

  private ObjectStreamUtils() {
    // This is a utility class.
  }

  /**
   * Gets the {@link ObjectStream} for training.
   * @param subjectOfTraining The {@link SubjectOfTrainingOrEvaluation}.
   * @return An {@link ObjectStream} derived from the given {@link SubjectOfTraining}.
   * @throws IOException Thrown if any of the input or annotation files cannot be read.
   */
  public static ObjectStream<NameSample> getObjectStream(SubjectOfTrainingOrEvaluation subjectOfTraining) throws IOException {

    ObjectStream<NameSample> sampleStream = null;

    if(subjectOfTraining instanceof IdylNLPSubjectOfTrainingOrEvaluation) {

      IdylNLPSubjectOfTrainingOrEvaluation nameFinderSubjectOfTraining = (IdylNLPSubjectOfTrainingOrEvaluation) subjectOfTraining;

      LOGGER.info("Using Idyl NLP formatted annotations.");

      final AnnotationReader annotationReader = new IdylNLPFileAnnotationReader(nameFinderSubjectOfTraining.getAnnotationsFile());
      final InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(subjectOfTraining.getInputFile()));
      sampleStream = new IdylNLPNameSampleStream(new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8), annotationReader);

    } else if(subjectOfTraining instanceof BratSubjectOfTrainingOrEvaluation) {

      BratSubjectOfTrainingOrEvaluation nameFinderSubjectOfTraining = (BratSubjectOfTrainingOrEvaluation) subjectOfTraining;

      LOGGER.info("Using Brat formatted annotations.");

      Map<String, String> typeToClassMap = new HashMap<>();
        typeToClassMap.put("Person", AnnotationConfiguration.ENTITY_TYPE);
        typeToClassMap.put("Location", AnnotationConfiguration.ENTITY_TYPE);
        typeToClassMap.put("Organization", AnnotationConfiguration.ENTITY_TYPE);
        typeToClassMap.put("Date", AnnotationConfiguration.ENTITY_TYPE);

      AnnotationConfiguration config = new AnnotationConfiguration(typeToClassMap);
        InputStream in = ObjectStreamUtils.class.getResourceAsStream(nameFinderSubjectOfTraining.getInputFile() + ".ann");

        // TODO: Return the brat annotations stream.
        // sampleStream = new BratAnnotationStream(config, "idylnlp", in);

    } else if(subjectOfTraining instanceof CoNLL2003SubjectOfTrainingOrEvaluation) {

      CoNLL2003SubjectOfTrainingOrEvaluation nameFinderSubjectOfTraining = (CoNLL2003SubjectOfTrainingOrEvaluation) subjectOfTraining;

      LOGGER.info("Using CoNLL-2003 formatted data.");

      InputStreamFactory in = new MarkableFileInputStreamFactory(new File(nameFinderSubjectOfTraining.getInputFile()));
      sampleStream = new Conll03NameSampleStream(Conll03NameSampleStream.LANGUAGE.EN, in, Conll02NameSampleStream.GENERATE_PERSON_ENTITIES);

    } else {

      LOGGER.info("Using OpenNLP formatted data.");

      final InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File(subjectOfTraining.getInputFile()));
      sampleStream = new NameSampleDataStream(new PlainTextByLineStream(inputStreamFactory, Constants.ENCODING_UTF8));

    }

    return sampleStream;

  }

}
