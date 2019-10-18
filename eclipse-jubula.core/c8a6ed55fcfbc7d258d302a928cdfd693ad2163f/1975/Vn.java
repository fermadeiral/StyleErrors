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
package org.eclipse.jubula.version;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.lang.text.StrSubstitutor;
import org.eclipse.core.runtime.Plugin;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 */
public class Vn extends Plugin {
    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.eclipse.jubula.version"; //$NON-NLS-1$

    /** key for property access */
    public static final String BUILD_VERSION_KEY = "build.version"; //$NON-NLS-1$

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(Plugin.class);

    /** The shared instance */
    private static Vn plugin;

    /** The version information */
    private Version m_version = Version.emptyVersion;

    /** {@inheritDoc} */
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;

        URL url = getBundle().getResource("version.properties"); //$NON-NLS-1$

        if (url != null) {
            Properties p = new Properties();
            InputStream versionInfo = null;
            try {
                versionInfo = url.openStream();
                p.load(versionInfo);
                String versionString = StrSubstitutor.replace(
                        p.getProperty(BUILD_VERSION_KEY), p);
                m_version = new Version(versionString);
            } catch (Throwable t) {
                log.warn(t.getLocalizedMessage(), t);
            } finally {
                if (versionInfo != null) {
                    versionInfo.close();
                }
            }
        }
    }

    /** {@inheritDoc} */
    public void stop(BundleContext context) throws Exception {
        plugin = null;
        m_version = Version.emptyVersion;
        super.stop(context);
    }

    /**
     * Returns the shared instance
     *
     * @return the shared instance
     */
    public static Vn getDefault() {
        return plugin;
    }

    /**
     * Returns the shared version information
     *
     * @return the shared version information
     */
    public Version getVersion() {
        return m_version;
    }
}