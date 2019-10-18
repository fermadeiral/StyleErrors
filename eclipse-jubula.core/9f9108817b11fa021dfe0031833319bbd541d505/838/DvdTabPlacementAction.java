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

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

/**
 * This is the action class for setting the tab placement.
 * 
 * @author BREDEX GmbH
 * @created 07.02.2008
 */
public class DvdTabPlacementAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /** the tab placement defined by constant from JTabbedPane */
    private transient int m_tabPlacement;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     * @param tabPlacement the tab placement to set using constant 
     *        from JTabbedPane
     */
    public DvdTabPlacementAction(String name, 
        DvdMainFrameController controller, int tabPlacement) {
        
        super(name);        
        m_controller = controller;
        m_tabPlacement = tabPlacement;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        DvdManager.singleton().changeTabPlacement(m_controller, m_tabPlacement);
    }
    
}