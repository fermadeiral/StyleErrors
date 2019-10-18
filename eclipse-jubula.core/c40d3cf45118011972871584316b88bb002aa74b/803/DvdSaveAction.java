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
import java.io.File;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdDialogs;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdMainFrame;
import org.eclipse.jubula.examples.aut.dvdtool.persistence.DvdPersistenceException;


/**
 * This is the action class for saving the dvd library
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdSaveAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdSaveAction(String name, DvdMainFrameController controller) {
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        DvdMainFrame frame = m_controller.getDvdMainFrame();
        int returnValue = frame.getFileChooser().showSaveDialog(frame);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                File file = frame.getFileChooser().getSelectedFile();
                boolean save = true;
                if (file.exists()) {
                    List<String> keys = new Vector<String>();
                    keys.add("save.overwrite.confirmation.fileexists"); //$NON-NLS-1$
                    keys.add("save.overwrite.confirmation.question"); //$NON-NLS-1$
                    save = DvdDialogs.confirm2(m_controller.getDvdMainFrame(),
                            "dialog.confirm.overwrite.file.title", //$NON-NLS-1$
                            keys);
                }
                if (save) {
                    m_controller.updateModel();
                    DvdManager.singleton().save(file);
                    m_controller.saved(file.getName());
                }
            } catch (DvdPersistenceException dpe) {
                DvdDialogs.showError(frame, dpe.getMessage());
            }
        }
    }
}