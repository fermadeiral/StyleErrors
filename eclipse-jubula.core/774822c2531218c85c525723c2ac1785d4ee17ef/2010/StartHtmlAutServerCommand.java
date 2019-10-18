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

import java.util.AbstractList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 01.09.2009
 */
public class StartHtmlAutServerCommand extends AbstractStartPseudoJavaAUT {
    /**
     * <code>DEFAULT_AUT_ID_ATTRIBUTE_NAME</code>
     */
    private static final String DEFAULT_AUT_ID_ATTRIBUTE_NAME = "id"; //$NON-NLS-1$
   
    /** the logger */
    private static Logger log = 
        LoggerFactory.getLogger(StartHtmlAutServerCommand.class);

    /** 
     * mapping from browser type (String) to corresponding 
     * Selenium browser command (String) 
     */
    private static final Map<String, String> BROWSER_TO_CMD_MAP = 
        new HashMap<String, String>();
    
    static {
        BROWSER_TO_CMD_MAP.put("Firefox", "*firefox"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("FirefoxOver47", "*firefox"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("InternetExplorer", "*iexplore"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("Safari", "*safari"); //$NON-NLS-1$ //$NON-NLS-2$
        BROWSER_TO_CMD_MAP.put("Chrome", "*googlechrome"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    /**
     * {@inheritDoc}
     */
    protected String[] createCmdArray(String baseCmd, 
            Map<String, String> parameters) {
        Vector<String> commands = new Vector<String>();
        commands.add(baseCmd);
        addDebugParams(commands, false);
        
        Object webdriverMode = parameters.get(
                AutConfigConstants.WEBDRIVER_MODE);
        Boolean useWebdriver = (String.valueOf(webdriverMode)
                .equals(Boolean.TRUE.toString()));
        StringBuilder serverClasspath = new StringBuilder();
        String [] bundlesToAddToClasspath = getBundlesForClasspath(
                useWebdriver);
            
        for (String bundleId : bundlesToAddToClasspath) {
            String classpathForBundleId = AbstractStartToolkitAut
                    .getClasspathForBundleId(bundleId);
            if (!StringUtils.isEmpty(classpathForBundleId)) {
                serverClasspath.append(classpathForBundleId);
                serverClasspath.append(PATH_SEPARATOR);
            } else {
                log.warn("Bundle not found: " + bundleId);  //$NON-NLS-1$
            }
        }
        
        commands.add("-classpath"); //$NON-NLS-1$
        commands.add(serverClasspath.toString());

        commands.add("com.bredexsw.jubula.rc.html.WebAUTServer"); //$NON-NLS-1$
        // connection parameters
        commands.add(String.valueOf(
                AutStarter.getInstance().getAutCommunicator().getLocalPort()));
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_ARGUMENTS)));
        if (useWebdriver) {
            commands.add(String.valueOf(parameters.get(
                    AutConfigConstants.BROWSER)));
        } else {
            commands.add(getBrowserString(
                    parameters.get(AutConfigConstants.BROWSER_PATH), 
                    parameters.get(AutConfigConstants.BROWSER),
                    useWebdriver));
        }
        if (useWebdriver) {            
            commands.add(String.valueOf(
                    parameters.get(AutConfigConstants.BROWSER_SIZE)));
        } else {
            // place holder
            commands.add("AUT"); //$NON-NLS-1$
        }
        
        fillRegistrationParameters(parameters, commands);
        
        // additional parameters
        Object idAttribute = parameters.get(AutConfigConstants.WEB_ID_TAG);
        if (idAttribute != null) {
            commands.add(String.valueOf(idAttribute));
        } else {
            commands.add(DEFAULT_AUT_ID_ATTRIBUTE_NAME);
        }
        if (useWebdriver) {
            commands.add(String.valueOf(parameters.get(
                    AutConfigConstants.BROWSER_PATH)));
            commands.add(String.valueOf(parameters.get(
                    AutConfigConstants.DRIVER_PATH)));
        } else {
            Object singleWindowMode =
                    parameters.get(AutConfigConstants.SINGLE_WINDOW_MODE);
            if (singleWindowMode != null) {
                commands.add(String.valueOf(singleWindowMode));
            } else {
                commands.add(String.valueOf(true));
            }
        }
        return commands.toArray(new String[commands.size()]);
    }

    /**
     * add host/port parameters to commands
     * @param parameters the parameters
     * @param commands the commands
     */
    private void fillRegistrationParameters(Map parameters,
            AbstractList<String> commands) {
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_AGENT_HOST)));
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_AGENT_PORT)));
        commands.add(String.valueOf(
                parameters.get(AutConfigConstants.AUT_ID)));
    }

    /**
     * @param useWebdriver whether to use webdriver
     * @return the bundles to add to the classpath
     */
    protected String[] getBundlesForClasspath(boolean useWebdriver) {
        String rcHtmlDriverBundleId = useWebdriver
                ? CommandConstants.RC_HTML_WEBDRIVER_BUNDLE_ID
                : CommandConstants.RC_HTML_SELENIUM2_BUNDLE_ID;
        return new String[] { CommandConstants.RC_HTML_BUNDLE_ID,
                              rcHtmlDriverBundleId,
                              CommandConstants.TOOLKIT_HTML_BUNDLE_ID,
                              CommandConstants.TOOLS_BUNDLE_ID,
                              CommandConstants.TOOLS_EXEC_BUNDLE_ID,
                              CommandConstants.COMMUNICATION_BUNDLE_ID,
                              CommandConstants.RC_COMMON_BUNDLE_ID,
                              CommandConstants.TOOLKIT_API_BUNDLE_ID,
                              CommandConstants.RC_BREDEX_COMMON_BUNDLE_ID,
                              CommandConstants.SLF4J_JCL_BUNDLE_ID,
                              CommandConstants.ORG_SLF4J_JUL_BUNDLE_ID,
                              CommandConstants.SLF4J_API_BUNDLE_ID,
                              CommandConstants.LOGBACK_CLASSIC_BUNDLE_ID,
                              CommandConstants.LOGBACK_CORE_BUNDLE_ID,
                              CommandConstants.LOGBACK_SLF4J_BUNDLE_ID,
                              CommandConstants.COMMONS_LANG_BUNDLE_ID,
                              CommandConstants.APACHE_IO_BUNDLE_ID,
                              CommandConstants.APACHE_EXEC_BUNDLE_ID,
                              CommandConstants.APACHE_ORO_BUNDLE_ID,
                              CommandConstants.COMMONS_COLLECTIONS_BUNDLE_ID };
    }

    /**
     * 
     * @param browserPath The path to the browser to start, or 
     *                    <code>null</code> if the default path for the given
     *                    browser type should be used.
     * @param browserType The browser type to start 
     *                    (ex. Firefox, Internet Explorer, Safari).
     * @param useWebdriver
     *                    Whether to use Webdriver for test execution
     * @return the command to use when starting Selenium in order to start the
     *         desired browser from the desired path.
     */
    private String getBrowserString(Object browserPath, Object browserType,
            boolean useWebdriver) {
        String browserString;
        
        if (useWebdriver) {            
            if (browserType == null) {
                throw new IllegalArgumentException(
                        "Unsupported browser type: " + browserType); //$NON-NLS-1$
            }
            browserString = String.valueOf(browserType);
        } else {
            Object browser = BROWSER_TO_CMD_MAP.get(browserType);
            if (browser == null) {
                throw new IllegalArgumentException(
                        "Unsupported browser type: " + browserType); //$NON-NLS-1$
            }
            browserString = String.valueOf(browser);

        }

        if (browserPath != null) {
            browserString += " " + String.valueOf(browserPath); //$NON-NLS-1$
        }
              
        return StringConstants.QUOTE + browserString + StringConstants.QUOTE;
    }


    @Override
    public String getRcBundleId() {
        return CommandConstants.RC_HTML_BUNDLE_ID;
    }
}
