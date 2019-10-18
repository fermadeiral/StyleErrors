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

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;

import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;

/**
 * Handles property Changes in the AUT
 *
 * @author BREDEX GmbH
 * @created 24.10.2013
 */
public class ChildPropertyChangeHandler implements ChangeListener<EventTarget> {
    /** Hierarchy **/
    private AUTJavaFXHierarchy m_hierarchy = ComponentHandler.getAutHierarchy();

    @Override
    public void changed(ObservableValue<? extends EventTarget> observable,
            EventTarget oldValue, EventTarget newValue) {
        m_hierarchy.removeComponentFromHierarchy(oldValue);
        m_hierarchy.createHierarchyFrom(newValue);
    }
}