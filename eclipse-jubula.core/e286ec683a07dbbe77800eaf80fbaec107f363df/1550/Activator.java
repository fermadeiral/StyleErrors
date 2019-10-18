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
package org.eclipse.jubula.client.core;

import org.eclipse.core.runtime.Plugin;
import org.eclipse.jubula.logging.Configurator;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.osgi.framework.BundleContext;

/**
 * @author BREDEX GmbH
 * @created Nov 29, 2010
 */
public class Activator extends Plugin {
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.eclipse.jubula.client.core"; //$NON-NLS-1$
    
    /** <code>RESOURCES_DIR</code> */
    public static final String RESOURCES_DIR = "resources/"; //$NON-NLS-1$

    /** The shared instance */
    private static Activator plugin;

    /**
     * Constructor
     */
    public Activator() {
        // empty
    }

    /**
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        try {
            Configurator.loadLogbackConfiguration("client"); //$NON-NLS-1$
        } catch (IllegalStateException ie) {
            // do nothing
        }
        // initializing the component system
        ComponentBuilder.getInstance().getCompSystem();
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
