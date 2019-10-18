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
import javax.swing.JRadioButton;
import javax.swing.JTextField;

/**
 * This is the base action class for copy of JRadioButtons to the clipboard.
 * The extending class must provide that JRadioButton.
 * 
 * The string that is copied to the clipboard has the format "{label}:{value}".
 * The value is "true" if the JRadioButton is selected and "false" otherwise.
 * 
 * @author BREDEX GmbH
 * @created 25.02.2008
 */
abstract class DvdCopyRadioButtonToClipboardAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    /** the dummy textfield needed only for copying to clipboard */
    private transient JTextField m_dummyTextField = new JTextField();
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdCopyRadioButtonToClipboardAction(String name, 
        DvdMainFrameController controller) {
        
        super(name);        
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        JRadioButton button = getRadioButton();
        String text = 
            button.getText() + ":" //$NON-NLS-1$
            + (button.isSelected() ? "true" : "false"); //$NON-NLS-1$ //$NON-NLS-2$

        // copy text to clipboard
        m_dummyTextField.setText(text);
        m_dummyTextField.selectAll();
        m_dummyTextField.copy();
    }
    
    /**
     * Returns the controller of the main frame
     * @return the controller of the main frame
     */
    DvdMainFrameController getController() {
        return m_controller;
    }
    
    /**
     * Returns the corresponding RadioButton
     * @return the corresponding RadioButton
     */
    abstract JRadioButton getRadioButton();
}