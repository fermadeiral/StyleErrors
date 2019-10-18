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


/**
 * This is the action class for adding a language.
 *
 * @author BREDEX GmbH
 * @created 18.02.2008
 */
public class DvdAddLanguageAction extends DvdAbstractDialogAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdAddLanguageAction(String name, 
            DvdMainFrameController controller) {
        
        super(name, controller, "new.language.input.message"); //$NON-NLS-1$
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleDialogInput(String inputValue) {
        // add a category to current selection of the tree
        m_controller.addLanguage(inputValue);
    }
}
