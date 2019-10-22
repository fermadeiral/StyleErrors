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
package ai.idylnlp.test.training.definition.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;

import javax.xml.bind.JAXBException;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;
import org.xmlunit.builder.Input;
import org.xmlunit.validation.Languages;
import org.xmlunit.validation.ValidationResult;
import org.xmlunit.validation.Validator;

public class XmlSchemaTest {

  private static final Logger LOGGER = LogManager.getLogger(XmlSchemaTest.class);

  private static final String PATH = new File("src/test/resources/").getAbsolutePath();
  private static final String XSD = new File("src/main/xsd/definition.xsd").getAbsolutePath();

  @Test
  public void test1() throws JAXBException {

    final String DEFINITION_FILE = PATH + File.separator + "valid-definition-1.xml";

    assertTrue(validate(DEFINITION_FILE));

  }

  @Test
  public void test2() throws JAXBException {

    final String DEFINITION_FILE = PATH + File.separator + "invalid-definition-1.xml";

    assertFalse(validate(DEFINITION_FILE));

  }

  private boolean validate(final String xmlFile) {

    Validator v = Validator.forLanguage(Languages.W3C_XML_SCHEMA_NS_URI);
    v.setSchemaSources(Input.fromFile(XSD).build());
    ValidationResult result = v.validateInstance(new StreamSource(new File(xmlFile)));

    result.getProblems().forEach(problem -> LOGGER.error(problem.getMessage()));

    return result.isValid();

  }

}
