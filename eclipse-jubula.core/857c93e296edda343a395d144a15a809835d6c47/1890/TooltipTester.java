/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester;

import java.util.Iterator;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.tester.AbstractTooltipTester;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.compatibility.WindowsUtil;

import javafx.scene.control.Tooltip;
import javafx.stage.Window;



/**
 * Toolkit specific commands for the <code>Tooltip</code>.
 * 
 * @author BREDEX GmbH
 * @created 19.05.2015
 */
public class TooltipTester extends AbstractTooltipTester {

    @Override
    public String getTooltipText() {

        return EventThreadQueuerJavaFXImpl.invokeAndWait("getTooltipText", //$NON-NLS-1$
                new Callable<String>() {
                    public String call() {
                        Iterator<Window> iter = WindowsUtil.getWindowIterator();
                        while (iter.hasNext()) {
                            Window window = iter.next();
                            if (window instanceof Tooltip) {
                                return ((Tooltip)window).getText();
                            }
                        }
                        return null;
                    }
                }
        );
    }
}
