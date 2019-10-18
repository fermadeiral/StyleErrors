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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

/**
 * This bundle activator installs the Jubula RCP accessor.
 * All embedded bundles are installed and only the specialized
 * bundles depending on Eclipse e3 or e4 are starting correctly.
 */
public class BundleInstaller implements BundleActivator {

    /** An bundle name to identify a pure e4 application. */
    private static final String E4_SPECIFIC_BUNDLE =
            "org.eclipse.e4.core.services"; //$NON-NLS-1$

    /** An bundle name to identify a pure e4 application. */
    private static final String SWT_SPECIFIC_BUNDLE =
            "org.eclipse.swt"; //$NON-NLS-1$

    /** The suffixes of the bundles used in Eclipse RCP e3. */
    private static final String BUNDLE_FOLDER_SUFFIX_SWT = ".swt"; //$NON-NLS-1$

    /** The suffixes of the bundles used in Eclipse RCP e3. */
    private static final String BUNDLE_FOLDER_SUFFIX_E3 = ".e3"; //$NON-NLS-1$

    /** The suffixes of the bundles used in Eclipse RCP e4. */
    private static final String BUNDLE_FOLDER_SUFFIX_E4 = ".e4"; //$NON-NLS-1$

    /** The suffixes of the bundles used in Eclipse RCP e4. */
    private static final String BUNDLE_FOLDER_SUFFIX_E4_SWT = ".e4.swt"; //$NON-NLS-1$

    /** The suffixes of the external extension bundles */
    private static final String BUNDLE_FOLDER_SUFFIX_EXT = ".ext"; //$NON-NLS-1$

    /**
     * Install embedded bundles, if they have not been already installed.
     * @param context The bundle context.
     * @throws Exception
     * @see #installBundles(BundleContext)
     * @see #startBundles(List)
     * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
     */
    public void start(BundleContext context) throws Exception {
        // install and start the bundles for the RCP accessor
        List<String> bundleFolderSuffixes = getBundleFolderSuffixes(context);
        try {
            List<Bundle> installedBundles = installAllBundles(
                    context, bundleFolderSuffixes);
            startBundles(installedBundles);
        } catch (Throwable t) {
            t.printStackTrace(); // unexpected exception
        }
    }

    /**
     * @param context The bundle context.
     * @return The bundle folder suffix depending on the RCP version
     *         identified by the existence of {@link #E4_SPECIFIC_BUNDLE},
     *         i.e. {@link #BUNDLE_FOLDER_SUFFIX_E3} or {@link #BUNDLE_FOLDER_SUFFIX_E4_SWT}.
     */
    private static List<String> getBundleFolderSuffixes(
            BundleContext context) {
        List<String> bundleFolderSuffixes = new ArrayList<String>();
        bundleFolderSuffixes.add(""); //$NON-NLS-1$
        Bundle[] installedBundles = context.getBundles();
        if (isBundleInstalled(installedBundles, E4_SPECIFIC_BUNDLE)) {
            bundleFolderSuffixes.add(BUNDLE_FOLDER_SUFFIX_E4);
            if (isBundleInstalled(installedBundles, SWT_SPECIFIC_BUNDLE)) {
                // e4 with SWT has been found
                bundleFolderSuffixes.add(BUNDLE_FOLDER_SUFFIX_SWT);
                bundleFolderSuffixes.add(BUNDLE_FOLDER_SUFFIX_E4_SWT);
            }
        } else {
            // e3 has been found (only with SWT)
            bundleFolderSuffixes.add(BUNDLE_FOLDER_SUFFIX_SWT);
            bundleFolderSuffixes.add(BUNDLE_FOLDER_SUFFIX_E3);
        }
        bundleFolderSuffixes.add(BUNDLE_FOLDER_SUFFIX_EXT);
        return bundleFolderSuffixes;
    }

    /**
     * @param installedBundles The array of installed bundles.
     * @param bundleName The bundle name searching for.
     * @return True, if the given array contains the given bundle name.
     */
    private static boolean isBundleInstalled(Bundle[] installedBundles,
            String bundleName) {
        for (int i = 0; i < installedBundles.length; i++) {
            if (bundleName.compareTo(
                    installedBundles[i].getSymbolicName()) == 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Calls {@link #installBundlesWithSuffix(BundleContext, String)} with all suffixes.
     * @param context The bundle context.
     * @param bundleFolderSuffixes A string list of the bundle folder suffixes.
     * @return A list of all newly installed bundles.
     * @throws BundleException
     * @throws IOException
     * @see BundleIterator#installBundle()
     */
    private static List<Bundle> installAllBundles(
            BundleContext context, List<String> bundleFolderSuffixes)
        throws BundleException, IOException {
        List<Bundle> bundles = new ArrayList<Bundle>();
        Iterator<String> suffixes = bundleFolderSuffixes.iterator();
        while (suffixes.hasNext()) {
            bundles.addAll(installBundlesWithSuffix(
                    context, suffixes.next()));
        }
        return bundles;
    }

    /**
     * Install all bundles located in the directory named "bundles",
     * if they are not already installed.
     * <p>Attention:
     * One bundle is randomly chosen out of a set of bundles with the same name
     * and with different versions located in the "bundles/" directory, but this
     * situation should normally not appear.
     * @param context The bundle context.
     * @param suffix The string defining the suffix of the bundle folder.
     * @return The newly installed bundles.
     */
    private static List<Bundle> installBundlesWithSuffix(BundleContext context,
            String suffix)
        throws IOException, BundleException,
                SecurityException, IllegalArgumentException {
        List<Bundle> bundles = new ArrayList<Bundle>();
        BundleIterator it = new BundleIterator(context, suffix);
        while (it.hasNext()) {
            it.next(); // move to next bundle in bundle folder
            Bundle newBundle = it.installBundle();
            if (newBundle != null) {
                // remember all newly installed bundles
                bundles.add(newBundle);
            }
        }
        return bundles;
    }

    /**
     * Start the given bundles (only newly installed).
     * Attention:
     * <ul>
     * <li>Newly installed bundles must be started to activate them in the first application invocation.</li>
     * <li>Newly installed bundles must be started <i>after</i> all necessary bundles are installed.
     *     Otherwise the OSGI framework may fail with unresolved dependencies.</li>
     * <li>If the bundles are already installed by a previous application invocation, the bundles are
     *     started automatically by the OSGI framework and are not started by this method.</li>
     * </ul>
     * @param bundles The list of bundles to start.
     * @throws BundleException
     */
    private static void startBundles(List<Bundle> bundles)
        throws BundleException {
        for (Bundle bundle : bundles) {
            bundle.start();
        }
    }

    /**
     * Stop all currently running bundles installed by this activator.
     * @param context The bundle context.
     * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
     */
    public void stop(BundleContext context) throws Exception {
        stopAllBundles(context, getBundleFolderSuffixes(context));
    }

    /**
     * @param context The bundle context.
     * @param bundleFolderSuffixes The string list of bundle folder suffixes,
     *        which defines the bundles to stop.
     */
    private static void stopAllBundles(
            BundleContext context, List<String> bundleFolderSuffixes) {
        Iterator<String> itSuffixes = bundleFolderSuffixes.iterator();
        while (itSuffixes.hasNext()) {
            BundleIterator it = new BundleIterator(
                    context, itSuffixes.next());
            while (it.hasNext()) {
                it.next();
                try {
                    it.uninstallBundle();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }
    }

}
