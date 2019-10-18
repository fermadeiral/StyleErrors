/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.extensions.wizard.NewJubulaExtensionWizard;
import org.eclipse.jubula.extensions.wizard.i18n.Messages;
import org.eclipse.jubula.extensions.wizard.model.Storage;
import org.eclipse.jubula.extensions.wizard.utils.Status;
import org.eclipse.jubula.extensions.wizard.utils.Tools;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.xml.businessmodell.AbstractComponent;
import org.eclipse.jubula.tools.internal.xml.businessmodell.CompSystem;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ConcreteComponent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;

/**
 * The third page of the New Jubula Extension Wizard.
 * This page prompts fields for the project's properties (ID, version, name and
 * vendor) and allows the user to select a component type and the component.
 * 
 * @author BREDEX GmbH
 */
public final class NewJubulaExtensionWizardPageThree extends WizardPage {

    /** The page's ID */
    private static final String PAGE_NAME = Messages.PageThree_PageName;
    
    /** The page's title */
    private static final String PAGE_TITLE = Messages.PageThree_PageTitle;
    
    /** The page's description */
    private static final String PAGE_DESCRIPTION = 
            Messages.PageThree_PageDescription;
    
    /** The page's container */
    private Composite m_container;
    
    /** The Properties group instance */
    private PropertiesGroup m_propertiesGroup;
    
    /** The Component group instance */
    private ComponentGroup m_componentGroup;
    
    /** The Optionals group instance */
    private OptionalsGroup m_optionalsGroup;
    
    /** The component type label */
    private Label m_componentTypeLabel;

    /** The component type combo box */
    private Combo m_componentType;
    
    /** The custom component text field */
    private Text m_customComponent;
    
    /** The standard component combo box */
    private Combo m_standardComponent;

    /** The component types */
    private String[] m_componentTypes;

    /** The component types qualifiers */
    private String[] m_componentTypesQualifiers;
    
    /** The instance of this wizard's storage */
    private final Storage m_storage;

    /**
     * The constructor that creates the page and sets
     * its title and description.
     * @param storage the storage instance this page instance should use
     */
    public NewJubulaExtensionWizardPageThree(Storage storage) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        
        m_storage = storage;
        m_propertiesGroup = new PropertiesGroup();
        m_componentGroup = new ComponentGroup();
        m_optionalsGroup = new OptionalsGroup();
    }
    
    @Override
    public void createControl(Composite parent) {
        m_container = new Composite(parent, SWT.NONE);
        
        
        setControl(m_container);
        m_container.setLayout(new FormLayout());
        
        m_propertiesGroup.createControl(m_container);
        
        m_componentGroup.createControl(m_container);
        
        m_optionalsGroup.createControl(m_container);
        
        m_container.getShell().setSize(550, 700);
        setPageComplete(false);
        
        List<Control> tabOrder = new ArrayList<>();
        tabOrder.addAll(Arrays.asList(m_propertiesGroup.getTabOrderList()));
        tabOrder.addAll(Arrays.asList(m_componentGroup.getTabOrderList()));
        tabOrder.addAll(Arrays.asList(m_optionalsGroup.getTabOrderList()));
        m_container.setTabList(tabOrder.toArray(new Control[0]));
        
        PlatformUI.getWorkbench().getHelpSystem()
            .setHelp(getShell(), 
                    Messages.PageThreeQualifier);
    }
    
    /**
     * Inits the List of component types.
     */
    private void initCompTypes() {
        List<String> tmpList = new ArrayList<>();
        List<String> tmpSubList = new ArrayList<>();
        Map<String, String> helpMap = new HashMap<>();
        
        final CompSystem compSystem = ComponentBuilder.getInstance()
            .getCompSystem();
        final String[] toolkitComponents = compSystem.getComponentTypes(
            m_storage.getToolkit().getToolkitId());
        for (String currComponent : toolkitComponents) {
            Component component = compSystem.findComponent(currComponent);
            if (component instanceof ConcreteComponent 
                    || component instanceof AbstractComponent) {
                tmpList.add(component.getType());
                helpMap.put(component.getType(), currComponent);
            }
        }
        m_componentTypes = tmpList.toArray(new String[tmpList.size()]);
        Arrays.sort(m_componentTypes);
        for (String compType : m_componentTypes) {
            tmpSubList.add(helpMap.get(compType));
        }
        m_componentTypesQualifiers = tmpSubList.toArray(
            new String[tmpSubList.size()]
        );
        m_componentGroup.setComponentTypes();
    }
    
    
    
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            initCompTypes();
            m_propertiesGroup.initializeValues();
            PlatformUI.getWorkbench()
                .getHelpSystem().setHelp(getShell(), 
                        Messages.PageThreeQualifier);
        }
        super.setVisible(visible);
    }

    /**
     * Checks whether all necessary conditions are met for this page
     * to be complete. Displays an error messages otherwise.
     * @return <code>true</code> if all conditions are met,
     *         <code>false</code> otherwise
     */
    public boolean checkPageComplete() {
        Status idStatus = m_propertiesGroup.validateId();
        Status versionStatus = m_propertiesGroup.validateVersion();
        Status componentStatus = m_componentGroup.validateComponentGroup();
        
        if (idStatus == Status.PROPERTIES_ID_OK 
                && versionStatus == Status.PROPERTIES_VERSION_OK
                && componentStatus == Status.COMPONENT_OK) {
            setErrorMessage(null);
            return true;
        }
        switch (idStatus) {
            case PROPERTIES_ID_ILLEGAL: 
                setErrorMessage(
                    Messages.PageThree_IdWrongFormatMsg);
                return false;
            case PROPERTIES_ID_EMPTY:
                setErrorMessage(Messages.PageThree_IdEmptyMsg);
                return false;
            case PROPERTIES_ID_OK:
                break;
            default:
                throw new IllegalArgumentException();
        }
        
        switch (versionStatus) {
            case PROPERTIES_VERSION_ILLEGAL:
                setErrorMessage(Messages.PageThree_VersionWrongFormatMsg);
                return false;
            case PROPERTIES_VERSION_EMPTY:
                setErrorMessage(Messages.PageThree_VersionEmptyMsg);
                return false;
            case PROPERTIES_VERSION_OK:
                break;
            default:
                throw new IllegalArgumentException();
        }
        switch (componentStatus) {
            case COMPONENT_TYPE_MISSING:
                setErrorMessage(
                        Messages.PageThree_ComponentTypeMissingMsg);
                return false;
            case COMPONENT_MISSING:
                setErrorMessage(Messages.PageThree_ComponentMissingMsg);
                return false;
            case COMPONENT_OK:
                break;
            default:
                throw new IllegalArgumentException();
        }
        
        return false;
    }

    
    /**
     * Creates and handles all items and controls of the properties group.
     */
    private final class PropertiesGroup {
        
        /** The OSGi definition of an ALPHA */
        private static final String ALPHA = "([a-zA-Z])"; //$NON-NLS-1$
        
        /** The OSGi definition of a DIGIT */
        private static final String DIGIT = "([0-9])"; //$NON-NLS-1$
        
        /** The OSGi definition of an ALPHANUM */
        private static final String ALPHANUM = "(" + ALPHA + "|" + DIGIT + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        /** The OSGi definition of a QUALIFIER */
        private static final String QUALIFIER =
                "(" + ALPHANUM + "|_|-)+"; //$NON-NLS-1$ //$NON-NLS-2$
        
        /** The OSGi definition of a NUMBER */
        private static final String NUMBER = "(" + DIGIT + ")+"; //$NON-NLS-1$ //$NON-NLS-2$
        
        /** The OSGi definiton of a MAJOR (version) */
        private static final String MAJOR = NUMBER;
        
        /** The OSGi definition of a MINOR (version) */
        private static final String MINOR = NUMBER;
        
        /** The OSGi definition of a MICRO (version) */
        private static final String MICRO = NUMBER;
        
        /** The OSGi definition of a VERSION */
        private static final String VERSION =
                MAJOR + "([.]" + MINOR + "([.]" + MICRO //$NON-NLS-1$ //$NON-NLS-2$
                + "([.]" + QUALIFIER + ")?)?)?"; //$NON-NLS-1$ //$NON-NLS-2$
        
        /** The OSGi definition of a TOKEN */
        private static final String TOKEN = 
                "(" + ALPHANUM + "|_|-)+"; //$NON-NLS-1$ //$NON-NLS-2$
        
        /** The OSGi definition of a SYMBOLIC NAME */
        private static final String SYMBOLIC_NAME = 
                ALPHA + "(" + TOKEN + ")?([.]" + TOKEN + ")*"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        
        
        /** The group container */
        private Group m_group;
        
        /** The id text field */
        private Text m_id;
        
        /** The version text field */
        private Text m_version;
        
        /** The name text field */
        private Text m_name;
        
        /** The vendor text field */
        private Text m_vendor;
        
        /** 
         * Calls methods to create all items and their listeners 
         * @param container
         *              The parent container
         */
        public void createControl(Composite container) {
            createGroup();
            createId();
            createVersion();
            createName();
            createVendor();
            
            m_group.setTabList(new Control[] {
                m_id, m_version, m_name, m_vendor
            });
        }

        /**
         * Creates the group
         */
        private void createGroup() {
            m_group = new Group(m_container, SWT.NONE);
            m_group.setText(Messages.PageThree_PropertiesGroupLbl);
            m_group.setLayout(new FormLayout());
            FormData fdGroup = new FormData();
            fdGroup.left = new FormAttachment(0, 10);
            fdGroup.top = new FormAttachment(0, 10);
            fdGroup.bottom = new FormAttachment(0, 163);
            fdGroup.right = new FormAttachment(100, -10);
            m_group.setLayoutData(fdGroup);
        }

        /**
         * Creates the ID label and text field
         */
        private void createId() {
            Label lblId = new Label(m_group, SWT.NONE);
            FormData fdLblId = new FormData();
            fdLblId.right = new FormAttachment(0, 62);
            fdLblId.top = new FormAttachment(0, 13);
            fdLblId.left = new FormAttachment(0, 7);
            lblId.setLayoutData(fdLblId);
            lblId.setText(Messages.PageThree_IdLbl);
            
            m_id = new Text(m_group, SWT.BORDER);
            FormData fdId = new FormData();
            fdId.right = new FormAttachment(100, -10);
            fdId.top = new FormAttachment(0, 10);
            fdId.left = new FormAttachment(0, 117);
            m_id.setLayoutData(fdId);
            m_id.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    setPageComplete(checkPageComplete());
                    m_storage.setID(m_id.getText());
                }
            });
        }

        /**
         * Creates the version label and text field
         */
        private void createVersion() {
            Label lblVersion = new Label(m_group, SWT.NONE);
            FormData fdLblVersion = new FormData();
            fdLblVersion.right = new FormAttachment(0, 62);
            fdLblVersion.top = new FormAttachment(0, 40);
            fdLblVersion.left = new FormAttachment(0, 7);
            lblVersion.setLayoutData(fdLblVersion);
            lblVersion.setText(Messages.PageThree_VersionLbl);
            
            m_version = new Text(m_group, SWT.BORDER);
            FormData fdVersion = new FormData();
            fdVersion.right = new FormAttachment(100, -10);
            fdVersion.top = new FormAttachment(0, 37);
            fdVersion.left = new FormAttachment(0, 117);
            m_version.setLayoutData(fdVersion);
            m_version.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    setPageComplete(checkPageComplete());
                    m_storage.setVersion(m_version.getText());
                }
            });
        }

        /**
         * Creates the name label and text field
         */
        private void createName() {
            Label lblName = new Label(m_group, SWT.NONE);
            FormData fdLblName = new FormData();
            fdLblName.right = new FormAttachment(0, 62);
            fdLblName.top = new FormAttachment(0, 67);
            fdLblName.left = new FormAttachment(0, 7);
            lblName.setLayoutData(fdLblName);
            lblName.setText(Messages.PageThree_NameLbl);
            
            m_name = new Text(m_group, SWT.BORDER);
            FormData fdName = new FormData();
            fdName.right = new FormAttachment(100, -10);
            fdName.top = new FormAttachment(0, 64);
            fdName.left = new FormAttachment(0, 117);
            m_name.setLayoutData(fdName);
            m_name.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    m_storage.setName(m_name.getText());
                }
            });
        }
        
        /**
         * Creates the vendor label and text field
         */
        private void createVendor() {
            Label lblVendor = new Label(m_group, SWT.NONE);
            FormData fdLblVendor = new FormData();
            fdLblVendor.right = new FormAttachment(0, 62);
            fdLblVendor.top = new FormAttachment(0, 94);
            fdLblVendor.left = new FormAttachment(0, 7);
            lblVendor.setLayoutData(fdLblVendor);
            lblVendor.setText(Messages.PageThree_VendorLbl);

            m_vendor = new Text(m_group, SWT.NONE);
            FormData fdVendor = new FormData();
            fdVendor.right = new FormAttachment(100, -10);
            fdVendor.top = new FormAttachment(0, 91);
            fdVendor.left = new FormAttachment(0, 117);
            m_vendor.setLayoutData(fdVendor);
            m_vendor.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    m_storage.setVendor(m_vendor.getText());
                }
            });
        }
        
        /**
         * @return the group 
         */
        public Group getGroup() {
            return m_group;
        }
        
        /**
         * Validates the entered ID.
         * @return <code>PROPERTIES_ID_OK</code> if the version is legal,<br>
         *         <code>PROPERTIES_ID_ILLEGAL</code> if the version's
         *         format is illegal or<br>
         *         <code>PROPERTIES_ID_EMPTY</code> if the version field
         *         is empty.
         */
        private Status validateId() {
            String id = m_id.getText();
            if (!id.equals("")) { //$NON-NLS-1$
                if (id.matches(SYMBOLIC_NAME)) {
                    return Status.PROPERTIES_ID_OK;
                }
                return Status.PROPERTIES_ID_ILLEGAL;
            }
            return Status.PROPERTIES_ID_EMPTY;
        }
        
        /**
         * Validates the entered version.
         * @return <code>PROPERTIES_VERSION_OK</code> if the version is legal,
         *         <br>
         *         <code>PROPERTIES_VERSION_ILLEGAL</code> if the version's
         *         format is illegal or <br>
         *         <code>PROPERTIES_VERSION_EMPTY</code> if the version field
         *         is empty.
         */
        private Status validateVersion() {
            String version = m_version.getText();
            if (!version.equals("")) { //$NON-NLS-1$
                if (version.matches(VERSION)) {
                    return Status.PROPERTIES_VERSION_OK;
                }
                return Status.PROPERTIES_VERSION_ILLEGAL;
            }
            return Status.PROPERTIES_VERSION_EMPTY;
        }

        /**
         * Initializes the page's properties fields
         */
        private void initializeValues() {
            Storage storage = m_storage;
            String id = storage.getProjectName()
                    .replaceAll("[^a-zA-Z0-9\\._-]", "_"); //$NON-NLS-1$ //$NON-NLS-2$
            if (!id.equals(storage.getProjectName())) {
                setMessage(Messages.PageThree_ProjectNameIllegalCharactersMsg, 
                        IMessageProvider.INFORMATION);
            }
            m_id.setText(id);
            m_version.setText(Messages.PageThree_DefaultVersionTxt);
            m_name.setText(storage.getProjectName());
            m_id.setFocus();
        }
        
        /**
         * @return an array that contains the tab order of 
         *          this groups components
         */
        public Control[] getTabOrderList() {
            return new Control[] { m_group };
        }
    }
    
    /**
     * Creates and handles all items and controls of the component group.
     */
    private final class ComponentGroup {
        /** The group container */
        private Group m_group;
        
        /** The "Use an existing class" browse button */
        private Button m_browseCustom;

        /** The custom component radio button */
        private Button m_customComponentBtn;

        /** The standard component radio button */
        private Button m_standardComponentBtn;
        
        /** 
         * Calls methods to create all items and their listeners 
         * @param container
         *              The parent container
         */
        public void createControl(Composite container) { 
            createGroup();
            createComponentType();
            createStandardComponentSupport();
            createCustomComponentSupport();
            
            m_group.setTabList(new Control[] {
                m_componentType, m_standardComponentBtn, 
                m_standardComponent, m_customComponentBtn, 
                m_customComponent, m_browseCustom
            });
        }

        /**
         * Creates the group and the heading label
         */
        private void createGroup() {
            m_group = new Group(m_container, SWT.NONE);
            m_group.setText(Messages.PageThree_ComponentGroupLbl);
            FormData fdGroup = new FormData();
            fdGroup.bottom = 
                    new FormAttachment(m_propertiesGroup.getGroup(),
                            200, SWT.BOTTOM);
            fdGroup.top = 
                    new FormAttachment(m_propertiesGroup.getGroup(), 10);
            fdGroup.right = 
                    new FormAttachment(m_propertiesGroup.getGroup(),
                            0, SWT.RIGHT);
            fdGroup.left = new FormAttachment(0, 10);
            m_group.setLayout(new FormLayout());
            m_group.setLayoutData(fdGroup);
        }
        
        /**
         * Creates the "create a new class" radio button and its controls
         */
        private void createComponentType() {
            m_componentTypeLabel = new Label(m_group, SWT.NONE);
            m_componentTypeLabel.setText(Messages
                    .PageThree_ChooseComponentTypeLbl);
            FormData fdComponentTypeLabel = new FormData();
            fdComponentTypeLabel.top = new FormAttachment(0, 13);
            fdComponentTypeLabel.left = new FormAttachment(0, 10);
            m_componentTypeLabel.setLayoutData(fdComponentTypeLabel);
            
            m_componentType = new Combo(m_group, SWT.READ_ONLY);
            FormData fdComponentType = new FormData();
            fdComponentType.top = new FormAttachment(0, 10);
            fdComponentType.left = new FormAttachment(m_componentTypeLabel, 10);
            fdComponentType.right = new FormAttachment(100, -30);
            m_componentType.setLayoutData(fdComponentType);
            
            m_componentType.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (m_componentType.getText().equals("")) { //$NON-NLS-1$
                        m_standardComponent.removeAll();
                        setButtonsEnabled(false, false);
                    } else {
                        int index = Arrays.binarySearch(m_componentTypes,
                                m_componentType.getText());
                        List<Object> components = getComponents(
                                m_componentTypesQualifiers[index]);
                        m_standardComponent.removeAll();
                        components.forEach(new Consumer<Object>() {
                            @Override
                            public void accept(Object c) {
                                m_standardComponent
                                    .add(((Component)c).getType());
                            }
                        });
                        boolean standardEnabled = m_standardComponent
                                .getItemCount() > 0;
                        setButtonsEnabled(standardEnabled, true);
                        setComponentsEnabled(standardEnabled, standardEnabled 
                                ? false : true);
                        setSelection(standardEnabled, standardEnabled 
                                ? false : true);
                        m_storage
                            .setComponentType(
                                    m_componentTypesQualifiers[index]);
                        setPageComplete(checkPageComplete());
                    }
                }
            });
            
            Tools.createInfo(m_componentType, Messages
                    .PageThree_ComponentTypeInfoTxt, 3, 0);
        }
        
        

        /**
         * Resets the component types if the toolkit has changed.
         */
        private void setComponentTypes() {
            Storage storage = m_storage;
            if (storage.hasToolkitChanged()) {
                m_componentType.removeAll();
                List<String> types = Arrays.asList(m_componentTypes);
                types.forEach(new Consumer<String>() {
                    @Override
                    public void accept(String c) {
                        if ((NewJubulaExtensionWizard.resolveTesterClass(c) 
                                != null) || NewJubulaExtensionWizard
                                    .lookupTesterClassInMap(c) != null) {
                            m_componentType.add(c);
                        }
                    }
                });
                
                storage.setComponentType(null);
                storage.setComponent(null);
                m_standardComponent.removeAll();
                m_customComponent.setText(""); //$NON-NLS-1$
                setComponentsEnabled(false, false);
                setButtonsEnabled(false, false);
                setSelection(false, false);
                
                storage.setToolkitChanged(false);
            }
        }
        
        /**
         * @param type the type of the components that should be returned
         * @return the components of the given type
         */
        private List<Object> getComponents(final String type) {
            CompSystem compSystem = 
                    ComponentBuilder.getInstance().getCompSystem();
            List<Component> components = compSystem.getComponents();
            return components
                .parallelStream()
                .filter(new Predicate<Component>() {
                    @Override
                    public boolean test(Component c) {
                        return c.getToolkitDesriptor()
                                .getToolkitID()
                                .equals(m_storage
                                        .getToolkit()
                                        .getToolkitId());
                    }
                })
                .filter(new Predicate<Component>() {
                    @Override
                    public boolean test(Component c) {
                        return c.getRealizedTypes().contains(type);
                    }
                })
                .collect(Collectors.toList());
        }
        
        /**
         * Creates the "Create a class and extend an existing one"
         * radio button, text field, browse button and their controls
         */
        private void createStandardComponentSupport() {
            m_standardComponentBtn = new Button(m_group, SWT.RADIO);
            m_standardComponentBtn
                .setText(Messages.PageThree_StandardComponentBtn);
            FormData fdStandardComponentBtn = new FormData();
            fdStandardComponentBtn.top = 
                    new FormAttachment(m_componentType, 12);
            fdStandardComponentBtn.left = new FormAttachment(0, 10);
            m_standardComponentBtn.setLayoutData(fdStandardComponentBtn);
            m_standardComponentBtn.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (m_standardComponentBtn.getSelection()) {
                        setComponentsEnabled(true, false);
                        setPageComplete(checkPageComplete());
                    }
                }
            });
            m_standardComponentBtn.setEnabled(false);
            
            m_standardComponent = new Combo(m_group, SWT.READ_ONLY);
            FormData fdStandardComponent = new FormData();
            fdStandardComponent.left = new FormAttachment(0, 33);
            fdStandardComponent.right = new FormAttachment(100, -30);
            fdStandardComponent.top = 
                    new FormAttachment(m_standardComponentBtn, 6);
            m_standardComponent.setLayoutData(fdStandardComponent);
            m_standardComponent.setEnabled(false);
            m_standardComponent.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    m_storage
                        .setComponent(m_standardComponent.getText());
                    m_storage.setComponentCustom(false);
                    setPageComplete(checkPageComplete());
                }
            });
            
            Tools.createInfo(m_standardComponent,
                    Messages.PageThree_StandardComponentInfoTxt, 3, 0);
        }
        
        /**
         * Creates the "Create a class and extend an existing one"
         * radio button, text field, browse button and their controls
         */
        private void createCustomComponentSupport() {
            m_customComponentBtn = new Button(m_group, SWT.RADIO);
            m_customComponentBtn.setText(Messages.PageThree_CustomComponentBtn);
            FormData fdCustomComponentBtn = new FormData();
            fdCustomComponentBtn.top = 
                    new FormAttachment(m_standardComponent, 10);
            fdCustomComponentBtn.left = new FormAttachment(0, 10);
            m_customComponentBtn.setLayoutData(fdCustomComponentBtn);
            m_customComponentBtn.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (m_customComponentBtn.getSelection()) {
                        setComponentsEnabled(false, true);
                        setPageComplete(checkPageComplete());
                    }
                }
            });
            m_customComponentBtn.setEnabled(false);
            
            m_browseCustom = new Button(m_group, SWT.NONE);
            m_browseCustom.setText(Messages.PageThree_BrowseBtn);
            FormData fdBrowseCustom = new FormData();
            fdBrowseCustom.top = 
                    new FormAttachment(m_customComponentBtn, 6);
            fdBrowseCustom.right = 
                    new FormAttachment(m_standardComponent, 0, SWT.RIGHT);
            m_browseCustom.setLayoutData(fdBrowseCustom);
            m_browseCustom.setEnabled(false);
            m_browseCustom.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    FilteredTypesSelectionDialog dialog = 
                            new FilteredTypesSelectionDialog(getShell(), false, 
                                    getWizard().getContainer(), 
                                    SearchEngine.createWorkspaceScope(), 5);
                    if (dialog.open() == Window.OK) {
                        IType element = (IType) dialog.getFirstResult();
                        m_customComponent
                            .setText(element.getFullyQualifiedName());
                        m_storage
                            .setComponent(m_customComponent.getText());
                        m_storage
                            .setComponentCustom(true);
                        setPageComplete(checkPageComplete());
                    }
                }
            });
            
            m_customComponent = new Text(m_group, SWT.BORDER);
            FormData fdExisting = new FormData();
            fdExisting.right = new FormAttachment(m_browseCustom, -7, SWT.LEFT);
            fdExisting.top = new FormAttachment(m_browseCustom, 0, SWT.TOP);
            fdExisting.left = 
                    new FormAttachment(m_standardComponent, 0, SWT.LEFT);
            fdExisting.bottom = 
                    new FormAttachment(m_browseCustom, 0, SWT.BOTTOM);
            m_customComponent.setLayoutData(fdExisting);
            m_customComponent.setEnabled(false);
            m_customComponent.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    m_storage
                        .setComponent(m_customComponent.getText());
                    m_storage
                        .setComponentCustom(true);
                    setPageComplete(checkPageComplete());
                }
            });
        }
        
       

        /**
         * Sets the components associated with the radio buttons to either
         * enabled or disabled.
         * @param standard <code>true</code> if the "add support for a standard
         * component of the selected type" combobox should be active, 
         * <code>false</code> otherwise.
         * @param custom <code>true</code> if the "add support for a custom
         * component" text field and browse button should be enabled, 
         * <code>false</code> otherwise
         */
        private void setComponentsEnabled(boolean standard, boolean custom) {
            m_standardComponent.setEnabled(standard);
            
            m_customComponent.setEnabled(custom);
            m_browseCustom.setEnabled(custom);
        }
        
        /**
         * Sets the selection of the standard component button and the
         * custom component button.
         * @param standard whether or not the standard component button should
         * be selected
         * @param custom whether or not the custom component button should
         * be selected
         */
        private void setSelection(boolean standard, boolean custom) {
            m_standardComponentBtn.setSelection(standard);
            m_customComponentBtn.setSelection(custom);
        }
        
        /**
         * Enables or disables the radio buttons, depending on the given
         * parameters.
         * @param standard <code>true</code> if the standard component radio
         * button should be enabled, <code>false</code> otherwise.
         * @param custom <code>true</code> if the custom component radio
         * button should be enabled, <code>false</code> otherwise.
         */
        private void setButtonsEnabled(boolean standard, boolean custom) {
            m_standardComponentBtn.setEnabled(standard);
            m_customComponentBtn.setEnabled(custom);
        }
        
        /**
         * Validates the component group.
         * @return Status the result of the validation
         */
        public Status validateComponentGroup() {
            if (m_componentType.getSelectionIndex() > 0) {
                if ((m_standardComponentBtn.getSelection() 
                        && m_standardComponent.getSelectionIndex() > -1)
                        || (m_customComponentBtn.getSelection() 
                        && !m_customComponent.getText().trim().equals(""))) { //$NON-NLS-1$
                    return Status.COMPONENT_OK;
                }
                return Status.COMPONENT_MISSING;
            }
            return Status.COMPONENT_TYPE_MISSING;
        }
        
        /**
         * @return the group
         */
        public Group getGroup() {
            return m_group;
        }
        
        /**
         * @return an array that contains the tab order of 
         *          this groups components
         */
        public Control[] getTabOrderList() {
            return new Control[] { m_group };
        }
    }
    
    /**
     * The Optionals group
     */
    private final class OptionalsGroup {
        /** The group */
        private Group m_group;
        
        /** The target platform button */
        private Button m_targetPlatform;
        
        /** 
         * Calls methods to create all items and their listeners 
         * @param container
         *              The parent container
         */
        public void createControl(Composite container) { 
            createGroup();
            createTargetPlatform();
            
            m_group.setTabList(new Control[] {
                m_targetPlatform
            });
        }

        /**
         * Creates the group and its controls.
         */
        private void createGroup() {
            m_group = new Group(m_container, SWT.NONE);
            m_group.setText(Messages.PageThree_OptionalsGroupLbl);
            FormData fdGroup = new FormData();
            fdGroup.bottom = 
                    new FormAttachment(m_componentGroup.getGroup(),
                            80, SWT.BOTTOM);
            fdGroup.top = 
                    new FormAttachment(m_componentGroup.getGroup(), 10);
            fdGroup.right = 
                    new FormAttachment(m_componentGroup.getGroup(),
                            0, SWT.RIGHT);
            fdGroup.left = new FormAttachment(0, 10);
            m_group.setLayout(new FormLayout());
            m_group.setLayoutData(fdGroup);
        }
        
        /**
         * Creates the target platform button and its controls
         */
        private void createTargetPlatform() {
            m_targetPlatform = new Button(m_group, SWT.CHECK);
            m_targetPlatform.setText(Messages.PageThree_TargetPlatformLbl);
            FormData fdTargetPlatform = new FormData();
            fdTargetPlatform.top = new FormAttachment(0, 13);
            fdTargetPlatform.left = new FormAttachment(0, 10);
            m_targetPlatform.setLayoutData(fdTargetPlatform);
            m_storage.setTargetPlatform(true);
            m_targetPlatform.setSelection(true);
            
            m_targetPlatform.addSelectionListener(new SelectionAdapter() {
                public void widgetSelected(SelectionEvent e) {
                    m_storage
                        .setTargetPlatform(m_targetPlatform.getSelection());
                }
            });
            
            Tools.createInfo(m_targetPlatform, 
                    Messages.PageThree_TargetPlatformInfo, 3, 0);
        }
        
        /**
         * @return an array that contains the tab order of 
         *          this groups components
         */
        public Control[] getTabOrderList() {
            return new Control[] { m_group };
        }
    }
}
