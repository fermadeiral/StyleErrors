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
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.client.ui.widgets.UIComponentHelper;
import org.eclipse.jubula.tools.internal.constants.MonitoringConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

/**
 * Base composite, where item (such as file, directory) pathes can be added,
 * edited, removed.
 *
 * @author BREDEX GmbH
 * @created Jan 15, 2016
 */
@SuppressWarnings("synthetic-access")
public abstract class BaseMultiBrowserComposite extends Composite {
   
    /** layout for buttons */
    public static final GridData BUTTON_LAYOUT;

    static {
        BUTTON_LAYOUT = new GridData();
        BUTTON_LAYOUT.horizontalAlignment = GridData.FILL;
        BUTTON_LAYOUT.grabExcessHorizontalSpace = true;
    }

    /** gui component to display the selected directories. */
    private List m_itemList;

    /** gui component to add item. */
    private Button m_addElementButton;

    /** gui component to edit selected item. */
    private Button m_editElementButton;

    /** gui component to remove selected item. */
    private Button m_removeElementButton;
    
    /**
     * Multi-item browser with add, edit, remove buttons.
     * 
     * @param parent
     *            The parent composite
     * @param attrId
     *            monitoring constant id
     * @param configurationValue
     *            value, which contains the current configuration
     */
    public BaseMultiBrowserComposite(Composite parent, String attrId,
            String configurationValue) {
        super(parent, SWT.NONE);
       
        GridData layoutData = new GridData();
        layoutData.horizontalAlignment = GridData.FILL;
        layoutData.grabExcessHorizontalSpace = true;
        layoutData.heightHint = 1;
        this.setLayoutData(layoutData);
        
        m_itemList = new List(
                parent, LayoutUtil.MULTI_TEXT_STYLE | SWT.MULTI);
        m_itemList.setData(MonitoringConstants.MONITORING_KEY,
                attrId);
        
        GridData textGridData = new GridData();
        textGridData.horizontalAlignment = GridData.FILL;
        textGridData.grabExcessHorizontalSpace = true;
        textGridData.heightHint = Dialog.convertHeightInCharsToPixels(
                LayoutUtil.getFontMetrics(m_itemList), 2) + 10;
        LayoutUtil.addToolTipAndMaxWidth(textGridData,
                m_itemList);
        m_itemList.setLayoutData(textGridData);

        initList(configurationValue);
        
        m_itemList
                .addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        checkActionButtons();
                        m_itemList.setFocus();
                    }
                });

        Composite dirctoryActionComposite = UIComponentHelper
                .createLayoutComposite(parent, 3);

        initActionButtons(dirctoryActionComposite);
        checkActionButtons();
    }
    
    /**
     * @return lististem, which contains the browsed items.
     */
    public List getItemList() {
        return m_itemList;
    }

    /**
     * Initialize the add, edit, remove buttons.
     * @param dirctoryActionComposite parent composite
     */
    protected void initActionButtons(Composite dirctoryActionComposite) {
        m_addElementButton = new Button(dirctoryActionComposite,
                SWT.PUSH);
        m_editElementButton = new Button(dirctoryActionComposite,
                SWT.PUSH);
        m_removeElementButton = new Button(dirctoryActionComposite,
                SWT.PUSH);

        m_addElementButton.setText(Messages.AUTConfigComponentElement);
        m_addElementButton.setLayoutData(BUTTON_LAYOUT);

        m_addElementButton.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent event) {
                handleSelection(false);
                checkActionButtons();
            }
        });
        m_editElementButton.setText(Messages.AUTConfigComponentEdit);
        m_editElementButton.setLayoutData(BUTTON_LAYOUT);
        m_editElementButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleSelection(true);
                checkActionButtons();
            }
        });

        m_removeElementButton.setText(Messages.AUTConfigComponentRemove);
        m_removeElementButton.setLayoutData(BUTTON_LAYOUT);
        m_removeElementButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                handleItemRemove();
                checkActionButtons();
            }

        });
    }

    /**
     * Handle item remove action, when an item should be removed.
     */
    protected void handleItemRemove() {
        int selectionIndex = m_itemList.getSelectionIndex();
        m_itemList
                .remove(m_itemList.getSelection()[0]);
        if (m_itemList.getItemCount() >= selectionIndex) {
            m_itemList.select(selectionIndex - 1);
        }
        if (m_itemList.getItemCount() == 1) {
            m_itemList.select(0);
        }
        if (m_itemList.getSelectionCount() == 0) {
            m_itemList.select(0);
        }
        
        updateStoredValues();
    }

    /**
     * Init list with previously saved configuration data
     * 
     * @param configurationValue contains saved item pathes
     */
    void initList(String configurationValue) {
        getItemList().removeAll();
        if (!StringUtils.isEmpty(configurationValue)) {
            String[] items = configurationValue
                    .split(StringConstants.SEMICOLON);
            for (int i = 0; i < items.length; i++) {
                getItemList().add(items[i]);
            }
        }
    }
    
    /**
     * returns the length of the concatenated item names
     * 
     * @return int
     */
    protected int getItemsLength() {
        String directories = StringConstants.EMPTY;
        for (int i = 0; i < getItemList().getItemCount(); i++) {
            directories = directories.concat(
                    getItemList().getItem(i) + StringConstants.SEMICOLON);
        }
        return directories.length();
    }
    
    /**
     * get the multiple item list in concatenated format, separated by semicolon
     * 
     * @return item list concatenated into one string, separated
     *         by semicolon
     */
    protected String getItemPathes() {

        String itemList = StringConstants.EMPTY;

        for (int i = 0; i < getItemList().getItemCount(); i++) {

            itemList = itemList.concat(
                    getItemList().getItem(i) + StringConstants.SEMICOLON);
        }
        if (!StringConstants.EMPTY.equals(itemList)) {
            // cut off the last semicolon
            itemList = itemList.substring(0,
                    itemList.length() - 1);
        }

        return itemList;
    }

    /**
     * Handle selection of item
     * 
     * @param isEditButtonPressed
     *            if true, this is modifying a current path, if false, it is
     *            addition for a new path
     */
    abstract void handleSelection(boolean isEditButtonPressed);

    /**
     * invoke the listeners, whiches are responsible to persist changes.
     */
    abstract void updateStoredValues();

    /**
     * check and set the enabled state of action buttons
     */
    protected void checkActionButtons() {
        if (m_itemList.getItemCount() == 0) {
            m_removeElementButton.setEnabled(false);
            m_editElementButton.setEnabled(false);
            return;
        }
        if (m_itemList.getSelectionCount() > 0) {
            m_removeElementButton.setEnabled(true);
            m_editElementButton.setEnabled(true);
        } else {
            m_removeElementButton.setEnabled(false);
            m_editElementButton.setEnabled(false);
        }
    }
}
