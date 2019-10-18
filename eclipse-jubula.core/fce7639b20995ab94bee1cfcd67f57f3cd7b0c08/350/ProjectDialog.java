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
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.layout.RowLayoutFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ProjectUIBP;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.DirectCombo;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Dialog containing all information necessary to uniquely identify a project.
 *
 * @author BREDEX GmbH
 * @created Jun 21, 2007
 */
public class ProjectDialog extends TitleAreaDialog {
    /** standard logging */
    private static Logger log = LoggerFactory.getLogger(TitleAreaDialog.class);

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
    
    /** The message m_text */
    private String m_message;
    
    /** the name textfield */
    private DirectCombo<String> m_nameComboBox;

    /** the version textfield */
    private DirectCombo<String> m_versionComboBox;

    /**
     * Mapping from project GUIDs to project versions 
     */
    private Map<String, List<String>> m_guidToVersionMap;

    /**
     * Mapping from project data to project 
     */
    private Map<ProjectData, IProjectPO> m_projectMap;
    
    /** List of project GUIDs */
    private List<String> m_guidList;
    
    /** List of project names */
    private List<String> m_nameList;
    
    /** Current list of versions */
    private List<String> m_versionList;
    
    /** result of dialog, null if nothing was selected */
    private ProjectData m_selection;
    
    /**
     * <code>m_title</code> title
     */
    private String m_title;

    /**
     * <code>m_image</code> associated image
     */
    private Image m_image;

    /**
     * <code>m_shellTitle</code> the shell title
     */
    private String m_shellTitle;
    
    /**
     * check box to select a default project and project version
     */
    private Button m_defaultProject;
    
    /**
     * <code>m_isDeleteOperation</code> true if dialog is "delete project"-dialog
     */
    private boolean m_isDeleteOperation = false;
    
    /**
     * check box to define if test result summary should not be deleted, when project is deleted
     */
    private Button m_keepTestresultSummaryButton;
    
    /**
     * true if test result summary should not be deleted, when project is deleted
     */
    private boolean m_keepTestresultSummary = false;

    /**
     * Value class to hold name and version info for a project.
     * @author BREDEX GmbH
     * @created Jun 21, 2007
     */
    public static class ProjectData {
        /** project guid */
        private String m_guid;
        /** project version */
        private String m_versionString;

        /**
         * Constructor
         * @param guid project's GUID
         * @param versionString project version string
         */
        public ProjectData(String guid, String versionString) {
            m_guid = guid;
            m_versionString = versionString;
        }

        /**
         * 
         * @return version string
         */
        public String getVersionString() {
            return m_versionString;
        }
        
        /**
         * sets the project version
         * @param version the project version
         */
        public void setVersionString(String version) {
            m_versionString = version;
        }

        /**
         * 
         * @return GUID
         */
        public String getGUID() {
            return m_guid;
        }
        
        /**
         * sets the project name
         * @param guid the projects GUID
         */
        public void setGUID(String guid) {
            m_guid = guid;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof ProjectData)) {
                return false;
            }
            
            ProjectData otherData = (ProjectData)obj;
            return new EqualsBuilder().append(getGUID(), otherData.getGUID())
                .append(getVersionString(), otherData.getVersionString())
                .isEquals();
        }

        /**
         * 
         * {@inheritDoc}
         */
        public int hashCode() {
            return new HashCodeBuilder().append(getGUID())
                .append(getVersionString())
                .toHashCode();
        }
    }

    /**
     * @param parentShell The parent shell.
     * @param projectList list of available projects
     * @param message message m_text
     * @param title title
     * @param image name of image
     * @param shellTitle shell title
     * @param isDeleteAction true if dialog is "delete project"-dialog
     */
    public ProjectDialog(
        Shell parentShell, List<IProjectPO> projectList, String message,
        String title, Image image, String shellTitle, boolean isDeleteAction) {
        
        super(parentShell);
        
        m_guidToVersionMap = 
            new HashMap<String, List<String>>();
        m_projectMap = new HashMap<ProjectData, IProjectPO>();
        m_guidList = new ArrayList<String>();
        m_nameList = new ArrayList<String>();
        m_versionList = new ArrayList<String>();
        for (IProjectPO proj : projectList) {
            String projGUID = proj.getGuid();
            String projVersion = proj.getVersionString();
            if ((projGUID != null) && (projVersion != null)) { // protect
                                                               // against racing
                                                               // conditions in
                                                               // DB
                if (!m_guidToVersionMap.containsKey(projGUID)) {
                    m_nameList.add(proj.getName());
                    m_guidList.add(projGUID);
                    m_guidToVersionMap.put(projGUID, new ArrayList<String>());
                }
                m_guidToVersionMap.get(projGUID).add(projVersion);
                m_projectMap.put(new ProjectData(projGUID, projVersion), proj);
            } else  {
                log.warn(Messages.ProjectWithGUID + StringConstants.SPACE 
                        + proj.getGuid() + StringConstants.SPACE 
                        + Messages.HasNoName + StringConstants.DOT);
            }
        }

        m_message = message;
        m_title = title;
        m_image = image;
        m_shellTitle = shellTitle;
        m_isDeleteOperation = isDeleteAction;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(m_title); 
        setTitleImage(m_image);
        getShell().setText(m_shellTitle); 
        // new Composite as container
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

        createComboBoxes(area);
        createDefaultProjectCheckbox(area);
        
        if (m_isDeleteOperation) {
            createDeleteTestresultsCheckbox(area);
        }

        LayoutUtil.createSeparator(parent);
        
        return area;
    }
    
    /**
     * @param composite the parent composite
     */
    private void createDeleteTestresultsCheckbox(Composite composite) {
        m_keepTestresultSummaryButton = new Button(composite, SWT.CHECK);
        m_keepTestresultSummaryButton.setText(
                Messages.DeleteProjectActionKeepTestresultSummaryCheckbox);
        m_keepTestresultSummaryButton.setSelection(false);
        GridData data = new GridData();
        data.horizontalSpan = HORIZONTAL_SPAN;
        m_keepTestresultSummaryButton.setLayoutData(data);
        ControlDecorator.createInfo(m_keepTestresultSummaryButton,  
                I18n.getString("ControlDecorator.KeepTestresultSummary"), false); //$NON-NLS-1$
    }

    /**
     * @param parent
     *            The parent composite.
     */
    private void createComboBoxes(Composite parent) {
        createEmptyLabel(parent);
        new Label(parent, SWT.NONE).setText(Messages.OpenProjectActionLabel);
        m_nameComboBox = new DirectCombo<String>(parent, SWT.SINGLE | SWT.BORDER
                | SWT.READ_ONLY, m_guidList, m_nameList, false, true);
        GridData gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_nameComboBox);
        m_nameComboBox.setLayoutData(gridData);
        m_nameComboBox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                m_versionList = m_guidToVersionMap.get(
                    m_nameComboBox.getSelectedObject());
                m_versionComboBox.setItems(m_versionList, m_versionList);
                m_versionComboBox.select(m_versionComboBox.getItemCount() - 1);
                enableOKButton();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing                
            }           
        });     
        if (m_nameComboBox.getItemCount() > 0) {
            m_nameComboBox.select(0);
        }
        m_versionList = m_guidToVersionMap.get(
            m_nameComboBox.getSelectedObject());
        createEmptyLabel(parent);
        new Label(parent, SWT.NONE).setText(Messages.OpenProjectActionLabel2);
        m_versionComboBox = 
            new DirectCombo<String>(parent, SWT.SINGLE | SWT.BORDER
                | SWT.READ_ONLY, m_versionList, m_versionList, false,
                new Comparator<String>() {

                    public int compare(String s1, String s2) {
                        return s1.compareTo(s2);
                    }
                
                });
        gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = HORIZONTAL_SPAN;
        m_versionComboBox.setLayoutData(gridData);
        m_versionComboBox.select(m_versionComboBox.getItemCount() - 1);
        m_versionComboBox.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                enableOKButton();
            }
            public void widgetDefaultSelected(SelectionEvent e) {
                // do nothing                
            }           
        });     
    }

    /**
     * Creates the check box to select a default project and project version
     * 
     * @param composite the parent composite
     */
    private void createDefaultProjectCheckbox(Composite composite) {
        if (!m_isDeleteOperation) {
            createEmptyLabel(composite);
            new Label(composite, SWT.NONE).setLayoutData(new GridData(
                    GridData.FILL, GridData.CENTER, false, false,
                    HORIZONTAL_SPAN, 1));
            IPreferenceStore prefs = Plugin.getDefault().getPreferenceStore();
            Composite checkLinkComposite = new Composite(composite, SWT.NONE);
            checkLinkComposite.setLayout(RowLayoutFactory.fillDefaults()
                    .spacing(0).create());
            m_defaultProject = new Button(checkLinkComposite, SWT.CHECK);
            m_defaultProject.setSelection(prefs.getBoolean(
                    Constants.PERFORM_AUTO_PROJECT_LOAD_KEY));
            DialogUtils.createLinkToSecureStoragePreferencePage(
                    checkLinkComposite,
                    Messages.OpenProjectDialogDefaultProjectCheckbox);
        }
    }

    /**
     * @param composite
     *            the parent composite to use
     */
    private void createEmptyLabel(Composite composite) {
        new Label(composite, SWT.NONE).setLayoutData(new GridData(
                GridData.FILL, GridData.CENTER, false, false,
                HORIZONTAL_SPAN + 1, 1));
    }
    
    /**
     * enables the OK button
     */
    public void enableOKButton() {
        if (getButton(IDialogConstants.OK_ID) != null) {
            getButton(IDialogConstants.OK_ID).setEnabled(true);
        }
        setMessage(m_message); 
    }

    /**
     * This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        m_selection = new ProjectData(m_nameComboBox.getSelectedObject(),
                m_versionComboBox.getSelectedObject());

        final ProjectUIBP defaultProject = ProjectUIBP.getInstance();
        if (m_isDeleteOperation) {
            m_keepTestresultSummary = m_keepTestresultSummaryButton
                    .getSelection();
        } else if (m_defaultProject.getSelection()) {
            defaultProject.saveMostRecentProjectData(m_selection);
        }
        setReturnCode(OK);
        close();
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
     * @return Returns the selection.
     */
    public ProjectData getSelection() {
        return m_selection;
    }
    
    /**
     * @return Returns true, if test result summary should not be deleted.
     */
    public boolean keepTestresultSummary() {
        return m_keepTestresultSummary;
    }

}
