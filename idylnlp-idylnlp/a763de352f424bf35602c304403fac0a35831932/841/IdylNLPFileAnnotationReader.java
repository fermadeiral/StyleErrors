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
package ai.idylnlp.nlp.annotation.reader;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.nlp.annotation.AnnotationReader;
import ai.idylnlp.model.nlp.annotation.IdylNLPAnnotation;

/**
 * Implementation of {@link AnnotationReader} that reads IdylNLP annotations
 * from a file with the format: lineNumber start end type
 *
 * @author Mountain Fog, Inc.
 *
 */
public class IdylNLPFileAnnotationReader implements AnnotationReader {

  private static final Logger LOGGER = LogManager.getLogger(IdylNLPFileAnnotationReader.class);

  private Map<Integer, Collection<IdylNLPAnnotation>> annotations;

  public IdylNLPFileAnnotationReader(String fileName) throws IOException {

    annotations = new HashMap<Integer, Collection<IdylNLPAnnotation>>();

    File file = new File(fileName);

    List<String> lines = FileUtils.readLines(file);

    for(String line : lines) {

      if(!line.startsWith("#") && !StringUtils.isEmpty(line)) {

        String[] annotation = line.split(" ");

        int lineNumber = Integer.parseInt(annotation[0]);

        IdylNLPAnnotation a = new IdylNLPAnnotation();
        a.setLineNumber(lineNumber);
        a.setTokenStart(Integer.parseInt(annotation[1]));
        a.setTokenEnd(Integer.parseInt(annotation[2]));
        a.setType(annotation[3]);

        Collection<IdylNLPAnnotation> m = annotations.get(lineNumber);

        if(m == null) {
          m = new LinkedList<IdylNLPAnnotation>();
          annotations.put(lineNumber, m);
        }

        m.add(a);

      }

    }

    for(Integer i : annotations.keySet()) {

      for(IdylNLPAnnotation annotation : annotations.get(i)) {

        LOGGER.debug("{}\t{}", i, annotation.toString());

      }

    }

  }

  @Override
  public Collection<IdylNLPAnnotation> getAnnotations(int lineNumber) {

    Collection<IdylNLPAnnotation> a = annotations.get(lineNumber);

    if(a != null) {
      return a;
    } else {
      return Collections.emptyList();
    }

  }

}
