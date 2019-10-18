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
package org.eclipse.jubula.autagent.common.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.jubula.autagent.common.AutStarter;
import org.eclipse.jubula.autagent.common.monitoring.MonitoringDataStore;
import org.eclipse.jubula.autagent.common.monitoring.MonitoringUtil;
import org.eclipse.jubula.autagent.common.utils.AutStartHelperRegister;
import org.eclipse.jubula.autagent.common.utils.IAUTStartHelper;
import org.eclipse.jubula.communication.internal.message.StartAUTServerStateMessage;
import org.eclipse.jubula.tools.internal.constants.AUTStartResponse;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.AutEnvironmentConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
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

    
    /** defines if eclipse directory is used instead of jar files */
    private static String developer =
            EnvironmentUtils.getProcessOrSystemProperty("JubulaRCDeveloperMode"); //$NON-NLS-1$
    
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
        IAUTStartHelper helper =
                AutStartHelperRegister.INSTANCE.getAutStartHelper();
        if (helper == null) {
            log.error("No AUTStartHelper registered "); //$NON-NLS-1$
            throw new IllegalStateException("No AUTStartHelper registered"); //$NON-NLS-1$
        }
        return helper.getClasspathEntriesForBundleId(bundleId);
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
     * 
     * @param classPath the array
     * @return a the class path in the same order as the array
     */
    protected static String createClassPath(String[] classPath) {
        StringBuilder pathBuilder = new StringBuilder();
        for (String entry : classPath) {
            String jarless = convertToDirectories(entry);
            if (jarless == null) {
                pathBuilder.append(entry).append(PATH_SEPARATOR);
            } else {
                pathBuilder.append(jarless);
            }
        }

        return pathBuilder.length() == 0 ? "" //$NON-NLS-1$
                : pathBuilder.substring(0,
                        pathBuilder.lastIndexOf(PATH_SEPARATOR));
    }

    /**
     * This is used for development only. The jar pathes are changed to use the
     * compiled classes from eclipse instead
     * 
     * @param entry the jar entry
     * @return the class folder from eclipse
     */
    private static String convertToDirectories(String entry) {
        if (developer == null) {
            return null;
        }
        if (entry.contains("target") && !entry.contains("agent") && entry.endsWith(".jar")) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            // the agentjar must be a jar, therefore ignore the entry
            // Developer mode uses the class files instead of the jar files
            // works at this moment only for the org.eclips.jubula rc jars and Linux
            String withoutJar = entry;
            int lastIndex = withoutJar.lastIndexOf(StringConstants.SLASH);
            String toTest =
                    withoutJar.substring(lastIndex, withoutJar.length());
            String jubulaBeginning = "org.eclipse.jubula"; //$NON-NLS-1$
            if (toTest.contains(jubulaBeginning)) {
                withoutJar = withoutJar.substring(0, lastIndex + 1);
                withoutJar = withoutJar.replace("/target/", "/bin/");  //$NON-NLS-1$//$NON-NLS-2$
                return withoutJar + PATH_SEPARATOR;
            }
        }
        return null;
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

}