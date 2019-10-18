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
package org.eclipse.jubula.client.core.utils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.exception.UnexpectedGenericTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * @author BREDEX GmbH
 * @created 23.10.2006
 * @param <TYPE> listener type
 */
public class ListenerManager <TYPE extends IGenericListener> {
    
    /** standard logging */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ListenerManager.class);
    
    /**
     * <code>m_listeners</code> listener for occurred events
     */
    private Set<TYPE> m_listeners = new HashSet<TYPE>();
    
    /**
     * @param l listener to register
     */
    public void addListener(TYPE l) {
        m_listeners.add(l);
    }
    
    /**
     * @param l listener unregister
     */
    public void removeListener(TYPE l) {
        m_listeners.remove(l);
    }
    
    /**
     * notify listener about occurred event
     * @param params list of parameters
     */
    public void fireNotification(List< ? extends Object> params) {
        final Set<TYPE> stableListeners = new HashSet<TYPE>(m_listeners);
        for (TYPE l : stableListeners) {
            try {
                l.checkGenericListElementType(params);
                l.eventOccurred(params);
            } catch (UnexpectedGenericTypeException u) {
                LOG.error(u.getMessage());
            } catch (Throwable t) {
                LOG.error(Messages.UnhandledExceptionWhileCallingListeners, t); 
            }
        }        
    }
}