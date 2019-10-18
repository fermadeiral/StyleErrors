/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.tools.internal.utils.generator;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;
import org.eclipse.jubula.tools.internal.xml.businessprocess.ConfigVersion;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * @author BREDEX GmbH
 * @created Jul 5, 2007
 */
public class ToolkitPluginParser {
    /**
     * <code>TOOLKIT_ELEMENT</code>
     */
    public static final String TOOLKIT_ELEMENT = "toolkit"; //$NON-NLS-1$

    /**
     * <code>m_xmlFile</code>
     */
    private String m_xmlFile;

    /**
     * <code>m_compSystem</code>
     */
    private CompSystem m_compSystem;

    /**
     * @param filename
     *     the path to plugin.xml
     * @param compSystem
     *     the CompSystem instance, to get major and minor version information
     */
    public ToolkitPluginParser(String filename, CompSystem compSystem) {
        m_xmlFile = filename;
        m_compSystem = compSystem;
    }

    /**
     * @return the toolkit descriptor
     */
    public ToolkitDescriptor getToolkitDescriptor() {
        DocumentBuilder db = null;
        Document xmlDom = null;
        try {
            db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FactoryConfigurationError e) {
            e.printStackTrace();
        }
        try {
            xmlDom = db.parse(m_xmlFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        NodeList toolkitElements = xmlDom.getElementsByTagName(TOOLKIT_ELEMENT);
        // there should only be one, so we'll take the first one.
        Element toolkitElement = (Element)toolkitElements.item(0);
        final String toolkitName = toolkitElement.getAttribute(
                ToolkitConstants.ATTR_NAME);
        final String toolkitID = toolkitElement
                .getAttribute(ToolkitConstants.ATTR_TOOLKITID);
        final String level = toolkitElement
                .getAttribute(ToolkitConstants.ATTR_LEVEL);
        final boolean isUserToolkit = Boolean.valueOf(toolkitElement
                .getAttribute(ToolkitConstants.ATTR_ISUSERTOOLKIT))
                .booleanValue();
        final String includes = toolkitElement
                .getAttribute(ToolkitConstants.ATTR_INCLUDES);
        final String depends = toolkitElement
            .getAttribute(ToolkitConstants.ATTR_DEPENDS);
        final int order = Integer.parseInt(toolkitElement
                .getAttribute(ToolkitConstants.ATTR_ORDER));
        final ConfigVersion configVersion = m_compSystem.getConfigVersion();
        final int majorVersion = configVersion.getMajorVersion().intValue();
        final int minorVersion = configVersion.getMinorVersion().intValue();
        final ToolkitDescriptor descr = new ToolkitDescriptor(
                toolkitID, toolkitName, includes, depends, level, order, 
                isUserToolkit, majorVersion, minorVersion);

        return descr;
    }

}
