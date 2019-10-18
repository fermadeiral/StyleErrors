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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jubula.client.core.model.IEventExecTestCasePO;
import org.eclipse.jubula.client.core.model.IEventHandlerContainer;
import org.eclipse.jubula.client.core.model.ReentryProperty;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.toolkit.common.xml.businessprocess.ComponentBuilder;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class AddEventHandlerDialog extends TitleAreaDialog {

    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1;    
    /** number of columns = 4 */
    private static final int NUM_COLUMNS_2 = 2;    
    /** vertical spacing = 2 */
    private static final int VERTICAL_SPACING = 10;    
    /** margin width = 0 */
    private static final int MARGIN_WIDTH = 10;    
    /** margin height = 2 */
    private static final int MARGIN_HEIGHT = 10;
    /** width hint = 300 */
    private static final int WIDTH_HINT = 300;

    /** maximum length of input */
    private int m_maxLength = 255;

    /** the parent area/composite */
    private Composite m_area = null;

    /** TextFieled for reference name */
    private Text m_refNameField;
    
    /** ComboBox for event types */
    private Combo m_eventTypesCombo;
    
    /** ComboBox for reentry types */
    private Combo m_reentryTypesCombo;

    /** The depending EventTestCase */
    private final IEventHandlerContainer m_eventHandlerContainer;

    /** 
     * label for max retries. this component is only shown if the RETRY 
     * reentry type is selected 
     */
    private Label m_maxRetriesLabel;
    /** 
     * text field for max retries. this component is only shown if the RETRY 
     * reentry type is selected 
     */
    private Spinner m_maxRetriesText;
    
    /** List of listener */
    private List < Listener > m_listenerList = new ArrayList < Listener > ();
    /** name of the test case to use as an event handler */
    private String m_tcName;
    
    /**
     * Constructor.
     * @param parentShell the parent shell.
     * @param tcName The name of the test case to use as an event handler.
     * @param eventTc the depending EventTestCase.
     */
    public AddEventHandlerDialog(Shell parentShell, String tcName,
        IEventHandlerContainer eventTc) {
        
        super(parentShell);
        m_eventHandlerContainer = eventTc;
        m_tcName = tcName;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Button createButton(Composite parent, int id, String label, 
        boolean defaultButton) {
        
        Button button = 
            super.createButton(parent, id, label, defaultButton);
        getButton(IDialogConstants.OK_ID).setEnabled(false);
        return button;
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {
        setTitle(Messages.AddEventHandlerDialogAddErrorHandler);
        setTitleImage(IconConstants.NEW_EH_DIALOG_IMAGE); 
        getShell().setText(Messages.AddEventHandlerDialogAddErrorHandler);
        setMessage(Messages.AddEventHandlerDialogMessage);

//      new Composite as container
        final GridLayout gridLayoutParent = new GridLayout();
        gridLayoutParent.numColumns = NUM_COLUMNS_1;
        gridLayoutParent.verticalSpacing = VERTICAL_SPACING;
        gridLayoutParent.marginWidth = MARGIN_WIDTH;
        gridLayoutParent.marginHeight = MARGIN_HEIGHT;
        parent.setLayout(gridLayoutParent);
        
        LayoutUtil.createSeparator(parent);
        m_area = new Composite(parent, SWT.FILL);
        final GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = NUM_COLUMNS_2;
        m_area.setLayout(gridLayout);
        
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.grabExcessVerticalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        gridData.widthHint = WIDTH_HINT;
        m_area.setLayoutData(gridData);
        
        new Label(m_area, SWT.NONE).setLayoutData(newGridData(NUM_COLUMNS_2));
        createWidgets(m_area);
        new Label(m_area, SWT.NONE).setLayoutData(newGridData(NUM_COLUMNS_2));
        LayoutUtil.createSeparator(parent);
        
        addListenerToWidgets();
        
        //add help id and tell eclipse that it's there
        Plugin.getHelpSystem().setHelp(parent, 
            ContextHelpIds.EVENT_HANDLER_ADD);
        setHelpAvailable(true);        
        
        return m_area;
    }
    
    
    
    /**
     * Creates the widgets.
     * 
     * @param parent
     *            the parent for the widgets.
     */
    private void createWidgets(Composite parent) {
        // create a non-editable text field for the name of the event handler
        Label eventNameLabel = new Label(parent, SWT.NONE);
        eventNameLabel.setText(Messages.AddEventHandlerDialogLabel);
        Text nameText = new Text(parent, SWT.FILL | SWT.BORDER);
        GridData eventNameGridData = 
            new GridData(SWT.FILL, SWT.NONE, true, false);
        nameText.setLayoutData(eventNameGridData);
        nameText.setText(m_tcName);
        nameText.setEnabled(false);

        // create an editable text field for the referenced name of the event
        // handler
        Label eventRefNameLabel = new Label(parent, SWT.NONE);
        eventRefNameLabel.setText(Messages.AddEventHandlerDialogRefNameLabel);
        m_refNameField = new Text(parent, SWT.FILL | SWT.BORDER);
        GridData eventRefNameGridData = new GridData(SWT.FILL, SWT.NONE, true,
                false);
        m_refNameField.setLayoutData(eventRefNameGridData);
        m_refNameField.setTextLimit(m_maxLength);

        Collection<IEventExecTestCasePO> eventTcList = m_eventHandlerContainer
                .getAllEventEventExecTC();
        // get a List of used event types in this TestCase.
        List<String> existentEventTypes = new ArrayList<String>();
        for (IEventExecTestCasePO eventTc : eventTcList) {
            existentEventTypes.add(eventTc.getEventType());
        }
        // create Combo with event types
        Label eventTypeLabel = new Label(parent, SWT.NONE);
        eventTypeLabel.setText(Messages.AddEventHandlerDialogEventType);
        m_eventTypesCombo = new Combo(parent, SWT.FILL | SWT.READ_ONLY);
        GridData eventGridData = new GridData(SWT.FILL, SWT.NONE, true, false);
        m_eventTypesCombo.setLayoutData(eventGridData);

        Set<String> mapKeySet = ComponentBuilder.getInstance().getCompSystem()
                .getEventTypes().keySet();
        List<String> selectableEventTypes = new ArrayList<String>();
        final Map<String, String> stringHelperMap = StringHelper.getInstance()
                .getMap();
        for (String eventTypeKey : mapKeySet) {
            if (!existentEventTypes.contains(eventTypeKey)) {
                selectableEventTypes.add(stringHelperMap.get(eventTypeKey));
            }
        }
        m_eventTypesCombo.setItems(selectableEventTypes
                .toArray(new String[selectableEventTypes.size()]));
        // create Combo with reentry types
        Label reentryTypeLabel = new Label(parent, SWT.NONE);
        reentryTypeLabel.setText(Messages.AddEventHandlerDialogReentryType);
        m_reentryTypesCombo = new Combo(parent, SWT.FILL | SWT.READ_ONLY);
        GridData reentryGridData = new GridData(SWT.FILL,
                SWT.NONE, true, false);
        m_reentryTypesCombo.setLayoutData(reentryGridData);
        ReentryProperty[] reentryProps = ReentryProperty.REENTRY_PROP_ARRAY;
        String[] reentryStrings = new String[reentryProps.length];
        for (int k = 0; k < reentryProps.length; k++) {
            reentryStrings[k] = reentryProps[k].toString();
        }
        m_reentryTypesCombo.setItems(reentryStrings);

        SelectionListener reentryComboListener = new SelectionListener() {
            public void widgetDefaultSelected(SelectionEvent e) {
                // Do nothing
            }

            public void widgetSelected(SelectionEvent e) {
                setMaxRetriesVisibility();
            }
        };

        m_reentryTypesCombo.addSelectionListener(reentryComboListener);
        createMaxNumRetriesWidgets(parent);
    }

    /**
     * Creates the widgets for maximum number of retries. 
     * 
     * @param parent
     *            the parent for the widgets.
     */
    private void createMaxNumRetriesWidgets(Composite parent) {
        m_maxRetriesLabel = new Label(parent, SWT.NONE);
        m_maxRetriesLabel.setText(
                Messages.AddEventHandlerDialogMaxNumRetries);
        
        m_maxRetriesText = new Spinner(
                parent, SWT.FILL | SWT.BORDER); 

        m_maxRetriesText.setSelection(
                IEventExecTestCasePO.DEFAULT_MAX_NUM_RETRIES);
        m_maxRetriesText.setMinimum(
                IEventExecTestCasePO.MIN_VALUE_MAX_NUM_RETRIES);
        m_maxRetriesText.setMaximum(
                IEventExecTestCasePO.MAX_VALUE_MAX_NUM_RETRIES);
     
        GridData maxRetriesGridData = 
            new GridData(SWT.FILL, SWT.NONE, true, false);
        m_maxRetriesText.setLayoutData(maxRetriesGridData);

        setMaxRetriesVisibility();
    }
        
    /**
     * Creates a new GridData.
     * @param horizontalSpan The horizontal span.
     * @return grid data
     */
    private GridData newGridData(int horizontalSpan) {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = false;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.horizontalSpan = horizontalSpan;
        return gridData;
    }
    
    /**
     * @param listener the listener
     */
    public void addListener(Listener listener) {
        if (!m_listenerList.contains(listener)) {
            m_listenerList.add(listener);
        }
    }
    
    /**
     * @param listener the listener
     */
    public void removeListener(Listener listener) {
        m_listenerList.remove(listener);
    }
      
    /**
     * Notifies the Listener
     */
    private void notifyListener() {
        Iterator<Listener> iter = m_listenerList.iterator();
        while (iter.hasNext()) {
            iter.next().notifySelected(m_refNameField.getText(),
                m_eventTypesCombo.getText(),
                m_reentryTypesCombo.getText(),
                m_maxRetriesText.isVisible() 
                    ? m_maxRetriesText.getSelection() : null);
        }
    }
       
    /**
     *  This method is called, when the OK button was pressed
     */
    protected void okPressed() {
        notifyListener();
        super.okPressed();
    }

    /**
     * @return false, if the reference name field contains an error: the name starts or
     *         end with a blank
     */
    private boolean checkRefNameFieldAction() {
        int nameLength = m_refNameField.getText().length();

        if (nameLength > 0) {
            if (nameLength >= m_maxLength) {
                ErrorHandlingUtil.createMessageDialog(MessageIDs.W_MAX_CHAR,
                        new Object[] { m_maxLength }, null);
                setErrorMessage(Messages.
                        AddEventHandlerDialogIncorrectRefNameInput);
                return false;
            }
            String refName = m_refNameField.getText();
            if (refName.startsWith(StringConstants.SPACE)
                    || refName.endsWith(StringConstants.SPACE)) {
                setErrorMessage(Messages.
                        AddEventHandlerDialogIncorrectRefNameInput);
                return false;
            }
        }
        return true;
    }

    /**
     * @return True, if something was selected in this combo box.
     */
    private boolean checkEventTypeComboAction() {
        if (m_eventTypesCombo.getText() == StringConstants.EMPTY) {
            setErrorMessage(Messages.AddEventHandlerDialogNoEventTypeSel);
            return false;
        }
        return true;
    }
    
    /**
     * @return True, if something was selected in this combo box.
     */
    private boolean checkReentryTypeComboAction() {
        if (m_reentryTypesCombo.getText() == StringConstants.EMPTY) {
            setErrorMessage(Messages.AddEventHandlerDialogNoReentryTypeSel);
            return false;
        }
        return true;
    }
    
    /**
     * en-/disables the OK button and makes a non-error title message.
     * 
     * @param enabled
     *            true or false.
     */
    private void enableOKButton(boolean enabled) {
        getButton(IDialogConstants.OK_ID).setEnabled(enabled);
        if (enabled) {
            setErrorMessage(null);
        }
    }
        
    /**
     * {@inheritDoc}
     */
    public boolean close() {
        m_listenerList.clear();
        return super.close();
    }
        
    /**
     * Abstract listener class to notify listener about the selections.
     * 
     * @author BREDEX GmbH
     * @created 13.04.2005
     */
    public abstract static class Listener {
        
        /**
         * Notifies about the selections.
         * @param refName of EventHandler.
         * @param eventType the selected event Type.
         * @param reentryType the selected reentryType.
         * @param maxRetries the maximum number of retries
         */
        public abstract void notifySelected(String refName,
            String eventType, String reentryType, 
            Integer maxRetries);
    }
        
    /**
     * Adds listener to the widgets.
     */
    private void addListenerToWidgets() {
        WidgetModifyListener listener = new WidgetModifyListener();
        m_eventTypesCombo.addModifyListener(listener);
        m_reentryTypesCombo.addModifyListener(listener);
        m_refNameField.addModifyListener(listener);
    }

    /**
     * 
     */
    private void setMaxRetriesVisibility() {
        boolean isRetryReentrySelected = 
            m_reentryTypesCombo.getSelectionIndex() != -1
            && ReentryProperty.REENTRY_PROP_ARRAY
                [m_reentryTypesCombo.getSelectionIndex()].equals(
                        ReentryProperty.RETRY);
                
        
        m_maxRetriesLabel.setVisible(isRetryReentrySelected);
        m_maxRetriesText.setVisible(isRetryReentrySelected);
        m_maxRetriesLabel.getParent().layout();
    }

    /**
     * ModifyListener for the all fields.
     * 
     * @author BREDEX GmbH
     * @created 14.04.2005
     */
    private class WidgetModifyListener implements ModifyListener {
        /**
         * {@inheritDoc}
         */
        public void modifyText(ModifyEvent e) {
            enableOKButton(dialogInputIsValid());
        }


    }

    /**
     * This method is called on modification of any of Add Event Handler Dialog components
     * @return false, if at least one of the values
     * that were inputed in the Add Event Handler Dialog is invalid
     * @author M.Maulhs
     * @created 22.10.2013
     */
    private boolean dialogInputIsValid() {
        return  checkRefNameFieldAction()
                && checkReentryTypeComboAction()
                && checkEventTypeComboAction();
    }
}