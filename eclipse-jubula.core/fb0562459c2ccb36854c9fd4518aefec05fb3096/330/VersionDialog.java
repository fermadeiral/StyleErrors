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

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;



/**
 * @author BREDEX GmbH
 * @created Jul 3, 2007
 */
public class VersionDialog extends TitleAreaDialog {
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 2;
    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;
    /** String value for the project name */
    private String m_projectName;
    /** int value of major version field */
    private Integer m_majorVersionNumber = null;
    /** int value of minor version field */
    private Integer m_minorVersionNumber = null;
    /** int value of micro version field */
    private Integer m_microVersionNumber = null;
    /** String value of qualifier version field */
    private String m_versionQualifier = null;
    /** The Version composite - has all versions field and logic in it*/
    private VersionComposite m_versionComposite;
    
    /** the message depends on the object that is selected */
    private String m_message = StringConstants.EMPTY;
    /** the image depends on the object that is selected */
    private String m_image = StringConstants.EMPTY;
    /** the shell depends on the object that is selected */
    private String m_shell = StringConstants.EMPTY;
    /** the title depends on the object that is selected */
    private String m_title = StringConstants.EMPTY;
    /** the major number of the highest version */
    private Integer m_greatestMajor = null;
    /** the minor number of the highest version */
    private Integer m_greatestMinor = null;
    /** the micro number of the highest version */
    private Integer m_greatestMicro = null;
    /** project Name Label*/
    private boolean m_withProjectNameLabel = false;
    
    /** the major number of the highest version */
    private ProjectVersion m_projectVersion = null;
    /** the qualifier of the highest version */
    private String m_greatestQualifier = null;

    /**
     * @param parentShell The parent shell.
     * @param title The name of the title.
     * @param version is the version you want as base version
     * @param message The message.
     * @param image The image of the dialog.
     * @param shell The name of the shell.
     */
    public VersionDialog(Shell parentShell, String title,
            ProjectVersion version, String message,
            String image, String shell) {

        super(parentShell);
        setNewVersion(version);
        m_message = message;
        m_image = image;
        m_shell = shell;
        m_title = title;
    }
    /**
     * @param parentShell The parent shell.
     * @param title The name of the title.
     * @param message The message.
     * @param image The image of the dialog.
     * @param shell The name of the shell.
     * @param withProjectNameLabel Should there be a field for project name?
     */
    public VersionDialog(Shell parentShell, String title,
            String message, String image, String shell,
            boolean withProjectNameLabel) {

        super(parentShell);
        m_withProjectNameLabel = withProjectNameLabel;
        m_greatestMajor = 1;
        m_greatestMinor = 0;
        m_projectVersion = new ProjectVersion(1, 0, null);
        m_message = message;
        m_image = image;
        m_shell = shell;
        m_title = title;
    }
    
    /**
     * 
     * @param greatestVersion the greatest existing projectVersion
     */
    private void setNewVersion(ProjectVersion greatestVersion) {
        m_greatestMajor = greatestVersion.getMajorNumber();
        m_greatestMinor = greatestVersion.getMinorNumber();
        m_greatestMicro = greatestVersion.getMicroNumber();
        m_greatestQualifier = greatestVersion.getVersionQualifier();
        if (m_greatestMajor != null && m_greatestMinor == null) {
            m_greatestMajor += 1;
        }
        if (m_greatestMinor != null && m_greatestMicro == null) {
            m_greatestMinor += 1;
        }
        if (m_greatestMicro != null) {
            m_greatestMicro += 1;
        }
        m_projectVersion = new ProjectVersion(m_greatestMajor, m_greatestMinor,
                m_greatestMicro, m_greatestQualifier);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setMessage(m_message);
        setTitle(m_title);
        setTitleImage(IconConstants.getImage(m_image));
        getShell().setText(m_shell);
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
        gridLayout.numColumns = 1;
        area.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = GridData.BEGINNING;
        area.setLayoutData(gridData);
        createVersionFields(area);
        createAdditionalComponents(area);
        LayoutUtil.createSeparator(parent);
        return area;
    }
    
    /**
     * Sets the shell style bits. This method has no effect after the shell iscreated.
     * <p>
     * The shell style bits are used by the framework method
     * <code>createShell</code> when creating this window's shell.
     * </p>
     * @param style the new shell style bits
     */
    public void setStyle(int style) {
        setShellStyle(style);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void initializeBounds() {
        super.initializeBounds();
        setMessage(m_message);
    }

    /**
     * @param area
     *            The composite. creates the text field to edit the TestSuite
     *            name.
     */
    private void createVersionFields(Composite area) {
        m_versionComposite = new VersionComposite(area, 0, m_projectVersion,
                m_withProjectNameLabel) {

            public Boolean isChangeAllowed() {
                return isInputAllowed();
            }

            public void setMessage(String string) {
                setErrorMessage(string);

            }

            public void modifiedAction() {
                if (getButton(IDialogConstants.OK_ID) != null
                        && isOKAllowed()) {
                    getButton(IDialogConstants.OK_ID).setEnabled(true);
                    setErrorMessage(null);
                } else if (getButton(IDialogConstants.OK_ID) != null
                        && !isOKAllowed()) {
                    getButton(IDialogConstants.OK_ID).setEnabled(false);
                }

            }

        };

    }

    /**
     * This method is also used to set the error message of the dialog.
     * So if This is returning <code>false</code> please set the error message of the dialog
     * @return False, if the input name already exists.
     */
    protected boolean isInputAllowed() {
        return true;
    }

    /**
     * This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        if (!m_versionComposite.modifyVersionFieldAction()) {
            return;
        }
        ProjectVersion version = m_versionComposite.getVersion();
        m_majorVersionNumber = version.getMajorNumber();
        m_minorVersionNumber = version.getMinorNumber();
        m_microVersionNumber = version.getMicroNumber();
        m_versionQualifier = version.getVersionQualifier();
        m_projectName = m_versionComposite.getProjectNameFieldValue();
        setReturnCode(OK);

        close();
    }

    /**
     * @return a projectVersion generated from the version numbers
     */
    public ProjectVersion getProjectVersion() {
        return new ProjectVersion(m_majorVersionNumber, m_minorVersionNumber,
                m_microVersionNumber, m_versionQualifier);
    }
    
    /**
     * Subclasses can add new guiComponents to the given layout.
     * @param parent the parent composite
     */
    protected void createAdditionalComponents(Composite parent) {
        parent.setEnabled(true); // placeholder
    }
    /** this could only be used when Dialog is open 
     * @return project version actually in the fields
     */
    public ProjectVersion getFieldVersion() {
        return m_versionComposite.getVersion();
    }
    
    /** this could only be used when Dialog is open 
     * @return project name
     */
    public String getProjectNameFieldValue() {
        return m_versionComposite.getProjectNameFieldValue();
    }
    
    /**
     * @return the project name
     */
    public String getProjectName() {
        return m_projectName;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, 
        boolean defaultButton) {
        Button button = super.createButton(parent, id, label, defaultButton);
        if (m_withProjectNameLabel || !isInputAllowed()) {
            getButton(IDialogConstants.OK_ID).setEnabled(false);          
        }
        return button;
    }
}
