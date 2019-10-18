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
package org.eclipse.jubula.client.alm.mylyn.core.bp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jubula.client.alm.mylyn.core.Activator;
import org.eclipse.jubula.client.alm.mylyn.core.i18n.Messages;
import org.eclipse.jubula.client.alm.mylyn.core.model.ALMChange;
import org.eclipse.jubula.client.alm.mylyn.core.model.CommentEntry;
import org.eclipse.jubula.client.alm.mylyn.core.model.FieldUpdate;
import org.eclipse.jubula.client.alm.mylyn.core.utils.ALMAccess;
import org.eclipse.jubula.client.core.businessprocess.TestResultBP;
import org.eclipse.jubula.client.core.businessprocess.TestresultSummaryBP;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ITestresultSummaryEventListener;
import org.eclipse.jubula.client.core.model.IALMReportingProperties;
import org.eclipse.jubula.client.core.model.IALMReportingRulePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO.AlmReportStatus;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.progress.IProgressConsole;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.ReportRuleType;
import org.eclipse.jubula.client.core.utils.TestResultNodeTraverser;
import org.eclipse.jubula.mylyn.utils.MylynAccess;
import org.eclipse.osgi.util.NLS;

/**
 * @author BREDEX GmbH
 */
public class CommentReporter implements ITestresultSummaryEventListener {
    /** instance of this class */
    private static CommentReporter instance;
    /** the progress console to use */
    private IProgressConsole m_console;
    /** the report properties to use */ 
    private IALMReportingProperties m_reportProps = null;
    
    /**
     * @author BREDEX GmbH
     */
    private static class ALMChangeCreationOperation implements
            ITreeNodeOperation<TestResultNode> {
        /** the taskIdToComment mapping */
        private Map<String, List<ALMChange>> m_taskIdToALMChange;
        /** report failure */
        private final boolean m_reportFailure;
        /** report success */
        private final boolean m_reportSuccess;
        /** reporting rules */
        private final List<IALMReportingRulePO> m_reportingRules;
        /** dashboard URL */
        private String m_dashboardURL;
        /** the summary id */
        private ITestResultSummaryPO m_summary;
        /** test result node counter */
        private long m_nodeCount = 0;
        
        /**
         * Constructor
         * 
         * @param taskIdToALMChange
         *            the mapping to fill with entries
         * @param reportSuccess
         *            reportSuccess
         * @param reportFailure
         *            reportFailure
         * @param reportingRules
         *            reportingRules
         * @param dashboardURL
         *            dashboardURL
         * @param summary 
         */
        public ALMChangeCreationOperation(
            Map<String, List<ALMChange>> taskIdToALMChange,
            boolean reportFailure, boolean reportSuccess,
            List<IALMReportingRulePO> reportingRules,
            String dashboardURL, ITestResultSummaryPO summary) {
            m_taskIdToALMChange = taskIdToALMChange;
            m_reportFailure = reportFailure;
            m_reportSuccess = reportSuccess;
            m_reportingRules = reportingRules;
            m_dashboardURL = dashboardURL;
            m_summary = summary;
        }

        /** {@inheritDoc} */
        public boolean operate(ITreeTraverserContext<TestResultNode> ctx,
                TestResultNode parent, TestResultNode resultNode,
                boolean alreadyVisited) {
            m_nodeCount++;
            boolean didNodePass = CommentEntry
                    .hasPassed(resultNode.getStatus());
            String taskIdforNode = resultNode.getTaskId();
            boolean hasTaskId = taskIdforNode != null;
            
            boolean writeCommentForNode = hasTaskId
                 && ((m_reportSuccess && didNodePass) 
                  || (m_reportFailure && !didNodePass));
            
            boolean writeFieldUpdateForNode = hasTaskId 
                  && !getApplicableRules(didNodePass).isEmpty();

            if (writeCommentForNode) {
                CommentEntry c = new CommentEntry(resultNode, m_dashboardURL,
                        m_summary, m_nodeCount);
                addALMChangeToNode(taskIdforNode, c);
            }
            if (writeFieldUpdateForNode) {
                FieldUpdate f = new FieldUpdate(resultNode, m_dashboardURL,
                        m_summary, m_nodeCount,
                        getApplicableRules(didNodePass));
                addALMChangeToNode(taskIdforNode, f);
            }

            return true;
        }

        /**
         * Processes an ALM change
         * @param taskIdforNode task ID for the node
         * @param change the change
         */
        private void addALMChangeToNode(String taskIdforNode,
                ALMChange change) {
            List<ALMChange> changes = m_taskIdToALMChange
                    .get(taskIdforNode);
            if (changes != null) {
                changes.add(change);
            } else {
                List<ALMChange> cs = new LinkedList<ALMChange>();
                cs.add(change);
                m_taskIdToALMChange.put(taskIdforNode, cs);
            }
        }

        /**
         * Returns the applicable reporting rules
         * @param didNodePass whether node passed
         * @return the applicable reporting rules
         */
        private List<IALMReportingRulePO> getApplicableRules(
                boolean didNodePass) {
            List<IALMReportingRulePO> applicableRules =
                    new ArrayList<IALMReportingRulePO>();
            ReportRuleType type = didNodePass ? ReportRuleType.ONSUCCESS
                    : ReportRuleType.ONFAILURE;
            for (IALMReportingRulePO rule : m_reportingRules) {
                if (rule.getType() == type) {
                    applicableRules.add(rule);
                }
            }
            return applicableRules;
        }

        /** {@inheritDoc} */
        public void postOperate(ITreeTraverserContext<TestResultNode> ctx,
                TestResultNode parent, TestResultNode node,
                boolean alreadyVisited) {
            // currently unused
        }
    }

    /** Constructor */
    private CommentReporter() {
        DataEventDispatcher.getInstance()
            .addTestresultSummaryEventListener(this);
    }

    /**
     * @return Returns the instance.
     */
    public static CommentReporter getInstance() {
        if (instance == null) {
            instance = new CommentReporter();
        }
        return instance;
    }

    /**
     * process the result tree
     * 
     * @param reportFailure
     *            reportFailure
     * @param reportSuccess
     *            reportSuccess
     * @param monitor
     *            monitor
     * @param reportingRules 
     *            reportingRules
     * @param summary
     *            the summary the result tree belongs to
     * @param rootResultNode
     *            the result node to report for
     * @return status
     */
    private IStatus processResultTree(IProgressMonitor monitor,
        boolean reportSuccess, boolean reportFailure,
        List<IALMReportingRulePO> reportingRules,
        ITestResultSummaryPO summary, TestResultNode rootResultNode) {
        Map<String, List<ALMChange>> taskIdToALMChange = 
            new HashMap<String, List<ALMChange>>();

        ITreeNodeOperation<TestResultNode> operation = 
            new ALMChangeCreationOperation(
                taskIdToALMChange, reportFailure, reportSuccess, reportingRules,
                m_reportProps.getDashboardURL(), summary);
        TestResultNodeTraverser traverser = new TestResultNodeTraverser(
                rootResultNode, operation);
        traverser.traverse();
        final IStatus reportStatus = reportToALM(monitor, taskIdToALMChange);
        
        if (reportStatus.isOK()) {
            TestresultSummaryBP.getInstance().setALMReportStatus(summary,
                AlmReportStatus.REPORTED);
        }
        
        return reportStatus;
    }

    /**
     * @param monitor
     *            the monitor to use
     * @param taskIdToALMChange
     *            the comment mapping
     * @return status
     */
    private IStatus reportToALM(IProgressMonitor monitor,
            Map<String, List<ALMChange>> taskIdToALMChange) {
        String repoLabel = m_reportProps.getALMRepositoryName();
        boolean failed = false;
        Set<String> taskIds = taskIdToALMChange.keySet();
        int taskAmount = taskIds.size();
        IProgressConsole c = getConsole();
        int successCount = 0;
        if (taskAmount > 0) {
            String out = NLS.bind(Messages.ReportToALMJob, taskAmount,
                repoLabel);
            monitor.beginTask(out, taskAmount);
            
            c.writeLine(out);
            int overallCommentCount = 0;
            int overallFieldUpdateCount = 0;
            for (String taskId : taskIds) {
                List<ALMChange> changes = taskIdToALMChange.get(taskId);
                List<CommentEntry> comments = new LinkedList<CommentEntry>();
                List<FieldUpdate> fieldUpdates = new LinkedList<FieldUpdate>();
                split(changes, comments, fieldUpdates);
                boolean commentingSucceeded = true;
                IStatus fieldUpdateStatus = Status.OK_STATUS;
                
                int commentAmount = comments.size();
                if (commentAmount > 0) {
                    writeStatus(c, taskId, commentAmount, Messages.
                            ReportingComment, Messages.ReportingComments);
                    commentingSucceeded = ALMAccess.createComment(
                            repoLabel, taskId, comments, monitor);
                    if (!commentingSucceeded) {
                        failed = true;
                        c.writeErrorLine(
                                NLS.bind(Messages.ReportingTaskFailed, taskId));
                    } else {
                        overallCommentCount += commentAmount;
                    }
                }
                int fieldUpdateAmount = fieldUpdates.size();
                if (fieldUpdateAmount > 0) {
                    writeStatus(c, taskId, fieldUpdateAmount,
                            Messages.ReportingFieldUpdate,
                            Messages.ReportingFieldUpdates);
                    fieldUpdateStatus = ALMAccess.updateFields(
                            repoLabel, taskId, fieldUpdates, monitor);
                    if (!fieldUpdateStatus.isOK()) {
                        failed = true;
                        writeErrorStatus(c, taskId, fieldUpdateStatus);
                        if (fieldUpdateStatus.getSeverity() == IStatus.CANCEL) {
                            break;
                        }
                    } else {
                        overallFieldUpdateCount += fieldUpdateAmount;
                    }
                }
                if (fieldUpdateStatus.isOK() && commentingSucceeded) {
                    successCount++;
                }
                monitor.worked(1);
            }
            c.writeLine(NLS.bind(Messages.ReportToALMJobDone, new Integer[] {
                overallCommentCount, overallFieldUpdateCount, successCount,
                taskAmount }));
            monitor.done();
        } else {
            c.writeLine(Messages.NothingToReport);
        }
        if (!failed || successCount > 0) {
            return Status.OK_STATUS;
        }
        return new Status(IStatus.ERROR, Activator.ID,
            "Reporting comments performed with errors..."); //$NON-NLS-1$
    }
    
    /**
     * write error lines to the console for the status and its children
     * @param c the {@link IProgressConsole}
     * @param taskId the id of the task
     * @param fieldUpdateStatus the update status of a field after the executin
     */
    private void writeErrorStatus(IProgressConsole c, String taskId,
            IStatus fieldUpdateStatus) {
        c.writeErrorLine(fieldUpdateStatus.getMessage());
        IStatus[] children = fieldUpdateStatus.getChildren();
        if (children != null && children.length > 0) {
            for (int i = 0; i < children.length; i++) {
                c.writeErrorLine(children[i].getMessage());
            }
        }
        c.writeErrorLine(
                NLS.bind(Messages.ReportingTaskFailed, taskId));
    }

    /**
     * writes the status of the commenting to a task to the console
     * @param c console
     * @param taskId task
     * @param changeAmount amount of changes 
     * @param one output if only one change
     * @param mult output if multiple changes
     */
    private void writeStatus(IProgressConsole c, String taskId,
            int changeAmount, String one, String mult) {
        IStatus status = null;
        if (changeAmount > 1) {
            status = new Status(IStatus.WARNING, Activator.ID,
                    NLS.bind(mult, changeAmount, taskId));
        } else {
            status = new Status(IStatus.OK, Activator.ID,
                    NLS.bind(one, taskId));
        }
        c.writeStatus(status);
    }

    /**
     * Splits a list of ALM changes into comments and field updates
     * @param changes the changes
     * @param comments the comments
     * @param fieldUpdates the field updates
     */
    private void split(List<ALMChange> changes, List<CommentEntry> comments,
            List<FieldUpdate> fieldUpdates) {
        for (ALMChange change : changes) {
            if (change instanceof CommentEntry) {
                comments.add((CommentEntry)change);
            } else if (change instanceof FieldUpdate) {
                fieldUpdates.add((FieldUpdate)change);
            }
        }
    }

    /**
     * @return the console
     */
    public IProgressConsole getConsole() {
        return m_console;
    }

    /**
     * @param console the console to set
     */
    public void setConsole(IProgressConsole console) {
        m_console = console;
    }

    /** {@inheritDoc} */
    public void handleTestresultSummaryChanged(
        final ITestResultSummaryPO summary, DataState state) {
        if (state != DataState.Added) {
            return;
        }

        IProjectPO project = GeneralStorage.getInstance().getProject();
        TestResult resultTestModel = TestResultBP.getInstance()
            .getResultTestModel();
        final TestResultNode rootResultNode = resultTestModel
            .getRootResultNode();

        Job job = gatherInformationAndCreateReportToALMJob(summary,
            project.getProjectProperties(), rootResultNode);
        
        if (job != null) {
            job.schedule();
        }
    }

    /**
     * @param summary the summary
     * @param properties the properties
     * @param rootResultNode the root result node
     * @return the job for reporting
     */
    public Job gatherInformationAndCreateReportToALMJob(
        final ITestResultSummaryPO summary,
        IALMReportingProperties properties, 
        final TestResultNode rootResultNode) {
        m_reportProps = properties;
        final boolean reportSuccess = properties.getIsReportOnSuccess();
        final boolean reportFailure = properties.getIsReportOnFailure();
        final List<IALMReportingRulePO> reportingRules = 
                properties.getALMReportingRules();
        final String almRepositoryName = properties.getALMRepositoryName();

        if (!StringUtils.isBlank(almRepositoryName)
            && (reportSuccess || reportFailure || !reportingRules.isEmpty())
                    && summary.isTestsuiteRelevant()) {
            Job reportToALMOperation = new Job(NLS.bind(
                Messages.ReportToALMJobName, almRepositoryName)) {
                protected IStatus run(IProgressMonitor monitor) {
                    getConsole().writeLine(
                        NLS.bind(Messages.TaskRepositoryConnectionTest,
                            almRepositoryName));
                    IStatus connectionStatus = MylynAccess
                        .testConnection(almRepositoryName);
                    if (connectionStatus.isOK()) {
                        getConsole().writeLine(
                            NLS.bind(
                                Messages.TaskRepositoryConnectionTestSucceeded,
                                almRepositoryName));
                        return processResultTree(monitor, reportSuccess,
                            reportFailure, reportingRules, summary,
                            rootResultNode);
                    }
                    getConsole().writeErrorLine(
                        NLS.bind(Messages.TaskRepositoryConnectionTestFailed,
                            connectionStatus.getMessage()));

                    return connectionStatus;

                }
            };
            return reportToALMOperation;
        }
        return null;
    }
}
