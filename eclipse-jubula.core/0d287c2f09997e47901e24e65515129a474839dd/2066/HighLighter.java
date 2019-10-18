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
package org.eclipse.jubula.rc.swing.tester.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;



/**
 * This class high- and lowlights any JComponent with a small green border.
 *
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class HighLighter {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(HighLighter.class);
    
    /** the color for the border, default = BX-green **/
    private Color m_defaultBorderColor = new Color(Constants.MAPPING_R,
                                                   Constants.MAPPING_G, 
                                                   Constants.MAPPING_B);

    /** the color for the border, default = BX-green **/
    private Color m_borderColor = m_defaultBorderColor;
    
    /**
     * High ligths the given Component by a border painted to the glass pane.
     *  @param component the component to highlight, will not be modified
     * @param border the color to highlight with
     */
    public void highLight(Component component, Color border) {
        if (border != null) {
            m_borderColor = border;
        } else {
            m_borderColor = m_defaultBorderColor;
        }
        if (component instanceof JComponent) {
            paintBorder((JComponent) component);
        } else if (log.isWarnEnabled()) {
            log.warn("highLight() called for a component " //$NON-NLS-1$
                    + "which is not a JComponent: " //$NON-NLS-1$
                    + component.getClass().getName());
        }
    }
    
    /**
     * Removes the high light border by repainting the component.
     * @param component the component to low light, will not be modified
     */
    public void lowLight(Component component) {
        component.repaint();
    }

    /**
     * Paints a border on the glasspane inside of <code>component</code>.
     * @param component the component to highlight
     */
    private void paintBorder(JComponent component) {
        Component glassPane = getGlassPane(component);
        if (glassPane == null) {
            log.warn("no glass pane for component: " + component.toString()); //$NON-NLS-1$
            if (log.isInfoEnabled()) {
                log.warn("no glass pane for component: " + component.toString()); //$NON-NLS-1$
            }
            // RETURN from here
            return;
        }
        
        Graphics graphics = glassPane.getGraphics();
        if (graphics == null) {
            if (log.isWarnEnabled()) {
                log.warn(("paintBorder() called for a currently not displayable component: " //$NON-NLS-1$
                    + component.toString()));
            }
            // RETURN from here
            return;
        }
        Color oldColor = graphics.getColor();
        graphics.setColor(m_borderColor);
        Rectangle bounds = SwingUtilities.convertRectangle(
                component, component.getVisibleRect(), glassPane);
        graphics.drawRect(bounds.x, bounds.y, bounds.width - 1,
                bounds.height - 1);
        graphics.setColor(oldColor);
    }

    /**
     * Gets the glass pane from the root pane, if any
     * @param component the JComponent to get the glass pane for
     * @return the glass pane for the given <code>component</code> or null ifno root pane is found.
     */
    private Component getGlassPane(JComponent component) {
        Component result = null;

        JRootPane rootPane = component.getRootPane();
        if (rootPane != null) {
            result = rootPane.getGlassPane();
        } else if (log.isWarnEnabled()) {
            log.warn("no root pane for " + component.getName()); //$NON-NLS-1$
        }
        return result;
    }
}