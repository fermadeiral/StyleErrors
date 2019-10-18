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
package org.eclipse.jubula.client.ui.rcp.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.businessprocess.ProjectNameBP;
import org.eclipse.jubula.client.core.events.DataChangedEvent;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.DataState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.ProjectState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.UpdateState;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.IProjectPropertiesPO;
import org.eclipse.jubula.client.core.model.IReusedProjectPO;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.core.persistence.EditSupport;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.NodePM;
import org.eclipse.jubula.client.core.persistence.PMException;
import org.eclipse.jubula.client.core.persistence.ProjectPM;
import org.eclipse.jubula.client.core.utils.TrackingUnit;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.CompletenessBP;
import org.eclipse.jubula.client.ui.rcp.controllers.PMExceptionHandler;
import org.eclipse.jubula.client.ui.rcp.factory.ControlFactory;
import org.eclipse.jubula.client.ui.rcp.handlers.project.RefreshProjectHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.rcp.search.query.DeprecatedModulesQuery;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedIntText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedProjectNameText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedSignatureText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.exception.ProjectDeletedException;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This is the class for the test data property page of a project.
 *
 * @author BREDEX GmbH
 * @created 08.02.2005
 */
public class ProjectGeneralPropertyPage extends AbstractProjectPropertyPage {
    /**
     * @author BREDEX GmbH
     * @created Aug 21, 2007
     */
    public interface IOkListener {
        /**
         * The OK button has been pressed.
         */
        public void okPressed() throws PMException;
    }

    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_3 = 3;

    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(ProjectGeneralPropertyPage.class);
    
    /** the m_text field for the project name */
    private CheckedText m_projectNameTextField;
    /** the m_isReusable checkbox for if the project is reusable */
    private Button m_isReusableCheckbox;
    /** the m_isProtected checkbox for if the project is protected */
    private Button m_isProtectedCheckbox;
    /** the StateController */
    private final WidgetModifyListener m_modifyListener = 
        new WidgetModifyListener();
    /** the StateController */
    private final ToolkitComboSelectionListener m_toolkitComboListener = 
        new ToolkitComboSelectionListener();
    /** the Combo to select the toolkit */
    private DirectCombo<String> m_projectToolkitCombo;
    
    /** the StateController for the markup combo */
    private final MarkupComboSelectionListener m_markupComboListener = 
        new MarkupComboSelectionListener();
    /** the combo to select the MarkupLanguate */
    private DirectCombo<String> m_projectMarkupCombo;
    /** the new project name */
    private String m_newProjectName;
    
    /**  Checkbox to decide if testresults should be deleted after specified days */
    private Button m_cleanTestresults = null;    
    /**  textfield to specify days after which testresults should be deleted after from database */
    private CheckedIntText m_cleanResultDays = null; 

    /**  Checkbox to decide if certain changes to the project should be tracked */
    private Button m_isTrackingActivatedButton = null;
    /**  group to specify how long changes should be stored */
    private Group m_trackChangesTimespanSelection = null;
    /**  textfield to specify how long tracked changes should be stored */
    private CheckedIntText m_trackChangesSpan = null;
    /**  textfield to specify what should be stored to identify who made a change */
    private Composite m_trackChangesUnitSelection = null;
    /**  group to specify what should be stored to identify who made a change */
    private Group m_trackChangesSignatureSelection = null;
    /**  textfield to specify what should be stored to identify who made a change */
    private CheckedSignatureText m_trackChangesSignature = null;
    /**  button to delete all tracked changes */
    private Button m_deleteChanges = null;
    
    /** set of listeners to be informed when ok has been pressed */
    private Set<IOkListener> m_okListenerList = new HashSet<IOkListener>();
    /**
     * the projects description text field
     */
    private Text m_projectDescriptionTextField;
    
    /**
     * the original / unmodified project properties
     */
    private IProjectPropertiesPO m_origProjectProps;
    /**
     * the old project name
     */
    private String m_oldProjectName;
    
    /**
     * @param es
     *            the editSupport
     */
    public ProjectGeneralPropertyPage(EditSupport es) {
        super(es);
        m_oldProjectName = getProject().getName();
        m_origProjectProps = ((IProjectPropertiesPO) es.getOriginal());
    }

    /**
     * {@inheritDoc}
     */
    protected Control createContents(Composite parent) {
        Composite composite = createComposite(parent, NUM_COLUMNS_1,
            GridData.FILL, false);
        Composite projectNameComposite = createComposite(composite,
            NUM_COLUMNS_2, GridData.FILL, false);
        noDefaultAndApplyButton();       

        createEmptyLabel(projectNameComposite);
        createEmptyLabel(projectNameComposite);
        
        createProjectNameField(projectNameComposite);
        createProjectDescrField(projectNameComposite);
        createProjectVersionInfo(projectNameComposite);
        createProjectGuidInfo(projectNameComposite);
        
        createEmptyLabel(projectNameComposite);
        separator(projectNameComposite, NUM_COLUMNS_2); 
        createEmptyLabel(projectNameComposite);

        createAutToolKit(projectNameComposite);
        createMarkupLanguage(projectNameComposite);
        separator(projectNameComposite, NUM_COLUMNS_2);
        createEmptyLabel(projectNameComposite);
        
        createIsReusable(projectNameComposite);
        createIsProtected(projectNameComposite);
        
        separator(projectNameComposite, NUM_COLUMNS_2);
        createEmptyLabel(projectNameComposite);
        
        createCleanTestResults(projectNameComposite);
        
        separator(projectNameComposite, NUM_COLUMNS_2);
        createTrackChangesEnablement(projectNameComposite);
        Composite trackChangesProperties = createComposite(composite,
                NUM_COLUMNS_3, GridData.FILL, false);
        createTrackChangesTimespanSelection(trackChangesProperties);
        createTrackChangesSignatureSelection(trackChangesProperties);
        createTrackChangesDeleteDataButton(trackChangesProperties);
        
        Composite innerComposite = new Composite(composite, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_1;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        innerComposite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalSpan = NUM_COLUMNS_2;
        compositeData.horizontalAlignment = GridData.FILL;
        compositeData.grabExcessHorizontalSpace = true;
        innerComposite.setLayoutData(compositeData);

        addListener();
        Plugin.getHelpSystem().setHelp(parent,
            ContextHelpIds.PROJECT_PROPERTY_PAGE);
        return composite;
    }
    
    /**
     * @param parent the parent composite
     */
    private void createProjectVersionInfo(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, 3, 
            GridData.FILL, true);
        
        createLabel(leftComposite, 
            Messages.ProjectPropertyPageProjectVersion);
        
        Text versionText = new Text(rightComposite, SWT.WRAP);
        GridData labelGrid = new GridData(GridData.FILL, GridData.CENTER, 
                true, false, 1, 1);
        labelGrid.widthHint = 124;
        versionText.setLayoutData(labelGrid);
        versionText.setText(getProject().getVersionString());
        versionText.setEditable(false);
        versionText.setBackground(rightComposite.getBackground());
    }

    /**
     * @param parent the parent composite
     */
    private void createProjectGuidInfo(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, 3, 
            GridData.FILL, true);
        
        ControlDecorator.createInfo(createLabel(leftComposite, 
            Messages.ProjectPropertyPageProjectGuid), 
            I18n.getString("ControlDecorator.ProjectPropertiesGUID"), false); //$NON-NLS-1$
        
        Text guidText = new Text(rightComposite, SWT.WRAP);
        GridData labelGrid = new GridData(GridData.FILL, GridData.CENTER, 
                true, false, 1, 1);
        labelGrid.widthHint = 124;
        guidText.setLayoutData(labelGrid);
        guidText.setText(getProject().getGuid());
        guidText.setEditable(false);
        guidText.setBackground(rightComposite.getBackground());
    }

    /**
     * @param parent
     *            the parent composite
     */
    private void createIsReusable(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_2,
                GridData.FILL, true);
        ControlDecorator.createInfo(createLabel(leftComposite, 
                Messages.ProjectPropertyPageIsReusable),
                I18n.getString("ControlDecorator.NewProjectIsReusable"), false); //$NON-NLS-1$
        m_isReusableCheckbox = new Button(rightComposite, SWT.CHECK);

        m_isReusableCheckbox.setSelection(m_origProjectProps.getIsReusable());
        m_isReusableCheckbox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                boolean isReusable = m_isReusableCheckbox.getSelection();
                if (isReusable) {
                    m_isProtectedCheckbox.setSelection(true);
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });
    }
    
    /**
     * @param parent
     *            the parent composite
     */
    private void createIsProtected(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_2,
                GridData.FILL, true);
        ControlDecorator.createInfo(createLabel(leftComposite, 
                Messages.ProjectPropertyPageIsProtected),
                I18n.getString("ControlDecorator.NewProjectIsProtected"), //$NON-NLS-1$
                false);
        m_isProtectedCheckbox = new Button(rightComposite, SWT.CHECK);

        m_isProtectedCheckbox.setSelection(m_origProjectProps.getIsProtected());

    }

    /**
     * Creates the textfield for the project name.
     * 
     * @param parent
     *            The parent composite.
     */
    private void createProjectNameField(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);
        createLabel(leftComposite, Messages.ProjectPropertyPageProjectName);
        m_projectNameTextField = new CheckedProjectNameText(rightComposite, 
            SWT.BORDER);
        m_projectNameTextField.setText(getProject().getName());
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        LayoutUtil.addToolTipAndMaxWidth(textGridData, m_projectNameTextField);
        m_projectNameTextField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_projectNameTextField);
    }
    
    /**
     * Creates the textfield for the project description.
     * 
     * @param parent
     *            The parent composite.
     */
    private void createProjectDescrField(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);
        createLabel(leftComposite, Messages.ProjectPropertyPageProjectDescr);
        m_projectDescriptionTextField = new Text(rightComposite, SWT.BORDER);
        m_projectDescriptionTextField.setText(StringUtils
                .defaultString(getProject().getComment()));
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        LayoutUtil.addToolTipAndMaxWidth(
                textGridData, m_projectDescriptionTextField);
        m_projectDescriptionTextField.setLayoutData(textGridData);
        LayoutUtil.setMaxChar(m_projectDescriptionTextField,
                IPersistentObject.MAX_STRING_LENGTH);   
    }
    
    /**
     * @param parent the parent composite
     */
    private void createMarkupLanguage(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1,
                GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_2,
                GridData.FILL, true);
        Label label = createLabel(leftComposite,
                Messages.ProjectPropertyPageMarkupLanguageLabel);
        ControlDecorator.createInfo(label,
                Messages.ProjectPropertyPageMarkupLanguageInfo, false);
        m_projectMarkupCombo = ControlFactory
                .createProjectMarkupLanguageCombo(rightComposite);

        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        m_projectToolkitCombo.setLayoutData(textGridData);
    }
    /**
     * @param parent the parent composite
     */
    private void createAutToolKit(Composite parent) {
        Composite leftComposite = createComposite(parent, NUM_COLUMNS_1, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(parent, NUM_COLUMNS_2, 
            GridData.FILL, true);
        createLabel(leftComposite, Messages.ProjectPropertyPageAutToolKitLabel);
        m_projectToolkitCombo = ControlFactory
                .createProjectToolkitCombo(rightComposite);
        m_projectToolkitCombo.setSelectedObject(getProject().getToolkit());
        GridData textGridData = new GridData();
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.horizontalAlignment = GridData.FILL;
        m_projectToolkitCombo.setLayoutData(textGridData);
    }
    
    /**
     * reflect the enablement of checkbox to the corresponding textfield
     */
    protected void enableCleanResultDaysTextfield() {
        enableSelectionAndEnablementDependent(
                m_cleanTestresults, m_cleanResultDays);
    }
    
    /**
     * @param parent The parent <code>Composite</code>
     */
    private void createCleanTestResults(Composite parent) {
        m_cleanTestresults = new Button(parent, SWT.CHECK);
        m_cleanTestresults.setText(Messages
                .TestResultViewPreferencePageCleanResults);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_cleanTestresults.setLayoutData(gridData);
        int testResultCleanupInterval = getProject()
                .getTestResultCleanupInterval();
        m_cleanTestresults.setSelection(testResultCleanupInterval
                != IProjectPO.NO_CLEANUP);
        m_cleanTestresults.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                enableCleanResultDaysTextfield();
                checkCompleteness();
            }

            public void widgetDefaultSelected(SelectionEvent e) {
                // nothing here
            }
        });
        m_cleanResultDays = new CheckedIntText(
                parent, SWT.BORDER, false, 1, Integer.MAX_VALUE);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        gridData.widthHint = 80;
        m_cleanResultDays.setLayoutData(gridData);
        if (testResultCleanupInterval > 0) {
            m_cleanResultDays
                    .setText(String.valueOf(testResultCleanupInterval));
        }
        m_cleanResultDays.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }
            
        });
        enableCleanResultDaysTextfield();
        ControlDecorator.createInfo(m_cleanResultDays,  
                I18n.getString("TestResultViewPreferencePage.cleanResultsInfo"), //$NON-NLS-1$
                false);
    }
    
    /**
     * @param parent The parent <code>Composite</code>
     */
    private void createTrackChangesEnablement(Composite parent) {
        m_isTrackingActivatedButton = new Button(parent, SWT.CHECK);
        m_isTrackingActivatedButton.setText(Messages
                .PrefPageTrackChanges);
        m_isTrackingActivatedButton.setSelection(
                m_origProjectProps.getIsTrackingActivated());
        GridData gridData = new GridData(SWT.BEGINNING, SWT.BEGINNING, 
                false, false);
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = false;
        m_isTrackingActivatedButton.setLayoutData(gridData);
        ControlDecorator.createInfo(m_isTrackingActivatedButton,
                I18n.getString("TestResultViewPreferencePage.TrackChangesInfo"), //$NON-NLS-1$
                false);
        
        m_isTrackingActivatedButton.addSelectionListener(
                new SelectionListener() {
                    public void widgetSelected(SelectionEvent e) {
                        if (m_isTrackingActivatedButton != null) {
                            UIComponentHelper.setEnabledRecursive(
                                    m_trackChangesTimespanSelection, 
                                    m_isTrackingActivatedButton.getSelection());
                            UIComponentHelper.setEnabledRecursive(
                                    m_trackChangesSignatureSelection, 
                                    m_isTrackingActivatedButton.getSelection());
                            
                            if (!m_isTrackingActivatedButton.getSelection()) {
                                m_trackChangesSpan.setBackground(null);
                            } else {
                                m_trackChangesSpan.validate();
                            }
                        }
                        checkCompleteness();
                    }
        
                    public void widgetDefaultSelected(SelectionEvent e) {
                        // nothing here
                    }
                });
    }
    
    /**
     * @param parent The parent <code>Composite</code>
     */
    private void createTrackChangesTimespanSelection(Composite parent) {
        m_trackChangesTimespanSelection = new Group(parent, SWT.NONE);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        m_trackChangesTimespanSelection.setLayout(new GridLayout());
        m_trackChangesTimespanSelection.setLayoutData(gridData);
        m_trackChangesTimespanSelection.setText(
                Messages.PrefPageTrackChangesTimespanSelectionText);
        m_trackChangesUnitSelection = new Composite(
                m_trackChangesTimespanSelection, SWT.NULL);
        m_trackChangesUnitSelection.setLayout(new GridLayout());
        
        Button daysButton = new Button(m_trackChangesUnitSelection, SWT.RADIO);
        daysButton.setText("Days"); //$NON-NLS-1$
        Button changesButton = new Button(m_trackChangesUnitSelection,
                SWT.RADIO);
        changesButton.setText("Changes"); //$NON-NLS-1$
        SelectionListener listener = new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                checkCompleteness();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                checkCompleteness();
            }
        };
        daysButton.addSelectionListener(listener);
        changesButton.addSelectionListener(listener);
        
        TrackingUnit unit = m_origProjectProps.getTrackChangesUnit();
        if (unit != null) {
            switch (unit) {
                case CHANGES:
                    changesButton.setSelection(true);
                    break;
                case DAYS:
                    daysButton.setSelection(true);
                    break;
                default:
                    break;
            }
        } else {
            daysButton.setSelection(true); // set days as default
        }
        
        m_trackChangesSpan = new CheckedIntText(m_trackChangesTimespanSelection,
                SWT.BORDER, false, 1, Integer.MAX_VALUE);
        gridData = new GridData(SWT.BEGINNING, SWT.NONE, true, true);
        gridData.widthHint = 80;
        m_trackChangesSpan.setLayoutData(gridData);
        ControlDecorator.createInfo(m_trackChangesSpan,  
                I18n.getString(
                        "TestResultViewPreferencePage.cleanResultsTimeunitInfo"), //$NON-NLS-1$
                    false);
        
        Integer span = m_origProjectProps.getTrackChangesSpan();
        if (span != null) {
            m_trackChangesSpan.setText(String.valueOf(span));
        } else {
            // set 10 as default
            m_trackChangesSpan.setText("10"); //$NON-NLS-1$
        }
        m_trackChangesSpan.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }
        });
        if (m_isTrackingActivatedButton != null) {
            UIComponentHelper.setEnabledRecursive(
                    m_trackChangesTimespanSelection, 
                    m_isTrackingActivatedButton.getSelection());
        }
        m_trackChangesSpan.setBackground(null);
    }
    
    /**
     * @param parent The parent <code>Composite</code>
     */
    private void createTrackChangesSignatureSelection(Composite parent) {
        m_trackChangesSignatureSelection = new Group(parent, SWT.NONE);
        m_trackChangesSignatureSelection.setLayout(new GridLayout());
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.horizontalSpan = 2;
        gridData.grabExcessHorizontalSpace = true;
        m_trackChangesSignatureSelection.setLayoutData(gridData);
        m_trackChangesSignatureSelection.setText(
                Messages.PrefPageTrackChangesSignatureSelectionText);
        Label label = new Label(m_trackChangesSignatureSelection, SWT.WRAP);
        label.setText(Messages.PrefPageTrackChangesSignatureDescription);
        final GridData labelData = new GridData();
        labelData.horizontalSpan = 2;
        labelData.grabExcessHorizontalSpace = false;
        labelData.horizontalAlignment = SWT.LEFT;
        labelData.widthHint = 400;
        label.setLayoutData(labelData);
        
        m_trackChangesSignature = new CheckedSignatureText(
                m_trackChangesSignatureSelection, SWT.BORDER);
        gridData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gridData.widthHint = 200;
        m_trackChangesSignature.setLayoutData(gridData);
        m_trackChangesSignature.addKeyListener(new KeyListener() {
            public void keyPressed(KeyEvent e) {
                // nothing
            }
            public void keyReleased(KeyEvent e) {
                checkCompleteness();
            }
        });
        
        String signature = m_origProjectProps.getTrackChangesSignature();
        if (signature != null) {
            m_trackChangesSignature.setText(signature);
        }
        m_trackChangesSignatureSelection.pack();
        if (m_isTrackingActivatedButton != null) {
            UIComponentHelper.setEnabledRecursive(
                    m_trackChangesSignatureSelection, 
                    m_isTrackingActivatedButton.getSelection());
        }
        parent.pack();
    }
    
    /**
     * @param parent The parent <code>Composite</code>
     */
    private void createTrackChangesDeleteDataButton(Composite parent) {
        m_deleteChanges = new Button(parent, SWT.PUSH);
        m_deleteChanges.setText(Messages
                .PrefPageTrackChangesDeleteData);
        GridData gridData = new GridData(SWT.END, SWT.BEGINNING, 
                false, false);
        gridData.horizontalSpan = 1;
        gridData.grabExcessHorizontalSpace = false;
        m_deleteChanges.setLayoutData(gridData);
        
        m_deleteChanges.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                Dialog qDialog = new MessageDialog(getShell(), 
                        Messages.UtilsConfirmation, null, 
                        Messages.PrefPageTrackChangesDeleteDataQuestion, 
                        MessageDialog.QUESTION, 
                        new String[] {Messages.UtilsYes, Messages.UtilsNo}, 0);
                qDialog.setBlockOnOpen(true);
                qDialog.open();
                if (qDialog.getReturnCode() == 0) {
                    // delete all tracked changes
                    try {
                        PlatformUI.getWorkbench().getProgressService().run(
                                true, false, 
                                new DeleteTrackedChangesOperation());
                    } catch (InvocationTargetException ite) {
                        // nothing
                    } catch (InterruptedException ie) {
                        // nothing
                    }
                }
            }
        });
    }
    
    /**
     * Checks if Preference Page is complete and valid
     */
    protected void checkCompleteness() {
        if (m_cleanResultDays.isEnabled() 
                && m_cleanResultDays.getValue() <= 0) {
            setErrorMessage(Messages
                    .TestResultViewPreferencePageCleanResultDaysEmpty);
            setValid(false);
            return;
        }
        if (m_trackChangesTimespanSelection.isEnabled()) {
            boolean unitSelected = false;
            for (Control child : m_trackChangesUnitSelection.getChildren()) {
                if (child instanceof Button) {
                    Button bt = (Button) child;
                    if (bt.getSelection()) {
                        unitSelected = true;
                    }
                }
            }
            if (!unitSelected) {
                setErrorMessage(Messages.PrefPageTrackChangesNoUnitSelected);
                setValid(false);
                return;
            }
        }
        if (m_trackChangesTimespanSelection.isEnabled() 
                && m_trackChangesSpan.getValue() <= 0) {
            setErrorMessage(Messages.PrefPageTrackChangesTimespanEmpty);
            setValid(false);
            return;
        }
        final String trackChangesSignature = m_trackChangesSignature.getText();
        if (m_trackChangesTimespanSelection.isEnabled()
            && !StringUtils.isEmpty(trackChangesSignature)
            && EnvironmentUtils
                .getProcessOrSystemProperty(trackChangesSignature) == null) {
            setErrorMessage(Messages.PrefPageTrackChangesSignatureInvalid);
            setValid(false);
            return;
        }
        setErrorMessage(null);
        setValid(true);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean performOk() {
        try {
            Plugin.startLongRunning(Messages
                    .RefreshTSBrowserActionProgressMessage);
            if (!m_oldProjectName.equals(m_newProjectName)) {
                if (ProjectPM.doesProjectNameExist(m_newProjectName)) {

                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.E_PROJECTNAME_ALREADY_EXISTS,
                            new Object[] { m_newProjectName }, null);
                    return false;
                }
            }
            IProjectPO project = getProject();
            storeProperties();
            storeAutoTestResultCleanup();
            if (!m_oldProjectName.equals(m_newProjectName)) {
                ProjectNameBP.getInstance().setName(
                        getEditSupport().getSession(), project.getGuid(),
                        m_newProjectName);
            }
            fireOkPressed();
            Set<IReusedProjectPO> origReused = 
                    ((IProjectPropertiesPO)getEditSupport().getOriginal())
                    .getUsedProjects();
            Set<IReusedProjectPO> newReused = new HashSet<IReusedProjectPO>(
                ((IProjectPropertiesPO)getEditSupport().getWorkVersion())
                    .getUsedProjects());
            boolean needRefresh = !origReused.containsAll(newReused)
                    || !newReused.containsAll(origReused); 
            newReused.removeAll(origReused);
            getEditSupport().saveWorkVersion();
            refreshAutMainList();
            boolean doSearchQuery = false;
            doSearchQuery = handleReusedChanged(newReused, needRefresh);
            DataEventDispatcher ded = DataEventDispatcher.getInstance();
            ded.fireProjectStateChanged(ProjectState.prop_modified);
            
            List<DataChangedEvent> events = 
                    new ArrayList<DataChangedEvent>();
            // FIXME zeb This updates the Test Case Browser. Once we have separate
            //           EditSupports for each property page, then we can use 
            //           "real" ReusedProjectPOs instead of a placeholder.
            events.add(new DataChangedEvent(
                    PoMaker.createReusedProjectPO("1", 1, 1, null, null), //$NON-NLS-1$
                    DataState.ReuseChanged, UpdateState.notInEditor));
            events.add(new DataChangedEvent(GeneralStorage.getInstance()
                    .getProject(), DataState.Renamed, UpdateState.notInEditor));
            ded.fireDataChangedListener(
                    events.toArray(new DataChangedEvent[0]));
            CompletenessBP.getInstance().completeProjectCheck();
            if (doSearchQuery) {
                NewSearchUI.runQueryInBackground(new DeprecatedModulesQuery());
            }
        
        } catch (PMException e) {
            ErrorHandlingUtil.createMessageDialog(e, null, null);
        } catch (ProjectDeletedException e) {
            PMExceptionHandler.handleProjectDeletedException();
        } catch (InterruptedException e) {
            ErrorHandlingUtil.createMessageDialog(
                    new JBException(Messages.UnexpectedError, e,
                            MessageIDs.E_UNEXPECTED_EXCEPTION));
        } catch (InvocationTargetException e) {
            ErrorHandlingUtil.createMessageDialog(
                    new JBException(Messages.UnexpectedError, e,
                            MessageIDs.E_UNEXPECTED_EXCEPTION));
        }
        Plugin.stopLongRunning();
        return true;
    }

    /**
     * this method asks if it should be searched for used deprecated
     * modules {@link DeprecatedModulesQuery} and also refreshes the master
     * session if there was a change in the reused of projects.
     * @param newReused the list of new {@link IReusedProjectPO}
     * @param needRefresh whether a refresh of the project is needed
     * @return boolean which states if the search query should be done
     * @throws InvocationTargetException might occur during refresh
     * @throws InterruptedException might occur during refresh
     */
    private boolean handleReusedChanged(Set<IReusedProjectPO> newReused,
            boolean needRefresh)
            throws InvocationTargetException, InterruptedException {
        boolean doSearchQuery = false;
        if (!newReused.isEmpty()) {
            doSearchQuery = openSearchForDeprecatedDialog();
        }
        if (needRefresh) {
            PlatformUI.getWorkbench().getProgressService().run(false, false,
                    new RefreshProjectHandler.RefreshProjectOperation());
        }
        for (IReusedProjectPO reused : newReused) {
            try {
                IProjectPO reusedProject =
                    ProjectPM.loadReusedProject(reused);
            } catch (JBException e) {
                // Could not refresh Component Name information for
                // reused project. Log the exception.
                log.error(Messages
                        .ErrorWhileRetrievingReusedProjectInformation, e);
            }
        }
        return doSearchQuery;
    }

    /**
     * creates and opens a dialog if a search for the deprecated Modules
     * should be done
     * @return the boolean if the search should be done
     */
    private boolean openSearchForDeprecatedDialog() {
        MessageDialog mdiag = new MessageDialog(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                Messages.ProjectPropertyPageSearchForDeprProjModuleTitle, null,
                Messages.ProjectPropertyPageSearchForDeprProjModuleMsg,
                MessageDialog.QUESTION,
                new String[] { IDialogConstants.YES_LABEL,
                               IDialogConstants.NO_LABEL },
                0);
        mdiag.create();
        Plugin.getHelpSystem().setHelp(
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
                ContextHelpIds.SEARCH_FOR_DEPRECATED_MODULES_DIALOG);
        return (mdiag.open() == Window.OK);
    }

    /**
     * store properties into the database
     */
    private void storeProperties() {
        IProjectPO project = getProject();
        if (m_isReusableCheckbox != null) {
            project.setIsReusable(m_isReusableCheckbox.getSelection());
        }
        if (m_isProtectedCheckbox != null) {
            project.setIsProtected(m_isProtectedCheckbox.getSelection());
        }
        if (m_projectDescriptionTextField != null) {
            project.setComment(m_projectDescriptionTextField.getText());
        }
        if (m_isTrackingActivatedButton != null) {
            project.setIsTrackingActivated(
                    m_isTrackingActivatedButton.getSelection());
        }
        if (m_trackChangesSignature != null) {
            project.getProjectProperties().setTrackChangesSignature(
                    m_trackChangesSignature.getText());
        }
        if (m_trackChangesSpan != null 
                && m_trackChangesSpan.getText() != null
                && !m_trackChangesSpan.getText().equals("")) { //$NON-NLS-1$
            project.getProjectProperties().setTrackChangesSpan(
                    Integer.valueOf(m_trackChangesSpan.getText()));
        }
        if (m_trackChangesUnitSelection != null) {
            TrackingUnit unit = null;
            for (Control child : m_trackChangesUnitSelection.getChildren()) {
                if (child instanceof Button) {
                    Button bt = (Button) child;
                    if (bt.getSelection()) {
                        String btText = bt.getText();
                        if (btText.equals("Days")) { //$NON-NLS-1$
                            unit = TrackingUnit.DAYS;
                        } else if (btText.equals("Changes")) { //$NON-NLS-1$
                            unit = TrackingUnit.CHANGES;
                        }
                    }
                }
            }
            if (unit != null) {
                project.getProjectProperties().setTrackChangesUnit(unit);
            }
        }
    }

    /**
     * store preferences made for auto test result cleanup
     */
    private void storeAutoTestResultCleanup() {
        if (m_cleanResultDays != null) {
            if (m_cleanTestresults != null) {
                boolean autoClean = m_cleanTestresults.getSelection();
                if (autoClean) {
                    getProject().setTestResultCleanupInterval(
                            Integer.valueOf(m_cleanResultDays.getText()));
                } else {
                    getProject().setTestResultCleanupInterval(
                            IProjectPO.NO_CLEANUP);
                }
            }
        }
    }

    /**
     * Notify listeners that OK was pressed.
     */
    private void fireOkPressed() throws PMException {
        for (IOkListener listener : m_okListenerList) {
            listener.okPressed();
        }
    }

    /**
     * Refreshes the AutMainList of the Project.
     */
    private void refreshAutMainList() throws ProjectDeletedException {
        try {
            GeneralStorage.getInstance().getMasterSession().refresh(
                GeneralStorage.getInstance().getProject().getAutCont());
        } catch (EntityNotFoundException enfe) {
            // Occurs if any Object Mapping information has been deleted while
            // the Project Properties were being edited.
            // Refresh the entire master session to ensure that AUT settings
            // and Object Mappings are in sync
            GeneralStorage.getInstance().reloadMasterSession(
                    new NullProgressMonitor());
        }
    }

    /**
     * Adds necessary listeners.
     */
    private void addListener() {
        m_projectNameTextField.addModifyListener(m_modifyListener);
        m_projectToolkitCombo.addSelectionListener(m_toolkitComboListener);
        m_projectMarkupCombo.addSelectionListener(m_markupComboListener);
    }
    
    /** 
     * The action of the project name field.
     * @param isProjectNameVerified True, if the project name was verified.
     * @return false, if the project name field contents an error:
     * the project name starts or end with a blank, or the field is empty
     */
    boolean modifyProjectNameFieldAction(
        boolean isProjectNameVerified) {
        
        boolean isCorrect = true;
        String projectName = m_projectNameTextField.getText();
        int projectNameLength = projectName.length();
        super.getShell().setText(Messages.ProjectPropertyPageShellTitle 
                + projectName);
        if ((projectNameLength == 0) || (projectName
                .startsWith(StringConstants.SPACE))
            || (projectName.charAt(projectNameLength - 1) == ' ')) {
            
            isCorrect = false;
        }
        if (isCorrect) {
            setErrorMessage(null);
            setMessage(Messages.PropertiesActionPage1, NONE);
            setValid(true);
            if (isProjectNameVerified) {
                m_newProjectName = projectName;
            }
            if (ProjectPM.doesProjectNameExist(projectName)
                && !m_oldProjectName.equals(projectName)) {
                    
                setErrorMessage(Messages
                        .ProjectSettingWizardPageDoubleProjectName); 
                isCorrect = false;
                setValid(false);
            }
        } else {
            if (projectNameLength == 0) {
                setErrorMessage(Messages.ProjectWizardEmptyProject);
                setValid(false);
            } else {
                setErrorMessage(Messages.ProjectWizardNotValidProject);
                setValid(false);
            }
        }
        return isCorrect;
    }
    
    
    /**
     * This private inner class contains a new ModifyListener.
     * @author BREDEX GmbH
     * @created 11.07.2005
     */
    @SuppressWarnings("synthetic-access")
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            Object o = e.getSource();
            if (o.equals(m_projectNameTextField)) {
                modifyProjectNameFieldAction(true);
                return;
            }
        }       
    }
    /**
     * @author BREDEX GmbH
     */
    private class MarkupComboSelectionListener implements SelectionListener {

        /** {@inheritDoc} */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_projectMarkupCombo)) {
                handleMarkupSelection();
                return;
            }
            Assert.notReached(Messages.EventActivatedUnknownWidget
                    + StringConstants.DOT);
        }
        /**
         * handles the selection of the markupLanguageCombo
         */
        private void handleMarkupSelection() {
            final String newMarkup = m_projectMarkupCombo
                    .getSelectedObject();
            final IProjectPO project = getProject();
            project.setMarkupLanguage(newMarkup);
        }

        /** {@inheritDoc} */
        public void widgetDefaultSelected(SelectionEvent e) {
            Assert.notReached(Messages.EventActivatedUnknownWidget
                    + StringConstants.DOT);
        }
    }
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     * @created 10.02.2005
     */
    @SuppressWarnings("synthetic-access")
    private class ToolkitComboSelectionListener implements SelectionListener {

        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_projectToolkitCombo)) {
                handleAutToolkitSelection();
                return;
            }
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                    + StringConstants.DOT);
        }

        /**
         * Handles the selection of the autToolkitCombo
         */
        private void handleAutToolkitSelection() {
            final String newToolkit = m_projectToolkitCombo
                .getSelectedObject();
            final IProjectPO project = getProject();
            project.setToolkit(newToolkit);
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) {
            Assert.notReached(Messages.EventActivatedUnknownWidget 
                    + StringConstants.DOT);
        }        
    }

    /**
     * @param toAdd The listener to add. 
     */
    public void addOkListener(IOkListener toAdd) {
        m_okListenerList.add(toAdd);
    }

    /**
     * {@inheritDoc}
     */
    public void setVisible(boolean visible) {
        refreshAutToolkitCombo();
        super.setVisible(visible);
    }

    /**
     * Refreshes the m_autToolkitCombo.
     */
    private void refreshAutToolkitCombo() {
        final Composite parent = m_projectToolkitCombo.getParent();
        final DirectCombo<String> tmpCombo = ControlFactory
                .createProjectToolkitCombo(parent);
        m_projectToolkitCombo.setItems(tmpCombo.getValues(), Arrays.asList(
            tmpCombo.getItems()));
        tmpCombo.dispose();
        m_projectToolkitCombo.setSelectedObject(getProject().getToolkit());
    }
    
    
    /**
     * Operation for deleting tracked changes
     * 
     * @author BREDEX GmbH
     */
    private class DeleteTrackedChangesOperation 
                        implements IRunnableWithProgress {
        
        /**
         * {@inheritDoc}
         */
        public void run(IProgressMonitor monitor) {
            final IProjectPO project = 
                    GeneralStorage.getInstance().getProject();
            final Map<INodePO, Boolean> nodeToWasLockedMap;
            try {
                nodeToWasLockedMap = 
                        NodePM.cleanupTrackedChanges(monitor, project);

                List<INodePO> listOfLockedNodes = new ArrayList<>(
                        nodeToWasLockedMap.size());
                for (INodePO node: nodeToWasLockedMap.keySet()) {
                    if (nodeToWasLockedMap.get(node).booleanValue()) {
                        listOfLockedNodes.add(node);
                    }
                }
                
                Display d = Plugin.getDisplay();
                if (d != null && !d.isDisposed()) {
                    fireDataChangedListeners(d, nodeToWasLockedMap);
                }
                
                if (!listOfLockedNodes.isEmpty()) {
                    Object[] details = listOfLockedNodes.toArray();
                    String[] namesOfLockedNodes;
                    final int maxNumberOfLockedNodesToDisplay = 10;
                    
                    if (details.length <= maxNumberOfLockedNodesToDisplay) {
                        // not too many locked nodes to display them all
                        namesOfLockedNodes = new String[details.length];
                        for (int i = 0; i < namesOfLockedNodes.length; i++) {
                            namesOfLockedNodes[i] = 
                                    ((INodePO)details[i]).getName();
                        }
                        ErrorHandlingUtil.createMessageDialog(
                                MessageIDs.
                                    I_COULD_NOT_DELETE_ALL_TRACKED_CHANGES,
                                null, namesOfLockedNodes);
                    } else {
                        // too many locked nodes to display them all
                        namesOfLockedNodes = 
                                new String[maxNumberOfLockedNodesToDisplay];
                        for (int i = 0; i < namesOfLockedNodes.length; i++) {
                            namesOfLockedNodes[i] = 
                                    ((INodePO)details[i]).getName();
                        }
                        Object[] params = new Object[2];
                        params[0] = maxNumberOfLockedNodesToDisplay;
                        params[1] = details.length;
                        ErrorHandlingUtil.createMessageDialog(
                                MessageIDs.
                                    I_COULD_NOT_DELETE_ALL_TRACKED_CHANGES_MANY,
                                params, namesOfLockedNodes);
                    }
                }
            } catch (PMException e) {
                ErrorHandlingUtil.createMessageDialog(e);
            } catch (ProjectDeletedException e) {
                ErrorHandlingUtil.createMessageDialog(e);
            }
        }
    }
    
    /**
     * Fires data state listeners for those nodes which were not locked
     * @param d the active Display
     * @param nodeToWasLockedMap the map indicating which nodes were locked
     */
    private void fireDataChangedListeners(Display d,
            final Map<INodePO, Boolean> nodeToWasLockedMap) {
        d.syncExec(new Runnable() {
            public void run() {
                for (INodePO node : nodeToWasLockedMap.keySet()) {
                    if (!nodeToWasLockedMap.get(node).
                            booleanValue()) {
                        DataEventDispatcher.getInstance().
                            fireDataChangedListener(node,
                                DataState.StructureModified,
                                UpdateState.all);
                    }
                }
            }
        });
    }
}