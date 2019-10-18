/*******************************************************************************
 * Copyright (c) 2004, 2010, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.common.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.commons.lang.LocaleUtils;
import org.eclipse.jubula.autagent.common.monitoring.IMonitoring;
import org.eclipse.jubula.autagent.common.utils.AutStartHelperRegister;
import org.eclipse.jubula.autagent.common.utils.IAUTStartHelper;
import org.eclipse.jubula.communication.internal.message.StartAUTServerStateMessage;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.jarutils.MainClassLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created Jul 9, 2007
 */
public abstract class AbstractStartJavaAut extends AbstractStartToolkitAut {
    /**
     * <code>JAVA_OPTIONS_INTRO</code>
     */
    protected static final String JAVA_OPTIONS_INTRO = "_JAVA_OPTIONS="; //$NON-NLS-1$

    /**
     * <code>JAVA_LANGUAGE_PROPERTY</code>
     */
    private static final String JAVA_LANGUAGE_PROPERTY = "-Duser.language="; //$NON-NLS-1$

    /**
     * <code>JAVA_COUNTRY_PROPERTY</code>
     */
    private static final String JAVA_COUNTRY_PROPERTY = "-Duser.country="; //$NON-NLS-1$

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(AbstractStartJavaAut.class);
     
    
    /**
     * {@inheritDoc}
     */
    protected String createBaseCmd(Map<String, String> parameters) 
        throws IOException {
        String executableFileName = parameters.get(
                AutConfigConstants.EXECUTABLE);
        if (executableFileName != null && executableFileName.length() > 0) {
            // Use the given executable, prepending the working directory if
            // the filename is relative
            File exe = new File(executableFileName);
            if (!exe.isAbsolute()) {
                exe = new File(
                    parameters.get(AutConfigConstants.WORKING_DIR), 
                    executableFileName);
            }
            if (exe.isFile() && exe.exists()) {
                return exe.getCanonicalPath();
            }
            // else
            String errorMsg = 
                executableFileName 
                + " does not point to a valid executable."; //$NON-NLS-1$
            LOG.warn(errorMsg);
            return executableFileName;
        }
    
        // Use java if no executable file is defined
        String java = StringConstants.EMPTY;
        String jre = parameters.get(AutConfigConstants.JRE_BINARY);
        if (jre == null) {
            jre = StringConstants.EMPTY;
        }
        File jreFile = new File(jre);
        if (jre.length() == 0) {
            java = "java"; //$NON-NLS-1$
        } else {
            if (!jreFile.isAbsolute()) {
                jreFile = new File(getWorkingDir(parameters), jre);
            }
            if (jreFile.isFile() && jreFile.exists()) {
                java = jreFile.getCanonicalPath();
            } else {
                String errorMsg = 
                    jreFile + " does not point to a valid JRE executable."; //$NON-NLS-1$
                LOG.error(errorMsg);
                throw new FileNotFoundException(errorMsg);
            }
        }
        return java; 
    }

    /**
     * Determines the main class for the AUT. <br>
     * If a main class was transmitted, use it. Otherwise scan the jar.
     * 
     * @param parameters The parameters for starting the AUT.
     * @return the main class or null if no main class was found. In this case
     *         m_errorMesssage contains a detailed message to send back.
     */
    protected String getAUTMainClass(Map parameters) {
        final String jarFile = (String)parameters.get(
                AutConfigConstants.JAR_FILE);
        String mainClass = getMainClassFromManifest(parameters);
        if (mainClass != null) {
            return mainClass;
        }
        if (LOG.isInfoEnabled()) {
            LOG.info("neither main class transmitted nor found in the manifest, " //$NON-NLS-1$
                    + "searching in jar: '" //$NON-NLS-1$
                    + String.valueOf(jarFile) + "'"); //$NON-NLS-1$
        }
        if (jarFile != null && jarFile.length() > 0) {
            try {
                List mains = MainClassLocator.getMainClass(new File(jarFile));
                if (mains.size() == 0) {
                    String message = "no main class found in '" //$NON-NLS-1$
                            + jarFile + "'"; //$NON-NLS-1$
                    LOG.error(message);
                    setErrorMessage(new StartAUTServerStateMessage(
                        AUTStartResponse.AUT_MAIN_NOT_FOUND_IN_JAR,
                        message));
                    return null;
                }
                if (mains.size() != 1) {
                    // the jar must contain exact one main class
                    // HERE send back a list of main classes
                    String message = "more than on main class found"; //$NON-NLS-1$
                    LOG.error(message);

                    setErrorMessage(new StartAUTServerStateMessage(
                        AUTStartResponse.AUT_MAIN_NOT_DISTINCT_IN_JAR,
                        message));
                    return null;
                }
                return ((String)mains.get(0)).replace('/', '.');
            } catch (NullPointerException npe) {
                // from new File()
                String message = "no jar given as classpath"; //$NON-NLS-1$ 
                LOG.error(message, npe);
                setErrorMessage(new StartAUTServerStateMessage(
                    AUTStartResponse.NO_JAR_AS_CLASSPATH, message));
                return null;
            } catch (IOException ioe) {
                String message = "scanning '" //$NON-NLS-1$
                        + String.valueOf(jarFile) + "' for main class failed"; //$NON-NLS-1$
                LOG.error(message, ioe);

                setErrorMessage(new StartAUTServerStateMessage(
                    AUTStartResponse.SCANNING_JAR_FAILED, message));
                return null;
            }
        }
        return null;
    }

    /**
     * Gets the main Class of the AUT jar
     * 
     * @param parameters The parameters for starting the AUT.
     * @return the main class
     */
    protected String getMainClassFromManifest(Map parameters) {
        String jarFile = createAbsoluteJarPath(parameters);
        return getAttributeFromManifest("main-class", jarFile); //$NON-NLS-1$
    }
    
    /**
     * @param attributeName the attribute name in the manifest
     * @param jarFile the path and name of the jar file to examine
     * @return the String value of the specified attribute name, or null if
     *         not found.
     */
    protected String getAttributeFromManifest(
        String attributeName, String jarFile) {
        
        if (jarFile == null || jarFile.length() < 1) {
            return null;
        }
        String attribute = null;
        try (JarFile jar = new JarFile(jarFile)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null) {
                attribute = manifest.getMainAttributes().getValue(
                        attributeName);
            }
        } catch (FileNotFoundException e) {
            LOG.error("File not found: " + jarFile, e); //$NON-NLS-1$
        } catch (IOException e) {
            LOG.error("Error reading jar file: " + jarFile, e); //$NON-NLS-1$
        }
        return attribute;
    }


    /**
     * 
     * @param parameters The parameters for starting the AUT.
     * @return the absolute path to the AUT jar file or null.
     */
    protected String createAbsoluteJarPath(Map parameters) {
        File workingDir = getWorkingDir(parameters);
        String jarPath = (String)parameters.get(AutConfigConstants.JAR_FILE);
        if (jarPath != null && jarPath.length() > 0) {
            if (workingDir != null) {
                File jarFile = new File(jarPath);
                if (!jarFile.isAbsolute()) {
                    jarPath = workingDir + FILE_SEPARATOR + jarPath;
                }
            }
        }
        return jarPath;
    }
    
    /**
     * @return the name of the main class for the AUT server.
     */
    protected abstract String getServerClassName();
    
    /**
     * @param cmds The command List. May <b>not</b> be <code>null</code>.
     * @param locale The <code>Locale</code> for the AUT. 
     *               May be <code>null</code> if no locale was specified.
     */
    protected void addLocale(List<String> cmds, Locale locale) {
        if (locale != null) {
            String country = locale.getCountry();
            if (country != null && country.length() > 0) {
                cmds.add(JAVA_COUNTRY_PROPERTY + country);
            }
            String language = locale.getLanguage();
            if (language != null && language.length() > 0) {
                cmds.add(JAVA_LANGUAGE_PROPERTY + language);
            }
        }
    }

    /**
     * Gets the classPath from the manifest of the given jar.
     * 
     * @param parameters The parameters for starting the AUT.
     * @return the classpath separated with OS-specific path separators 
     * or an empty String if the given jar is null or if there is no manifest
     * in the jar.
     */
    protected String getClassPathFromManifest(Map parameters) {
        String jarFile = createAbsoluteJarPath(parameters);
        String classPath = getAttributeFromManifest("class-path", jarFile); //$NON-NLS-1$
        if (classPath == null) {
            return StringConstants.EMPTY;
        }
        classPath = classPath.trim();
        return classPath.replace(' ', PATH_SEPARATOR.charAt(0));
    }

    /**
     * @param parameters The parameters for starting the AUT.
     * @return <code>true</code> if the AUT is being started via an executable
     *         file or script. Otherwise, <code>false</code>.
     */
    protected boolean isRunningFromExecutable(Map parameters) {
        return parameters.containsKey(AutConfigConstants.EXECUTABLE);
    }
    
    /**
     * Sets -javaagent and JRE arguments as SUN environment variable.
     * @param parameters The parameters for starting the AUT
     * @return the _JAVA_OPTIONS environment variable including -javaagent
     * and jre arguments
     */
    protected String setJavaOptions(Map<String, String> parameters) {
        StringBuffer sb = new StringBuffer();
        if (isRunningFromExecutable(parameters)) {
            Locale locale = LocaleUtils.toLocale(parameters
                .get(AutConfigConstants.AUT_LOCALE));
            
            // set agent and locals
            sb.append(JAVA_OPTIONS_INTRO);
            if (org.eclipse.jubula.tools.internal.utils.MonitoringUtil
                    .shouldAndCanRunWithMonitoring(parameters)) {
                String monAgent = getMonitoringAgent(parameters);
                if (monAgent != null) {
                    sb.append(monAgent).append(StringConstants.SPACE);
                }
            }                 
            sb.append(StringConstants.QUOTE)
                .append("-javaagent:") //$NON-NLS-1$
                .append(getAbsoluteAgentJarPath())
                .append(StringConstants.QUOTE);
               
            if (locale != null) {
                sb.append(StringConstants.SPACE)
                    .append(JAVA_COUNTRY_PROPERTY)
                    .append(locale.getCountry());
                sb.append(StringConstants.SPACE)
                    .append(JAVA_LANGUAGE_PROPERTY)
                    .append(locale.getLanguage());
            }
        } else {
            if (org.eclipse.jubula.tools.internal.utils.MonitoringUtil
                    .shouldAndCanRunWithMonitoring(parameters)) {
                String monAgent = getMonitoringAgent(parameters);
                if (monAgent != null) {
                    sb.append(JAVA_OPTIONS_INTRO).append(monAgent);
                }
            }
        }

        return sb.toString();
    }
    
    /**
     * Gets the absolute path of the org.eclipse.jubula.rc.common.agent.jar file.
     * @return the absolute path
     */
    protected String getAbsoluteAgentJarPath() {
        return AbstractStartToolkitAut.getClasspathForBundleId(
                CommandConstants.RC_COMMON_AGENT_BUNDLE_ID);
    }
    
    /**
     * @return the AUTAgent installation directory
     */
    public static File getInstallDir() {
        IAUTStartHelper helper =
                AutStartHelperRegister.INSTANCE.getAutStartHelper();
        if (helper == null) {
            LOG.error("No AUTStartHelper registered"); //$NON-NLS-1$
            return null;
        }
        return helper.getInstallationDirectory();
    }
    
    /**
     * This method will load the class which implements the {@link IMonitoring} 
     * interface, and will invoke the "getAgent" method. 
     * @param parameters The AutConfigMap
     * @return agentString The agent String like -javaagent:myagent.jar
     * or null if the monitoring agent String couldn't be generated
     */        
    protected String getMonitoringAgent(Map<String, String> parameters) { 
        IAUTStartHelper helper =
                AutStartHelperRegister.INSTANCE.getAutStartHelper();
        if (helper == null) {
            LOG.error("No AUTStartHelper registered"); //$NON-NLS-1$
            return null;
        }
        return helper.getMonitoringAgent(parameters);
    }
    
}