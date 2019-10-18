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
package org.eclipse.jubula.client.api.converter.utils;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import org.eclipse.jubula.client.api.converter.exceptions.MinorConversionException;


/**
 * Loads an component name information file.
 * It is expected that the properties reside in <code>resources/</code>.
 * 
 * @author BREDEX GmbH
 * @created 30.10.2014
 */
public class CompNameLoader {
    /** <code>BASE_PATH</code> */
    private static final String BASE_PATH = "resources/"; //$NON-NLS-1$
    
    /** <code>ENDING</code> */
    private static final String ENDING = ".properties"; //$NON-NLS-1$
    
    /** the mapping */
    private Properties m_properties = new Properties();
    
    /**
     * The constructor.
     * @param componentName the name of the component
     */
    public CompNameLoader(String componentName) {
        try {
            URL resourceURL = CompNameLoader.class.getClassLoader()
                .getResource(BASE_PATH + componentName + ENDING);
            
            if (resourceURL != null) {
                m_properties.load(resourceURL.openStream());
            } else {
                throw new MinorConversionException("Unable to retrieve information about " //$NON-NLS-1$
                        + componentName);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException(e);
        }
    }
    
    /**
     * Gets the mapped name to a key
     * @param key the key
     * @return the mapped name
     */
    public String get(String key) {
        String value = m_properties.getProperty(key);
        if (value == null) {
            String superComponentName =
                    m_properties.getProperty("superComp"); //$NON-NLS-1$
            if (superComponentName != null) {
                CompNameLoader superCompLoader =
                        new CompNameLoader(superComponentName);
                value = superCompLoader.get(key);
            }
        }
        return value;
    }
}
