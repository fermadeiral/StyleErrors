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
import javax.swing.JTextArea;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdContentPanel;


/**
 * This is the action class for clearing the description text area.
 * 
 * @author BREDEX GmbH
 * @created 20.02.2008
 */
public class DvdClearDescriptionAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdClearDescriptionAction(String name, 
        DvdMainFrameController controller) {
        
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        DvdContentPanel contentPanel = 
            m_controller.getDvdMainFrame().getDvdContentPanel();
        JTextArea descriptionTextArea = contentPanel.getTextAreaDescription();
        descriptionTextArea.setText(""); //$NON-NLS-1$
    }
}