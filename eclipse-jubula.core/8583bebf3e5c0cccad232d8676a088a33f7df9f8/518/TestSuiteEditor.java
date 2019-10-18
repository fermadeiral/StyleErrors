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

import java.util.Iterator;

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.ICompNamesPairPO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TSEditorDropTargetListener;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.contentprovider.TestSuiteEditorContentProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;


/**
 * Editor for ExecTestCases
 *
 * @author BREDEX GmbH
 * @created 05.09.2005
 */
public class TestSuiteEditor extends AbstractTestCaseEditor {

    /** {@inheritDoc} */
    public void createPartControlImpl(Composite parent) {
        super.createPartControlImpl(parent);
        addDoubleClickListener(CommandIDs.OPEN_SPECIFICATION_COMMAND_ID, 
                getMainTreeViewer());
        if (!Plugin.getDefault().anyDirtyStar())  {
            checkAndRemoveUnusedTestData();
        }
    }

    /**
     * Sets the input of the tree viewer for specification.
     */
    public void setInitialInput() {
        getMainTreeViewer().setContentProvider(
                new TestSuiteEditorContentProvider());  
        ITestSuitePO rootPO = 
            (ITestSuitePO)getEditorHelper().getEditSupport().getWorkVersion();
        
        try {
            getTreeViewer().getTree().setRedraw(false);
            getTreeViewer().setInput(new ITestSuitePO[] {rootPO});
        } finally {
            getTreeViewer().getTree().setRedraw(true);
            getMainTreeViewer().expandAll();
            getMainTreeViewer().setSelection(new StructuredSelection(rootPO));
        }
        runLocalChecks();
    }
    
    /**
     * Checks, if all fields were filled in correctly.
     * @return True, if all fields were filled in correctly. 
     */
    protected boolean checkCompleteness() {
        ITestSuitePO tsWorkVersion = 
            (ITestSuitePO)getEditorHelper().getEditSupport().getWorkVersion();
        if (tsWorkVersion.getName() == null
                || StringConstants.EMPTY.equals(tsWorkVersion.getName())) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_CANNOT_SAVE_EDITOR, null, 
                    new String[]{Messages.TestCaseEditorNoTsuiteName});
            return false;
        } 
        if (tsWorkVersion.getName().startsWith(BLANK) 
            || tsWorkVersion.getName().endsWith(BLANK)) { 
            
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_CANNOT_SAVE_EDITOR, null, 
                    new String[]{Messages.TestCaseEditorWrongTsName});
            return false;
        }
        final IProjectPO project = GeneralStorage.getInstance().
            getProject();

        if (!tsWorkVersion.getName().equals(
                getEditorHelper().getEditSupport().getOriginal().getName())
            && ProjectPM.doesTestSuiteExists(project.getId(), 
                tsWorkVersion.getName())) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_CANNOT_SAVE_EDITOR, null,
                new String[]{Messages.TestCaseEditorDoubleTsuiteName});
            return false;
        }
        if (tsWorkVersion.getStepDelay() == -1) { // empty step delay
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_CANNOT_SAVE_EDITOR, null, 
                    new String[]{Messages.TestSuiteEditorEmptyStepDelay});
            return false;
        }
        Iterator iter = tsWorkVersion.getNodeListIterator();
        while (iter.hasNext()) {
            INodePO child = (INodePO)iter.next();
            if (child instanceof IExecTestCasePO) {
                IExecTestCasePO execTC = (IExecTestCasePO)child;
                if (!checkExecTCCompleteness(execTC)) {
                    
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Checks, if all fields were filled in correctly.
     * @return True, if all fields were filled in correctly.
     * @param testCase the checked testCase.
     */
    private boolean checkExecTCCompleteness(IExecTestCasePO testCase) {
        
        Object[] tcName = new Object[]{testCase.getName()};
        String name = testCase.getName();
        if (name == null || StringConstants.EMPTY.equals(name)) {
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX, 
                    tcName, new String[]{Messages.TestCaseEditorNoExecTcName});
            return false;
        } 
        if (testCase.getName().startsWith(BLANK) 
            || testCase.getName().endsWith(BLANK)) { 
            
            ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX, 
                    tcName, new String[]{
                        Messages.TestCaseEditorWrongExecTcName});
            return false;
        }
        for (ICompNamesPairPO compNamesPair : testCase.getCompNamesPairs()) {
            if (compNamesPair.getSecondName().equals(
                StringConstants.EMPTY)) {
                
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX,
                        tcName, new String[]{
                            NLS.bind(Messages.TestCaseEditorCompNameError,
                                compNamesPair.getFirstName()) 
                            + Messages.TestCaseEditorEmptyCompName});
                return false;
            }
            if (compNamesPair.getSecondName().startsWith(BLANK) 
                || compNamesPair.getSecondName().endsWith(BLANK)) { 
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_CANNOT_SAVE_EDITOR_TC_EX,
                        tcName, new String[]{NLS.bind(
                                Messages.TestCaseEditorCompNameError,
                                compNamesPair.getFirstName()) 
                            + Messages.TestCaseEditorWrongCompName});
                return false;
            }
        }
        return true;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getEditorPrefix() {
        return Messages.PluginTS;
    }
    
    /**
     * {@inheritDoc}
     */
    protected void fillContextMenu(IMenuManager mgr) {
        IStructuredSelection selection = getStructuredSelection();
        if (selection.getFirstElement() == null) {
            return;
        }
        MenuManager submenuAdd = new MenuManager(Messages.TestSuiteBrowserAdd,
                ADD_ID);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.REFERENCE_TC);
        mgr.add(submenuAdd);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_CONDITIONAL_STATEMENT);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_WHILE_DO);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_DO_WHILE);
        CommandHelper.createContributionPushItem(submenuAdd,
                RCPCommandIDs.NEW_ITERATE_LOOP);
        CommandHelper.createContributionPushItem(mgr,
                IWorkbenchCommandConstants.EDIT_COPY);
        CommandHelper.createContributionPushItem(mgr,
                IWorkbenchCommandConstants.EDIT_PASTE);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.TOGGLE_ACTIVE_STATE);
        mgr.add(new Separator());
        mgr.add(new GroupMarker("editing")); //$NON-NLS-1$
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.REVERT_CHANGES);
        mgr.add(new Separator());
        MenuManager submenuRefactor = new MenuManager(
                Messages.TestCaseEditorRefactor, REFACTOR_ID);
        mgr.add(submenuRefactor);
        CommandHelper.createContributionPushItem(submenuRefactor,
                RCPCommandIDs.EXTRACT_TESTCASE);
        CommandHelper.createContributionPushItem(submenuRefactor,
                RCPCommandIDs.REPLACE_WITH_TESTCASE);
        CommandHelper.createContributionPushItem(submenuRefactor,
                RCPCommandIDs.SAVE_AS_NEW);
        mgr.add(new Separator());
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.DELETE_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.OPEN_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                CommandIDs.SHOW_SPECIFICATION_COMMAND_ID);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.SHOW_WHERE_USED);
        
        collapseExpandItems(mgr);
        
        mgr.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        mgr.add(new Separator());
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.NEW_COMMENT);
        CommandHelper.createContributionPushItem(mgr,
                RCPCommandIDs.EDIT_COMMENT);
    }

    /**
     * {@inheritDoc}
     */
    public Image getDisabledTitleImage() {
        return IconConstants.DISABLED_TS_EDITOR_IMAGE;
    }
    
    /**
     * Sets the help to the HelpSystem.
     * @param parent the parent composite to set the help id to
     */
    protected void setHelp(Composite parent) {
        Plugin.getHelpSystem().setHelp(parent, 
            ContextHelpIds.TEST_SUITE_EDITOR);     
    }
    
    /**
     * {@inheritDoc}
     */
    protected DropTargetListener getViewerDropAdapter() {
        return new TSEditorDropTargetListener(this);
    }

    /** {@inheritDoc} */
    public Image getIcon() {
        return IconConstants.TS_EDITOR_IMAGE;
    }
}