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
package org.eclipse.jubula.mylyn;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author BREDEX GmbH
 */
public class Activator extends Plugin {
    /** the bundle ID */
    public static final String ID = "org.eclipse.jubula.mylyn"; //$NON-NLS-1$
    /** context */
    private static BundleContext context;
    /** the current instance */
    private static Activator plugin;

    /** @return the context */
    static BundleContext getContext() {
        return context;
    }

    /**
     * @return instance of Activator
     */
    public static Activator getDefault() {
        return plugin;
    }
    
    /** {@inheritDoc} */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        plugin = this;
    }

    /** {@inheritDoc} */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
        plugin = null;
    }
}
