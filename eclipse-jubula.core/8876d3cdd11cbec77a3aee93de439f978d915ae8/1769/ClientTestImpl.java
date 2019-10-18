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
package org.eclipse.jubula.client.core;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.event.EventListenerList;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Document;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jubula.client.core.businessprocess.AbstractXMLReportGenerator;
import org.eclipse.jubula.client.core.businessprocess.CompleteXMLReportGenerator;
import org.eclipse.jubula.client.core.businessprocess.ErrorsOnlyXMLReportGenerator;
import org.eclipse.jubula.client.core.businessprocess.FileXMLReportWriter;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.TestExecution.PauseMode;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent.State;
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.core.businessprocess.TestresultSummaryBP;
import org.eclipse.jubula.client.core.commands.CAPRecordedCommand;
import org.eclipse.jubula.client.core.commands.DisconnectFromAutAgentResponseCommand;
import org.eclipse.jubula.client.core.commands.GetAutConfigMapResponseCommand;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.events.AutAgentEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.IAUTEventListener;
import org.eclipse.jubula.client.core.events.IAUTServerEventListener;
import org.eclipse.jubula.client.core.events.IServerEventListener;
import org.eclipse.jubula.client.core.events.ServerEvent;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestResult;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.MonitoringReportPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.TestResultPM;
import org.eclipse.jubula.client.core.persistence.TestResultSummaryPM;
import org.eclipse.jubula.client.core.testresult.export.ExporterRegistry;
import org.eclipse.jubula.client.core.utils.NodeNameUtil;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.client.internal.BaseConnection;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.ICommand;
import org.eclipse.jubula.communication.internal.listener.ICommunicationErrorListener;
import org.eclipse.jubula.communication.internal.message.BuildMonitoringReportMessage;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.DisconnectFromAutAgentMessage;
import org.eclipse.jubula.communication.internal.message.GetAutConfigMapMessage;
import org.eclipse.jubula.communication.internal.message.GetMonitoringDataMessage;
import org.eclipse.jubula.communication.internal.message.Message;
import org.eclipse.jubula.communication.internal.message.StartAUTServerMessage;
import org.eclipse.jubula.communication.internal.message.StopAUTServerMessage;
import org.eclipse.jubula.communication.internal.message.UnknownMessageException;
import org.eclipse.jubula.toolkit.common.businessprocess.ToolkitSupportBP;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.toolkit.common.monitoring.MonitoringAttribute;
import org.eclipse.jubula.toolkit.common.monitoring.MonitoringRegistry;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.InputConstants;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.IMonitoringValue;
import org.eclipse.jubula.tools.internal.objects.MonitoringValue;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.MonitoringUtil;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.jubula.tools.internal.utils.ZipUtil;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This class contains methods for starting and stopping a test. It's also holds
 * the listener for AutAgentEvent, AUTServerEvent and AUTEvent.
 * 
 * @author BREDEX GmbH
 * @created 16.07.2004
 */
public class ClientTestImpl implements IClientTest {
    /**
     * <code>TEST_SUITE_EXECUTION_RELATIVE_WORK_AMOUNT</code>
     */
    public static final int TEST_SUITE_EXECUTION_RELATIVE_WORK_AMOUNT = 1000;

    /** the logger */
    private static Logger log = LoggerFactory.getLogger(ClientTestImpl.class);

    /** file extension for XML */
    private static final String XML_FILE_EXT = ".xml"; //$NON-NLS-1$
    
    /** used in filenames for reports for successful tests */
    private static final String TEST_SUCCESSFUL = "ok"; //$NON-NLS-1$
    
    /** used in filenames for reports for failed tests */
    private static final String TEST_FAILED = "failed"; //$NON-NLS-1$
    /**
     * timeout for report job, after this time the job will be canceled
     * 1200000ms = 20min
     */
    private static final int MONITORING_REPORT_TIMEOUT = 1200000;
    
    /** timeout for requesting AutConfigMap from Agent */
    private static final int REQUEST_CONFIG_MAP_TIMEOUT = 10000;

    /** A list of listeners for all events*/
    private static EventListenerList eventListenerList = 
        new EventListenerList();
    
    /**
     * Time of test suite start
     */
    private Date m_testsuiteStartTime = null;
    
    /**
     * Time of test job start
     */
    private Date m_testjobStartTime = null;
    
    /**
     * Time of TestEnd
     */
    private Date m_endTime = null;

    /** log style (for example, Complete or Errors only) */
    private String m_logStyle = null;

    /**
     * flag, which indicates monitoring reports will be exported automatically.
     */
    private boolean m_generateMonitoringReport;
    
    /**
     * log path for results
     */
    private String m_logPath = null;

    /** The test result summary */
    private ITestResultSummaryPO m_summary;   
    
    /**
     * <code>m_relevant</code> the relevant flag
     */
    private boolean m_relevant = true;
    
    /**
     * <code>m_xmlScrennshot</code> the XML screenshot flag
     */
    private boolean m_xmlScrennshot = true;
    
    /**
     * <code>m_pauseOnError</code>
     */
    private boolean m_pauseOnError = false;

    /**
     * name of the file which is overwriting the default behavior
     */
    private String m_fileName;
    
    /** true if the report testresult Job is running */
    private AtomicBoolean m_isReportRunning = new AtomicBoolean(false);

    /** whether we are trimming the test result tree */
    private boolean m_trimming;
    /**
     * empty default constructor
     */
    public ClientTestImpl() {
        super();
        m_trimming = ArrayUtils.contains(Platform.getCommandLineArgs(), "-trimtree"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public void connectToAutAgent(String serverName, 
        String port) {
        
        try {
            if (!initServerConnection(serverName, port)) {
                // *ServerEvnetListener are already notified from 
                // initConnections() 
                fireAutAgentStateChanged(new AutAgentEvent(
                        AutAgentEvent.SERVER_CANNOT_CONNECTED));
                return;
            }
        } catch (JBVersionException e) {
            fireAutAgentStateChanged(new AutAgentEvent(
                AutAgentEvent.VERSION_ERROR));
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void disconnectFromAutAgent() {
        // Send request to AUT-Agent and wait for response
        ICommand command = new DisconnectFromAutAgentResponseCommand();
        Message message = new DisconnectFromAutAgentMessage();
        try {
            AutAgentConnection.getInstance().request(message, command, 10000);
        } catch (NotConnectedException e) {
            // Exceptions thrown from getInstance(): no connections are
            // established, just log
            log.info(Messages.ClosingTheConnectionsFailed, e);
        } catch (CommunicationException e) {
            // Exceptions thrown from getInstance(): no connections are
            // established, just log
            log.info(Messages.ClosingTheConnectionsFailed, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startAut(IAUTMainPO aut, IAUTConfigPO conf) 
        throws ToolkitPluginException {

        final String autToolkit = aut.getToolkit();
        if (!ComponentBuilder.getInstance().getLevelToolkitIds().contains(
                autToolkit)
            && ToolkitConstants.LEVEL_TOOLKIT.equals(
                ToolkitSupportBP.getToolkitLevel(autToolkit))) {

            throw new ToolkitPluginException(
                    Messages.ErrorMessageAUT_TOOLKIT_NOT_AVAILABLE);
        }
        
        try {
            // start the AUTServer
            Map<String, String> autConfigMap = createAutConfigMap(conf);
            autConfigMap.put(AutConfigConstants.NAME_TECHNICAL_COMPONENTS,
                String.valueOf(aut.isGenerateNames()));
            //add AUT properties to the config map
            if (!aut.getPropertyMap().isEmpty()) {
                String currentEnv = StringUtils.defaultIfEmpty(
                        autConfigMap.get(AutConfigConstants.ENVIRONMENT),
                        StringConstants.EMPTY);
                for (String autProp : aut.getPropertyMap().keySet()) {
                    String property = EnvironmentUtils.toPropertyString(autProp,
                            aut.getPropertyMap().get(autProp));
                    currentEnv = currentEnv
                            .concat(System.lineSeparator() + property);
                }
                autConfigMap.put(AutConfigConstants.ENVIRONMENT, currentEnv);
            }
            StartAUTServerMessage startAUTServerMessage = 
                new StartAUTServerMessage(autConfigMap, autToolkit);
            AutAgentConnection.getInstance().send(startAUTServerMessage);
            if (log.isDebugEnabled()) {
                log.debug(Messages.StartAUTServerMessageSend);
            }
        } catch (NotConnectedException nce) {
            // The AutAgentConnection was closed. This Exception occurs after 
            // initializing the server, so there must be a shutdown(). The 
            // listeners are already notified from the ConnectionListener of
            // the AutAgentConnection, -> just log.
            log.info(nce.getLocalizedMessage(), nce);
        } catch (ConnectionException ce) {
            // This exception is thrown from AutAgentConnection.getInstance(). See comment above.
            log.info(ce.getLocalizedMessage(), ce);
        } catch (CommunicationException cce) {
            log.error(cce.getLocalizedMessage(), cce);
            // message could not send for any reason, close the connections
            try {
                closeConnections();
            } catch (ConnectionException ce) {
                log.error(Messages.ClosingTheConnectionsFailed, ce);
            }
        }
    }
    
    /**
     * Creates the Map with the autConfig which will be send to the server.
     * @param autConfig the {@link IAUTConfigPO}
     * @return the Map which will be send to the server.
     */
    private Map<String, String> createAutConfigMap(IAUTConfigPO autConfig) {
        final Set<String> autConfigKeys = autConfig.getAutConfigKeys();
        final Map<String, String> mapToSend = 
            new HashMap<String, String>(autConfigKeys.size());
        for (String key : autConfigKeys) {
            String value = autConfig.getValue(key, null);
            mapToSend.put(key, value);
        }
        
        // exists only if the monitoring agent has been selected in the AUT configuration
        final String monitoringID = 
                mapToSend.get(AutConfigConstants.MONITORING_AGENT_ID);
        // add non-rendered monitoring attributes
        if (!StringUtils.isEmpty(monitoringID)) {  
            
            IConfigurationElement monitoringExtension =
                MonitoringRegistry.getElement(monitoringID);
            
            if (monitoringExtension != null) {
                // read all monitoring attributes for the given monitoring id
                List<MonitoringAttribute> attributeList = MonitoringRegistry
                        .getAttributes(monitoringExtension);
                
                for (MonitoringAttribute monitoringAttribute : attributeList) {
                    if (!monitoringAttribute.isRender()) { 
                        mapToSend.put(monitoringAttribute.getId(),
                                monitoringAttribute.getDefaultValue());
                    }
                }
            }
        }
        
        try {
            final Communicator communicator = AutAgentConnection.getInstance()
                .getCommunicator();
            mapToSend.put(AutConfigConstants.AUT_AGENT_PORT,
                String.valueOf(communicator.getPort()));
            mapToSend.put(AutConfigConstants.AUT_AGENT_HOST,
                communicator.getHostName());
            mapToSend.put(AutConfigConstants.AUT_NAME,
                mapToSend.get(AutConfigConstants.AUT_ID));
        } catch (ConnectionException e) {
            log.error(Messages.UnableToAppendAUTAgent);
        }
        return mapToSend;
    }

    /**
     * closes the connections
     * @throws ConnectionException in case of error.
     */
    private void closeConnections() throws ConnectionException {
        AutAgentConnection.getInstance().close();
        AUTConnection.getInstance().close();
    }

    /**
     * {@inheritDoc}
     */
    public void stopAut(AutIdentifier autId) {
        if (log.isInfoEnabled()) {
            log.info(Messages.StoppingTest);            
        }
        try {
            AutAgentConnection.getInstance().getCommunicator().send(
                    new StopAUTServerMessage(autId));
        } catch (ConnectionException ce) {
            // Exceptions thrown from getInstance(): no connections are
            // established, just log
            if (log.isInfoEnabled()) {
                log.info(Messages.ClosingTheConnectionsFailed, ce);
            }
        } catch (CommunicationException e) {
            log.error(Messages.ErrorOccurredWhileTryingToStopAUT, e);
        }
        TestExecution te = TestExecution.getInstance();
        ITestSuitePO startedTestSuite = te.getStartedTestSuite();
        if (startedTestSuite != null && startedTestSuite.isStarted()) {
            startedTestSuite.setStarted(false);
        }
        te.setStartedTestSuite(null);
        if (te.getStartedTestSuite() != null
                && te.getStartedTestSuite().isStarted()) {
            fireEndTestExecution();
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void startObjectMapping(AutIdentifier autId, int mod, 
            int inputCode, int inputType, int modWP, 
            int inputCodeWP, int inputTypeWP) throws ConnectionException, 
            NotConnectedException, CommunicationException {
        
        log.info(Messages.StartingObjectMapping);
        // put the AUTServer into the mode OBJECT_MAPPING via sending a
        // ChangeAUTModeMessage(OBJECT_MAPPING).
        IStatus autConnection = AUTConnection.getInstance().connectToAut(autId, 
                new NullProgressMonitor());
        if (autConnection.getCode() == IStatus.OK) {

            ChangeAUTModeMessage message = new ChangeAUTModeMessage();
            message.setMode(ChangeAUTModeMessage.OBJECT_MAPPING);
            message.setMappingKeyModifier(mod);
            switch (inputType) {
                case InputConstants.TYPE_MOUSE_CLICK:
                    message.setMappingMouseButton(inputCode);
                    message.setMappingKey(InputConstants.NO_INPUT);
                    break;
                case InputConstants.TYPE_KEY_PRESS:
                    // fall through
                default:
                    message.setMappingKey(inputCode);
                    message.setMappingMouseButton(InputConstants.NO_INPUT);
                    break;
            }
            message.setMappingWithParentsKeyModifier(modWP);
            switch (inputTypeWP) {
                case InputConstants.TYPE_MOUSE_CLICK:
                    message.setMappingWithParentsMouseButton(inputCodeWP);
                    message.setMappingWithParentsKey(InputConstants.NO_INPUT);
                    break;
                case InputConstants.TYPE_KEY_PRESS:
                    // fall through
                default:
                    message.setMappingWithParentsKey(inputCodeWP);
                    message.setMappingWithParentsMouseButton(
                            InputConstants.NO_INPUT);
                    break;
            }
            AUTConnection.getInstance().send(message);
        }
            
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void startRecordTestCase(ISpecTestCasePO spec, 
            IWritableComponentNameCache compNamesCache, 
            int recordCompMod, int recordCompKey, 
            int recordApplMod, int recordApplKey,
            int checkModeKeyMod, int checkModeKey, 
            int checkCompKeyMod, int checkCompKey,
            boolean dialogOpen,
            SortedSet<String> singleLineTrigger,
            SortedSet<String> multiLineTrigger) {
        log.info(Messages.StartingRecordModus);
        // put the AUTServer into the mode RECORD_MODE via sending a
        // ChangeAUTModeMessage(RECORD_MODE).
        try {
            ChangeAUTModeMessage message = new ChangeAUTModeMessage();
            message.setMode(ChangeAUTModeMessage.RECORD_MODE);
            message.setMappingKeyModifier(recordCompMod);
            message.setMappingKey(recordCompKey);
            message.setKey2Modifier(recordApplMod);
            message.setKey2(recordApplKey);
            message.setCheckModeKeyModifier(checkModeKeyMod);
            message.setCheckModeKey(checkModeKey);
            message.setCheckCompKeyModifier(checkCompKeyMod);
            message.setCheckCompKey(checkCompKey);
            message.setRecordDialogOpen(dialogOpen);
            message.setSingleLineTrigger(singleLineTrigger);
            message.setMultiLineTrigger(multiLineTrigger);
            ObjectMappingEventDispatcher.setCategoryToCreateIn(null);
                            
            AUTConnection.getInstance().send(message);
            CAPRecordedCommand.setRecSpecTestCase(spec);
            CAPRecordedCommand.setCompNamesCache(compNamesCache);
        } catch (UnknownMessageException ume) {
            fireAUTServerStateChanged(new AUTServerEvent(ume.getErrorId()));
        } catch (NotConnectedException nce) {
            log.error(nce.getLocalizedMessage(), nce);
            // HERE: notify the listeners about unsuccessful mode change
        } catch (CommunicationException ce) {
            log.error(ce.getLocalizedMessage(), ce);
            // HERE: notify the listeners about unsuccessful mode change
        }
    }

    /**
     * {@inheritDoc}
     */
    public void resetToTesting() {
        log.info("setting mode to test"); //$NON-NLS-1$
        // stops the object mapping modew by sending a
        // ChangeAUTModeMessage(TESTING) to the AUTServer and then closing the
        // connection to the AUT.
        CAPRecordedCommand.setRecordListener(null);
        try {
            if (AUTConnection.getInstance().isConnected()) {
                try {
                    ChangeAUTModeMessage message = new ChangeAUTModeMessage();
                    message.setMode(ChangeAUTModeMessage.TESTING);
                    AUTConnection.getInstance().send(message);
                } catch (UnknownMessageException ume) {
                    fireAUTServerStateChanged(new AUTServerEvent(
                            ume.getErrorId()));
                } catch (NotConnectedException nce) {
                    log.error(nce.getLocalizedMessage(), nce);
                    // HERE: notify the listeners about unsuccessful mode
                    // change
                } catch (CommunicationException ce) {
                    log.error(ce.getLocalizedMessage(), ce);
                    // HERE: notify the listeners about unsuccessful mode
                    // change
                }
                AUTConnection.getInstance().close();
            }
        } catch (ConnectionException e) {
            log.error("Error occurred while closing connection to AUT.", e); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    public void startTestSuite(final ITestSuitePO execTestSuite,
            final AutIdentifier autId,
            final boolean autoScreenshot,
            final int iterMax,
            final Map<String, String> externalVars,
            final String noRunOptMode,
            String jobDesc) {
        final String jobName;
        if (jobDesc == null) {
            jobName = NLS.bind(Messages.StartWorkingWithTestSuite,
                    execTestSuite.getName());
        } else {
            jobName = NLS.bind(Messages.StartWorkingWithTestSuite,
                    jobDesc);
        }
        Job runningTestSuite = new Job(jobName) {
            protected IStatus run(IProgressMonitor monitor) {
                monitor.beginTask(jobName,
                        TEST_SUITE_EXECUTION_RELATIVE_WORK_AMOUNT);
                TestExecution.getInstance().setStartedTestSuite(execTestSuite);
                execTestSuite.setStarted(true);
                m_testsuiteStartTime = new Date();
                setTestresultSummary(PoMaker.createTestResultSummaryPO());
                TestExecution.getInstance().executeTestSuite(execTestSuite,
                        autId, autoScreenshot, iterMax, externalVars,
                        getTestresultSummary(), monitor, noRunOptMode);
                if (monitor.isCanceled()) {
                    return Status.CANCEL_STATUS;
                }
                return Status.OK_STATUS;
            }
        };
        runningTestSuite.addJobChangeListener(new JobChangeAdapter() {
            public void done(IJobChangeEvent event) {
                if (event.getResult().matches(IStatus.CANCEL)) {
                    stopTestExecution();         
                }
            }
        }); 
        runningTestSuite.schedule();
    }

    /** {@inheritDoc} */
    public List<INodePO> startTestJob(ITestJobPO testJob,
            boolean autoScreenshot, int iterMax, List<String> skippTSnames,
            String noRunOptMode) {
        TestExecution.getInstance().setStartedTestJob(testJob);
        List<INodePO> executedTestSuites = new ArrayList<INodePO>();
        m_testjobStartTime = new Date();
        try {
            final AtomicBoolean isTestExecutionFailed = 
                new AtomicBoolean(false);
            final AtomicInteger testExecutionMessageId = 
                new AtomicInteger(0);
            final AtomicInteger testExecutionState = 
                new AtomicInteger(0);
            final AtomicBoolean isTestExecutionFinished = 
                new AtomicBoolean(false);
            ITestExecutionEventListener executionListener = 
                    createExecutionListener(
                            isTestExecutionFailed, testExecutionMessageId,
                            testExecutionState, isTestExecutionFinished);
            List<INodePO> refTestSuiteList = 
                testJob.getUnmodifiableNodeList();
            
            for (INodePO node : refTestSuiteList) {
                if (node instanceof ICommentPO) {
                    continue;
                } else if (node instanceof IRefTestSuitePO) {
                    IRefTestSuitePO refTestSuite = (IRefTestSuitePO)node;
                    if (skippTSnames != null 
                            && skippTSnames.contains(refTestSuite.getName())) {
                        continue;
                    }
                    isTestExecutionFailed.set(false);
                    isTestExecutionFinished.set(false);
                    addTestExecutionEventListener(executionListener);
                    AutIdentifier autId = new AutIdentifier(refTestSuite
                            .getTestSuiteAutID());
                    startTestSuite(refTestSuite.getTestSuite(), autId,
                            autoScreenshot, iterMax, null, noRunOptMode, 
                            NodeNameUtil.getText(refTestSuite));
                    while (!isTestExecutionFinished.get()) {
                        TimeUtil.delay(500);
                    }
                    if (!isTestExecutionFailed.get()) {
                        executedTestSuites.add(refTestSuite);
                    }                
                    if (!continueTestJobExecution(testExecutionState, 
                            testExecutionMessageId)) {
                        break;
                    }
                }
            }
        } finally {
            TestExecution.getInstance().setStartedTestJob(null);
        }
        return executedTestSuites;
    }

    /**
     * Creates a test execution listener.
     * @param isTestExecutionFailed flag, which indicates the test is failed
     * @param testExecutionMessageId message id of test execution
     * @param testExecutionState state of the test execution
     * @param isTestExecutionFinished flag, which indicate the test execution is finished
     * @return test execution listener
     */
    private ITestExecutionEventListener createExecutionListener(
            final AtomicBoolean isTestExecutionFailed,
            final AtomicInteger testExecutionMessageId,
            final AtomicInteger testExecutionState,
            final AtomicBoolean isTestExecutionFinished) {
        ITestExecutionEventListener executionListener = 
            new ITestExecutionEventListener() {
                /** {@inheritDoc} */
                public void stateChanged(TestExecutionEvent event) {
                    State state = event.getState();
                    testExecutionState.set(state.ordinal());
                    if (state == State.TEST_EXEC_FAILED) {
                        if (event.getException() instanceof JBException) {
                            JBException e = (JBException)
                                event.getException();
                            testExecutionMessageId.set(e.getErrorId());
                        }
                        isTestExecutionFailed.set(true);
                        testExecutionFinished();
                    }
                }
                /** {@inheritDoc} */
                public void endTestExecution() {
                    testExecutionFinished();
                }
                
                private void testExecutionFinished() {
                    isTestExecutionFinished.set(true);
                    removeTestExecutionEventListener(this);
                }
                @Override
                public void receiveExecutionNotification(
                        String notification) {
                    // nothing
                    
                }
            };
        return executionListener;
    }


    /**
     * @param testExecutionState the test execution state 
     * @param testExecutionMessageId the test execution message id
     * @return whether the test job execution should be stopped or not
     */
    private boolean continueTestJobExecution(AtomicInteger testExecutionState,
            AtomicInteger testExecutionMessageId) {
        int messageID = testExecutionMessageId.get();
        if (messageID == MessageIDs.E_NO_AUT_CONNECTION_ERROR.intValue()
                || messageID == MessageIDs.E_TIMEOUT_CONNECTION.intValue()) {
            return false;
        }
        return testExecutionState.get() != State.TEST_EXEC_STOP.ordinal();
    }

    /**
     * {@inheritDoc}
     */
    public void stopTestExecution() {
        fireTestExecutionChanged(new TestExecutionEvent(
                State.TEST_EXEC_STOP));
        TestExecution.getInstance().stopExecution();
    }

    /**
     * {@inheritDoc}
     */
    public void pauseTestExecution(PauseMode pm) {
        TestExecution.getInstance().pauseExecution(pm);
    }

    /**
     * {@inheritDoc}
     */
    public void addTestEventListener(IAUTEventListener listener) {
        if (log.isInfoEnabled()) {
            log.info(Messages.AddingIAUTEventListener + StringConstants.SPACE
                    + listener.getClass().getName() + StringConstants.COLON
                    + listener.toString());
        }
        eventListenerList.add(IAUTEventListener.class, listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeTestEventListener(IAUTEventListener listener) {
        if (log.isInfoEnabled()) {
            log.info(Messages.RemovingIAUTEventListener + StringConstants.SPACE
                    + listener.getClass().getName() + StringConstants.COLON
                    + listener.toString());
        }
        eventListenerList.remove(IAUTEventListener.class, listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addAutAgentEventListener(
            IServerEventListener listener) {
        eventListenerList.add(IServerEventListener.class, listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeAutAgentEventListener(
            IServerEventListener listener) {

        eventListenerList.remove(IServerEventListener.class, listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addAUTServerEventListener(
            IAUTServerEventListener listener) {
        eventListenerList.add(IAUTServerEventListener.class, listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeAUTServerEventListener(
            IAUTServerEventListener listener) {
        eventListenerList.remove(IAUTServerEventListener.class, listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void addTestExecutionEventListener(
            ITestExecutionEventListener listener) {
        eventListenerList.add(ITestExecutionEventListener.class, listener);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void removeTestExecutionEventListener(
            ITestExecutionEventListener listener) {
        eventListenerList.remove(ITestExecutionEventListener.class, listener);

    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void fireAUTStateChanged(AUTEvent event) {
        if (log.isInfoEnabled()) {
            log.info(Messages.FiringAUTStateChanged + StringConstants.COLON 
                    + String.valueOf(event.getState()));
        }
        // Guaranteed to return a non-null array
        Object[] listeners = eventListenerList.getListenerList();

        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IAUTEventListener.class) {
                ((IAUTEventListener)listeners[i + 1]).stateChanged(event);
            }
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void fireAutAgentStateChanged(AutAgentEvent event) {
        if (log.isInfoEnabled()) {
            log.info(Messages.FiringAUTStateChanged + StringConstants.COLON 
                    + String.valueOf(event.getState()));
        }
        // Guaranteed to return a non-null array
        Object[] listeners = eventListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IServerEventListener.class) {
                ((IServerEventListener)listeners[i + 1]).stateChanged(event);
            }
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void fireAUTServerStateChanged(AUTServerEvent event) {
        if (log.isInfoEnabled()) {
            log.info(Messages.FiringAUTStateChanged + StringConstants.COLON 
                    + String.valueOf(event.getState()));
        }
        // Guaranteed to return a non-null array
        Object[] listeners = eventListenerList.getListenerList();
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == IAUTServerEventListener.class) {
                ((IAUTServerEventListener)listeners[i + 1]).stateChanged(event);
            }
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void fireTestExecutionChanged(TestExecutionEvent event) {
        Object[] listeners = eventListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ITestExecutionEventListener.class) {
                ((ITestExecutionEventListener)listeners[i + 1])
                        .stateChanged(event);
            }
        }
    }
    
    /**
     * if the execution of a test ends normally the execution data from
     * the profiling agent can be collected. For that, a GetMonitoringDataMessage
     * is send to the AutAgent to collect the data.
     */
    public void getMonitoringData() {

        String externalMonitoringDirectory = m_generateMonitoringReport
                ? getMonitoringDirectory() : null;
        GetMonitoringDataMessage message = new GetMonitoringDataMessage(
                TestExecution.getInstance().getConnectedAutId()
                .getExecutableName(), externalMonitoringDirectory);
        try {
            AutAgentConnection.getInstance().send(message);
        } catch (NotConnectedException nce) {
            log.error(nce.getLocalizedMessage(), nce);
        } catch (CommunicationException ce) {
            log.error(ce.getLocalizedMessage(), ce);
        }
         
    }
    /**
     * Many Agents are supporting a mechansim to generate reports 
     * from the execution data, 
     */
    public void buildMonitoringReport() {
        BuildMonitoringReportMessage message = 
            new BuildMonitoringReportMessage(
                TestExecution.getInstance().getConnectedAutId()
                        .getExecutableName());
       
        try {
            AutAgentConnection.getInstance().send(message);
        } catch (NotConnectedException nce) {
            log.error(nce.getLocalizedMessage(), nce);
        } catch (CommunicationException ce) {
            log.error(ce.getLocalizedMessage(), ce);
        }
         
    } 

    /** {@inheritDoc} */
    public Map<String, String> requestAutConfigMapFromAgent(String autId) {
        Map<String, String> autConfigMap = null;
        GetAutConfigMapMessage message = new GetAutConfigMapMessage(autId);
        GetAutConfigMapResponseCommand response = 
            new GetAutConfigMapResponseCommand();
        try {
            AutAgentConnection.getInstance().request(message, response,
                    REQUEST_CONFIG_MAP_TIMEOUT);
            final AtomicBoolean timeoutFlag = new AtomicBoolean(true);
            final Timer timerTimeout = new Timer();
            timerTimeout.schedule(new TimerTask() {
                public void run() {
                    timeoutFlag.set(false);
                    timerTimeout.cancel();
                }
            }, REQUEST_CONFIG_MAP_TIMEOUT);
            while (!response.hasReceivedResponse()
                    && timeoutFlag.get()) {
                TimeUtil.delay(200);
                log.info(Messages.WaitingForAutConfigMapFromAgent);
            }
            autConfigMap = response.getAutConfigMap();
        } catch (NotConnectedException nce) {
            log.error(nce.getLocalizedMessage(), nce);
        } catch (CommunicationException ce) {
            log.error(ce.getLocalizedMessage(), ce);
        }
        return autConfigMap;
    }
    
    /** 
     * {@inheritDoc}
     */
    public void fireEndTestExecution() {
        m_endTime = new Date();
        TestResult result = TestResultBP.getInstance().getResultTestModel(); 
        if (result != null) {            
            createReportJob(result);
        }          
        
        Object[] listeners = eventListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ITestExecutionEventListener.class) {
                ((ITestExecutionEventListener)listeners[i + 1])
                        .endTestExecution();
            }
        } 
    }
    
    /** 
     * {@inheritDoc}
     */
    public void fireReceiveExecutionNotification(String notification) {
    
        Object[] listeners = eventListenerList.getListenerList();
        for (int i = listeners.length - 2; i >= 0; i -= 2) {
            if (listeners[i] == ITestExecutionEventListener.class) {
                ((ITestExecutionEventListener)listeners[i + 1])
                        .receiveExecutionNotification(notification);
            }
        } 
    }
    
    /**
     * creating the job that is building and writing test data to DB. 
     * @param result The test results  
     */
    private void createReportJob(final TestResult result) {
        final AtomicBoolean ab = new AtomicBoolean(false);
        final Job job = new Job (Messages.ClientCollectingInformation) {
            private String m_jobFamily = this.getName();
            public boolean belongsTo(Object family) {
                return m_jobFamily.equals(family);
            } 
            protected IStatus run(IProgressMonitor monitor) {  
                m_isReportRunning.set(true);
                try {
                    monitor.beginTask(Messages.ClientWritingReportToDB,
                            IProgressMonitor.UNKNOWN);
                    writeTestresultToDB(result);
                    if (m_logPath != null) {
                        monitor.beginTask(Messages.ClientWritingReport,
                                IProgressMonitor.UNKNOWN);
                        writeReportToFileSystem(result);
                    }
                    monitor.done();
                    m_isReportRunning.set(false);
                    return Status.OK_STATUS;
                } catch (Throwable t) {
                    // this is due that everything that happens in the job
                    // will otherwise not be logged (like memory Exception)
                    log.error(Messages.ClientWritingReportError, t);
                    m_isReportRunning.set(false);
                    return Status.CANCEL_STATUS;
                }
            }
        }; 
        job.addJobChangeListener(new JobChangeAdapter() {
            public void done(IJobChangeEvent event) {
                if (isRunningWithMonitoring()) {
                    final Job monJob = createMonitoringJob(result);
                    monJob.addJobChangeListener(new JobChangeAdapter() {
                        public void done(IJobChangeEvent changeEvent) {
                            ab.set(true);
                            DataEventDispatcher.getInstance()
                                .fireTestresultSummaryChanged(
                                    getTestresultSummary(),
                                    DataState.Added);
                        }
                    });
                    monJob.schedule();
                    final Timer timerTimeout = new Timer();
                    timerTimeout.schedule(new TimerTask() {
                        public void run() {
                            monJob.cancel();
                            timerTimeout.cancel();
                        }
                    }, MONITORING_REPORT_TIMEOUT);
                } else {
                    ab.set(true);
                    DataEventDispatcher.getInstance()
                        .fireTestresultSummaryChanged(
                            getTestresultSummary(),
                            DataState.Added);
                }
            }
        });
        job.setPriority(Job.LONG);        
        job.schedule();             
        while (!ab.get()) {
            TimeUtil.delay(200);            
        }            
    }
    
    /**
     * Creates the Job which can be scheduled to create the monitoring report
     * @param result the result to create the monitoring report for
     * @return the Job
     */
    private Job createMonitoringJob(final TestResult result) {
        return new Job(Messages.ClientCollectingInformation) {
            private String m_jobFamily = this.getName();

            public boolean belongsTo(Object family) {
                return m_jobFamily.equals(family);
            }

            protected IStatus run(IProgressMonitor monitor) {
                try {
                    monitor.beginTask(Messages.ClientWritingReportToDB,
                            IProgressMonitor.UNKNOWN);
                    monitor.setTaskName(Messages.ClientCalculating);
                    getMonitoringData();
                    while (result.getMonitoringValues() == null
                            || result.getMonitoringValues().isEmpty()) {
                        TimeUtil.delay(500);
                        if (result.getMonitoringValues() != null) {
                            break;
                        }
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                    }
                    
                    if (result.getMonitoringValues().containsKey(
                            MonitoringConstants.MONITORING_ERROR)) {
                        MonitoringValue value = (MonitoringValue) result
                                .getMonitoringValues()
                                .get(MonitoringConstants.MONITORING_ERROR);
                        if (value != null) {
                            fireReceiveExecutionNotification(value.getValue());
                            return Status.OK_STATUS;
                        }
                    }
                    
                    monitor.setTaskName(Messages.ClientBuildingReport);
                    buildMonitoringReport();
                    while (result.getReportData() == null) {
                        TimeUtil.delay(500);
                        if (result.getReportData() 
                                == (MonitoringConstants.EMPTY_REPORT)) {
                            break;
                        }
                        if (monitor.isCanceled()) {
                            return Status.CANCEL_STATUS;
                        }
                    }
                    writeMonitoringResults(result);
                    if (m_logPath != null && m_generateMonitoringReport) {
                        writeMonitoringReportToFile(result);
                    }
               
                    //set the monitoring report in result to null, 
                    //so it can be garbage collected
                    result.setReportData(null);
                    monitor.done();
                    return Status.OK_STATUS;
                } catch (Throwable t) {
                    // this is due that everything that happens in the job
                    // will otherwise not be logged (like memory Exception)
                    log.error(Messages.ClientWritingReportError, t);
                    return Status.CANCEL_STATUS;
                }
            }
        };
    }
    /**
     * writes monitoring results
     * @param result The monitoring results
     */    
    public void writeMonitoringResults(TestResult result) {
        
        ITestResultSummaryPO summary = getTestresultSummary();
        summary.setMonitoringValues(result.getMonitoringValues());

        IMonitoringValue significantValue = findSignificantValue(result
                .getMonitoringValues());
        if (significantValue == null) {
            summary.setMonitoringValue(null);
        } else {
            summary.setMonitoringValue(significantValue.getValue());
            summary.setMonitoringValueType(significantValue.getType());
        }
        summary.setInternalMonitoringId(result.getMonitoringId());
        if (result.getReportData() == MonitoringConstants.EMPTY_REPORT) {
            summary.setReportWritten(false);
        } else {
            summary.setMonitoringReport(new MonitoringReportPO(result
                    .getReportData()));
            summary.setReportWritten(true);
        }
        summary = TestResultSummaryPM.mergeTestResultSummaryInDB(summary); 
        setTestresultSummary(summary);
    }
    
    /**
     * Write Jacoco Monitoring report to file
     * @param result result to create the monitoring report for
     */ 
    public void writeMonitoringReportToFile(TestResult result) {

        byte[] reportData = result.getReportData();
        if (reportData == null || reportData 
                == (MonitoringConstants.EMPTY_REPORT)) {
            return;
        }
        
        final ITestResultSummaryPO testResultSummary = 
                                        getTestresultSummary();
        String theProjName = testResultSummary
                .getTestsuiteName() + "-" //$NON-NLS-1$
                + testResultSummary.getInternalMonitoringId()
                + "-" //$NON-NLS-1$
                + testResultSummary.getId().toString();
        BufferedOutputStream bos = null;
        File tmpZip = null;
        try {
           
            tmpZip = File.createTempFile("tmpReport", ".zip"); //$NON-NLS-1$ //$NON-NLS-2$
            bos = new BufferedOutputStream(new FileOutputStream(tmpZip));
            bos.write(reportData);

            String targetDirName = getMonitoringDirectory() + File.separator 
                    + theProjName;

            ZipUtil.unzip(tmpZip, new File(targetDirName));

        } catch (IOException e) {
            log.error(e.toString());   
        } finally {
            try {
                if (bos != null) {
                    bos.close();
                }
                if (tmpZip != null) {
                    tmpZip.delete();
                }
            } catch (IOException e) {
                log.error("Error while writing zip file to tmp dir", e); //$NON-NLS-1$                        
            }
        }
      
    }
    
    /**
     * @return directory path, where the monitoring reports will be exported.
     */
    private String getMonitoringDirectory() {
        String monitoringDirectory = StringUtils.isNotBlank(m_logPath)
                ? m_logPath + File.separator + "JaCoCo Reports" : null; //$NON-NLS-1$
        return monitoringDirectory;
    }
    
    /**
     * find the significant monitoring value
     * @param map The map containing monitoring values
     * @return The MonitoringValue or null if no significant value was set
     */
    public IMonitoringValue findSignificantValue(
            Map<String, IMonitoringValue> map) {
        
        for (IMonitoringValue value : map.values()) {
            if (value.isSignificant()) {
                return value;
            }
        }
        
        return null;
    }
    
    /**
     * checks if last connected AUT was running with monitoring agent.
     * 
     * @return true if last connected AUT was running with monitoring else false
     */
    private boolean isRunningWithMonitoring() {
        AutIdentifier autID = TestExecution.getInstance().getConnectedAutId();
        if (autID != null) {
            Map<String, String> m = requestAutConfigMapFromAgent(
                    autID.getExecutableName());
            if (m != null) {
                if (MonitoringUtil.shouldAndCanRunWithMonitoring(m)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param result
     *            the result to write
     * @return the id of the persisted test result summary
     */
    public ITestResultSummaryPO writeTestresultToDB(TestResult result) {
        ITestResultSummaryPO summary = getTestresultSummary();
        summary.writeKeyValuePairsIntoAdditionalInformation();
        TestresultSummaryBP instance = TestresultSummaryBP.getInstance();
        instance.fillTestResultSummary(result, summary);

        TestResultSummaryPM.storeTestResultSummaryInDB(summary);
        Long summaryId = summary.getId();
        TestResultPM.storeTestResult(instance.createTestResultDetailsSession(
                result, summaryId));
        return summary;
    }

    /**
     * @param result
     *            the result to write
     */
    public void writeReportToFileSystem(TestResult result) {
        if (result != null && m_logPath != null) {
            AbstractXMLReportGenerator generator = null;
            // Use the appropriate report generator
            // Default is currently Complete
            if (Messages.TestResultViewPreferencePageStyleErrorsOnly
                    .equalsIgnoreCase(m_logStyle)) {

                generator = new ErrorsOnlyXMLReportGenerator(result);
            } else {
                generator = new CompleteXMLReportGenerator(result);
            }
            String filename = createFilename(result);
            writeReport(generator, filename);
            
            ExporterRegistry.exportTestResult(result, m_logPath,
                    filename);
        }
    }

    /**
     * Writes a report to disk using the given ReportGenerator.
     * 
     * @param generator
     *            generates the XML that will be written to disk.
     * @param filenameGenerated 
     */
    private void writeReport(AbstractXMLReportGenerator generator,
            String filenameGenerated) {
        Document document = generator.generateXmlReport();
        String fileName;
        fileName = m_logPath + StringConstants.SLASH 
                + filenameGenerated;  

        try {
            new FileXMLReportWriter(fileName).write(document);
        } catch (IOException e) {
            log.error(Messages.ClientWritingReportError, e);
        }
    }

    /**
     * @param result The Test Result.
     * @return a suitable filename for the given test result model.
     */
    private String createFilename(ITestResult result) {
        if (StringUtils.isNotBlank(m_fileName)) {
            return m_fileName;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(StringConstants.SLASH);
        sb.append(Messages.ExecutionLog);
        sb.append(StringConstants.MINUS);
        sb.append(result.getProjectName());
        sb.append(StringConstants.MINUS);
        TestResultNode testSuiteNode = result.getRootResultNode();
        sb.append(testSuiteNode.getName());
        sb.append(StringConstants.MINUS);

        // Add result of test
        if (testSuiteNode.getStatus() == TestResultNode.SUCCESS) {
            sb.append(TEST_SUCCESSFUL);
        } else {
            sb.append(TEST_FAILED);
        }

        if (new File(m_logPath, sb.toString() + XML_FILE_EXT).exists()) {
            int postfix = 1;
            sb.append(StringConstants.MINUS);
            while (new File(m_logPath, sb.toString()
                    + postfix + XML_FILE_EXT).exists()) {
                postfix++;
            }
            sb.append(postfix);
        }

        return sb.toString();
    }

    /**
     * Initializes the AutAgentConnection and the AUTConnection, in case of an
     * error, the listeners are notified with appropriate ServerEvent.
     * @param serverName The name of the server.
     * @param port The port number.
     * @throws JBVersionException in case of a version error between Client
     * and AutStarter
     * @return false if an error occurs, true otherwise
     */
    private boolean initServerConnection(String serverName, String port) 
        throws JBVersionException {
        
        try {
            AutAgentConnection.createInstance(serverName, port);
            final AutAgentConnection instance = 
                AutAgentConnection.getInstance();
            instance.getCommunicator().addCommunicationErrorListener(
                new AUTAgentConnectionListener());
            instance.run();
            if (log.isDebugEnabled()) {
                log.debug(Messages.ConnectedToTheServer);
            }
        } catch (ConnectionException ce) {
            log.error(ce.getLocalizedMessage(), ce);
            return false;
        } catch (BaseConnection.AlreadyConnectedException ae) {
            // The connection is already established.
            if (log.isDebugEnabled()) {
                log.debug(ae.getLocalizedMessage(), ae);
            }
            return false;
        }
        return true;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Date getEndTime() {
        return m_endTime;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Date getTestsuiteStartTime() {
        return m_testsuiteStartTime;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Date getTestjobStartTime() {
        return m_testjobStartTime;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setLogPath(String logPath) {
        m_logPath = logPath;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void setLogStyle(String logStyle) {
        m_logStyle = logStyle;
    }
    
    @Override
    public void setGenerateMonitoringReport(Boolean generateMonitoringReport) {
        this.m_generateMonitoringReport = generateMonitoringReport;
    }

    /** {@inheritDoc} */
    public ITestResultSummaryPO getTestresultSummary() {
        return m_summary;
    }
    
    /**
     * @param summary The Test Result Summary to set.
     */
    private void setTestresultSummary(ITestResultSummaryPO summary) {
        m_summary = summary;
    }

    /**
     * {@inheritDoc}
     */
    public void setRelevantFlag(boolean relevant) {
        m_relevant = relevant;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRelevant() {
        return m_relevant;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPauseTestExecutionOnError() {
        return m_pauseOnError;
    }

    /**
     * {@inheritDoc}
     */
    public void pauseTestExecutionOnError(boolean pauseOnError) {
        m_pauseOnError = pauseOnError;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setScreenshotXMLFlag(boolean screenshotXml) {
        m_xmlScrennshot = screenshotXml;        
    }

    /**
     * {@inheritDoc}
     */
    public boolean isScreenshotForXML() {
        return m_xmlScrennshot;
    }

    /**
     * {@inheritDoc}
     */
    public void setFileName(String fileName) {
        m_fileName = fileName;
    }
    
    /**
     * The listener listening to the communicator.
     *
     * @author BREDEX GmbH
     * @created 12.08.2004
     */
    private class AUTAgentConnectionListener implements
        ICommunicationErrorListener {

        /**
         * {@inheritDoc}
         */
        public void connectionGained(InetAddress inetAddress, int port) {
            if (log.isInfoEnabled()) {
                try {
                    String logMessage = "connected to "  //$NON-NLS-1$
                        + inetAddress.getHostName() 
                        + StringConstants.COLON + String.valueOf(port);
                    log.info(logMessage);
                } catch (SecurityException se) {
                    log.debug("security violation while getting the host name from IP-address"); //$NON-NLS-1$
                }
            }
            fireAutAgentStateChanged(
                new AutAgentEvent(ServerEvent.CONNECTION_GAINED));
        }

        /**
         * {@inheritDoc}
         */
        public void shutDown() {
            log.info("connection to AUT Agent closed"); //$NON-NLS-1$
            log.info("closing connection to the AUTServer"); //$NON-NLS-1$
            
            try {
                AUTConnection.getInstance().close();
                fireAutAgentStateChanged(
                        new AutAgentEvent(ServerEvent.CONNECTION_CLOSED));
            } catch (ConnectionException ce) {
                // the connection to the AUTServer is not established
                // -> just log this
                log.debug(ce.getLocalizedMessage(), ce);
            }
        }

        /**
         * {@inheritDoc}
         */
        public void sendFailed(Message message) {
            log.warn("sending message failed:"  //$NON-NLS-1$
                + message.toString());
            log.info("closing connection to the AUT Agent"); //$NON-NLS-1$
            
            try {
                AutAgentConnection.getInstance().close();
            } catch (ConnectionException e) {
                log.warn(e.getLocalizedMessage());
            }
        }

        /**
         * {@inheritDoc}
         */
        public void acceptingFailed(int port) {
            log.error("accepting failed() called although this is a 'client':" //$NON-NLS-1$
                + String.valueOf(port));
        }

        /**
         * {@inheritDoc}
         */
        public void connectingFailed(InetAddress inetAddress, int port) {
            log.warn("connecting the AUT Agent failed"); //$NON-NLS-1$
            fireAutAgentStateChanged(new AutAgentEvent(
                    AutAgentEvent.SERVER_CANNOT_CONNECTED));
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReportingRunning() {
        return m_isReportRunning.get();
    }

    /** {@inheritDoc} */
    public boolean isTrimming() {
        return m_trimming;
    }

}