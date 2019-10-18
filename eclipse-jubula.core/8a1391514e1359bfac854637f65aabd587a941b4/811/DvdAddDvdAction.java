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

import org.eclipse.jubula.examples.aut.dvdtool.model.Dvd;


/**
 * This is the action class for adding a dvd.
 *
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdAddDvdAction extends AbstractAction  {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller; // see findBugs
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdAddDvdAction(String name, 
        DvdMainFrameController controller) {
        super(name);
        
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        m_controller.addDvd(new Dvd());
        m_controller.updateDisableOrEnableActions();
    }
}
