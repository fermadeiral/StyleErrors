/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/
/**
 * 
 */
package org.eclipse.jubula.rc.javafx.tester.util.compatibility;

import java.lang.reflect.Method;
import java.util.Iterator;

import javafx.collections.ObservableList;
import javafx.stage.Window;

/**
 * This utils is to compensate the differences between Java8 and Java9
 * 
 * @author BREDEX GmbH
 *
 */
public class WindowsUtil {

    /** not needed */
    private WindowsUtil() {
        // utils
    }

    /**
     * @return the Iterator of all Windows see {@link Window#impl_getWindows()}
     *         or {@link Window#getWindows()}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static Iterator<Window> getWindowIterator() {

        Class<Window> clazz = Window.class;
        try {
            // java 8
            Method method = clazz.getMethod("impl_getWindows"); //$NON-NLS-1$
            Object o = method.invoke(null);
            if (o instanceof Iterator) {
                return ((Iterator) o);
            }
        } catch (Exception e) {
            // ignore it because might be java 9
        }
        try {
            // java9
            Method method = clazz.getMethod("getWindows"); //$NON-NLS-1$
            Object o = method.invoke(null);
            if (o instanceof ObservableList) {
                return ((ObservableList) o).iterator();
            }
        } catch (Exception e) {
            // empty
        }
        throw new NoSuchMethodError(
                "neither Window.getwindows() nor Window.impl_getWindows() found"); //$NON-NLS-1$
    }

}
