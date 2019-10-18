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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "m_testsuiteOrPropertiesOrTestcase"
    })
@XmlRootElement(name = "testsuite")
public class Testsuite {

    /**
     * 
     */
    @XmlElementRefs({
        @XmlElementRef(name = "testcase",
                type = Testcase.class, required = false),
        @XmlElementRef(name = "system-out",
        type = JAXBElement.class, required = false),
        @XmlElementRef(name = "properties",
        type = Properties.class, required = false),
        @XmlElementRef(name = "testsuite",
        type = Testsuite.class, required = false),
        @XmlElementRef(name = "system-err",
        type = JAXBElement.class, required = false)
        })
    
    private List<Object> m_testsuiteOrPropertiesOrTestcase;
    /**
     * 
     */
    @XmlAttribute(name = "name")
    private String m_name;
    /**
     * 
     */
    @XmlAttribute(name = "tests", required = true)
    private String m_tests;
    /**
     * 
     */
    @XmlAttribute(name = "failures")
    private String m_failures;
    /**
     * 
     */
    @XmlAttribute(name = "errors")
    private String m_errors;
    /**
     * 
     */
    @XmlAttribute(name = "time")
    private String m_time;
    /**
     * 
     */
    @XmlAttribute(name = "disabled")
    private String m_disabled;
    /**
     * 
     */
    @XmlAttribute(name = "skipped")
    private String m_skipped;
    /**
     * 
     */
    @XmlAttribute(name = "timestamp")
    private String m_timestamp;
    /**
     * 
     */
    @XmlAttribute(name = "hostname")
    private String m_hostname;
    /**
     * 
     */
    @XmlAttribute(name = "id")
    private String m_id;
    /**
     * 
     */
    @XmlAttribute(name = "package")
    private String m_package;
    /**
     * 
     */
    @XmlAttribute(name = "assertions")
    private String m_assertions;
    /**
     * 
     */
    @XmlAttribute(name = "file")
    private String m_file;

    /**
     * Gets the value of the testsuiteOrPropertiesOrTestcase property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the testsuiteOrPropertiesOrTestcase property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTestsuiteOrPropertiesOrTestcase().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Properties }
     * {@link Testsuite }
     * {@link Testcase }
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * @return list of testsuites, properties or testcases
     * 
     */
    public List<Object> getTestsuiteOrPropertiesOrTestcase() {
        if (m_testsuiteOrPropertiesOrTestcase == null) {
            m_testsuiteOrPropertiesOrTestcase = new ArrayList<Object>();
        }
        return this.m_testsuiteOrPropertiesOrTestcase;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return m_name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.m_name = value;
    }

    /**
     * Gets the value of the tests property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTests() {
        return m_tests;
    }

    /**
     * Sets the value of the tests property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTests(String value) {
        this.m_tests = value;
    }

    /**
     * Gets the value of the failures property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFailures() {
        return m_failures;
    }

    /**
     * Sets the value of the failures property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFailures(String value) {
        this.m_failures = value;
    }

    /**
     * Gets the value of the errors property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrors() {
        return m_errors;
    }

    /**
     * Sets the value of the errors property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrors(String value) {
        this.m_errors = value;
    }

    /**
     * Gets the value of the time property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTime() {
        return m_time;
    }

    /**
     * Sets the value of the time property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTime(String value) {
        this.m_time = value;
    }

    /**
     * Gets the value of the disabled property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDisabled() {
        return m_disabled;
    }

    /**
     * Sets the value of the disabled property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDisabled(String value) {
        this.m_disabled = value;
    }

    /**
     * Gets the value of the skipped property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSkipped() {
        return m_skipped;
    }

    /**
     * Sets the value of the skipped property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSkipped(String value) {
        this.m_skipped = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTimestamp() {
        return m_timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTimestamp(String value) {
        this.m_timestamp = value;
    }

    /**
     * Gets the value of the hostname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getHostname() {
        return m_hostname;
    }

    /**
     * Sets the value of the hostname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setHostname(String value) {
        this.m_hostname = value;
    }

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return m_id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.m_id = value;
    }

    /**
     * Gets the value of the package property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPackage() {
        return m_package;
    }

    /**
     * Sets the value of the package property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPackage(String value) {
        this.m_package = value;
    }

    /**
     * Gets the value of the assertions property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAssertions() {
        return m_assertions;
    }

    /**
     * Sets the value of the assertions property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAssertions(String value) {
        this.m_assertions = value;
    }

    /**
     * Gets the value of the file property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFile() {
        return m_file;
    }

    /**
     * Sets the value of the file property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFile(String value) {
        this.m_file = value;
    }

}
