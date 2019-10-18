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
package org.eclipse.jubula.client.ui.rcp.widgets;

import java.util.Collection;

import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.ui.rcp.dialogs.EnterAutIdDialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.provider.ControlDecorator;
import org.eclipse.jubula.client.ui.utils.DialogUtils;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;



/**
 * @author BREDEX GmbH
 * @created Jan 21, 2010
 */
public class AutIdListComposite extends Composite {

    /** the AUT containing the AUT ID list to be viewed / modified */
    private IAUTMainPO m_aut;

    /**
     * Constructor
     * 
     * @param parent The parent composite for this component.
     * @param aut The AUT containing the AUT ID list to be viewed / modified
     * @param autIdValidator The validator for the AUT ID text field.
     */
    public AutIdListComposite(Composite parent, IAUTMainPO aut, 
            IValidator autIdValidator) {
        super(parent, SWT.NONE);
        m_aut = aut;
        init(autIdValidator);
    }

    /**
     * Creates the necessary components and sets their initial values.
     * 
     * @param autIdValidator The validator for the AUT ID text field.
     */
    @SuppressWarnings("unchecked")
    private void init(final IValidator autIdValidator) {
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = 2;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        setLayout(compositeLayout);
        Label idLabel = new Label(this, SWT.NONE);
        idLabel.setText(Messages.AUTPropertiesDialogAutId);
        ControlDecorator.createInfo(idLabel, 
                I18n.getString("AUTPropertiesDialog.AutId.helpText"), false); //$NON-NLS-1$
        GridData data = new GridData(SWT.BEGINNING, SWT.FILL, false, false);
        data.horizontalSpan = 1;
        idLabel.setLayoutData(data);

        // Created to keep layout consistent
        new Label(this, SWT.NONE).setVisible(false);
        
        final WritableList idListModel = new WritableList(m_aut.getAutIds(),
                String.class);
        final ListViewer idListViewer = new ListViewer(this,
                LayoutUtil.MULTI_TEXT_STYLE);
        idListViewer.setContentProvider(new ObservableListContentProvider());
        idListViewer.setComparator(new ViewerComparator());
        idListViewer.setInput(idListModel);
        final List idList = idListViewer.getList();
        data = new GridData(SWT.FILL, SWT.FILL, true, false);
        data.verticalSpan = 3;
        data.widthHint = Dialog.convertHeightInCharsToPixels(LayoutUtil
                .getFontMetrics(idList), 4);
        idList.setLayoutData(data);

        createButtons(this, autIdValidator, idList, idListModel);
    }

    /**
     * 
     * @param parent The parent composite for the buttons.
     * @param idList The list component containing the AUT IDs.
     * @param autIdValidator The validator for AUT IDs.
     * @param idListModel The model containing the AUT IDs.
     */
    private static void createButtons(Composite parent, 
            final IValidator autIdValidator,
            final List idList, final Collection<String> idListModel) {
        GridData data;
        final Button addButton = new Button(parent, SWT.NONE);
        addButton.setText(Messages.AUTConfigComponentAdd);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        addButton.setLayoutData(data);
        addButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            public void widgetSelected(SelectionEvent event) {
                EnterAutIdDialog dialog = new EnterAutIdDialog(
                        addButton.getShell(), null, autIdValidator);
                if (dialog.open() == Window.OK) {
                    idListModel.add(dialog.getAutId());
                }
            }
        });
        
        final Button editButton = new Button(parent, SWT.NONE);
        editButton.setEnabled(false);
        editButton.setText(Messages.AUTConfigComponentEdit);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        editButton.setLayoutData(data);
        editButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            @SuppressWarnings("synthetic-access")
            public void widgetSelected(SelectionEvent event) {
                handleEditAutIdDialog(idList, autIdValidator, idListModel);
            }
        });

        final Button removeButton = new Button(parent, SWT.NONE);
        removeButton.setEnabled(false);
        removeButton.setText(Messages.AUTConfigComponentRemove);
        data = new GridData(SWT.FILL, SWT.BEGINNING, false, false);
        removeButton.setLayoutData(data);
        removeButton.addSelectionListener(new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent event) {
                widgetSelected(event);
            }
            public void widgetSelected(SelectionEvent event) {
                if (idList.getSelectionCount() != 0) {
                    idListModel.remove(
                            idList.getItem(idList.getSelectionIndex()));
                    if (idList.getItemCount() > 0) {
                        idList.setSelection(0);
                    } else {
                        idList.deselectAll();
                        removeButton.setEnabled(false);
                        editButton.setEnabled(false);
                    }
                }
            }
        });
        
        idList.addSelectionListener(new SelectionListener() {

            @SuppressWarnings("synthetic-access")
            public void widgetDefaultSelected(SelectionEvent event) {
                handleEditAutIdDialog(idList, autIdValidator, idListModel);
            }

            public void widgetSelected(SelectionEvent event) {
                boolean enableSelectionButtons = 
                    idList.getSelectionCount() == 1;
                editButton.setEnabled(enableSelectionButtons);
                removeButton.setEnabled(enableSelectionButtons);
            }
            
        });
    }
    
    /**
     * 
     * @param idList The list component containing the AUT ID being edited.
     * @param autIdValidator The validator for the AUT ID.
     * @param idListModel The model containing the AUT ID being edited.
     */
    private static void handleEditAutIdDialog(List idList, 
            IValidator autIdValidator, Collection<String> idListModel) {
        if (idList.getSelectionCount() != 0) {
            String originalId = 
                idList.getItem(idList.getSelectionIndex());
            EnterAutIdDialog dialog = new EnterAutIdDialog(
                    idList.getShell(), originalId, autIdValidator);
            dialog.create();
            DialogUtils.setWidgetNameForModalDialog(dialog);
            if (dialog.open() == Window.OK) {
                idListModel.remove(originalId);
                idListModel.add(dialog.getAutId());
            }
        }
    }
}
