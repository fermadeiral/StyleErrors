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
package org.eclipse.jubula.autagent.commands;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.adaptor.EclipseStarter;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.monitoring.MonitoringDataStore;
import org.eclipse.jubula.autagent.monitoring.MonitoringUtil;
import org.eclipse.jubula.communication.internal.message.StartAUTServerStateMessage;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.AutEnvironmentConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.ZipUtil;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Jul 10, 2007
 *
 */
public abstract class AbstractStartToolkitAut implements IStartAut {
    
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(AbstractStartToolkitAut.class);

    /** 
     * the name of the bundle JAR Manifest Attribute that indicates that the
     * bundle is a source-bundle
     */
    private static final String SOURCE_BUNDLE_MANIFEST_ATTR = 
            "Eclipse-SourceBundle"; //$NON-NLS-1$
    
    /**
     * the message to send back if the command for starting the AUTServer could
     * not created
     */
    private StartAUTServerStateMessage m_errorMessage;

    /** true if executable file and -javaagent are set */
    private boolean m_isAgentSet = false;

    /**
     *
     * {@inheritDoc}
     */
    public StartAUTServerStateMessage startAut(Map<String, String> parameters)
        throws IOException {
        StartAUTServerStateMessage envCheckMsg = validateEnvironment();
        AutIdentifier autId = new AutIdentifier(parameters.get(
                AutConfigConstants.AUT_ID));
        if (envCheckMsg == null) {
            if (!MonitoringUtil.checkForDuplicateAutID(String.valueOf(autId))) {
                MonitoringDataStore cm = MonitoringDataStore.getInstance();
                cm.putConfigMap(autId.getID(), parameters);
            }
            File workingDir = getWorkingDir(parameters);
            String java = createBaseCmd(parameters);
            String[] cmdArray = createCmdArray(java, parameters);
            String[] envArray = createEnvArray(parameters, m_isAgentSet);
            // if no environment variables set
            if ((envArray == null) && log.isInfoEnabled()) {
                log.info("envArray: NULL"); //$NON-NLS-1$
            }
            if (log.isInfoEnabled()) {
                StringBuffer logMessage = new StringBuffer();
                for (int i = 0; i < cmdArray.length; i++) {
                    logMessage.append(cmdArray[i]
                        + IStartAut.WHITESPACE_DELIMITER);
                }
                log.info("starting AUT with command: " //$NON-NLS-1$
                    + logMessage.toString());
            }
            StartAUTServerStateMessage stateMessage = executeCommand(cmdArray,
                    envArray, workingDir, autId);
            stateMessage.setAutId(autId);
            return stateMessage;
        }
        envCheckMsg.setAutId(autId);
        return envCheckMsg;
    }

    /**
     * Validates the runtime environment. This method should be overridden
     * in subclasses which need a specific environment.
     * @return null if the environment is OK or a message with a specific
     * error code.
     */
    protected StartAUTServerStateMessage validateEnvironment() {
        return null;
    }

    /**
     *
     * @param parameters startup parameters for the AUT.
     * @return the working directory for the AUT, or <code>null</code> if no
     *         working directory was defined.
     */
    protected File getWorkingDir(Map parameters) {
        String autWorkDir = 
            (String)parameters.get(AutConfigConstants.WORKING_DIR);
        if (autWorkDir == null) {
            autWorkDir = StringConstants.EMPTY;
        }
        File workingDir = new File(autWorkDir);
        if (!workingDir.isDirectory() || !workingDir.exists()) {
            if (log.isInfoEnabled()) {
                log.info("Working dir: invalid"); //$NON-NLS-1$
            }
            workingDir = null;
        }

        return workingDir;
    }

    /**
     * Creates the environment variables for starting the AUTServer.
     * @param parameters startup parameters for the AUT.
     * @param isAgentSet true if executable file and agent are set.
     * @return the environment settings as array.
     */
    protected String[] createEnvArray(Map<String, String> parameters,
        boolean isAgentSet) {
        m_isAgentSet = isAgentSet;
        final String environment =
            parameters.get(AutConfigConstants.ENVIRONMENT);
        
        final boolean generate = Boolean.valueOf(parameters.get(
                AutConfigConstants.NAME_TECHNICAL_COMPONENTS));
        
        Properties oldProp = EnvironmentUtils.getProcessEnvironment();
        String[] newEnvArray = null;
        
        if (generate) {
            Properties generateProperty = new Properties();
            generateProperty.setProperty(
                        AutEnvironmentConstants.GENERATE_COMPONENT_NAMES, 
                    String.valueOf(generate));
            oldProp = EnvironmentUtils
                    .setEnvironment(oldProp, generateProperty);
            newEnvArray = EnvironmentUtils.propToStrArray(
                    oldProp, IStartAut.PROPERTY_DELIMITER);
        }
        
        if ((environment != null) && (environment.trim().length() != 0)) {
            String[] envArray = EnvironmentUtils.strToStrArray(environment, "\r\n"); //$NON-NLS-1$
            Properties newProp = EnvironmentUtils.strArrayToProp(
                    envArray, IStartAut.PROPERTY_DELIMITER);
            newProp = EnvironmentUtils.setEnvironment(oldProp, newProp);
            newEnvArray = EnvironmentUtils.propToStrArray(
                    newProp, IStartAut.PROPERTY_DELIMITER);
        }
        
        return newEnvArray;
    }

    /**
     *
     * @param parameters startup parameters for the AUT.
     * @return a <code>String</code> that represents a
     * call to an executable. Ex. "java" or "/opt/java1.6/java".
     */
    protected abstract String createBaseCmd(Map<String, String> parameters) 
        throws IOException;

    /**
     *
     * @param baseCmd The base command to execute. For example, "java".
     * @param parameters startup parameters for the AUT.
     * @return an <code>Array</code> of <code>String</code>s representing
     *         a command line.
     */
    protected abstract String[] createCmdArray(String baseCmd,
        Map<String, String> parameters);

    /**
     * Executes the given command in the given environment with the
     * given working directory.
     * @param cmdArray The command line to execute.
     * @param envArray The execution environment.
     * @param workingDir The working directory.
     * @param autId id of aut.
     * @return a <code>StartAutServerStateMessage</code> which either describes an error
     * condition or just tells the originator that the AUT was started correctly.
     */
    protected StartAUTServerStateMessage executeCommand(String [] cmdArray,
            String [] envArray, File workingDir, AutIdentifier autId)
                    throws IOException {

        final AutStarter autAgent = AutStarter.getInstance();
        Process process = Runtime.getRuntime().exec(cmdArray, envArray,
            workingDir);
        if (isErrorMessage()) {
            log.error("AbstractStartToolkitAut - executeCommand: " //$NON-NLS-1$
                    + getErrorMessage());
            return getErrorMessage();
        }
        if (!autAgent.watchAUT(process, m_isAgentSet, autId)) {
            process.destroy(); // new AUTServer could not be watched
            return createBusyMessage();
        }
        return new StartAUTServerStateMessage(AUTStartResponse.OK);
    }

    /**
     * Internal: generate a return message with the information about the problem. This
     * is used in other methods to propagate errors.
     *
     * @param errorMessage The errorMessage to store.
     */
    protected void setErrorMessage(StartAUTServerStateMessage errorMessage) {
        m_errorMessage = errorMessage;
    }

    /**
     * Internal: get a return message with the information about a problem. This
     * is used in other methods to propagate errors.
     *
     * @return Returns the errorMessage.
     */
    protected StartAUTServerStateMessage getErrorMessage() {
        if (m_errorMessage == null) {
            m_errorMessage = new StartAUTServerStateMessage(
                AUTStartResponse.ERROR, "Unexpected error, no detail available."); //$NON-NLS-1$
        }
        return m_errorMessage;
    }

    /**
     * Internal: checks whether there is currently an error message.
     *
     * @return <code>true</code> if an error has occurred and there is an
     *         error message available. Otherwise <code>false</code>.
     */
    protected boolean isErrorMessage() {
        return m_errorMessage != null;
    }

    /**
     * Creates a <code>StartAUTServerStateMessage</code> with an
     * <code>ERROR</code> state and a description that the server is already running.
     * This message will eventually be returned be <code>execute()</code>.
     *
     * @return a new <code>StartAUTServerStateMessage</code>
     */
    protected StartAUTServerStateMessage createBusyMessage() {
        return new StartAUTServerStateMessage(AUTStartResponse.ERROR,
            "AUTServer is already running"); //$NON-NLS-1$
    }

    /**
     * 
     * @param bundleId The ID of the bundle to search for classpath entries.
     * @return classpath entries contained within the bundle with the given ID.
     *         If
     *         <ul> 
     *           <li>the bundle cannot be resolved to a file, or</li> 
     *           <li>the bundle is not a JAR file and the bundle's directory contains no JAR files</li>
     *         </ul>
     *         an empty array will be returned.
     */
    private static String[] getClasspathEntriesForBundleId(String bundleId) {
        Bundle mainBundle = getBundleForID(bundleId);
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
                    log.error("An error occurred while trying to extract nested JARs from " + bundle.getSymbolicName(), e); //$NON-NLS-1$
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
            log.error("Bundle with ID '" + bundle.getSymbolicName() + "' could not be resolved to a file.", ioe); //$NON-NLS-1$//$NON-NLS-2$
        }
        return path;
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
            log.warn("systemBundleContext is null - skipping bundleLookupWithInactive()"); //$NON-NLS-1$
        }
        return result;
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
                log.error("No bundle found for ID '" + bundleId + "'."); //$NON-NLS-1$//$NON-NLS-2$
            }
        }
        return bundle;
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
                log.error("Error while reading JAR file.", ioe); //$NON-NLS-1$
            } finally {
                try {
                    jarFile.close();
                } catch (IOException ioe) {
                    log.error("Error while closing JAR file.", ioe); //$NON-NLS-1$
                }
            }
        } catch (IOException ioe) {
            log.error("Error while opening JAR file.", ioe); //$NON-NLS-1$
        } catch (SecurityException se) {
            log.error("Error while opening JAR file.", se); //$NON-NLS-1$
        }

        return false;
    }

    /**
     * 
     * @param bundleId The ID of the bundle to search for a classpath.
     * @return the classpath contained within the bundle with the given ID.
     *         If
     *         <ul> 
     *           <li>the bundle cannot be resolved to a file, or</li> 
     *           <li>the bundle is not a JAR file and the bundle's directory contains no JAR files</li>
     *         </ul>
     *         an empty String will be returned.
     */
    public static String getClasspathForBundleId(String bundleId) {
        String[] classPath = getClasspathEntriesForBundleId(bundleId);
        return createClassPath(classPath);
    }

    /**
     * Creates a class path sting for a given string array
     * @param classPath the array
     * @return a the class path in the same order as the array
     */
    protected static String createClassPath(String[] classPath) {
        StringBuilder pathBuilder = new StringBuilder();
        for (String entry : classPath) {
            pathBuilder.append(entry).append(PATH_SEPARATOR);
        }
        
        return pathBuilder.length() == 0 ? "" //$NON-NLS-1$
                : pathBuilder.substring(0,
                        pathBuilder.lastIndexOf(PATH_SEPARATOR));
    }
 
    /**
     * Adds the parameters for remote debugging to the given command List
     * 
     * @param cmds
     *            the command List
     * @param isDirectExec
     *            true if the AUT is started by exec and not by a JVM
     */
    protected void addDebugParams(List<String> cmds, boolean isDirectExec) {
        final String rcDebug = IStartAut.RC_DEBUG;
        if (rcDebug != null) {
            if (isDirectExec) {
                cmds.add("-vmargs -Xms128m -Xmx512m"); //$NON-NLS-1$
            }
            cmds.add("-Xdebug"); //$NON-NLS-1$
            cmds.add("-Xnoagent"); //$NON-NLS-1$
            cmds.add("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=" + rcDebug); //$NON-NLS-1$
            cmds.add("-Djava.compiler=NONE"); //$NON-NLS-1$
        }
    }
    
    /**
     * Return the bundle id of the RC bundle
     * @return the bundle name
     */
    public abstract String getRcBundleId();

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
}