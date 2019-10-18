/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.components;

import java.util.List;

import javafx.collections.ListChangeListener;
import javafx.event.EventTarget;

import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;

/**
 * Handles List Changes in the AUT
 *
 * @author BREDEX GmbH
 * @created 24.10.2013
 */
public class ChildListChangeHandler implements ListChangeListener<EventTarget> {

    /** Hierarchy **/
    private AUTJavaFXHierarchy m_hierarchy = ComponentHandler.getAutHierarchy();

    @Override
    public void onChanged(Change<? extends EventTarget> c) {
        c.next();
        List<? extends EventTarget> changedObjects = c.getRemoved();
        for (EventTarget o : changedObjects) {
            m_hierarchy.removeComponentFromHierarchy(o);
        }
        // Needed for the fact that sometimes the change getTo() has a higher value than the list itself
        List<? extends EventTarget> list =  c.getList();
        int size = list.size();
        if (c.wasAdded() && c.getTo() > size) {
            changedObjects = list.subList(c.getFrom(), size);
        } else {
            changedObjects = c.getAddedSubList();
        }
        for (EventTarget o : changedObjects) {
            m_hierarchy.createHierarchyFrom(o);
        }
    }

}
