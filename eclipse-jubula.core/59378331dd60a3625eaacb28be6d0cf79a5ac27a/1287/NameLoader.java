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
package org.eclipse.jubula.toolkit.api.gen.internal.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ToolkitDescriptor;


/**
 * Loads the configuration for the API generation from the properties file. It
 * is expected that the properties reside in
 * <code>resources/apigen.properties</code>.
 * 
 * @author BREDEX GmbH
 * @created 11.09.2014
 */
public class NameLoader {
    /** <code>RESOURCES_NAMEMAPPINGS_PROPERTIES</code> */
    private static final String RESOURCES_NAMEMAPPINGS_PROPERTIES =
        "resources/nameMappings.properties"; //$NON-NLS-1$
    
    /** <code>RESOURCES_ENUMMAPPINGS_PROPERTIES</code> */
    private static final String RESOURCES_ENUMMAPPINGS_PROPERTIES =
        "resources/enumMappings.properties"; //$NON-NLS-1$
    
    /** package base path */
    private static final String PACKAGE_BASE_PATH =
        "org.eclipse.jubula.toolkit."; //$NON-NLS-1$

    /** package base path */
    private static final String INTERNAL =
        ".internal"; //$NON-NLS-1$
    
    /** specific path for interfaces */
    private static final String PACKAGE_SPECIFIC_INTERFACE =
        ".components"; //$NON-NLS-1$
    
    /** specific path for implementation classes */
    private static final String PACKAGE_SPECIFIC_IMPLCLASS =
        ".internal.impl"; //$NON-NLS-1$

    /** specific path for implementation classes */
    private static final String FACTORY_NAME_EXTENSION =
        "Components"; //$NON-NLS-1$

    /** specific path for implementation classes */
    private static final String TOOLKITINFO_NAME_EXTENSION =
        "ToolkitInfo"; //$NON-NLS-1$

    /** Pattern for detecting types in java.lang */
    private static Pattern javaLang = Pattern.compile(
            "java\\.lang\\.([A-Z][a-zA-Z]*)"); //$NON-NLS-1$
    
    /** Pattern for detecting types in org.eclipse.jubula.toolkit.enums.ValueSets */
    private static Pattern jubulaEnum = Pattern.compile(
            "org\\.eclipse\\.jubula\\.toolkit\\.enums\\.ValueSets\\.([A-Z][a-zA-Z]*)"); //$NON-NLS-1$
    
    /**
     * <code>instance</code> the singleton instance
     */
    private static NameLoader instance = null;
    
    /** the mapping properties */
    private Properties m_mappingProperties;
    
    /** the enum mapping properties */
    private Properties m_enumMappingProperties;
    
    /**
     * The constructor.
     */
    private NameLoader() {
        try {
            URL nameResourceURL = NameLoader.class.getClassLoader()
                .getResource(RESOURCES_NAMEMAPPINGS_PROPERTIES);
            m_mappingProperties = new Properties();
            m_mappingProperties.load(nameResourceURL.openStream());
            
            URL enumResourceURL = NameLoader.class.getClassLoader()
                .getResource(RESOURCES_ENUMMAPPINGS_PROPERTIES);
            m_enumMappingProperties = new Properties();
            m_enumMappingProperties.load(enumResourceURL.openStream());
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * @return the singleton instance
     */
    public static NameLoader getInstance() {
        if (instance == null) {
            instance = new NameLoader();
        }
        return instance;
    }
    
    /**
     * Translates a comp system name (from e.g. an action or a parameter) 
     * to how it shall be used in api
     * @param name original name
     * @return the name which should be used in api
     */
    public String translateFromCompSystem(String name) {
        String desiredName = CompSystemI18n.getString(name);
        desiredName = desiredName.replace(
                StringConstants.MINUS, StringConstants.SPACE)
                    .replace(
                StringConstants.LEFT_PARENTHESIS, StringConstants.SPACE)
                    .replace(
                StringConstants.RIGHT_PARENTHESIS, StringConstants.SPACE)
                    .replace(
                StringConstants.SLASH, StringConstants.SPACE);
        desiredName = WordUtils.capitalize(desiredName);
        desiredName = StringUtils.deleteWhitespace(desiredName);
        desiredName = WordUtils.uncapitalize(desiredName);
        return desiredName;
    }
    
    /**
     * Checks in the name/enum mappings property file whether there is a mapping for a
     * given parameter name and returns it and if not, returns the original type
     * @param type the type of the parameter
     * @param name the name of the parameter
     * @return the name which should be used in api
     */
    public String findTypeForParameter(String type, String name) {
        String mapEntry = m_enumMappingProperties.getProperty(name);
        if (mapEntry != null) {
            return mapEntry;
        }
        
        return getRealTypeForParameter(type, name);
    }
    /**
     * Checks in the name mappings property file whether there is a mapping for a
     * given parameter name and returns it and if not, returns the original type
     * @param type the type of the parameter
     * @param name the name of the parameter
     * @return the name which should be used if you need the real type from the parameter
     */
    public String getRealTypeForParameter(String type, String name) {
        String mapEntry = m_mappingProperties.getProperty(type);
        if (mapEntry != null) {
            return mapEntry;
        }
        return type;
    }
    
    /**
     * @param toolkitName the toolkit name
     * @return the name extension of the api package name for the component
     */
    public String getClassPackageName(String toolkitName) {
        return getToolkitPackageName(toolkitName, false)
                + PACKAGE_SPECIFIC_IMPLCLASS;
    }
    
    /**
     * @param toolkitName the toolkit name
     * @return the name extension of the api package name for the component
     */
    public String getInterfacePackageName(String toolkitName) {
        return getToolkitPackageName(toolkitName, false)
                + PACKAGE_SPECIFIC_INTERFACE;
    }
    
    /**
     * @param toolkitName the toolkit name
     * @param internal whether the package should be internal
     * @return the toolkit package base name
     */
    public String getToolkitPackageName(String toolkitName, boolean internal) {
        String basePackageName = PACKAGE_BASE_PATH + toolkitName;
        if (internal) {
            basePackageName += INTERNAL;
        }
        return basePackageName;
    }
    
    /**
     * Translates a component name from the comp system to how it shall be used in the api
     * @param name the class name in comp system
     * @return the name of the class in the api
     */
    public String getClassName(String name) {
        String desiredName = CompSystemI18n.getString(name);
        desiredName = desiredName
                .replace(StringConstants.SLASH, StringConstants.SPACE);
        if (desiredName.startsWith(StringConstants.LEFT_PARENTHESIS)) {
            desiredName = StringUtils.substringAfter(
                    desiredName, StringConstants.RIGHT_PARENTHESIS);
        }
        desiredName = desiredName
                .replace(StringConstants.LEFT_PARENTHESIS,
                        StringConstants.SPACE)
                .replace(StringConstants.RIGHT_PARENTHESIS,
                        StringConstants.SPACE);
        desiredName = WordUtils.capitalize(desiredName);
        desiredName = StringUtils.deleteWhitespace(desiredName);
        return desiredName;
    }

    /**
     * Returns the toolkit name
     * @param toolkitDesriptor toolkit descriptor
     * @return the toolkit name
     */
    public String getToolkitPackageName(ToolkitDescriptor toolkitDesriptor) {
        return toolkitDesriptor.getName().toLowerCase();
    }
    
    /**
     * modifies a string such that it fits into api naming patterns
     * @param string the string
     * @return the adjusted string
     */
    public String executeExceptions(String string) {
        return string.replace("abstract", "base"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /**
     * Returns the toolkit name
     * @param toolkitName the toolkit name
     * @return the name for a toolkit
     */
    public String getToolkitName(String toolkitName) {
        return WordUtils.capitalize(toolkitName);
    }
    
    /**
     * Returns the name for a component factory for a toolkit
     * @param toolkitName the toolkit name
     * @return the name for a component factory for a toolkit
     */
    public String getFactoryName(String toolkitName) {
        return getToolkitName(toolkitName) + FACTORY_NAME_EXTENSION;
    }
    
    /**
     * Returns the name for a component factory for a toolkit
     * @param toolkitName the toolkit name
     * @return the name for a component factory for a toolkit
     */
    public String getToolkitComponentClassName(String toolkitName) {
        return getToolkitName(toolkitName) + TOOLKITINFO_NAME_EXTENSION;
    }
    
    /**
     * Checks whether the given name is a value in the enum map
     * @param name the name
     * @return <code>true</code> if and only if the given name is a value in the enum map
     */
    public boolean isInEnumMap(String name) {
        return m_enumMappingProperties.containsValue(name);
    }
    
    /**
     * Returns the enum type for a param
     * @param paramType the param
     * @return the enum
     */
    public String getEnumForParam(String paramType) {
        return m_enumMappingProperties.getProperty(paramType);
    }
    
    /**
     * Cuts the path of a type name if it is from the java.lang package or
     * from org.eclipse.jubula.toolkit.enums.ValueSets or leaves it as it is
     * @param paramType the type name
     * @return the shortened type name
     */
    public static String beautifyParamType(String paramType) {
        if (javaLang.matcher(paramType).matches()) {
            return paramType.replaceAll(javaLang.pattern(), "$1"); //$NON-NLS-1$
        } else if (jubulaEnum.matcher(paramType).matches()) {
            return paramType.replaceAll(jubulaEnum.pattern(), "$1"); //$NON-NLS-1$
        }
        return paramType;
    }
}
