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
 * This is the action class for disabling or enabling of categories.
 * The corresponding dvds are also set disabled or enabled accordingly.
 * 
 * @author BREDEX GmbH
 * @created 12.02.2008
 */
public class DvdDisableOrEnableCategoryAction extends AbstractAction  {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /** the enable state to be set */
    private transient boolean m_enableState;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     * @param enableState the enable state to set
     */
    public DvdDisableOrEnableCategoryAction(String name, 
        DvdMainFrameController controller, boolean enableState) {
        
        super(name);
        
        m_controller = controller;
        m_enableState = enableState;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        m_controller.setCurrentCategoryEnableState(m_enableState);
        m_controller.updateDisableOrEnableActions();
    }
    
}