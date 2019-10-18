/*******************************************************************************
 * Copyright (c) 2006, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.aut.adder.swt;

import org.eclipse.jubula.examples.aut.adder.swt.businessprocess.AutShellBP;
import org.eclipse.jubula.examples.aut.adder.swt.gui.AutShell;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Monitor;


/**
 * Starter for the Application Under Test (SWT-Version). It contains the main() - method.
 * 
 * @created 23.02.2006
 */
public class SimpleAdderSWT {
    
    /**
     * empty utility constructor
     */
    private SimpleAdderSWT() {
        // empty utility constructor
    }

    /**
     * The main method.
     * 
     * @param args a <code>String</code> value
     */
    public static void main(String[] args) {
        AutShellBP a = new AutShellBP();
        AutShell shell = a.getAutShell();
        shell.pack();
        Monitor monitor = Display.getCurrent().getPrimaryMonitor();
        Rectangle rect = monitor.getBounds();
        int x = rect.x + Math.max(0, (rect.width 
                - shell.getBounds().width) / 2);
        int y = rect.y + Math.max(0, (rect.height 
                - shell.getBounds().height) / 2);
        shell.setBounds(x, y,
                Math.max(300, shell.getBounds().width + 20),
                shell.getBounds().height);
        shell.open();
        final Display display = shell.getDisplay();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }
}