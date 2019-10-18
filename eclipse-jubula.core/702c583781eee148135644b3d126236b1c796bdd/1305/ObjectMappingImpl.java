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
package org.eclipse.jubula.client.internal.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.client.ObjectMapping;
import org.eclipse.jubula.client.exceptions.LoadResourceException;
import org.eclipse.jubula.tools.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for loading object mapping associations
 * @author BREDEX GmbH
 * @created Oct 09, 2014
 */
public class ObjectMappingImpl implements ObjectMapping {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
            ObjectMappingImpl.class);
    
    /** object mapping associations */
    private Properties m_objectMappingAssociations = new Properties();

    /** object mapping associations */
    private Map<String, ComponentIdentifier> m_map =
            new TreeMap<String, ComponentIdentifier>();
    
    /**
     * Utility class for loading object mapping association
     * @param input the input stream containing the encoded object mapping
     */
    public ObjectMappingImpl(InputStream input) {
        super();
        Validate.notNull(input, "The input stream must not be null."); //$NON-NLS-1$
        
        try {
            m_objectMappingAssociations.load(input);
            for (Object obj : m_objectMappingAssociations.keySet()) {
                if (obj instanceof String) {
                    String compName = (String) obj;
                    if (m_map.containsKey(compName)) {
                        log.error("There is already a mapping for the component name " //$NON-NLS-1$
                                + compName);
                    } else {
                        try {
                            String encodedString = m_objectMappingAssociations
                                    .getProperty(compName);
                            m_map.put(compName, getIdentifier(encodedString));
                        } catch (LoadResourceException e) {
                            log.error(e.getLocalizedMessage(), e);
                        }                    
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error while initialising the ObjectMappingLoader", e); //$NON-NLS-1$
        }
    }
    
    /** {@inheritDoc} */
    @Nullable public ComponentIdentifier get(@NonNull String compName) {
        Validate.notNull(compName, "The component name must not be null."); //$NON-NLS-1$
        
        return m_map.get(compName);
    }

    /**
     * Returns a component identifier instance for the given encoded component
     * identifier
     * 
     * @param encodedString
     *            the encoded component identifier string
     * @param <T>
     *            the type of the component
     * @return the component identifier or <code>null</code>
     * @throws LoadResourceException
     */
    public static <T> ComponentIdentifier<T> getIdentifier(String encodedString)
            throws LoadResourceException {
        try {
            if (encodedString != null) {
                Object decodedObject = SerializationUtils.decode(encodedString);
                if (decodedObject instanceof ComponentIdentifier) {
                    return (ComponentIdentifier<T>) decodedObject;
                }
                throw new LoadResourceException("The decoded object is " //$NON-NLS-1$
                        + "not of type 'org.eclipse.jubula.tools.ComponentIdentifier<T>'."); //$NON-NLS-1$
            }
        } catch (IOException e) {
            throw new LoadResourceException(
                    "Could load the given component name", e); //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
            throw new LoadResourceException(
                    "Problems during deserialization...", e); //$NON-NLS-1$
        }
        return null;
    }
}