/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle;

import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jubula.client.core.businessprocess.compcheck.ProblemPropagator;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectLoadedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IProjectStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.core.model.IConditionalStatementPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.ITestJobPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.rules.SingleJobRule;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.teststyle.checks.BaseCheck;
import org.eclipse.jubula.client.teststyle.checks.CheckCont;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;
import org.eclipse.jubula.client.teststyle.gui.TeststyleProblemAdder;
import org.eclipse.jubula.client.teststyle.gui.decoration.DecoratorHandler;
import org.eclipse.jubula.client.teststyle.i18n.Messages;
import org.eclipse.jubula.client.teststyle.problems.ProblemCont;
import org.eclipse.jubula.client.ui.utils.JobUtils;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Initialize and handles the teststyles and its definitions.
 * 
 * @author marcell
 * 
 */
public final class TeststyleHandler implements IDataChangedListener,
        IProjectLoadedListener, IProjectStateListener {

    /**
     * Job which is updating the teststyle problems in the nodes
     * @author BREDEX GmbH
     *
     */
    class TestStyleJob extends Job {

        /** the data changed events to process */
        private DataChangedEvent[] m_events;

        /**
         * 
         * @param name the name of the Job
         * @param events the {@link DataChangedEvent} to process
         */
        public TestStyleJob(String name, DataChangedEvent... events) {
            super(name);
            this.m_events = events;
        }
        /** {@inheritDoc} */
        public boolean belongsTo(Object family) {
            if (family instanceof TestStyleJob) {
                return true;
            }
            return super.belongsTo(family);
        }
        @Override
        protected IStatus run(IProgressMonitor monitor) {
            if (monitor.isCanceled()) {
                return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
                        getName());
            }
            monitor.beginTask(Messages.TestStyleRunningOperation,
                    IProgressMonitor.UNKNOWN);
            for (DataChangedEvent e : m_events) {
                if (e.getUpdateState() != UpdateState.onlyInEditor) {
                    handleChangedPo(e.getPo(), e.getDataState(),
                            e.getUpdateState());
                }
            }
            if (monitor.isCanceled()) {
                return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
                        getName());
            }
            refresh();
            if (monitor.isCanceled()) {
                return new Status(IStatus.CANCEL, Activator.PLUGIN_ID,
                        getName());
            }
            try {
                addTeststyleProblems(monitor);
            } catch (JBException e1) {
             // this might occur during create new version
                return new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                        getName());
            }
            ProblemPropagator.INSTANCE.propagate();
            monitor.done();
            return new Status(IStatus.OK, Activator.PLUGIN_ID, getName());
        }

    }

    /**
     * Job which is cleaning up and doing a complete teststyle check
     * @author BREDEX GmbH
     */
    class CompleteTestStyleCheckJob extends Job {
        /**
         * @param name the name of the job
         */
        public CompleteTestStyleCheckJob(String name) {
            super(name);
        }

        /** {@inheritDoc} */
        public boolean belongsTo(Object family) {
            if (family instanceof CompleteTestStyleCheckJob) {
                return true;
            }
            return super.belongsTo(family);
        }

        /**
         * {@inheritDoc}
         */
        protected IStatus run(IProgressMonitor monitor) {
            ProblemCont.instance.clear();
            if (isEnabled()) {
                // Check'em all!
                for (BaseContext context : CheckCont.getContexts()) {
                    for (Object obj : context.getAll()) {
                        check(obj);
                    }
                }
            }
            refresh();
            try {
                addTeststyleProblems(monitor);
            } catch (JBException e1) {
                // this might occur during create new version
                return new Status(IStatus.WARNING, Activator.PLUGIN_ID,
                        getName());
            }
            return new Status(IStatus.OK, Activator.PLUGIN_ID, getName());
        }
    }
    
    /** singleton */
    private static TeststyleHandler instance;

    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(TeststyleHandler.class);

    /**
     * Private constructor of the singleton
     */
    private TeststyleHandler() {
        DataEventDispatcher.getInstance().addProjectLoadedListener(this, false);
        addToListener();
    }

    /**
     * @return the singleton instance
     */
    public static TeststyleHandler getInstance() {
        if (instance == null) {
            instance = new TeststyleHandler();
        }
        return instance;
    }

    /**
     * {@inheritDoc}
     */
    public void handleProjectLoaded() {
        if (GeneralStorage.getInstance().getProject() == null) {
            return; // if there is no project, don't proceed
        }
        ExtensionHelper.initCheckConfiguration();
        doCompleteCheck();
        ProblemPropagator.INSTANCE.propagate();
    }

    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        if (!isEnabled()) {
            return;
        }
        
        boolean isUpdateInEditor = true;
        for (DataChangedEvent e : events) {
            if (e.getUpdateState() != UpdateState.onlyInEditor) {
                isUpdateInEditor = false;
                break;
            }
        }
        if (!isUpdateInEditor) {
            TestStyleJob tj = new TestStyleJob("Teststyle", events); //$NON-NLS-1$
            tj.setRule(
                    new MultiRule(new ISchedulingRule[]{
                        SingleJobRule.COMPLETENESSRULE,
                        SingleJobRule.TESTSTYLERULE}));
            JobUtils.executeJob(tj, null);
            for (Job job : Job.getJobManager().find(tj)) {
                if (job != tj) {
                    job.cancel();
                }
            }
            return;
        }
    }
    
    /**
     * @param po changed persistent object
     * @param dataState kind of modification
     * @param updateState determines the parts to update
     */
    private void handleChangedPo(IPersistentObject po, DataState dataState,
        UpdateState updateState) {
        // FIXME mbs Need a event for closing a project
        // Clean up first
        switch (dataState) {
            case Renamed: // fall through
            case Added: // fall through
            case StructureModified: // fall through
            case ReuseChanged:
            case Saved:
                ProblemCont.instance.remove(po);
                check(po);
                break;
            case Deleted: // fall through
                ProblemCont.instance.remove(po);
            default:
                break;
        }
        // always check the project after each change
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project != null) {
            ProblemCont.instance.remove(project);
            check(project);
        }
        if (po instanceof ISpecTestCasePO || po instanceof ITestSuitePO
                || po instanceof ITestJobPO
                || po instanceof IConditionalStatementPO
                || po instanceof IAbstractContainerPO) {
            INodePO node = (INodePO)po;
            Iterator<INodePO> iter = node.getAllNodeIter();
            handleChangedPo(iter, dataState, updateState);
        }
    }
    
    /**
     * @param iter iterator of child node
     * @param dataState kind of modification
     * @param updateState determines the parts to update
     */
    private void handleChangedPo(Iterator<INodePO> iter, DataState dataState,
            UpdateState updateState) {

        while (iter.hasNext()) {
            INodePO child = iter.next();
            handleChangedPo(child, dataState, updateState);
        }
    }

    /**
     * This method checks the Object obj with every check in the contexts of
     * this check for violation and decorates it appropriately.
     * 
     * @param obj
     *            The object that should be checked.
     */
    public void check(Object obj) {        
        // gather all checks for this
        BaseContext context = BaseContext.getFor(obj.getClass());
        List<BaseCheck> checks = CheckCont.getChecksFor(context);
        
        // Test the object!
        for (BaseCheck check : checks) {
            if (check.isActive(context) && check.hasError(obj)) {
                if (obj instanceof ITestDataCubePO) {
                    ProblemCont.instance.add(
                            ((ITestDataCubePO)obj).getId(), check);
                } else {
                    ProblemCont.instance.add(obj, check);
                }
            }
        }
    }

    /**
     * Checks every element with checks of the project for CheckStyle errors.
     */
    public void doCompleteCheck() {
        Job checkEverythingJob =
                new CompleteTestStyleCheckJob("TestStyle - complete"); //$NON-NLS-1$
        checkEverythingJob.setRule(
                new MultiRule(new ISchedulingRule[]{
                    SingleJobRule.COMPLETENESSRULE,
                    SingleJobRule.TESTSTYLERULE}));
        JobUtils.executeJob(checkEverythingJob, null);
        try {
            checkEverythingJob.join();
        } catch (InterruptedException e) {
            LOG.warn("Error waiting for Job TestStyle job", e); //$NON-NLS-1$
        }
    }

    /**
     * 
     * @param monitor a monitor or null<code>null</code>
     * @throws {@link JBException} if the project is 
     *         null in the {@link GeneralStorage}
     */
    private void addTeststyleProblems(IProgressMonitor monitor) 
            throws JBException {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project == null) {
            throw new JBException("Project is null", //$NON-NLS-1$
                    MessageIDs.E_PROJECT_NOT_FOUND);
        }
        final ITreeNodeOperation<INodePO> op = new TeststyleProblemAdder();
        final TreeTraverser traverser = new TreeTraverser(project, op);
        if (monitor != null) {
            traverser.setMonitor(monitor);
        }
        traverser.traverse(true);
    }

    /**
     * Adds the handler to the important listeners.
     */
    public void addToListener() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addDataChangedListener(this, true);
        ded.addProjectStateListener(this);
    }

    /**
     * Removes the handler to the important listeners.
     */
    public void removeFromListener() {
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.removeDataChangedListener(this);
        ded.removeProjectStateListener(this);
    }

    /**
     * Starts the whole routine (adds the handler and initialize the extensions
     * with its definitions)
     */
    public void start() {
        // Fill the CheckCont with initChecks
        ExtensionHelper.initChecks();

        // And add the handler to the listener, so that we can use events.
        if (isEnabled()) {
            addToListener();
        }
    }

    /**
     * Stops the whole checkstyle routine.
     */
    public void stop() {
        if (isEnabled()) {
            removeFromListener();
        }
        ProblemCont.instance.clear();
    }

    /**
     * Refreshes the decorators so that they start decorating the available 
     * resources again.
     */
    public void refresh() {
        DecoratorHandler.refresh();
    }
    
    /**
     * @return Is teststyle enabled for this project?
     */
    public boolean isEnabled() {
        IProjectPO project = GeneralStorage.getInstance().getProject();
        if (project == null) {
            return false;
        }
        return project.getProjectProperties().getCheckConfCont().getEnabled();
    }

    /** {@inheritDoc} */
    public void handleProjectStateChanged(ProjectState state) {
        switch (state) {
            case prop_modified:
                doCompleteCheck();
                break;
            case closed:
                ProblemCont.instance.clear();
                break;
            case opened:
            default:
                break;
        }
    }
}
