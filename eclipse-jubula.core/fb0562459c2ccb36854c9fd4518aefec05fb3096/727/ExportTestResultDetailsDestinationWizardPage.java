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
package org.eclipse.jubula.client.ui.rcp.wizards.pages;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.databinding.wizard.WizardPageSupport;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.utils.FileUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Wizard page for selecting where to export Test Result details.
 *
 * @author BREDEX GmbH
 * @created Jun 22, 2010
 */
public class ExportTestResultDetailsDestinationWizardPage 
        extends WizardPage {

    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            ExportTestResultDetailsDestinationWizardPage.class);

    /** name of the "destination" property */
    private static final String PROP_NAME_DESTINATION = "destination"; //$NON-NLS-1$
    
    /** chosen destination filename */
    private String m_destination;
    
    /**
     * Constructor
     */
    public ExportTestResultDetailsDestinationWizardPage() {
        super("testResultDetailsExportDestinationPage"); //$NON-NLS-1$
        setTitle(Messages.ExportTestResultDetailsWizardDestinationPageTitle);
        setDescription(
            Messages.ExportTestResultDetailsWizardPageDescription);
    }

    /**
     * {@inheritDoc}
     */
    public void createControl(Composite parent) {
        initializeDialogUnits(parent);
        DataBindingContext dbc = new DataBindingContext();

        // destination specification group
        Composite destinationSelectionGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        destinationSelectionGroup.setLayout(layout);
        destinationSelectionGroup.setLayoutData(new GridData(
                GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL));
        Label destinationLabel = new Label(destinationSelectionGroup, SWT.NONE);
        destinationLabel.setText(Messages
            .ExportTestResultDetailsWizardDestinationPageDestinationLabelText);
        // destination name entry field
        final Text destinationNameField = 
            new Text(destinationSelectionGroup, SWT.SINGLE | SWT.BORDER);
        GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL
                | GridData.GRAB_HORIZONTAL);
        destinationNameField.setLayoutData(data);
        UpdateValueStrategy targetToModelUpdateStrategy = 
            new UpdateValueStrategy(UpdateValueStrategy.POLICY_UPDATE);
        targetToModelUpdateStrategy.setAfterGetValidator(new IValidator() {
            public IStatus validate(Object value) {
                String stringValue = String.valueOf(value);
                if (!FileUtils.isValidPath(stringValue)) {
                    return ValidationStatus.error(Messages
                        .ExportTestResultDetailsWizardDestinationPageInvDest);
                }
                return ValidationStatus.ok();
            }
        });

        dbc.bindValue(
                WidgetProperties.text(SWT.Modify).observe(destinationNameField),
                PojoProperties.value(PROP_NAME_DESTINATION).observe(this),
                targetToModelUpdateStrategy, 
                new UpdateValueStrategy(UpdateValueStrategy.POLICY_NEVER));
        
        try {
            destinationNameField.setText(
                    new File(Utils.getLastDirPath()).getCanonicalPath());
        } catch (IOException ioe) {
            LOG.error(
                Messages.AnErrorOccurredWhileInitializingTheDestinationPath
                + StringConstants.DOT, ioe);
        }
        
        // destination browse button
        Button destinationBrowseButton = 
            new Button(destinationSelectionGroup, SWT.PUSH);
        destinationBrowseButton.setText(
            Messages.ExportTestResultDetailsWizardDestinationPageBrowseBtnText);
        destinationBrowseButton.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent event) {
                final DirectoryDialog fileDialog = new DirectoryDialog(
                        getWizard().getContainer().getShell(), 
                        SWT.SAVE | SWT.APPLICATION_MODAL);
                fileDialog.setText(Messages
                    .ExportTestResultDetailsWizardDestinationPageDialogTitle);
                fileDialog.setFilterPath(Utils.getLastDirPath());
                String newFileName = fileDialog.open();
                if (newFileName != null) {
                    Utils.storeLastDirPath(newFileName);
                    destinationNameField.setText(newFileName);
                }
            }
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
        });
        setButtonLayoutData(destinationBrowseButton);
        setControl(parent);
        WizardPageSupport.create(this, dbc);
    }

    /**
     * 
     * @return the destination chosen on this page, or <code>null</code> if no
     *         destination was chosen. Note that this method may also return 
     *         the empty string.
     */
    public String getDestination() {
        return m_destination;
    }
    
    /**
     * 
     * @param destination The destination to set.
     */
    public void setDestination(String destination) {
        m_destination = destination;
    }
}
