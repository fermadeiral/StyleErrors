/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.api.converter.exceptions.InvalidNodeNameException;
import org.eclipse.jubula.client.api.converter.utils.ProjectCache;
import org.eclipse.jubula.client.api.converter.utils.Utils;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *  Information for Creating a Java Class corresponding to a Node
 *  @created 28.10.2014
 */
public class NodeInfo {
    
    /** maps a UUID from a test case/suite/job to the name of its
      * corresponding node info for generation */
    private static Map<String, NodeInfo> uuidToClassNameMap;
    
    /** The class name of the test case */
    private String m_className;

    /** The base path of the package */
    private String m_packageBasePath;
    
    /** The node */
    private INodePO m_node;
    
    /** the default toolkit */
    private String m_defaultToolkit;
    
    /** the fully qualified name */
    private String m_fqName;

    /** the package name */
    private String m_packageName;

    /** the project name */
    private String m_projectName;

    /** the fileName */
    private String m_fileName;

    /** the fully qualified file name */
    private String m_fqFileName;
    
    /**
     * @param fqFileName the fully qualified file name
     * @param node the node
     * @param packageBasePath the base path of the package
     * @param defaultToolkit the default toolkit
     */
    public NodeInfo (String fqFileName, INodePO node,
            String packageBasePath, String defaultToolkit) {
        m_fqFileName = fqFileName;
        m_fileName = StringUtils.substringBeforeLast(m_fqFileName, ".java"); //$NON-NLS-1$
        m_className = StringUtils.substringAfterLast(m_fileName,
                StringConstants.SLASH);
        m_node = node;
        m_packageBasePath = packageBasePath;
        m_defaultToolkit = defaultToolkit;
        
        Logger log = LoggerFactory.getLogger(NodeInfo.class);
        
        IProjectPO project = null;
        try {
            project = ProjectCache.get(node.getParentProjectId());
        } catch (JBException e) {
            Plugin.getDefault().writeErrorLineToConsole(
                                "Error while loading project.", true); //$NON-NLS-1$
        }
        
        try {
            m_projectName = Utils.translateToPackageName(project);
        } catch (InvalidNodeNameException e) {
            log.error(e.getLocalizedMessage());
        }
        m_fqName = Utils.getFullyQualifiedTranslatedName(node,
                m_packageBasePath, m_projectName);
        m_packageName = StringUtils.substringBeforeLast(m_fqName,
                StringConstants.DOT);
        
    }
    
    /**
     * @return The class name of the test case
     */
    public String getClassName() {
        return m_className;
    }
    
    /**
     * @return The base path of the package
     */
    public String getPackageBasePath() {
        return m_packageBasePath;
    }
    
    /**
     * @return The node
     */
    public INodePO getNode() {
        return m_node;
    }
    
    /**
     * @return The default toolkit
     */
    public String getDefaultToolkit() {
        return m_defaultToolkit;
    }
    
    /**
     * @return The fully qualified name
     */
    public String getFqName() {
        return m_fqName;
    }
    
    /**
     * @return The package name
     */
    public String getPackageName() {
        return m_packageName;
    }
    
    /**
     * @return The project name
     */
    public String getProjectName() {
        return m_projectName;
    }

    /**
     * @return the fully qualified file name
     */
    public String getFqFileName() {
        return m_fqFileName;
    }

    /**
     * @return the uuidToClassNameMap
     */
    public static Map<String, NodeInfo> getUuidToNodeInfoMap() {
        return uuidToClassNameMap;
    }

    /**
     * @param map the uuidToClassNameMap to set
     */
    public static void setUuidToNodeInfoMap(
            Map<String, NodeInfo> map) {
        NodeInfo.uuidToClassNameMap = map;
    }
}
