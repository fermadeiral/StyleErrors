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

import javax.swing.text.JTextComponent;

/**
 * This is the action class for copy of text from the chapter textfield to 
 * the clipboard.
 * The whole text of the JTextComponent is copied. The selection is not changed.
 * 
 * @author BREDEX GmbH
 * @created 22.02.2008
 */
public class DvdCopyChapterTextToClipboardAction 
    extends DvdCopyTextToClipboardAction {

    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdCopyChapterTextToClipboardAction(String name, 
        DvdMainFrameController controller) {
        
        super(name, controller);        
    }
    
    /**
     * {@inheritDoc}
     */
    JTextComponent getTextComponent() {
        return getController().getDvdMainFrame()
            .getDvdContentPanel().getTextFieldChapters();
    }
}