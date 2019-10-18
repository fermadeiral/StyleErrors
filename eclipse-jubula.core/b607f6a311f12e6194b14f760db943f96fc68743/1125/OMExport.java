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
package org.eclipse.jubula.client.api.ui.utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.SerializationUtils;

/**
 * Containing encoded object mapping and information about
 * how it can be generated into a Java class.
 * @author BREDEX GmbH
 * @created 17.10.2014
 */
public class OMExport {
    /** the name of the class which is the target of the generation */
    private String m_targetClassName;
    /** the mapping of logical component name <--> encoded component identifier */
    private Map<String, String> m_objectMapping;

    /**
     * @param map the mapping
     * @param fileName the target file name
     */
    public OMExport(Map<String, String> map, String fileName) {
        setObjectMapping(map);
        setTargetClassName(fileName);
    }

    /**
     * @return the name of the class which is the target of the generation
     */
    public String getTargetClassName() {
        return m_targetClassName;
    }

    /**
     * @param name the name of the class which is the target of the generation
     */
    private void setTargetClassName(String name) {
        m_targetClassName = name;
    }
    
    /**
     * @return the map containing mapping from identifier to java qualifier
     */
    public Map<String, String> createIdentifierMap() {
        Map<String, String> identifierMap = new HashMap<String, String>();
        
        for (Entry<String, String> entry : getObjectMapping().entrySet()) {
            String key = entry.getKey();
            identifierMap.put(key, translateToJavaIdentifier(key));
        }
        return identifierMap;
    }
    
    /**
     * @return StringBuffer containing the map with the encoded object mappings
     */
    public StringBuffer createEncodedAssociations() {
        StringBuffer encodedAssociations = new StringBuffer();
        Map<String, String> identifierMap = new HashMap<String, String>();
       
        for (Entry<String, String> entry : getObjectMapping().entrySet()) {
            String key = entry.getKey();
            encodedAssociations.append(key);
            encodedAssociations.append(StringConstants.EQUALS_SIGN);
            encodedAssociations.append(entry.getValue());
            encodedAssociations.append(StringConstants.NEWLINE);
            identifierMap.put(key, translateToJavaIdentifier(key));
        }
        return encodedAssociations;
    }
    

    /**
     * Translates a string to a valid java identifier
     * @param key the string
     * @return a valid java identifier
     */
    private String translateToJavaIdentifier(String key) {
        String modifiedKey = key;
        String [] exceptions = new String[] {
            StringConstants.DOT,
            StringConstants.SPACE,
            StringConstants.BACKSLASH,
            StringConstants.SLASH,
            StringConstants.STAR,
            StringConstants.COLON,
            StringConstants.LEFT_BRACKET,
            StringConstants.RIGHT_BRACKET,
            StringConstants.LEFT_PARENTHESIS,
            StringConstants.RIGHT_PARENTHESIS,
            StringConstants.EQUALS_SIGN,
            StringConstants.PLUS,
            StringConstants.MINUS,
            StringConstants.PIPE};
        for (String exception : exceptions) {
            modifiedKey = modifiedKey.replace(
                    exception, StringConstants.UNDERSCORE);
        }
        return modifiedKey;
    }

    /**
     * @return the objectMapping
     */
    public Map<String, String> getObjectMapping() {
        return m_objectMapping;
    }

    /**
     * @param objectMapping the objectMapping to set
     */
    private void setObjectMapping(Map<String, String> objectMapping) {
        m_objectMapping = objectMapping;
    }

    /**
     * Serializes a component identifier
     * @param identifier the component identifier
     * @return the serialization
     */
    public static String getSerialization(ComponentIdentifier identifier)
            throws IOException {
        return SerializationUtils.encode(identifier);
    }
}