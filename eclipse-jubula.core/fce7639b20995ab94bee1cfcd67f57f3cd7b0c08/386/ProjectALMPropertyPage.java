/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.properties;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jubula.client.core.model.ALMReportingRulePO;
import org.eclipse.jubula.client.core.model.IALMReportingRulePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.utils.ReportRuleType;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.properties.ProjectGeneralPropertyPage.IOkListener;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedURLText;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.mylyn.utils.MylynAccess;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;


/**
 * This is the class for the test data property page of a project.
 *
 * @author BREDEX GmbH
 * @created 08.02.2005
 */
public class ProjectALMPropertyPage extends AbstractProjectPropertyPage 
    implements IOkListener {
    /**
     * @author BREDEX GmbH
     */
    private class ConnectionTestListener implements SelectionListener {
        /** {@inheritDoc} */
        public void widgetSelected(SelectionEvent e) {
            String selectedObject = m_almRepoCombo.getSelectedObject();
            if (selectedObject != null) {
                IStatus connectionStatus = MylynAccess.testConnection(
                        selectedObject);
                if (connectionStatus.isOK()) {
                    m_connectionTest.setImage(IconConstants.STEP_OK_IMAGE);
                    setErrorMessage(null);
                } else {
                    m_connectionTest.setImage(IconConstants.ERROR_IMAGE);
                    setErrorMessage(connectionStatus.getMessage());
                }
            } else {
                m_connectionTest.setImage(null);
                setErrorMessage(null);
            }
        }
        
        /** {@inheritDoc} */
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }
    
    
    /**
     * @author BREDEX GmbH
     */
    private class DataUpdateListener implements ModifyListener {
        /** {@inheritDoc} */
        public void modifyText(ModifyEvent e) {
            updateALMData();
        }
    }
    
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;

    /** the Combo to select the connected ALM system */
    private DirectCombo<String> m_almRepoCombo;
    /** the button to test the connection with */ 
    private Button m_connectionTest;
    
    /**
     * Checkbox to decide if a comment should be automatically posted to the ALM
     * in case of a succeeded test
     */
    private Button m_reportOnSuccess = null;

    /** group for on success */
    private Group m_onSuccessGroup;

    /** group for on failure */
    private Group m_onFailureGroup;

    /** label advertising the comment writing rules on success*/ 
    private Label m_onSuccessRulesLabel;

    /** label advertising the comment writing rules on failure*/ 
    private Label m_onFailureRulesLabel;

    /** table viewer containing rules on success*/ 
    private TableViewer m_onSuccessTableViewer;

    /** table viewer containing rules on failure*/ 
    private TableViewer m_onFailureTableViewer;
    
    /** the add button */
    private Button m_onSuccessAddButton = null;

    /** the delete button */
    private Button m_onSuccessRemoveButton = null;

    /** the add button */
    private Button m_onFailureAddButton = null;

    /** the delete button */
    private Button m_onFailureRemoveButton = null;
    
    /** list containing on success report rules */
    private List<IALMReportingRulePO> m_reportingRules;
    
    /** listener to keep the data in sync */
    private ModifyListener m_dataUpdater = new DataUpdateListener();
    
    /**
     * Checkbox to decide if a comment should be automatically posted to the ALM
     * in case of a failed test
     */
    private Button m_reportOnFailure = null;    
    
    /**
     * the dashboards URL text field
     */
    private CheckedText m_dashboardURL;
    
    /**
     * the original / unmodified project properties
     */
    private IProjectPropertiesPO m_origProjectProps;
    
    /** the label provider for the reporting rules table */
    private ReportingRulesTableLabelProvider m_reportingRulesLabelProvider =
            new ReportingRulesTableLabelProvider();
    
    /**
     * @param es
     *            the editSupport
     */
    public ProjectALMPropertyPage(EditSupport es) {
        super(es);
        m_origProjectProps = ((IProjectPropertiesPO) es.getOriginal());
    }

    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        Composite composite = createComposite(parent, NUM_COLUMNS_1,
                GridData.FILL, false);

        createALMPageDescription(composite);
        
        Composite main = createComposite(composite, NUM_COLUMNS_2,
                GridData.FILL, false);
        noDefaultAndApplyButton();       

        createEmptyLabel(main);
        createEmptyLabel(main);
        
        createALMrepositoryChooser(main);
        createDashboardURL(main);
        createReportOnSuccess(main);
        createReportOnFailure(main);
        
        Event event = new Event();
        event.type = SWT.Selection;
        event.widget = m_almRepoCombo;
        m_almRepoCombo.notifyListeners(SWT.Selection, event);
        
        Plugin.getHelpSystem().setHelp(parent,
            ContextHelpIds.PROJECT_ALM_PROPERTY_PAGE);
        return composite;
    }
    
    /**
     * @param parent the parent to use
     */
    private void createDashboardURL(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.FILL, true);
        createLabel(leftComposite, 
                Messages.ProjectPropertyPageDasboardURLLabel);
        m_dashboardURL = new CheckedURLText(rightComposite, SWT.BORDER);
        m_dashboardURL.setText(StringUtils
                .defaultString(m_origProjectProps.getDashboardURL()));
        m_dashboardURL.validate();
        GridData textGridData = new GridData(GridData.FILL_HORIZONTAL);
        textGridData.grabExcessHorizontalSpace = true;
        m_dashboardURL.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_dashboardURL,
                IPersistentObject.MAX_STRING_LENGTH);
        m_dashboardURL.addModifyListener(m_dataUpdater);
    }
    
    /**
     * @param parent the parent to use
     */
    private void createReportOnFailure(Composite parent) {
        m_onFailureAddButton = new Button(parent, SWT.PUSH);
        m_onFailureRemoveButton = new Button(parent, SWT.PUSH);
        Composite leftPart = createComposite(parent, NUM_COLUMNS_1,
                GridData.FILL, true);
        m_onFailureGroup = new Group(parent, NONE);
        m_reportOnFailure = new Button(leftPart, SWT.CHECK);
        m_reportOnFailure.setSelection(
                m_origProjectProps.getIsReportOnFailure());
        m_onFailureRulesLabel = new Label(leftPart, NONE);
        m_onFailureTableViewer = new TableViewer(leftPart, SWT.MULTI
                | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION
                | SWT.BORDER);
        m_onFailureTableViewer.addFilter(
                new RuleTypeFilter(ReportRuleType.ONFAILURE));
        
        createRuleGroup(leftPart,
                m_onFailureGroup,
                m_reportOnFailure,
                m_onFailureRulesLabel,
                m_onFailureTableViewer,
                Messages.ProjectPropertyPageReportOnFailureLabel,
                m_onFailureAddButton,
                m_onFailureRemoveButton);
    }
    
    /**
     * @param parent the parent to use
     */
    private void createReportOnSuccess(Composite parent) {
        
        m_onSuccessAddButton = new Button(parent, SWT.PUSH);
        m_onSuccessRemoveButton = new Button(parent, SWT.PUSH);
        Composite leftPart = createComposite(parent, NUM_COLUMNS_1,
                GridData.FILL, true);
        m_onSuccessGroup = new Group(parent, NONE);
        m_reportOnSuccess = new Button(leftPart, SWT.CHECK);
        m_reportOnSuccess.setSelection(
                m_origProjectProps.getIsReportOnSuccess());
        m_onSuccessRulesLabel = new Label(leftPart, NONE);
        m_onSuccessTableViewer = new TableViewer(leftPart, SWT.MULTI
                | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION
                | SWT.BORDER);
        m_onSuccessTableViewer.addFilter(
                new RuleTypeFilter(ReportRuleType.ONSUCCESS));
        
        createRuleGroup(leftPart,
                m_onSuccessGroup,
                m_reportOnSuccess,
                m_onSuccessRulesLabel,
                m_onSuccessTableViewer,
                Messages.ProjectPropertyPageReportOnSuccessLabel,
                m_onSuccessAddButton,
                m_onSuccessRemoveButton);
    }

    /**
     * Creates UI for managing reporting rules for on success / on failure.
     * Designed to allow creation of identical groups as easy as possible.
     * @param leftPart composite for layout
     * @param group composite for layout
     * @param report button whether results should be reported
     * @param rulesLabel label
     * @param tableViewer the table containing the reporting rules
     * @param groupTitle title of the group
     * @param addButton add button
     * @param removeButton remove button
     */
    private void createRuleGroup(Composite leftPart, Group group,
            Button report, Label rulesLabel, final TableViewer tableViewer,
            String groupTitle, Button addButton, Button removeButton) {
        GridData groupGridData = new GridData(SWT.FILL, SWT.TOP, true, false);
        groupGridData.horizontalSpan = 2;
        group.setLayout(new GridLayout(2, false));
        group.setLayoutData(groupGridData);
        group.setText(groupTitle);
        leftPart.setParent(group);
        report.setText(Messages
                .ProjectPropertyPageReportWriteCommentLabel);
        ControlDecorator.createInfo(report, Messages
                .ProjectPropertyPageReportWriteCommentTooltip, false);
        GridData labelGridData = new GridData(GridData.FILL_VERTICAL);
        labelGridData.grabExcessHorizontalSpace = false;
        report.setLayoutData(labelGridData);
        report.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                updateALMData();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        rulesLabel.setText(Messages
                .ProjectPropertyPageReportRulesLabel);
        ControlDecorator.createInfo(rulesLabel, Messages
                .ProjectPropertyPageReportRulesTooltip, false);
        GridData rulesLabelGridData = new GridData(GridData.FILL_VERTICAL);
        rulesLabelGridData.verticalIndent = 15;
        rulesLabel.setLayoutData(rulesLabelGridData);
        ArrayContentProvider provider = new ArrayContentProvider();
        tableViewer.setContentProvider(provider);
        m_reportingRules = getProject().getProjectProperties()
                .getALMReportingRules();
        createTableContent(tableViewer);
        tableViewer.setInput(m_reportingRules);
        final Table table = tableViewer.getTable();
        tableViewer.setLabelProvider(m_reportingRulesLabelProvider);
        
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData layoutData = new GridData(600, 150, true, false);
        layoutData.horizontalAlignment = SWT.FILL;
        layoutData.heightHint = 150;
        table.setLayoutData(layoutData);
        
        Composite rightPart = new Composite(group, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        rightPart.setLayout(compositeLayout);
        GridData compositeData = new GridData(
                SWT.RIGHT, SWT.BOTTOM, false, false);
        rightPart.setLayoutData(compositeData);
        
        WidgetSelectionListener selectionListener =
                new WidgetSelectionListener(tableViewer);
        
        addButton.setParent(rightPart);
        addButton.setText(Messages.ProjectPropertyPageALMReportRuleAdd);
        addButton.setLayoutData(buttonGrid());
        addButton.addSelectionListener(selectionListener);

        removeButton.setParent(rightPart);
        removeButton.setText(Messages.ProjectPropertyPageALMReportRuleRemove);
        removeButton.setLayoutData(buttonGrid());
        removeButton.addSelectionListener(selectionListener);
    }

    /** creates the content of the table 
     * @param tableViewer the associated table viewer
     */
    private void createTableContent(final TableViewer tableViewer) {
        TableViewerColumn nameColumn =
                new TableViewerColumn(tableViewer, SWT.LEFT);
        nameColumn.getColumn().setText(Messages.ALMReportRuleDescription);
        nameColumn.getColumn().setWidth(200);
        nameColumn.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                if (element instanceof IALMReportingRulePO) {
                    return ((IALMReportingRulePO) element).getName();
                }
                return null;
            }
        });
        nameColumn.setEditingSupport(
                new ReportingRuleNameEditingSupport(tableViewer));
        
        TableViewerColumn fieldColumn =
                new TableViewerColumn(tableViewer, SWT.LEFT);
        fieldColumn.getColumn().setText(Messages.ALMReportRuleField);
        fieldColumn.getColumn().setWidth(200);
        fieldColumn.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                if (element instanceof IALMReportingRulePO) {
                    return ((IALMReportingRulePO) element).getAttributeID();
                }
                return null;
            }
        });
        fieldColumn.setEditingSupport(
                new ReportingRuleFieldIDEditingSupport(tableViewer));
        
        TableViewerColumn valueColumn =
                new TableViewerColumn(tableViewer, SWT.LEFT);
        valueColumn.getColumn().setText(Messages.ALMReportRuleValue);
        valueColumn.getColumn().setWidth(200);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                if (element instanceof IALMReportingRulePO) {
                    return ((IALMReportingRulePO) element).getValue();
                }
                return null;
            }
        });
        valueColumn.setEditingSupport(
                new ReportingRuleValueEditingSupport(tableViewer));
        
        TableViewerFocusCellManager focusCellManager = 
                new TableViewerFocusCellManager(tableViewer,
                        new FocusCellOwnerDrawHighlighter(tableViewer));
        ColumnViewerEditorActivationStrategy actSupport = 
            new ColumnViewerEditorActivationStrategy(tableViewer) {
                protected boolean isEditorActivationEvent(
                        ColumnViewerEditorActivationEvent event) {
                    return event.eventType 
                            == ColumnViewerEditorActivationEvent.TRAVERSAL
                        || event.eventType 
                            == ColumnViewerEditorActivationEvent.
                                MOUSE_DOUBLE_CLICK_SELECTION
                        || (event.eventType 
                                == ColumnViewerEditorActivationEvent.KEY_PRESSED
                            && event.keyCode == SWT.CR)
                        || event.eventType 
                            == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
                }
            };
            
        TableViewerEditor.create(tableViewer, focusCellManager, 
                actSupport, ColumnViewerEditor.TABBING_HORIZONTAL
                    | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                    | ColumnViewerEditor.TABBING_VERTICAL 
                    | ColumnViewerEditor.KEYBOARD_ACTIVATION);
    }

    /**
     * @param parent the parent to use
     */
    private void createALMPageDescription(Composite parent) {
        createEmptyLabel(parent);
        
        Composite composite = createComposite(parent, 1,
                GridData.FILL, true);
        createLabel(composite, Messages.ProjectPropertyPageALMLabel);
    }
    
    /**
     * @param parent the parent to use
     */
    private void createALMrepositoryChooser(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, 3,
                GridData.FILL, true);
        createLabel(leftComposite,
                Messages.ProjectPropertyPageALMRepositoryLabel);
        String configuredRepo = m_origProjectProps
                .getALMRepositoryName();
        m_almRepoCombo = ControlFactory
                .createALMRepositoryCombo(rightComposite, configuredRepo);
        m_almRepoCombo.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                m_connectionTest.setImage(IconConstants.STEP_TESTING_IMAGE);
                if (m_almRepoCombo.getSelectedObject() == null) {
                    m_dashboardURL.setEnabled(false);
                    m_dashboardURL.setBackground(null);
                    setEnabledRecursive(m_onSuccessGroup, false);
                    setEnabledRecursive(m_onFailureGroup, false);
                    m_onSuccessTableViewer.setSelection(null);
                    m_onFailureTableViewer.setSelection(null);
                    m_onSuccessTableViewer.getTable().setEnabled(false);
                    m_onFailureTableViewer.getTable().setEnabled(false);
                } else {
                    m_dashboardURL.setEnabled(true);
                    m_dashboardURL.validate();
                    setEnabledRecursive(m_onSuccessGroup, true);
                    setEnabledRecursive(m_onFailureGroup, true);
                    m_onSuccessTableViewer.getTable().setEnabled(true);
                    m_onFailureTableViewer.getTable().setEnabled(true);
                }
                m_reportingRulesLabelProvider.refresh();
                setErrorMessage(null);
                updateALMData();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
        m_almRepoCombo.setSelectedObject(configuredRepo);
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        m_almRepoCombo.setLayoutData(textGridData);
        
        m_connectionTest = new Button(rightComposite, SWT.PUSH);
        m_connectionTest.setText(Messages.ProjectPropertyPageALMConnectionTest);
        m_connectionTest.setImage(IconConstants.STEP_TESTING_IMAGE);
        m_connectionTest.addSelectionListener(new ConnectionTestListener());
    }
    
    /**
     * recursively dis-/enable control and all its children
     * @param control the control
     * @param enabled whether the control should be enabled
     */
    public void setEnabledRecursive(Control control, boolean enabled) {
        if (control instanceof Composite) {
            Composite composite = (Composite) control;
            for (Control c : composite.getChildren()) {
                setEnabledRecursive(c, enabled);
            }
        } else {
            control.setEnabled(enabled);
        }
    }

    /**
     * update the data
     */
    private void updateALMData() {
        IProjectPropertiesPO props = getProject().getProjectProperties();
        if (m_almRepoCombo != null) {
            props.setALMRepositoryName(m_almRepoCombo.getText());
        }
        if (m_reportOnFailure != null) {
            props.setIsReportOnFailure(m_reportOnFailure.getSelection());
        }
        if (m_reportOnSuccess != null) {
            props.setIsReportOnSuccess(m_reportOnSuccess.getSelection());
        }
        if (m_dashboardURL != null) {
            props.setDashboardURL(m_dashboardURL.getText().trim());
        }
        if (m_reportingRules != null) {
            props.setALMReportingRules(m_reportingRules);
        }
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * It assigns actions to different button types.
     * 
     * @author BREDEX GmbH
     * @created 11.07.2014
     */
    private class WidgetSelectionListener implements SelectionListener {

        /** table on which the button action should be performed */
        private TableViewer m_tableViewer;
        
        /** constructor
         * @param tableViewer the table on which the button action should be performed
         */
        WidgetSelectionListener(TableViewer tableViewer) {
            m_tableViewer = tableViewer;
        }
        
        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            handleSelectionEvent(e);
        }

        /**
         * @param e a SelectionEvent
         */
        private void handleSelectionEvent(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_onSuccessAddButton)
                    || o.equals(m_onFailureAddButton)) {
                handleAddButtonEvent((Button) o, m_tableViewer);
                return;
            } else if (o.equals(m_onSuccessRemoveButton)
                    || o.equals(m_onFailureRemoveButton)) {
                handleRemoveButtonEvent(m_tableViewer);
                return;
            }

            Assert.notReached(Messages.EventActivatedUnknownWidget 
                + StringConstants.COLON + StringConstants.SPACE 
                + StringConstants.APOSTROPHE + String.valueOf(e.getSource()) 
                + StringConstants.APOSTROPHE);
        }
        
        /**
         * Reacts, when an object is double clicked.
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            handleSelectionEvent(e);
        }
    }
    
    /** Handles the add-button event by opening a dialogue to add a
     *  reporting rule to a given table.
     * @param button the add button
     * @param tableViewer the table viewer
     */
    void handleAddButtonEvent(Button button, TableViewer tableViewer) {
        ReportRuleType type = null;
        if (tableViewer.equals(m_onSuccessTableViewer)) {
            type = ReportRuleType.ONSUCCESS;
        } else if (tableViewer.equals(m_onFailureTableViewer)) {
            type = ReportRuleType.ONFAILURE;
        }
        IALMReportingRulePO rule = PoMaker.createALMReportingRulePO(
                StringUtils.EMPTY, StringUtils.EMPTY, StringUtils.EMPTY,
                type);
        m_reportingRules.add(rule);
        rule.setParentProjectId(getProject().getId());
        tableViewer.refresh();
        tableViewer.reveal(rule);
        tableViewer.editElement(rule, 0);
    }
    
    /** Handles the remove-button event of a given table by deleting the selected
     *  reporting rule from the given table.
     * @param tableViewer the table viewer
     */
    void handleRemoveButtonEvent(TableViewer tableViewer) {
        StructuredSelection selection = 
                (StructuredSelection) tableViewer.getSelection();
        
        Iterator itr = selection.iterator();
        while (itr.hasNext()) {
            m_reportingRules.remove(itr.next());
        }
        tableViewer.refresh();
    }
    

    /**
     * Creates new gridData for the buttons.
     * @return The new GridData.
     */
    private GridData buttonGrid() {
        GridData buttonData = new GridData();
        buttonData.horizontalAlignment = GridData.FILL;
        return buttonData;      
    }
    
    /**
     * Viewer filter which only shows ALM reporting rules of a given type
     *  
     * @author BREDEX GmbH
     * @created 29.07.2014
     */
    private class RuleTypeFilter extends ViewerFilter {
        
        /** the type of the rules to show */
        private ReportRuleType m_type = null;

        /**
         * constructor
         * @param type the type to show
         */
        public RuleTypeFilter(ReportRuleType type) {
            m_type = type;
        }
        
        @Override
        public boolean select(Viewer viewer, Object parentElement,
                Object element) {
            if (element instanceof IALMReportingRulePO) {
                IALMReportingRulePO rule = (IALMReportingRulePO) element;
                if (rule.getType() == m_type) {
                    return true;
                }
            }
            return false;
        }
    }
    
    /**
     *
     * @author BREDEX GmbH
     * @created Jul 30, 2014
     */
    private abstract static class ReportingRuleEditingSupport
            extends EditingSupport {

        /**
         * Constructor
         * 
         * @param viewer The viewer
        */
        public ReportingRuleEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }
    
        /**
         * {@inheritDoc}
         */
        protected boolean canEdit(Object element) {
            return true;
        }
    
        /**
         * {@inheritDoc}
         */
        protected CellEditor getCellEditor(Object element) {
            return new TextCellEditor((Composite)getViewer().getControl());
        }
    }
    
    /**
     *
     * @author BREDEX GmbH
     * @created Jul 31, 2014
     */
    private static class ReportingRuleNameEditingSupport
            extends ReportingRuleEditingSupport {
        
        /**
         * Constructor
         * 
         * @param viewer The viewer
        */
        public ReportingRuleNameEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }
        
        /**
         * {@inheritDoc}
         */
        protected Object getValue(Object element) {
            return StringUtils.defaultString(
                    ((IALMReportingRulePO)element).getName());
        }
        
        /**
         * {@inheritDoc}
         */
        protected void setValue(Object element, Object value) {
            String hostNameValue = String.valueOf(value);
            ((IALMReportingRulePO)element).setName(hostNameValue);
            getViewer().update(element, null);
        }
    }
    
    /**
    *
    * @author BREDEX GmbH
    * @created Jul 31, 2014
    */
    private static class ReportingRuleFieldIDEditingSupport
           extends ReportingRuleEditingSupport {
       
        /**
         * Constructor
         * 
         * @param viewer The viewer
        */
        public ReportingRuleFieldIDEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }
       
        /**
         * {@inheritDoc}
         */
        protected Object getValue(Object element) {
            return StringUtils.defaultString(
                    ((IALMReportingRulePO)element).getAttributeID());
        }
       
        /**
         * {@inheritDoc}
         */
        protected void setValue(Object element, Object value) {
            String hostNameValue = String.valueOf(value);
            ((IALMReportingRulePO)element).setAttributeID(hostNameValue);
            getViewer().update(element, null);
        }
    }
   
     /**
      *
      * @author BREDEX GmbH
      * @created Jul 31, 2014
     */
    private static class ReportingRuleValueEditingSupport
            extends ReportingRuleEditingSupport {
      
        /**
         * Constructor
         * 
         * @param viewer The viewer
        */
        public ReportingRuleValueEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }
      
        /**
         * {@inheritDoc}
         */
        protected Object getValue(Object element) {
            return StringUtils.defaultString(
                    ((IALMReportingRulePO)element).getValue());
        }
      
        /**
         * {@inheritDoc}
         */
        protected void setValue(Object element, Object value) {
            String hostNameValue = String.valueOf(value);
            ((IALMReportingRulePO)element).setValue(hostNameValue);
            getViewer().update(element, null);
        }
    }
    
    /**
     * Label provider for ALM reporting rules table
     */
    private class ReportingRulesTableLabelProvider
                implements ITableLabelProvider, IColorProvider {

        /** whether the table is enabled */
        private boolean m_enabled = true;
        
        /** the listeners */
        private List<ILabelProviderListener> m_listeners;
        
        /**
         * The label provider for the table containing ALM reporting rules
         */
        public ReportingRulesTableLabelProvider() {
            super();
            m_listeners = new ArrayList<ILabelProviderListener>();
        }
        
        /**
         * refreshes the enabled-state and notifies listeners
         */
        public void refresh() {
            boolean newState = m_onSuccessTableViewer.getTable().getEnabled();
            if (newState != m_enabled) {
                m_enabled =  newState;
                for (ILabelProviderListener listener : m_listeners) {
                    listener.labelProviderChanged(
                            new LabelProviderChangedEvent(this));
                }
            }
        }
        
        @Override
        public void addListener(ILabelProviderListener listener) {
            m_listeners.add(listener);
        }

        @Override
        public void dispose() {
            // nothing
        }

        @Override
        public boolean isLabelProperty(Object element, String property) {
            return false;
        }

        @Override
        public void removeListener(ILabelProviderListener listener) {
            m_listeners.remove(listener);
        }

        @Override
        public Color getForeground(Object element) {
            int greyTone = m_enabled ? 0 : 128;
            return new Color(getControl().getDisplay(), 
                    greyTone, greyTone, greyTone);
        }

        @Override
        public Color getBackground(Object element) {
            int greyTone = 250;
            if (element instanceof ALMReportingRulePO) {
                ALMReportingRulePO currentRule = (ALMReportingRulePO)element;
                int i = 0;
                for (IALMReportingRulePO rule : m_reportingRules) {
                    if (rule.equals(currentRule)) {
                        break;
                    }
                    if (rule.getType().equals(currentRule.getType())) {
                        i++;                        
                    }
                }
                switch (i % 2) {
                    case 0:
                        greyTone = 235;
                        break;
                    case 1:
                        greyTone = 240;
                        break;
                    default:
                        break;
                }
                if (!m_enabled) {
                    greyTone += 5;
                }
            }
            return new Color(getControl().getDisplay(),
                    greyTone, greyTone, greyTone);
        }

        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof ALMReportingRulePO) {
                ALMReportingRulePO rule = (ALMReportingRulePO)element;
                switch (columnIndex) {
                    case 0:
                        return rule.getName();
                    case 1:
                        return rule.getAttributeID();
                    case 2:
                        return rule.getValue();
                    default:
                        break;
                }
            }
            return null;
        }
        
    }    
    
    /** {@inheritDoc} */
    public void okPressed() {
        IProjectPropertiesPO props = getProject().getProjectProperties();
        List<IALMReportingRulePO> invalidRules = 
                new LinkedList<IALMReportingRulePO>();

        List<IALMReportingRulePO> almReportingRules = props
                .getALMReportingRules();
        for (IALMReportingRulePO rule : almReportingRules) {
            if (StringUtils.isEmpty(rule.getAttributeID())
                    || StringUtils.isEmpty(rule.getValue())) {
                invalidRules.add(rule);
            }
        }

        almReportingRules.removeAll(invalidRules);
    }
}