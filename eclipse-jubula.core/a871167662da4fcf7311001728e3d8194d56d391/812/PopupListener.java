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
package org.eclipse.jubula.examples.aut.dvdtool.control;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;

/**
 * This class shows a popup menu when it is triggered by a mouse event.
 * 
 * @author BREDEX GmbH
 * @created 15.02.2008
 */
public class PopupListener extends MouseAdapter {
    
    /** popup menu to be shown */
    private JPopupMenu m_popupMenu;
    
    /**
     * public constructor, initialises this listener
     * 
     * @param popupMenu
     *            the popup menu to be shown when popup is triggered
     */
    public PopupListener(JPopupMenu popupMenu) {
        m_popupMenu = popupMenu;
    }
    
    
    /**
     * {@inheritDoc}
     */
    public void mousePressed(MouseEvent event) {
        maybeShowPopup(event);
    }
    
    /**
     * {@inheritDoc}
     */
    public void mouseReleased(MouseEvent event) {
        maybeShowPopup(event);
    }
    
    /**
     * Shows a popupmenu if the mouseevent is a popup trigger
     * @param event a mouseevent
     */
    private void maybeShowPopup(MouseEvent event) {
        if (event.isPopupTrigger()) {
            m_popupMenu.show(event.getComponent(), event.getX(), event
                    .getY());
        }
    }
    
}