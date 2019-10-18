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
package org.eclipse.jubula.client.toolkit.ui;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author BREDEX GmbH
 */
public class Activator implements BundleActivator {
    /**
     * the context
     */
    private static BundleContext context;

    /**
     * @return the context
     */
    static BundleContext getContext() {
        return context;
    }

    /** {@inheritDoc} */
    public void start(BundleContext bundleContext) throws Exception {
        Activator.context = bundleContext;
    }

    /** {@inheritDoc} */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
    }
}