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
package org.eclipse.jubula.client.ui.rcp.utils;

import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;

/**
 * @author BREDEX GmbH
 * @created Nov 9, 2005
 */
public abstract class JBThread extends IsAliveThread {

    /**
     * Constructor
     */
    public JBThread() {
        addErrorHandler();
    }

    /**
     * @param name
     *      String
     */
    public JBThread(String name) {
        super(name);
        addErrorHandler();
    }

    /**
     * {@inheritDoc}
     */
    public void start() {
        try {
            super.start();
        } catch (RuntimeException e) {
            Plugin.getDefault().handleError(e);
            errorOccurred();
        }
    }

    /**
     * do sth after an error occurred.
     *
     */
    protected abstract void errorOccurred();

    /**
     * adds a ErrorHandler
     *
     */
    private void addErrorHandler() {
        setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                Plugin.getDefault().handleError(e);
                errorOccurred();
            }
        });
    }
}
