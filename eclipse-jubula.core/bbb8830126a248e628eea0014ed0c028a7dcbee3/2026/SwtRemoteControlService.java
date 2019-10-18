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
package org.eclipse.jubula.rc.rcp.swt.aut;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.rcp.common.classloader.EclipseUrlLocator;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.tools.internal.constants.AUTServerExitConstants;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class extends the SwtAUTServer to avoid buddy class loading,
 * which does not work, if the SWT library is in it's own bundle.
 */
public class SwtRemoteControlService extends SwtAUTServer {
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(SwtRemoteControlService.class);
    
    /** An instance of this class. */
    private static SwtRemoteControlService instance;

    /** True, if AUTServer is running, otherwise false. */
    private boolean m_hasRemoteControlServiceStarted = false;

    /**
     * Private constructor for Singleton pattern.
     */
    private SwtRemoteControlService() {
        super();
        // set also static instance in parent AUTServer
        setInstance(this);
    }

    /**
     * @return The instance of this remote control service.
     */
    public static AUTServer getInstance() {
        if (instance == null) {
            instance = new SwtRemoteControlService();
        }
        return instance;
    }

    /**
     * Check that the remote control service has been started
     * and start it, if necessary. It can only be started, if
     * there exists an active shell, which contains the
     * needed display.
     * @param display The model element with existing widget
     * @param rcpSwtComonentNamer The component namer for RCP with SWT.
     */
    public void checkRemoteControlService(Display display,
            RcpSwtComponentNamer rcpSwtComonentNamer) {
        if (!m_hasRemoteControlServiceStarted) {
            if (startRemoteControlService(display)) {
                prepareRemoteControlService(display, rcpSwtComonentNamer);
            }
        }
    }

    /**
     * Start the SwtAUTServer by connecting with the AUT agent.
     * @param display The SWT display.
     * @return True, if the AUTServer is already running, otherwise false.
     */
    private boolean startRemoteControlService(final Display display) {
        String autAgentHost = EnvironmentUtils
                .getProcessOrSystemProperty(AutConfigConstants.AUT_AGENT_HOST);
        if (autAgentHost != null) {
            try {
                setAutAgentHost(autAgentHost);
                setAutAgentPort(EnvironmentUtils.getProcessOrSystemProperty(
                        AutConfigConstants.AUT_AGENT_PORT));
                setAutID(EnvironmentUtils.getProcessOrSystemProperty(
                        AutConfigConstants.AUT_NAME));
                setInstallationDir(EnvironmentUtils.getProcessOrSystemProperty(
                        Constants.AUT_JUB_INSTALL_DIRECTORY));
                setDisplay(display);
                start(true); // true = start an RCP accessor
                m_hasRemoteControlServiceStarted = true;
            } catch (Exception e) {
                LOG.error(e.getLocalizedMessage(), e);
                System.exit(AUTServerExitConstants.AUT_START_ERROR);
            }
        }
        return m_hasRemoteControlServiceStarted;
    }

    /**
     * Prepare the SwtAUTServer for SWT components.
     * @param display The display.
     * @param rcpSwtComponentNamer The component namer for RCP with SWT.
     */
    private static void prepareRemoteControlService(
            Display display,
            RcpSwtComponentNamer rcpSwtComponentNamer) {
        // Registering the AdapterFactory for SWT at the registry
        AdapterFactoryRegistry.initRegistration(new EclipseUrlLocator());
        // add listener to AUT
        AUTServer.getInstance().addToolKitEventListenerToAUT();
        // add listener for SWT specific component naming like dialog buttons
        display.addFilter(SWT.Paint, rcpSwtComponentNamer);
        display.addFilter(SWT.Activate, rcpSwtComponentNamer);
    }
}