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
package ai.idylnlp.test.nlp.sentence;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import com.neovisionaries.i18n.LanguageCode;

import ai.idylnlp.nlp.sentence.SegmentedSentenceDetector;

public class SegmentedSentenceDetectorTest {

  private static final String MODEL_PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String SRX_FILE = MODEL_PATH + File.separator + "example.srx";

  @Test
  public void segment1() throws IOException {

    final String srx = FileUtils.readFileToString(new File(SRX_FILE));

    SegmentedSentenceDetector detector = new SegmentedSentenceDetector(srx, LanguageCode.en);
    String[] sentences = detector.sentDetect("This is the first sentence. This is the second sentence.");

    assertTrue(Arrays.asList(sentences).contains("This is the first sentence."));
    assertTrue(Arrays.asList(sentences).contains("This is the second sentence."));

  }

}
