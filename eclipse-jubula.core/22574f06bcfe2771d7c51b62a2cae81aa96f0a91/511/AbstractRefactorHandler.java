/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.ParamNameBPDecorator;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.dialogs.TestCaseTreeDialog;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author BREDEX GmbH
 * @created Jul 14, 2010
 */
public abstract class AbstractRefactorHandler 
    extends AbstractSelectionBasedHandler {

    /** The name of the new TC */
    private String m_newTCName = null;

    /** The category where to store the new TC */
    private INodePO m_selectedCategory = null;

    /**
     * @param editor
     *            the current editor
     * @return the new extracted test case name
     */
    private String getSuggestedName(AbstractTestCaseEditor editor) {
        String newName = InitialValueConstants.DEFAULT_TEST_CASE_NAME;
        final IStructuredSelection cs = (IStructuredSelection) editor
                .getTreeViewer().getSelection();
        if (cs.size() == 1) {
            Object e = cs.getFirstElement();
            if (e instanceof IExecTestCasePO) {
                String execName = ((IExecTestCasePO) e).getName();
                if (!StringUtils.isBlank(execName)) {
                    newName = execName;
                }
            }
        }
        return newName;
    }
    
    /**
     * @param newSpecTc
     *            new created specTestCase (after extraction)
     * @param mapper
     *            mapper to use for resolving of param names in this context
     */
    public static void registerParamNamesToSave(ISpecTestCasePO newSpecTc,
            ParamNameBPDecorator mapper) {
        for (IParamDescriptionPO desc : newSpecTc.getParameterList()) {
            mapper.registerParamDescriptions((ITcParamDescriptionPO) desc);
        }
    }

    /**
     * Asks from the user the new TC name and the category where to save it.
     *    Includes business logic to handle 'Back' button when choosing Category.
     * @param editor the Test Case Editor
     * @return whether the operation can commence
     */
    protected boolean askNewNameAndCategory(AbstractTestCaseEditor editor) {
        TestCaseTreeDialog dialog = new TestCaseTreeDialog(
                getActiveShell(),
                Messages.SelectCategoryDialogTitle,
                Messages.SelectCategoryDialogMessage,
                null,
                Messages.SelectCategoryDialogTitle,
                SWT.SINGLE, IconConstants.OPEN_TC_DIALOG_IMAGE);
        dialog.setReuseds(false);
        dialog.setOnlyCategories(true);
        dialog.setEnterTextLabel(Messages.RefactorTCOptTextLabel,
                getSuggestedName(editor));
        // not very nice, but at this point we make use of the knowledge
        // that what nodes are in the TreeViewer (we told this to the Dialog...)
        dialog.setPreSelect(GeneralStorage.getInstance().
                getProject().getSpecObjCont());
        dialog.open();
        if (dialog.getReturnCode() != IDialogConstants.OK_ID
                || dialog.getSelection().isEmpty()) {
            return false;
        }
        m_newTCName = dialog.getEnteredText();
        m_selectedCategory = dialog.getSelection().get(0);
        return m_newTCName != null && m_selectedCategory != null;
    }

    /**
     * @return the name of the new TC
     */
    protected String getNewTCName() {
        return m_newTCName;
    }

    /**
     * @return the category where to save the new TC
     */
    protected INodePO getCategory() {
        return m_selectedCategory;
    }

    /**
     * Performs preliminary checks
     * @param event the ExecutionEvent
     * @return whether the operation can commence
     */
    protected boolean prepareForRefactoring(ExecutionEvent event) {
        String newTcName = null;
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (!(activePart instanceof AbstractTestCaseEditor)) {
            return false;
        }
        final AbstractTestCaseEditor editor = (AbstractTestCaseEditor) 
                activePart;
        if (editor.isDirty()) {
            Dialog editorDirtyDlg = ErrorHandlingUtil
                    .createMessageDialog(MessageIDs.Q_SAVE_AND_EXTRACT);
            if (editorDirtyDlg.getReturnCode() != Window.OK) {
                return false;
            }
            editor.doSave(new NullProgressMonitor());
        }
        return true;
    }
}
