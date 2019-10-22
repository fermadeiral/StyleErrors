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
package ai.idylnlp.nlp.filters.confidence.serializers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

import org.apache.commons.math3.stat.descriptive.SynchronizedSummaryStatistics;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ai.idylnlp.model.nlp.ConfidenceFilterSerializer;

/**
 * An implementation of {@link ConfidenceFilterSerializer} that serializes
 * confidence values to the local disk.
 *
 * @author Mountain Fog, Inc.
 *
 */
public class LocalConfidenceFilterSerializer implements ConfidenceFilterSerializer {

  private static final Logger LOGGER = LogManager.getLogger(LocalConfidenceFilterSerializer.class);

  private File serializedFile;

  /**
   * Creates a new {@link LocalConfidenceFilterSerializer} and sets
   * the serialized filename to <code>confidences.dat</code>.
   */
  public LocalConfidenceFilterSerializer() {

    this.serializedFile = new File("confidences.dat");

  }

  /**
   * Creates a new {@link LocalConfidenceFilterSerializer}.
   * @param serializedFile The {@link File} to hold the serialized
   * confidence values.
   */
  public LocalConfidenceFilterSerializer(File serializedFile) {

    this.serializedFile = serializedFile;

  }

  @Override
  public int serialize(Map<String, SynchronizedSummaryStatistics> statistics) throws Exception {

    serializedFile.createNewFile();

    FileOutputStream fos = new FileOutputStream(serializedFile.getAbsolutePath());
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(statistics);
        oos.close();
        fos.close();

        LOGGER.info("Serialized confidence values for {} entity models to {}.", statistics.size(),
            serializedFile.getAbsolutePath());

        return statistics.size();

  }

  @SuppressWarnings("unchecked")
  @Override
  public int deserialize(Map<String, SynchronizedSummaryStatistics> statistics) throws Exception {

    if(serializedFile.exists()) {

      FileInputStream fis = new FileInputStream(serializedFile.getAbsolutePath());
          ObjectInputStream ois = new ObjectInputStream(fis);
          statistics = (Map<String, SynchronizedSummaryStatistics>) ois.readObject();
          ois.close();
          fis.close();

          LOGGER.info("Deserialized confidence values for {} entity models from {}.", statistics.size(),
              serializedFile.getAbsolutePath());

          return statistics.size();

    } else {

      return 0;

    }

  }

}
