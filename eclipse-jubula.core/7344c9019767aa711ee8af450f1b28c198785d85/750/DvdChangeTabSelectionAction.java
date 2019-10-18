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
import javax.swing.JTabbedPane;

/**
 * This is the action class for clearing the description text area.
 * 
 * @author BREDEX GmbH
 * @created 26.02.2008
 */
public class DvdChangeTabSelectionAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdChangeTabSelectionAction(String name, 
        DvdMainFrameController controller) {
        
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        JTabbedPane tabbedPane = 
            m_controller.getDvdMainFrame().getDvdDetailTabbedPane();
        int indexToSelect = (tabbedPane.getSelectedIndex() + 1);
        
        if (indexToSelect >= tabbedPane.getTabCount()) {
            indexToSelect = 0;
        }
        
        tabbedPane.setSelectedIndex(indexToSelect);
    }
}