/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.controllers;

import java.util.Iterator;

import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingMultiPageEditor;
/**
 * Tracks the open ObjectMappingMultiPageEditor
 *
 * @author BREDEX GmbH
 * @created 17.01.2016
 */
public enum OpenOMETracker {
    /** singleton **/
    INSTANCE;
    
    /** list */
    private ObservableList m_openEditors = new WritableList();

    /**
     * Adds a IChangeListener to the list of open ObjectMappingMultiPageEditors
     * @param l the listener which should be added
     */
    public void addListener(IChangeListener l) {
        m_openEditors.addChangeListener(l);
    }

    /**
     * Removes a IChangeListener from the list of open ObjectMappingMultiPageEditors
     * @param l the listener which should be removed
     */
    public void removeListener(IChangeListener l) {
        m_openEditors.removeChangeListener(l);
    }

    /**
     * Adds an ObjectMappingMultiPageEditor
     * @param ome the ObjectMappingMultiPageEditor
     */
    public void addOME(ObjectMappingMultiPageEditor ome) {
        if (!m_openEditors.contains(ome)) {
            m_openEditors.add(ome);
        }
    }

    /**
     * Get the Iterator to iterate over the list of open ObjectMappingMultiPageEditors
     * @return the iterator
     */
    public Iterator getIterator() {
        return m_openEditors.iterator();
    }
    
    /**
     * Removes the ObjectMappingMultiPageEditor
     * @param ome the ObjectMappingMultiPageEditor which should be removed
     * @return true if the list contained the editor
     */
    public boolean removeOME(ObjectMappingMultiPageEditor ome) {
        return m_openEditors.remove(ome);
    }
}
