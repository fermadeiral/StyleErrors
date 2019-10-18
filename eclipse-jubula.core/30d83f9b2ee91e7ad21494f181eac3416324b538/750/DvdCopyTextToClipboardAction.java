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
import javax.swing.text.JTextComponent;

/**
 * This is the base action class for copy of text from a JTextComponent to 
 * the clipboard. The extending class must provide that JTextComponent.
 * The whole text of the JTextComponent is copied. The selection is not changed.
 * 
 * @author BREDEX GmbH
 * @created 21.02.2008
 */
abstract class DvdCopyTextToClipboardAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdCopyTextToClipboardAction(String name, 
        DvdMainFrameController controller) {
        
        super(name);        
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        JTextComponent textComp = getTextComponent();
        
        // memorize current selection
        int startIx = textComp.getSelectionStart();
        int endIx = textComp.getSelectionEnd();
        
        // copy whole text to clipboard
        textComp.selectAll();
        textComp.copy();
        
        // restore memorized selection
        textComp.setSelectionStart(startIx);
        textComp.setSelectionEnd(endIx);
    }
    
    /**
     * Returns the controller of the main frame
     * @return the controller of the main frame
     */
    DvdMainFrameController getController() {
        return m_controller;
    }
    
    /**
     * Returns the corresponding JTextComponent
     * @return the corresponding JTextComponent
     */
    abstract JTextComponent getTextComponent();
}