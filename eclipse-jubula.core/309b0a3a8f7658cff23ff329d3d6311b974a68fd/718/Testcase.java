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
 * generated
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "m_skippedOrErrorOrFailure"
     })
@XmlRootElement(name = "testcase")
public class Testcase {

    /**
     * 
     */
    @XmlElementRefs({
        @XmlElementRef(name = "failure",
                type = Failure.class, required = false),
        @XmlElementRef(name = "system-out",
        type = JAXBElement.class, required = false),
        @XmlElementRef(name = "error", type = Error.class, required = false),
        @XmlElementRef(name = "system-err",
        type = JAXBElement.class, required = false),
        @XmlElementRef(name = "skipped", type = Skipped.class, required = false)
        })
    private List<Object> m_skippedOrErrorOrFailure;
    /**
     * 
     */
    @XmlAttribute(name = "name", required = true)
    private String m_name;
    /**
     * 
     */
    @XmlAttribute(name = "assertions")
    private String m_assertions;
    /**
     * 
     */
    @XmlAttribute(name = "time")
    private String m_time;
    /**
     * 
     */
    @XmlAttribute(name = "classname")
    private String m_classname;
    /**
     * 
     */
    @XmlAttribute(name = "status")
    private String m_status;
    /**
     * 
     */
    @XmlAttribute(name = "class")
    private String m_clazz;
    /**
     * 
     */
    @XmlAttribute(name = "file")
    private String m_file;
    /**
     * 
     */
    @XmlAttribute(name = "line")
    private String m_line;

    /**
     * Gets the value of the skippedOrErrorOrFailure property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the skippedOrErrorOrFailure property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSkippedOrErrorOrFailure().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Error }
     * {@link Failure }
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link JAXBElement }{@code <}{@link String }{@code >}
     * {@link Skipped }
     * @return List of skipped,error and failure objects
     * 
     */
    public List<Object> getSkippedOrErrorOrFailure() {
        if (m_skippedOrErrorOrFailure == null) {
            m_skippedOrErrorOrFailure = new ArrayList<Object>();
        }
        return this.m_skippedOrErrorOrFailure;
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
     * Gets the value of the classname property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClassname() {
        return m_classname;
    }

    /**
     * Sets the value of the classname property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClassname(String value) {
        this.m_classname = value;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStatus() {
        return m_status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStatus(String value) {
        this.m_status = value;
    }

    /**
     * Gets the value of the clazz property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getClazz() {
        return m_clazz;
    }

    /**
     * Sets the value of the clazz property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setClazz(String value) {
        this.m_clazz = value;
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

    /**
     * Gets the value of the line property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLine() {
        return m_line;
    }

    /**
     * Sets the value of the line property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLine(String value) {
        this.m_line = value;
    }

}
