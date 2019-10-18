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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.commons.lang.LocaleUtils;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.MonitoringUtil;


/**
 * @author BREDEX GmbH
 * @created 04.09.2007
 * 
 */
public class StartRcpAutServerCommand extends AbstractStartJavaAut {

    /** 
     * -nl : 
     * the argument that defines the locale for the RCP application. 
     */
    private static final String NL = "-nl"; //$NON-NLS-1$

    /**
     * @param pathSeparator the defined pathSeparator
     * @param cmds the cmd list
     * @param parameters The startup parameters for the AUT.
     */
    private void createDirectAutJavaCallParameter(final String pathSeparator, 
        List<String> cmds, Map<String, String> parameters) { 
        
        StringBuffer autClassPath = new StringBuffer();
        final String autJar = parameters.get(
                AutConfigConstants.JAR_FILE);
        String manifestClassPath = getClassPathFromManifest(parameters);
        if (manifestClassPath.length() > 0) {
            autClassPath.append(manifestClassPath).append(pathSeparator);
        }
        if (autClassPath.toString() != null
                && !StringConstants.EMPTY.equals(autClassPath.toString())) {
            cmds.add("-classpath"); //$NON-NLS-1$
            cmds.add(autClassPath.toString());
        }
        cmds.add("-jar"); //$NON-NLS-1$
        cmds.add(autJar);
        final String autArgs = 
            parameters.get(AutConfigConstants.AUT_ARGUMENTS);
        if (autArgs != null) {
            StringTokenizer args = new StringTokenizer(autArgs, 
                WHITESPACE_DELIMITER);
            while (args.hasMoreTokens()) {
                String arg = args.nextToken();
                cmds.add(arg);
            }
        }
    }

    /**
     * @param cmds the cmd list
     * @param parameters The startup parameters for the AUT.
     */
    private void createDirectAutExeCallParameter(List<String> cmds, 
        Map<String, String> parameters) { 
        final String autArgs = parameters.get(AutConfigConstants.AUT_ARGUMENTS);
        if (autArgs != null) {
            StringTokenizer args = new StringTokenizer(autArgs, 
                WHITESPACE_DELIMITER);
            while (args.hasMoreTokens()) {
                String arg = args.nextToken();
                cmds.add(arg);
            }
        }
    }

    /**
     * 
     * @param parameters
     *            The parameters for starting the AUT.
     * @return a command line array as list with locale, JRE-parameters and
     *         optional debug parameters
     */
    private List<String> createDirectAutJavaCall(
        final Map<String, String> parameters) {
        // create exec string array
        List<String> cmds = new Vector<String>();
        // add locale
        addLocale(cmds, LocaleUtils.toLocale(
            parameters.get(AutConfigConstants.AUT_LOCALE)));
        // add JRE parameter
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
        addDebugParams(cmds, true);
        return cmds;
    }

    /**
     * {@inheritDoc}
     */
    protected String getServerClassName() {
        return CommandConstants.AUT_SWT_SERVER;
    }

    /**
     * {@inheritDoc}
     */
    protected String[] createCmdArray(String baseCmd, 
        Map<String, String> parameters) {
        
        List<String> cmds;
        
        if (!isRunningFromExecutable(parameters)) {
            // Start using java
            cmds = createDirectAutJavaCall(parameters);
            cmds.add(0, baseCmd);
                    
            createDirectAutJavaCallParameter(PATH_SEPARATOR, cmds, parameters);
            addLocale(cmds, LocaleUtils.toLocale(parameters
                .get(AutConfigConstants.AUT_LOCALE)));
        } else {
            // Start using executable file
            cmds = new Vector<String>();

            cmds.add(0, baseCmd);
                        
            createDirectAutExeCallParameter(cmds, parameters);
            // add locale
            // Note: This overrides the -nl defined in the <app>.ini file, if
            // any. It will not override a -nl from the command line.
            if (!cmds.contains(NL)) {
                Locale locale = LocaleUtils.toLocale(parameters
                    .get(AutConfigConstants.AUT_LOCALE));
                if (locale != null) {
                    if ((locale.getCountry() != null 
                        && locale.getCountry().length() > 0)
                        || (locale.getLanguage() != null 
                        && locale.getLanguage().length() > 0)) {

                        // Add -nl argument if country and/or language is
                        // available.
                        cmds.add(1, NL);
                        cmds.add(2, locale.toString());
                    }
                }
            }
            addDebugParams(cmds, true);
        }

        String[] cmdArray = cmds.toArray(new String[cmds.size()]);
        return cmdArray;
    }

    /**
     * {@inheritDoc}
     */
    protected String[] createEnvArray(Map<String, String> parameters, 
        boolean isAgentSet) {
        
        String [] envArray = super.createEnvArray(parameters, isAgentSet);
        if (envArray == null) {
            envArray = EnvironmentUtils.propToStrArray(
                    EnvironmentUtils.getProcessEnvironment(), 
                    IStartAut.PROPERTY_DELIMITER);
        }
        Vector<String> envList = new Vector<String>(Arrays.asList(envArray));
        envList.addAll(getConnectionProperties(parameters,
            StartSwtAutServerCommand.ENV_VALUE_SEP));
        
        if (MonitoringUtil.shouldAndCanRunWithMonitoring(parameters)) {
            String monAgent = this.getMonitoringAgent(parameters);
            if (monAgent != null) {
                StringBuffer sb = new StringBuffer();
                sb.append(JAVA_OPTIONS_INTRO);
                sb.append(monAgent);
                envList.add(sb.toString());   
                envArray = super.createEnvArray(parameters, true);
            }
        }        
        envArray = envList.toArray(new String [envList.size()]);
      
        return envArray;
    }

    /**
     * 
     * @param parameters
     *            The AUT Configuration parameters.
     * @param valueSeparator
     *            The string to use to separate property names from property
     *            values.
     * @return the list of properties.
     */
    private List<String> getConnectionProperties(
        Map<String, String> parameters, String valueSeparator) {

        List<String> props = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();

        sb = new StringBuffer();
        sb.append(AutConfigConstants.KEYBOARD_LAYOUT).append(valueSeparator)
            .append(parameters.get(AutConfigConstants.KEYBOARD_LAYOUT));
        props.add(sb.toString());

        sb = new StringBuffer();
        sb.append(AutConfigConstants.AUT_AGENT_HOST).append(valueSeparator)
            .append(parameters.get(AutConfigConstants.AUT_AGENT_HOST));
        props.add(sb.toString());

        sb = new StringBuffer();
        sb.append(AutConfigConstants.AUT_AGENT_PORT).append(valueSeparator)
            .append(parameters.get(AutConfigConstants.AUT_AGENT_PORT));
        props.add(sb.toString());

        sb = new StringBuffer();
        sb.append(AutConfigConstants.AUT_NAME).append(valueSeparator)
            .append(parameters.get(AutConfigConstants.AUT_NAME));
        props.add(sb.toString());

        sb = new StringBuffer();
        sb.append(Constants.AUT_JUB_INSTALL_DIRECTORY).append(valueSeparator)
            .append(getInstallDir());
        props.add(sb.toString());
        return props;
    }
    
    @Override
    public String getRcBundleId() {
        return CommandConstants.RC_RCP_BUNDLE_ID;
    }

}
