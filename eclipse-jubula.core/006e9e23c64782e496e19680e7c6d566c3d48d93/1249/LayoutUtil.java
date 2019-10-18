/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.aut.adder.swing.gui;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Utility methods for layouting components
 * 
 * @author BREDEX GmbH
 */
public class LayoutUtil {
    
    /**Inset*/
    public static final Insets DEFAULT_INSETS = new Insets(5, 5, 5, 5);

    /**
     * forbid construction
     */
    private LayoutUtil() {
        // do nothing
    }

    /**
     * create GridBagLayout
     * @param x x
     * @param y y
     * @param width width
     * @param height height
     * @return a GridBagConstraints object
     */
    public static GridBagConstraints makegbc(int x, int y, int width, 
            int height) {

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.NONE;
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.gridwidth = width;
        gbc.gridheight = height;
        gbc.insets = DEFAULT_INSETS;
        return gbc;
    }

    /**
     * Calculates the dimension of a string
     * @param comp the component that should display the text
     * @param str the str
     * @return the dimension
     */
    public static Dimension getStringDimension(Component comp, String str) {
        FontMetrics fm = comp.getFontMetrics(comp.getFont());
        return new Dimension(fm.stringWidth(str), fm.getHeight());
    }
}