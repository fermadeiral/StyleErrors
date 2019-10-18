/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.rcp.installer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * Helper class for installing OSGi bundles located in a bundle. This class
 * requires bundle files (*.jar) in the directory named "bundles/" in the given
 * bundle context. The file names must contain a version between the last '_'
 * and the file name extension.
 */
public class BundleIterator {

    /** The directory to search for bundles. */
    private static final String BUNDLES_DIR = "bundles"; //$NON-NLS-1$

    /** The file pattern to search for bundles. */
    private static final String FILE_PATTERN = "*.jar"; //$NON-NLS-1$

    /** The context containing the bundles to install. */
    private BundleContext m_context;

    /** The URL list of the bundles to install. */
    private Enumeration<URL> m_urls;

    /** The current URL of the iterator. */
    private URL m_url;

    /** The current bundle name of the iterator. */
    private String m_bundleName;

    /** The current bundle of the iterator. */
    private Bundle m_bundle;

    /**
     * @param context
     *            The context containing the bundles to install in the directory
     *            named "bundles/". Each bundle file must end with ".jar".
     * @param bundleFolderSuffix The suffix for the bundle folder added to "bundles/".
     */
    public BundleIterator(BundleContext context, String bundleFolderSuffix) {
        this.m_context = context; // store context for later usage
        m_urls = context.getBundle().findEntries(
                         // the directory searching for bundles
                        BUNDLES_DIR + bundleFolderSuffix,
                        // the file pattern (*.jar)
                        FILE_PATTERN,
                        // false = search not recursively
                        false);
        if (m_urls == null) {
            // create an empty enumeration if no bundles have been found
            final List<URL> emptyList = Collections.emptyList();
            m_urls = Collections.enumeration(emptyList);
        }
    }

    /**
     * Iterator method.
     *
     * @return True, if there are one or more bundles left to install, otherwise
     *         false.
     */
    public boolean hasNext() {
        return m_urls.hasMoreElements();
    }

    /**
     * Iterate to next bundle.
     * @return The current bundle, if it is already installed, otherwise null.
     */
    public Bundle next() {
        m_url = m_urls.nextElement();
        m_bundleName = getBundleName(m_url);
        m_bundle = Platform.getBundle(m_bundleName);
        return m_bundle;
    }

    /**
     * Installs the current bundle, if it has not been already installed.
     * @return The already installed or newly installed bundle.
     * @throws IOException Errors with input stream of bundle.
     * @see BundleContext#installBundle(String, java.io.InputStream)
     *      for detailed exception description.
     */
    public Bundle installBundle()
        throws IOException, BundleException,
                SecurityException, IllegalStateException {
        if (m_bundle != null) {
            // bundle exists
            return m_bundle;
        }
        // bundle does not exist, so install it
        BufferedInputStream inputStream = new BufferedInputStream(
                m_url.openStream());
        try {
            m_bundle = m_context.installBundle(m_bundleName, inputStream);
        } finally {
            inputStream.close(); // installBundle() needs closing of input
                                 // stream
        }
        return m_bundle;
    }

    /**
     * Stop the current bundle, if it is installed and activated.
     * @return True, if the bundle was active and has been stopped, otherwise false.
     */
    public boolean uninstallBundle() {
        if (m_bundle != null) {
            try {
                m_bundle.uninstall(); // un-install bundle
                return true;
            } catch (Throwable e) {
                System.err.println(e);
            }
        }
        return false;
    }

    /**
     * Convert e. g.
     * "bundleentry://6.fwk29851476/bundles/de.test_1.0.0.201301182302.jar"
     * to "de.test" by using the string between the last char '/' and
     * '_'.
     *
     * @param url
     *            The URL of the bundle.
     * @return The bundle name.
     */
    private static String getBundleName(URL url) {
        String urlString = url.getFile();
        int indexBegin = urlString.lastIndexOf('/') + 1;
        int indexEnd = urlString.lastIndexOf('_');
        return urlString.substring(indexBegin, indexEnd);
    }

}
