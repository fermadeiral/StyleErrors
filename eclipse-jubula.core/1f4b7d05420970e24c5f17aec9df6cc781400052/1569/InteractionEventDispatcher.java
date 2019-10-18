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
package org.eclipse.jubula.client.core.events;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * @author BREDEX GmbH
 * @created Nov 10, 2010
 */
public class InteractionEventDispatcher implements IDataChangedListener {
    /**
     * singleton instance 
     */
    private static InteractionEventDispatcher instance;

    /**
    * a set of programmable selection listener
    */
    private Set<IProgrammableSelectionListener> 
        m_progammableSelectionListeners = 
            new HashSet<IProgrammableSelectionListener>();

    /**
     * Constructor
     */
    private InteractionEventDispatcher() {
        instance = this;
    }

    /** to notify clients about ... */
    public interface IProgrammableSelectionListener {
        /**
         * @param s
         *            - The selections to process
         */
        public void processSelection(IStructuredSelection s);
    }

    /**
     * 
     * @param l
     *            - listener for programmable selection events
     */
    public void addIProgrammableSelectionListener(
            IProgrammableSelectionListener l) {
        m_progammableSelectionListeners.add(l);
    }

    /**
     * @param l
     *            - listener for programmable selection events
     */

    public void removeIProgrammableSelectionListener(
            IProgrammableSelectionListener l) {
        m_progammableSelectionListeners.remove(l);
    }

    /**
     * @param s
     *            - The selections to process
     */

    public void fireProgammableSelectionEvent(IStructuredSelection s) {
        final Set<IProgrammableSelectionListener> stableListeners = 
            new HashSet<IProgrammableSelectionListener>(
                m_progammableSelectionListeners);
        for (IProgrammableSelectionListener l : stableListeners) {
            l.processSelection(s);
        }
    }

    /**
     * @return the singleton instance
     */
    public static InteractionEventDispatcher getDefault() {
        if (instance == null) {
            instance = new InteractionEventDispatcher();
            DataEventDispatcher.getInstance()
                .addDataChangedListener(instance, true);
        }
        return instance;
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        for (DataChangedEvent e : events) {
            handleDataChanged(e.getPo(), e.getDataState());
        }
    }
    
    /** {@inheritDoc} */
    public void handleDataChanged(IPersistentObject po, DataState dataState) {
        if (dataState == DataState.Added) {
            fireProgammableSelectionEvent(new StructuredSelection(po));
        }
    }
}
