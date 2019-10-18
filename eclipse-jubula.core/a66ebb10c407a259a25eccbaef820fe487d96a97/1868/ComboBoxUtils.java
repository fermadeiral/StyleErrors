/*******************************************************************************
 * Copyright (c) 2018 BREDEX GmbH. All rights reserved. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License v1.0 which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors: BREDEX GmbH - initial API and implementation and/or initial
 * documentation
 *******************************************************************************/

package org.eclipse.jubula.rc.javafx.tester.util.compatibility;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Skin;

/**
 * Utils class to encapsulate the differences between java8/9
 * @author BREDEX GmbH
 *
 */
public class ComboBoxUtils {

    /** java 8 ComboBoxBaseSkin */
    private static final String JAVA8_COMBO_BASESKIN =
            "com.sun.javafx.scene.control.skin.ComboBoxBaseSkin"; //$NON-NLS-1$
    /** java 9 ComboBoxBaseSkin */
    private static final String JAVA9_COMBO_BASESKIN =
            "javafx.scene.control.skin.ComboBoxBaseSkin"; //$NON-NLS-1$
    /** java8 ComboBoxListViewSkin */
    private static final String JAVA8_COMBO_LISTVIEWSKIN =
            "com.sun.javafx.scene.control.skin.ComboBoxListViewSkin"; //$NON-NLS-1$
    /** java 9 ComboBoxListViewSkin */
    private static final String JAVA9_COMBO_LISTVIEWSKIN =
            "javafx.scene.control.skin.ComboBoxListViewSkin"; //$NON-NLS-1$
    /** the name of the arrow button field of a combo box skin */
    private static final String ARROW_BUTTON_FIELD_NAME = "arrowButton"; //$NON-NLS-1$
    /** get poput content method name */
    private static final String GET_POPUP_CONTENT = "getPopupContent"; //$NON-NLS-1$
    
    /** logger */
    private static Logger log = LoggerFactory.getLogger(ComboBoxUtils.class);

    /** */
    private ComboBoxUtils() {
        // Utility
    }

    /**
     * @param combobox the combobox
     * @return a {@link ListView} which is the list of the {@link ComboBox}
     */
    public static ListView<?> getPopUpContent(ComboBox<?> combobox) {
        Class<?> clazz = null;
        Object skin = combobox.getSkin();
        try {
            clazz = Class.forName(JAVA8_COMBO_LISTVIEWSKIN);
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName(JAVA9_COMBO_LISTVIEWSKIN);
            } catch (ClassNotFoundException e2) {
                throw new RuntimeException(e2);
            }
        }
        try {
            Method method = clazz.getMethod(GET_POPUP_CONTENT);
            Object o = method.invoke(skin);
            if (o instanceof ListView<?>) {
                return (ListView<?>) o;
            }
        } catch (NoSuchMethodException | SecurityException
                | IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * 
     * @param combobox the {@link ComboBox}
     * @return either the {@link ComboBox} or the arrow Button of the
     *         {@link ComboBox}
     */
    public static Node getArrowButton(ComboBox<?> combobox) {
        Skin<?> skin = combobox.getSkin();
        Class<?> clazz = null;
        try {
            clazz = Class.forName(JAVA8_COMBO_BASESKIN);
        } catch (ClassNotFoundException e) {
            try {
                clazz = Class.forName(JAVA9_COMBO_BASESKIN);
            } catch (ClassNotFoundException e2) {
                log.warn("ComboBox base skins not found"); //$NON-NLS-1$
                return combobox;
            }
        }
        try {
            Field declaredField =
                    clazz.getDeclaredField(ARROW_BUTTON_FIELD_NAME);
            declaredField.setAccessible(true);
            return (Node) declaredField.get(skin);
        } catch (IllegalArgumentException | IllegalAccessException
                | NoSuchFieldException | SecurityException e) {
            // ignore
        }
        return combobox;
    }
}
