/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.rcp.e3.accessor;

import java.lang.reflect.Field;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.rcp.common.classloader.EclipseUrlLocator;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.widgets.Display;
/**
 * This is the {@link BundleActivator} for an simple startup.
 * This class is activated on startup and is starting a {@link Thread} 
 * which is waiting for the <code>Default<code> {@link Display} after an 
 * specified amount of sleep.
 * This approach is should only be used if the application has no workbench.
 * Naming for parts like the toolbar is not working, as well GEF might not work
 * 
 * The SimpleStartup is only working if the Default Display is used for the ui!
 * @author BREDEX GmbH
 */
public class SimpleStartup implements BundleActivator {
    
    /** environment variable */
    public static final String JUBULA_ACCESSOR_SIMPLE = "JUBULA_ACCESSOR_SIMPLE"; //$NON-NLS-1$
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(SimpleStartup.class);
    /** the sleep time before checking the {@link Display} */
    private static int sleeptime = 30;
    
    /**
     * Thread to wait for the default {@link Display} and start the {@link AUTServer}
     */
    private class WaitForDisplay implements Runnable {
        /** the timeout for waiting for the default display */
        private static final int WAIT_FOR_DISPLAY_TIMEOUT = 30000;
        
        /** {@inheritDoc} */
        public void run() {
            LOG.info("starting wait for default Display job"); //$NON-NLS-1$
            long start = System.currentTimeMillis();
            boolean wait = true;
            while (wait) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    LOG.debug("Sleep interrupted"); //$NON-NLS-1$
                }
                if ((System.currentTimeMillis() - start) > sleeptime * 1000) {
                    wait = false;
                }
            }
            LOG.info("wait for Default display"); //$NON-NLS-1$
            try {
                start = System.currentTimeMillis();
                boolean waitforDisplay = true;
                while (waitforDisplay) {
                    Object obj = getDefaultDisplay();
                    if (obj != null) {
                        waitforDisplay = false;
                    }
                    if ((System.currentTimeMillis() - start) 
                            > WAIT_FOR_DISPLAY_TIMEOUT) {
                        return;
                    }
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        LOG.debug("Sleep interrupted"); //$NON-NLS-1$
                    }
                }
            } catch (Exception e) {
                LOG.debug("getting the Display via reflection failed", e); //$NON-NLS-1$
            }
            final Display display = Display.getDefault();

            LOG.info("starting AUT server"); //$NON-NLS-1$
            AUTServer autServer = initAutServer(display);
            AdapterFactoryRegistry.initRegistration(new EclipseUrlLocator());
//          // add listener to AUT
            autServer.addToolKitEventListenerToAUT();
        }
        
        /**
         * Initializes the AUT Server for the host application.
         * 
         * @param display
         *            The Display to use for the AUT Server.
         * @return the AUTServer instance
         */
        private AUTServer initAutServer(Display display) {
            AUTServer instance = AUTServer.getInstance(
                    CommandConstants.AUT_SWT_SERVER);
            ((SwtAUTServer) instance).setDisplay(display);
            instance.setAutAgentHost(EnvironmentUtils
                    .getProcessOrSystemProperty(
                            AutConfigConstants.AUT_AGENT_HOST));
            instance.setAutAgentPort(EnvironmentUtils
                    .getProcessOrSystemProperty(
                            AutConfigConstants.AUT_AGENT_PORT));
            instance.setAutID(EnvironmentUtils
                    .getProcessOrSystemProperty(AutConfigConstants.AUT_NAME));
            instance.setInstallationDir(
                    EnvironmentUtils.getProcessOrSystemProperty(
                            Constants.AUT_JUB_INSTALL_DIRECTORY));
            instance.start(true);
            return instance;
        }
        
    }
    
    /** 
     * {@inheritDoc}
     */
    public void start(BundleContext context) throws Exception {
        String env = EnvironmentUtils
                .getProcessOrSystemProperty(JUBULA_ACCESSOR_SIMPLE);
        if (env == null) {
            LOG.info("using standard accessor"); //$NON-NLS-1$
            return;
        } 
        LOG.warn("Using simple accessor"); //$NON-NLS-1$
        if (env.equals(StringConstants.EMPTY)) {
            LOG.info("with default " + sleeptime + "s sleeptime"); //$NON-NLS-1$ //$NON-NLS-2$
        } else {
            try {
                sleeptime = Integer.parseInt(env);
                LOG.info("with " + env + "s sleeptime"); //$NON-NLS-1$ //$NON-NLS-2$
            } catch (NumberFormatException nfe) {
                LOG.debug("failed to get number from environment"); //$NON-NLS-1$
                LOG.debug("using default " + sleeptime + "s sleeptime"); //$NON-NLS-1$ //$NON-NLS-2$
                // ignore using default
            }
        }

        Thread thread = new Thread(new WaitForDisplay());
        thread.start();
    }

    /**
     * Getting the value of the Default display without calling
     * {@link Display#getDefault()}
     * 
     * @return the value of {@link Display} <code>Default</code> variable
     * @throws Exception 
     */
    private Object getDefaultDisplay() throws Exception {
        Class clazz = Display.class;
        try {
            Field field = clazz.getDeclaredField("Default"); //$NON-NLS-1$
            field.setAccessible(true);
            Object defaultDisplayValue = field.get(null);
            return defaultDisplayValue;
        } catch (NoSuchFieldException nsfe) {
            throw new Exception(nsfe);
        } catch (SecurityException se) {
            throw new Exception(se);
        } catch (IllegalAccessException e) {
            throw new Exception(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stop(BundleContext context) throws Exception {
        // nothing to stop
    }
    
}
