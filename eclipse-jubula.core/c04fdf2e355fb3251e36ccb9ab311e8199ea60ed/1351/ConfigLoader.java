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
package org.eclipse.jubula.toolkit.api.gen.internal.utils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.utils.generator.ToolkitConfig;


/**
 * Loads the configuration for the API generation from the properties file. It
 * is expected that the properties reside in
 * <code>resources/apigen.properties</code>.
 * 
 * @author BREDEX GmbH
 * @created 09.09.2014
 */
public class ConfigLoader {
    /**
     * <code>TOOLKIT_LOCATIONS</code>
     */
    private static final String TOOLKIT_NAMES = 
        "api.gen.toolkit.names"; //$NON-NLS-1$
    /**
     * <code>RESOURCES_APIGEN_PROPERTIES</code>
     */
    private static final String RESOURCES_APIGEN_PROPERTIES =
        "resources/apigen.properties"; //$NON-NLS-1$
    /**
     * <code>XML_FILENAME</code>
     */
    private static final String XML_PATH = 
        "api.gen.toolkit.xml.conf"; //$NON-NLS-1$
    /**
     * <code>RESOURCE_BUNDLE_FILENAME</code>
     */
    private static final String RESOURCE_BUNDLE_PATH = 
        "api.gen.toolkit.resourcebundle.path"; //$NON-NLS-1$
    /**
     * <code>RESOURCE_BUNDLE_FQN</code>
     */
    private static final String RESOURCE_BUNDLE_FQN = 
        "api.gen.toolkit.resourcebundle.fqn"; //$NON-NLS-1$
    /**
     * <code>BASEDIR</code>
     */
    private static final String BASEDIR = "api.gen.toolkit.name.scheme"; //$NON-NLS-1$
    /**
     * <code>OUTPUTDIR</code>
     */
    private static final String OUTPUTDIR = "api.gen.toolkit.output"; //$NON-NLS-1$
    /**
     * <code>OUTPUTDIR</code>
     */
    private static final String GENERATIONDIR = "api.gen.toolkit.generation.dir"; //$NON-NLS-1$
    /**
     * <code>OUTPUTDIR</code>
     */
    private static final String CONVERTER_INFO_DIR = "api.gen.converter.info.dir"; //$NON-NLS-1$
    /**
     * <code>instance</code> the singleton instance
     */
    private static ConfigLoader instance = null;
    /**
     * toolkit filename and path information
     */
    private ToolkitConfig m_toolkitConfig;
    /**
     * directory for the generation
     */
    private String m_generationDir;
    /**
     * directory for information for converter
     */
    private String m_converterInfoDir;
    /**
     * The constructor.
     */
    private ConfigLoader() {
        try {
            URL resourceURL = ConfigLoader.class.getClassLoader()
                .getResource(RESOURCES_APIGEN_PROPERTIES);
            
            Properties p = new Properties();
            p.load(resourceURL.openStream());
            
            String toolkitIDs = p.getProperty(TOOLKIT_NAMES);
            
            List<String> toolkitIDList = new ArrayList<String>(
                    Arrays.asList(StringUtils.stripAll(
                            StringUtils.split(toolkitIDs, ','))));
            
            m_toolkitConfig = new ToolkitConfig(
                    p.getProperty(BASEDIR),
                    p.getProperty(XML_PATH),
                    p.getProperty(RESOURCE_BUNDLE_PATH),
                    p.getProperty(RESOURCE_BUNDLE_FQN),
                    p.getProperty(OUTPUTDIR),
                    toolkitIDList);
            
            m_generationDir = p.getProperty(GENERATIONDIR);
            
            m_converterInfoDir = p.getProperty(CONVERTER_INFO_DIR);
            
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * @return the singleton instance
     */
    public static ConfigLoader getInstance() {
        if (instance == null) {
            instance = new ConfigLoader();
        }
        return instance;
    }
    
    /**
     * 
     * @return a list of toolkit plugin names
     */
    public ToolkitConfig getToolkitConfig() {
        return m_toolkitConfig;
    }

    /**
     * @return the generationDir
     */
    public String getGenerationDir() {
        return m_generationDir;
    }

    /**
     * @return the generationDir
     */
    public String getConverterInfoDir() {
        return m_converterInfoDir;
    }
}
