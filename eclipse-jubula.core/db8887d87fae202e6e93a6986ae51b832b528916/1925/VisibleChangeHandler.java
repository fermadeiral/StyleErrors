/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.components;

import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
/**
 * Handles Visibility Changes of components in the AUT
 *
 * @author BREDEX GmbH
 * @created 26.5.2015
 */
public enum VisibleChangeHandler implements ChangeListener<Boolean> {
    /**Singleton**/
    INSTACE;
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            VisibleChangeHandler.class);
    /** Hierarchy **/
    private AUTJavaFXHierarchy m_hierarchy = ComponentHandler.getAutHierarchy();
    
    @Override
    public void changed(ObservableValue<? extends Boolean> observable,
            Boolean oldValue, Boolean newValue) {
        if (!(observable instanceof BooleanProperty)) {
            // this should not happen
            log.error("The observed visible property is not a BooleanProperty"); //$NON-NLS-1$
            return;
        }
        if (!newValue) {
            m_hierarchy.removeComponentFromHierarchy(
                    (EventTarget) ((BooleanProperty)observable).getBean());
        } else {
            m_hierarchy.createHierarchyFrom(
                    (EventTarget) ((BooleanProperty)observable).getBean());
        }

    }

}
