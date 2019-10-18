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
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdTechPanel;


/**
 * This is the action class for copy of selection value from the bonus checkbox 
 * to the clipboard.
 * A selected checkbox is represented by the string "TRUE".
 * A deselected checkbox is represented by the string "FALSE".
 * 
 * @author BREDEX GmbH
 * @created 25.02.2008
 */
public class DvdCopyBonusValueToClipboardAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    /** the dummy textfield needed only for copying to clipboard */
    private transient JTextField m_dummyTextField = new JTextField();
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdCopyBonusValueToClipboardAction(String name, 
        DvdMainFrameController controller) {
        
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        DvdTechPanel techPanel = m_controller.getDvdMainFrame()
            .getDvdTechMainPanel().getDvdTechPanel();
        JCheckBox bonusCheckBox = techPanel.getCheckBoxBonus();
        String valueStr = 
            bonusCheckBox.isSelected() ? "TRUE" : "FALSE"; //$NON-NLS-1$ //$NON-NLS-2$

        // copy value string to clipboard
        m_dummyTextField.setText(valueStr);
        m_dummyTextField.selectAll();
        m_dummyTextField.copy();
    }
}