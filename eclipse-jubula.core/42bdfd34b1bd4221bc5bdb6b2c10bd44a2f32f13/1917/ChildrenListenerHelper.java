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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventTarget;

/**
 * Helper-Class for adding listeners to children of components, this is relevant
 * for noticing changes in the AUT and changing the Hierarchy respectively.
 *
 * @author BREDEX GmbH
 * @created 24.10.2013
 */
public class ChildrenListenerHelper {

    /** List Handler **/
    private static ListChangeListener<EventTarget> listhandler =
                    new ChildListChangeHandler();

    /** Property Handler **/
    private static ChangeListener<EventTarget> prophandler =
                    new ChildPropertyChangeHandler();

    /**
     * Private Constructor
     */
    private ChildrenListenerHelper() {
        // private
    }

    /**
     * Adds the correct change listener to the given object
     *
     * @param list
     *            the list to listen to for changes
     */
    public static void addListener(
            ObservableList<? extends EventTarget> list) {
        list.addListener(listhandler);
    }

    /**
     * Adds the correct change listener to the given object
     *
     * @param prop
     *            the property to listen to for changes
     */
    public static void addListener(
            ReadOnlyObjectProperty<? extends EventTarget> prop) {
        prop.addListener(prophandler);
    }

    /**
     * Removes the correct change listener from the given object
     *
     * @param list
     *            the list to remove the listener from
     */
    public static void removeListener(
            ObservableList<? extends EventTarget> list) {
        list.removeListener(listhandler);
    }

    /**
     * Removes the correct change listener from the given object
     *
     * @param prop
     *            the prop to remove the listener from
     */
    public static void removeListener(
            ObjectProperty<? extends EventTarget> prop) {
        prop.removeListener(prophandler);
    }
}