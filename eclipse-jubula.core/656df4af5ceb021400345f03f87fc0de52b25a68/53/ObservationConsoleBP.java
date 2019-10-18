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
package org.eclipse.jubula.autagent.common.remote.dialogs;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;

import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 * @created 20.06.2005
 * 
 */
public class ObservationConsoleBP {
    /** the logger */
    private static final Logger LOG = LoggerFactory
        .getLogger(ObservationConsoleBP.class);    
    
    /**
     * singleton
     */
    private static ObservationConsole shell = null;

    /**
     * instance
     */
    private static ObservationConsoleBP instance = null;
    
    
    /**
     * constructor
     *
     */
    private ObservationConsoleBP() {
        // private constructor
    }

    /**
     * creates a shell
     */
    public void create() {
        
        if (shell == null) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    
                    public void run() {
                        shell = new ObservationConsole();                    
                        initialize();
                        
                        //sets the window location to the bottom-left corner 
                        Toolkit screen = java.awt.Toolkit.getDefaultToolkit();
                        Dimension screenSize = screen.getScreenSize();
                        shell.setLocation((screenSize.width 
                                - shell.getWidth()), (screenSize.height 
                                        - shell.getHeight()) - 30);
                        
                        shell.setVisible(true);
                    }                    
                });
            } catch (InterruptedException e) {
                LOG.error("Error while closing RecordConsole", e); //$NON-NLS-1$
            } catch (InvocationTargetException e) {
                LOG.error("Error while closing RecordConsole", e); //$NON-NLS-1$
            }
        }

    }
        
    /**
     * Initialize AutFrame
     */
    private void initialize() {
        shell.setSize(480, 235);
        shell.setResizable(true);
        //shell.setUndecorated(true);
        shell.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }
    
    /**
     * @param check String
     */
    public void setCheckLabel(boolean check) {
        if (shell != null) {
            shell.setCheckLabel(check);
        }        
    }
    
    /**
     * @param recAction String
     */
    public void setRecordedAction(String recAction) {
        if (shell != null) {
            shell.appendTextArea(" - " + recAction); //$NON-NLS-1$
        }        
    }
    
    /** 
     */
    public void setRecordedActionFailed() {
        if (shell != null) {
            shell.appendTextArea("Info: UserAction not Recorded..."); //$NON-NLS-1$
        }
    }
    
    /**
     * @param extraMsg String
     */
    public void setExtraMessage(String extraMsg) {
        if (shell != null) {
            shell.appendTextArea("Info: " + extraMsg); //$NON-NLS-1$
        }        
    }
    
    /** 
     * disposes the shell if open
     */
    public void closeShell() {
        if (shell != null) {
            try {
                EventQueue.invokeAndWait(new Runnable() {
                    public void run() {
                        shell.dispose();
                        shell = null;
                    }
                });
            } catch (InterruptedException e) {
                LOG.error("Error while closing RecordConsole", e); //$NON-NLS-1$
            } catch (InvocationTargetException e) {
                LOG.error("Error while closing RecordConsole", e); //$NON-NLS-1$
            }

        }
    }
    
    /**
     * getting instance of this class
     * @return instance
     */
    public static ObservationConsoleBP getInstance() {
        if (instance == null) {
            instance = new ObservationConsoleBP();
        }
        return instance;
    }
}
