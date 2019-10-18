/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.alm.mylyn.core;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;

/**
 * @author BREDEX GmbH
 */
public class Activator extends Plugin {
    /** the bundle ID */
    public static final String ID = "org.eclipse.jubula.client.alm.mylyn.core"; //$NON-NLS-1$
    /** context */
    private static BundleContext context;
    /** the current instance */
    private static Activator plugin;
    /** the location of the ALM access properties file */
    private static final String ALM_ACCESS_PROPERTIES_LOCATION = "resources/almAccess.properties"; //$NON-NLS-1$
    /** ALM access properties */
    private Properties m_almAccessProperties = new Properties();

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

        URL entry = bundleContext.getBundle().getEntry(
                ALM_ACCESS_PROPERTIES_LOCATION);
        InputStream openStream = entry.openStream();
        try {
            getAlmAccessProperties().load(openStream);
        } finally {
            openStream.close();
        }
        plugin = this;
    }

    /** {@inheritDoc} */
    public void stop(BundleContext bundleContext) throws Exception {
        Activator.context = null;
        plugin = null;
        getAlmAccessProperties().clear();
    }

    /**
     * @return the almAccessProperties
     */
    public Properties getAlmAccessProperties() {
        return m_almAccessProperties;
    }

    /**
     * @param almAccessProperties the almAccessProperties to set
     */
    public void setAlmAccessProperties(Properties almAccessProperties) {
        m_almAccessProperties = almAccessProperties;
    }
}
