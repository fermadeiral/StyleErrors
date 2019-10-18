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

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * A class that contains a list of all toolkits. This class is only needed
 * for parsing the toolkits.xml file to java objects. It should not be used
 * otherwise!
 * 
 * @author BREDEX GmbH
 * @noinstantiate
 */
@XmlRootElement(name = "toolkits")
@XmlAccessorType(XmlAccessType.FIELD)
final class Toolkits {
    
    /** The list of toolkits */
    @XmlElement(name = "toolkit")
    private List<Toolkit> m_toolkits = null;
    
    /**
     * @return the list of toolkits
     */
    public List<Toolkit> getToolkits() {
        return m_toolkits;
    }
    
    /**
     * @param toolkits the list of toolkits
     */
    public void setToolkits(List<Toolkit> toolkits) {
        this.m_toolkits = toolkits;
    }
}
