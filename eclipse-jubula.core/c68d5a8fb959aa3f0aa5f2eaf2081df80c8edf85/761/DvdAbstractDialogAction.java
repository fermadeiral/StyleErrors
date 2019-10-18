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

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdDialogs;


/**
 * This is the base class for action classes using dialogs.
 * 
 * @author BREDEX GmbH
 * @created 18.02.2008
 */
public abstract class DvdAbstractDialogAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /** the key of message to display in the dialog */
    private transient String m_dialogMessageKey;
    
    /**
     * public constructor
     * @param name the text to display for the action
     * @param controller the controller of the main frame
     * @param dialogMessageKey the key of message to display in the dialog
     */
    public DvdAbstractDialogAction(String name, 
            DvdMainFrameController controller, String dialogMessageKey) {
        super(name);
        m_controller = controller;
        m_dialogMessageKey = dialogMessageKey;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        boolean done = false;
        while (!done) {
            // ask for input
            String inputValue = DvdDialogs.getInput(m_controller
                    .getDvdMainFrame(), m_dialogMessageKey);
            
            if (inputValue != null) {
                inputValue = inputValue.trim();
                if (inputValue.equals("")) { //$NON-NLS-1$
                    continue;
                } 
                done = true;
                handleDialogInput(inputValue);
            } else {
                done = true;
            }
        }
    }
    
    /**
     * Handles the input value from the dialog.
     * 
     * @param inputValue the value that was entered in the dialog
     */
    public abstract void handleDialogInput(String inputValue);
    
}