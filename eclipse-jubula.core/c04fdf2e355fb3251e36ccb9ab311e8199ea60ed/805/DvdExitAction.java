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
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdDialogs;


/**
 * This is the action class for closing the program.
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdExitAction extends AbstractAction {

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;

    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdExitAction(String name, DvdMainFrameController controller) {
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        boolean close = true;
        if (m_controller.isChanged()) {
            boolean save = true;
            List<String> keys = new Vector<String>();
            keys.add("exit.library.has.changed"); //$NON-NLS-1$
            keys.add("exit.question.save"); //$NON-NLS-1$
            int choice = DvdDialogs.confirm3(m_controller.getDvdMainFrame(), 
                    "dialog.confirm.exit.needs.save.title", //$NON-NLS-1$
                    keys);
            switch (choice) {
                case DvdDialogs.YES:
                    save = true;
                    close = true;
                    break;
                case DvdDialogs.NO:
                    save = false;
                    close = true;
                    break;
                case DvdDialogs.CANCEL:
                    save = false;
                    close = false;
                    break;
                default:
                    save = true;
                    close = false;
            }
            if (save) {
                m_controller.getSaveAction().actionPerformed(e);
                if (m_controller.isChanged()) {
                    // saving cancelled -> don't exit
                    close = false;
                }
            }
        }
        if (close) {
            JFrame frame = m_controller.getDvdMainFrame();
            frame.setVisible(false);
            frame.dispose();
            System.exit(0);
        }
    }
}