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
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdContentPanel;


/**
 * This is the action class for copy of text from the chapter label to 
 * the clipboard.
 * 
 * @author BREDEX GmbH
 * @created 27.02.2008
 */
public class DvdCopyChapterLabelToClipboardAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    /** the dummy textfield needed only for copying to clipboard */
    private transient JTextField m_dummyTextField = new JTextField();
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdCopyChapterLabelToClipboardAction(String name, 
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
        JLabel label = contentPanel.getLabelChapters();       
        String text = label.getText();

        // copy text to clipboard
        m_dummyTextField.setText(text);
        m_dummyTextField.selectAll();
        m_dummyTextField.copy();
    }
}