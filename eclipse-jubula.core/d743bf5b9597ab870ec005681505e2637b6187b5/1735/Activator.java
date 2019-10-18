/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.app.autagent;

import org.eclipse.jubula.logging.Configurator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author BREDEX GmbH
 * @created Jun 21, 2011
 */
public class Activator implements BundleActivator {
    /** the bundle context */
    private static BundleContext context;

    /**
     * @return the bundle context.
     */
    static BundleContext getContext() {
        return context;
    }

    /** {@inheritDoc} */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
        Configurator.loadLogbackConfiguration("aut_agent"); //$NON-NLS-1$
    }

    /** {@inheritDoc} */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }
}