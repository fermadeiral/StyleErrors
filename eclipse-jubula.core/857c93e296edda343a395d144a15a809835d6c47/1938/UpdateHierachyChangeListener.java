/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.components;

import java.util.Objects;

import org.eclipse.jubula.rc.common.components.AUTHierarchy;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;

import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventTarget;

/**
 * this listener looks for changes on {@link StringProperty} and refreshes the {@link AUTHierarchy}
 * by removing and adding it again. Useful for Node.getIDProperty()
 * @author BREDEX GmbH
 * 
 * @param <T> the Type the listener should be for
 */
public class UpdateHierachyChangeListener<T> implements ChangeListener<T> {
   
    @Override
    public void changed(ObservableValue<? extends T> observable,
            T oldValue, T newValue) {
        if (observable instanceof ReadOnlyProperty) {
            @SuppressWarnings("rawtypes")
            Object bean = ((ReadOnlyProperty) observable).getBean();
            if (bean instanceof EventTarget 
                    && !Objects.equals(newValue, oldValue)) {
                EventTarget eventTarget = (EventTarget) bean;
                AUTJavaFXHierarchy hierarchy =
                        ComponentHandler.getAutHierarchy();
                if (hierarchy.getHierarchyContainer(eventTarget) != null) {
                    hierarchy.removeComponentFromHierarchy(eventTarget);
                    hierarchy.createHierarchyFrom(eventTarget);
                }
            }
        }
    }

}
