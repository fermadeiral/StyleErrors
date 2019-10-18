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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jubula.client.core.businessprocess.ITestExecutionEventListener;
import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.TestExecution.PauseMode;
import org.eclipse.jubula.client.core.businessprocess.TestExecutionEvent;
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.events.AUTServerEvent;
import org.eclipse.jubula.client.core.events.AutAgentEvent;
import org.eclipse.jubula.client.core.events.IAUTEventListener;
import org.eclipse.jubula.client.core.events.IAUTServerEventListener;
import org.eclipse.jubula.client.core.events.IServerEventListener;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.internal.exceptions.ConnectionException;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;


/**
 * @author BREDEX GmbH
 * @created 21.07.2005
 */
public interface IClientTest {
    /**
     * connects to server
     * @param autAgentHostName The name of the server.
     * @param port The port number.
     */
    public abstract void connectToAutAgent(
            String autAgentHostName, String port);

    /**
     * disconnects from the Server
     *
     */
    public abstract void disconnectFromAutAgent();

    /**
     * start the application under test.
     * @param conf AutConf
     * @param aut the IAUTMainPO the AUT.
     * @throws ToolkitPluginException if the toolkit for the AUT cannot be found
     */
    public abstract void startAut(IAUTMainPO aut, IAUTConfigPO conf) 
            throws ToolkitPluginException;

    /**
     * Stops the Running AUT with the given ID.
     * 
     * @param autId The ID of the Running AUT to stop.
     */
    public abstract void stopAut(AutIdentifier autId);

    /**
     * Starts the object mapping. <br>
     * @param mod key modifier
     * @param inputCode the code representing the input that will collect a 
     *                  UI element
     * @param inputType the type of input that will trigger UI collection
     *                  (key press, mouse click, etc.)
     * @param modWP key modifier that will collect a 
     *                  UI element together with its parents
     * @param inputCodeWP the code representing the input that will collect a 
     *                  UI element together with its parents
     * @param inputTypeWP the type of input that will trigger collection of
     *                      a UI component together with its parents
     *                  (key press, mouse click, etc.)
     * @param autId The ID of the AUT for which to start the Object Mapping.
     * 
     * @throws ConnectionException
     * @throws NotConnectedException
     * @throws CommunicationException
     */
    public abstract void startObjectMapping(AutIdentifier autId, int mod, 
            int inputCode, int inputType, int modWP, 
            int inputCodeWP, int inputTypeWP) throws ConnectionException, 
            NotConnectedException, CommunicationException;

    /**
     * Starts the record mode. <br>
     * @param spec  SpecTestCasePO
     * @param compNamesCache The Component Names cache associated with the 
     *                        edit session of the spec test case.
     * @param recordCompMod key modifier
     * @param recordCompKey key
     * @param recordApplMod key modifier
     * @param recordApplKey key
     * @param checkModeKeyMod key modifier
     * @param checkModeKey key
     * @param checkCompKeyMod key modifier
     * @param checkCompKey key
     * @param dialogOpen boolean
     * @param singleLineTrigger SortedSet
     * @param multiLineTrigger SortedSet
     */
    public abstract void startRecordTestCase(ISpecTestCasePO spec,
            IWritableComponentNameCache compNamesCache, int recordCompMod,
            int recordCompKey, int recordApplMod, int recordApplKey,
            int checkModeKeyMod, int checkModeKey, int checkCompKeyMod,
            int checkCompKey, boolean dialogOpen,
            SortedSet<String> singleLineTrigger,
            SortedSet<String> multiLineTrigger);

    /**
     * Finishes the the object mapping. <br> 
     */
    public abstract void resetToTesting();

    /**
     * Starts the testsuite
     * 
     * @param execTestSuite
     *            The testSuite to be tested
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     * @param iterMax maximum iteration count
     * @param autId
     *            The ID of the Running AUT on which the test will take place.
     * @param externalVars
     *            a map of externally set variables; may be <code>null</code>
     * @param noRunOptMode
     *            The value of no-run option argument if no-run mode was specified, null otherwise
     * @param jobDesc
     *            The displayed TS name or null for using of execTestSuite name.
     */
    public abstract void startTestSuite(ITestSuitePO execTestSuite,
        AutIdentifier autId, boolean autoScreenshot, int iterMax,
        Map<String, String> externalVars, String noRunOptMode,
        String jobDesc);

    /**
     * Starts the given Test Job.
     * 
     * @param testJob The Test Job to start.
     * @param autoScreenshot
     *            whether screenshots should be automatically taken in case of
     *            test execution errors
     * @param iterMax maximum number of iterations
     * @param skippTSnames List of TestSuite names which should be skipped
     * @param noRunOptMode The mode of no run-option if it was specified or null otherwise
     * @return list of actually executed test suites
     */
    public abstract List<INodePO> startTestJob(ITestJobPO testJob,
            boolean autoScreenshot, int iterMax, List<String> skippTSnames,
            String noRunOptMode);
    
    /**
     * Stops test execution.
     */
    public abstract void stopTestExecution();

    /**
     * Pauses test execution.
     * 
     * @param pm
     *            the pause mode to use
     */
    public abstract void pauseTestExecution(PauseMode pm);
    
    /**
     * set pause test execution on error flag.
     * 
     * @param pauseOnError
     *            whether the test execution should automatically pause in case
     *            of errors
     */
    public abstract void pauseTestExecutionOnError(boolean pauseOnError);

    /**
     * @return get the pause test execution on error flag.
     */
    public abstract boolean isPauseTestExecutionOnError();
    
    /**
     * adds an IAUTServerEventListener
     * 
     * @param listener -
     *            the listener to add
     */
    public void addAUTServerEventListener(
            IAUTServerEventListener listener);

    /**
     * add an AutStarterEventListener
     * 
     * @param listener -
     *            the listener to add
     */
    public void addAutAgentEventListener(
            IServerEventListener listener);

    /**
     * add an AUTEventListener
     * 
     * @param listener -
     *            the listener to add
     */
    public void addTestEventListener(IAUTEventListener listener);

    /**
     * add a TestExecutionEventListener
     * @param listener a TestExecutionEventListener
     */
    public void addTestExecutionEventListener(
            ITestExecutionEventListener listener);
    
    /**
     * Notify all listeners that have registered interest for notification on
     * changing the state of the connection to the AUTServer.
     * 
     * @param event
     *            the event containing detailed information about the new state
     */
    public void fireAUTServerStateChanged(AUTServerEvent event);

    /**
     * Notify all listeners that have registered interest for notification on
     * this event type.
     * 
     * @param event
     *            the event containing detailed information about the new state
     */
    public void fireAUTStateChanged(AUTEvent event);
    
    /**
     * 
     */
    public void fireEndTestExecution();
    
    /**
     * Notify all listeners that have registered interest for notification on
     * changing the state of the connection to the AUT Agent.
     * 
     * @param event
     *            the event containing detailed information about the new state
     */
    public void fireAutAgentStateChanged(AutAgentEvent event);

    /**
     * Notify all listeners that have registered interest for notification on
     * changing the state of the TestExecution
     * @param event The event containing the information about the state
     */
    public void fireTestExecutionChanged(TestExecutionEvent event);

    /**
     * removes an AUTServerListener
     * 
     * @param listener -
     *            the listener to remove
     */
    public void removeAUTServerEventListener(
            IAUTServerEventListener listener);

    /**
     * remove an AutStarterListener
     * 
     * @param listener -
     *            the listener to remove
     */
    public void removeAutAgentEventListener(
            IServerEventListener listener);

    /**
     * remove an AUTListener
     * 
     * @param listener -
     *            the listener to remove
     */
    public void removeTestEventListener(IAUTEventListener listener);

    /**
     * Remove a TestExecutionEventListener
     * @param listener The listener to remove.
     */
    public void removeTestExecutionEventListener(
            ITestExecutionEventListener listener);
    
    /**
     * @return Returns the endTime.
     */
    public Date getEndTime();
    

    /**
     * @return Returns the test suite startTime.
     */
    public Date getTestsuiteStartTime();
    
    /**
     * @return Returns the test job startTime.
     */
    public Date getTestjobStartTime();
    
    
    /**
     * @return the Test Result Summary for the current test execution, or for
     *         the previous test execution if no test is currently running.
     */
    public ITestResultSummaryPO getTestresultSummary();
    
    /**
     * @param logPath The logPath to set.
     */
    public void setLogPath(String logPath);
    
    /**
     * @param generateMonitoringReport the monitoring report generation flag to set
     */
    public void setGenerateMonitoringReport(Boolean generateMonitoringReport);
    
    /**
     * @param logStyle <code>String</code> representing the style the log
     *                 use (for example, Complete or Errors only)
     */
    public void setLogStyle(String logStyle);

    /**
     * @param relevant
     *            whether the upcoming test execution is relevant for long term
     *            reporting
     */
    public void setRelevantFlag(boolean relevant);
    
    /**
     * @return  whether the upcoming test execution is relevant for long term
     *            reporting
     */
    public boolean isRelevant();
    
    /**
     * sending a request to the agent to get the config map from the last
     * connected AUT.
     * @param autId the AUT id to retrieve the map for
     * @return null if no config map available; otherwise the map
     */
    public Map<String, String> requestAutConfigMapFromAgent(String autId);

    /**
     * 
     * @param screenshotXml
     *            whether the XML and HTML should have screenshots in the files
     */
    public void setScreenshotXMLFlag(boolean screenshotXml);
    
    /**
     * @return whether the XML and HTML testresults should have screenshots
     */    
    public boolean isScreenshotForXML();

    /**
     * Sets the fileName which should be used for the HTML and XML documents
     * @param fileName the name of the file
     */
    public void setFileName(String fileName);
    
    /**
     * @return <code>true</code> if report testresults is running
     */
    public boolean isReportingRunning();

    /**
     * @return whether successful parts of the TestResultTree should be trimmed
     */
    public boolean isTrimming();
}