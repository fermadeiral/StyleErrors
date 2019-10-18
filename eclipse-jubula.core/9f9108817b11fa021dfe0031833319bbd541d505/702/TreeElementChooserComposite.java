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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;



/**
 * Composite with a tree and a list. Content can be shifted between the tree 
 * and list.
 *
 * @author BREDEX GmbH
 * @created Jun 13, 2007
 */
@SuppressWarnings("synthetic-access")
public class TreeElementChooserComposite extends Composite {
    
    /**
     * @author BREDEX GmbH
     * @created Aug 17, 2007
     */
    public interface IUsedListModifiedListener {
        
        /**
         * Handle list modified event.
         * @param newListEntries All entries after the list has been modified
         */
        public void usedListModified(String [] newListEntries);
        
    }

    /** horizontal style */
    public static final int HORIZONTAL = 0;
    /** vertical style */
    public static final int VERTICAL = 1;
    
    /** string that begins the display value */
    private static final String DISPLAY_VALUE_START = " ["; //$NON-NLS-1$
    
    /** string that ends the display value */
    private static final String DISPLAY_VALUE_END = "]"; //$NON-NLS-1$

    /***/
    private static final String LABEL = "Label"; //$NON-NLS-1$
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;  
    /** number of columns = 3 */
    private static final int NUM_COLUMNS_3 = 3; 
    
    /** the list field for the available items */ 
    private Tree m_availableTree;
    
    /** the list field for the used items */ 
    private List m_usedList;
    
    /** the button to shift a selection from used list to available tree  */
    private Button m_selectionAvailableToUsedButton;
    /** the button to shift all from available tree to used list  */
    private Button m_allAvailableToUsedButton;
    
    /** the button to shift a selection from used list to available tree  */
    private Button m_selectionUsedToAvailableButton;
    /** the button to shift all from available tree to used list */
    private Button m_allUsedToAvailableButton;

    /** the button to swap an item between the tree and list  */
    private Button m_swapButton;

    /** disabled button content array */
    private Object[] m_disabledButtonContents;
    /** button content array */
    private Object[] m_buttonContents;

    /** Parents that have a child that is used */
    private Set<String> m_usedParents = new HashSet<String>();
    
    /** the StateController */
    private final WidgetSelectionListener m_selectionListener = 
        new WidgetSelectionListener();
    
    /** Mapping between display Strings and corresponding GUI objects */
    private Map<String, IChooserCompositeGuiObject> m_listItemsToGuiObjects =
        new HashMap<String, IChooserCompositeGuiObject>();
    
    /** Mapping between TreeItems and corresponding GUI objects */
    private Map<TreeItem, IChooserCompositeGuiObject> m_treeItemsToGuiObjects =
        new HashMap<TreeItem, IChooserCompositeGuiObject>();

    /** list listeners */
    private Set<IUsedListModifiedListener> m_listeners = 
        new HashSet<IUsedListModifiedListener>();
    
    /**
     * Composite, with two listBoxes. You can shift the content between the two ListBoxes.
     * @param parent The parent composite.
     * @param availableLabel Label text of the available tree.
     * @param availableObjects All available objects.
     * @param usedLabel Label text of the used list.
     * @param usedObjects Objects to be placed in the used list.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonTexts The texts of the 5 buttons (example: ">",">>","<","<<")
     * @param buttonToolTips The texts of the toolTips of 5 buttons.
     * @param style <p>ListElementChooserComposite.HORIZONTAL (ListBoxes are side by side) or</p>
     * <p>ListElementChooserComposite.VERTICAL (ListBoxe one is below ListBox two)</p>
     */
    public TreeElementChooserComposite(Composite parent, String availableLabel, 
        Set<IChooserCompositeGuiObject> availableObjects, String usedLabel, 
        Set<IChooserCompositeGuiObject> usedObjects, int lineNumber, 
        String[] buttonTexts, String[] buttonToolTips, int style) {
        
        super(parent, SWT.NONE);
        m_disabledButtonContents = buttonTexts;
        m_buttonContents = buttonTexts;
        createControl(availableLabel, availableObjects, usedLabel, 
            usedObjects, lineNumber, buttonToolTips, style);
    }
    
    /**
     * @param parent The parent composite.
     * @param availableLabel Label text of the available tree.
     * @param availableObjects All available objects.
     * @param usedLabel Label text of the used list.
     * @param usedObjects Objects to be placed in the used list.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonImages The images of the 5 buttons.
     * @param disabledButtonImages The disabled images of the 5 buttons.
     * @param buttonToolTips The texts of the toolTips of 5 buttons.
     * @param style <p>ListElementChooserComposite.HORIZONTAL (ListBoxes are side by side) or</p>
     * <p>ListElementChooserComposite.VERTICAL (ListBoxe one is below ListBox two)</p>
     */
    public TreeElementChooserComposite(Composite parent, String availableLabel, 
        Set<IChooserCompositeGuiObject> availableObjects, String usedLabel, 
        Set<IChooserCompositeGuiObject> usedObjects, int lineNumber, 
        Image[] buttonImages, Image[] disabledButtonImages, 
        String[] buttonToolTips, int style) {
        
        super(parent, SWT.NONE);
        m_disabledButtonContents = disabledButtonImages;
        m_buttonContents = buttonImages;
        createControl(availableLabel, availableObjects, usedLabel,
            usedObjects, lineNumber, buttonToolTips, style);
    }

    /**
     * Creates the ListElementChooserComposite.
     * @param availableLabel Label text of the available tree.
     * @param availableObjects All available objects.
     * @param usedLabel Label text of the used list.
     * @param usedObjects Objects to be placed in the used list.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonToolTips The texts of the toolTips of 5 buttons.
     * @param style ListElementChooserComposite.HORIZONTAL or ListElementChooserComposite.VERTICAL 
     */
    private void createControl(String availableLabel, 
        Set<IChooserCompositeGuiObject> availableObjects,  String usedLabel, 
        Set<IChooserCompositeGuiObject> usedObjects, int lineNumber, 
        String[] buttonToolTips, int style) {

        
        if (style == HORIZONTAL) {
            createHorizontalLayout(availableLabel, usedLabel, 
                lineNumber, buttonToolTips, style);
        } else {
            Composite composite = this;
            GridLayout compositeLayout = new GridLayout();
            compositeLayout.numColumns = NUM_COLUMNS_3;
            compositeLayout.marginHeight = 0;
            compositeLayout.marginWidth = 0;
            composite.setLayout(compositeLayout);
            GridData compositeData = new GridData();
            compositeData.horizontalAlignment = GridData.FILL;
            compositeData.grabExcessHorizontalSpace = true;
            composite.setLayoutData(compositeData);
            createVerticalLayout(composite, availableLabel, usedLabel, 
                lineNumber, buttonToolTips, style);
        }        
        initFields(availableObjects, usedObjects);
        addListener();       
        checkButtons();        
    }

    /**
     * Creates the layout with Vertical alignment.
     * @param parent The parent composite.
     * @param listOneLabel Label text of the first ListBox.
     * @param listTwoLabel Label text of the second ListBox.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonToolTips The texts of the toolTips of 5 buttons.
     * @param style ListElementChooserComposite.HORIZONTAL or ListElementChooserComposite.VERTICAL 
     */
    private void createVerticalLayout(Composite parent, String listOneLabel, 
        String listTwoLabel, int lineNumber, String[] buttonToolTips, 
        int style) {
        
        Composite compositeLeft = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);

        Composite compositeMiddle = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, false);
        
        Composite compositeRight = createComposite(parent, NUM_COLUMNS_1, 
            GridData.FILL, true);
        
        m_availableTree = createAvailableTree(compositeLeft, listOneLabel, 
            lineNumber); 
        m_availableTree.addSelectionListener(m_selectionListener);
        createShiftButtons(style, compositeMiddle, buttonToolTips);
        m_usedList = createListField(compositeRight, listTwoLabel, 
            lineNumber);
        m_usedList.addSelectionListener(m_selectionListener);
    }

    /**
     * Creates the layout with Horizontal alignment.
     * @param listOneLabel Label text of the first ListBox.
     * @param listTwoLabel Label text of the second ListBox.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonToolTips The texts of the toolTips of 5 buttons.
     * @param style ListElementChooserComposite.HORIZONTAL or ListElementChooserComposite.VERTICAL 
     */
    private void createHorizontalLayout(String listOneLabel, 
        String listTwoLabel, int lineNumber, String[] buttonToolTips, 
        int style) {
        
        Composite composite = this;
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = NUM_COLUMNS_2;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData(GridData.FILL_BOTH);
        compositeData.grabExcessHorizontalSpace = true;
        compositeData.grabExcessVerticalSpace = false;
        compositeData.verticalAlignment = GridData.BEGINNING;
        composite.setLayoutData(compositeData);
        
        m_availableTree = createAvailableTree(composite, listOneLabel, 
            lineNumber); 
        m_availableTree.addSelectionListener(m_selectionListener);
        createShiftButtons(style, composite, buttonToolTips);
        m_usedList = createListField(composite, listTwoLabel, 
            lineNumber);
        m_usedList.addSelectionListener(m_selectionListener);
    }

    /**
     * Inits all swt field in this page.
     * @param availableObjects All available objects.
     * @param usedObjects Objects to be placed in the used list.
     */
    protected void initFields(Set<IChooserCompositeGuiObject> availableObjects, 
        Set<IChooserCompositeGuiObject> usedObjects) {
        
        Map<String, TreeItem> parentsThatExist = 
            new HashMap<String, TreeItem>();
        for (IChooserCompositeGuiObject guiObject : availableObjects) {
        
            String parent = guiObject.getParent();
            if (!parentsThatExist.containsKey(parent)) {
                TreeItem parentItem = new TreeItem(m_availableTree, SWT.NONE);
                parentItem.setText(parent);
                parentsThatExist.put(parent, parentItem);
            }
            
            boolean isValueInUsedList = 
                usedObjects.contains(guiObject);

            if (!isValueInUsedList) {
                TreeItem valueItem = 
                    new TreeItem(parentsThatExist.get(parent), SWT.NONE);
                valueItem.setText(guiObject.getTitle());
                m_treeItemsToGuiObjects.put(valueItem, guiObject);
            }
        }
        
        sortTreeItems();
        
        for (IChooserCompositeGuiObject item : usedObjects) {
            m_usedList.add(item.getDisplayString());
            m_listItemsToGuiObjects.put(item.getDisplayString(), item);
            m_usedParents.add(item.getParent());
        }
        
        sortListItems();
    }
    
    /**
     * Sorts all items in the used list alphabetically.
     */
    private void sortListItems() {
        String [] items = m_usedList.getItems();
        Arrays.sort(items);
        m_usedList.removeAll();
        for (String listItem : items) {
            m_usedList.add(listItem);
        }
    }

    /**
     * Creates the 5 arrow buttons
     * @param style ListElementChooserComposite.HORIZONTAL or ListElementChooserComposite.VERTICAL 
     * @param parent The parent composite.
     * @param buttonToolTips The texts of the toolTips of 5 buttons. 
     */
    private void createShiftButtons(int style, Composite parent, 
        String[] buttonToolTips) {
        if (style == HORIZONTAL) {
            createComposite(parent, NUM_COLUMNS_1, GridData.BEGINNING, false);
            Composite composite = createComposite(parent, NUM_COLUMNS_2, 
                GridData.FILL, true);
            Composite leftComposite = createComposite(composite, NUM_COLUMNS_2, 
                GridData.FILL, true);
            Composite rightComposite = createComposite(composite, NUM_COLUMNS_2,
                GridData.FILL, true);
            m_selectionAvailableToUsedButton = 
                new Button(leftComposite, SWT.PUSH);
            m_allAvailableToUsedButton = new Button(leftComposite, SWT.PUSH);
            m_selectionUsedToAvailableButton = 
                new Button(rightComposite, SWT.PUSH);
            m_allUsedToAvailableButton = new Button(rightComposite, SWT.PUSH);
            m_swapButton = new Button(rightComposite, SWT.PUSH);
        } else {
            createLabel(parent, StringConstants.EMPTY);
            m_selectionAvailableToUsedButton = new Button(parent, SWT.PUSH);
            m_allAvailableToUsedButton = new Button(parent, SWT.PUSH);
            createLabel(parent, StringConstants.EMPTY);
            m_selectionUsedToAvailableButton = new Button(parent, SWT.PUSH);
            m_allUsedToAvailableButton = new Button(parent, SWT.PUSH);
            m_swapButton = new Button(parent, SWT.PUSH);
        }
        GridData selectionOneToTwoGridData = new GridData();
        selectionOneToTwoGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            selectionOneToTwoGridData.horizontalAlignment = GridData.END;
        }
        selectionOneToTwoGridData.grabExcessHorizontalSpace = true;
        m_selectionAvailableToUsedButton.setLayoutData(
            selectionOneToTwoGridData);
        m_selectionAvailableToUsedButton.setImage(
            (Image)m_disabledButtonContents[0]);
        m_selectionAvailableToUsedButton.setEnabled(false);
        GridData allOneToTwoGridData = new GridData();
        allOneToTwoGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            allOneToTwoGridData.horizontalAlignment = GridData.BEGINNING;
        }
        allOneToTwoGridData.grabExcessHorizontalSpace = true;
        m_allAvailableToUsedButton.setLayoutData(allOneToTwoGridData);
        m_allAvailableToUsedButton.setImage((Image)m_disabledButtonContents[1]);
        m_allAvailableToUsedButton.setEnabled(false);
        GridData selectionTwoToOneGridData = new GridData();
        selectionTwoToOneGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            selectionTwoToOneGridData.horizontalAlignment = GridData.END;
        }
        selectionTwoToOneGridData.grabExcessHorizontalSpace = true;
        m_selectionUsedToAvailableButton.setLayoutData(
            selectionTwoToOneGridData);
        m_selectionUsedToAvailableButton.setImage(
            (Image)m_disabledButtonContents[2]);
        m_selectionUsedToAvailableButton.setEnabled(false);
        GridData allTwoToOneGridData = new GridData();
        allTwoToOneGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            allTwoToOneGridData.horizontalAlignment = GridData.BEGINNING;
        }
        allTwoToOneGridData.grabExcessHorizontalSpace = true;
        m_allUsedToAvailableButton.setLayoutData(allTwoToOneGridData);
        m_allUsedToAvailableButton.setImage((Image)m_disabledButtonContents[3]);
        m_allUsedToAvailableButton.setEnabled(false);
        setButtonRepresentations();
        setTooltips(buttonToolTips);
    }

    /**
     * Sets the text or image data for the buttons.
     */
    private void setButtonRepresentations() {
        if (m_buttonContents instanceof Image[]) {
            m_selectionAvailableToUsedButton.setImage(
                (Image)m_buttonContents[0]);
            m_allAvailableToUsedButton.setImage((Image)m_buttonContents[1]);
            m_selectionUsedToAvailableButton.setImage(
                (Image)m_buttonContents[2]);
            m_allUsedToAvailableButton.setImage((Image)m_buttonContents[3]);
        } else {
            m_selectionAvailableToUsedButton.setText(
                (String)m_buttonContents[0]);
            m_allAvailableToUsedButton.setText((String)m_buttonContents[1]);
            m_selectionUsedToAvailableButton.setText(
                (String)m_buttonContents[2]);
            m_allUsedToAvailableButton.setText((String)m_buttonContents[3]);
        }
    }
    
    /**
     * Sets the tooltips for the buttons.
     * @param buttonToolTips The texts of the toolTips of 5 buttons. 
     */
    private void setTooltips(String[] buttonToolTips) {
        m_selectionAvailableToUsedButton.setToolTipText(buttonToolTips[0]);
        m_allAvailableToUsedButton.setToolTipText(buttonToolTips[1]);
        m_selectionUsedToAvailableButton.setToolTipText(buttonToolTips[2]);
        m_allUsedToAvailableButton.setToolTipText(buttonToolTips[3]);
        m_swapButton.setToolTipText(buttonToolTips[4]);
    }
    /**
     * Sorts all items in the available tree alphabetically.
     */
    private void sortTreeItems() {
        SortedMap<String, java.util.List<String>> sortedTree = 
            new TreeMap<String, java.util.List<String>>();
        Set<IChooserCompositeGuiObject> tempSet = 
            new HashSet<IChooserCompositeGuiObject>();
        boolean [] isItemExpanded = 
            new boolean [m_availableTree.getItemCount()];
        for (int i = 0; i < m_availableTree.getItemCount(); i++) {
            TreeItem parentItem = m_availableTree.getItem(i);
            isItemExpanded[i] = parentItem.getExpanded();
            java.util.List<String> childNames = new ArrayList<String>();
            for (TreeItem childItem : parentItem.getItems()) {
                childNames.add(childItem.getText());
                IChooserCompositeGuiObject guiObj = 
                    m_treeItemsToGuiObjects.get(childItem);
                tempSet.add(guiObj);
            }
            Collections.sort(childNames);
            sortedTree.put(parentItem.getText(), childNames);
        }
        
        m_availableTree.removeAll();
        m_treeItemsToGuiObjects.clear();
        
        for (String parentName : sortedTree.keySet()) {
            TreeItem parentItem = new TreeItem(m_availableTree, SWT.NONE);
            parentItem.setText(parentName);

            for (String childName : sortedTree.get(parentName)) {
                TreeItem childItem = new TreeItem(parentItem, SWT.NONE);
                childItem.setText(childName);
                for (IChooserCompositeGuiObject obj : tempSet) {
                    if (obj.getParent().equals(parentName)
                        && obj.getTitle().equals(childName)) {
                        
                        m_treeItemsToGuiObjects.put(childItem, obj);
                        break;
                    }
                }
            }
        }
        
        for (int i = 0; i < m_availableTree.getItemCount(); i++) {
            if (isItemExpanded[i]) {
                TreeItem item = m_availableTree.getItem(i);
                Event expandEvent = new Event();
                expandEvent.time = (int)System.currentTimeMillis();
                expandEvent.type = SWT.Expand;
                expandEvent.widget = m_availableTree;
                expandEvent.item = item;
                m_availableTree.notifyListeners(SWT.Expand, expandEvent);
                item.setExpanded(true);
                m_availableTree.update();
            }
        }
    }
    
    /**
     * Use the GUI object represented by the given item.
     * @param item the item that represents the object to use.
     */
    private void useObject(TreeItem item) {
        IChooserCompositeGuiObject guiObj = m_treeItemsToGuiObjects.get(item);
        
        String displayString = guiObj.getDisplayString();
        m_listItemsToGuiObjects.put(displayString, guiObj);
        m_usedList.add(displayString);
        item.dispose();

        m_usedParents.add(guiObj.getParent());
        
        sortListItems();
        
    }
    
    /**
     * Make the given object available.
     * @param obj the object to make available.
     * @param swapping Flag to indicate that a 'swap' is currently in progress. 
     */
    private void makeObjectAvailable(IChooserCompositeGuiObject obj, 
        boolean swapping) {
        
        m_listItemsToGuiObjects.remove(obj.getDisplayString());
        if (!swapping) {
            m_usedParents.remove(obj.getParent());
        }
        m_usedList.remove(obj.getDisplayString());
        
        if (obj.getParent() != null) {
            
            TreeItem parentItem = null;
            String parentText = obj.getParent();
            for (TreeItem parent : m_availableTree.getItems()) {
                if (parent.getText().equals(parentText)) {
                    parentItem = parent;
                    break;
                }
            }
            if (parentItem != null) {
                TreeItem item = new TreeItem(parentItem, SWT.NONE);
                item.setText(obj.getTitle());
                m_treeItemsToGuiObjects.put(item, obj);
                sortTreeItems();
            }
        }

    }

    /**
     * Informs all listeners that the list has been modified.
     */
    private void fireListModified() {
        for (IUsedListModifiedListener listener : m_listeners) {
            listener.usedListModified(m_usedList.getItems());
        }
    }

    /**
     * Creates a label for this composite.
     * @param text The label text to set.
     * @param parent The composite.
     * @return a new label
     */
    private Label createLabel(Composite parent, String text) {
        Label label = new Label(parent, SWT.NONE);
        label.setText(text);
        GridData labelGrid = new GridData(GridData.BEGINNING, GridData.CENTER, 
            false, false, 1, 1);
        label.setLayoutData(labelGrid);
        return label;
    }
    
    /**
     * Creates a new composite.
     * @param parent The parent composite.
     * @param numColumns the number of columns for this composite.
     * @param alignment The horizontalAlignment.
     * @param horizontalSpace The horizontalSpace.
     * @return The new composite.
     */
    private Composite createComposite(Composite parent, int numColumns, 
            int alignment, boolean horizontalSpace) {
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout();
        compositeLayout.numColumns = numColumns;
        compositeLayout.marginHeight = 0;
        compositeLayout.marginWidth = 0;
        composite.setLayout(compositeLayout);
        GridData compositeData = new GridData();
        compositeData.horizontalAlignment = alignment;
        compositeData.grabExcessHorizontalSpace = horizontalSpace;
        composite.setLayoutData(compositeData);
        return composite;       
    }
    
    /**
     * Creates a new multiline textfield
     * @param composite The parent composite.
     * @param labelText The text for the label.
     * @param lines The quantity of lines of this list.
     * @return The new multiline textfield.
     */
    private List createListField(Composite composite, 
        String labelText, int lines) {
        
        Composite leftComposite = createComposite(composite, NUM_COLUMNS_2, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(composite, NUM_COLUMNS_1, 
            GridData.FILL, true);
        Label label = createLabel(leftComposite, labelText);
        List listField = 
            new List(rightComposite, LayoutUtil.MULTI_TEXT_STYLE);
        listField.setData(LABEL, label);
        GridData listGridData = new GridData();
        listGridData.horizontalAlignment = GridData.FILL;
        listGridData.horizontalSpan = NUM_COLUMNS_1;
        listGridData.grabExcessHorizontalSpace = true;
        listGridData.heightHint = Dialog.convertHeightInCharsToPixels(
            LayoutUtil.getFontMetrics(listField), lines);
        listField.setLayoutData(listGridData);
        return listField;
    }
    
    /**
     * Creates a new tree
     * @param composite The parent composite.
     * @param labelText The text for the label.
     * @param lines The quantity of lines of this list.
     * @return The new tree.
     */
    private Tree createAvailableTree(Composite composite, 
        String labelText, int lines) {
        
        Composite leftComposite = createComposite(composite, NUM_COLUMNS_2, 
            GridData.BEGINNING, false);
        Composite rightComposite = createComposite(composite, NUM_COLUMNS_1, 
            GridData.FILL, true);
        Label label = createLabel(leftComposite, labelText);
        Tree tree = 
            new Tree(rightComposite, LayoutUtil.MULTI_TEXT_STYLE);
        tree.setData(LABEL, label);
        GridData listGridData = new GridData();
        listGridData.horizontalAlignment = GridData.FILL;
        listGridData.horizontalSpan = NUM_COLUMNS_1;
        listGridData.grabExcessHorizontalSpace = true;
        listGridData.heightHint = Dialog.convertHeightInCharsToPixels(
            LayoutUtil.getFontMetrics(tree), lines);
        tree.setLayoutData(listGridData);
        return tree;
    }

    /**
     * @param key The key string
     * @param value The value string
     * @return a <code>String</code> that represents the combination of 
     *         <code>key</code> and <code>value</code>
     */
    protected String getDisplayValue(String key, String value) {
        String displayValue = key + DISPLAY_VALUE_START + value 
            + DISPLAY_VALUE_END;  
        return displayValue;
    }
    
    /**
     * Handles the selectionEvent of the selectionOneToTwoButton.
     */
    protected void handleSelectionAvailableToUsedButtonEvent() {
        if (m_selectionAvailableToUsedButton.getEnabled()) {
            for (TreeItem item : m_availableTree.getSelection()) {
                useObject(item);
            }
            
            sortListItems();
            fireListModified();
            checkButtons();
        }
    }
    
    /**
     * Handles the selectionEvent of the selectionTwoToOneButton.
     */
    protected void handleSelectionUsedToAvailableButtonEvent() {
        if (m_selectionUsedToAvailableButton.getEnabled()) {
            String [] usedSelection = m_usedList.getSelection();
    
            if (checkSelectionUsedToAvailable(usedSelection) == null) {
                for (String sel : m_usedList.getSelection()) {
                    IChooserCompositeGuiObject guiObj = 
                        m_listItemsToGuiObjects.get(sel);
                    makeObjectAvailable(guiObj, false);
                }
                
                checkButtons();
                fireListModified();
    
            } else {
                Dialog dialog = ErrorHandlingUtil.createMessageDialog(
                    MessageIDs.I_COULD_NOT_REMOVE_REUSED_PROJECTS);
                dialog.getReturnCode();
            }
        }
    }
    
    /**
     * @param selection The array of selected elements.
     * @return An error message <code>String</code> if not all elements can be removed.
     *         Otherwise <code>null</code>.
     */
    protected String checkSelectionUsedToAvailable(String [] selection) {
        return null;
    }

    /**
     * Handles the selectionEvent of the allOneToTwoButton.
     */
    protected void handleAllAvailableToUsedButtonEvent() {
        for (TreeItem keyItem : m_availableTree.getItems()) {
            // Should only be enabled if each key item has one and only one child item
            useObject(keyItem.getItem(0));
        }

        String[] selection = m_usedList.getItems();
        Arrays.sort(selection);
        m_usedList.removeAll();
        m_usedList.setItems(selection);
        fireListModified();
        checkButtons();
    }
    
    /**
     * Handles the selectionEvent of the allTwoToOneButton.
     */
    protected void handleAllUsedToAvailableButtonEvent() {

        String [] usedItems = m_usedList.getItems();
        String errorMsg = checkSelectionUsedToAvailable(usedItems);
        if (errorMsg == null) {
            for (String listItem : usedItems) {
                makeObjectAvailable(
                    m_listItemsToGuiObjects.get(listItem), false);
            }
            m_usedList.removeAll();
            fireListModified();
            checkButtons();
        } else {
            Dialog dialog = ErrorHandlingUtil.createMessageDialog(
                MessageIDs.I_COULD_NOT_REMOVE_REUSED_PROJECTS);
            dialog.getReturnCode();
        }
    }
        
    /**
     * Handles the selectionEvent of the allTwoToOneButton.
     */
    protected void handleSwapButtonEvent() {
        String usedSelection = m_usedList.getSelection()[0];
        TreeItem availableSelection = m_availableTree.getSelection()[0];
        
        useObject(availableSelection);
        makeObjectAvailable(m_listItemsToGuiObjects.get(usedSelection), true);
        
        fireListModified();
        checkButtons();
    }

    /**
     * Dis-/Enables the UP-/Down-/SwapButtons.
     */
    public void checkButtons() {
        // allOneToTwo: no parent item has more than one element and at least one parent has one element
        boolean parentHasMoreThanOneElement = false;
        boolean parentsHaveNoElements = true;
        boolean enableAllOneToTwo = true;
        // allTwoToOne: list contains at least one element
        boolean listContainsAnElement, enableAllTwoToOne = false;
        // selectionOneToTwo: some selection and no selection is a parent item and none of selections' parents are already somehow in list
        //                    and no selected items share a parent
        boolean someSelectionOne, selectionContainsParentItem = false;
        boolean partOfSelectionIsInList = false; 
        boolean availableSelectionShareParent = false;
        boolean enableSelectionOneToTwo = true;
        // selectionTwoToOne: some selection
        boolean someSelectionTwo = false;
        boolean enableSelectionTwoToOne = true;
        // swap: one element selected for each and both selections share a common parent
        boolean oneElementSelectedOne, oneElementSelectedTwo = false;
        boolean elementsShareCommonParent = false;
        boolean enableSwap = true;
        parentHasMoreThanOneElement = hasMultipleChildren();

        enableAllOneToTwo = !parentHasMoreThanOneElement 
            && !parentsHaveNoElements;
        
        listContainsAnElement = m_usedList.getItemCount() > 0;
        enableAllTwoToOne = listContainsAnElement;
        
        TreeItem[] treeSelection = m_availableTree.getSelection();
        someSelectionOne = treeSelection.length > 0;
        Set<String> parentsOfSelection = new HashSet<String>();
        for (TreeItem selItem : treeSelection) {
            if (selItem.getParentItem() == null) {
                selectionContainsParentItem = true;
                break;
            }
            
            String parent = m_treeItemsToGuiObjects.get(selItem).getParent();
            if (!parentsOfSelection.contains(parent)) {
                parentsOfSelection.add(parent);
            } else {
                availableSelectionShareParent = true;
                break;
            }
            
            
            if (isChildItemUsed(selItem.getParentItem().getText())) {
                
                partOfSelectionIsInList = true;
                break;
            }
        }
        
        enableSelectionOneToTwo = someSelectionOne 
            && !selectionContainsParentItem
            && !partOfSelectionIsInList
            && !availableSelectionShareParent;
        
        someSelectionTwo = m_usedList.getSelectionCount() > 0;
        enableSelectionTwoToOne = someSelectionTwo;
        
        oneElementSelectedOne = m_availableTree.getSelectionCount() == 1;
        oneElementSelectedTwo = m_usedList.getSelectionCount() == 1;
        if (oneElementSelectedOne && oneElementSelectedTwo) {
            elementsShareCommonParent = 
                isCommonParentForElements(selectionContainsParentItem);
        }
        enableSwap = elementsShareCommonParent;
        
        enableSelectionOneToTwoButton(enableSelectionOneToTwo);
        enableSelectionTwoToOneButton(enableSelectionTwoToOne);
        enableAllOneToTwoButton(enableAllOneToTwo);
        enableAllTwoToOneButton(enableAllTwoToOne);
        enableSwapButton(enableSwap);
    }

    /**
     * @param selectionContainsParentItem does the current selection contain a
     *                                    parent item
     * @return <code>true</code> if the selected elements have the same parent.
     *         Otherwise, <code>false</code>.
     */
    private boolean isCommonParentForElements(
            boolean selectionContainsParentItem) {
        
        boolean elementsShareCommonParent;
        IChooserCompositeGuiObject availableObj = 
            m_treeItemsToGuiObjects.get(m_availableTree.getSelection()[0]);
        IChooserCompositeGuiObject usedObj = 
            m_listItemsToGuiObjects.get(m_usedList.getSelection()[0]);
        elementsShareCommonParent = !selectionContainsParentItem 
            && availableObj != null && usedObj != null
            && availableObj.getParent().equals(usedObj.getParent());
        return elementsShareCommonParent;
    }

    /**
     * @return <code>true</code> if any GUI objects share a parent. Otherwise,
     *         <code>false</code>.
     */
    private boolean hasMultipleChildren() {
        Set<IChooserCompositeGuiObject> guiObjects = 
            new HashSet<IChooserCompositeGuiObject>(
                m_treeItemsToGuiObjects.values());
        guiObjects.addAll(m_listItemsToGuiObjects.values());
        Set<String> parentSet = new HashSet<String>();
        
        for (IChooserCompositeGuiObject obj : guiObjects) {
            String parentString = obj.getParent();
            if (!parentSet.contains(parentString)) {
                parentSet.add(parentString);
            } else {
                return true;
            }
        }
        return false;
    }

    /**
     * @param text the parent text.
     * @return <code>true</code> if a used GUI object has the given text as 
     *         parent. Otherwise, <code>false</code>.
     */
    private boolean isChildItemUsed(String text) {
        return m_usedParents.contains(text);
    }

    /**
     * 
     * @param enable <code>true</code> if the button should be enabled. <code>false</code>
     *               if the button should be disabled.
     */
    private void enableSelectionOneToTwoButton(boolean enable) {
        if (enable) {
            m_selectionAvailableToUsedButton.setImage(
                (Image)m_buttonContents[0]);
            m_selectionAvailableToUsedButton.setEnabled(true);
        } else {
            m_selectionAvailableToUsedButton.setImage(
                (Image)m_disabledButtonContents[0]);
            m_selectionAvailableToUsedButton.setEnabled(false);
        }
    }
    
    /**
     * 
     * @param enable <code>true</code> if the button should be enabled. <code>false</code>
     *               if the button should be disabled.
     */
    private void enableSelectionTwoToOneButton(boolean enable) {
        if (enable) {
            m_selectionUsedToAvailableButton.setImage(
                (Image)m_buttonContents[2]);
            m_selectionUsedToAvailableButton.setEnabled(true);
        } else {
            m_selectionUsedToAvailableButton.setImage(
                (Image)m_disabledButtonContents[2]);
            m_selectionUsedToAvailableButton.setEnabled(false);
        }
    }
    
    /**
     * 
     * @param enable <code>true</code> if the button should be enabled. <code>false</code>
     *               if the button should be disabled.
     */
    private void enableAllOneToTwoButton(boolean enable) {
        if (enable) {
            m_allAvailableToUsedButton.setImage(
                (Image)m_buttonContents[1]);
            m_allAvailableToUsedButton.setEnabled(true);
        } else {
            m_allAvailableToUsedButton.setImage(
                (Image)m_disabledButtonContents[1]);
            m_allAvailableToUsedButton.setEnabled(false);
        }
    }

    /**
     * 
     * @param enable <code>true</code> if the button should be enabled. <code>false</code>
     *               if the button should be disabled.
     */
    private void enableAllTwoToOneButton(boolean enable) {
        if (enable) {
            m_allUsedToAvailableButton.setImage(
                (Image)m_buttonContents[3]);
            m_allUsedToAvailableButton.setEnabled(true);
        } else {
            m_allUsedToAvailableButton.setImage(
                (Image)m_disabledButtonContents[3]);
            m_allUsedToAvailableButton.setEnabled(false);
        }
    }

    /**
     * 
     * @param enable <code>true</code> if the button should be enabled. <code>false</code>
     *               if the button should be disabled.
     */
    private void enableSwapButton(boolean enable) {
        if (enable) {
            m_swapButton.setImage(
                (Image)m_buttonContents[4]);
            m_swapButton.setEnabled(true);
        } else {
            m_swapButton.setImage(
                (Image)m_disabledButtonContents[4]);
            m_swapButton.setEnabled(false);
        }
    }
/**
     * Disposes the SWT widgets.
     */
    public void dispose() {
        removeListener();
        super.dispose();
    }
    
    /**
     * 
     * @param listener The listener to add.
     */
    public void addListModifiedListener(IUsedListModifiedListener listener) {
        m_listeners.add(listener);
    }
    
    /**
     * 
     * @param listener The listener to remove.
     */
    public void removeListModifiedListener(IUsedListModifiedListener listener) {
        m_listeners.remove(listener);
    }

    /**
     * Adds necessary listeners.
     */
    private void addListener() {       
        m_selectionUsedToAvailableButton.addSelectionListener(
            m_selectionListener);
        m_selectionAvailableToUsedButton.addSelectionListener(
            m_selectionListener);
        m_allAvailableToUsedButton.addSelectionListener(m_selectionListener);
        m_allUsedToAvailableButton.addSelectionListener(m_selectionListener);
        m_swapButton.addSelectionListener(m_selectionListener);
    }
    
    /**
     * Removes all listeners.
     */
    private void removeListener() {
        m_selectionAvailableToUsedButton.removeSelectionListener(
            m_selectionListener);
        m_selectionUsedToAvailableButton.removeSelectionListener(
            m_selectionListener);
        m_allAvailableToUsedButton.removeSelectionListener(m_selectionListener);
        m_allUsedToAvailableButton.removeSelectionListener(m_selectionListener);
        m_availableTree.removeSelectionListener(m_selectionListener);
        m_usedList.removeSelectionListener(m_selectionListener);
    }
    
    /**
     * The object from the model that can be chosen/unchosen.
     * 
     * @author BREDEX GmbH
     * @created Aug 16, 2007
     */
    public interface IChooserCompositeGuiObject {
        
        /**
         * Indicates the name of the tree item that will be the receiver's
         * parent when it is available.
         * 
         * @return the name of the parent when the receiver is available.
         */
        public String getParent();

        /**
         * Indicates the displayed title of the object when it is available.
         * 
         * @return the title that represents the receiver when it is available.
         */
        public String getTitle();
        
        /**
         * Indicates the displayed title of the object when it is used.
         * 
         * @return the title that represents the receiver when it is used.
         */
        public String getDisplayString();

        /**
         * @return the model object represented by the receiver.
         */
        public Object getModelObject();
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     * @created 10.02.2005
     */
    @SuppressWarnings("synthetic-access")
    private class WidgetSelectionListener implements SelectionListener {

        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_selectionAvailableToUsedButton)) {
                handleSelectionAvailableToUsedButtonEvent();
                return;
            } else if (o.equals(m_selectionUsedToAvailableButton)) {
                handleSelectionUsedToAvailableButtonEvent();
                return;
            } else if (o.equals(m_allAvailableToUsedButton)) {
                handleAllAvailableToUsedButtonEvent();
                return;
            } else if (o.equals(m_allUsedToAvailableButton)) {
                handleAllUsedToAvailableButtonEvent();
                return;
            } else if (o.equals(m_swapButton)) {
                handleSwapButtonEvent();
                return;
            } else if (o.equals(m_availableTree)) {
                checkButtons();
                return;
            } else if (o.equals(m_usedList)) {
                checkButtons();
                return;
            } 
            Assert.notReached(Messages.EventActivatedByUnknownWidget 
                    + StringConstants.DOT);
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) { 
            Object o = e.getSource();
            if (o.equals(m_availableTree)) {
                handleSelectionAvailableToUsedButtonEvent();
                return;
            } else if (o.equals(m_usedList)) {
                handleSelectionUsedToAvailableButtonEvent();
                return;
            } 
            Assert.notReached(Messages.EventActivatedByUnknownWidget 
                    + StringConstants.DOT);
        }        
    }
    /**
     * @return Returns the allOneToTwoButton.
     */
    public Button getAllAvailableToUsedButton() {
        return m_allAvailableToUsedButton;
    }
    /**
     * @return Returns the allTwoToOneButton.
     */
    public Button getAllUsedToAvailableButton() {
        return m_allUsedToAvailableButton;
    }
    /**
     * @return Returns the listOne.
     */
    public Tree getAvailableTree() {
        return m_availableTree;
    }
    /**
     * @return Returns the listTwo.
     */
    public List getUsedList() {
        return m_usedList;
    }
    /**
     * @return Returns the selectionOneToTwoButton.
     */
    public Button getSelectionAvailableToUsedButton() {
        return m_selectionAvailableToUsedButton;
    }
    /**
     * @return Returns the selectionTwoToOneButton.
     */
    public Button getSelectionUsedToAvailableButton() {
        return m_selectionUsedToAvailableButton;
    }
    
    /**
     * @return Returns the label of ListBox one.
     */
    public Label getListOneLabel() {
        return (Label)m_availableTree.getData(LABEL);
    }
    
    /**
     * @return Returns the label of ListBox two.
     */
    public Label getListTwoLabel() {
        return (Label)m_usedList.getData(LABEL);
    }

}
