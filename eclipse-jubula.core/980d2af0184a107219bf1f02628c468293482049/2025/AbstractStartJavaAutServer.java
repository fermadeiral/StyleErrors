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
package org.eclipse.jubula.autagent.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang.LocaleUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Upperclass for the JavaFX and SwingStart Server Command classes.
 * This was created because the AbstractStartJavaAut already has more
 * than 400 lines of code and it is also extended by StartRcpAutServerCommand.
 * This class implements all methods that are the same for the 
 * JavaFX and Swing classes.
 */
public abstract class AbstractStartJavaAutServer extends AbstractStartJavaAut {

    /** the logger */
    private static final  Logger LOG = 
        LoggerFactory.getLogger(AbstractStartJavaAutServer.class);
    
    /** separates the environment variables */
    private static final String ENV_SEPARATOR = "\n"; //$NON-NLS-1$
    
    /**
     * @return the Id of the specific rc bundle
     */
    protected abstract String getRcBundleClassPath();

    /**
     * Sets -javaagent, JRE arguments and the arguments for 
     * the AutServer as environment variables.
     * @param parameters The parameters for starting the AUT
     * @param autServerClasspath The classpath of the AUT Server
     */
    protected void setEnv(Map<String, String> parameters, 
        String autServerClasspath) {
        String env = parameters.get(AutConfigConstants.ENVIRONMENT);
        if (env == null) {
            env = StringConstants.EMPTY;
        } else {
            env += ENV_SEPARATOR;
        }
        env += setJavaOptions(parameters);
        if (isRunningFromExecutable(parameters)) {
            // agent arguments
            String serverPort = "null"; //$NON-NLS-1$
            final Communicator autCommunicator = AutStarter.getInstance()
                .getAutCommunicator();
            if (autCommunicator != null) {
                serverPort = String.valueOf(autCommunicator.getLocalPort());
            }
        
            env += ENV_SEPARATOR + Constants.AUT_SERVER_PORT + "=" + serverPort; ////$NON-NLS-1$
            env += ENV_SEPARATOR + Constants.AUT_SERVER_CLASSPATH + "=" + autServerClasspath; //$NON-NLS-1$
            env += ENV_SEPARATOR + Constants.AUT_SERVER_NAME + "=" + getServerClassName(); //$NON-NLS-1$
            env += ENV_SEPARATOR + Constants.AUT_JUB_INSTALL_DIRECTORY + "=" + getInstallDir(); //$NON-NLS-1$
            // AUT-Agent variables
            env += ENV_SEPARATOR + AutConfigConstants.AUT_AGENT_HOST + "=" + parameters.get(AutConfigConstants.AUT_AGENT_HOST); //$NON-NLS-1$
            env += ENV_SEPARATOR + AutConfigConstants.AUT_AGENT_PORT + "=" + parameters.get(AutConfigConstants.AUT_AGENT_PORT); //$NON-NLS-1$
            env += ENV_SEPARATOR + AutConfigConstants.AUT_NAME + "=" + parameters.get(AutConfigConstants.AUT_NAME); //$NON-NLS-1$
        }
        // create environment
        parameters.put(AutConfigConstants.ENVIRONMENT, env);
    }
    
    /**
     * 
     * @return an array of Strings representing the launch classpath.
     */
    public String[] getLaunchClasspath() {
        StringBuffer autServerClasspath = new StringBuffer();
        createServerClasspath(autServerClasspath);
        autServerClasspath.append(PATH_SEPARATOR)
            .append(getRcBundleClassPath());
        return autServerClasspath.toString().split(PATH_SEPARATOR);
    }

    /**
     * Creates the Server classpath.
     * @param serverClasspath the server classpath
     */
    protected void createServerClasspath(StringBuffer serverClasspath) {

        String [] bundlesToAddToClasspath = {
            CommandConstants.TOOLS_BUNDLE_ID, 
            CommandConstants.TOOLS_EXEC_BUNDLE_ID,
            CommandConstants.COMMUNICATION_BUNDLE_ID, 
            CommandConstants.RC_COMMON_BUNDLE_ID,
            CommandConstants.TOOLKIT_API_BUNDLE_ID,
            CommandConstants.SLF4J_JCL_BUNDLE_ID,
            CommandConstants.SLF4J_API_BUNDLE_ID,
            CommandConstants.COMMONS_LANG_BUNDLE_ID,
            CommandConstants.APACHE_IO_BUNDLE_ID,
            CommandConstants.APACHE_EXEC_BUNDLE_ID,
            CommandConstants.APACHE_ORO_BUNDLE_ID,
            CommandConstants.COMMONS_BEAN_UTILS_BUNDLE_ID,
            CommandConstants.COMMONS_COLLECTIONS_BUNDLE_ID,
            CommandConstants.LOGBACK_CLASSIC_BUNDLE_ID,
            CommandConstants.LOGBACK_CORE_BUNDLE_ID,
            CommandConstants.LOGBACK_SLF4J_BUNDLE_ID
        };
        
        for (String bundleId : bundlesToAddToClasspath) {
            serverClasspath.append(
                    AbstractStartToolkitAut.getClasspathForBundleId(bundleId));
            serverClasspath.append(PATH_SEPARATOR);
        }
        
        
        serverClasspath.append(getAbsExtImplClassesPath());       
        if (LOG.isDebugEnabled()) {
            LOG.debug("serverClasspath" + serverClasspath); //$NON-NLS-1$
        }
    }
    
    /**
     * Gets the absolute path of the location of the external ImplClasses.
     * @return the absolute path
     */
    protected String getAbsExtImplClassesPath() {
        
        final File implDir = new File(CommandConstants.EXT_IMPLCLASSES_PATH);
        final StringBuffer paths = new StringBuffer(implDir.getAbsolutePath());
        final File[] jars = implDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar"); //$NON-NLS-1$
            }
        });
        
        if (jars != null) {
            final int maxIdx = jars.length;
            for (int i = 0; i < maxIdx; i++) {
                File f = jars[i];
                paths.append(PATH_SEPARATOR);
                paths.append(f.getAbsolutePath());
            }
        }
        return paths.toString();
    }
    
    /**
     * Adds elements to the given cmds List.
     * @param autServerClasspath the server classpath
     * @param cmds the 1st part of the cmd array
     * @param parameters The parameters for starting the AUT.
     */
    protected void createAutServerClasspath(StringBuffer autServerClasspath, 
        List<String> cmds, Map<String, String> parameters) {
        
        final Communicator autCommunicator = AutStarter.getInstance()
            .getAutCommunicator();
        if (autCommunicator != null) {
            cmds.add(String.valueOf(autCommunicator.getLocalPort()));
        } else {
            cmds.add("null"); //$NON-NLS-1$
        }
        
        String autMain = getAUTMainClass(parameters);
        if (autMain == null) {
            return;
        }
        cmds.add(autMain);
        autServerClasspath.append(PATH_SEPARATOR)
            .append(getRcBundleClassPath());
        cmds.add(autServerClasspath.toString());
        cmds.add(getServerClassName());
    }

    /**
     * 
     * @return the server base path including resources path
     */
    protected String createServerBasePath() {
        return AbstractStartToolkitAut.getClasspathForBundleId(
                CommandConstants.RC_COMMON_BUNDLE_ID);
    }
    
    /**
     * @param parameters
     *            The parameters for starting the AUT.
     * @return The arguments for the AUT that were found in the given
     *         parameters.
     */
    protected List<String> createAutArguments(Map<String, String> parameters) {
        List<String> argsList = new Vector<String>();
        final String joinedAutRunArgs = parameters.get(
            AutConfigConstants.AUT_RUN_AUT_ARGUMENTS);
        if (joinedAutRunArgs != null) {
            return Arrays.asList(StringUtils.split(joinedAutRunArgs,
                AutConfigConstants.AUT_RUN_AUT_ARGUMENTS_SEPARATOR_CHAR));
        }
        processAutRunArguments(parameters, argsList);

        return argsList;
    }

    /**
     * Process the aut arguments for creating aut arguments
     * @param parameters argument parameter map
     * @param argsList argument lists, which contains the parameters
     */
    private void processAutRunArguments(Map<String, String> parameters,
            List<String> argsList) {
        String autArguments = parameters.get(AutConfigConstants.AUT_ARGUMENTS);

        if (autArguments == null) {
            autArguments = StringConstants.EMPTY;
        }

        String delimiterString = "%_"; //$NON-NLS-1$

        String[] quotedParts = StringUtils.substringsBetween(autArguments, "\"", //$NON-NLS-1$
                "\""); //$NON-NLS-1$
        if (quotedParts != null) {
            for (String quotedPart : quotedParts) {
                String escapedQuotePart = new String(quotedPart);
                escapedQuotePart = escapedQuotePart.replaceAll("\\s+", //$NON-NLS-1$
                        delimiterString);
                autArguments = autArguments.replace(quotedPart,
                        escapedQuotePart);
            }
        }
        StringTokenizer args = new StringTokenizer(autArguments,
            WHITESPACE_DELIMITER);
        while (args.hasMoreTokens()) {
            String arg = args.nextToken();
            if (arg.contains(delimiterString)) {
                arg = arg.replaceAll(delimiterString, " ").replaceAll("\"", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            }
            
            argsList.add(arg);
        }
    }
    
    /**
     * @param cmds the commands list
     * @param autServerClasspath the autServerClassPath to change
     * @param parameters The parameters for starting the AUT.
     */
    protected void createAutServerLauncherClasspath(List<String> cmds, 
            StringBuffer autServerClasspath, Map<String, String> parameters) {
        
        addBaseSettings(cmds, parameters);
        cmds.add("-classpath"); //$NON-NLS-1$
        StringBuffer autClassPath = createAutClasspath(parameters);
        String serverBasePath = createServerBasePath(); 
        cmds.add(autClassPath.append(PATH_SEPARATOR)
                    .append(serverBasePath).toString());
        // add classname of autLauncher
        cmds.add(CommandConstants.AUT_SERVER_LAUNCHER);
        // add autServerBase dirs to autServerClassPath
        autServerClasspath.append(PATH_SEPARATOR).append(serverBasePath);
    }
    
    
    /**
     * * Creates the AUT settings.
     * 
     * @param cmds
     *            the commands list
     * @param parameters
     *            The parameters for starting the AUT.
     */
    protected void addBaseSettings(List<String> cmds,
        Map<String, String> parameters) {
        // add locale
        addLocale(cmds, LocaleUtils.toLocale(parameters
            .get(AutConfigConstants.AUT_LOCALE)));

        // add JRE params
        final String jreParams = parameters
            .get(AutConfigConstants.JRE_PARAMETER);
        if (jreParams != null && jreParams.length() > 0) {
            StringTokenizer tok = new StringTokenizer(jreParams,
                WHITESPACE_DELIMITER);
            while (tok.hasMoreTokens()) {
                cmds.add(tok.nextToken());
            }
        }

        // add debug options (if necessary)
        addDebugParams(cmds, false);
        // add -Duser.dir and workingDir here
    }
    
    
    /**
     * Creates the AUT classpath. 
     * @param parameters The parameters for starting the AUT.
     * @return The classpath of the AUT.
     */
    protected StringBuffer createAutClasspath(Map parameters) {
        // Add AUT classpath
        StringBuffer autClassPath = new StringBuffer();
        String jarFile = (String)parameters.get(AutConfigConstants.JAR_FILE);
        if (jarFile == null) {
            jarFile = StringConstants.EMPTY;
        }
        String manifestClassPath = getClassPathFromManifest(parameters);
        if (manifestClassPath.length() > 0) {
            autClassPath.append(manifestClassPath).append(PATH_SEPARATOR);
        }
        autClassPath.append(jarFile);
        if (jarFile != null && jarFile.length() > 0) {
            autClassPath.append(PATH_SEPARATOR);
        }
        return autClassPath;
    }
}
