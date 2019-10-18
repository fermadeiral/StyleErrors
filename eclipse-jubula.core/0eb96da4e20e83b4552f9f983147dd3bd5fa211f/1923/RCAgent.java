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
package org.eclipse.jubula.rc.common.agent;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;


/**
 * @author BREDEX GmbH
 * @created Mar 26, 2008
 */
public class RCAgent {

    /**
     * hidden constructor
     */
    private RCAgent() {
        super();
    }
    
    /**
     * Creates the arguments array for AutServer, <br>
     * saves the current ClassLoader, <br>
     * calls the main-method of the AUTServerLauncher, <br>
     * reactivates the saved ClassLoader.
     * @param agentArguments String agentArguments
     * @param instrumentation a java.lang.instrument.Instrumentation instance
     * @throws ClassNotFoundException 
     * @throws NoSuchMethodException 
     * @throws SecurityException 
     * @throws InvocationTargetException 
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     *              If reflection calls fail.
     * @throws MalformedURLException 
     *              If any entry of the AUT Server classpath cannot be 
     *              parsed to a URL.
     */
    public static void premain(String agentArguments, 
            Instrumentation instrumentation) throws ClassNotFoundException, 
            SecurityException, NoSuchMethodException, IllegalArgumentException, 
            IllegalAccessException, InvocationTargetException, 
            MalformedURLException {  
        
        String autServerClassPath =
            System.getenv("AUT_SERVER_CLASSPATH"); //$NON-NLS-1$
        
        // create AutServer arguments
        String[] args = 
            new String[Constants.MIN_ARGS_REQUIRED];
        
        args[Constants.ARG_SERVERPORT] = System.getenv("AUT_SERVER_PORT"); //$NON-NLS-1$
        // placeholder
        args[Constants.ARG_AUTMAIN] = "AutMain"; //$NON-NLS-1$
        args[Constants.ARG_AUTSERVER_CLASSPATH] = autServerClassPath;
        args[Constants.ARG_AUTSERVER_NAME] = System.getenv("AUT_SERVER_NAME"); //$NON-NLS-1$

        // Aut Agent arguments
        args[Constants.ARG_REG_HOST] = 
            System.getenv(AutConfigConstants.AUT_AGENT_HOST);
        args[Constants.ARG_REG_PORT] = 
            System.getenv(AutConfigConstants.AUT_AGENT_PORT);
        args[Constants.ARG_AUT_NAME] = 
            System.getenv(AutConfigConstants.AUT_NAME);
        // true for agent is activated
        args[Constants.ARG_AGENT_SET] = CommandConstants.RC_COMMON_AGENT_ACTIVE;

        String [] fileNames = autServerClassPath.split(
                System.getProperty("path.separator")); //$NON-NLS-1$
        URL [] urls = new URL[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            urls[i] = new File(fileNames[i]).toURI().toURL();
        }
        
        final ClassLoader oldContextClassLoader = Thread.currentThread()
            .getContextClassLoader();
        
        try {
            ClassLoader autServerLauncherLoader = new URLClassLoader(urls);
            Class<?> autServerLauncherClass = 
                autServerLauncherLoader.loadClass(
                        CommandConstants.AUT_SERVER_LAUNCHER);
            Method mainMethod = 
                autServerLauncherClass.getMethod("main", String[].class); //$NON-NLS-1$
            mainMethod.invoke(null, new Object[] {args});
        } finally {
            Thread.currentThread().setContextClassLoader(oldContextClassLoader);
        }
    }
}
