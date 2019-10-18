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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * @author BREDEX GmbH
 * @created May 03, 2013
 */
public class ChangeCtdsColumnUsagePage extends WizardPage
        implements ISelectionChangedListener, SelectionListener, KeyListener {

    /**
     * Map, which contains for each selectable parameter name
     * a corresponding set of execution Test Cases.
     */
    private final ExistingAndNewParameterData m_paramData;

    /** The old parameter name combo box. */
    private ComboViewer m_oldParamNameCombo;

    /** The new parameter name combo box. */
    private Combo m_newParamNameCombo;

    /**
     * @param paramData The data of the parameter names including a map of
     *                  the Test Cases.
     */
    public ChangeCtdsColumnUsagePage(
            ExistingAndNewParameterData paramData) {
        super(Messages.ChangeCtdsColumnUsageSelectPageTitle,
                Messages.ChangeCtdsColumnUsageSelectPageTitle, null);
        setDescription(Messages.ChangeCtdsColumnUsageSelectPageDescription);
        m_paramData = paramData;
        setPageComplete(false);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        // shrink height of dialog content to 100 pixel (originally about 200 pixel)
        if (parent.getLayoutData() instanceof GridData) {
            GridData gridData = (GridData) parent.getLayoutData();
            gridData.heightHint = 100;
        }

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout(2, true));
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setControl(composite);

        Group groupLeft = createGroup(composite,
                Messages.ChangeParameterUsageOldNameLabel);
        m_oldParamNameCombo = new ComboViewer(groupLeft, SWT.READ_ONLY);
        m_oldParamNameCombo.getCombo().setLayoutData(
                new GridData(GridData.FILL_HORIZONTAL));
        m_oldParamNameCombo.setContentProvider(
                new ParameterDescriptionContentProvider());
        m_oldParamNameCombo.setLabelProvider(
                new ParameterDescriptionLabelProvider());
        m_oldParamNameCombo.setInput(m_paramData);
        m_oldParamNameCombo.addSelectionChangedListener(this);

        Group groupRight = createGroup(composite,
                Messages.ChangeParameterUsageNewNameLabel);
        m_newParamNameCombo = new Combo(groupRight, SWT.READ_ONLY);
        m_newParamNameCombo.setLayoutData(
                new GridData(GridData.FILL_HORIZONTAL));
        m_newParamNameCombo.addSelectionListener(this);
        m_newParamNameCombo.addKeyListener(this);
    }

    /**
     * @param parent The parent composite.
     * @param text The title text.
     * @return A vertically centered and horizontal filled group
     *         with the given title text and horizontal.
     */
    private static Group createGroup(Composite parent, String text) {
        Group group = new Group(parent, SWT.NONE);
        group.setLayout(new GridLayout(1, false));
        group.setLayoutData(new GridData(
                SWT.FILL, SWT.CENTER, true, true));
        group.setText(text);
        return group;
    }

    /**
     * Called, when the selection of the old parameter name in the tree view changed.
     * {@inheritDoc}
     */
    public void selectionChanged(SelectionChangedEvent event) {
        IParamDescriptionPO paramDesc =
                getSelectedParamDescription();
        if (paramDesc != null) {
            m_newParamNameCombo.setItems(
                    m_paramData.setOldParamDescription(paramDesc));
            if (m_newParamNameCombo.getItemCount() == 1) {
                m_newParamNameCombo.select(0);
            }
        }
        m_newParamNameCombo.setEnabled(paramDesc != null);
        checkPageComplete();
    }

    /**
     * @return The selected parameter description of the old parameter names tree,
     *         or null if no parameter description is selected.
     */
    private IParamDescriptionPO getSelectedParamDescription() {
        StructuredSelection treeSelection =
                (StructuredSelection) m_oldParamNameCombo.getSelection();
        Object selection = treeSelection.getFirstElement();
        if (selection instanceof IParamDescriptionPO) {
            return (IParamDescriptionPO) selection;
        }
        return null;
    }

    /**
     * Called, when the combo box selection has been changed. Calls {@link #checkPageComplete()}.
     * {@inheritDoc}
     */
    public void widgetSelected(SelectionEvent e) {
        if (e.getSource() == m_newParamNameCombo) {
            checkPageComplete();
        }
    }

    /**
     * Do nothing.
     * {@inheritDoc}
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        // do nothing
    }

    /**
     * Do nothing.
     * {@inheritDoc}
     */
    public void keyPressed(KeyEvent e) {
        // do nothing
    }

    /**
     * Called, when something is typed into the combo box. Calls only {@link #checkPageComplete()}.
     * {@inheritDoc}
     */
    public void keyReleased(KeyEvent e) {
        checkPageComplete();
    }

    /**
     * Checks, if this page has been completed or not and set the complete state:
     * The new parameter name must not be empty.
     */
    private void checkPageComplete() {
        m_paramData.setNewParamName(m_newParamNameCombo.getText());
        setPageComplete(m_paramData.isComplete()
                && m_newParamNameCombo.isEnabled());
    }

    /**
     * Show help contend attached to wizard after selecting the ? icon,
     * or pressing F1 on Windows / Shift+F1 on Linux / Help on MAC.
     * {@inheritDoc}
     */
    public void performHelp() {
        Plugin.getHelpSystem().displayHelp(ContextHelpIds
                .SEARCH_REFACTOR_CHANGE_CTDS_COLUMN_USAGE_WIZARD_PAGE);
    }

}
