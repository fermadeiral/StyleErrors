/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.model;

import java.io.InputStream;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.eclipse.jubula.extensions.wizard.i18n.Messages;

/**
 * Reads and provides the toolkits from the <code>toolkits.xml</code> file.
 * 
 * @author BREDEX GmbH
 */
public enum ToolkitProvider {

    /** The singleton's instance */
    INSTANCE;

    /** The list of toolkits */
    private List<Toolkit> m_toolkits;
    
    /** 
     * Constructor that reads the xml file and puts the toolkit objects 
     * into the list 
     */
    private ToolkitProvider() {
        InputStream is = this.getClass()
                .getResourceAsStream(Messages.ToolkitsXmlPath);
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(Toolkits.class);
            Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();

            Toolkits toolkits = (Toolkits) jaxbMarshaller.unmarshal(is);
            m_toolkits = toolkits.getToolkits();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * @return the toolkit list
     */
    public List<Toolkit> getToolkits() {
        return m_toolkits;
    }
    
    /**
     * @return the singleton's instance
     */
    public static ToolkitProvider getInstance() {
        return INSTANCE;
    }
}
