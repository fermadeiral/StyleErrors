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

import javax.swing.JRadioButton;

/**
 * This is the action class for copy of fsk18 radiobutton to the clipboard.
 * 
 * The string that is copied to the clipboard has the format "{label}:{value}".
 * The value is "true" if the JRadioButton is selected and "false" otherwise.
 * 
 * @author BREDEX GmbH
 * @created 25.02.2008
 */
public class DvdCopyFsk18RadioButtonToClipboardAction 
    extends DvdCopyRadioButtonToClipboardAction {

    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdCopyFsk18RadioButtonToClipboardAction(String name, 
        DvdMainFrameController controller) {
        
        super(name, controller);        
    }
    
    /**
     * {@inheritDoc}
     */
    JRadioButton getRadioButton() {
        return getController().getDvdMainFrame()
            .getDvdContentPanel().getRadioButtonFsk18();
    }
}