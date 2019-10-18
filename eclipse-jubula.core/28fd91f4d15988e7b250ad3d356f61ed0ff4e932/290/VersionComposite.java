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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.core.model.ProjectVersion;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedIntText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedProjectNameText;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public abstract class VersionComposite extends Composite {

    /** TextField for the project name */
    private Text m_projectNameField;
    /** TextField for the project version qualifier */
    private Text m_versionQualifierField;
    /** TextField for the project major version */
    private Text m_majorVersionField;
    /** TextField for the project minor version */
    private Text m_minorVersionField;
    /** TextField for the project micro version */
    private Text m_microVersionField;
    
    /** is everything right? */
    private boolean m_okAllowed = false;
    /** max length*/
    private int m_length = 255;

    /**
     * 
     * @param parent parent
     * @param style swt style
     * @param version the {@link ProjectVersion} which should be used as initial input
     */
    public VersionComposite(Composite parent, int style, 
            ProjectVersion version) {
        this(parent, style, version, false);
    }
    
    /**
     * 
     * @param parent parent
     * @param style swt style
     * @param version the {@link ProjectVersion} which should be used as initial input
     * @param withNameField should there be the project name field?
     */
    public VersionComposite(Composite parent, int style,
            ProjectVersion version, boolean withNameField) {
        super(parent, style);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 4;
        this.setLayout(gridLayout);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.BEGINNING;
        this.setLayoutData(gridData);
        createNameField(this, withNameField);
        createVersionFields(this, version);
        checkIfVersionsAreCorrect();
        checkAndModifyEnablementOfFields();
    }

    /**
     * creates the project name field
     * @param composite parent
     * @param withName true if the name field should be created
     */
    private void createNameField(Composite composite, boolean withName) {
        if (!withName) {
            return;
        }
        GridData gridData = newGridData();
        new Label(composite, SWT.NONE).setText(
                Messages.SaveProjectAsActionLabel);
        m_projectNameField = new CheckedProjectNameText(
                composite, SWT.SINGLE | SWT.BORDER);
        gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_projectNameField);
        gridData.widthHint = 0;
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        m_projectNameField.setLayoutData(gridData);
        m_projectNameField.setText(StringUtils.defaultString(null));
        m_projectNameField.setData(SwtToolkitConstants.WIDGET_NAME,
                "VersionComposite.ProjectNameField"); //$NON-NLS-1$
        LayoutUtil.setMaxChar(m_projectNameField, m_length);
        m_projectNameField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyVersionFieldAction();           
            }
        });
        
    }

    /**
     * @param area The composite. creates the text field to edit the TestSuite name.
     * @param version the inital version for the fields
     */
    private void createVersionFields(Composite area, ProjectVersion version) {
        new Label(area, SWT.NONE).setText(
                Messages.CreateNewProjectVersionActionVersionNumbers);
        m_majorVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            true, 0, Integer.MAX_VALUE);
        GridData gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_majorVersionField);
        gridData.widthHint = Dialog.convertWidthInCharsToPixels(
                LayoutUtil.getFontMetrics(m_majorVersionField), 10);
        m_majorVersionField.setLayoutData(gridData);
        m_majorVersionField.setText(version.getMajorNumber() != null ? String
                .valueOf(version.getMajorNumber()) : StringConstants.EMPTY);
        LayoutUtil.setMaxChar(m_majorVersionField, m_length);
        m_majorVersionField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyVersionFieldAction();
            }
        });
        m_majorVersionField.setData(SwtToolkitConstants.WIDGET_NAME,
                "VersionComposite.MajorVersionField"); //$NON-NLS-1$
        
        m_minorVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            true, 0, Integer.MAX_VALUE);
        gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_minorVersionField);
        gridData.widthHint = Dialog.convertWidthInCharsToPixels(
                LayoutUtil.getFontMetrics(m_minorVersionField), 10);
        m_minorVersionField.setLayoutData(gridData);
        m_minorVersionField.setText(version.getMinorNumber() != null ? String
                .valueOf(version.getMinorNumber()) : StringConstants.EMPTY);
        LayoutUtil.setMaxChar(m_minorVersionField, m_length);
        m_minorVersionField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {        
                modifyVersionFieldAction();         
            }
        });
        m_minorVersionField.setData(SwtToolkitConstants.WIDGET_NAME,
                "VersionComposite.MinorVersionField"); //$NON-NLS-1$
        m_microVersionField = new CheckedIntText(area, SWT.SINGLE | SWT.BORDER, 
            true, 0, Integer.MAX_VALUE);
        gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_microVersionField);
        gridData.widthHint = Dialog.convertWidthInCharsToPixels(
                LayoutUtil.getFontMetrics(m_microVersionField), 10);
        m_microVersionField.setLayoutData(gridData);
        m_microVersionField.setText(version.getMicroNumber() != null ? String
                .valueOf(version.getMicroNumber()) : StringConstants.EMPTY);
        LayoutUtil.setMaxChar(m_microVersionField, m_length);
        m_microVersionField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {           
                modifyVersionFieldAction();        
            }
        });
        m_microVersionField.setData(SwtToolkitConstants.WIDGET_NAME,
                "VersionComposite.MicroVersionField"); //$NON-NLS-1$
        new Label(area, SWT.NONE).setText(
                Messages.CreateNewProjectVersionActionQualifierLabel);
        m_versionQualifierField = new Text(area, SWT.SINGLE | SWT.BORDER);
        gridData = newGridData();
        LayoutUtil.addToolTipAndMaxWidth(gridData, m_versionQualifierField);
        gridData.widthHint = 0;
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = GridData.FILL;
        m_versionQualifierField.setLayoutData(gridData);
        m_versionQualifierField.setText(StringUtils.defaultIfBlank(
                version.getVersionQualifier(), StringConstants.EMPTY));
        LayoutUtil.setMaxChar(m_versionQualifierField, m_length);
        m_versionQualifierField.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e) {
                modifyVersionFieldAction();           
            }
        });
        m_versionQualifierField.setData(SwtToolkitConstants.WIDGET_NAME,
                "VersionComposite.VersionQualifierField"); //$NON-NLS-1$
    }
    
    /**
     * Creates a new GridData.
     * @return grid data
     */
    private GridData newGridData() {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.BEGINNING;
        gridData.horizontalSpan = 1;
        return gridData;
    }
    
    /**
     * Ask if this version is allowed as a change.
     * This method is also used to set the error message of the dialog
     * @return true if the chosen version can be created
     */
    public abstract Boolean isChangeAllowed();
    
    /**
     * sets the message of the dialog if it is necessary
     * @param string message
     */
    public abstract void setMessage(String string);
    
    /**
     * this is called after {@link #modifyVersionFieldAction()} to give the
     * implementing composite the change to react on changes
     */
    public abstract void modifiedAction();
    
    /**
     * the action of a version field
     * @return false, if one of the fields contains an error
     */
    public boolean modifyVersionFieldAction() {        
        boolean isCorrect = checkIfVersionsAreCorrect();
        checkAndModifyEnablementOfFields();
        if (isCorrect) {
            m_okAllowed = true;
            if (!isChangeAllowed()) {
                m_okAllowed = false;
                isCorrect = false;
            }
        } else {
            m_okAllowed = false;
        }
        modifiedAction();
        return isCorrect;
    }
    /**
     * Enables and disables version number fields so that the number sequence is correct
     */
    private void checkAndModifyEnablementOfFields() {
        if (StringUtils.isBlank(m_majorVersionField.getText())) {
            m_minorVersionField.setEnabled(false);
            m_microVersionField.setEnabled(false);
        } else {
            m_minorVersionField.setEnabled(true);
        }
        if (StringUtils.isBlank(m_minorVersionField.getText())
                || !m_minorVersionField.isEnabled()) {
            m_microVersionField.setEnabled(false);
        } else {
            m_microVersionField.setEnabled(true);
        }

    }
    
    /**
     * Checks for some conditions which are not correct
     * @return true if everything is okay
     */
    private boolean checkIfVersionsAreCorrect() {
        boolean isCorrect = false;
        try {
            isCorrect = (StringUtils.isNotBlank(m_majorVersionField.getText()) 
                    || StringUtils
                    .isNotBlank(m_versionQualifierField.getText()));
            if (isCorrect && StringUtils.isNotBlank(
                    m_minorVersionField.getText())) {
                isCorrect = StringUtils.isNotBlank(
                        m_majorVersionField.getText());
            }
            if (isCorrect && StringUtils.isNotBlank(
                    m_microVersionField.getText())) {
                isCorrect = StringUtils.isNotBlank(
                        m_minorVersionField.getText())
                        && StringUtils.isNotBlank(
                                m_majorVersionField.getText());
            }

        } catch (NumberFormatException nfe) {
            // Do nothing, the input is not correct and isCorrect remains false
        }
        if (!isCorrect) {
            setMessage(Messages.CreateNewProjectVersionActionInvalidVersion);
        }
        return isCorrect;
    }
    
    /**
     * @return Returns the text of the input field.
     */
    public Integer getMajorFieldValue() {
        Integer value = null;
        try {
            value = Integer.parseInt(m_majorVersionField.getText());
        } catch (NumberFormatException nfe) {
            // This must not be handled
        }
        return value;
    }

    /**
     * @return Returns the text of the input field.
     */
    public Integer getMinorFieldValue() {
        Integer value = null;
        try {
            if (m_minorVersionField.isEnabled()) {
                value = Integer.parseInt(m_minorVersionField.getText());
            }
        } catch (NumberFormatException nfe) {
            // This must not be handled
        }
        return value;
    }
    
    /**
     * @return Returns the text of the input field.
     */
    public Integer getMicroFieldValue() {
        Integer value = null;
        try {
            if (m_microVersionField.isEnabled()) {                
                value = Integer.parseInt(m_microVersionField.getText());
            }
        } catch (NumberFormatException nfe) {
            // This must not be handled
        }
        return value;
    }
    
    /**
     * @return Returns the text of the input field.
     */
    public String getQualifierFieldValue() {
        if (StringUtils.isBlank(m_versionQualifierField.getText())) {
            return null;
        }
        return StringUtils.trim(m_versionQualifierField.getText());
    }
    
    /**
     * @return Returns the text of the project name field.
     */
    public String getProjectNameFieldValue() {
        if (m_projectNameField != null) {
            return m_projectNameField.getText();
        }
        return StringConstants.EMPTY;
    }

    /**
     * @return if the ok button should be active
     */
    public boolean isOKAllowed() {
        return m_okAllowed;
    }
    
    /**
     * @return the projectVersion generated from the field values
     */
    public ProjectVersion getVersion() {
        return new ProjectVersion(getMajorFieldValue(), getMinorFieldValue(),
                getMicroFieldValue(), getQualifierFieldValue());
    }
}
