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
package org.eclipse.jubula.client.ui.rcp.search.page;

import java.util.List;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.DialogPage;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.data.FieldName;
import org.eclipse.jubula.client.ui.rcp.search.data.SearchOptions;
import org.eclipse.jubula.client.ui.rcp.search.data.TypeName;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.search.ui.ISearchPage;
import org.eclipse.search.ui.ISearchPageContainer;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * @author BREDEX GmbH
 * @created Jul 26, 2010
 */
public abstract class AbstractSearchPage extends DialogPage implements
        ISearchPage, SelectionListener {

    /** number of columns = 4 */
    private static final int NUM_COLUMNS = 4;

    /** vertical spacing */
    private static final int VERTICAL_SPACING = 10;

    /** search Text Field */
    private Combo m_searchStringCombo;

    /** CheckbBox to select use regular expression */
    private Button m_useRegExCheck;

    /** CheckbBox to select use search case sensitive */
    private Button m_caseSensitivCheck;

    /** group named "Use Selection In" */
    private Group m_groupUseSelection;

    /** CheckbBox to select use search in test suit browser */
    private Button m_scopeTestSuitBrowserCheck;

    /** CheckbBox to select use search in test case browser */
    private Button m_scopeTestCaseBrowserCheck;

    /** radio button to select use search in selected nodes */
    private Button m_scopeWholeProjectRadio;

    /** radio button to select use search in selected nodes */
    private Button m_scopeSelectedNodesRadio;

    /** radio button to select use search in master test case browser */
    private Button m_scopeTestCaseBrowserMasterRadio;

    /** radio button to select use search in all test case browser */
    private Button m_scopeTestCaseBrowserAllRadio;

    /** Check box to select searching in reused Projects. */
    private Button m_scopeSearchInReusedProjects;

    /** {@inheritDoc} */
    public void createControl(Composite parent) {
        getButtonSelections().reset();
        Composite pageContent = new Composite(parent, SWT.NONE);
        
        GridLayout layout = new GridLayout();
        layout.numColumns = NUM_COLUMNS;
        layout.verticalSpacing = VERTICAL_SPACING;
        layout.marginWidth = LayoutUtil.MARGIN_WIDTH;
        layout.marginHeight = LayoutUtil.MARGIN_HEIGHT;
        pageContent.setLayout(layout);
        
        Label findLabel = new Label(pageContent, SWT.NONE);
        findLabel.setText(Messages.SimpleSearchPageSearch);

        m_searchStringCombo = new Combo(pageContent, SWT.BORDER);
        m_searchStringCombo.setLayoutData(
                createGridData(NUM_COLUMNS - 1, true));
        m_searchStringCombo.setItems(
                getSearchData().getRecent().toArray(
                        new String[getSearchData().getRecent().size()]));
        if (m_searchStringCombo.getItemCount() == 0) {
            m_searchStringCombo.setText(Messages.SimpleSearchPagePhrase);
        } else {
            m_searchStringCombo.select(0);
        }
        // select the hole search text
        m_searchStringCombo.setSelection(
                new Point(0, m_searchStringCombo.getTextLimit()));
        m_searchStringCombo.forceFocus();
        // create the group boxes for search
        DataBindingContext dbc = new DataBindingContext();
        createSearchOptionsGroup(pageContent);
        createSearchInGroup(dbc, pageContent);
        createSearchForGroup(dbc, pageContent);
        createScopeGroup(dbc, pageContent);
        dbc.updateTargets();
        setControl(pageContent);
        setEnabledButtons();
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.FIND_DIALOG);
    }

    /**
     * creates the Direction Group
     * @param parent the parent to use
     */
    private void createSearchOptionsGroup(Composite parent) {
        Group group = createGroup(
                parent, Messages.SimpleSearchPageOptionGroupHeader, 2);
        group.setLayoutData(createGridData(NUM_COLUMNS, true));
        m_caseSensitivCheck = createCheck(
                group, Messages.SimpleSearchPageCaseSen, false);
        m_useRegExCheck = createCheck(
                group, Messages.SimpleSearchPageRegEx, false);
    }

    /**
     * Create search for group. Subclasses may override.
     * 
     * @param dbc
     *            The data binding context.
     * @param parent
     *            the parent
     */
    private void createSearchForGroup(DataBindingContext dbc, 
        Composite parent) {
        Group group = createGroup(parent,
            Messages.SimpleSearchPageSearchForGroupHeader, 3);
        group.setLayoutData(createGridData(NUM_COLUMNS, true));
        for (TypeName searchableType : getSearchData().getSearchableTypes()) {
            createTypeCheck(dbc, group, searchableType);
        }
    }
    
    /**
     * Create search in group. Subclasses may override.
     * 
     * @param dbc
     *            The data binding context.
     * @param parent
     *            the parent
     */
    private void createSearchInGroup(DataBindingContext dbc, 
        Composite parent) {
        final FieldName[] searchableFieldNames = 
            getSearchData().getSearchableFieldNames();
        if (searchableFieldNames != null) {
            Group group = createGroup(parent,
                Messages.SimpleSearchPageSearchInGroupHeader, 3);
            group.setLayoutData(createGridData(NUM_COLUMNS, true));
            for (FieldName searchableField : searchableFieldNames) {
                createFieldCheck(dbc, group, searchableField);
            }
        }
    }

    /**
     * subclasses may override
     * @param dbc The data binding context.
     * @param parent the parent
     */
    private void createScopeGroup(DataBindingContext dbc, Composite parent) {
        Group group = createGroup(
                parent, Messages.SimpleSearchPageScope, 3);
        group.setLayoutData(createGridData(4, true));
        m_scopeWholeProjectRadio = createRadio(
                group, Messages.SimpleSearchPageScopeWholeProject, true);
        m_scopeWholeProjectRadio.addSelectionListener(this);
        m_scopeSelectedNodesRadio = createRadio(
                group, Messages.SimpleSearchPageScopeSelectedNodes, false);
        m_scopeSelectedNodesRadio.addSelectionListener(this);
        m_scopeSearchInReusedProjects = createCheck(
                group,
                Messages.SimpleSearchPageScopeSearchInReusedProjects, false);
        m_groupUseSelection = createGroup(
                group, Messages.SimpleSearchPageScopeUseSelectionIn, 3);
        m_scopeTestSuitBrowserCheck = createCheck(
                m_groupUseSelection,
                Messages.SimpleSearchPageScopeTestSuiteBrowserCheck,
                true);
        m_scopeTestSuitBrowserCheck.setLayoutData(createGridData(2, true));
        m_scopeTestCaseBrowserCheck = createCheck(
                m_groupUseSelection,
                Messages.SimpleSearchPageScopeTestCaseBrowserCheck,
                true);
        m_scopeTestCaseBrowserCheck.setLayoutData(createGridData(3, true));
        m_scopeTestCaseBrowserCheck.addSelectionListener(this);
        m_scopeTestSuitBrowserCheck.addSelectionListener(this);
        m_scopeTestCaseBrowserMasterRadio = createRadio(
                m_groupUseSelection,
                Messages.SimpleSearchPageScopeTestCaseBrowserMainRadio,
                true);
        GridData gridData = createGridData(1, false);
        gridData.horizontalIndent = 20;
        m_scopeTestCaseBrowserMasterRadio.setLayoutData(gridData);
        m_scopeTestCaseBrowserAllRadio = createRadio(
                m_groupUseSelection,
                Messages.SimpleSearchPageScopeTestCaseBrowserAllRadio,
                false);
    }

    /**
     * @param dbc
     *            the data binding context
     * @param parent
     *            the parent
     * @param searchableType
     *            the type to search for
     */
    private void createTypeCheck(DataBindingContext dbc, Composite parent,
            TypeName searchableType) {
        Button check = new Button(parent, SWT.CHECK);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        check.setLayoutData(gd);
        check.setText(searchableType.getName());

        IObservableValue guiElement = WidgetProperties.selection()
                .observe(check);
        IObservableValue modelElement = 
                PojoProperties.value("selected").observe(searchableType); //$NON-NLS-1$
        dbc.bindValue(guiElement, modelElement);
    }
    
    /**
     * @param dbc
     *            the data binding context
     * @param parent
     *            the parent
     * @param searchableField
     *            the type to search for
     */
    private void createFieldCheck(DataBindingContext dbc, Composite parent,
        FieldName searchableField) {
        Button check = new Button(parent, SWT.CHECK);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        check.setLayoutData(gd);
        check.setText(searchableField.getDescName());

        IObservableValue guiElement = WidgetProperties.selection()
                .observe(check);
        IObservableValue modelElement = 
                PojoProperties.value("selected").observe(searchableField); //$NON-NLS-1$
        dbc.bindValue(guiElement, modelElement);
    }

    /**
     * @param horizontalSpan
     *            the horizontal column span
     * @param grabHorizontal
     *            set to true to grabExcessHorizontalSpace
     * @return a valid grid data
     */
    private GridData createGridData(
            int horizontalSpan, boolean grabHorizontal) {
        GridData gd = GridDataFactory.fillDefaults().create();
        gd.horizontalSpan = horizontalSpan;
        gd.grabExcessHorizontalSpace = grabHorizontal;
        return gd;
    }

    /**
     * Create a group box used for options with three columns.
     * @param parent The parent.
     * @param header The header text.
     * @param columns The number of columns.
     * @return The created group option box with the given parent, header text,
     *         and number of columns.
     */
    private Group createGroup(Composite parent, String header, int columns) {
        Group group = new Group(parent, SWT.NONE);
        group.setText(header);
        GridLayout layout = new GridLayout();
        layout.numColumns = columns;
        layout.verticalSpacing = VERTICAL_SPACING;
        layout.marginWidth = LayoutUtil.MARGIN_WIDTH;
        layout.marginHeight = LayoutUtil.MARGIN_HEIGHT;
        group.setLayout(layout);
        group.setLayoutData(createGridData(columns, true));
        return group;
    }

    /**
     * @param parent The parent.
     * @param text The text.
     * @param isSelected True, if the radio button is selected, otherwise False.
     * @return A radio button with the given parent, text and selected state.
     */
    private Button createRadio(
            Composite parent, String text, boolean isSelected) {
        return createButton(parent, SWT.RADIO, text, isSelected);
    }

    /**
     * @param parent The parent.
     * @param text The text.
     * @param isSelected True, if the check box is selected, otherwise False.
     * @return A check box with the given parent, text, and selected state.
     */
    private Button createCheck(
            Composite parent, String text, boolean isSelected) {
        return createButton(parent, SWT.CHECK, text, isSelected);
    }

    /**
     * @param parent The parent.
     * @param style The style of the button, e.g.
     *              {@link SWT#CHECK} or {@link SWT#RADIO}
     * @param text The text.
     * @param isSelected True, if the radio button is selected, otherwise False.
     * @return A radio button with the given parent, text and selected state.
     */
    private Button createButton(
            Composite parent, int style, String text, boolean isSelected) {
        Button button = new Button(parent, style);
        button.setText(text);
        getButtonSelections().next(button, isSelected);
        return button;
    }

    /**
     * @param e The event.
     */
    public void widgetSelected(SelectionEvent e) {
        // ensure that Test Suite Browser or Test Case Browser is selected
        if (!m_scopeTestSuitBrowserCheck.getSelection()
                && !m_scopeTestCaseBrowserCheck.getSelection()) {
            if (e.getSource() == m_scopeTestCaseBrowserCheck) {
                m_scopeTestSuitBrowserCheck.setSelection(true);
                notifySelectionListener(m_scopeTestSuitBrowserCheck);
            } else if (e.getSource() == m_scopeTestSuitBrowserCheck) {
                m_scopeTestCaseBrowserCheck.setSelection(true);
                notifySelectionListener(m_scopeTestCaseBrowserCheck);
            }
        }
        setEnabledButtons();
    }

    /**
     * @param button The button to notify listeners on.
     */
    private static void notifySelectionListener(Button button) {
        Event event = new Event();
        event.type = SWT.Selection;
        event.widget = button;
        button.notifyListeners(SWT.Selection, event);
    }

    /**
     * Validate the selection to enable and disable options.
     */
    private void setEnabledButtons() {
        // enable "Use selection" group only, if selected nodes is checked
        setChildrenEnabled(
                m_groupUseSelection, m_scopeSelectedNodesRadio.getSelection());
        if (!m_scopeTestCaseBrowserCheck.getSelection()) {
            // master is activated, if TCB is checked
            m_scopeTestCaseBrowserMasterRadio.setEnabled(false);
            // all only activated, if both TCB and selected nodes are checked
            m_scopeTestCaseBrowserAllRadio.setEnabled(false);
        }
    }

    /**
     * Set all children of a composite enabled or disabled.
     * @param composite The composite, e.g. a group.
     * @param isEnabled True, if content will be enabled, otherwise false.
     */
    private void setChildrenEnabled(Composite composite, boolean isEnabled) {
        for (Control child : composite.getChildren()) {
            child.setEnabled(isEnabled);
        }
    }

    /**
     * @param e The event.
     */
    public void widgetDefaultSelected(SelectionEvent e) {
        // do nothing
    }

    /** {@inheritDoc} */
    public boolean performAction() {
        getButtonSelections().store();
        if (GeneralStorage.getInstance().getProject() != null) {
            // fill search data from search dialog
            SearchOptions searchData = getSearchData();
            searchData.setData(
                    m_searchStringCombo.getText(),
                    m_caseSensitivCheck.getSelection(),
                    m_useRegExCheck.getSelection(),
                    m_scopeSelectedNodesRadio.getSelection(),
                    m_scopeTestSuitBrowserCheck.getSelection(),
                    m_scopeTestCaseBrowserCheck.getSelection(),
                    m_scopeTestCaseBrowserAllRadio.getSelection(),
                    m_scopeSearchInReusedProjects.getSelection());
            List<String> recent = searchData.getRecent();
            m_searchStringCombo.setItems(
                    recent.toArray(new String[recent.size()]));
            m_searchStringCombo.select(0);
            m_searchStringCombo.setFocus();
            // do search
            NewSearchUI.runQueryInBackground(getNewQuery());
        }
        return true;
    }

    /** {@inheritDoc} */
    public void setContainer(ISearchPageContainer container) {
        // no container support yet
    }

    /**
     * @return The class storing the checked state of components.
     */
    protected abstract ButtonSelections getButtonSelections();

    /**
     * @return The search data for the search page.
     */
    protected abstract SearchOptions getSearchData();

    /**
     * @return A new search query for the search page.
     */
    protected abstract ISearchQuery getNewQuery();

}
