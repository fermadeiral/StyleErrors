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
package org.eclipse.jubula.app.testexec.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringWriter;
import java.rmi.UnknownHostException;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.Validate;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jubula.app.testexec.i18n.Messages;
import org.eclipse.jubula.autagent.AutStarter;
import org.eclipse.jubula.autagent.AutStarter.Verbosity;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;
import org.eclipse.jubula.client.cmd.JobConfiguration;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent.RegistrationStatus;
import org.eclipse.jubula.client.core.agent.IAutRegistrationListener;
import org.eclipse.jubula.client.core.businessprocess.ExternalTestDataBP;
import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.TestExecution;
import org.eclipse.jubula.client.core.businessprocess.TestExecution.PauseMode;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.businessprocess.TestResultReportNamer;
import org.eclipse.jubula.client.core.businessprocess.compcheck.CompletenessGuard;
import org.eclipse.jubula.client.core.businessprocess.problems.IProblem;
import org.eclipse.jubula.client.core.businessprocess.problems.ProblemFactory;
import org.eclipse.jubula.client.core.constants.TestExecutionConstants;
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.events.AutAgentEvent;
import org.eclipse.jubula.client.core.events.IAUTEventListener;
import org.eclipse.jubula.client.core.events.IAUTServerEventListener;
import org.eclipse.jubula.client.core.events.IServerEventListener;
import org.eclipse.jubula.client.core.events.ServerEvent;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecStackModificationListener;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.NodeNameUtil;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.internal.AutAgentConnection;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.JBVersionException;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.jubula.tools.internal.utils.FileUtils;
import org.eclipse.jubula.tools.internal.utils.IsAliveThread;
import org.eclipse.jubula.tools.internal.utils.NetUtil;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.eclipse.osgi.util.NLS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This controller offers methods to create batch jobs from file
 * and to execute jobs.
 *
 * @author BREDEX GmbH
 * @created Mar 29, 2006
 */
public class ExecutionController implements IAUTServerEventListener,
        IServerEventListener, IAUTEventListener, ITestExecutionEventListener, 
        IAutRegistrationListener {
    /**
     * @author BREDEX GmbH
     * @created Oct 16, 2009
     */
    private final class WatchdogTimer extends IsAliveThread {

        /** when should the run be finished? */
        private long m_stoptime;
        
        /** should the time stop */
        private boolean m_abort = false;
        
        /**
         * @param timeout Time in seconds the watchdog should wait before
         * aborting the run.
         */
        public WatchdogTimer(int timeout) {
            super(Messages.WatchdogTimer);
            setDaemon(true);
            m_stoptime = new Date().getTime();
            m_stoptime += timeout * 1000;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        @Override
        public void run() {
            do {
                TimeUtil.delay(1000);
                if (m_abort) {
                    return;
                }
            } while (new Date().getTime() < m_stoptime);

            sysErr(Messages.ExecutionControllerAbort);

            ClientTest.instance().stopTestExecution();
            stopProcessing();
            
            // wait 30 seconds, then exit the whole program
            TimeUtil.delay(30000);
            if (!m_abort) {
                System.exit(1);
            }
        }

        /**
         * abort this watchdog
         */
        public void abort() {
            m_abort = true;
            this.interrupt();
        }
    }

    /**
     * @author BREDEX GmbH
     */
    private final class CollectAllErrorsOperation 
        extends AbstractNonPostOperatingTreeNodeOperation<INodePO> {
        /** the errors to show */
        private Set<IProblem> m_errorsToShow = new HashSet<IProblem>();

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<INodePO> ctx,
                INodePO parent, INodePO node, boolean alreadyVisited) {
            if (ProblemFactory.hasProblem(node)) {
                for (IProblem problem : node.getProblems()) {
                    if (problem.getStatus().getSeverity() == IStatus.ERROR) {
                        getErrorsToShow().add(problem);
                    }
                }
            }
            return node.isActive();
        }
        
        /** @return the m_errorsToShow */
        public Set<IProblem> getErrorsToShow() {
            return m_errorsToShow;
        }
    }
    
    /** 
     * Name of the environment variable that defines the time the client should
     * wait during AUT startup process.
     */
    private static final String AUT_STARTUP_DELAY_VAR = "TEST_AUT_STARTUP_DELAY"; //$NON-NLS-1$
    
    /**
     * default time to wait during startup process 
     */
    private static final int AUT_STARTUP_DELAY_DEFAULT = 5000;
    
    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(ExecutionController.class);
    
    /** instance of controller */
    private static ExecutionController instance;

    /** configuration of desired job */
    private JobConfiguration m_job;
    
    /** true if client is processing a job */
    private boolean m_idle = false;

    /** true if ITE is currently executing a test suite */
    private boolean m_isTestSuiteRunning = false;
    
    /** true if this is the first time the AUT is being started for the current test suite */
    private boolean m_isFirstAutStart = true;
    
    /** true if test should be reported as successful */
    private boolean m_noErrorWhileExecution = true;
    
    /** true if client sends a shutdown command to end test execution */
    private boolean m_shutdown = false;

    /** 
     * true if fatal error occurred, and processing of the batch process must
     * be stopped
     */
    private boolean m_stopProcessing = false;
    
    /** process for watching test execution */
    private TestExecutionWatcher m_progress = new TestExecutionWatcher();

    /** the ID of the AUT that was started for test execution */
    private AutIdentifier m_startedAutId = null;
    
    /** private constructor */
    private ExecutionController() {
        IClientTest clientTest = ClientTest.instance();
        clientTest.addAUTServerEventListener(this);
        clientTest.addAutAgentEventListener(this);
        clientTest.addTestEventListener(this);
        clientTest.addTestExecutionEventListener(this);
        AutAgentRegistration.getInstance().addListener(this);
    }
    
    /**
     * Method to get the single instance of this class.
     * @return the instance of this Singleton
     */
    public static ExecutionController getInstance() {
        if (instance == null) {
            instance = new ExecutionController();
        }
        return instance;
    }
    
    /**
     * creates the job passed to command Line client
     * 
     * @param configFile
     *            File
     * @throws IOException
     *             Error
     * @return a job configuration
     */
    public JobConfiguration initJob(File configFile) throws IOException {
        if (configFile != null) {
            // Create JobConfiguration from XMl
            BufferedReader in = null;
            StringWriter writer = new StringWriter();
            try {
                in = new BufferedReader(new FileReader(configFile));
                String line = null;
                while ((line = in.readLine()) != null) {
                    writer.write(line);
                }
            } finally {
                if (in != null) {
                    in.close();
                }
            }
            String xml = writer.toString();    
            m_job = JobConfiguration.readFromXML(xml);
        } else {
            // or create an empty JobConfiguration
            m_job = new JobConfiguration();
        }
        return m_job;
    }
    
    /**
     * executes the complete test
     * @throws CommunicationException Error
     * @return boolean true if all testsuites completed successfully 
     * or if test execution was successful up to specified mode of no-run option
     */
    public boolean executeJob() throws CommunicationException {
        String noRun = m_job.getNoRunOptMode();
        if (AbstractCmdlineClient.isNoRun()) {
            sysOut(StringConstants.TAB
                    + NLS.bind(Messages.ExecutionControllerNoRunExecutionBegin,
                            TestExecutionConstants.RunSteps.valueOf(noRun.
                                    toUpperCase()).getDescription()));
        }
        // start the watchdog timer
        WatchdogTimer timer = null;
        if (m_job.getTimeout() > 0) {        
            timer = new WatchdogTimer(m_job.getTimeout());
            timer.start();
        }
        IClientTest clientTest = ClientTest.instance();
        //connection to AUT Agent
        if (!prepareAUTAgentConnection(clientTest)) {
            return false;
        } else if (TestExecution.shouldExecutionStop(noRun,
                TestExecutionConstants.RunSteps.CAA)) {
            return true;
        }
        // set monitoring report generation
        clientTest.setGenerateMonitoringReport(
                m_job.isGenerateMonitoringReport());
        clientTest.setScreenshotXMLFlag(m_job.isXMLScreenshot());
        //prepare connection to the DB
        prepareDBConnection();
        if (TestExecution.shouldExecutionStop(noRun,
                TestExecutionConstants.RunSteps.CDB)) {
            return true;
        }
        // load project
        loadProject();
        if (TestExecution.shouldExecutionStop(noRun,
                TestExecutionConstants.RunSteps.LP)) {
            return true;
        }
        //check the completeness of the test
        checkTestCompleteness();
        if (TestExecution.shouldExecutionStop(noRun,
                TestExecutionConstants.RunSteps.CC)) {
            return true;
        }
        // start AUT, working will be set false, after AUT started
        m_idle = true;
        // ends testexecution if shutdown command was received from the client
        if (m_shutdown) {
            sysOut(Messages.ReceivedShutdownCommand);
            endTestExecution();
        }
        try {
            //start AUT and check that it was started
            ensureAutIsStarted(m_job.getActualTestSuite(), 
                  m_job.getAutConfig());
            if (TestExecution.shouldExecutionStop(noRun,
                    TestExecutionConstants.RunSteps.SA)) {
                return true;
            }
            //start of the test execution
            doTest(m_job.getTestJob() != null);
        } catch (ToolkitPluginException e1) {
            sysErr(NLS.bind(Messages.ExecutionControllerAUT,
                Messages.ErrorMessageAUT_TOOLKIT_NOT_AVAILABLE));
        }
        if (timer != null) {
            timer.abort();
        }
        return isNoErrorWhileExecution();
    }

    /**
     * calls the test job or test suite execution depending on what was given as an testexec option
     * @param testJobIsSpecified boolean is true if test job option was specified in the command line
     */
    private void doTest(boolean testJobIsSpecified) {
        if (testJobIsSpecified) {
            doTestJob();
            return;
        }
        doTestSuite();
    }

    /**
     * starts the embedded AUT Agent
     * @param port the port number of the embedded AUT Agent
     * @throws CommunicationException in case of failure to connect to embedded AUT Agent
     * @return true if embedded Agent was started successfully and false otherwise
     */
    private boolean startEmbeddedAutAgent(int port)
        throws CommunicationException {
        AutStarter autAgentInstance = AutStarter.getInstance();
        if (autAgentInstance.getCommunicator() == null) {
            // Embedded Agent is not running. We need to start it before
            // trying to connect to it.
            try {
                sysOut(I18n.getString("AUTAgent.EmbeddedAUTAgentStart",  //$NON-NLS-1$
                        new String[] {String.valueOf(port)}));
                autAgentInstance.start(
                        port, false, Verbosity.QUIET, false);
                return true;
            } catch (UnknownHostException uhe) {
                LOG.error(uhe.getLocalizedMessage(), uhe);
                sysErr(StringConstants.TAB + NLS.bind(
                        Messages.ExecutionControllerAUTStartUnknownHost,
                        uhe.getLocalizedMessage()));
            } catch (JBVersionException e) {
                LOG.error(e.getLocalizedMessage(), e);
                sysErr(StringConstants.TAB + NLS.bind(
                        Messages.ExecutionControllerAUTStartVersionConflict,
                        e.getErrorMessagesString()));
            } catch (IOException e) {
                LOG.error(e.getLocalizedMessage(), e);
                sysErr(StringConstants.TAB + NLS.bind(
                        Messages.ExecutionControllerAUTStartFailed,
                        e.getMessage()));
            }
        }
        return false;
    }

    /**
     * execute a test suite
     */
    private void doTestSuite() {
        // executing batch of test suites
        while (m_job.getActualTestSuite() != null 
                && !m_stopProcessing) {
            while (m_idle && !m_stopProcessing) {
                TimeUtil.delay(50);
            }
            if (m_job.getActualTestSuite() != null && !m_stopProcessing
                    && !m_idle && !m_isFirstAutStart) {
                ITestSuitePO ts = m_job.getActualTestSuite();
                m_idle = true;
                sysOut(StringConstants.TAB
                        + NLS.bind(Messages.ExecutionControllerTestSuiteBegin,
                                ts.getName()));
                ClientTest.instance().startTestSuite(
                        ts,
                        m_startedAutId != null ? m_startedAutId : m_job
                                .getAutId(), m_job.isAutoScreenshot(),
                        m_job.getIterMax(), null,
                        m_job.getNoRunOptMode(), null);
            }
        }
        waitForReportingToFinish(TimeoutConstants
                .CLIENT_REPORTING_AFTER_FAILURE_TIMEOUT);
    }

    /**
     * run a test job
     */
    private void doTestJob() {
        String tjName = m_job.getTestJob().getName();
        sysOut(NLS.bind(Messages.ExecutionControllerTestJobBegin, tjName));
        sysOut(NLS.bind(Messages.ExecutionControllerTestJobExpectedTestSuites,
                new Object[] { tjName,
                                m_job.getTestJob().getNodeListSize()}));
        List<INodePO> executedTestSuites = ClientTest.instance().startTestJob(
                m_job.getTestJob(), m_job.isAutoScreenshot(),
                m_job.getIterMax(), m_job.getIncompleteTSs(),
                m_job.getNoRunOptMode());
        sysOut(NLS.bind(Messages.ExecutionControllerTestJobExecutedTestSuites,
                new Object[] { tjName,
                        executedTestSuites.size()}));
        Iterator<INodePO> tsIterator = m_job.getTestJob().getNodeListIterator();
        waitForReportingToFinish(TimeoutConstants
                .CLIENT_REPORTING_AFTER_FAILURE_TIMEOUT);
        while (tsIterator.hasNext()) {
            INodePO testsuite = tsIterator.next();
            if (!executedTestSuites.contains(testsuite)) {
                sysErr(NLS.bind(Messages.
                        ExecutionControllerTestJobUnsuccessfulTestSuites,
                        new Object[] { tjName,
                                testsuite.getName()}));
            }
        }

    }

    /**
     * waits for the reporting job to finish.
     * @param timeout timeout in milliseconds
     */
    private void waitForReportingToFinish(long timeout) {
        long endtime = System.currentTimeMillis() + timeout;
        TimeUtil.delay(500); // wait for reporting job to start
        while (ClientTest.instance().isReportingRunning()) {
            TimeUtil.delay(250);
            if (endtime - System.currentTimeMillis() < 0) {
                return;
            }
        }
    }
    
    /**
     * end processing and notify any waiting CLC service threads
     */
    private void stopProcessing() {
        m_stopProcessing = true;
    }
    
    /** Prepares the test execution by:
     *   <p> * starting of the AUT Agent
     *   <p> * initializing AUT Agent connection
     *   and checks if these steps were successful
     *   @param clientTest the clientTest instance
     *   @throws CommunicationException Error in case of connection to AUT Agent failure
     *   @return boolean true if connection to the AUT Agent was successful
     */
    private boolean prepareAUTAgentConnection(IClientTest clientTest)
        throws CommunicationException {
        int autAgentPortNumber = m_job.getPort();
        if (StringUtils.isEmpty(m_job.getServer())) {
            // if "port" parameter for testexec was not given (in command line and/or in configuration file) 
            // port number equals 0 by default and any free port should be used for embedded AUT Agent in this case
            if (autAgentPortNumber == 0) {
                autAgentPortNumber = NetUtil.getFreePort();
            }
            //the "server" parameter (autAgentHostName) for testexec is set to "localhost"
            m_job.setEmbeddedAutAgentHostName();
            if (!startEmbeddedAutAgent(autAgentPortNumber)) {
                endTestExecution();
                return false;
            } 
        } 
        String autAgentHostName = m_job.getServer();
        sysOut(NLS.bind(Messages.ConnectingToAUTAgent,
            new Object[] { autAgentHostName, autAgentPortNumber }));
        // init ClientTestImpl
        clientTest.connectToAutAgent(autAgentHostName, 
                String.valueOf(autAgentPortNumber));
        if (!AutAgentConnection.getInstance().isConnected()) {
            throw new CommunicationException(
                    Messages.ConnectionToAUTAgentFailed,
                    MessageIDs.E_COMMUNICATOR_CONNECTION);
        }
        return true;
    }
    
    /**
     * prepares the test execution by initializing database connection
     */
    private void prepareDBConnection() {
        // setting LogDir , resource/html must be in classpath
        setLogDir();
        // set data dir for external data
        ExternalTestDataBP.setDataDir(new File(m_job.getDataDir()));
        // init Persistor
        // Persistence (JPA / EclipseLink).properties and mapping files
        // have to be in classpath
        Persistor.setDbConnectionName(m_job.getDbscheme());
        Persistor.setUser(m_job.getDbuser());
        Persistor.setPw(m_job.getDbpw());
        Persistor.setUrl(m_job.getDb());
        if (!Persistor.init()) {
            throw new IllegalArgumentException(Messages.
                    ExecutionControllerInvalidDBDataError, null);
        }
    }

    /**
     * sets the log Directory
     */
    private void setLogDir() {
        if (StringUtils.isNotEmpty(m_job.getResultDir())) {
            Validate.isTrue(FileUtils.isValidPath(m_job.getResultDir()),
                    Messages.ExecutionControllerLogPathError);
            ClientTest.instance().setLogPath(m_job.getResultDir());
        }
        if (StringUtils.isNotBlank(m_job.getFileName())) {
            String filePath = m_job.getResultDir() + StringConstants.SLASH
                    + m_job.getFileName();
            boolean isWritable = FileUtils.isWritableFile(filePath
                    + TestResultReportNamer.FILE_EXTENSION_XML);
            if (isWritable) {
                isWritable = FileUtils.isWritableFile(filePath
                        + TestResultReportNamer.FILE_EXTENSION_HTML);
            }
            Validate.isTrue(isWritable,
                    Messages.ExecutionControllerResultNameError);

            ClientTest.instance().setFileName(m_job.getFileName());
        }
    }

    /**
     * starts the AUT
     * @param ts the Test Suite which will be started
     * @param autConf configuration for this AUT
     */
    private void ensureAutIsStarted(ITestSuitePO ts, IAUTConfigPO autConf) 
        throws ToolkitPluginException {
        if (ts != null && autConf != null) {
            final IAUTMainPO aut = ts.getAut();
            
            if (ts != null) {
                AutIdentifier autToStart = new AutIdentifier(autConf
                        .getConfigMap().get(AutConfigConstants.AUT_ID));
                AUTStartListener asl = new AUTStartListener(autToStart);
                IClientTest clientTest = ClientTest.instance();
                clientTest.addTestEventListener(asl);
                clientTest.addAUTServerEventListener(asl);
                AutAgentRegistration.getInstance().addListener(asl);
                sysOut(NLS.bind(Messages.ExecutionControllerAUT,
                        NLS.bind(Messages.ExecutionControllerAUTStart,
                                aut.getName(), autConf.getName())));
                clientTest.startAut(aut, autConf);
                m_startedAutId = autToStart;
           
                while (!asl.autStarted() && !asl.hasAutStartFailed()) {
                    TimeUtil.delay(500);
                }
                
                waitExternalTime();
            }
        } else {
            sysOut(Messages.ExecutionControllerAUTIsPossiblyStarted);
            // assume that the AUT has already been started via e.g. autrun
            m_idle = false;
            m_isFirstAutStart = false;
        }
    }

    /**
     * this method delays the test execution start during AUT startup
     */
    private void waitExternalTime() {
        TimeUtil.delayDefaultOrExternalTime(AUT_STARTUP_DELAY_DEFAULT,
                AUT_STARTUP_DELAY_VAR);
    }

    /**
     * @author BREDEX GmbH
     * @created 24.08.2009
     */
    public class AUTStartListener implements IAUTEventListener, 
        IAUTServerEventListener, IAutRegistrationListener {
        /** flag to indicate that the AUT has been successfully started */
        private boolean m_autStarted = false;
        
        /** flag to indicate that the AUT start has failed*/
        private boolean m_autStartFailed = false;
        
        /** timer to set autStartFailed to true after a certain amount of time */
        private Timer m_startFailedTimer = new Timer();
        
        /** startup timeout: 5 minutes */
        private long m_autStartTimeout = 5 * 60 * 1000;

        /** ID of the AUT that should be started */
        private AutIdentifier m_autToStart;
        
        /** 
         * Constructor
         * 
         * @param autToStart ID of the AUT that should be started.
         */
        public AUTStartListener(AutIdentifier autToStart) {
            m_autToStart = autToStart;
            m_startFailedTimer.schedule(new TimerTask() {
                public void run() {
                    setAutStartFailed(true);
                    removeListener();
                }
            }, m_autStartTimeout);
        }
        
        /** @return the hasBeenNotified */
        public synchronized boolean autStarted() {
            return m_autStarted;
        }

        /** {@inheritDoc} */
        public synchronized void stateChanged(AUTEvent event) {
            switch (event.getState()) {
                case AUTEvent.AUT_STARTED:
                    m_autStarted = true;
                    dispose();
                    break;
                default:
                    break;
            }
        }

        /** @return the autStartFailed */
        public synchronized boolean hasAutStartFailed() {
            return m_autStartFailed;
        }

        /** {@inheritDoc} */
        public void stateChanged(AUTServerEvent event) {
            switch (event.getState()) {
                case AUTServerEvent.COMMUNICATION:
                case AUTServerEvent.COULD_NOT_ACCEPTING:
                case AUTServerEvent.DOTNET_INSTALL_INVALID:
                case AUTServerEvent.INVALID_JAR:
                case AUTServerEvent.INVALID_JAVA:
                case AUTServerEvent.JDK_INVALID:
                case AUTServerEvent.NO_MAIN_IN_JAR:
                case AUTServerEvent.SERVER_NOT_INSTANTIATED:
                case ServerEvent.CONNECTION_CLOSED:
                    setAutStartFailed(true);
                    dispose();
                    break;
                default:
                    break;
            }
        }

        /**
         * @param autStartFailed the autStartFailed to set
         */
        protected synchronized void setAutStartFailed(boolean autStartFailed) {
            m_autStartFailed = autStartFailed;
        }
        
        /** dispose this listener and stop all running tasks */
        private void dispose () {
            m_startFailedTimer.cancel();
            removeListener();
        }
        
        /** remove listener */
        protected void removeListener() {
            IClientTest clientTest = ClientTest.instance();
            clientTest.removeTestEventListener(this);
            clientTest.removeAUTServerEventListener(this);
            AutAgentRegistration.getInstance().removeListener(this);
        }

        /**
         * {@inheritDoc}
         */
        public void handleAutRegistration(AutRegistrationEvent event) {
            if (event.getAutId().equals(m_autToStart)
                    && event.getStatus() == RegistrationStatus.Register) {
                m_autStarted = true;
                dispose();
            }
        }
    }
    
    /**
     * loads a project
     */
    private void loadProject() {
        sysOut(Messages.ExecutionControllerDatabase
                + NLS.bind(Messages.ExecutionControllerLoadingProject,
                    new Object[] { m_job.getProjectName(),
                                  m_job.getProjectVersion() }));
        try {
            IProjectPO actualProject = 
                ProjectPM.loadProjectByNameAndVersion(m_job.getProjectName(), 
                    m_job.getProjectVersion());
            if (actualProject != null) {
                ProjectPM.loadProjectInROSession(actualProject);
                final IProjectPO currentProject = GeneralStorage.getInstance()
                    .getProject();
                m_job.setProject(currentProject);
                sysOut(Messages.ExecutionControllerDatabase
                    + NLS.bind(Messages.ExecutionControllerProjectLoaded,
                            m_job.getProjectName()));
            }
            m_job.checkProjectExistence();
        } catch (JBException e1) {
            /* An exception was thrown while loading data or closing a session
             * using Persistence (JPA / EclipseLink). The project is never set. This is detected
             * during job validation (initAndValidate). */
        }
        
    }

    /**
     * checks the completeness of the test
     */
    private void checkTestCompleteness() {
        sysOut(Messages.ExecutionControllerProjectCompleteness);
        m_job.initAndValidate();
        boolean noErrors = true;
        Set<ITestSuitePO> distinctListOfTs = new HashSet<ITestSuitePO>();
        distinctListOfTs.addAll(m_job.getTestSuites());
        for (ITestSuitePO ts : distinctListOfTs) {
            boolean noError = true;
            CompletenessGuard.checkAll(ts,
                    new NullProgressMonitor());
            sysOut(NLS.bind(Messages.ExecutionControllerTestSuiteCompleteness,
                    ts.getName()));
            final CollectAllErrorsOperation op = 
                    new CollectAllErrorsOperation();
            TreeTraverser traverser = new TreeTraverser(ts, op);
            traverser.traverse(true); 
            for (IProblem problem : op.getErrorsToShow()) {
                if (problem.hasUserMessage()) {
                    sysOut(problem.getUserMessage());
                }
                noError = false;
                m_job.removeTestSuites(ts);
            }
            if (noError) {
                sysOut(Messages.ExecutionControllerTestSuiteCompletenessOk);
            } else {
                sysOut(Messages.ExecutionControllerTestSuiteCompletenessNOk);
                noErrors = false;
            }
        }
        if (!noErrors && m_job.isExecuteJobsPartly()) {
            sysOut(NLS.bind(
                    Messages.ExecutionControllerTestSuiteCompletenessNOkSkipp,
                    StringUtils.join(m_job.getIncompleteTSs(),
                            StringConstants.COMMA + StringConstants.SPACE)));
        } else {
            Validate.isTrue(noErrors, 
                    Messages.ExecutionControllerProjectCompletenessFailed);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(AUTServerEvent event) {
        switch (event.getState()) {
            case AUTServerEvent.INVALID_JAR:
                sysErr(Messages.ExecutionControllerInvalidJarError);
                stopProcessing();
                m_idle = false;
                break;
            case AUTServerEvent.INVALID_JAVA:
                sysErr(Messages.ExecutionControllerInvalidJREError);
                stopProcessing();
                m_idle = false;
                break;
            case AUTServerEvent.SERVER_NOT_INSTANTIATED:
                sysErr(Messages.ExecutionControllerServerNotInstantiated);
                stopProcessing();
                m_idle = false;
                break;
            case AUTServerEvent.NO_MAIN_IN_JAR:
                sysErr(Messages.ExecutionControllerInvalidMainError);
                stopProcessing();
                m_idle = false;
                break;
            case AUTServerEvent.COMMUNICATION:
                sysErr(event.toStringWithAdditionalInformation());
                stopProcessing();
                m_idle = false;
                break;
            case AUTServerEvent.COULD_NOT_ACCEPTING:
                sysErr(Messages.ExecutionControllerAUTStartError);
                stopProcessing();
                m_idle = false;
                break;
            case ServerEvent.CONNECTION_CLOSED:
                stopProcessing();
                m_idle = false;
                break;
            case AUTServerEvent.DOTNET_INSTALL_INVALID:
                sysErr(Messages.ExecutionControllerDotNetInstallProblem);
                stopProcessing();
                m_idle = false;
                break;       
            case AUTServerEvent.JDK_INVALID:
                sysErr(Messages.ExecutionControllerInvalidJDKError);
                break;
            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(AutAgentEvent event) {
        sysOut(NLS.bind(Messages.ExecutionControllerServer, event));
        switch (event.getState()) {
            case ServerEvent.CONNECTION_CLOSED:
                break;
            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void stateChanged(AUTEvent event) {
        switch (event.getState()) {
            case AUTEvent.AUT_STARTED:
                sysOut(NLS.bind(Messages.ExecutionControllerAUT,
                    Messages.ExecutionControllerAUTConnectionEstablished));
                break;
            case AUTEvent.AUT_CLASS_VERSION_ERROR:
            case AUTEvent.AUT_MAIN_NOT_FOUND:
            case AUTEvent.AUT_NOT_FOUND:
            case AUTEvent.AUT_ABORTED:
            case AUTEvent.AUT_START_FAILED:
                sysErr(Messages.ExecutionControllerAUTStartError);
                stopProcessing();
                break;
            case AUTEvent.AUT_STOPPED:
                if (m_isTestSuiteRunning) {
                    sysErr(Messages.ExecutionControllerAUTConnectionLost);
                    ClientTest.instance().stopTestExecution();
                } else {
                    sysOut(NLS.bind(Messages.ExecutionControllerAUT,
                        Messages.ExecutionControllerAUTDisconnected));
                }
                stopProcessing();
                break;
            case AUTEvent.AUT_RESTARTED:
                return;
            default:
                break;
        }
        // generally do not do this, if AUT-Restart-Action is executed!
        if (m_isFirstAutStart) {
            m_idle = false;
            m_isFirstAutStart = false;
        }

    }

    /**
     * Writes an output to console
     * 
     * @param text
     *            the text to write
     */
    private void sysOut(String text) {
        AbstractCmdlineClient.printConsoleLn(text, true);
    }
    
    /**
     * Writes an output to console
     * 
     * @param text
     *            the message to log and println to sys.err
     */
    private void sysErr(String text) {
        AbstractCmdlineClient.printlnConsoleError(text);
    }
    
    /**
     * {@inheritDoc}
     */
    public void stateChanged(TestExecutionEvent event) {
        Exception exception = event.getException();
        if (exception instanceof JBException) {
            StringBuilder errorMsgBuilder = new StringBuilder(
                    exception.getMessage());
            if (((JBException) exception)
                    .getErrorId() == MessageIDs.E_NO_AUT_CONNECTION_ERROR
                    && m_job.getAutConfig() == null) {
                errorMsgBuilder.append(StringConstants.NEWLINE);
                errorMsgBuilder.append(StringConstants.TAB);
                errorMsgBuilder.append(
                        Messages.
                            ExecutionControllerCouldNotConnectToAUTWithAutrun);
            }

            sysErr(errorMsgBuilder.toString());
            TestExecution.getInstance().stopExecution();
            stopProcessing();
        }

        switch (event.getState()) {
            case TEST_EXEC_RESULT_TREE_READY:
                TestExecution.getInstance().getTrav()
                    .addExecStackModificationListener(m_progress);
                break;
            case TEST_EXEC_START:
            case TEST_EXEC_RESTART:
                m_isTestSuiteRunning = true;
                break;
            case TEST_EXEC_FINISHED:
                sysOut(Messages.ExecutionControllerTestSuiteEnd);
                m_job.getNextTestSuite();
                m_isTestSuiteRunning = false;
                break;
            case TEST_EXEC_PAUSED:
                TestExecution.getInstance().pauseExecution(PauseMode.UNPAUSE);
                break;
            case TEST_EXEC_ERROR:
            case TEST_EXEC_FAILED:
            case TEST_EXEC_STOP:
                m_job.getNextTestSuite();
                m_isTestSuiteRunning = false;
                break;
            default:
                break;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void endTestExecution() {
        m_idle = false;
    }

    /**
     * @author BREDEX GmbH
     * @created Dec 3, 2010
     */
    protected class TestExecutionWatcher 
            implements IExecStackModificationListener {

        /**
         * {@inheritDoc}
         */
        public void stackIncremented(INodePO node) {
            String nodeType = StringConstants.EMPTY;
            String name = String.valueOf(node.getName());
            if (node instanceof IEventExecTestCasePO) {
                IEventExecTestCasePO evPo = (IEventExecTestCasePO)node;
                if (evPo.getReentryProp() != ReentryProperty.RETRY) {
                    setNoErrorWhileExecution(false);
                }
                nodeType = Messages.EventHandler;
            } else if (node instanceof IRefTestSuitePO) {
                nodeType = Messages.TestSuite;
                name = NodeNameUtil.getText((IRefTestSuitePO)node);
            } else if (node instanceof IExecTestCasePO) {
                nodeType = Messages.TestCase;
                name = NodeNameUtil.getText((IExecTestCasePO)node, false);
            } else if (node instanceof IConditionalStatementPO) {
                nodeType = Messages.Conditional;
            } else if (node instanceof IAbstractContainerPO) {
                nodeType = Messages.Container;
            } else if (node instanceof IDoWhilePO) {
                nodeType = Messages.DoWhile;
            } else if (node instanceof IWhileDoPO) {
                nodeType = Messages.WhileDo;
            } else if (node instanceof IIteratePO) {
                nodeType = Messages.Iterate;
            }
            
            StringBuilder sb = new StringBuilder(nodeType);
            sb.append(StringConstants.COLON);
            sb.append(StringConstants.SPACE);
            sb.append(name);
            sysOut(sb.toString());
        }

        /**
         * {@inheritDoc}
         */
        public void stackDecremented() {
            // do nothing
        }

        /**
         * {@inheritDoc}
         */
        public void nextDataSetIteration() {
            // do nothing
        }
        
        /**
         * {@inheritDoc}
         */
        public void nextCap(ICapPO cap) {
            sysOut(StringConstants.TAB
                    + Messages.Step 
                    + StringConstants.COLON
                    + StringConstants.SPACE
                    + String.valueOf(cap.getName()));
        }

        /**
         * {@inheritDoc}
         */
        public void retryCap(ICapPO cap) {
            sysOut(StringConstants.TAB
                    + Messages.RetryStep 
                    + StringConstants.COLON
                    + StringConstants.SPACE
                    + String.valueOf(cap.getName()));
        }

        /** {@inheritDoc} */
        public void infiniteLoop() {
            // not relevant
        }
    }

    /**
     * @param job the job to set
     */
    public void setJob(JobConfiguration job) {
        m_job = job;
    }

    /**
     * {@inheritDoc}
     */
    public void handleAutRegistration(AutRegistrationEvent event) {
        if ((event.getAutId().equals(m_startedAutId)
                || event.getAutId().equals(m_job.getAutId()))
                && event.getStatus() == RegistrationStatus.Register) {
            // generally do not do this, if AUT-Restart-Action is executed!
            if (m_isFirstAutStart) {
                m_idle = false;
                m_isFirstAutStart = false;
            }
        }
    }

    /**
     * @param noErrorWhileExecution the noErrorWhileExecution to set
     */
    protected void setNoErrorWhileExecution(boolean noErrorWhileExecution) {
        m_noErrorWhileExecution = noErrorWhileExecution;
    }

    /**
     * @return the noErrorWhileExecution
     */
    protected boolean isNoErrorWhileExecution() {
        return m_noErrorWhileExecution;
    }

    /**
     * {@inheritDoc}
     */
    public void receiveExecutionNotification(String notification) {
        LOG.error(notification);
        AbstractCmdlineClient.printConsoleLn(notification, true);
    }
}