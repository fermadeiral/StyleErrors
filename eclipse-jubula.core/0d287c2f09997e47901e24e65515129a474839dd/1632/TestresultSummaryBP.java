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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.ClientTest;
import org.eclipse.jubula.client.core.IClientTest;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.client.core.model.IALMReportingRulePO;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.IDoWhilePO;
import org.eclipse.jubula.client.core.model.IIteratePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParameterDetailsPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestResultAdditionPO;
import org.eclipse.jubula.client.core.model.ITestResultPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO.AlmReportStatus;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.IWhileDoPO;
import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.model.TestResultParameter;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.exception.JBFatalException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to get keywords and summary from testresultnode to persist in database
 * 
 * @author BREDEX GmbH
 * @created Mar 4, 2010
 */
public class TestresultSummaryBP {
    /**
     * <code>autrun</code>
     * if autconfig is null because autrun ist used,
     * use this constant for summary table
     */
    public static final String AUTRUN = "autrun"; //$NON-NLS-1$

    /** constant for keyword type Iterate loop */
    public static final int TYPE_ITERATE = 9;
    
    /** constant for keyword type While Do loop */
    public static final int TYPE_WHILEDO = 8;
    
    /** constant for keyword type Do While loop */
    public static final int TYPE_DOWHILE = 7;
    
    /** constant for keyword type Abstract Container */
    public static final int TYPE_CONTAINER = 6;
    
    /** constant for keyword type Condition */
    public static final int TYPE_CONDITION = 5;

    /** constant for keyword type Comment */
    public static final int TYPE_COMMENT = 4;

    /** constant for keyword type Test Step */
    public static final int TYPE_TEST_STEP = 3;
    
    /** constant for keyword type Test Case */
    public static final int TYPE_TEST_CASE = 2;
    
    /** constant for keyword type Test Suite */
    public static final int TYPE_TEST_SUITE = 1;
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(
            TestresultSummaryBP.class);
    
    /** instance */
    private static TestresultSummaryBP instance = null;
    
    /** id of parent keyword*/
    private Long m_parentKeyWordId;
    
    /**
     * @param result The Test Result containing the source data.
     * @param summary The Test Result Summary to fill with data.
     */
    public void fillTestResultSummary(TestResult result,
        ITestResultSummaryPO summary) {
        TestExecution te = TestExecution.getInstance();
        ITestSuitePO ts = te.getStartedTestSuite();
        IAUTMainPO startedAut = te.getConnectedAut();
        if (result.getAutConfigMap() != null && startedAut != null) {
            String autConfigName = result.getAutConfigName();
            for (IAUTConfigPO conf : startedAut.getAutConfigSet()) {
                if (conf.getValue(AutConfigConstants.AUT_CONFIG_NAME, "invalid") //$NON-NLS-1$
                        .equals(autConfigName)) {
                    summary.setInternalAutConfigGuid(conf.getGuid());
                    break;
                }
            }
        } else {
            summary.setInternalAutConfigGuid(AUTRUN);
        }
        summary.setAutConfigName(result.getAutConfigName());
        summary.setAutCmdParameter(result.getAutArguments());
        summary.setAutId(result.getAutId());
        
        summary.setAutOS(System.getProperty("os.name")); //$NON-NLS-1$
        IAUTMainPO aut = startedAut != null ? startedAut : ts.getAut();
        if (aut != null) {
            summary.setInternalAutGuid(aut.getGuid());
            summary.setAutName(aut.getName());
            summary.setAutToolkit(aut.getToolkit());
        }
        summary.setTestsuiteDate(new Date());
        summary.setInternalTestsuiteGuid(ts.getGuid());
        summary.setTestsuiteName(ts.getName());
        summary.setInternalProjectGuid(result.getProjectGuid());
        summary.setInternalProjectID(result.getProjectId());
        summary.setProjectName(result.getProjectName());
        summary.setProjectMajorVersion(result.getProjectMajorVersion());
        summary.setProjectMinorVersion(result.getProjectMinorVersion());
        summary.setProjectMicroVersion(result.getProjectMicroVersion());
        summary.setProjectVersionQualifier(result.getProjectVersionQualifier());
        IClientTest clientTest = ClientTest.instance();
        Date startTime = clientTest.getTestsuiteStartTime();
        summary.setTestsuiteStartTime(startTime);
        Date endTime = new Date();
        summary.setTestsuiteEndTime(endTime);
        summary.setTestsuiteDuration(
                TimeUtil.getDurationString(startTime, endTime));
        summary.setTestsuiteExecutedTeststeps(te.getNumberOfTestedSteps());
        summary.setTestsuiteExpectedTeststeps(te.getExpectedNumberOfSteps());
        summary.setTestsuiteEventHandlerTeststeps(
                te.getNumberOfEventHandlerSteps() 
                    + te.getNumberOfRetriedSteps());
        summary.setTestsuiteFailedTeststeps(te.getNumberOfFailedSteps());
        summary.setTestsuiteRelevant(te.getStartedTestSuite().getRelevant());
        ITestJobPO tj = te.getStartedTestJob();
        if (tj != null) {
            summary.setTestJobName(tj.getName());
            summary.setInternalTestJobGuid(tj.getGuid());
            summary.setTestJobStartTime(clientTest
                    .getTestjobStartTime());
        }
        summary.setTestsuiteStatus(
                result.getRootResultNode().getStatus());      
        //set default monitoring values.       
        summary.setInternalMonitoringId(
                MonitoringConstants.EMPTY_MONITORING_ID);         
        summary.setReportWritten(false);
        determineAlmRepositoryStatus(summary, result);
        summary.setMonitoringValueType(MonitoringConstants.EMPTY_TYPE); 
    }
    
    /**
     * @param summary
     *            the summary to determine the status for
     * @param result
     *            the result
     */
    private void determineAlmRepositoryStatus(ITestResultSummaryPO summary,
        TestResult result) {
        IProjectPO project = result.getProject();
        final IProjectPropertiesPO projectProperties = project
            .getProjectProperties();
        final boolean reportSuccess = projectProperties.getIsReportOnSuccess();
        final boolean reportFailure = projectProperties.getIsReportOnFailure();
        final List<IALMReportingRulePO> reportingRules =
                projectProperties.getALMReportingRules();
        final List<IALMReportingRulePO> reportingRulesCopy =
                new ArrayList<IALMReportingRulePO>();
        for (IALMReportingRulePO rule : reportingRules) {
            reportingRulesCopy.add(rule.copy());
        }
        final String almRepositoryName = projectProperties
            .getALMRepositoryName();
        
        AlmReportStatus status = AlmReportStatus.NOT_CONFIGURED;
        if (StringUtils.isNotEmpty(almRepositoryName)
            && (reportSuccess || reportFailure || !reportingRules.isEmpty())
            && summary.isTestsuiteRelevant()) {
            status = AlmReportStatus.NOT_YET_REPORTED;
            summary.setALMRepositoryName(almRepositoryName);
            summary.setIsReportOnSuccess(reportSuccess);
            summary.setIsReportOnFailure(reportFailure);
            summary.setALMReportingRules(reportingRulesCopy);
            summary.setDashboardURL(projectProperties.getDashboardURL());
        }
        summary.setAlmReportStatus(status);
    }

    /**
     * @param result The Test Result.
     * @param summaryId id of test result summary
     * @return session of test result details to persist in database
     */
    public EntityManager createTestResultDetailsSession(TestResult result,
            Long summaryId) {
        final EntityManager sess = Persistor.instance().openSession();
        Persistor.instance().getTransaction(sess);
        buildTestResultDetailsSession(
                result.getRootResultNode(), sess, summaryId, 1, 1);
        return sess;
    }
    
    /**
     * Recursively build list of test result details to persist in database.
     * 
     * @param result TestResultNode
     * @param sess Session
     * @param summaryId id of testrun summary
     * @param nodeLevel "Indentation"-level of the node.
     * @param startingNodeSequence Initial sequence number for this section
     *                             of the Test Results.
     *                             
     * @return the continuation of the sequence number.
     */
    private int buildTestResultDetailsSession(TestResultNode result,
            EntityManager sess, Long summaryId, final int nodeLevel, 
            final int startingNodeSequence) {
        int nodeSequence = startingNodeSequence;
        TestResultNode resultNode = result;
        ITestResultPO keyword = PoMaker.createTestResultPO();
        keyword.setInternalTestResultSummaryID(summaryId);
        fillNode(keyword, resultNode);
        keyword.setKeywordLevel(nodeLevel);
        keyword.setKeywordSequence(nodeSequence);
        keyword.setInternalParentKeywordID(m_parentKeyWordId);
        sess.persist(keyword);
        for (TestResultNode node : resultNode.getResultNodeList()) {
            m_parentKeyWordId = keyword.getId();
            nodeSequence = buildTestResultDetailsSession(node, sess, summaryId, 
                    nodeLevel + 1, nodeSequence + 1);
        }
        
        return nodeSequence;
    }
    
    /**
     * fill result node
     * @param keyword ITestResultPO
     * @param resultNode ITestResultPO
     */
    private void fillNode(ITestResultPO keyword, TestResultNode resultNode) {
        INodePO node = resultNode.getNode();
        keyword.setKeywordName(node.getName());
        keyword.setInternalKeywordGuid(node.getGuid());
        keyword.setKeywordComment(node.getComment());
        keyword.setTaskId(resultNode.getTaskId());
        keyword.setInternalKeywordStatus(resultNode.getStatus());
        keyword.setKeywordStatus(resultNode.getStatusString());
        if (resultNode.getTimeStamp() != null) {
            keyword.setTimestamp(resultNode.getTimeStamp());
        }
        
        if (resultNode.getParent() != null) {
            keyword.setInternalParentKeywordID(
                    resultNode.getParent().getNode().getId());
        }

        addParameterListToResult(keyword, resultNode);

        keyword.setKeywordType(resultNode.getTypeOfNode());
        if (node instanceof ICapPO) {
            keyword.setInternalKeywordType(TYPE_TEST_STEP);
            
            //set component name, type and action name
            ICapPO cap = (ICapPO)node;
            String compNameGuid = cap.getComponentName();
            keyword.setInternalComponentNameGuid(compNameGuid);
            keyword.setComponentName(
                    StringUtils.defaultString(resultNode.getComponentName()));
            keyword.setInternalComponentType(cap.getComponentType());
            keyword.setComponentType(CompSystemI18n.getString(
                    cap.getComponentType()));
            keyword.setInternalActionName(cap.getActionName());
            keyword.setActionName(CompSystemI18n.getString(
                    cap.getActionName()));
            String commandlog = resultNode.getCommandLog();
            if (StringUtils.isNotBlank(commandlog)) {
                ITestResultAdditionPO testResultAddition = 
                        NodeMaker.createTestResultAddtionPO(commandlog);
                testResultAddition.setInternalTestResultSummaryID(
                        keyword.getInternalTestResultSummaryID());
                keyword.addAdditon(testResultAddition);
            }
            
            //add screenshot if exists
            if (resultNode.getScreenshot() != null) {
                keyword.setImage(resultNode.getScreenshot());
            }
            
            //add error details
            addErrorDetails(keyword, resultNode);
            keyword.setNoOfSimilarComponents(
                    resultNode.getNoOfSimilarComponents());
            keyword.setOmHeuristicEquivalence(
                    resultNode.getOmHeuristicEquivalence());
        } else if (node instanceof ITestCasePO) {
            keyword.setInternalKeywordType(TYPE_TEST_CASE);
            //setting the JUnitsuite
            keyword.setIsJUnitSuite(node.isJUnitTestSuite());
        } else if (node instanceof ITestSuitePO) {
            keyword.setInternalKeywordType(TYPE_TEST_SUITE);
        } else if (node instanceof ICommentPO) {
            keyword.setInternalKeywordType(TYPE_COMMENT);
        } else if (node instanceof IConditionalStatementPO) {
            keyword.setInternalKeywordType(TYPE_CONDITION);
        } else if (node instanceof IAbstractContainerPO) {
            keyword.setInternalKeywordType(TYPE_CONTAINER);
        } else if (node instanceof IDoWhilePO) {
            keyword.setInternalKeywordType(TYPE_DOWHILE);
        } else if (node instanceof IWhileDoPO) {
            keyword.setInternalKeywordType(TYPE_WHILEDO);
        } else if (node instanceof IIteratePO) {
            keyword.setInternalKeywordType(TYPE_ITERATE);
        }
    }
    
    /**
     * @param node TestResultNode
     * @param keyword ITestResultPO
     */
    private void addParameterListToResult(ITestResultPO keyword,
            TestResultNode node) {
        for (TestResultParameter param : node.getParameters()) {
            IParameterDetailsPO parameter = PoMaker.createParameterDetailsPO();
            parameter.setParameterName(param.getName());
            parameter.setParameterType(param.getType());
            parameter.setInternalTestResultSummaryID(
                    keyword.getInternalTestResultSummaryID());
            String value = param.getValue();
            if (value.length() >= 3500) {
                log.warn("Parameter value to long: " + value); //$NON-NLS-1$
                value = StringUtils.substring(value, 0, 3500);
            }
            parameter.setParameterValue(value);
            keyword.addParameter(parameter);
        }
    }
    
    /**
     * add error details to test result element
     * @param keyword ITestResultPO
     * @param node TestResultNode
     */
    private void addErrorDetails(ITestResultPO keyword, TestResultNode node) {
        if (node.getStatus() == TestResultNode.ERROR 
                || node.getStatus() == TestResultNode.RETRYING) {
            TestErrorEvent event = node.getEvent();
            keyword.setStatusType(I18n.getString(event.getId(),
                    true));
            
            Map<String, Object> eventProps = event.getProps();
            String descriptionKey = (String) eventProps
                    .get(TestErrorEvent.Property.DESCRIPTION_KEY);
            if (descriptionKey != null) {
                Object[] args = (Object[]) eventProps
                        .get(TestErrorEvent.Property.PARAMETER_KEY);
                // error description
                keyword.setStatusDescription(String.valueOf(I18n.getString(
                        descriptionKey, args)));
            }

            if (eventProps.containsKey(TestErrorEvent.Property.OPERATOR_KEY)) {
                String value = String.valueOf(eventProps
                        .get(TestErrorEvent.Property.OPERATOR_KEY));
                keyword.setStatusOperator(value);
            }

            if (eventProps.containsKey(TestErrorEvent.Property.PATTERN_KEY)) {
                String value = String.valueOf(eventProps
                        .get(TestErrorEvent.Property.PATTERN_KEY));
                keyword.setExpectedValue(value);
            }

            if (eventProps.containsKey(
                    TestErrorEvent.Property.ACTUAL_VALUE_KEY)) {
                String value = String.valueOf(eventProps
                        .get(TestErrorEvent.Property.ACTUAL_VALUE_KEY));
                keyword.setActualValue(value);
            }
        }
    }


    /**
     * perform model changes
     * 
     * @param selectedSummary the summary to change the comment for
     * @param newTitle the new comment title
     * @param newDetails the new comment details
     */
    public void setCommentTitleAndDetails(
        ITestResultSummaryPO selectedSummary, String newTitle,
        String newDetails) {
        
        Persistor persistor = Persistor.instance();
        final EntityManager sess = persistor.openSession();
        try {
            final EntityTransaction tx = persistor.getTransaction(sess);

            ITestResultSummaryPO summary = sess.merge(selectedSummary);

            summary.setCommentTitle(newTitle);
            summary.setCommentDetail(newDetails);

            persistor.commitTransaction(sess, tx);
            DataEventDispatcher.getInstance().fireTestresultSummaryChanged(
                    summary, DataState.StructureModified);
        } catch (PMException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
        } catch (ProjectDeletedException e) {
            throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
        } finally {
            persistor.dropSession(sess);
        }
    }
    
    /**
     * @return instance of TestresultSummaryBP
     */
    public static TestresultSummaryBP getInstance() {
        if (instance == null) {
            instance = new TestresultSummaryBP();
        }
        return instance;
    }

    /**
     * @param selectedSummary summary
     * @param newRelevance newRelevance
     */
    public void setRelevance(ITestResultSummaryPO selectedSummary,
        boolean newRelevance) {
        if (selectedSummary != null) {
            Persistor persistor = Persistor.instance();
            final EntityManager sess = persistor.openSession();
            try {
                final EntityTransaction tx = persistor.getTransaction(sess);

                ITestResultSummaryPO msummary = sess.merge(selectedSummary);

                msummary.setTestsuiteRelevant(newRelevance);
                persistor.commitTransaction(sess, tx);
                DataEventDispatcher.getInstance().fireTestresultSummaryChanged(
                        msummary, DataState.StructureModified);
            } catch (PMException e) {
                throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                        MessageIDs.E_DATABASE_GENERAL);
            } catch (ProjectDeletedException e) {
                throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                        MessageIDs.E_PROJECT_NOT_FOUND);
            } finally {
                persistor.dropSession(sess);
            }
        }
    }
    
    /**
     * @param selectedSummary
     *            summary
     * @param reportStatus
     *            reportStatus
     */
    public void setALMReportStatus(ITestResultSummaryPO selectedSummary,
        AlmReportStatus reportStatus) {
        if (selectedSummary != null) {
            Persistor persistor = Persistor.instance();
            final EntityManager sess = persistor.openSession();
            try {
                final EntityTransaction tx = persistor.getTransaction(sess);

                ITestResultSummaryPO msummary = sess.merge(selectedSummary);

                msummary.setAlmReportStatus(reportStatus);
                persistor.commitTransaction(sess, tx);
                DataEventDispatcher.getInstance().fireTestresultSummaryChanged(
                    msummary, DataState.StructureModified);
            } catch (PMException e) {
                throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_DATABASE_GENERAL);
            } catch (ProjectDeletedException e) {
                throw new JBFatalException(Messages.StoringOfMetadataFailed, e,
                    MessageIDs.E_PROJECT_NOT_FOUND);
            } finally {
                persistor.dropSession(sess);
            }
        }
    }
}
