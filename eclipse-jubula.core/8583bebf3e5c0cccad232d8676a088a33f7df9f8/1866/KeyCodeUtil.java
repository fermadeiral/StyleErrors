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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.jubula.tools.internal.exception.Assert;

import javafx.scene.input.KeyCode;

/**
 * This utils is to compensate the differences between Java8 and Java9
 * @author BREDEX GmbH
 *
 */
public class KeyCodeUtil {

    /** utils */
    private KeyCodeUtil() {
        // utils
    }

    /**
     * 
     * @param keyCode the keyCode
     * @return the underlying platform code see {@link KeyCode#impl_getCode()}
     *         or {@link KeyCode#getCode()}
     */
    public static int getKeyCode(KeyCode keyCode) {
        try {
            // java 8
            return getKeyCodeViaReflection("impl_getCode", keyCode); //$NON-NLS-1$
        } catch (Exception e) {
            // ignore it because might be java 9
        }
        try {
            // java9
            return getKeyCodeViaReflection("getCode", keyCode); //$NON-NLS-1$
        } catch (Exception e) {
            // empty
        }
        throw new NoSuchMethodError(
                "neither KeyCode.getCode() nor KeyCode.impl_getCode() found"); //$NON-NLS-1$

    }

    /**
     * 
     * @param string
     *            the method name
     * @param keyCode
     *            the {@link KeyCode} where we need the integer code from
     * @return the integer code
     * @throws NoSuchMethodException
     *             {@link Class#getMethod(String, Class...)}
     * @throws IllegalAccessException
     *             {@link Method#invoke(Object, Object...)}
     * @throws InvocationTargetException
     *             {@link Method#invoke(Object, Object...)}
     */
    private static int getKeyCodeViaReflection(String string, KeyCode keyCode)
            throws NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        Class<KeyCode> clazz = KeyCode.class;
        Method method = clazz.getMethod(string);
        Object o = method.invoke(keyCode);
        if (o instanceof Integer) {
            return ((Integer) o);
        }
        Assert.notReached();
        return -1;
    }
}
