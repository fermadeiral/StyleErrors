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
package org.eclipse.jubula.rc.javafx.tester.util;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javafx.scene.Node;
import javafx.scene.effect.Effect;
import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;

/**
 * Util class to highlight nodes
 *
 * @author BREDEX GmbH
 * @created 10.10.2013
 */
public class HighlightNode {

    /** for log messages */
    private static Logger log = LoggerFactory.getLogger(HighlightNode.class);
    
    /**
     * Name of the environment variable that defines the color
     * of the shadow for the object mapping
     */
    private static final String OBJECT_MAPPING_COLOR_VAR = "TEST_OBJECT_MAPPING_COLOR"; //$NON-NLS-1$
    
    
    /** The old effect of the Node **/
    private static Map<Node, Effect> oldEffects = new HashMap<>();
    
    /** the color of the shadow
     * default is {@link Color#GREEN}
     * you can set your own color with {@link #OBJECT_MAPPING_COLOR_VAR} as environment or system property and a string.
     * This should have the format as seen in {@link Color#web(String)}
     */
    private static Color shadowColor = Color.GREEN;

    /**
     * private Constructor
     */
    private HighlightNode() {
        // private Constructor
    }

    static {
        try {
            String value = EnvironmentUtils
                    .getProcessOrSystemProperty(OBJECT_MAPPING_COLOR_VAR);
            if (value != null) {
                shadowColor = Color.valueOf(value);
            }
        } catch (Exception e) {
            log.warn("Could not parse color for object mapping", e); //$NON-NLS-1$
        }
    }
    /**
     * Use this method only from the FX-Thread! Draws a border around the given
     * Node
     *
     * @param n
     *            the Node
     */
    public static void drawHighlight(Node n) {
        // If the effect property is bound to another property it is not allowed
        // to change it.
        if (n.effectProperty().isBound()) {
            return;
        }
        if (n.getEffect() != null) {
            oldEffects.put(n, n.getEffect());
        }

        n.setEffect(new InnerShadow(10, shadowColor));
    }

    /**
     * Use this method only from the FX-Thread! Removes the Border
     *
     * @param n
     *            the Node
     */
    public static void removeHighlight(Node n) {
        if (n.effectProperty().isBound()) {
            return;
        }
        Effect oldEffect = oldEffects.remove(n);
        n.setEffect(oldEffect);
    }
}
