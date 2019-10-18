/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle;

import org.eclipse.jubula.client.teststyle.constants.Ext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;


/**
 * The activator class controls the plug-in life cycle
 */
public class Activator implements BundleActivator {
    
    /** The plug-in ID */
    public static final String PLUGIN_ID = 
        Ext.PLUGIN_ID; 
    
    /** The shared instance */
    private static Activator plugin;

    /**
     * The constructor
     */
    public Activator() {
        // nothing here
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        plugin = this;
        TeststyleHandler.getInstance().start();
        AnalyzeHandler.getInstance().start();
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        TeststyleHandler.getInstance().stop();
        AnalyzeHandler.getInstance().stop();
        plugin = null;
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
