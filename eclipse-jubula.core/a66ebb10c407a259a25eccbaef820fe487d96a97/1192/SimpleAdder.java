package org.eclipse.jubula.examples.aut.adder.swing;

import java.util.Arrays;
import java.util.List;

import org.eclipse.jubula.examples.aut.adder.swing.businessprocess.AutFrameBP;


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
/**
 * Starter for the Application Under Test. It contains the main() - method.
 *
 * @created 20.07.2004
 */
public class SimpleAdder {
    
    /**
     * Private constructor of the utility class
     *
     */
    private SimpleAdder() {
        // empty constructor
    }
    
    /**
     * The main method.
     * @param args a <code>String</code> value
     * 
     * There are the following allowed arguments to start the AUT with
     * -c or -complex starts the aut in an advanced mode, where you have
     * various useless settings.
     */
    public static void main(String[] args) {
        List argList = Arrays.asList(args);
        // parameter for starting AUT in a simple or advanced mode
        boolean complexMode =
            argList.contains("-complex") //$NON-NLS-1$
            || argList.contains("-c"); //$NON-NLS-1$
        boolean alternativeLayout =
            argList.contains("-alternative") //$NON-NLS-1$
            || argList.contains("-a"); //$NON-NLS-1$
        boolean slow =
            argList.contains("-slow") //$NON-NLS-1$
            || argList.contains("-s"); //$NON-NLS-1$
        
        new AutFrameBP(complexMode, alternativeLayout, slow);
    }
}
