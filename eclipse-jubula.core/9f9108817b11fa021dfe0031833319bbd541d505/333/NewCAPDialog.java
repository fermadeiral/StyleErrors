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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.businessprocess.CompNameTypeManager;
import org.eclipse.jubula.client.core.businessprocess.ComponentNamesBP;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.core.constants.InitialValueConstants;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.CompNamePopUpTextField;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;



/**
 * @author BREDEX GmbH
 * @created 06.12.2004
 */
public class NewCAPDialog extends TitleAreaDialog {
    
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;    
    /** number of columns = 4 */
    private static final int NUM_COLUMNS_4 = 4;    
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;    
    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;
    /** horizontal span = 3 */
    private static final int HORIZONTAL_SPAN = 3;
    /** the m_text field for the CapPO name */
    private Text m_capNameField;
    /** the m_text field for the component name */
    private CompNamePopUpTextField m_componentNameField;
    /** the combo box for the components */
    private DirectCombo <String> m_componentCombo;
    /** the combo box for the actions */
    private DirectCombo <String> m_actionCombo;
    /** The name of the cap */
    private String m_capName;
    /** The type of the component */
    private String m_componentType;
    /** The name of the component */
    private String m_componentName;
    /** The name of the action */
    private String m_actionName;
    /** The label of the actionCombo */
    private Label m_actionLabel;
    /** The label of the componentTextField */
    private Label m_compNameLabel;
    /** the ISpecTestCasePO */
    private INodePO m_nodeGui;
    /** the modifyListener */
    private final WidgetModifyListener m_modifyListener = 
        new WidgetModifyListener();

    /** the component cache to use for finding and modifying components */
    private IComponentNameCache m_compCache;
    
    /**
     * The constructor.
     * @param parentShell the parent shell
     * @param nodeGui the ISpecTestCasePO.
     * @param compCache The Component Name cache to use.
     */
    public NewCAPDialog(Shell parentShell, INodePO nodeGui, 
            IComponentNameCache compCache) {
       
        super(parentShell);
        m_nodeGui = nodeGui;
        m_compCache = compCache;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.NewCapDialogTitle);
        setMessage(Messages.NewCapDialogMessage);
        setTitleImage(IconConstants.NEW_CAP_DIALOG_IMAGE);
        getShell().setText(Messages.NewCAPDialogShellTitle);
//      new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        LayoutUtil.createSeparator(parent);
        Composite area = new Composite(parent, SWT.NONE);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_4;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        area.setLayoutData(gridData);
        createFields(area);
        LayoutUtil.createSeparator(parent);
        String str = getNextChildrenName(m_nodeGui);
        m_capNameField.setText(str); 
        m_capNameField.selectAll();
        m_capNameField.addModifyListener(m_modifyListener);
        m_componentNameField.addModifyListener(m_modifyListener);
        Plugin.getHelpSystem().setHelp(parent, ContextHelpIds.CAP);
        setHelpAvailable(true);
        return area;
    }

    /**
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, 
        boolean defaultButton) {
        
        Button button = super.createButton(parent, id, label, defaultButton);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        return button;
    }

    /**
     *  This method is called, when the OK button or ENTER was pressed.
     */
    protected void okPressed() {
        m_capName = m_capNameField.getText();
        m_componentType = m_componentCombo.getSelectedObject();
        m_componentName = m_componentNameField.getText();
        m_actionName = m_actionCombo.getSelectedObject();
        setReturnCode(OK);
        close();
    }

    /**
     * {@inheritDoc}
     */
    public boolean close() {
        return super.close();
    }

    /**
     * @param area The composite.
     * creates the editor widgets
     */
    private void createFields(Composite area) {
        createCapNameField(area);
        createComponentCombo(area);
        createComponentName(area);
        createActionCombo(area);
    }
    
    /**
     * @param area The composite.
     * creates the m_text field to edit the CapPO name
     */
    private void createCapNameField(Composite area) {
        Label label = new Label(area, SWT.NONE);
        label.setText(Messages.NewCAPDialogCapNameLabel);
        m_capNameField = new Text(area, SWT.SINGLE | SWT.BORDER);
        GridData gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_capNameField);
        m_capNameField.setLayoutData(gridData);
        LayoutUtil.setMaxChar(m_capNameField);
    }
    
    /**
     * Creates a new GridData.
     * @return grid data
     */
    private GridData newGridData() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = HORIZONTAL_SPAN;
        return gridData;
    }

    /**
     * @param area The composite.
     * creates the combo box to choose the component
     */
    private void createComponentCombo(Composite area) {
        CompSystem compSystem = ComponentBuilder.getInstance().getCompSystem();
        Label label = new Label(area, SWT.NONE);
        label.setText(Messages.NewCAPDialogComponentLabel);
        List <String> valueList = new ArrayList <String> ();
        List <String> displayList = new ArrayList <String> ();
        
        String[] toolkitComponents = compSystem.getComponentTypes(
            GeneralStorage.getInstance().getProject().getToolkit());
        for (String compType : toolkitComponents) {
            if (compSystem.findComponent(compType).isVisible()) {
                valueList.add(compType);
                displayList.add(StringHelper.getInstance().get(compType, true));
            }
        }        
        m_componentCombo = new DirectCombo<String>(area, 
                SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, valueList, 
                displayList, true, true);
        GridData gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_componentCombo);
        m_componentCombo.setLayoutData(gridData);
        m_componentCombo.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                componentSelection();
            }
        });
    }
    
    /**
     * @param area The composite.
     * creates the m_text field to edit the component name
     */
    private void createComponentName(Composite area) {
        m_compNameLabel = new Label(area, SWT.NONE);
        m_compNameLabel.setText(Messages.NewCAPDialogComponentNameLabel);
        m_compNameLabel.setEnabled(false);
        m_componentNameField = new CompNamePopUpTextField(
                m_compCache, area,
                SWT.SINGLE | SWT.BORDER);
        m_componentNameField.setEnabled(false);
        GridData gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_componentNameField);
        m_componentNameField.setLayoutData(gridData);
        LayoutUtil.setMaxChar(m_componentNameField);
    }
    
    /**
     * @param area The composite.
     * creates the combo box to choose the action
     */
    private void createActionCombo(Composite area) {
        m_actionLabel = new Label(area, SWT.NONE);
        m_actionLabel.setText(Messages.NewCAPDialogActionLabel);
        m_actionLabel.setEnabled(false);
        m_actionCombo = new DirectCombo<String>(
            area, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY, 
            new ArrayList<String>(), new ArrayList<String>(), 
            false, false);
        m_actionCombo.setEnabled(false);
        GridData gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_actionCombo);
        m_actionCombo.setLayoutData(gridData);
    }
    
    /**
     * <p>fills in the actionCombo after selecting a component type</p>
     * <p>enables the actionCombo and the componentNameTextField</p>
     */
    private void componentSelection() {
        boolean defaultMappingComponent = false;
        String componentName = StringConstants.EMPTY;
        if (m_componentCombo.getSelectedObject() == null) {
            m_actionCombo.removeAll();
            return;
        }
        Map<String, String> map = StringHelper.getInstance().getMap();
        Component component = ComponentBuilder.getInstance().getCompSystem()
            .findComponent(m_componentCombo.getSelectedObject());
        // If this is a concrete component with default object
        // mapping, the component name is fix and must not be
        // changed by the user. The component name is actually
        // the logical name of the default mapping.
        if (component.isConcrete()) {
            ConcreteComponent cc = (ConcreteComponent)component;
            if (cc.hasDefaultMapping()) {
                defaultMappingComponent = true;
            }
        }
        List<Action> actions = 
            new ArrayList<Action>(component.getActions().size());
        for (Object obj : component.getActions()) {
            Action action = (Action)obj;
            if (!action.isDeprecated() && !action.isApiAction()) {
                actions.add(action);
            }
        }
        
        final int actionSize = actions.size();
        String[] actionNamesSorted = new String[actionSize];
        Map<String, String> helpMap = new HashMap <String, String> ();
        for (int i = 0; i < actionSize; i++) {
            Action action = actions.get(i);
            if (!action.isDeprecated()) {
                String actionName = action.getName();
                actionNamesSorted[i] = map.get(actionName);
                helpMap.put(actionNamesSorted[i], actionName);
            }
        }
        Arrays.sort(actionNamesSorted);
        List <String>actionComboObjList = new ArrayList<String>();
        for (String actionName : actionNamesSorted) {
            actionComboObjList.add(helpMap.get(actionName));
        }
        m_actionCombo.setItems(actionComboObjList, 
            Arrays.asList(actionNamesSorted));
        m_componentNameField.setText(componentName);
        m_compNameLabel.setEnabled(!defaultMappingComponent);
        m_componentNameField.setEnabled(!defaultMappingComponent);
        m_actionLabel.setEnabled(true);
        m_actionCombo.setEnabled(true);
        modifyComponentTypeAction();
        modifyComponentNameFieldAction();
    }
    
    /** 
     * the action of the cap name field
     * @param compNamesAreAlreadyCorrect true, if component names are already correct
     * @return false, if the cap name field contents an error:
     * the step name starts or end with a blank, or the field is empty
     */
    private boolean modifyCapNameFieldAction(
            boolean compNamesAreAlreadyCorrect) {
        
        boolean isCorrect = true;
        int capNameLength = m_capNameField.getText().length();
        if ((capNameLength == 0)
            || (m_capNameField.getText().startsWith(" "))  //$NON-NLS-1$
            || (m_capNameField.getText().charAt(capNameLength - 1) == ' ')) {
            
            isCorrect = false;
        }
        if (isCorrect) {
            if (!compNamesAreAlreadyCorrect) {
                modifyComponentTypeAction();
            } else {
                enableOKButton();
            }
            // ----------------------------
            // FIXME Andreas : Iteration over DB Names.
            // ----------------------------
            
        } else {
            getButton(IDialogConstants.OK_ID).setEnabled(false);
            if (capNameLength == 0) {
                setErrorMessage(Messages.NewCAPDialogEmptyStep); 
            } else {
                setErrorMessage(Messages.NewCAPDialogNotValidStep); 
            }
        }
        return isCorrect;
    }
    
    /** 
     * the action of the component type combobox
     * @return false, if the component type combobox is empty
     */
    private boolean modifyComponentTypeAction() {
        boolean isCorrect = true;
        if (m_componentCombo.getText().length() == 0) {
            isCorrect = false;
        }
        if (isCorrect) {
            m_componentNameField.setFilter(m_componentCombo
                .getSelectedObject());
            modifyComponentNameFieldAction();
        } else {
            if (getButton(IDialogConstants.OK_ID) != null) {
                getButton(IDialogConstants.OK_ID).setEnabled(false);
            }
            setErrorMessage(Messages.NewCAPDialogEmptyCompType); 
        }
        return isCorrect;
    }
    
    /** 
     * the action of the cap name field
     * @return false, if the cap name field contains a simple error:
     *   the step name starts or ends with a blank or the field is empty
     *   type checks are not done at this level, only upon submission
     */
    private boolean modifyComponentNameFieldAction() {
        boolean isCorrect = true, defaultName = false;
        int componentNameLength = m_componentNameField.getText().length();
        if ((componentNameLength == 0)
            || (m_componentNameField.getText().startsWith(
                    StringConstants.SPACE)) 
            || (m_componentNameField.getText().charAt(
                componentNameLength - 1) == ' ')) {
            
            isCorrect = false;
        }
        if (!m_componentNameField.isEnabled()) {
            defaultName = true;
            isCorrect = true;
        }
        String guid = m_compCache.getGuidForName(
                m_componentNameField.getText());
        IComponentNamePO cN = null;
        String type = null;
        if (guid != null) {
            cN = m_compCache.getResCompNamePOByGuid(guid);
            type = cN.getComponentType();
        }
        
        if (cN != null && !ComponentNamesBP.UNKNOWN_COMPONENT_TYPE.equals(type)
            && !CompNameTypeManager.mayBeCompatible(cN,
                    m_componentCombo.getSelectedObject())) {
            enableOKButton();
            setErrorMessage("Using this Component Name has a chance of causing type errors."); //$NON-NLS-1$
            return true;
        }

        if (isCorrect) {
            modifyCapNameFieldAction(true);
            return isCorrect;
        }
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        if (componentNameLength == 0 && !defaultName) {
            setErrorMessage(Messages.NewCAPDialogEmptyCompName);
        } else if (defaultName) {
            setErrorMessage(NLS.bind(Messages.NewCAPDialogReservedCompName, 
                    m_componentNameField.getText()));
        } else {
            setErrorMessage(Messages.NewCAPDialogNotValidCompName);  
        }

        if (isCorrect) {
            modifyCapNameFieldAction(true);
        }
        return isCorrect;
    }
    
    /**
     * enables the OK button and makes a non-error title message
     */
    private void enableOKButton() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        setErrorMessage(null);
    }
    
    /**
     * @return Returns the capNameFieldText.
     */
    public String getCapName() {
        return m_capName;
    }
    
    /**
     * @return Returns the componentComboText.
     */
    public String getComponentType() {
        return m_componentType;
    }
    
    /**
     * @return Returns the actionNameComboText.
     */
    public String getActionName() {
        return m_actionName;
    }
    
    /**
     * @return Returns the componentNameFieldText.
     */
    public String getComponentName() {
        return m_componentName;
    }
    
    /**
     * @param parent Parent TestCase
     * @return name for Next Cap
     */
    private String getNextChildrenName(INodePO parent) {
        String capName = StringConstants.EMPTY;
        int index = parent.getNodeListSize() + 1;
        boolean uniqueName = false;
        while (!uniqueName) {
            capName = InitialValueConstants.DEFAULT_CAP_NAME + index;
            uniqueName = true;
            for (INodePO node : parent.getUnmodifiableNodeList()) {
                if (node.getName().equals(capName)) {
                    uniqueName = false;
                    index++;
                    break;
                }
            }
        }
        return capName;
    }  
    
    /**
     * This private inner class contains a new ModifyListener.
     * @author BREDEX GmbH
     * @created 15.07.2005
     */
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            Object o = e.getSource();
            if (o.equals(m_capNameField)) {
                modifyCapNameFieldAction(false);
                return;
            } else if (o.equals(m_componentNameField)) {
                modifyComponentNameFieldAction();
                return;
            } 
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                + StringConstants.LEFT_PARENTHESIS + o 
                + StringConstants.RIGHT_PARENTHESIS + StringConstants.DOT);
        }     
    }
}