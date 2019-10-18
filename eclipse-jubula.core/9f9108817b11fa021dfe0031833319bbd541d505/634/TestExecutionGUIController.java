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
package org.eclipse.jubula.client.ui.rcp.controllers;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ConnectAutAgentBP;
import org.eclipse.jubula.client.ui.rcp.dialogs.nag.RCPAUTStartDelayNagTask;
import org.eclipse.jubula.client.ui.rcp.handlers.AbstractStartTestHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager.AutAgent;
import org.eclipse.jubula.client.ui.rcp.utils.JBThread;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.SendCompSystemI18nMessage;
import org.eclipse.jubula.communication.internal.message.StopAUTServerMessage;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class is a gui controller, which methods are directly
 * executed by user actions in the graphical user interface.
 * Most of these actions are delegated to the
 * TestExecutionContributor.
 *
 * @author BREDEX GmbH
 * @created Feb 28, 2006
 */
public class TestExecutionGUIController {

    /** The logger */
    static final Logger LOG = 
        LoggerFactory.getLogger(TestExecutionGUIController.class);
    
    /** the timeout for the info nagger dialog if RCP AUT startup takes too long */
    private static final int NAGGER_TIMEOUT = 120 * 1000;

    /** Utility class */
    private TestExecutionGUIController() {
        // empty constructor
    }

    /**
     * @param aut aut to start
     * @param conf associated configuration for AUT to start
     */
    public static void startAUT(final IAUTMainPO aut, final IAUTConfigPO conf) {
        new JBThread() {
            /** inform user if AUT does not start within two minutes */
            private TimerTask m_infoRCPTask = null;
            
            @Override
            public void run() {
                if (aut.getToolkit()
                        .equals(CommandConstants.RCP_TOOLKIT)) {
                    AutIdentifier autId = new AutIdentifier(
                            conf.getConfigMap().get(
                                    AutConfigConstants.AUT_ID));
                    m_infoRCPTask = new RCPAUTStartDelayNagTask(autId);
                    Timer timer = new Timer();
                    try {
                        timer.schedule(m_infoRCPTask, NAGGER_TIMEOUT);
                    } catch (IllegalStateException e) {
                        // do nothing if task has already been cancelled
                    }
                }
                
                TestExecutionContributor.getInstance().startAUTaction(
                        aut, conf);
            }

            @Override
            public void interrupt() {
                disconnectFromServer();
                if (m_infoRCPTask != null) {
                    m_infoRCPTask.cancel();
                }
                super.interrupt();
            }

            @Override
            protected void errorOccurred() {
                if (m_infoRCPTask != null) {
                    m_infoRCPTask.cancel();
                }
            }
        } .start();
    }

    /**
     * starts the selected test suite. Testsuite must be
     * startable
     * @param ts ITestSuitePO
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     * @param iterMax the maximum number of iterations
     * @param autId The ID of the Running AUT on which the test will take place.
     */
    public static void startTestSuite(final ITestSuitePO ts,
        final AutIdentifier autId, final boolean autoScreenshot,
        final int iterMax) {
        TestExecutionContributor.setClientMinimized(true);
        JBThread t = new JBThread("Initialize Test Execution") { //$NON-NLS-1$
            @Override
            public void run() {
                if (!AbstractStartTestHandler.prepareTestExecution()) {
                    stopTestSuite();
                }
                TestExecutionContributor.getInstance().startTestSuiteAction(
                        ts, autId, autoScreenshot, iterMax);
            }

            @Override
            protected void errorOccurred() {
                // do nothing
            }
        };
        t.start();
        ts.setStarted(true);
    }

    /**
     * stops started TestSuite
     */
    public static void stopTestSuite() {
        JBThread t = new JBThread() {
            @Override
            public void run() {
                TestExecutionContributor.getInstance().
                    stopTestSuiteAction();
                List<ITestSuitePO> testSuites = TestSuiteBP
                        .getListOfTestSuites();
                for (ITestSuitePO ts : testSuites) {
                    ts.setStarted(false);
                }
            }

            @Override
            protected void errorOccurred() {
                // nothing

            }
        };
        t.start();
    }

    /**
     * @param autAgent
     *            server to connect Opens a dialog to select a server/port
     *            combination and connect to selected server.
     */
    public static void connectToAutAgent(final AutAgent autAgent) {
        DataEventDispatcher.getInstance().fireAutAgentConnectionChanged(
                ServerState.Connecting);
        final String jobName = NLS.bind(Messages.UIJobConnectToAUTAgent,
                new Object[]{autAgent.getName(), 
                    String.valueOf(autAgent.getPort())});
        Job connectToAUTAgent = new Job(jobName) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName, IProgressMonitor.UNKNOWN);
                connectToAutAgentImpl(autAgent);
                monitor.done();
                return Status.OK_STATUS;
            }
            
            @Override
            public boolean belongsTo(Object family) {
                if (family 
                        == AutAgentConnection.CONNECT_TO_AGENT_JOB_FAMILY_ID) {
                    return true;
                }
                return super.belongsTo(family);
            }
        };
        JobUtils.executeJob(connectToAUTAgent, null);
        ConnectAutAgentBP.getInstance().setCurrentAutAgent(autAgent);
    }


    /**
     * disconnects from server if connected
     */
    public static void disconnectFromServer() {
        JBThread t = new JBThread() {
            @Override
            public void run() {
                try {
                    if (AUTConnection.getInstance().isConnected()) {
                        AutAgentConnection.getInstance().request(
                                new StopAUTServerMessage(), new ICommand() {
                                    public Message execute() {
                                        return null;
                                    }

                                    public Message getMessage() {
                                        return null;
                                    }

                                    public void setMessage(Message message) {
                                        // empty
                                    }

                                    public void timeout() {
                                        // empty
                                    }
                                }, 2000);
                    }
                } catch (NotConnectedException e1) {
                    // no need to react, we are in the process of ending the AUT
                } catch (ConnectionException e1) {
                    // no need to react, we are in the process of ending the AUT
                } catch (CommunicationException e1) {
                    // no need to react, we are in the process of ending the AUT
                }
                TestExecutionContributor.getInstance()
                        .disconnectFromAutAgent();
            }

            @Override
            protected void errorOccurred() {
                // empty
            }
        };
        t.start();
    }


    /**
     * @param autAgent
     *            server to connect
     */
    private static void connectToAutAgentImpl(final AutAgent autAgent) {
        TestExecutionContributor.getInstance().connectToAutAgent(
            autAgent.getName(), autAgent.getPort().toString());
        try {
            AutAgentConnection connection = AutAgentConnection.getInstance();
            if (connection.isConnected()) {
                SendCompSystemI18nMessage message =
                    new SendCompSystemI18nMessage();
                message.setResourceBundles(CompSystemI18n.bundlesToString());
                try {
                    connection.send(message);
                } catch (NotConnectedException e) {
                    LOG.error("Could not send CompSystemI18nResourceBundle to AutAgent", e);  //$NON-NLS-1$
                } catch (IllegalArgumentException e) {
                    LOG.error("Could not send CompSystemI18nResourceBundle to AutAgent", e);  //$NON-NLS-1$
                } catch (CommunicationException e) {
                    LOG.error("Could not send CompSystemI18nResourceBundle to AutAgent", e);  //$NON-NLS-1$
                }
            }
        } catch (ConnectionException e) {
            DataEventDispatcher.getInstance().fireAutAgentConnectionChanged(
                    ServerState.Disconnected);
        }
        if (Plugin.getActiveView() != null) {
            Plugin.showStatusLine(Plugin.getActiveView());
        }
    }
}
