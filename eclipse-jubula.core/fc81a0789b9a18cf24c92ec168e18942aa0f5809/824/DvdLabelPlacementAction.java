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
 * This is the action class for setting label placement in the rating panel.
 * 
 * @author BREDEX GmbH
 * @created 19.02.2008
 */
public class DvdLabelPlacementAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /** the label placement defined by constant from AbstractButton */
    private transient int m_labelPlacement;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     * @param labelPlacement the label placement to set using constant 
     *        from AbstractButton
     */
    public DvdLabelPlacementAction(String name, 
        DvdMainFrameController controller, int labelPlacement) {
        
        super(name);        
        m_controller = controller;
        m_labelPlacement = labelPlacement;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        DvdManager.singleton()
            .changeLabelPlacement(m_controller, m_labelPlacement);
    }
    
}