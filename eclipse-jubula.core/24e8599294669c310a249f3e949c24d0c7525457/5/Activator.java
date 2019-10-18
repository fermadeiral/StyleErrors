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
package org.eclipse.jubula.app.testexec;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jubula.autagent.OsgiAUTStartHelper;
import org.eclipse.jubula.autagent.common.utils.AutStartHelperRegister;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends Plugin {
    /** plugin id */
    public static final String PLUGIN_ID = "org.eclipse.jubula.app.testexec"; //$NON-NLS-1$

    /** the shared instance*/
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
        plugin = this;
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        try {
            super.start(context);
            AutStartHelperRegister.INSTANCE.setAutStartHelper(
                    new OsgiAUTStartHelper());
        } catch (IllegalArgumentException iae) {
            AbstractCmdlineClient.printlnConsoleError(iae.getMessage());
            throw iae;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

}
