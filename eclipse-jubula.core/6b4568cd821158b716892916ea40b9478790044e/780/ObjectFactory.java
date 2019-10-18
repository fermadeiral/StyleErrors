/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.exporter.junitmodel;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * generated
 */
@XmlRegistry
public class ObjectFactory {

    /**
     * 
     */
    @SuppressWarnings("nls")
    private static final QName SYSTEMERR_QNAME = new QName("", "system-err");
    /**
     * 
     */
    @SuppressWarnings("nls")
    private static final QName SYSTEMOUT_QNAME = new QName("", "system-out");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.eclipse.jubula.client.core.junitxmlreportgen
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Testsuite }
     * @return testsuite
     */
    public Testsuite createTestsuite() {
        return new Testsuite();
    }

    /**
     * Create an instance of {@link Properties }
     * @return porperties
     */
    public Properties createProperties() {
        return new Properties();
    }

    /**
     * Create an instance of {@link Property }
     * @return property
     */
    public Property createProperty() {
        return new Property();
    }

    /**
     * Create an instance of {@link Testcase }
     * @return property
     */
    public Testcase createTestcase() {
        return new Testcase();
    }

    /**
     * Create an instance of {@link Skipped }
     * @return skipped
     */
    public Skipped createSkipped() {
        return new Skipped();
    }

    /**
     * Create an instance of {@link Error }
     * @return error
     */
    public Error createError() {
        return new Error();
    }

    /**
     * Create an instance of {@link Failure }
     * @return failure
     */
    public Failure createFailure() {
        return new Failure();
    }

    /**
     * Create an instance of {@link Testsuites }
     * @return testsuites
     */
    public Testsuites createTestsuites() {
        return new Testsuites();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * @param value accepts String
     * @return JAXB object
     */
    @XmlElementDecl(namespace = "", name = "system-err")
    public JAXBElement<String> createSystemErr(String value) {
        return new JAXBElement<String>(SYSTEMERR_QNAME,
                String.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link String }{@code >}}
     * @param value accepts String
     * @return JAXB object
     */
    @XmlElementDecl(namespace = "", name = "system-out")
    public JAXBElement<String> createSystemOut(String value) {
        return new JAXBElement<String>(SYSTEMOUT_QNAME,
                String.class, null, value);
    }

}
