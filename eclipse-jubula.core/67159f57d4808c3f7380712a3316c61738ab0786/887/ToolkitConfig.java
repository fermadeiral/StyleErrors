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

import java.text.MessageFormat;
import java.util.List;

/**
 * @author BREDEX GmbH
 * @created Jul 11, 2007
 */
public class ToolkitConfig {
    /**
     * 
     */
    private String m_basedir;
    /**
     * 
     */
    private String m_xmlPath;
    /**
     * 
     */
    private String m_resourceBundlePath;
    
    /**
     * The fully-qualified name of the resource bundle (template)
     */
    private String m_resourceBundleFQN;
    /**
     * 
     */
    private List<String> m_toolkitNames;
    /**
     * <code>m_outputdir</code>
     */
    private String m_outputdir;
    
    /**
     * @param basedir the base directory for the toolkit plugin
     * @param xmlPath the filename used for XML toolkit files
     * @param resourceBundlePath the filename used for resource bundles in toolkits
     * @param resourceBundlefqn the FQN of the resource bundle (template)
     * @param outputDir the output directory
     * @param toolkitNames the names (dirs) of all toolkits to be generated
     */
    public ToolkitConfig(String basedir, String xmlPath, 
            String resourceBundlePath, String resourceBundlefqn, 
            String outputDir, List<String> toolkitNames) {
        super();
        m_basedir = basedir;
        m_xmlPath = xmlPath;
        m_resourceBundlePath = resourceBundlePath;
        m_resourceBundleFQN = resourceBundlefqn;
        m_outputdir = outputDir;
        m_toolkitNames = toolkitNames;
    }

    /**
     * @return a list of toolkit names
     */
    public List<String> getToolkitNames() {
        return m_toolkitNames;
    }

    /**
     * @param toolkitName the name of the toolkit
     * @return the path to the XML file
     */
    public String getXMLPath(String toolkitName) {
        StringBuffer path = new StringBuffer();
        path.append(MessageFormat.format(m_basedir, 
                new Object[] {toolkitName}));
        path.append('/').append(m_xmlPath);
        String result = path.toString();
        return result;
    }
    
    /**
     * @param toolkitName the name of the toolkit
     * @return the path to the plugin.xml file
     */
    public String getPluginXMLPath(String toolkitName) {
        // FIXME: path should be configurable --> texgen.properties
        StringBuffer path = new StringBuffer();
        path.append(MessageFormat.format(m_basedir, 
                new Object[] {toolkitName}));
        path.append("/plugin.xml"); //$NON-NLS-1$
        String filename = path.toString();
        return filename;
    }
    
    /**
     * 
     * @param toolkitName the name of the toolkit
     * @return the path to the resourcebundle
     */
    public String getResourceBundlePath(String toolkitName) {
        StringBuffer path = formatBasename(toolkitName);
        path.append('/');
        path.append(MessageFormat.format(m_resourceBundlePath, 
                new Object[] {toolkitName.toLowerCase()}));
        String filename = path.toString();
        return filename;
    }
    
    /**
     * @param toolkitName the name of the toolkit
     * @return the FQN of the resource bundle for the toolkit
     */
    public String getResourceBundleFQN(String toolkitName) {
        String fqn = MessageFormat.format(m_resourceBundleFQN,
                new Object[] {toolkitName.toLowerCase()});
        return fqn;
    }
    
    /**
     * @param toolkitName the name of the toolkit
     * @return the formatted basename
     */
    private StringBuffer formatBasename(String toolkitName) {
        StringBuffer name = new StringBuffer();
        name.append(MessageFormat.format(m_basedir, 
                new Object[] {toolkitName}));
        return name;
    }

    /**
     * @return the outputdir
     */
    public String getOutputdir() {
        return m_outputdir;
    }
}