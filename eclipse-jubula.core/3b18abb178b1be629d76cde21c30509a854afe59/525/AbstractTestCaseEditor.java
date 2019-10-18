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
package org.eclipse.jubula.client.ui.rcp.editors;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.persistence.EntityManager;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ICoreRunnable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.businessprocess.CalcTypes;
import org.eclipse.jubula.client.core.businessprocess.CompNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.businessprocess.ObjectMappingEventDispatcher;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.businessprocess.UsedToolkitBP;
import org.eclipse.jubula.client.core.businessprocess.compcheck.CompletenessGuard;
import org.eclipse.jubula.client.core.businessprocess.db.TimestampBP;
import org.eclipse.jubula.client.core.commands.CAPRecordedCommand;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IDataChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IParamChangedListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.RecordModeState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IObjectMappingPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.model.ITimestampPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.persistence.CompNamePM;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMAlreadyLockedException;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.utils.AbstractNonPostOperatingTreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeNodeOperation;
import org.eclipse.jubula.client.core.utils.ITreeTraverserContext;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.core.utils.TreeTraverser;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.provider.DecoratingCellLabelProvider;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.controllers.TestExecutionContributor;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionTransfer;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TreeViewerContainerDragSourceListener;
import org.eclipse.jubula.client.ui.rcp.events.GuiEventDispatcher;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.contentprovider.TestCaseEditorContentProvider;
import org.eclipse.jubula.client.ui.rcp.provider.labelprovider.TooltipLabelProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;


/**
 * @author BREDEX GmbH
 * @created 13.10.2004
 */
@SuppressWarnings("synthetic-access")
public abstract class AbstractTestCaseEditor extends AbstractJBEditor 
    implements IParamChangedListener {
    
    /** central test data update listener */
    private CentralTestDataUpdateListener m_ctdUpdateListener =
            new CentralTestDataUpdateListener();
    /** update job for renewing the decorator*/
    private Job m_decoraterUpdateJob =
            Job.create("Update decoraters", new ICoreRunnable() { //$NON-NLS-1$
                @Override
                public void run(IProgressMonitor monitor) {
                    Plugin.getDisplay().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            IDecoratorManager dm = PlatformUI.getWorkbench()
                                    .getDecoratorManager();
                            try {
                                dm.setEnabled(Constants.CC_DECORATOR_ID, true);
                                dm.update(Constants.CC_DECORATOR_ID);
                            } catch (CoreException e) {
                                LOG.error(e.getLocalizedMessage(), e);
                            }
                        }
                    });
                }
            });
    /**
     * Creates the initial Context of this Editor.<br>
     * Subclasses may override this method. 
     * @param parent Composite
     */
    public void createPartControlImpl(Composite parent) {
        createSashForm(parent);
        setParentComposite(parent);
        // sets the input of the trees.
        setInitialInput();

        ColumnViewerToolTipSupport.enableFor(getTreeViewer());
        DecoratingCellLabelProvider lp = new DecoratingCellLabelProvider(
                new TooltipLabelProvider(), Plugin.getDefault()
                        .getWorkbench().getDecoratorManager()
                        .getLabelDecorator());
        getTreeViewer().setLabelProvider(lp);
        
        DataEventDispatcher ded = DataEventDispatcher.getInstance();
        ded.addPropertyChangedListener(this, true);
        addDragAndDropSupport(DND.DROP_MOVE, 
                new Transfer[] {LocalSelectionTransfer.getInstance()});
        getEditorHelper().addListeners();
        setActionHandlers();

        GuiEventDispatcher.getInstance()
            .addEditorDirtyStateListener(this, true);
        ded.addDataChangedListener(m_ctdUpdateListener, false);
        ded.addParamChangedListener(this, true);
    }
    
    /**
     * adds Drag and Drop support for the trees.
     * 
     * @param operations The DnD operation types to support.
     * @param transfers The DnD transfer types to support.
     */
    protected void addDragAndDropSupport(
            int operations, Transfer[] transfers) {
        getMainTreeViewer().addDragSupport(operations, transfers,
            new TreeViewerContainerDragSourceListener(getTreeViewer()));
        getMainTreeViewer().addDropSupport(operations, transfers, 
            getViewerDropAdapter()); 
    }

    /**
     * @return the viewer drop adapter
     */
    protected abstract DropTargetListener getViewerDropAdapter();

    /**
     * @param parent the parent of the SashForm.
     * @return the created SashForm.
     */
    protected SashForm createSashForm(Composite parent) {
        SashForm sashForm = new SashForm(parent, SWT.MULTI | SWT.VERTICAL);
        GridLayout compLayout = new GridLayout(1, true);
        compLayout.marginWidth = 0;
        compLayout.marginHeight = 0;
        sashForm.setLayout(compLayout);
        GridData gridData = new GridData (GridData.FILL_BOTH);
        sashForm.setLayoutData(gridData);        
        setControl(sashForm);
        createMainPart(sashForm);
        return sashForm;
    }
    
    /**
     * Refreshes all referenced Test Data Cubes within the context of this 
     * editor when Central Test Data changes.
     * 
     * @author BREDEX GmbH
     * @created 17.03.2011
     */
    private class CentralTestDataUpdateListener 
            implements IDataChangedListener {

        /**
         * {@inheritDoc}
         */
        public void handleDataChanged(IPersistentObject po,
                DataState dataState, UpdateState updateState) {

            if (po instanceof ITestDataCategoryPO 
                    && dataState == DataState.StructureModified 
                    && updateState != UpdateState.notInEditor) {

                ITreeNodeOperation<INodePO> refreshRefDataCubeOp =
                    new AbstractNonPostOperatingTreeNodeOperation<INodePO>() {
                        public boolean operate(
                                ITreeTraverserContext<INodePO> ctx,
                                INodePO parent, INodePO node,
                                boolean alreadyVisited) {
                            
                            if (node instanceof IParamNodePO) {
                                IParameterInterfacePO referencedCube = 
                                    ((IParamNodePO)node)
                                        .getReferencedDataCube();
                                if (referencedCube != null) {
                                    getEditorHelper().getEditSupport()
                                        .getSession().refresh(referencedCube);
                                }
                            }
                            return true;
                        }
                    };

                TreeTraverser refDataCubeRefresher = 
                    new TreeTraverser(
                            (INodePO)getEditorHelper().getEditSupport()
                                .getWorkVersion(),
                            refreshRefDataCubeOp, true, 2);
                refDataCubeRefresher.traverse(true);
                
            }
        }

        /** {@inheritDoc} */
        public void handleDataChanged(DataChangedEvent... events) {
            for (DataChangedEvent e : events) {
                handleDataChanged(e.getPo(), e.getDataState(),
                        e.getUpdateState());
            }
        }
    }

    @Override
    public void setInitialInput() {
        getMainTreeViewer().setContentProvider(
                new TestCaseEditorContentProvider());  
        
        INodePO workVersion = 
            (INodePO)getEditorHelper().getEditSupport().getWorkVersion();

        initTopTreeViewer(workVersion);
        
        runLocalChecks();
    }

    /**
     * run local completeness checks such as test data completeness
     */
    public void runLocalChecks() {
        INodePO node = (INodePO) getEditorHelper().getEditSupport()
                .getWorkVersion();
        node.clearProblems();
        for (Iterator<INodePO> childIterator =
                node.getAllNodeIter(); childIterator.hasNext();) {
            INodePO next = childIterator.next();
            next.clearProblems();
        }
        CompletenessGuard.checkAll(node, new NullProgressMonitor());
        m_decoraterUpdateJob.setPriority(Job.DECORATE);
        m_decoraterUpdateJob.schedule(300);

    }
    
    /**
     * @param root the root of the TreeViewer.
     */
    protected void initTopTreeViewer(INodePO root) {
        try {
            getMainTreeViewer().getTree().setRedraw(false);
            getMainTreeViewer().setInput(null);
            getMainTreeViewer().setInput(new INodePO[] {root});
        } finally {
            getMainTreeViewer().getTree().setRedraw(true);
            getMainTreeViewer().expandToLevel(2);
            setSelection(new StructuredSelection(root));
        }
    }

    /**
     * @param monitor IProgressMonitor
     */
    public void doSave(IProgressMonitor monitor) {
        if (!checkCompleteness()) {
            return;
        }
        monitor.beginTask(Messages.EditorsSaveEditors,
                IProgressMonitor.UNKNOWN);
        try {
            EditSupport editSupport = getEditorHelper().getEditSupport();
            removeIncorrectCompNamePairs();
            fixCompNameReferences();
            final IPersistentObject perObj = editSupport.getWorkVersion();

            getCompNameCache().clearUnusedCompNames((INodePO) perObj);

            if (perObj instanceof ISpecTestCasePO) {
                final IProjectPO project = GeneralStorage.getInstance()
                    .getProject();
                UsedToolkitBP.getInstance().addToolkit((ISpecTestCasePO)perObj, 
                    project);
            }
            TimestampBP.refreshTimestamp((ITimestampPO)perObj);
            editSupport.saveWorkVersion();
            getCompNameCache().clear();
            ObjectMappingEventDispatcher.updateObjectMappings(
                    (INodePO) perObj);
            DataEventDispatcher.getInstance().fireDataChangedListener(
                getWorkVersion(), DataState.Saved, UpdateState.all);

            getEditorHelper().resetEditableState();
            getEditorHelper().setDirty(false);
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
            try {
                reOpenEditor(((NodeEditorInput)getEditorInput()).getNode());
            } catch (PMException e1) {
                PMExceptionHandler.handlePMExceptionForEditor(e, this);
            }
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        } finally {
            monitor.done();
        }
    }
    
    /**
     * Replaces Component Name references with the referenced Component Names
     * and deletes any Component Name references that are no longer used.
     */
    private void fixCompNameReferences() {
        // Replace all reference guids with referenced guids
        INodePO rootNode = 
            (INodePO)getEditorHelper().getEditSupport().getWorkVersion();
        IComponentNameCache compNameCache = getCompNameCache();
        Iterator<INodePO> iter = rootNode.getNodeListIterator();
        while (iter.hasNext()) {
            INodePO nodePO = iter.next();
            if (nodePO instanceof IExecTestCasePO) {
                IExecTestCasePO exec = (IExecTestCasePO)nodePO;
                List<String> toRemove = 
                    new ArrayList<String>();
                List<ICompNamesPairPO> toAdd = 
                    new ArrayList<ICompNamesPairPO>();
                for (ICompNamesPairPO pair : exec.getCompNamesPairs()) {
                    String firstName = pair.getFirstName();
                    String secondName = pair.getSecondName();
                    IComponentNamePO firstCompNamePo = 
                        compNameCache.getResCompNamePOByGuid(firstName);
                    IComponentNamePO secondCompNamePo = 
                        compNameCache.getResCompNamePOByGuid(secondName);

                    if (!(firstCompNamePo.getGuid().equals(firstName)
                            && secondCompNamePo.getGuid().equals(secondName))) {
                        String componentType = pair.getType();
                        
                        toRemove.add(firstName);
                        toAdd.add(
                                PoMaker.createCompNamesPairPO(
                                        firstCompNamePo.getGuid(), 
                                        secondCompNamePo.getGuid(), 
                                        componentType));
                    }
                }
                for (String stringToRemove : toRemove) {
                    exec.removeCompNamesPair(stringToRemove);
                }
                for (ICompNamesPairPO pairToAdd : toAdd) {
                    exec.addCompNamesPair(pairToAdd);
                }
            } else if (nodePO instanceof ICapPO) {
                ICapPO capPo = (ICapPO)nodePO;
                String compNameGuid = capPo.getComponentName();
                IComponentNamePO compNamePo = 
                    compNameCache.getResCompNamePOByGuid(compNameGuid);
                if (compNamePo != null
                        && !compNamePo.getGuid().equals(compNameGuid)) {
                    capPo.setComponentName(compNamePo.getGuid());
                }
            }
        }

        
        // Delete all unused reference comp names
        CompNamePM.removeUnusedCompNames(
                GeneralStorage.getInstance().getProject().getId(),
                getEditorHelper().getEditSupport().getSession());
    }

    /**
     * Removes incorrect CompNamePair from ExecTC during saving.
     */
    private void removeIncorrectCompNamePairs() {
        INodePO node = 
            (INodePO)getEditorHelper().getEditSupport().getWorkVersion();
        if (node instanceof ISpecTestCasePO
                || node instanceof ITestSuitePO) {
            for (Iterator<INodePO> it = node.getAllNodeIter(); it.hasNext(); ) {
                INodePO o = it.next();
                if (o instanceof IExecTestCasePO) {
                    IExecTestCasePO exec = (IExecTestCasePO)o;
                    ICompNamesPairPO [] pairArray = 
                        exec.getCompNamesPairs().toArray(
                            new ICompNamesPairPO[
                                exec.getCompNamesPairs().size()]);
                    for (ICompNamesPairPO pair : pairArray) {
                        searchAndSetComponentType(pair);
                        if (!CompNamesBP.isValidCompNamePair(pair)) {
                            exec.removeCompNamesPair(pair.getFirstName());
                        }
                    }
                }
            }
        } else {
            LOG.error(Messages.WrongEditSupportInTestCaseEditor 
                + StringConstants.COLON + StringConstants.SPACE + node);
        }
    }
    
    
    /**
     * 
     * @param pair the current compNamesPairPO
     */
    private void searchAndSetComponentType(final ICompNamesPairPO pair) {
        if (!StringUtils.isEmpty(pair.getType())) {
            return;
        }
        EditSupport supp = getEditorHelper().getEditSupport();
        IPersistentObject workVersion = supp.getWorkVersion();
        if (workVersion instanceof INodePO) {
            CalcTypes.recalculateCompNamePairs(getCompNameCache(),
                    (INodePO) workVersion);
            
        } else {
            LOG.warn("class not supported for recalculating component pair " //$NON-NLS-1$
                    + workVersion.getClass().getCanonicalName());
        }
    }

    /**
     * Checks, if all fields were filled in correctly.
     * @return True, if all fields were filled in correctly. 
     */
    protected boolean checkCompleteness() {
        ISpecTestCasePO testCase = (ISpecTestCasePO)getEditorHelper()
                .getEditSupport().getWorkVersion();
        final Integer mId = MessageIDs.E_CANNOT_SAVE_EDITOR_TC_SP;
        if (testCase.getName() == null
                || StringConstants.EMPTY.equals(testCase.getName())) {
            ErrorHandlingUtil.createMessageDialog(mId, null,
                    new String[] { Messages.TestCaseEditorNoTcName });
            return false;
        }
        if (testCase.getName().startsWith(BLANK)
                || testCase.getName().endsWith(BLANK)) {
            ErrorHandlingUtil.createMessageDialog(mId, null,
                    new String[] { Messages.TestCaseEditorWrongTcName });
            return false;
        }
        Iterator<INodePO> iter = testCase.getNodeListIterator();
        while (iter.hasNext()) {
            Object node = iter.next();
            if (node instanceof ICapPO) {
                ICapPO cap = (ICapPO)node;
                if (cap.getName() == null
                        || StringConstants.EMPTY.equals(cap.getName())) {
                    ErrorHandlingUtil.createMessageDialog(mId, null,
                            new String[] { Messages.TestCaseEditorNoCapName });
                    return false;
                }
                if (cap.getName().startsWith(BLANK)
                        || cap.getName().endsWith(BLANK)) {
                    ErrorHandlingUtil.createMessageDialog(mId, null, 
                            new String[] { NLS.bind(
                                    Messages.TestCaseEditorWrongTsName,
                                    cap.getName()) });
                    return false;
                }
                if (componentHasDefaultMapping(cap)) { 
                    continue; // there is no component name
                }
                if (cap.getComponentName() == null
                        || StringConstants.EMPTY.equals(
                                cap.getComponentName())) {
                    ErrorHandlingUtil.createMessageDialog(mId, null, 
                            new String[] { NLS.bind(
                                    Messages.TestCaseEditorNoCompName,
                                    cap.getName()) });
                    return false;
                }
                if (cap.getComponentName().startsWith(BLANK) || cap
                                .getComponentName().endsWith(BLANK)) {
                    ErrorHandlingUtil.createMessageDialog(mId, null,
                            new String[] { NLS.bind(
                                    Messages.TestCaseEditorWrongCompName2,
                                    cap.getName()) });
                    return false;
                }
            }
        }
        for (Object object : testCase.getAllEventEventExecTC()) {
            IEventExecTestCasePO eventTC = (IEventExecTestCasePO)object;
            if (StringConstants.EMPTY.equals(eventTC.getName())) {
                ErrorHandlingUtil.createMessageDialog(mId, null,
                        new String[] { Messages.TestCaseEditorNoEventTcName });
                return false;
            }
            if (eventTC.getName().startsWith(BLANK)
                    || eventTC.getName().endsWith(BLANK)) {
                ErrorHandlingUtil.createMessageDialog(mId, null,
                        new String[] { NLS.bind(
                                Messages.TestCaseEditorWrongEhName,
                                eventTC.getName()) });
                return false;
            }
        }
        return checkRefsAndCompNames(testCase);
    }

    /**
     * 
     * @param cap
     *            the cap from which wee need the component type
     * @return if the component has a default mapping
     */
    private boolean componentHasDefaultMapping(ICapPO cap) {
        Component component = ComponentBuilder.getInstance()
                .getCompSystem().findComponent(cap.getComponentType());
        if (component.isConcrete()) {
            return ((ConcreteComponent) component).hasDefaultMapping();
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void checkMasterSessionUpToDate() {
        super.checkMasterSessionUpToDate();
        ITimestampPO node = 
            (ITimestampPO)getEditorHelper().getEditSupport().getWorkVersion();
        TimestampBP.refreshEditorNodeInMasterSession(node);
    }
    
    /**
     * Checks if testdata of the current original testCase contains references.
     * <p>Checks also the propagated compName.
     * @param testCase the current testCase
     * @return true, if data was not mixed.
     */
    private boolean checkRefsAndCompNames(ISpecTestCasePO testCase) {
        ITDManager mgr = testCase.getDataManager();
        for (int row = 0; row < mgr.getDataSetCount(); row++) {
            IDataSetPO row2 = mgr.getDataSet(row);
            for (int col = 0; col < row2.getColumnCount(); col++) {
                String data = row2.getValueAt(col);
                String uniqueId = mgr.getUniqueIds().get(col);
                IParamDescriptionPO desc = 
                    testCase.getParameterForUniqueId(uniqueId);
                ParamValueConverter conv = 
                    new ModelParamValueConverter(data, testCase, desc);
                for (String refName : conv.getNamesForReferences()) {
                    ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_CANNOT_SAVE_EDITOR_TC_SP, null, 
                        new String[]{NLS.bind(
                                Messages.TestCaseEditorContReference,
                            refName)});  
                    return false;
                }
                
            }
        }
        Iterator<INodePO> iter = testCase.getAllNodeIter();
        while (iter.hasNext()) {
            INodePO nodePO = iter.next();
            if (nodePO instanceof IExecTestCasePO) {
                IExecTestCasePO exec = (IExecTestCasePO)nodePO;
                for (ICompNamesPairPO pair : exec.getCompNamesPairs()) {
                    if (pair.getSecondName() == null || StringConstants.EMPTY
                        .equals(pair.getSecondName())) {

                        ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.E_CANNOT_SAVE_EDITOR_TC_SP, null, 
                            new String[]{NLS.bind(
                                    Messages.TestCaseEditorMmissingCompName,
                                new Object[]{StringHelper.getInstance()
                                    .getMap().get(pair.getType()), 
                                    pair.getFirstName(), 
                                    exec.getName()})});
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    protected void fillContextMenu(IMenuManager mgr) {
        if (getStructuredSelection().getFirstElement() == null) {
            return;
        }
        MenuManager submenuAdd = new MenuManager(Messages.TestSuiteBrowserAdd,
                ADD_ID);
        MenuManager submenuRefactor = new MenuManager(
                Messages.TestCaseEditorRefactor, REFACTOR_ID);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.REFERENCE_TC);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.NEW_CAP);

        mgr.add(submenuAdd);
        CommandHelper.createContributionPushItem(mgr,
                IWorkbenchCommandConstants.EDIT_CUT);
        CommandHelper.createContributionPushItem(mgr,
                IWorkbenchCommandConstants.EDIT_COPY);
        CommandHelper.createContributionPushItem(mgr,
                IWorkbenchCommandConstants.EDIT_PASTE);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.TOGGLE_ACTIVE_STATE);
        mgr.add(new Separator());
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.EDIT_PARAMETERS);
        mgr.add(new GroupMarker("editing")); //$NON-NLS-1$
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.REVERT_CHANGES);
        mgr.add(new Separator());
        mgr.add(submenuRefactor);
        mgr.add(new Separator());
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.DELETE_COMMAND_ID);
        mgr.add(CommandHelper.createContributionItem(
                RCPCommandIDs.FIND,
                null, Messages.FindContextMenu,
                CommandContributionItem.STYLE_PUSH));
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.OPEN_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.SHOW_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.SHOW_WHERE_USED);
        
        collapseExpandItems(mgr);
        
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_CONDITIONAL_STATEMENT);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_WHILE_DO);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_DO_WHILE);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_ITERATE_LOOP);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_TESTCASE);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.ADD_EVENT_HANDLER);
        CommandHelper.createContributionPushItem(submenuRefactor,
                RCPCommandIDs.EXTRACT_TESTCASE);
        CommandHelper.createContributionPushItem(submenuRefactor,
                RCPCommandIDs.REPLACE_WITH_TESTCASE);
        CommandHelper.createContributionPushItem(submenuRefactor,
                RCPCommandIDs.SAVE_AS_NEW);
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        mgr.add(new Separator());
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.NEW_COMMENT);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.EDIT_COMMENT);
    }

    /**
     * Adds the collapse and expand items
     * @param mgr the menu manager
     */
    protected void collapseExpandItems(IMenuManager mgr) {
        mgr.add(new Separator());
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.EXPAND_TREE_ITEM_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.COLLAPSE_TREE_ITEM_COMMAND_ID);
        mgr.add(new Separator());
    }

    /**
     * Cleanup on closing.
     */
    public void dispose() {
        try {
            DataEventDispatcher ded = DataEventDispatcher.getInstance();
            ded.removeParamChangedListener(this);
            ded.removeDataChangedListener(m_ctdUpdateListener);
            if (CAPRecordedCommand.getRecordListener() == this) {
                CAPRecordedCommand.setRecordListener(null);
                TestExecutionContributor.getInstance().getClientTest()
                        .resetToTesting();
            }

            if (getEditorSite() != null && getEditorSite().getPage() != null) {
                ded.fireRecordModeStateChanged(RecordModeState.notRunning);
                removeGlobalActionHandler();
            }
        } finally {
            super.dispose();
        }
    }

    /**
     * Removes global action handler to prevent memory leaks. Only clears the 
     * action handlers if handlers for this editor are currently in use. This 
     * means that if another editor has registered its own handlers, then that
     * editor is responsible for clearing its own action handlers.
     */
    private void removeGlobalActionHandler() {
        getEditorSite().getActionBars().updateActionBars();
    }
    
    /** {@inheritDoc} */
    public void handleDataChanged(DataChangedEvent... events) {
        for (DataChangedEvent e : events) {
            handleDataChanged(e.getPo(), e.getDataState(), e.getUpdateState());
        }
    }
    
    /** {@inheritDoc} */
    public void handlePropertyChanged(boolean isCompNameChanged) {
        super.handlePropertyChanged(isCompNameChanged);
        runLocalChecks();
    }
    
    /** {@inheritDoc} */
    public void handleParamChanged(Object caller) {
        runLocalChecks();
        refresh();
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleDataChanged(IPersistentObject po, DataState dataState,
            UpdateState updState) {
        
        if (po instanceof INodePO) {
            INodePO changedNode = (INodePO)po;
            INodePO editorNode = (INodePO)getEditorHelper().getEditSupport()
                    .getWorkVersion();
            boolean isVisibleInEditor = editorNode.indexOf(changedNode) > -1;
            isVisibleInEditor |= contains(editorNode, changedNode);
            if (editorNode instanceof ISpecTestCasePO) {
                isVisibleInEditor |= ((ISpecTestCasePO)editorNode)
                        .getAllEventEventExecTC().contains(po);
            }
            switch (dataState) {
                case Added:
                    if (isVisibleInEditor) {
                        handleNodeAdded(changedNode);
                    }
                    break;
                case Deleted:
                    if (!(po instanceof IProjectPO)) {
                        isVisibleInEditor = true;
                        refresh();
                    } 
                    break;
                case Renamed:
                    createPartName();
                    break;
                case StructureModified:
                    if (isVisibleInEditor) {
                        getEditorHelper().setDirty(true);
                    }
                    if (!handleStructureModified(po)) {
                        return;
                    }
                    break;
                default:
            }
            if (isVisibleInEditor) {
                runLocalChecks();
            }
            getEditorHelper().handleDataChanged(po, dataState);
        } else if (po instanceof IComponentNamePO
                && updState != UpdateState.onlyInEditor) {
            handleCompNameChanged((IComponentNamePO) po, dataState);
        } else if (po instanceof IObjectMappingPO 
                && updState != UpdateState.onlyInEditor) {
            IPersistentObject workversion =
                    getEditorHelper().getEditSupport().getWorkVersion();
            if (workversion instanceof ITestSuitePO) {
                EntityManager session =
                        getEditorHelper().getEditSupport().getSession();
                session.refresh(session.find(po.getClass(), po.getId()));
            }
            runLocalChecks();
        }
    }
    
    /**
     * Handles Component Name changes
     * @param cN the Component Name
     * @param state the DataState
     */
    private void handleCompNameChanged(IComponentNamePO cN, DataState state) {
        String guid = cN.getGuid();
        switch (state) {
            case Renamed:
                getCompNameCache().renamedCompName(cN.getGuid(), cN.getName());
                break;
            default:
        }
    }
    
    /**
     * @param parent node
     * @param changedNode searched node
     * @return <code>true</code> if parent node contains changedNode node.
     *          Otherwise return <code>false</code>.
     */
    private boolean contains(INodePO parent, INodePO changedNode) {
        for (Iterator it = parent.getAllNodeIter(); it.hasNext(); ) {
            if (it.next().equals(changedNode)) {
                return true;
            }
        }
        return false;
    }

     /**
      * Handles a PO that has been modified.
      * 
      * @param po The modified object.
      * @return <code>false</code> if an error occurs during handling. 
      *         Otherwise, <code>true</code>.
      */
    private boolean handleStructureModified(IPersistentObject po) {
        if (po instanceof ISpecTestCasePO) {
            final ISpecTestCasePO specTestCasePO = (ISpecTestCasePO)po;
            final INodePO workVersion = (INodePO)getEditorHelper()
                .getEditSupport().getWorkVersion();
            final List<IExecTestCasePO> execTestCases = NodePM.
                getInternalExecTestCases(specTestCasePO.getGuid(), 
                    specTestCasePO.getParentProjectId());
            if (!execTestCases.isEmpty() && containsWorkVersionReuses(
                    workVersion, specTestCasePO)) {
                
                if (Plugin.getActiveEditor() != this && isDirty()) {
                    ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.I_SAVE_AND_REOPEN_EDITOR, 
                        new Object[]{getTitle(), 
                            specTestCasePO.getName()}, null);
                    return false;
                }
                try {
                    reOpenEditor(getEditorHelper().getEditSupport()
                            .getOriginal());
                } catch (PMException e) {
                    ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_REFRESH_FAILED, null,
                        new String[] {Messages.ErrorMessageEDITOR_CLOSE});
                    getSite().getPage().closeEditor(this, false);
                }  
                return false;
            }
        }
        return true;
    }
    
    /**
     * @param root node, where starts the validation
     * @param specTc changed specTc
     * @return if editor contains an reusing testcase for given specTestCase
     */
    private static boolean containsWorkVersionReuses(INodePO root, 
        ISpecTestCasePO specTc) {
        
        final Iterator<INodePO> it = root.getNodeListIterator();
        final List <INodePO> childList = IteratorUtils.toList(it);
        // Add EventHandler to children List!
        if (root instanceof ISpecTestCasePO) { 
            final ISpecTestCasePO rootSpecTc = (ISpecTestCasePO)root;
            childList.addAll(rootSpecTc.getAllEventEventExecTC());
        }
        for (INodePO child : childList) {
            if (child instanceof IExecTestCasePO) {
                final IExecTestCasePO execTc = (IExecTestCasePO)child;
                if (specTc.equals(execTc.getSpecTestCase())) {
                    return true;
                }
                if (containsWorkVersionReuses(execTc, specTc)) {
                    return true;
                }
            }
        }    
        return false;
    }
    
    /**
     * Sets the EventHandler properties.
     * @param eventHandler the EventHandlerTc
     * @param refName the referenced name of EventHandler
     * @param eventType the event type
     * @param reentryType the reentry type
     * @param maxRetries the maximum number of retries
     */
    void setEventHandlerProperties(
        IEventExecTestCasePO eventHandler,
        String refName, String eventType,
        String reentryType,
        Integer maxRetries) {
        
        eventHandler.setName(refName);

        eventHandler.setEventType(eventType);
        ReentryProperty[] reentryProps = ReentryProperty.REENTRY_PROP_ARRAY;
        for (int i = 0; i < reentryProps.length; i++) {
            if (String.valueOf(reentryProps[i]).equals(reentryType)) {
                eventHandler.setReentryProp(reentryProps[i]);
                break;
            }
        }
        Assert.verify(eventHandler.getReentryProp() != null,
            Messages.ErrorWhenSettingReentryProperty 
            + StringConstants.EXCLAMATION_MARK);

        eventHandler.setMaxRetries(maxRetries);
    }
    
    /**
     * synchronizes the list of parameter unique ids in TDManagers of ExecTestCases
     * and the associated parameter list
     * @param root root node of editor
     */
    private void updateTDManagerOfExecTestCases(INodePO root) {
        Iterator<INodePO> it = root.getAllNodeIter();
        while (it.hasNext()) {
            INodePO child = it.next();
            if (child instanceof IExecTestCasePO) {
                ((IExecTestCasePO)child).synchronizeParameterIDs();
            }
        }     
    }

    /**
     * Checks and removes unused TestData of IExecTestCasePOs.
     */
    protected final void checkAndRemoveUnusedTestData() {
        final EditSupport editSupport = getEditorHelper().getEditSupport();
        final IPersistentObject workVersion = editSupport.getWorkVersion();
        if (!(workVersion instanceof INodePO)) {
            return;
        }
        final INodePO nodePO = (INodePO)workVersion;
        final List<IExecTestCasePO> execsWithUnusedTestData = TestCaseParamBP
            .getExecTcWithUnusedTestData(nodePO);
        if (execsWithUnusedTestData.isEmpty()) {
            return;
        }
        
        try {
            editSupport.lockWorkVersion();
            getEditorHelper().setDirty(true);
            updateTDManagerOfExecTestCases(nodePO);
            doSave(new NullProgressMonitor());
        } catch (PMAlreadyLockedException e) {
            // ignore, we are only doing housekeeping
        } catch (PMException e) {
            PMExceptionHandler.handlePMExceptionForMasterSession(e);
        }
    }

    /**
     * 
     * @return the receiver's current selection, if it is an 
     *         {@link IStructuredSelection}. Otherwise, returns an empty 
     *         selection.
     */
    protected IStructuredSelection getStructuredSelection() {
        ISelection selection = getSelection();
        if (selection instanceof IStructuredSelection) {
            return (IStructuredSelection)selection;
        }
        
        return StructuredSelection.EMPTY;
    }
    
    /**
     * Refreshes the viewer and updates the expansion state and selection
     * based on the added node.
     * 
     * @param addedNode The node that has been added.
     */
    private void handleNodeAdded(INodePO addedNode) {
        refresh();
        setSelection(new StructuredSelection(addedNode));
    }

    /**
     * @return the icon of the editor
     */
    public abstract Image getIcon();
}
