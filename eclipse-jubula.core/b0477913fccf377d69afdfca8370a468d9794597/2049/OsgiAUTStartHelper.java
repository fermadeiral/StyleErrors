/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.jubula.autagent.common.commands.IStartAut;
import org.eclipse.jubula.autagent.common.monitoring.IMonitoring;
import org.eclipse.jubula.autagent.common.monitoring.MonitoringDataStore;
import org.eclipse.jubula.autagent.common.monitoring.MonitoringUtil;
import org.eclipse.jubula.autagent.common.utils.IAUTStartHelper;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.ZipUtil;
import org.eclipse.osgi.service.datalocation.Location;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 *
 */
public class OsgiAUTStartHelper
        implements IAUTStartHelper {
    
    /** 
     * the name of the bundle JAR Manifest Attribute that indicates that the
     * bundle is a source-bundle
     */
    private static final String SOURCE_BUNDLE_MANIFEST_ATTR = 
            "Eclipse-SourceBundle"; //$NON-NLS-1$
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(OsgiAUTStartHelper.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public File getInstallationDirectory() {
        Location installLoc = Platform.getInstallLocation();
        String installDir = installLoc.getURL().getFile();
        return new File(installDir);
    }
    

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMonitoringAgent(Map<String, String> parameters) {
        String autId = parameters.get(
                AutConfigConstants.AUT_ID);
        MonitoringDataStore mds = MonitoringDataStore.getInstance();
        boolean duplicate = MonitoringUtil.checkForDuplicateAutID(autId);
        if (!duplicate) {            
            mds.putConfigMap(autId, parameters); 
        }
        String agentString = null;
        String monitoringImplClass = parameters.get(
                MonitoringConstants.AGENT_CLASS); 
        String bundleId = parameters.get(
                MonitoringConstants.BUNDLE_ID);
        try {  
            Bundle bundle = Platform.getBundle(bundleId);
            if (bundle == null) {
                LOG.error("No bundle was found for the given bundleId"); //$NON-NLS-1$
                return null;
            }
            Class<?> monitoringClass = 
                    bundle.loadClass(monitoringImplClass);
            Constructor<?> constructor = monitoringClass.getConstructor();
            IMonitoring agentInstance = 
                (IMonitoring)constructor.newInstance();
            agentInstance.setAutId(autId);
            //set the path to the agent jar file
            agentInstance.setInstallDir(FileLocator.getBundleFile(bundle));
            agentString = agentInstance.createAgent();
            if (!duplicate) {
                mds.putMonitoringAgent(autId, agentInstance);  
            } 
        } catch (InstantiationException e) {
            LOG.error("The instantiation of the monitoring class failed ", e); //$NON-NLS-1$
        } catch (IllegalAccessException e) {
            LOG.error("Access to the monitoring class failed ", e); //$NON-NLS-1$
        } catch (SecurityException e) {
            LOG.error("Access to the monitoring class failed ", e); //$NON-NLS-1$
        } catch (NoSuchMethodException e) {
            LOG.error("A method in the monitoring class could not be found", e); //$NON-NLS-1$
        } catch (IllegalArgumentException e) {
            LOG.error("A argument which is passed to monitoring class is invalide", e); //$NON-NLS-1$
        } catch (InvocationTargetException e) {
            LOG.error("The method call of 'getAgent' failed, you have to implement the interface IMonitoring", e); //$NON-NLS-1$
        } catch (ClassNotFoundException e) {
            LOG.error("The monitoring class can not be found", e); //$NON-NLS-1$
        } catch (IOException e) {
            LOG.error("IOException while searching for the given bundle", e); //$NON-NLS-1$
        }     
        return agentString;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String[] getClasspathEntriesForBundleId(String bundleId) {
        Bundle mainBundle = getBundleForID(bundleId);
        if (mainBundle == null) {
            return new String[0];
        }
        ArrayList<Bundle> bundleAndFragmentList = new ArrayList<>();        
        bundleAndFragmentList.add(mainBundle);
        // Checks if the bundles are from us, so we only add fragments
        // from our bundles and not from others (like slf4j)
        if (StringUtils.containsIgnoreCase(bundleId, "jubula") //$NON-NLS-1$
                || StringUtils.containsIgnoreCase(bundleId, "guidancer")) { //$NON-NLS-1$
            bundleAndFragmentList.addAll(getFragmentsForBundleId(bundleId));
        }
        List<String> classpathEntries = new ArrayList<String>();
        for (Bundle bundle : bundleAndFragmentList) {
            classpathEntries.addAll(getPathforBundle(bundle));
        }
        return classpathEntries.toArray(new String[classpathEntries.size()]);
    }
    
    /**
     * Looks for the bundle with the given ID and the highest Version. This
     * search also includes non active bundles.
     * 
     * @param bundleId
     *            the bundle ID to look for
     * @return the bundle
     */
    public static Bundle getBundleForID(String bundleId) {
        Bundle bundle = Platform.getBundle(bundleId);
        if (bundle == null) {
            bundle = bundleLookupWithInactive(bundleId);
            if (bundle == null) {
                LOG.error("No bundle found for ID '" + bundleId + "'."); //$NON-NLS-1$//$NON-NLS-2$
            }
        }
        return bundle;
    }
    
    /**
     * Finds fragments for the given bundle in the running Platform. If no
     * active fragments are found, e.g. when jre version is below the minimum
     * BREE of a bundle, we are also adding non-active (installed) fragments.
     * 
     * @param rcBundleId the bundle name
     * @return the fragments which have been found
     */
    public static List<Bundle> getFragmentsForBundleId(String rcBundleId) {
        Bundle fragmentHost = getBundleForID(rcBundleId);
        ArrayList<Bundle> fragments = new ArrayList<Bundle>();
        
        Bundle[] f = Platform.getFragments(fragmentHost);
        if (f == null) {
            fragments.addAll(
                    fragmentLookupWithInactive(fragmentHost));
        } else {
            for (Bundle fragment : f) {
                fragments.add(fragment);
            }
        } 
        return fragments;
    }

    /**
     * Determines the file-system path to the jar for the given bundle and also
     * for nested jars within this jar
     * 
     * @param bundle the bundle to get the path for
     * @return A list containing the path to the jar, or several paths if the
     *         jar contained nested jars
     */
    public static List<String> getPathforBundle(Bundle bundle) {
        List<String> path = new ArrayList<String>();
        try {
            File bundleFile = FileLocator.getBundleFile(bundle);
            if (bundleFile.isFile()) {
                // bundle file is not a directory, so we assume it's a JAR file
                path.add(bundleFile.getAbsolutePath());   
                // since the classloader cannot handle nested JARs, we need to extract
                // all known nested JARs and add them to the classpath
                try {
                    // assuming that it's a JAR/ZIP file
                    File[] createdFiles = ZipUtil.unzipTempJars(bundleFile);
                    for (int i = 0; i < createdFiles.length; i++) {
                        path.add(createdFiles[i].
                                getAbsolutePath());
                    }
                } catch (IOException e) {
                    LOG.error("An error occurred while trying to extract nested JARs from " + bundle.getSymbolicName(), e); //$NON-NLS-1$
                }
            } else {
                Enumeration<URL> e = bundle.findEntries(
                        "/", "*.jar", true); //$NON-NLS-1$//$NON-NLS-2$
                if (e != null) {
                    while (e.hasMoreElements()) {
                        URL jarUrl = e.nextElement();
                        File jarFile = 
                            new File(bundleFile + jarUrl.getFile());
                        if (!isJarFileWithManifestAttr(
                                jarFile, SOURCE_BUNDLE_MANIFEST_ATTR)) {
                            path.add(jarFile.getAbsolutePath());
                        }
                    }
                }
            }
        } catch (IOException ioe) {
            LOG.error("Bundle with ID '" + bundle.getSymbolicName() + "' could not be resolved to a file.", ioe); //$NON-NLS-1$//$NON-NLS-2$
        }
        return path;
    }
    
    /**
     * 
     * @param file The file to check.
     * @param manifestAttr The name of the Manifest Attribute to check for.
     * @return <code>true</code> iff all of the following statements apply:<ul>
     *         <li><code>file</code> is a valid, existing JAR file</li>
     *         <li><code>file</code> has a JAR Manifest</li>
     *         <li><code>file</code>'s JAR Manifest contains an Attribute 
     *             named <code>manifestAttr</code></li>
     *         <li>no error occurs while performing the above checks</li>
     *         </ul>
     */
    private static boolean isJarFileWithManifestAttr(
            File file, 
            String manifestAttr) {

        try {
            JarFile jarFile = new JarFile(file);
            try {
                Manifest manifest = jarFile.getManifest();
                if (manifest != null) {
                    return manifest.getMainAttributes().containsKey(
                            new Attributes.Name(manifestAttr));
                }
            } catch (IOException ioe) {
                LOG.error("Error while reading JAR file.", ioe); //$NON-NLS-1$
            } finally {
                try {
                    jarFile.close();
                } catch (IOException ioe) {
                    LOG.error("Error while closing JAR file.", ioe); //$NON-NLS-1$
                }
            }
        } catch (IOException ioe) {
            LOG.error("Error while opening JAR file.", ioe); //$NON-NLS-1$
        } catch (SecurityException se) {
            LOG.error("Error while opening JAR file.", se); //$NON-NLS-1$
        }

        return false;
    }
    
    /**
     * Looks for the fragments which belong to the given Bundle. This search 
     * also includes non active bundles.
     * @param mainBundle the bundle to find the fragments for
     * @return the list with the fragments that have been found
     */
    private static List<Bundle> fragmentLookupWithInactive(Bundle mainBundle) {
        Bundle[] bundles = EclipseStarter.getSystemBundleContext().
                getBundles();
        List<Bundle> fragments = new ArrayList<Bundle>();
        for (Bundle bundle : bundles) {
            String fragmentHost = bundle.getHeaders().get(Constants.
                    FRAGMENT_HOST);
            if (fragmentHost != null) {
                if (fragmentHost.contains(StringConstants.SEMICOLON)) {
                    fragmentHost = fragmentHost.split(
                            StringConstants.SEMICOLON)[0];
                }
                if (fragmentHost.equals(mainBundle.getSymbolicName())) {
                    for (Bundle fragment : fragments) {
                        if (fragment.getSymbolicName().equals(
                                bundle.getSymbolicName())
                                && bundle.getVersion().compareTo(
                                        fragment.getVersion()) > 0) {
                            fragments.remove(fragment);
                        }
                    }
                    fragments.add(bundle);
                }
            }
        }
        return fragments;
    }

    /**
     * Looks for the bundle with the given ID and the highest Version. This
     * search also includes non active bundles.
     * 
     * @param bundleId
     *            the bundle ID to look for
     * @return the bundle
     */
    private static Bundle bundleLookupWithInactive(String bundleId) {
        BundleContext systemBundleContext = EclipseStarter
                .getSystemBundleContext();
        Bundle result = null;
        if (systemBundleContext != null) {
            Bundle[] bundles = systemBundleContext.getBundles();
            Version currVersion = Version.emptyVersion;
            for (Bundle bundle : bundles) {
                if (bundle.getSymbolicName().equals(bundleId)
                        && bundle.getVersion().compareTo(currVersion) > 0) {
                    result = bundle;
                    currVersion = bundle.getVersion();
                }
            }
        } else {
            LOG.warn("systemBundleContext is null - skipping bundleLookupWithInactive()"); //$NON-NLS-1$
        }
        return result;
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getFragmentPathforBundleID(String rcBundleID) {
        Map<String, String> fragmentMap = new HashMap<>();
        List<Bundle> fragments = new ArrayList<Bundle>();
        fragments = getFragmentsForBundleId(rcBundleID);

        for (Bundle bundle : fragments) {
            StringBuilder pathBuilder = new StringBuilder();
            for (String entry : getPathforBundle(bundle)) {
                pathBuilder.append(entry).append(
                        IStartAut.PATH_SEPARATOR);
            }
            if (pathBuilder.length() > 0) {
                fragmentMap.put(pathBuilder.substring(
                        0,
                        pathBuilder.lastIndexOf
                        (IStartAut.PATH_SEPARATOR)),
                        bundle.getHeaders()
                        .get(Constants.BUNDLE_NAME));
            }
        }
        return fragmentMap;
    }
}
