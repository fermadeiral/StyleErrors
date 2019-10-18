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
package org.eclipse.jubula.examples.aut.dvdtool;

import org.eclipse.jubula.examples.aut.dvdtool.control.DvdMainFrameController;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdMainFrame;


/**
 * This is the main class of the dvd tool.
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DVDTool {
    
    /**
     * private constructor, do not instantiate this class
     */
    private DVDTool() {
        // empty
    }

    /** 
     * main method
     * @param args the given arguments, if any
     */
    public static void main(String[] args) {
        checkForDevelopmentVersion(args);
        DvdMainFrame frame = new DvdMainFrameController().getDvdMainFrame();
        frame.setLocation(100, 90);
        frame.pack();
        frame.setVisible(true);
    }

    /**
     * check if some development state should be simulated
     * @param args arguments from main()
     */
    private static void checkForDevelopmentVersion(String[] args) {
        if (args.length == 1) {
            try {
                String s = args[0].toUpperCase();
                if ((s.length() == 2) && s.startsWith("V")) { //$NON-NLS-1$
                    int state = Integer.parseInt(s.substring(1));
                    DevelopmentState.instance().setState(state);
                }
            } catch (NumberFormatException e) {
                // ignore wrong parameters
            }
        }
    }
}
