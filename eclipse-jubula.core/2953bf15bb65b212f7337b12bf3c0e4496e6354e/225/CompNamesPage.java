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
package org.eclipse.jubula.client.ui.rcp.views;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.ui.rcp.editors.AbstractTestCaseEditor;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.rcp.widgets.ComponentNamesTableComposite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.Page;


/**
 * @author BREDEX GmbH
 * @created Sep 10, 2008
 */
public class CompNamesPage extends Page implements ISelectionListener {
    
    /** the primary control for this page */
    private ComponentNamesTableComposite m_control;
    
    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        getControl().setFocus();
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleEditorSaved(IWorkbenchPart part, ISelection selection) {
        selectionChanged(part, selection);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        m_control = new ComponentNamesTableComposite(parent, SWT.NONE);
    }

    /**
     * {@inheritDoc}
     */
    public Control getControl() {
        return m_control;
    }

    /**
     * {@inheritDoc}
     */
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        if (!(selection instanceof StructuredSelection)) { 
            // e.g. in Jubula plugin-version you can open an java editor, 
            // that reacts on org.eclipse.jface.text.TextSelection, which
            // is not a StructuredSelection
            return;
        }
        IStructuredSelection sel = (IStructuredSelection)selection;
        IExecTestCasePO selectedExecNode = null;
        IWorkbenchPart selectedExecNodeOwner = null;
        Object selected = sel.getFirstElement();
        if (selected instanceof SearchResultElement) {
            selected = ((SearchResultElement) selected).getObject();
        }
        if (selected instanceof IExecTestCasePO) {
            selectedExecNode = (IExecTestCasePO) selected;
            selectedExecNodeOwner = part;
        }
            
        if (part instanceof IJBEditor) {
            m_control.getCellEdit().setComponentNameCache(
                ((IJBEditor)part).getCompNameCache());
        }
        m_control.setSelectedExecNodeOwner(selectedExecNodeOwner);
        m_control.setSelectedExecNode(selectedExecNode);
        if (part instanceof AbstractTestCaseEditor) {
            AbstractTestCaseEditor editor = (AbstractTestCaseEditor)part;
            if (editor.getEditorInput() instanceof ITestSuitePO) {
                m_control.controlPropagation(false);
            }
        }
    }
}
