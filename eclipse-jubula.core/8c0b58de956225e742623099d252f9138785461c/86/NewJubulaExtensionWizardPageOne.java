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

import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.environments.IExecutionEnvironment;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.extensions.wizard.i18n.Messages;
import org.eclipse.jubula.extensions.wizard.model.Storage;
import org.eclipse.jubula.extensions.wizard.utils.Tools;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * The first page of the New Jubula Extension Wizard.
 * This page contains input fields for the project name
 * and the execution environment.
 * 
 * @author BREDEX GmbH
 */
public final class NewJubulaExtensionWizardPageOne extends WizardPage {
    
    /** The page's ID */
    private static final String PAGE_NAME = Messages.PageOne_PageName;
    
    /** The page's title */
    private static final String PAGE_TITLE = Messages.PageOne_PageTitle;
    
    /** The page's description */
    private static final String PAGE_DESCRIPTION = 
            Messages.PageOne_PageDescription;
    
    /** The page's container */
    private Composite m_container;
    
    /** The name group instance */
    private final NameGroup m_nameGroup;
    
    /** The location group instance */
    private final LocationGroup m_locationGroup;
    
    /** The JRE group instance */
    private final JREGroup m_jreGroup;
    
    /** A boolean whether or not this page was visible before */
    private boolean m_isFirstCall = true;
    
    /** The instance of this wizard's storage */
    private final Storage m_storage;
    
    /**
     * The constructor that creates the page and sets
     * its title and description.
     * @param storage the storage instance this page instance should use
     */
    public NewJubulaExtensionWizardPageOne(Storage storage) {
        super(PAGE_NAME);
        setTitle(PAGE_TITLE);
        setDescription(PAGE_DESCRIPTION);
        
        m_storage = storage;
        m_nameGroup = new NameGroup();
        m_locationGroup = new LocationGroup();
        m_jreGroup = new JREGroup();
    }
    
    @Override
    public void createControl(Composite parent) {
        m_container = new Composite(parent, SWT.NONE);
        
        setControl(m_container);
        m_container.setLayout(new FormLayout());
        
        m_nameGroup.createControl(m_container);
        m_locationGroup.createControl(m_container);
        m_jreGroup.createControl(m_container);
        
        setPageComplete(false);
        
        List<Control> tabOrder = new ArrayList<>();
        tabOrder.addAll(Arrays.asList(m_nameGroup.getTabOrderList()));
        tabOrder.addAll(Arrays.asList(m_jreGroup.getTabOrderList()));
        m_container.setTabList(tabOrder.toArray(new Control[0]));
        
        PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), 
                Messages.PageOneQualifier);
    }
    
    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (visible) {
            if (m_isFirstCall) {
                m_container.getShell().setSize(600, 750);
                m_isFirstCall = false;
            }
            PlatformUI.getWorkbench().getHelpSystem().setHelp(getShell(), 
                    Messages.PageOneQualifier);
        }
    }
    
    /**
     * Creates and handles all items and controls of the name group.
     */
    private final class NameGroup {
        
        /** The project name text field */
        private Text m_projectName;
        
        /** The project name label */
        private Label m_lblProjectName;
        
        /**
         * Calls methods to create all items and their listeners
         * @param container
         *              The parent container
         */
        public void createControl(Composite container) {
            createLabel(container);
            createTextField(container);
        }
        
        /**
         * Creates the project name label
         * @param container
         *              The parent container
         */
        private void createLabel(Composite container) {
            m_lblProjectName = new Label(container, SWT.NONE);
            FormData fdLblProjectName = new FormData();
            fdLblProjectName.top = new FormAttachment(0, 10);
            fdLblProjectName.left = new FormAttachment(0, 10);
            m_lblProjectName.setLayoutData(fdLblProjectName);
            m_lblProjectName.setText(Messages.PageOne_ProjectNameLbl);
        }
        
        /**
         * Creates the project name text field
         * @param container
         *              The parent container
         */
        private void createTextField(Composite container) {
            m_projectName = new Text(container, SWT.BORDER);
            FormData fdTextProjectName = new FormData();
            fdTextProjectName.top = new FormAttachment(0, 7);
            fdTextProjectName.left = new FormAttachment(m_lblProjectName, 6);
            fdTextProjectName.right = new FormAttachment(100, -10);
            m_projectName.setLayoutData(fdTextProjectName);
            m_projectName.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    String projectName = m_projectName.getText();
                    if (projectName.equals("")) { //$NON-NLS-1$
                        setPageComplete(false);
                        setErrorMessage(Messages.PageOne_ProjectNameEmptyMsg);
                    } else {
                        m_storage.setProjectName(projectName);
                        if (validateName(projectName)) {
                            setPageComplete(true);
                            setErrorMessage(null);
                        } else {
                            setPageComplete(false);
                            setErrorMessage(Messages
                                    .PageOne_ProjectNameInvalidCharsMsg); 
                            m_storage.setProjectName(null);
                        }
                    }
                }
            });
            m_projectName.setFocus();
        }
        
        /**
         * Validates the entered project name.
         * @param projectName
         *      the project name that is supposed to be checked
         * @return
         *      <code>true</code> when the name is valid 
         *      and <code>false</code> otherwise
         */
        private boolean validateName(String projectName) {
            if (m_projectName.getText().length() > 255) {
                return false;
            }
            
            IWorkspace workspace = JavaPlugin.getWorkspace();
            IStatus status = 
                    workspace.validateName(projectName, 
                            IResource.PROJECT);
            if (!m_locationGroup.validateLocation()) {
                return false;
            }
            return ((status.getCode() == IStatus.OK) ? true : false);
        }
        
        /**
         * @return the entered project name
         */
        @Deprecated
        public String getProjectName() {
            return m_projectName.getText().trim();
        }
        
        /**
         * @return the project name text field
         */
        public Text getTextField() {
            return m_projectName;
        }
        
        /**
         * @return an array that contains the tab order of 
         *          this groups components
         */
        public Control[] getTabOrderList() {
            return new Control[] { m_projectName };
        }
    }
    
    /**
     * Creates and handles all items and controls of the location group.
     */
    private final class LocationGroup {
        
        /** Use default location check box */
        private Button m_checkbox;
        
        /** Location browse button */
        private Button m_browseButton;
        
        /** Location browse button formdata */
        private FormData m_fdBrowseButton;
        
        /** Location label */
        private Label m_label;
        
        /** Location text field */
        private Text m_location;
        
        
        /** 
         * Calls methods to create all items and their listeners 
         * @param container
         *              The parent container
         */
        private void createControl(Composite container) {
            createCheckbox(container);
            createBrowseButton(container);
            createLabel(container);
            createTextField(container);
            
            initializePath();
        }

        /**
         * Creates the default location check box and its listeners
         * @param container
         *              The parent container
         */
        private void createCheckbox(Composite container) {
            m_checkbox = new Button(container, SWT.CHECK);
            m_checkbox.setSelection(true);
            FormData fdBtnUseDefaultLocation = new FormData();
            fdBtnUseDefaultLocation.top = 
                    new FormAttachment(m_nameGroup.getTextField(), 18);
            fdBtnUseDefaultLocation.left = new FormAttachment(0, 10);
            m_checkbox.setLayoutData(fdBtnUseDefaultLocation);
            m_checkbox.setText(Messages.PageOne_UseDefaultLocationLbl);
            m_checkbox.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    if (m_checkbox.getSelection()) {
                        m_label.setEnabled(false);
                        m_location.setEnabled(false);
                        m_browseButton.setEnabled(false);
                        initializePath();
                    } else {
                        m_label.setEnabled(true);
                        m_location.setEnabled(true);
                        m_browseButton.setEnabled(true);
                    }
                }
            });
            m_checkbox.setEnabled(false);
            
            m_checkbox.setVisible(false);
        }

        /**
         * Creates the browse button and its listeners
         * @param container
         *              The parent container
         */
        private void createBrowseButton(Composite container) {
            m_browseButton = new Button(container, SWT.NONE);
            m_browseButton.setEnabled(false);
            m_fdBrowseButton = new FormData();
            m_fdBrowseButton.right = new FormAttachment(100, -10);
            m_browseButton.setLayoutData(m_fdBrowseButton);
            m_browseButton.setText(Messages.PageOne_BrowseButtonTxt);
            m_browseButton.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    DirectoryDialog directoryDialog = 
                            new DirectoryDialog(getControl().getShell(),
                                    SWT.OPEN);
                    directoryDialog.setFilterPath(ResourcesPlugin.getWorkspace()
                            .getRoot().getLocation().toString());
                    String path = directoryDialog.open();
                    m_location.setText(path);
                }
                
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // Not needed
                }
            });
            
            m_browseButton.setVisible(false);
        }
        
        /**
         * Creates the location label
         * @param container
         *              The parent container
         */
        private void createLabel(Composite container) {
            m_label = new Label(container, SWT.NONE);
            m_label.setEnabled(false);
            m_fdBrowseButton.top = new FormAttachment(m_label, -5, SWT.TOP);
            FormData fdLblLocation = new FormData();
            fdLblLocation.top = new FormAttachment(m_checkbox, 8);
            fdLblLocation.left = new FormAttachment(0, 10);
            m_label.setLayoutData(fdLblLocation);
            m_label.setText(Messages.PageOne_LocationLbl);
            
            m_label.setVisible(false);
        }
        
        /**
         * Creates the location text field and its listener
         * @param container
         *              The parent container
         */
        private void createTextField(Composite container) {
            m_location = new Text(container, SWT.BORDER);
            m_location.setEnabled(false);
            m_fdBrowseButton.left = new FormAttachment(m_location, 6);
            FormData fdTextLocation = new FormData();
            fdTextLocation.top = new FormAttachment(m_checkbox, 5);
            fdTextLocation.left = new FormAttachment(m_label, 32);
            fdTextLocation.right = new FormAttachment(100, -75);
            m_location.setLayoutData(fdTextLocation);
            m_location.addModifyListener(new ModifyListener() {
                @Override
                public void modifyText(ModifyEvent e) {
                    if (!m_nameGroup.getProjectName().equals("")) { //$NON-NLS-1$
                        if (validateLocation()) {
                            setPageComplete(true);
                            setErrorMessage(null);
                        } else {
                            setPageComplete(false);
                            setErrorMessage(Messages
                                    .PageOne_ProjectLocationInvalidMsg);
                        }
                    }
                }
            });
            
            m_location.setVisible(false);
        }
        
        /**
         * Initializes the path of the location text field
         */
        private void initializePath() {
            m_location.setText(ResourcesPlugin.getWorkspace()
                      .getRoot().getLocation().toString());
        }
        
        /**
         * Validates the entered project location.
         * 
         * @return <code>true</code> if the path is legal and if the project
         *          folder does not exist yet, <code>false</code> otherwise
         */
        private boolean validateLocation() {
            Storage storage = m_storage;
            Path path = new Path(m_location.getText() + "/"  //$NON-NLS-1$
                                + storage.getProjectName());
            Path pathRc = new Path(path + ".rc"); //$NON-NLS-1$
            Path pathToolkit = new Path(path + ".toolkit"); //$NON-NLS-1$
            Path pathFeature = new Path(path + ".feature"); //$NON-NLS-1$
            
            // Check whether paths are legal
            try {
                Paths.get(path.toOSString());
                Paths.get(pathRc.toOSString());
                Paths.get(pathToolkit.toOSString());
                Paths.get(pathFeature.toOSString());
            } catch (InvalidPathException e) {
                return false;
            }
            
            if (!path.toFile().exists()
                    && !pathRc.toFile().exists()
                    && !pathToolkit.toFile().exists()
                    && !pathFeature.toFile().exists()) {
                return true;
            }
            
            return false;
        }
    }
    
    /**
     * Creates and handles all items and controls of the JRE group.
     */
    private final class JREGroup {
        
        /** 
         * A regular expression that describes the ID 
         * of the recommended JRE.
         */
        private final String m_recommendedJre = 
                "Java.*1\\.(([1-9][0-9])|[8-9])"; //$NON-NLS-1$
        
        /** The displayed preference pages inside the "Configure JREs..." 
         * preference window.
         */
        private final String[] m_displayedIds =  new String[] {
            "org.eclipse.jdt.ui.preferences.CompliancePreferencePage", //$NON-NLS-1$
            "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage", //$NON-NLS-1$
            "org.eclipse.jdt.debug.ui.jreProfiles" //$NON-NLS-1$
        };
        
        /** The group container */
        private Group m_group;
        
        /** The execution environment list */
        private Combo m_executionEnvironmentList;
        
        /** The execution environment list FormData */
        private FormData m_fdExecutionEnvironmentList;
        
        /** The execution environment radio button */
        private Button m_executionEnvironmentButton;
        
        /** The JRE info symbol */
        private Object m_infoSymbol;
        
        /** The specific JRE radio button */
        private Button m_specificJreButton;
        
        /** The specific JRE list */
        private Combo m_specificJreList;
        
        /** The default JRE radio button */
        private Button m_defaultJreButton;
        
        /** The JRE preferences link */
        private Link m_preferencesLink;
        
        /** 
         * Calls methods to create all items and their listeners 
         * @param container
         *              The parent container
         */
        public void createControl(Composite container) {
            /* "Use an execution environment JRE" items */
            createGroup(container);
            createExecutionEnvironmentList(m_group);
            createExecutionEnvironmentButton(m_group);
            createInfoSymbol(m_executionEnvironmentButton);
            
            /* "Use project specific JRE" items */
            createSpecificJreButton(m_group);
            createSpecificJreList(m_group);
            
            /* "Use default JRE" items */
            createDefaultJreButton(m_group);
            createPreferencesLink(m_group);
            
            initializeData();
            
            /* For unknown reasons, the tab order is only correct if the
             * m_executionEnvironmentButton is not added to the list.
             * If the radio button is added to the following list, the tab
             * order will be incorrect and the three radio buttons would not 
             * behave as intended.
             * Even though the radio button is not added to the list, SWT will 
             * see it as part of the correct tab order.
             */
            m_group.setTabList(new Control[] {
                m_executionEnvironmentList, m_specificJreList,
                m_preferencesLink
            });
        }
        
        /**
         * Creates the group
         * @param container
         *              The parent container
         */
        private void createGroup(Composite container) {
            m_group = new Group(container, SWT.NONE);
            m_group.setText(Messages.PageOne_ExecutionEnvironmentLbl);
            m_group.setLayout(new FormLayout());
            FormData fdGroup = new FormData();
            fdGroup.top = 
                    new FormAttachment(m_nameGroup.getTextField(), 18);
            fdGroup.left = new FormAttachment(0, 10);
            fdGroup.right = 
                    new FormAttachment(m_nameGroup.getTextField(), 
                            0, SWT.RIGHT);
            m_group.setLayoutData(fdGroup);
        }
        
        /**
         * Creates the execution environment list
         * @param parent
         *          The parent container
         */
        private void createExecutionEnvironmentList(Composite parent) {
            m_executionEnvironmentList = new Combo(parent,
                    SWT.READ_ONLY);
            m_fdExecutionEnvironmentList = new FormData();
            m_fdExecutionEnvironmentList.right = new FormAttachment(100, -13);
            m_fdExecutionEnvironmentList.top = new FormAttachment(0, 4);
            m_executionEnvironmentList
            .setLayoutData(m_fdExecutionEnvironmentList);
            m_executionEnvironmentList.addSelectionListener(
                    new SelectionListener() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            if (m_executionEnvironmentList.getText()
                                    .matches(m_recommendedJre)) {
                                setInfoSymbolWarning(m_infoSymbol, false);
                            } else {
                                setInfoSymbolWarning(m_infoSymbol, true);
                            }
                            m_storage.setExecutionEnvironment(
                                    m_executionEnvironmentList.getText());
                        }
                        @Override
                        public void widgetDefaultSelected(SelectionEvent e) {
                            // Not needed
                        }
                    });
        }
        
        /**
         * Creates the execution environment radio button and its listener
         * @param parent
         *          The parent controller
         */
        private void createExecutionEnvironmentButton(Composite parent) {
            m_executionEnvironmentButton = new Button(parent,
                    SWT.RADIO);
            m_fdExecutionEnvironmentList.left = 
                    new FormAttachment(m_executionEnvironmentButton, 17);
            m_executionEnvironmentButton.setSelection(true);
            FormData fdBtnUseExecutionEnvironment = new FormData();
            fdBtnUseExecutionEnvironment.top = new FormAttachment(0, 7);
            fdBtnUseExecutionEnvironment.left = new FormAttachment(0, 10);
            m_executionEnvironmentButton
                .setLayoutData(fdBtnUseExecutionEnvironment);
            m_executionEnvironmentButton
                .setText(Messages.PageOne_UseExecutionEnvironmentJreLbl);
            m_executionEnvironmentButton
                .addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        m_executionEnvironmentList.setEnabled(true);
                        m_specificJreList.setEnabled(false);
                        setInfoSymbolWarning(m_infoSymbol, false);
                    }
                });
        }
        
        /**
         * Creates the JRE info symbol
         * @param parent
         *          The parent controller
         */
        private void createInfoSymbol(Control parent) {
            m_infoSymbol = Tools.createInfo(parent, 
                    Messages.PageOne_ExecutionEnvironmentWarningMsg, -3, -2);
        }
        
        /**
         * Creates the specific JRE radio button
         * @param parent
         *          The parent controller
         */
        private void createSpecificJreButton(Composite parent) {
            m_specificJreButton = new Button(parent, SWT.RADIO);
            FormData fdSpecificJreButton = new FormData();
            fdSpecificJreButton.top = new FormAttachment(0, 37);
            fdSpecificJreButton.left = new FormAttachment(0, 10);
            m_specificJreButton.setLayoutData(fdSpecificJreButton);
            m_specificJreButton.setText(Messages
                    .PageOne_UseProjectSpecificJreLbl);
            m_specificJreButton.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    m_executionEnvironmentList.setEnabled(false);
                    m_specificJreList.setEnabled(true);
                    setInfoSymbolWarning(m_infoSymbol, true);
                }

                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // Not needed
                }
            });
        }
        
        /**
         * Creates the specific JRE list
         * @param parent
         *          The parent controller
         */
        private void createSpecificJreList(Composite parent) {
            m_specificJreList = new Combo(parent, SWT.READ_ONLY);
            m_specificJreList.setEnabled(false);
            FormData fdSpecificJreList = new FormData();
            fdSpecificJreList.right = 
                    new FormAttachment(m_executionEnvironmentList, 
                            0, SWT.RIGHT);
            fdSpecificJreList.left = 
                    new FormAttachment(m_executionEnvironmentList, 0, SWT.LEFT);
            fdSpecificJreList.top = 
                    new FormAttachment(m_executionEnvironmentList, 6);
            m_specificJreList.setLayoutData(fdSpecificJreList);
            m_specificJreList.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    m_storage
                        .setExecutionEnvironment(m_specificJreList.getText());
                }
            });
        }
        
        /**
         * Creates the default JRE radio button
         * @param parent
         *          The parent container
         */
        private void createDefaultJreButton(Composite parent) {
            m_defaultJreButton = new Button(parent, SWT.RADIO);
            
            FormData fdDefaultJreButton = new FormData();
            fdDefaultJreButton.top = 
                    new FormAttachment(m_specificJreButton, 11);
            fdDefaultJreButton.left = new FormAttachment(0, 10);
            m_defaultJreButton.setLayoutData(fdDefaultJreButton);
            m_defaultJreButton.setText(Messages.PageOne_UseDefaultJreLbl);
            m_defaultJreButton.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    m_executionEnvironmentList.setEnabled(false);
                    m_specificJreList.setEnabled(false);
                    setInfoSymbolWarning(m_infoSymbol, true);
                    
                    String defaultEnv = JavaRuntime.getDefaultVMInstall()
                            .getName();
                    m_storage.setExecutionEnvironment(
                            defaultEnv);
                }
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // Not needed
                }
            });
        }
        
        /**
         * Creates the JRE preferences link
         * @param parent
         *          The parent container
         */
        private void createPreferencesLink(Composite parent) {
            m_preferencesLink = new Link(parent, SWT.NONE);
            FormData fdPreferencesLink = new FormData();
            fdPreferencesLink.right = new FormAttachment(100, -10);
            fdPreferencesLink.bottom = new FormAttachment(m_defaultJreButton,
                    0, SWT.BOTTOM);
            m_preferencesLink.setLayoutData(fdPreferencesLink);
            m_preferencesLink.setText(Messages.PageOne_ConfigureJresLinkLbl);
            m_preferencesLink.addSelectionListener(new SelectionListener() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    PreferencesUtil.createPreferenceDialogOn(null,
                        "org.eclipse.jdt.debug.ui.preferences.VMPreferencePage", //$NON-NLS-1$
                        m_displayedIds, null).open();
                    initializeData();
                }
                @Override
                public void widgetDefaultSelected(SelectionEvent e) {
                    // Not needed
                }
            });
        }
        
        /**
         * Sets the JRE info symbol to either a help or a warning symbol.
         * @param warning
         *              Sets a warning symbol if true and a help symbol if false.
         * @param infoSymbol
         *              The symbol that should be changed
         */
        private void setInfoSymbolWarning(Object infoSymbol, 
                boolean warning) {
            
            if (warning) {
                if (infoSymbol instanceof CLabel) {
                    ((CLabel) infoSymbol).setImage(FieldDecorationRegistry
                            .getDefault()
                            .getFieldDecoration(FieldDecorationRegistry
                                    .DEC_WARNING)
                            .getImage());
                } else if (infoSymbol instanceof ControlDecoration) {
                    ((ControlDecoration) infoSymbol)
                        .setImage(FieldDecorationRegistry
                            .getDefault()
                            .getFieldDecoration(FieldDecorationRegistry
                                    .DEC_WARNING)
                            .getImage());
                }
            } else {
                if (infoSymbol instanceof CLabel) {
                    ((CLabel) infoSymbol)
                        .setImage(FieldDecorationRegistry
                            .getDefault()
                            .getFieldDecoration(FieldDecorationRegistry
                                    .DEC_INFORMATION)
                            .getImage());
                } else if (infoSymbol instanceof ControlDecoration) {
                    ((ControlDecoration) infoSymbol)
                        .setImage(FieldDecorationRegistry
                            .getDefault()
                            .getFieldDecoration(FieldDecorationRegistry
                                    .DEC_INFORMATION)
                            .getImage());
                }
            }
        }
        
        /**
         * Initializes the JRE group items.
         */
        private void initializeData() {
            m_executionEnvironmentList.removeAll();
            IExecutionEnvironment[] environments =
                    JavaRuntime.getExecutionEnvironmentsManager()
                    .getExecutionEnvironments();
            Arrays.stream(environments)
                  .forEach(new Consumer<IExecutionEnvironment>() {
                      @Override
                      public void accept(IExecutionEnvironment environment) { 
                          m_executionEnvironmentList.add(environment.getId());
                      }
                  });
            
            selectExecutionEnvironment();
            
            m_specificJreList.removeAll();
            IVMInstallType[] jres = JavaRuntime.getVMInstallTypes();
            Arrays.stream(jres).forEach(new Consumer<IVMInstallType>() {
                @Override
                public void accept(IVMInstallType jre) {
                    Arrays.stream(jre.getVMInstalls())
                            .forEach(new Consumer<IVMInstall>() {
                                @Override
                                public void accept(IVMInstall inst) {
                                    m_specificJreList
                                            .add(inst.getName());
                                }
                            });
                }
            });
            m_specificJreList.select(0);
            
            m_defaultJreButton.setText(Messages
                    .PageOne_UseDefaultJreCurrentlyLbl 
                + JavaRuntime.getDefaultVMInstall().getName() + "')"); //$NON-NLS-1$
        }
        
        /**
         * Selects a recommended JRE if present and otherwise the
         * first JRE of the list.
         * The latest JRE will most likely be selected but this is not
         * guaranteed.
         */
        private void selectExecutionEnvironment() {
            List<String> execEnvironments = 
                    Arrays.asList(m_executionEnvironmentList.getItems());
            List<Object> recommendedEnvironments =
                    execEnvironments.parallelStream()
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String env) {
                                return env.matches(m_recommendedJre);
                            }
                        })
                        .collect(Collectors.toList());
            if (recommendedEnvironments.size() > 0) {
                m_executionEnvironmentList.select(
                        execEnvironments.indexOf(
                                recommendedEnvironments.get(
                                        recommendedEnvironments.size() - 1
                                )
                        )
                );
            } else {
                m_executionEnvironmentList.select(0);
            }
            m_storage
                .setExecutionEnvironment(m_executionEnvironmentList.getText());
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
