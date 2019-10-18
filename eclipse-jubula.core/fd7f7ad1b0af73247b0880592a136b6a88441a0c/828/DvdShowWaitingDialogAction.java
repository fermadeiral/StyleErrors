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

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.WindowConstants;

import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This is the action class for showing a waiting dialog.
 * The dialog is mandatory and disappears after SECONDS_TO_WAIT seconds.
 * 
 * @author BREDEX GmbH
 * @created 27.02.2008
 */
public class DvdShowWaitingDialogAction extends AbstractAction {

    /** the number of seconds to wait for closing the dialog */
    private static final transient int SECONDS_TO_WAIT = 6;

    /** the controller of the main frame */
    private transient DvdMainFrameController m_controller;
    
    /**
     * public constructor
     * @param name the text to display
     * @param controller the controller of the main frame
     */
    public DvdShowWaitingDialogAction(String name, 
        DvdMainFrameController controller) {
        
        super(name);
        m_controller = controller;
    }
    
    /**
     * {@inheritDoc}
     */
    public void actionPerformed(ActionEvent e) {
        JFrame frame = m_controller.getDvdMainFrame();
        final JDialog dialog = 
            new JDialog(frame, 
                    Resources.getString("waiting.dialog.title"), //$NON-NLS-1$
                    true);
        Container contentPane = dialog.getContentPane();
        JLabel messageText = new JLabel(
                Resources.getString("waiting.dialog.message")); //$NON-NLS-1$
        final JProgressBar progressBar = new JProgressBar();
        
        progressBar.setIndeterminate(false);        
        contentPane.setLayout(new BorderLayout());
        contentPane.add(messageText, BorderLayout.NORTH);
        contentPane.add(progressBar, BorderLayout.SOUTH);        
        dialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        dialog.setResizable(false);
        int width = 150;
        int height = 60;
        int xPos = frame.getX() + (frame.getWidth() / 2) - (width / 2); 
        int yPos = frame.getY() + (frame.getHeight() / 2) - (height / 2); 
        dialog.setSize(width, height);
        dialog.setLocation(xPos, yPos);

        Thread waitThread = new Thread() {
            public void run() {
                try {
                    for (int i = 0; i <= 100; i++) {
                        progressBar.setValue(i);
                        Thread.sleep(10 * SECONDS_TO_WAIT);
                    }
                } catch (InterruptedException ex) {
                    // allow interruption
                } finally {
                    dialog.setVisible(false);
                }
            }
        };
        
        waitThread.start();
        dialog.setVisible(true);
    }
}