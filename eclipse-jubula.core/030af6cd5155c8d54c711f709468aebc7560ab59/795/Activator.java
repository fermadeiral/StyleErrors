/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.wiki.ui;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * @author BREDEX GmbH
 */
public class Activator extends AbstractUIPlugin {
    /** context */
    private static BundleContext context;
    
    /** single instance of plugin */
    private static Activator activator;

    /** @return the context */
    static BundleContext getContext() {
        return context;
    }

    /** {@inheritDoc} */
    public void start(BundleContext bundleContext) throws Exception {
        super.start(bundleContext);
        Activator.context = bundleContext;
        activator = this;
    }

    /** {@inheritDoc} */
    public void stop(BundleContext bundleContext) throws Exception {
        super.stop(bundleContext);
        Activator.context = null;
    }
    /**
     * gets the plugin activator
     * @return the plugin activator
     */
    public static Activator getActivator() {
        return activator;
    }
}