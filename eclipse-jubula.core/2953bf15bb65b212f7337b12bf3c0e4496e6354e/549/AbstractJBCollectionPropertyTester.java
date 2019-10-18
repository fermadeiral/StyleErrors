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
package org.eclipse.jubula.client.ui.rcp.propertytester;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.ICommentPO;
import org.eclipse.jubula.client.core.model.IControllerPO;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IRefTestSuitePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.propertytester.AbstractBooleanPropertyTester;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.LocalSelectionClipboardTransfer;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TCEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TJEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.controllers.dnd.TSEditorDndSupport;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractJBEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestJobEditor;
import org.eclipse.jubula.client.ui.rcp.editors.TestSuiteEditor;
import org.eclipse.ui.IEditorPart;

/**
 * Property tester for selections in Abstract JB Editor. 
 * 
 * @created 02.11.2015
 */
public class AbstractJBCollectionPropertyTester extends
        AbstractBooleanPropertyTester {

    /** the id of the "isPasteAllowed" property */
    public static final String IS_PASTE_ALLOWED = "isPasteAllowed"; //$NON-NLS-1$
    
    /** the id of the "isCopyAllowed" property */
    public static final String IS_COPY_ALLOWED = "isCopyAllowed"; //$NON-NLS-1$
    
    /** the id of the "isCutAllowed" property */
    public static final String IS_CUT_ALLOWED = "isCutAllowed"; //$NON-NLS-1$
    
    /** canExtractOrSaveAs */
    public static final String CAN_EXTRACT_OR_SAVE = "canExtractOrSaveAs"; //$NON-NLS-1$
    
    /** canAddCondition */
    public static final String CAN_ADD_COND = "canAddCondition"; //$NON-NLS-1$
    
    /**
     * testable properties
     */
    private static final String[] PROPERTIES = new String[] { 
        IS_PASTE_ALLOWED, IS_COPY_ALLOWED, IS_CUT_ALLOWED,
        CAN_EXTRACT_OR_SAVE, CAN_ADD_COND};

    /**
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public boolean testImpl(Object receiver, String property, Object[] args) {
        Collection<? extends Object> selectionContents = 
                (Collection<? extends Object>)receiver;
        if (property.equals(IS_PASTE_ALLOWED)) {
            return testIsPasteAllowed(selectionContents);
        } else if (property.equals(IS_COPY_ALLOWED)) {
            return testIsCopyCutAllowed(selectionContents, false);
        } else if (property.equals(IS_CUT_ALLOWED)) {
            return testIsCopyCutAllowed(selectionContents, true);
        } else if (property.equals(CAN_EXTRACT_OR_SAVE)) {
            return testCanExtractOrSave(selectionContents);
        } else if (property.equals(CAN_ADD_COND)) {
            return canAddCondition(selectionContents);
        }
        return false;
    }
    
    /**
     * Tests whether a selection can be extracted or saved as
     * @param selection the selection
     * @return whether yes or no
     */
    private boolean testCanExtractOrSave(
            Collection<? extends Object> selection) {
        IEditorPart activeEditor = Plugin.getActiveEditor();
        if (!(activeEditor instanceof TestCaseEditor
                || activeEditor instanceof TestSuiteEditor)
                || selection == null
                || selection.isEmpty()) {
            return false;
        }
        for (Object o : selection) {
            if (!(o instanceof ICommentPO
                    || o instanceof IControllerPO
                    || o instanceof ICapPO
                    || o instanceof IExecTestCasePO)
                || o instanceof IEventExecTestCasePO) {
                return false;
            }
        }
        return nodesAndHaveSameParent(selection);
    }
    
    /**
     * Decides whether one can add a Conditional Statement to the place
     * @param selection the selection
     * @return whether
     */
    private boolean canAddCondition(Collection<? extends Object> selection) {
        IEditorPart activeEditor = Plugin.getActiveEditor();
        if (!(activeEditor instanceof TestCaseEditor
                || activeEditor instanceof TestSuiteEditor)
            || selection == null
            || selection.size() != 1) {
            return false;
        }
        Object sel = selection.iterator().next();
        if (sel instanceof ISpecTestCasePO
                || sel instanceof ITestSuitePO) {
            return true;
        }
        if (!(sel instanceof INodePO)) {
            return false;
        }
        INodePO par = ((INodePO) sel).getParentNode();
        return par != null && (par instanceof ISpecTestCasePO
                || par instanceof ITestSuitePO);
    }
    
    /**
     * 
     * @param selectionContents The selection contents to test.
     * @param isItCut true if is it a cut event
     * @return <code>true</code> if the copy command should be enabled for
     *         the given selection contents. Otherwise <code>false</code>.
     */
    private boolean testIsCopyCutAllowed(
            Collection<? extends Object> selectionContents,
            boolean isItCut) {
        
        IEditorPart activeEditor = Plugin.getActiveEditor();
        if (!(activeEditor instanceof AbstractJBEditor)
                || selectionContents == null
                || selectionContents.isEmpty()
                || (activeEditor.isDirty() && !isItCut)) {
            return false;
        }
        
        boolean isEnable = false;
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (activeEditor instanceof TestCaseEditor) {
            classes.add(IExecTestCasePO.class);
            classes.add(ICapPO.class);
            classes.add(IControllerPO.class);
            classes.add(ICommentPO.class);
            isEnable = getCopyActionEnablement(selectionContents, classes,
                    isItCut);
            isEnable &= nodesAndHaveSameParent(selectionContents);
        } else if (activeEditor instanceof TestSuiteEditor) {
            classes.add(IExecTestCasePO.class);
            classes.add(ICommentPO.class);
            classes.add(IControllerPO.class);
            isEnable = getCopyActionEnablement(selectionContents, classes,
                    isItCut);
        } else if (activeEditor instanceof TestJobEditor) {
            classes.add(IRefTestSuitePO.class);
            classes.add(ICommentPO.class);
            isEnable = getCopyActionEnablement(selectionContents, classes,
                    isItCut);
        }
        return isEnable;
    }
    
    /**
     * Checks if the collection is a collection of nodes with the same parent
     * @param selection the selection
     * @return whether
     */
    private boolean nodesAndHaveSameParent(Collection<? extends Object>
        selection) {
        INodePO par = null;
        for (Object obj : selection) {
            if (!(obj instanceof INodePO)) {
                return false;
            }
            if (par != null && (par != ((INodePO) obj).getParentNode())) {
                return false;
            }
            par = ((INodePO) obj).getParentNode();
        }
        return true;
    }

    /**
     * 
     * @param selectionContents The selection contents to test.
     * @return <code>true</code> if the paste command should be enabled for
     *         the given selection contents. Otherwise <code>false</code>.
     */
    private boolean testIsPasteAllowed(
            Collection<? extends Object> selectionContents) {
        
        LocalSelectionClipboardTransfer transfer = 
                LocalSelectionClipboardTransfer.getInstance();
        IEditorPart activeEditor = Plugin.getActiveEditor();
        if (!(activeEditor instanceof AbstractJBEditor)
                || selectionContents == null || selectionContents.isEmpty()) {
            return false;
        }
        
        AbstractJBEditor aJBEditor = (AbstractJBEditor)activeEditor;
        Object toDrop = aJBEditor.getEditorHelper().getClipboard()
                .getContents(transfer);
        
        if (toDrop == null || !(toDrop instanceof IStructuredSelection)
                || aJBEditor.getSelection() == null
                || !(aJBEditor.getSelection()
                        instanceof IStructuredSelection)
                || (transfer.getIsItCut() && !aJBEditor.getTreeViewer()
                        .equals(transfer.getSource()))) {

            return false;
        }
        
        IStructuredSelection targetSel =
                (IStructuredSelection)aJBEditor.getSelection();
        boolean isEnable = false;
        if (aJBEditor instanceof TestCaseEditor) {
            isEnable = getPasteActionEnablementForTCE(
                    (IStructuredSelection)toDrop, targetSel);
        } else if (aJBEditor instanceof TestSuiteEditor) {
            isEnable = getPasteActionEnablementForTSE(
                    (IStructuredSelection)toDrop, targetSel);
        } else if (aJBEditor instanceof TestJobEditor) {
            isEnable = getPasteActionEnablementForTJE(
                    (IStructuredSelection)toDrop, targetSel);
        }
        return isEnable;
    }
    
    /**
     * @param selectionContents The element what we would like to paste.
     * @param checkedClasses SelectionContents items should be on it.
     * @param isItCut it is true if the event is cut
     * @return <code>true</code> if the paste operation should be
     *         enabled for the given arguments. Otherwise, 
     *         <code>false</code>.
     */
    private boolean getCopyActionEnablement(
            Collection<? extends Object> selectionContents,
            List<Class<?>> checkedClasses, boolean isItCut) {

        INodePO par = null;
        for (Object node : selectionContents) {
            if (node == null || (node instanceof IEventExecTestCasePO
                    && isItCut)) {
                return false;
            }
            boolean contains = false;
            for (Class<?> checkedClass : checkedClasses) {
                if (checkedClass.isAssignableFrom(node.getClass())) {
                    contains = true;
                    break;
                }
            }
            if (!contains) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * @param toDrop    The copied elements.
     * @param targetSel The element where we would like to paste.
     * @return <code>true</code> if the paste operation should be
     *         enabled for the given arguments. Otherwise, 
     *         <code>false</code>.
     */
    private boolean getPasteActionEnablementForTCE(IStructuredSelection toDrop,
            IStructuredSelection targetSel) {
        
        for (Object target : targetSel.toList()) {
            if (target == null || !(target instanceof INodePO)
                    || !TCEditorDndSupport.validateCopy(toDrop,
                            (INodePO)target)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param toDrop    The copied elements.
     * @param targetSel The element where we would like to paste.
     * @return <code>true</code> if the paste operation should be
     *         enabled for the given arguments. Otherwise, 
     *         <code>false</code>.
     */
    private boolean getPasteActionEnablementForTSE(IStructuredSelection toDrop,
            IStructuredSelection targetSel) {
        
        for (Object target : targetSel.toList()) {
            if (target == null || !(target instanceof INodePO)
                    || !TSEditorDndSupport.validateCopy(toDrop,
                            (INodePO)target)) {
                return false;
            }
        }

        return true;
    }

    /**
     * @param toDrop    The copied elements.
     * @param targetSel The element where we would like to paste.
     * @return <code>true</code> if the paste operation should be
     *         enabled for the given arguments. Otherwise, 
     *         <code>false</code>.
     */
    private boolean getPasteActionEnablementForTJE(IStructuredSelection toDrop,
            IStructuredSelection targetSel) {
        
        for (Object target : targetSel.toList()) {
            if (target == null || !(target instanceof INodePO)
                    || !TJEditorDndSupport.validateCopy(toDrop,
                            (INodePO)target)) {
                return false;
            }
        }

        return true;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Class<? extends Object> getType() {
        return Collection.class;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String[] getProperties() {
        return PROPERTIES;
    }

}
