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

import java.awt.Rectangle;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.NodeTraverseHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TableColumnBase;
import javafx.scene.control.TableView;

/**
 * {@link TableUtils} is to resolve the problems between the changes of Java 8 and Java 9 fore tables
 * @author BREDEX GmbH
 *
 */
public class TableUtils {
    
    /** getColumHeader method name */
    private static final String GET_COLUMN_HEADER_FOR = "getColumnHeaderFor"; //$NON-NLS-1$
    /** Java 8 */
    private static final String JAVA9_TABLE_HEADER_ROW = "javafx.scene.control.skin.TableHeaderRow"; //$NON-NLS-1$
    /** Java 9 */
    private static final String JAVA8_TABLE_HEADER_ROW = "com.sun.javafx.scene.control.skin.TableHeaderRow"; //$NON-NLS-1$
    /** for log messages */
    private static Logger log = LoggerFactory.getLogger(TableUtils.class);

    /** */
    private TableUtils() {
        // utils class
    }

    /**
     * 
     * @param table the {@link TableView}
     * @param column the column number
     * @param relative should the calculation be relative or absolute
     * @return the {@link Rectangle} or null if it is not found
     */
    public static Rectangle getNodeBoundsofHeader(Parent table,
            TableColumnBase<?, ?> column, boolean relative) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(
                    JAVA8_TABLE_HEADER_ROW);
        } catch (ClassNotFoundException e) {
            // ignore maybe its java 8
            try {
                clazz = Class
                        .forName(JAVA9_TABLE_HEADER_ROW);
            } catch (ClassNotFoundException e2) {
                String message = "neither " + JAVA8_TABLE_HEADER_ROW //$NON-NLS-1$
                        + " nor " + JAVA9_TABLE_HEADER_ROW + " found";  //$NON-NLS-1$//$NON-NLS-2$
                log.error(message, e2);
                throw new RuntimeException(message, e2);
            }
        }
        try {
            List<?> headerRow = NodeTraverseHelper.getInstancesOf(table, clazz);
            Object colH = null;
            for (Object tableHeaderRow : headerRow) {
                Method method = clazz.getMethod(GET_COLUMN_HEADER_FOR,
                        TableColumnBase.class);
                colH = method.invoke(tableHeaderRow, column);
                if (colH != null) {
                    if (relative) {
                        return NodeBounds.getRelativeBounds((Node) colH,
                                (Node) tableHeaderRow);
                    }
                    return NodeBounds.getAbsoluteBounds((Node) colH);
                }
            }
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            log.error("Error occured", e); //$NON-NLS-1$
        }
        return null;
    }
}
