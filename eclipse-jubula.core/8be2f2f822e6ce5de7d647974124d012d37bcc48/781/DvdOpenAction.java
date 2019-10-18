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
import org.eclipse.jubula.examples.aut.dvdtool.persistence.DvdInvalidContentException;
import org.eclipse.jubula.examples.aut.dvdtool.persistence.DvdPersistenceException;


/**
 * This is the action class for opening a dvd library
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdOpenAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller; // see findBugs
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdOpenAction(String name, DvdMainFrameController controller) {
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        boolean open = true;
        if (m_controller.isChanged()) {
            boolean save = true;
            List<String> keys = new Vector<String>();
            keys.add("open.library.has.changed"); //$NON-NLS-1$
            keys.add("open.question.save"); //$NON-NLS-1$
            int choice = DvdDialogs.confirm3(m_controller.getDvdMainFrame(), 
                    "dialog.confirm.open.needs.save.title", keys); //$NON-NLS-1$                    
            switch (choice) {
                case DvdDialogs.YES:
                    save = true;
                    open = true;
                    break;
                case DvdDialogs.NO:
                    save = false;
                    open = true;
                    break;
                case DvdDialogs.CANCEL:
                    save = false;
                    open = false;
                    break;
                default:
                    save = false;
                    open = false;
            }
            if (save) {
                m_controller.getSaveAction().actionPerformed(e);
                if (m_controller.isChanged()) {
                    // saving cancelled -> do not open
                    open = false;
                }
            }
        }
        if (open) {
            DvdMainFrame frame = m_controller.getDvdMainFrame();
            int returnValue = frame.getFileChooser().showOpenDialog(frame);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                try {
                    File file = frame.getFileChooser().getSelectedFile(); 
                    DvdManager.singleton().open(m_controller, file);
                    m_controller.opened(file.getName());
                } catch (DvdInvalidContentException dice) {
                    DvdDialogs.showMessage(frame, dice.getMessage());
                } catch (DvdPersistenceException dpe) {
                    DvdDialogs.showError(frame, dpe.getMessage());
                } 
            }
        }
    }
}