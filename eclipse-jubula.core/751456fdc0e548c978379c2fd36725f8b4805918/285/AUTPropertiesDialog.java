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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TableViewerEditor;
import org.eclipse.jface.viewers.TableViewerFocusCellManager;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.databinding.validators.AutConfigNameValidator;
import org.eclipse.jubula.client.ui.rcp.databinding.validators.AutIdValidator;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.utils.AutPropertyManager;
import org.eclipse.jubula.client.ui.rcp.utils.AutPropertyManager.AutProperty;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.client.ui.rcp.widgets.AutIdListComposite;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedRequiredText;
import org.eclipse.jubula.client.ui.rcp.wizards.pages.AUTSettingWizardPage;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.toolkit.common.exception.ToolkitPluginException;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;



/**
 * @author BREDEX GmbH
 * @created 08.02.2005
 */
public class AUTPropertiesDialog extends TitleAreaDialog {

    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;
    
    /** number of columns = 3 */
    private static final int NUM_COLUMNS_3 = 3;

    /** the quantity of lines in a m_text field = 5 */
    private static final int LINES_5 = 5;

    /** the add button */
    private Button m_addButton = null;

    /** the delete button */
    private Button m_removeButton = null;

    /** the add button */
    private Button m_propAddButton = null;

    /** the delete button */
    private Button m_propRemoveButton = null;

    /** the edit button */
    private Button m_editButton = null;
    
    /** the duplicate button */
    private Button m_duplicateButton = null;

    /** the AUT name editor */
    private Text m_autNameText;
    
    /** The combo with the toolkitnames */
    private DirectCombo<String> m_autToolKitComboBox;

    /** The button to indicate whether names should be generated */
    private Button m_generateNames;
    
    /** the list field for the aut configs */
    private List m_autConfigList;

    /** the name of the selected aut or a dummy AUT*/
    private IAUTMainPO m_autMain;
    
    /** the image of the error message */
    private final String m_origAUTName;

    /**
     * true, if the edit button was pressed, false if the add button was pressed
     * (in the AUTPropertyPage).
     */
    private boolean m_edit = false;

    /** the WidgetSelectionListener */
    private final WidgetSelectionListener m_selectionListener = 
        new WidgetSelectionListener();

    /** the WidgetVerifyListener */
    private final WidgetVerifyListener m_verifyListener = 
        new WidgetVerifyListener();
    
    /** the WidgetStateController */
    private final WidgetModifyListener m_modifyListener = 
        new WidgetModifyListener();
    
    /** the Project to which the AUT Definition belongs */
    private IProjectPO m_project;

    /** label advertising the comment writing property*/ 
    private Label m_propLabel;

    /** table viewer containing aut properties*/ 
    private TableViewer m_propTableViewer;
    
    /** view model help to handling the properties */
    private java.util.List<AutProperty> m_viewModel;

    /**
     * The contructor.
     * 
     * @param parentShell
     *            The shell.
     * @param edit
     *            The boolean for the pressed "edit" button.
     * @param autMain
     *            The selected AUTMain in the AUTPropertyPage.
     * @param project 
     *            The Project to which the AUT Definition belongs
     */
    public AUTPropertiesDialog(Shell parentShell, boolean edit,
        IAUTMainPO autMain, IProjectPO project) {

        super(parentShell);
        m_project = project;
        m_edit = edit;
        if (autMain == null) {
            m_autMain = PoMaker.createAUTMainPO(StringUtils.EMPTY);
        } else {
            m_autMain = autMain;
        }
        m_origAUTName = m_autMain.getName();
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        ScrolledComposite scroll = new ScrolledComposite(parent, SWT.V_SCROLL 
            | SWT.H_SCROLL);
        scroll.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        Composite composite = newComposite(scroll, NUM_COLUMNS_3, 
            GridData.FILL);
        Composite leftComposite = newComposite(composite,  NUM_COLUMNS_3, 
            GridData.BEGINNING);
        Composite rightComposite = newComposite(composite, NUM_COLUMNS_1, 
            GridData.BEGINNING);  
        
        createAUTNameEditor(leftComposite);
        createAutToolkitCombo(leftComposite);
        m_autConfigList = newListField(leftComposite, 
                Messages.AUTPropertiesDialogAutConfigListText, LINES_5);
        createAutConfigButtons(rightComposite);
        createGenerateNamesCheckBox(leftComposite);
        separator(leftComposite);
        createAutIdList(leftComposite);
        separator(leftComposite);
        createProperty(leftComposite);
        
        newLabel(leftComposite, StringConstants.EMPTY);
        newLabel(leftComposite, StringConstants.EMPTY);
        newLabel(leftComposite, StringConstants.EMPTY);
        
        Composite innerComposite = new Composite(leftComposite, SWT.NONE);
        GridLayout innerCompositeLayout = new GridLayout();
        innerCompositeLayout.numColumns = NUM_COLUMNS_1;
        innerCompositeLayout.marginHeight = 0;
        innerCompositeLayout.marginWidth = 0;
        innerComposite.setLayout(innerCompositeLayout);
        GridData innerCompositeData = new GridData();
        innerCompositeData.horizontalSpan = NUM_COLUMNS_2;
        innerCompositeData.horizontalAlignment = GridData.FILL;
        innerCompositeData.grabExcessHorizontalSpace = true;
        innerComposite.setLayoutData(innerCompositeData);
        addListener();
        
        if (m_edit) {
            initFields();
        } else {
            if (m_autToolKitComboBox.getItemCount() == 1) {
                m_autToolKitComboBox.select(0);
            } else {
                m_autToolKitComboBox.deselectAll();
                m_autToolKitComboBox.clearSelection();
            }
            if (CommandConstants.RCP_TOOLKIT.equals(
                    m_autToolKitComboBox.getSelectedObject())) {
                m_generateNames.setEnabled(true);
                m_generateNames.setSelection(true);
                m_autMain.setGenerateNames(true);
            } else {
                m_generateNames.setEnabled(false);
                m_generateNames.setSelection(false);
            }
            enableOKButton(false);
        }
        Plugin.getHelpSystem().setHelp(parent, 
                ContextHelpIds.AUT_CONFIGURATION);
        setTitle(Messages.ProjectWizardAutSettings);
        scroll.setContent(composite);
        scroll.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        scroll.setExpandHorizontal(true);
        scroll.setExpandVertical(true);
        return scroll;
    }

    /**
     * @param parent the parent to use
     */
    private void createProperty(Composite parent) {
        Composite composite = createComposite(parent, NUM_COLUMNS_2,
                GridData.FILL, true);
        

        m_propLabel = new Label(composite, SWT.NONE);
        m_propLabel.setText(Messages.AUTPropertyTitle);
        GridData data = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
        data.horizontalSpan = 1;
        m_propLabel.setLayoutData(data);  
        // Created to keep layout consistent
        new Label(composite, SWT.NONE).setVisible(false);   
         
        m_propTableViewer = new TableViewer(composite, SWT.MULTI
                | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION
                | SWT.BORDER);
        m_propAddButton = new Button(composite, SWT.PUSH);
        m_propRemoveButton = new Button(composite, SWT.PUSH);
        createPropertyTable(composite, m_propAddButton, m_propRemoveButton,
                m_propTableViewer);
    }

    /**
     * @param parent 
     * @param numColumns 
     * @param alignment 
     * @param horizontalSpace 
     * @return Composite 
     */
    protected Composite createComposite(Composite parent, int numColumns, 
            int alignment, boolean horizontalSpace) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalAlignment = alignment;
        compositeData.grabExcessHorizontalSpace = horizontalSpace;
        composite.setLayoutData(compositeData);
        return composite;       
    }
    
    /**
     * @param parent 
     * @param addButton 
     * @param removeButton 
     * @param tableViewer 
     */
    private void createPropertyTable(Composite parent, Button addButton,
            Button removeButton, TableViewer tableViewer) {
        
        tableViewer.setContentProvider(new AutPropertyManager
                .AutPropertiesContentProvider());
        
        createTableContent(tableViewer);
        m_viewModel = new LinkedList<AutProperty>();
        m_viewModel.addAll(AutPropertyManager.convertProprtyMapToList(
                m_autMain.getPropertyMap()));
        tableViewer.setInput(m_viewModel);
        final Table table = tableViewer.getTable();
        
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        GridData layoutData = new GridData(SWT.FILL, SWT.FILL, false, false);
        layoutData.heightHint = 80;
        table.setLayoutData(layoutData);
        
        Composite rightPart = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        rightPart.setLayout(compositeLayout);
        GridData compositeData = new GridData(
                SWT.FILL, SWT.BOTTOM, false, false);
        rightPart.setLayoutData(compositeData);
        
        addButton.setParent(rightPart);
        addButton.setText(Messages.AUTPropertyAdd);
        addButton.setLayoutData(buttonGrid());
        addButton.addSelectionListener(m_selectionListener);

        removeButton.setParent(rightPart);
        removeButton.setText(Messages.AUTPropertyRemove);
        removeButton.setLayoutData(buttonGrid());
        removeButton.addSelectionListener(m_selectionListener);
    }

    /** creates the content of the table 
     * @param tableViewer the associated table viewer
     */
    private void createTableContent(final TableViewer tableViewer) {
        TableViewerColumn nameColumn =
                new TableViewerColumn(tableViewer, SWT.LEFT);
        nameColumn.getColumn().setText(Messages.AUTPropertyName);
        nameColumn.getColumn().setWidth(200);
        nameColumn.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                return ((AutProperty)element).getName();
            }
        });
        nameColumn.setEditingSupport(
                new PropertyNameEditingSupport(tableViewer));
        
        TableViewerColumn valueColumn =
                new TableViewerColumn(tableViewer, SWT.LEFT);
        valueColumn.getColumn().setText(Messages.AUTPropertyValue);
        valueColumn.getColumn().setWidth(200);
        valueColumn.setLabelProvider(new ColumnLabelProvider() {
            public String getText(Object element) {
                return ((AutProperty)element).getValue();
            }
        });
        valueColumn.setEditingSupport(
                new PropertyValueEditingSupport(tableViewer));
        
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
    
    /** add a new AUT property*/
    private void handlePropAddButtonEvent() {
        AutProperty prop = new AutProperty();
        m_viewModel.add(prop);
        m_propTableViewer.refresh();
        m_propTableViewer.editElement(prop, 0);
    }
    
    /** remove the selected properties*/
    private void handlePropRemoveButtonEvent() {
        IStructuredSelection sel = (IStructuredSelection) m_propTableViewer
                .getSelection();
        
        Iterator itr = sel.iterator();
        while (itr.hasNext()) {
            m_viewModel.remove(itr.next());
        }
        m_propTableViewer.refresh();
        checkForErrors();
    }

    /**
     * Checks the values of all fields, en-/dis-abling the OK button and 
     * setting an error message as necessary. 
     */
    private void checkForErrors() {
        boolean error = false;
        error |= modifyAUTNameFieldAction();
        if (!error) {
            error |= modifyAUTToolkitComboAction();
        }
        if (!error) {
            error |= modifyAUTPropertiesAction();
        }
        
        if (error) {
            enableOKButton(false);
        } else {
            setMessage(null);
            enableOKButton(true);
        }
    }
    
    /** 
     * The action of the AUT name field.
     * @return false, if the AUT name field contents an error:
     * the project name starts or end with a blank, or the field is empty
     */
    private boolean modifyAUTNameFieldAction() {
        boolean isError = false;
        String autName = m_autNameText.getText();
        if (!isError) {
            Iterator iter = m_project.getAutMainList().iterator();
            while (iter.hasNext()) {
                IAUTMainPO aut = (IAUTMainPO)iter.next();
                if (!m_edit) {
                    if (aut.getName().equals(m_autNameText.getText())) {
                        setMessage(Messages.AUTPropertiesDialogDoubleAUTName,
                                IMessageProvider.ERROR);
                        isError |= true;
                    }
                } else {
                    if (aut.getName().equals(m_autNameText.getText())
                            && !m_autNameText.getText().equals(m_origAUTName)) {
                        setMessage(Messages.AUTPropertiesDialogDoubleAUTName,
                                IMessageProvider.ERROR);
                        isError |= true;
                    }
                }
            }
        } 
        if (!isError) {
            if (autName.length() == 0) {
                setMessage(Messages.AUTPropertiesDialogEmptyAUTName,
                        IMessageProvider.ERROR);
                isError |= true;
            }
        }
        
        if (!isError) {
            if (autName.startsWith(StringConstants.SPACE)
                    || autName.charAt(autName.length() - 1) == ' ') {
                setMessage(Messages.AUTPropertiesDialogInvalidAUTName,
                        IMessageProvider.ERROR);
                isError |= true;
            }
        }
        
        return isError;
    }

    /** 
     * The action of the AUT toolkit combo box.
     * @return false, if the AUT toolkit combo box contains an error:
     *                the selection is empty
     */
    private boolean modifyAUTToolkitComboAction() {
        String selection = m_autToolKitComboBox.getSelectedObject();
        if (selection == null || selection.trim().length() == 0) {
            setMessage(Messages.AUTPropertiesDialogNoToolkitSelected,
                IMessageProvider.ERROR);
            return true;
        }
        return false;
    }
    
    /**
     * @return false if there is no mistake else true
     */
    private boolean modifyAUTPropertiesAction() {
        Iterator<AutProperty> props = m_viewModel.iterator();
        
        while (props.hasNext()) {
            AutProperty prop = props.next();
            if (StringUtils.isEmpty(prop.getName())) {
                setMessage(Messages.AUTPropertyNameIsEmpty,
                        IMessageProvider.ERROR);
                return true;
            }
            if (StringUtils.isEmpty(prop.getValue())) {
                setMessage(Messages.AUTPropertyValueIsEmpty,
                        IMessageProvider.ERROR);
                return true;
            }
            if (isAUTContainsDuplicatePropertyName(prop)) {
                setMessage(Messages.AUTPropertyDuplicated,
                        IMessageProvider.ERROR);
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param selectedProp an AUT property what we need to compare
     * @return true if contains a property name more times else return false 
     */
    private boolean isAUTContainsDuplicatePropertyName(
            AutProperty selectedProp) {
        Iterator<AutProperty> props = m_viewModel.iterator();
        
        while (props.hasNext()) {
            AutProperty prop = props.next();
            if (prop != selectedProp && prop.getName().toLowerCase()
                    .equals(selectedProp.getName().toLowerCase())) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Enables the OK button if the argument is true, and disables it otherwise.
     * @param enabled The new enabled state.
     */
    private void enableOKButton(boolean enabled) {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(enabled);
        }
    }

    /**
     * Creates a new multiline textfield
     * @param parent The parent composite.
     * @param labelText The m_text for the label.
     * @param lines The quantity of lines of this list.
     * @return The new multiline textfield.
     */
    private List newListField(Composite parent, String labelText, int lines) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(labelText);
        List listField = new List(parent, 
            LayoutUtil.SINGLE_TEXT_STYLE | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData listGridData = new GridData();
        listGridData.horizontalAlignment = GridData.FILL;
        listGridData.grabExcessHorizontalSpace = true;
        listGridData.horizontalSpan = NUM_COLUMNS_3 - 1;
        listGridData.heightHint = Dialog.convertHeightInCharsToPixels(
            LayoutUtil.getFontMetrics(listField), lines);
        LayoutUtil.addToolTipAndMaxWidth(listGridData, listField);
        listField.setLayoutData(listGridData);
        return listField;
    }

    /**
     * Fills the textFields with the data of the model, if the edit button was
     * pressed in the AUTPropertyPage.
     */
    private void initFields() {
        m_autNameText.setText(m_autMain.getName());
        m_autToolKitComboBox.setSelectedObject(m_autMain.getToolkit());
        java.util.Set<IAUTConfigPO> autConfigSet = m_autMain
            .getAutConfigSet();
        if (!autConfigSet.isEmpty()) {
            int i = 0;
            String[] autConfigs = new String[autConfigSet.size()];
            for (IAUTConfigPO configPO : autConfigSet) {              
                autConfigs[i] = configPO.getName();
                i++;
            }
            setAutConfigList(autConfigs);
        }
    }

    /**
     * Creates and configures the editor for the field aut class name. The
     * editor is also added to the page.
     * 
     * @param parent The parent composite.
     */
    private void createAUTNameEditor(Composite parent) {
        newLabel(parent, Messages.AUTPropertiesDialogAUTName);
        m_autNameText = newRequiredTextField(parent);
        LayoutUtil.setMaxChar(m_autNameText);
    }

    /**
     * Creates the Combo with the toolkit names
     * @param parent The parent composite.
     */
    private void createAutToolkitCombo(Composite parent) {
        newLabel(parent, Messages.AUTPropertiesDialogToolkit);
        try {
            m_autToolKitComboBox = ControlFactory.createAutToolkitCombo(
                parent, m_project, m_autMain.getToolkit());
            m_autToolKitComboBox.deselectAll();
            m_autToolKitComboBox.clearSelection();
            String autToolkit = m_autMain.getToolkit(); 
            if (autToolkit != null && autToolkit.trim().length() != 0) {
                m_autToolKitComboBox.setSelectedObject(autToolkit);
            } else if (m_autToolKitComboBox.getItemCount() == 1) {
                m_autToolKitComboBox.select(0);
            }

        } catch (ToolkitPluginException tpe) {
            // Toolkit for project could not be found.
            // Create a combo with only the aut toolkit.
            m_autToolKitComboBox = ControlFactory.createAutToolkitCombo(
                parent, m_autMain);
        }
        newLabel(parent, StringConstants.EMPTY);
    }
    
    /**
     * Creates the CheckBox for the name generation
     * @param parent The parent composite.
     */
    private void createGenerateNamesCheckBox(Composite parent) {
        Label infoLabel = newLabel(parent,
                Messages.AUTPropertiesDialogGenerateNames);
        ControlDecorator.createInfo(infoLabel, 
                I18n.getString("AUTPropertiesDialog.generateNamesDescription"), //$NON-NLS-1$
                false);
        m_generateNames = new Button(parent, SWT.CHECK);
        m_generateNames.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing 
            }

            public void widgetSelected(SelectionEvent e) {
                if (m_autMain.isGenerateNames() 
                        != m_generateNames.getSelection()) {
                    m_autMain.setGenerateNames(m_generateNames.getSelection());
                    NagDialog.runNagDialog(getShell(), 
                            "InfoNagger.changeAUTNameGeneration",  //$NON-NLS-1$
                            ContextHelpIds.AUT_WIZARD_PAGE_GENERATE_NAMES); 
                }
            }
            
        });
        if (m_autMain.getToolkit() != null 
                && m_autMain.getToolkit()
                    .equals(CommandConstants.RCP_TOOLKIT)) {
            m_generateNames.setEnabled(true);
        } else {
            m_generateNames.setEnabled(false);
            m_generateNames.setSelection(false);
        }
        m_generateNames.setSelection(m_autMain.isGenerateNames());
        newLabel(parent, StringConstants.EMPTY);
    }
    
    /**
     * Creates three buttons.
     * 
     * @param parent The parent composite.
     */
    private void createAutConfigButtons(Composite parent) {
        Text invisibleText = new Text(parent, SWT.BORDER);
        invisibleText.setVisible(false);
        GridData invisibleGrid = new GridData();
        invisibleGrid.widthHint = 10;
        invisibleText.setLayoutData(invisibleGrid);
        Text invisibleText2 = new Text(parent, SWT.BORDER);
        invisibleText2.setVisible(false);
        invisibleText2.setLayoutData(invisibleGrid);
        
        m_addButton = new Button(parent, SWT.PUSH);
        m_addButton.setText(Messages.AUTPropertyPageAdd);
        m_addButton.setLayoutData(buttonGrid());
        String selectedObject = m_autToolKitComboBox.getSelectedObject();
        m_addButton.setEnabled(selectedObject != null 
                && selectedObject.trim().length() > 0);
        
        m_duplicateButton = new Button(parent, SWT.PUSH);
        m_duplicateButton.setText(Messages.AUTPropertyPageDuplicate);
        m_duplicateButton.setData(SwtToolkitConstants.WIDGET_NAME, 
                "AUTPropertyPage.Duplicate");  //$NON-NLS-1$
        m_duplicateButton.setLayoutData(buttonGrid());
        m_duplicateButton.setEnabled(false);

        m_editButton = new Button(parent, SWT.PUSH);
        m_editButton.setText(Messages.AUTPropertyPageEdit);
        m_editButton.setLayoutData(buttonGrid());
        m_editButton.setEnabled(false);

        m_removeButton = new Button(parent, SWT.PUSH);
        m_removeButton.setText(Messages.AUTPropertyPageRemove);
        m_removeButton.setLayoutData(buttonGrid());
        m_removeButton.setEnabled(false);
    }

    /**
     * Handles the selectionEvent of the Add Button.
     */
    private void handleAddButtonEvent() {
        editNewAUTConfig(PoMaker.createAUTConfigPO());
    }

    /**
     * Edit a newly create AUT configuration
     * @param autConfig the new configuration
     */
    private void editNewAUTConfig(IAUTConfigPO autConfig) {
        String autName = m_autMain.getName();
        if (autName == null || autName.length() == 0) {
            autName = m_autNameText.getText();
        }
        String[] selection = m_autConfigList.getSelection();
        AUTConfigPropertiesDialog dialog = new AUTConfigPropertiesDialog(
                m_addButton.getShell(), autConfig, getAutToolkit(), autName,
                m_autMain, new AutIdValidator(m_project, m_autMain, 
                        autConfig), 
                new AutConfigNameValidator(m_autMain, autConfig));
        dialog.create();
        DialogUtils.setWidgetNameForModalDialog(dialog);
        dialog.getShell().setText(Messages.AUTPropertiesDialogAUTConfigTitle);
        dialog.open();
        if (dialog.getReturnCode() == Window.OK) {
            m_autConfigList.add(autConfig.getName());
            String[] autConfigList = m_autConfigList.getItems();          
            setAutConfigList(autConfigList);
            m_autMain.addAutConfigToSet(autConfig);
            setFocus(new String[]{autConfig.getName()});
            return;
        }
        setFocus(selection);
    }

    /**
     * @return the selected toolkit
     */
    private String getAutToolkit() {
        if (m_autMain.getToolkit() == null) {
            m_autMain.setToolkit(m_autToolKitComboBox.getSelectedObject());
        }
        return m_autMain.getToolkit();
    }
    
    /**
     * Handles the selectionEvent of the Edit Button.
     */
    private void handleEditButtonEvent() {
        IAUTConfigPO autConfig = getSelectedAUTConf();
        if (autConfig != null) {
            Map<String, String> preEditConfig = 
                PoMaker.createAUTConfigPO().getConfigMap();
            Utils.makeAutConfigCopy(autConfig.getConfigMap(), 
                preEditConfig);
            AUTConfigPropertiesDialog dialog = new AUTConfigPropertiesDialog(
                m_editButton.getShell(), autConfig, getAutToolkit(), 
                m_autMain.getName(), m_autMain,
                new AutIdValidator(m_project, m_autMain, 
                        autConfig),
                new AutConfigNameValidator(m_autMain, autConfig));
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            dialog.getShell().setText(
                    Messages.AUTPropertiesDialogAUTConfigTitle);
            dialog.open();
            if (dialog.getReturnCode() == Window.OK) {
                final Set<IAUTConfigPO> autConfigSet = 
                    m_autMain.getAutConfigSet();
                String[] listHelper = 
                    new String[autConfigSet.size()];
                int i = 0;
                for (IAUTConfigPO configPO : autConfigSet) {
                    listHelper[i] = configPO.getName();
                    i++;
                }
                setAutConfigList(listHelper);
            } else {
                Utils.makeAutConfigCopy(preEditConfig, 
                    autConfig.getConfigMap());
            }
            
            // Prevents "unique contstraint violations"
            Map<String, String> newAutConfigMap  = 
                    new HashMap<String, String> (autConfig.getConfigMap());
            autConfig.setConfigMap(newAutConfigMap);
            
            setFocus(new String[]{autConfig.getName()});
        }
    }

    /**
     * @return the AUTConfigPO from the selection or null if no selection
     * matches
     */
    private IAUTConfigPO getSelectedAUTConf() {
        String[] selection = m_autConfigList.getSelection();
        if (selection.length == 0) {
            return null;
        }
        String selectedAutConfigName = selection[0];
        Set<IAUTConfigPO> autConfigSet = m_autMain.getAutConfigSet();
        for (IAUTConfigPO configPO : autConfigSet) {
            String autConfigName = configPO.getName();
            if (selectedAutConfigName.equals(autConfigName)) {
                return configPO;
            }
        }
        return null;
    }
    

    /**
     * Handles the selectionEvent of the Remove Button.
     */
    private void handleRemoveButtonEvent() {
        IAUTConfigPO autConfig = null;
        String[] selection = m_autConfigList.getSelection();
        if (!StringConstants.EMPTY.equals(selection[0])) {
            for (IAUTConfigPO configPO : m_autMain.getAutConfigSet()) {
                String autConfigName = configPO.getName();
                if (selection[0].equals(autConfigName)) {
                    autConfig = configPO;
                    break;
                }
            }
            m_autMain.removeAutConfig(autConfig);
            m_autConfigList.remove(selection[0]);
            if (m_autConfigList.getItemCount() > 0) {
                m_autConfigList.setSelection(0);
            }
            handleAutConfigListEvent();
        }
    }
    
    /**
     * Sets the focus on the new/edited autConfigName.
     * @param autConfigName The new/edited autConfigName.
     */
    private void setFocus(String[] autConfigName) {
        m_autConfigList.setSelection(autConfigName);
        handleAutConfigListEvent();
    }

    /**
     * Handles the selectionEvent of the autConfig list.
     */
    private void handleAutConfigListEvent() {
        if (m_autConfigList.getItemCount() == 0) {
            m_editButton.setEnabled(false);
            m_duplicateButton.setEnabled(false);
            m_removeButton.setEnabled(false);
            return;
        }
        String[] selection = m_autConfigList.getSelection();
        if (selection.length > 0) {
            m_editButton.setEnabled(true);
            m_duplicateButton.setEnabled(true);
            m_removeButton.setEnabled(true);
        }
    }

   
    /**
     * @param parent The composite.
     * @return a new text field with validation.
     */
    private CheckedRequiredText newRequiredTextField(Composite parent) {
        final CheckedRequiredText textField = 
            new CheckedRequiredText(parent, SWT.BORDER);
        GridData textGrid = new GridData(GridData.FILL, GridData.CENTER, true,
            false, NUM_COLUMNS_3 - 1, 1);
        LayoutUtil.addToolTipAndMaxWidth(textGrid, textField);
        textField.setLayoutData(textGrid);
        return textField;
    }

    /**
     * Creates a new composite.
     * 
     * @param parent The parent composite.
     * @param numColumns The number of columns for this composite.
     * @param verticalAlignment The vertical alignment of this composite.
     * @return The new composite.
     */
    private Composite newComposite(Composite parent, int numColumns, 
        int verticalAlignment) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = false;
        compositeData.verticalAlignment = verticalAlignment;
        composite.setLayoutData(compositeData);
        return composite;

    }

    /**
     * Creates a label for this page.
     * 
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    private Label newLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER,
            false, false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;
    }
  
    /**
     * Creates new gridData for the buttons.
     * 
     * @return The new GridData.
     */
    private GridData buttonGrid() {
        GridData buttonData = new GridData();
        buttonData.horizontalAlignment = GridData.FILL;
        return buttonData;

    }

    /**
     * Adds listeners.
     */
    private void addListener() {
        m_addButton.addSelectionListener(m_selectionListener);
        m_editButton.addSelectionListener(m_selectionListener);
        m_duplicateButton.addSelectionListener(m_selectionListener);
        m_removeButton.addSelectionListener(m_selectionListener);
        m_autConfigList.addSelectionListener(m_selectionListener);
        m_autConfigList.addMouseListener(new MouseAdapter() {
          
            public void mouseDoubleClick(MouseEvent e) {
                handleEditButtonEvent();
            }
        });
        m_autNameText.addVerifyListener(m_verifyListener);
        m_autNameText.addModifyListener(m_modifyListener);
        m_autToolKitComboBox.addSelectionListener(m_selectionListener);
    }

    /**
     * This private inner class contains a new SelectionListener.
     * 
     * @author BREDEX GmbH
     * @created 10.02.2005
     */
    private class WidgetSelectionListener implements SelectionListener {

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
            if (o.equals(m_addButton)) {
                handleAddButtonEvent();
                return;
            } else if (o.equals(m_editButton)) {
                handleEditButtonEvent();
                return;
            } else if (o.equals(m_duplicateButton)) {
                handleDuplicateButtonEvent();
                return;
            } else if (o.equals(m_removeButton)) {
                handleRemoveButtonEvent();
                return;
            } else if (o.equals(m_autConfigList)) {
                handleAutConfigListEvent();
                return;
            } else if (o.equals(m_autToolKitComboBox)) {
                handleAutToolKitComboBoxEvent();
                return;
            } else if (o.equals(m_propAddButton)) {
                handlePropAddButtonEvent();
                return;
            } else if (o.equals(m_propRemoveButton)) {
                handlePropRemoveButtonEvent();
                return;
            }

            Assert.notReached(Messages.EventActivatedUnknownWidget 
                + StringConstants.COLON + StringConstants.SPACE 
                + StringConstants.APOSTROPHE + String.valueOf(e.getSource()) 
                + StringConstants.APOSTROPHE);
        }

        /**
         * Handles the selections
         */
        private void handleAutToolKitComboBoxEvent() {
            String selectedObject = m_autToolKitComboBox.getSelectedObject();
            String oldToolkit = m_autMain.getToolkit();
            m_autMain.setToolkit(selectedObject);
            AUTSettingWizardPage.checkToolkit(getShell(), 
                    m_autMain, oldToolkit);
            m_addButton.setEnabled(selectedObject != null 
                    && selectedObject.trim().length() > 0);
            if (CommandConstants.RCP_TOOLKIT.equals(selectedObject)) {
                m_generateNames.setEnabled(true);
                m_generateNames.setSelection(true);
            } else {
                m_generateNames.setEnabled(false);
                m_generateNames.setSelection(false);
            }
            checkForErrors();
        }

        /**
         * Reacts, when an object is double clicked.
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            handleSelectionEvent(e);
        }
    }

    /**
     * This private inner class contains a new VerifyListener.
     * 
     * @author BREDEX GmbH
     * @created 11.02.2005
     */
    private class WidgetVerifyListener implements VerifyListener {

        /**
         * {@inheritDoc}
         */
        public void verifyText(VerifyEvent e) {
            Object o = e.getSource();
            if (o.equals(m_autNameText)) {
                m_autNameText.setBackground(
                        e.display.getSystemColor(SWT.COLOR_WHITE));
                return;
            }
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                + StringConstants.DOT);
        }
    }

    /**
     * 
     */
    private void handleDuplicateButtonEvent() {
        IAUTConfigPO cfg = getSelectedAUTConf();
        if (cfg != null) {
            final IAUTConfigPO newConfig = PoMaker.createAUTConfigPO(cfg);
            String name = newConfig.getName();
            newConfig.setValue(AutConfigConstants.AUT_CONFIG_NAME,
                    Messages.AUTPropertyPageDupPrefix + name); 
            String autId = newConfig.getValue(AutConfigConstants.AUT_ID, null);
            if (autId != null && autId.length() > 0) {
                IValidator val = new AutIdValidator(m_project);
                String newAutId = autId;
                boolean valid = false;
                int i = 1;
                while (!valid) {
                    newAutId = autId + StringConstants.UNDERSCORE + i++;
                    if (val.validate(newAutId).isOK()) {
                        valid = true;
                    }
                }
                newConfig.setValue(AutConfigConstants.AUT_ID, newAutId);
            }
            editNewAUTConfig(newConfig);
        }
    }

    /**
     * @return Returns the autName.
     */
    public String getAutName() {
        return m_autNameText.getText();
    }

    /**
     * @param autName The autNameText to set.
     */
    public void setAutName(String autName) {
        m_autNameText.setText(autName);
    }

    /**
     * @return Returns the autConfig list.
     */
    public String[] getAutConfigList() {
        return m_autConfigList.getItems();
    }

    /**
     * @param autConfigList The autConfigList to set.
     */
    private void setAutConfigList(String[] autConfigList) {
        if (autConfigList[0] != null) {
            Arrays.sort(autConfigList);
            m_autConfigList.removeAll();
            m_autConfigList.setItems(autConfigList);
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void okPressed() {
        m_autMain.setName(m_autNameText.getText());
        m_autMain.setToolkit(m_autToolKitComboBox.getSelectedObject());
        m_autMain.setGenerateNames(m_generateNames.getSelection());
        m_autMain.setPropertyMap(AutPropertyManager.convertPropertyListToMap(
                m_viewModel));
        setAutMain(m_autMain);
        super.okPressed();
    }
    
    
    /**
     * {@inheritDoc}
     */
    public int open() {
        checkForErrors();
//        if (!m_edit) {
//            enableOKButton(false);
//        }
        return super.open();
    }

    /**
     * @return Returns the autMain.
     */
    public IAUTMainPO getAutMain() {
        return m_autMain;
    }

    /**
     * @param autMain
     *            The autMain to set.
     */
    public void setAutMain(IAUTMainPO autMain) {
        m_autMain = autMain;
    }
    
    /**
     * Creates a separator line.
     * @param composite The parent composite.
     */
    private void separator(Composite composite) {
        newLabel(composite, StringConstants.EMPTY);
        Label sep = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData sepData = new GridData();
        sepData.horizontalAlignment = GridData.FILL;
        sepData.horizontalSpan = NUM_COLUMNS_3;
        sep.setLayoutData(sepData);
        newLabel(composite, StringConstants.EMPTY);
    }
    
    /**
     * This private inner class contains a new ModifyListener.
     * @author BREDEX GmbH
     * @created 12.07.2005
     */
    private class WidgetModifyListener implements ModifyListener {

        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            Object o = e.getSource();
            if (o.equals(m_autNameText)) {
                checkForErrors();
                return;
            }           
        }       
    }
    
    /**
     * Creates the graphical components for viewing and changing the list of
     * AUT IDs.
     * 
     * @param parent The parent composite for the graphical components.
     */
    private void createAutIdList(Composite parent) {
        Composite autIdComposite = new AutIdListComposite(parent, m_autMain, 
                new AutIdValidator(m_project, m_autMain));
        GridData compositeData = new GridData();
        compositeData.horizontalAlignment = SWT.FILL;
        compositeData.grabExcessHorizontalSpace = true;
        autIdComposite.setLayoutData(compositeData);
    }

    /** @author BREDEX GmbH */
    private abstract static class PropertyEditingSupport
            extends EditingSupport {

        /**
         * Constructor
         * 
         * @param viewer The viewer
        */
        public PropertyEditingSupport(ColumnViewer viewer) {
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
    
    /** @author BREDEX GmbH */
    private class PropertyNameEditingSupport
            extends PropertyEditingSupport {
        
        /**
         * Constructor
         * 
         * @param viewer The viewer
        */
        public PropertyNameEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }
        
        /**
         * {@inheritDoc}
         */
        protected Object getValue(Object element) {
            return ((AutProperty)element).getName();
        }
        
        /**
         * {@inheritDoc}
         */
        protected void setValue(Object element, Object name) {
            AutProperty prop = (AutProperty) element;
            prop.setName((String)name);
            getViewer().update(element, null);
            checkForErrors();
        }
    }
   
    /** @author BREDEX GmbH */
    private class PropertyValueEditingSupport
            extends PropertyEditingSupport {
      
        /**
         * Constructor
         * 
         * @param viewer The viewer
        */
        public PropertyValueEditingSupport(ColumnViewer viewer) {
            super(viewer);
        }
      
        /**
         * {@inheritDoc}
         */
        protected Object getValue(Object element) {
            return ((AutProperty)element).getValue();
        }
      
        /**
         * {@inheritDoc}
         */
        protected void setValue(Object element, Object value) {
            AutProperty prop = (AutProperty) element;
            prop.setValue((String)value);
            getViewer().update(element, null);
            checkForErrors();
        }
    }
}