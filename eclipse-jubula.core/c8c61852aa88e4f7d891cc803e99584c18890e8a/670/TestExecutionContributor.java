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

import java.net.InetAddress;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.TestExecution.PauseMode;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent.State;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.commands.AUTModeChangedCommand;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.constants.TestExecutionConstants;
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.events.AutAgentEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ServerState;
import org.eclipse.jubula.client.core.events.IAUTEventListener;
import org.eclipse.jubula.client.core.events.IAUTServerEventListener;
import org.eclipse.jubula.client.core.events.IServerEventListener;
import org.eclipse.jubula.client.core.events.ServerEvent;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IObjectMappingCategoryPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.jobs.StartAutJob;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.OMEditorTreeLabelProvider;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TimingConstantsClient;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 20.07.2004
 */
public class TestExecutionContributor
    implements IAUTEventListener, IServerEventListener,
    IAUTServerEventListener, ITestExecutionEventListener {

    /** instance of this class */
    private static TestExecutionContributor instance;
    
    /** the logger */
    private static Logger log = LoggerFactory
        .getLogger(TestExecutionContributor.class);
    
    /** ClientTestImpl */
    private IClientTest m_clientTest;
    /** the started server */
    private String m_server = StringConstants.EMPTY; 
    /** the port number of the started server */
    private String m_port = StringConstants.EMPTY;
    
    /**
     * Creates an empty editor action bar contributor
     */
    private TestExecutionContributor () {
        super();
        setClientTest(ClientTest.instance());
        registerAsListener();
    }
    
    /**
     * add this Contributor as listener for AUTEvents, AutStarterEvents and
     * AUTServerEvents. Deregistering happens in deregister(), called from
     * dispose()..
     */
    private void registerAsListener() {
        IClientTest clientTest = ClientTest.instance();
        clientTest.addTestEventListener(this);
        clientTest.addAutAgentEventListener(this);
        clientTest.addAUTServerEventListener(this);
        clientTest.addTestExecutionEventListener(this);
    }

    /**
     * deregister this as listener
     */
    private void deregister() {
        IClientTest clientTest = ClientTest.instance();
        clientTest.removeTestEventListener(this);
        clientTest.removeAutAgentEventListener(this);
        clientTest.removeAUTServerEventListener(this);
        clientTest.removeTestExecutionEventListener(this);
    }
    
    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // remove this as EventListener from ClientTestImpl
        deregister();
        // free ClientTestImpl reference
        setClientTest(null);
    }

    /**
     * @param event AUTEvent
     */
    public void stateChanged(final AUTEvent event) {
        handleEvent(event);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stateChanged(final AutAgentEvent event) {
        handleEvent(event);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stateChanged(final AUTServerEvent event) {
        handleEvent(event);
    }
    
    /**
     * {@inheritDoc}
     * @param event The event.
     */
    public void stateChanged(final TestExecutionEvent event) {
        handleEvent(event);
    }
    
    /**
     * @return Returns the clientTest.
     */
    public IClientTest getClientTest() {
        return m_clientTest;
    }
    
    /**
     * @param clientTest The clientTest to set.
     */
    public void setClientTest(IClientTest clientTest) {
        m_clientTest = clientTest;
    }
    
    /**
     * display a text for an AUTEvent in the status line
     * @param event the raised event, determines the displayed message 
     */
    void handleEvent(AUTEvent event) {
        log.info(Messages.HandleAUTEvent + StringConstants.COLON 
                + StringConstants.SPACE + event.toString());
        String message = Plugin.getStatusLineText();
        String error = null;
        int icon = Plugin.isConnectionStatusIcon();
        switch (event.getState()) {
            case AUTEvent.AUT_STARTED: 
                message = Messages.TestExecutionContributorAUTStartedTesting;
                icon = Constants.AUT_UP;
                fireAndSetAutState(true);
                AUTModeChangedCommand.setAutMode(ChangeAUTModeMessage.TESTING);
                break;
            case AUTEvent.AUT_ABORTED:
                fireAndSetAutState(false);
                break;
            case AUTEvent.AUT_STOPPED:
                message = Messages.TestExecutionContributorAUTStopped;
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;
            case AUTEvent.AUT_NOT_FOUND:
                error = Messages.TestExecutionContributorAUTNotFound;
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;
            case AUTEvent.AUT_MAIN_NOT_FOUND:
                error = Messages.TestExecutionContributorMainClassNotFound;
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;  
            case AUTEvent.AUT_CLASS_VERSION_ERROR:
                error = Messages.TestExecutionContributorClassVersionError;
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;  
            case AUTEvent.AUT_START_FAILED:
                error = StringConstants.EMPTY;
                icon = Constants.NO_SC;
                fireAndSetAutState(false);
                break;  
            case AUTEvent.AUT_RESTARTED:
                m_clientTest.addAUTServerEventListener(this);
                fireAndSetAutState(true);
                // only important for TestExecution
                break;
            case AUTEvent.AUT_ABOUT_TO_TERMINATE:
                m_clientTest.removeAUTServerEventListener(this);
                break;
            default:
                Assert.notReached(Messages.UnknownAUTState 
                        + String.valueOf(event.getState()));
                break;  
        }
        Plugin.showStatusLine(icon, message); 
        if (error != null) {            // show error
            if (StringConstants.EMPTY.equals(error)) {
                error = null;
            }
            ErrorHandlingUtil.createMessageDialog(MessageIDs.E_AUT_START, null, 
                    error.split(StringConstants.NEWLINE));
        }
    }
    
    /**
     * @param isAutRunning flag, if aut is running or not
     */
    private void fireAndSetAutState(boolean isAutRunning) {
        AutState state = isAutRunning ? AutState.running : AutState.notRunning;
        DataEventDispatcher.getInstance().fireAutStateChanged(state);
    }

    /**
     * display a text for an AUTServerEvent in the status line, also
     * enables/disables the actions 
     * @param event the raised event, determines the displayed message
     */
    void handleEvent(AUTServerEvent event) {
        if (log.isDebugEnabled()) {
            log.debug(Messages.HandleAUTServerEvent + event.toString());       
        }
        int icon = Plugin.isConnectionStatusIcon();
        String message = Plugin.getStatusLineText();
        String error = null;
        switch (event.getState()) {
            case ServerEvent.CONNECTION_CLOSED:                
                message = getConnectionCloseMsg(message);
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case ServerEvent.CONNECTION_GAINED:
                message = Messages.TestExecutionContributorConnectedToAUTServer;
                icon = Constants.NO_AUT;
                // no changes for actions, see stateChanged(AUTEvent);
                break;
            case AUTServerEvent.SERVER_NOT_INSTANTIATED:
                error = Messages.TestExecutionContributorServerNotInstantiated;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.DOTNET_INSTALL_INVALID:
                error = Messages.TestExecutionContributorDotNetInstallProblem;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.JDK_INVALID:
                error = Messages.TestExecutionContributorUnrecognizedJavaAgent;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.INVALID_JAVA:
                error = Messages.TestExecutionContributorStartingJavaFailed;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.COULD_NOT_ACCEPTING:
                error = Messages.
                    TestExecutionContributorOpeningConnAUTServerFailed;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.COMMUNICATION:
                error = Messages.
                    TestExecutionContributorCommunicationAUTServerFailed;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.INVALID_JAR:
                error = Messages.TestExecutionContributorInvalidJar;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.NO_MAIN_IN_JAR:
                error = Messages.TestExecutionContributorNoMainInJar;
                icon = Constants.NO_SC;
                setAutNotRunningState();
                break;
            case AUTServerEvent.TESTING_MODE:
            case AUTServerEvent.MAPPING_MODE:
            case AUTServerEvent.RECORD_MODE:
            case AUTServerEvent.CHECK_MODE:
                autServerModeChanged(event);
                return;             
            default:
                setAutNotRunningState();
                log.error(Messages.UnknownAUTServerState + event.getState());
        }
        if (StringUtils.isNotBlank(event.getAdditionalInfo())) {
            error += event.getAdditionalInfo();
        }
        showError(icon, message, error);
    }

    /**
     * Get connection closed message text
     * @param msg base message
     * @return detailed message
     */
    private String getConnectionCloseMsg(String msg) {
        String message = getConnectionMessage(msg);
        message = message + StringConstants.SPACE + StringConstants.COLON
                + StringConstants.SPACE
                + Messages.TestExecutionContributorConnAUTServClosed;
        return message;
    }

    /**
     * show an error info
     * @param icon display info
     * @param message display info
     * @param error display info
     */
    private void showError(int icon, String message, String error) {
        Plugin.showStatusLine(icon, message); 
        if (error != null) {            // show error
            if (StringConstants.EMPTY.equals(error)) {
                showError(null);
            } else {
                showError(error);
            }
        }
    }

    /**
     * @param errorHolder The error string
     */
    private void showError(final String errorHolder) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_AUT_START, null,
                        errorHolder.split(StringConstants.NEWLINE));
            }
        });
    }

    /**
     * @param message the serverConnectionStatusMessage to show in status bar
     * @return the serverConnectionStatusMessage to show in status bar
     */
    private String getConnectionMessage(String message) {
        String msg = message;
        InetAddress localhost = EnvConstants.LOCALHOST;
        msg = NLS.bind(Messages.TestExecutionContributorConnectedToAUTAgent1,
            new Object[] { m_server, m_port });
        if (Messages.StartAutBPLocalhost.equals(m_server.toLowerCase())) {
            msg = NLS.bind(
                Messages.TestExecutionContributorConnectedToAUTAgent2,
                new Object[] { m_server, m_port });
        } else if (localhost != null
            && (m_server.equals(localhost.getHostName())
                || m_server.equals(localhost.getHostAddress()) || m_server
                    .equals(localhost.getCanonicalHostName()))) {

            msg = NLS
                .bind(Messages.TestExecutionContributorConnectedToAUTAgent3,
                    new Object[] { m_server, Messages.StartAutBPLocalhost,
                        m_port });
        }
        return msg;
    }

    /**
     * 
     */
    private void setAutNotRunningState() {
        DataEventDispatcher.getInstance().fireAutStateChanged(
            AutState.notRunning);
    }

    /**
     * set the status Line for changed mode
     * @param e AUTServerEvent
     */
    private void autServerModeChanged(AUTServerEvent e) {
        int icon = Plugin.isConnectionStatusIcon();
        String message = Plugin.getStatusLineText();
        switch (e.getState()) {
            case AUTServerEvent.TESTING_MODE:
                message = Messages.TestExecutionContributorAUTStartedTesting;
                icon = Constants.AUT_UP;
                break;
            case AUTServerEvent.MAPPING_MODE:
                String strCat;
                IObjectMappingCategoryPO cat =
                    ObjectMappingEventDispatcher.getCategoryToCreateIn();
                if (cat != null) {
                    strCat = cat.getName();
                    if (OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat) != null) {
                        strCat = OMEditorTreeLabelProvider
                            .getTopLevelCategoryName(strCat);
                    }
                } else {
                    strCat = Messages.TestExecutionContributorCatUnassigned;
                }
                
                message = NLS.bind(Messages.
                        TestExecutionContributorAUTStartedMapping, strCat);  
                icon = Constants.MAPPING;
                break;
            case AUTServerEvent.RECORD_MODE:
                //message = I18n.getString("TestExecutionContributor.AUTStartedRecording");  //$NON-NLS-1$
                message = Messages.TestExecutionContributorAUTStartedRecording;
                icon = Constants.RECORDING;
                break;
            case AUTServerEvent.CHECK_MODE:
                //message = I18n.getString("TestExecutionContributor.AUTStartedRecordingCheckMode");  //$NON-NLS-1$
                message = Messages.
                    TestExecutionContributorAUTStartedRecordingCheckMode;
                icon = Constants.CHECKING;
                break;
            default:
                break;
        }
        Plugin.showStatusLine(icon, message); 
    }
    
    /**
     * display a text for an AutAgentEvent in the status line 
     * @param event the raised event, determines the displayed message
     */
    void handleEvent(AutAgentEvent event) {
        if (log.isDebugEnabled()) {
            log.debug(Messages.HandleAUTAgentEvent + StringConstants.COLON
                    + StringConstants.SPACE + event.toString());
        } 
        String statusLineMessage = Plugin.getStatusLineText();
        Integer messageId = MessageIDs.E_SERVER_ERROR;
        String error = null;
        int icon = Plugin.isConnectionStatusIcon();
        switch (event.getState()) {
            case ServerEvent.CONNECTION_CLOSED:
                statusLineMessage = Messages.StatusLine_NotConnected;
                icon = Constants.NO_SERVER;
                DataEventDispatcher.getInstance().fireAutAgentConnectionChanged(
                        ServerState.Disconnected);
                break;
            case ServerEvent.CONNECTION_GAINED:
                statusLineMessage = getConnectionMessage(statusLineMessage);
                icon = Constants.NO_SC;
                DataEventDispatcher.getInstance()
                    .fireAutAgentConnectionChanged(ServerState.Connected);
                // no changing for the actions, see AUTServerEvent
                break;
            case AutAgentEvent.SERVER_CANNOT_CONNECTED:
                error = Messages.InfoDetailConnectToAutAgentFailed;
                statusLineMessage = Messages.StatusLine_NotConnected;
                DataEventDispatcher.getInstance().fireAutAgentConnectionChanged(
                        ServerState.Disconnected);
                messageId = MessageIDs.I_SERVER_CANNOT_CONNECTED;
                break;
            case AutAgentEvent.VERSION_ERROR:
                error = Messages.ErrorMessageVERSION_ERROR;
                statusLineMessage = Messages.StatusLine_NotConnected;
                DataEventDispatcher.getInstance().fireAutAgentConnectionChanged(
                        ServerState.Disconnected);
                break;
            default:
                log.error(Messages.UnknownAUTAgentState + StringConstants.COLON
                        + StringConstants.SPACE 
                        + String.valueOf(event.getState()));
                break;
        }
        Plugin.showStatusLine(icon, statusLineMessage);
        if (error != null) {
            //  show error
            if (StringConstants.EMPTY.equals(error)) {
                error = null;
            }
            ErrorHandlingUtil.createMessageDialog(messageId, 
                    new String [] {m_server, m_port}, 
                        error.split(StringConstants.NEWLINE));
        }       
    }

    /**
     * enables and disables the start and stopTestSuiteActions
     * @param event the event to determins the enabled/disabled actions
     */
    void handleEvent(TestExecutionEvent event) {
        String message = Plugin.getStatusLineText();
        String error = null;
        int icon = Plugin.isConnectionStatusIcon();
        ICapPO cap = null;
        String testCaseName = null;
        String capName = null;
        switch (event.getState()) {
            case TEST_EXEC_STOP:
                icon = Constants.AUT_UP;
                message = Messages.TestExecutionContributorSuiteStop;
                break;
            case TEST_EXEC_FAILED:
                error = getTestSuiteErrorText(event);
                message = Messages.TestExecutionContributorSuiteFailed;
                break;
            case TEST_EXEC_START:
                setClientMinimized(true);
                icon = Constants.AUT_UP;
                message = Messages.TestExecutionContributorSuiteRun;
                showTestResultTreeView();
                break;
            case TEST_EXEC_RESULT_TREE_READY:
                message = Messages.TestExecutionContributorSuiteRun;
                break;
            case TEST_EXEC_FINISHED:
                message = Messages.TestExecutionContributorSuiteFinished;
                icon = Constants.AUT_UP;
                break;
            case TEST_EXEC_COMPONENT_FAILED:
                cap = TestExecution.getInstance().getActualCap();
                final String compName = CompNameManager.getInstance().
                        getNameByGuid(cap.getComponentName());
                testCaseName = cap.getSpecAncestor().getName();
                capName = cap.getName();
                error = NLS.bind(Messages.TestExecutionContributorCompFailure,
                        new Object[]{compName, testCaseName, capName});
                message = Messages.TestExecutionContributorSuiteFailed;
                break;
            case TEST_EXEC_PAUSED:
                setClientMinimized(false);
                icon = Constants.PAUSED;
                message = Messages.TestExecutionContributorSuitePaused;
                break;
            case TEST_RUN_INCOMPLETE_TESTDATA_ERROR:
                setClientMinimized(false);
                error = getIncompleteTestRunMessage(
                        State.TEST_RUN_INCOMPLETE_TESTDATA_ERROR);
                message = Messages.TestExecutionContributorSuiteFailed;
                break;
            case TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR:
                setClientMinimized(false);
                error = getIncompleteTestRunMessage(
                        State.TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR);
                message = Messages.TestExecutionContributorSuiteFailed;
                break;
            case TEST_EXEC_RESTART:
                icon = Constants.AUT_UP;
                message = Messages.TestExecutionContributorSuiteRun;
                break;
            default:
                log.error(Messages.UnknownTestExecutionEvent);
                endTestExecution();
        }
        showErrorAndStatus(event, message, error, icon);
    }



    /**
     * @param event the current TestExecutionEvent
     * @param message the status message
     * @param error the error message
     * @param icon the status icon
     */
    private void showErrorAndStatus(TestExecutionEvent event, String message, 
            String error, int icon) {
        
        DataEventDispatcher.getInstance().fireTestSuiteStateChanged(event);
        Plugin.showStatusLine(icon, message);
        if (error != null) {
            String[] errorDetail = error.split(StringConstants.NEWLINE);
            if (StringConstants.EMPTY.equals(error)) {
                errorDetail = null;
            }
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_TEST_EXECUTION_ERROR, null, errorDetail);
        }
    }

    /**
     * Gets the I18N-String of an error occurred in an incomplete TestSuite-Run
     * depending of the given TestExecutionEvent-ID
     * @param testExecEventID the TestExecutionEvent-ID
     * @return the I18N-String of the error description.
     */
    private String getIncompleteTestRunMessage(State testExecEventID) {
        ICapPO cap = TestExecution.getInstance().getActualCap();
        String testCaseName = cap.getSpecAncestor().getName();
        String capName = cap.getName();
        switch (testExecEventID) {
            case TEST_RUN_INCOMPLETE_TESTDATA_ERROR:
                return NLS.bind(Messages.
                    TestExecutionContributorTEST_RUN_INCOMPLETE_TESTDATA_ERROR,
                    new Object[]{testCaseName, capName});
            case TEST_RUN_INCOMPLETE_OBJECTMAPPING_ERROR:
                return NLS.bind(Messages.
                        TestExecutionContributorRunIncompleteOMError,
                    new Object[]{testCaseName, capName});
            default:
                log.error(Messages.UnknownTestExecutionEvent);
                endTestExecution();  
        }
        return StringConstants.EMPTY;
    }

    /**
     * minimize/maximize when preference is set
     * 
     * @param flag
     *            boolean
     */
    public static void setClientMinimized(final boolean flag) {
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
                Constants.MINIMIZEONSUITESTART_KEY)) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    Shell shell = Plugin.getActiveWorkbenchWindowShell();
                    boolean foundShell = shell != null ? true : false;
                    if (!foundShell) {
                        shell = Plugin.getDisplay().getActiveShell();
                        foundShell = shell != null ? true : false;
                    }
                    if (!foundShell) {
                        shell = Display.getDefault().getActiveShell();
                        foundShell = shell != null ? true : false;
                    }
                    if (!foundShell) {
                        shell = Plugin.getShell();
                        foundShell = shell != null ? true : false;
                    }
                    shell.setMinimized(flag);
                    if (shell.getMinimized() != flag) {
                        shell.setMaximized(!flag);
                    }
                }
            });
        }
    }
    
    /**
     * returns an Error Text for displaying in GUI
     * @param event TestExecutionEvent
     * @return Text
     */
    private String getTestSuiteErrorText(TestExecutionEvent event) {
        if (event.getException() != null) {
            return event.getException().getMessage();    
        }
        return Messages.TestExecutionContributorSuiteFailed;
    }

    /**
     * Shows the TestResultTreeView
     */
    private void showTestResultTreeView() {
        if (Plugin.getDefault().getPreferenceStore().getBoolean(
                Constants.OPENRESULTVIEW_KEY)) {
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    try {
                        Plugin.getActivePage().showView(Constants.TESTRE_ID);
                    } catch (PartInitException pie) {
                        log.error(Messages
                            .TestResultTreeViewCouldNotInitialised, pie);
                    } catch (NullPointerException npe) {
                        log.error(Messages.WindowIsNull, npe);
                    }
                }
            });
        }
    }
    
    /**
     * the startAUT action
     * @param conf configuration to use for AUT
     * @param aut The IAUTMainPO
     */
    public void startAUTaction(final IAUTMainPO aut, final IAUTConfigPO conf) {
        Validate.notNull(conf, Messages.ConfigurationMustNotNull);
        Job job = new StartAutJob(aut, conf);
        JobUtils.executeJob(job, null);
    }

    /**
     * the connectServer action
     * @param autAgentHost The server to start.
     * @param port The port of the server to start.
     */
    public void connectToAutAgent(String autAgentHost, String port) {
        m_server = autAgentHost;
        m_port = port;
        getClientTest().connectToAutAgent(autAgentHost, port);
    }

    /**
     * the disconnectServer action
     */
    public void disconnectFromAutAgent() {
        getClientTest().disconnectFromAutAgent();
        m_server = StringConstants.EMPTY;
        m_port = StringConstants.EMPTY;
    }

    /**
     * Stops the Running AUT with the given ID.
     * 
     * @param autId The ID of the Running AUT to stop.
     */
    public  void stopAUT(AutIdentifier autId) {
        fireAndSetAutState(false);
        final ITestSuitePO startedTestSuite = TestExecution.getInstance()
            .getStartedTestSuite();
        if (startedTestSuite != null
            && startedTestSuite.isStarted()) {
            
            stopTestSuiteAction();
        }
        getClientTest().stopAut(autId);
    }
    
    /**
     * Starts the testSuiteExecution
     * @param ts The runnableTestSuite.
     * @param autId The ID of the Running AUT on which the test will take place.
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     * @param iterMax maximum number of iterations
     */
    public void startTestSuiteAction(ITestSuitePO ts, 
            AutIdentifier autId, boolean autoScreenshot, int iterMax) {
        TimeUtil.delay(TimingConstantsClient.START_TEST_SUITE_DELAY);
        getClientTest().startTestSuite(ts, autId, autoScreenshot, iterMax, null,
            TestExecutionConstants.RunSteps.NORMAL.getStepValue(), null);
    }

    /**
     * Stops the TestSuiteExecution
     */
    public void stopTestSuiteAction() {
        getClientTest().stopTestExecution();
    }


    /**
     * {@inheritDoc}
     */
    public void pauseTestSuiteAction(PauseMode pm) {
        getClientTest().pauseTestExecution(pm);
    }
    
    /**
     * @return Returns the instance.
     */
    public static TestExecutionContributor getInstance() {
        if (instance == null) {
            instance = new TestExecutionContributor();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public void endTestExecution() {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project == null) {
            return;
        }
        List<ITestSuitePO> tsList = TestSuiteBP.getListOfTestSuites(project);
        for (ITestSuitePO ts : tsList) {
            ts.setStarted(false);
        }
        if (TestExecution.getInstance().getStartedTestSuite() != null) {
            TestExecution.getInstance().getStartedTestSuite().setStarted(false);
        }
        try {
            AUTConnection.getInstance().getCommunicator()
                .interruptAllTimeouts();
        } catch (ConnectionException e) {
            log.error(e.getLocalizedMessage(), e);
        }
        setClientMinimized(false);
    }

    @Override
    public void receiveExecutionNotification(String notification) {
        ErrorHandlingUtil.createMessageDialog(MessageIDs.E_TEST_EXECUTION_ERROR,
                null, new String[] { notification });
    }
}
