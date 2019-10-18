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

import java.util.Arrays;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.LayoutUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;



/**
 * Creates a new composite, with two listBoxes. You can shift the content between the two ListBoxes.
 *
 * @author BREDEX GmbH
 * @created 08.07.2005
 */
public class ListElementChooserComposite extends Composite {
    
    /** horizontal style */
    public static final int HORIZONTAL = 0;
    /** vertical style */
    public static final int VERTICAL = 1;
    /***/
    private static final String LABEL = "Label"; //$NON-NLS-1$
    /** number of columns = 1 */
    private static final int NUM_COLUMNS_1 = 1; 
    /** number of columns = 2 */
    private static final int NUM_COLUMNS_2 = 2;  
    /** number of columns = 3 */
    private static final int NUM_COLUMNS_3 = 3; 
    
    /** the list field for the available languages */ 
    private List m_listOne;
    
    /** the list field for the project languages */ 
    private List m_listTwo;
    
    /** the button to shift a selection from List One to List Two  */
    private Button m_selectionOneToTwoButton;
    /** the button to shift all from List One to List Two  */
    private Button m_allOneToTwoButton;
    
    /** the button to shift a selection from List Two to List One  */
    private Button m_selectionTwoToOneButton;
    /** the button to shift all from List Two to List One  */
    private Button m_allTwoToOneButton;
    /** selection of listBox one */
    private String[] m_listOneSelection;
    /** selection of listBox two */
    private String[] m_listTwoSelection;
    /** disabled button content array */
    private Object[] m_disabledButtonContents;
    /** button content array */
    private Object[] m_buttonContents;

    /** the StateController */
    private final WidgetSelectionListener m_selectionListener = 
        new WidgetSelectionListener();

    
    /**
     * Composite, with two listBoxes. You can shift the content between the two ListBoxes.
     * @param parent The parent composite.
     * @param listOneLabel Label text of the first ListBox.
     * @param listOneList Content (=List) for the first ListBox.
     * @param listTwoLabel Label text of the second ListBox.
     * @param listTwoList Content (=List) for the second ListBox.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonTexts The texts of the 4 buttons (example: ">",">>","<","<<")
     * @param buttonToolTips The texts of the toolTips of 4 buttons.
     * @param style <p>ListElementChooserComposite.HORIZONTAL (ListBoxes are side by side) or</p>
     * <p>ListElementChooserComposite.VERTICAL (ListBoxe one is below ListBox two)</p>
     */
    public ListElementChooserComposite(Composite parent, String listOneLabel, 
        java.util.List listOneList, String listTwoLabel, 
        java.util.List listTwoList, int lineNumber, String[] buttonTexts, 
        String[] buttonToolTips, int style) {
        
        super(parent, SWT.NONE);
        m_disabledButtonContents = buttonTexts;
        m_buttonContents = buttonTexts;
        createControl(listOneLabel, listOneList, listTwoLabel, 
            listTwoList, lineNumber, buttonToolTips, style);
    }
    
    /**
     * @param parent The parent composite.
     * @param listOneLabel Label text of the first ListBox.
     * @param listOneList Content (=List) for the first ListBox.
     * @param listTwoLabel Label text of the second ListBox.
     * @param listTwoList Content (=List) for the second ListBox.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonImages The images of the 4 buttons.
     * @param disabledButtonImages The disabled images of the 4 buttons.
     * @param buttonToolTips The texts of the toolTips of 4 buttons.
     * @param style <p>ListElementChooserComposite.HORIZONTAL (ListBoxes are side by side) or</p>
     * <p>ListElementChooserComposite.VERTICAL (ListBoxe one is below ListBox two)</p>
     */
    public ListElementChooserComposite(Composite parent, String listOneLabel, 
            java.util.List listOneList, String listTwoLabel, 
            java.util.List listTwoList, int lineNumber, Image[] buttonImages, 
            Image[] disabledButtonImages, String[] buttonToolTips, int style) {
        
        super(parent, SWT.NONE);
        m_disabledButtonContents = disabledButtonImages;
        m_buttonContents = buttonImages;
        createControl(listOneLabel, listOneList, listTwoLabel,
            listTwoList, lineNumber, buttonToolTips, style);
    }

    /**
     * Creates the ListElementChooserComposite.
     * @param listOneLabel Label text of the first ListBox.
     * @param listOneList Content (=List) for the first ListBox.
     * @param listTwoLabel Label text of the second ListBox.
     * @param listTwoList Content (=List) for the first ListBox.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonToolTips The texts of the toolTips of 4 buttons.
     * @param style ListElementChooserComposite.HORIZONTAL or ListElementChooserComposite.VERTICAL 
     */
    private void createControl(String listOneLabel, 
        java.util.List listOneList,  String listTwoLabel, 
        java.util.List listTwoList, int lineNumber, String[] buttonToolTips, 
        int style) {

        
        if (style == HORIZONTAL) {
            createHorizontalLayout(listOneLabel, listTwoLabel, 
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
            createVerticalLayout(composite, listOneLabel, listTwoLabel, 
                lineNumber, buttonToolTips, style);
        }        
        initFields(listOneList, listTwoList);
        addListener();       
        checkButtons();        
    }

    /**
     * Creates the layout with Vertical alignment.
     * @param parent The parent composite.
     * @param listOneLabel Label text of the first ListBox.
     * @param listTwoLabel Label text of the second ListBox.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonToolTips The texts of the toolTips of 4 buttons.
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
        
        m_listOne = createListField(compositeLeft, listOneLabel, 
            lineNumber); 
        m_listOne.addSelectionListener(m_selectionListener);
        createShiftButtons(style, compositeMiddle, buttonToolTips);
        m_listTwo = createListField(compositeRight, listTwoLabel, 
            lineNumber);
        m_listTwo.addSelectionListener(m_selectionListener);
    }

    /**
     * Creates the layout with Horizontal alignment.
     * @param listOneLabel Label text of the first ListBox.
     * @param listTwoLabel Label text of the second ListBox.
     * @param lineNumber The number of lines of both ListBoxes.
     * @param buttonToolTips The texts of the toolTips of 4 buttons.
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
        
        m_listOne = createListField(composite, listOneLabel, 
            lineNumber); 
        m_listOne.addSelectionListener(m_selectionListener);
        createShiftButtons(style, composite, buttonToolTips);
        m_listTwo = createListField(composite, listTwoLabel, 
            lineNumber);
        m_listTwo.addSelectionListener(m_selectionListener);
    }

    /**
     * Inits all swt field in this page.
     * @param listOneList The content of the forst ListBox.
     * @param listTwoList The content of the forst ListBox.
     */
    protected void initFields(java.util.List listOneList, 
        java.util.List listTwoList) {
        
        fillLists(listOneList, listTwoList);
    }
    
    /**
     * Creates the 4 arrow buttons
     * @param style ListElementChooserComposite.HORIZONTAL or ListElementChooserComposite.VERTICAL 
     * @param parent The parent composite.
     * @param buttonToolTips The texts of the toolTips of 4 buttons. 
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
            m_selectionOneToTwoButton = new Button(leftComposite, SWT.PUSH);
            m_allOneToTwoButton = new Button(leftComposite, SWT.PUSH);
            m_selectionTwoToOneButton = new Button(rightComposite, SWT.PUSH);
            m_allTwoToOneButton = new Button(rightComposite, SWT.PUSH);
        } else {
            createLabel(parent, StringConstants.EMPTY);
            m_selectionOneToTwoButton = new Button(parent, SWT.PUSH);
            m_allOneToTwoButton = new Button(parent, SWT.PUSH);
            createLabel(parent, StringConstants.EMPTY);
            m_selectionTwoToOneButton = new Button(parent, SWT.PUSH);
            m_allTwoToOneButton = new Button(parent, SWT.PUSH);
        }
        GridData selectionOneToTwoGridData = new GridData();
        selectionOneToTwoGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            selectionOneToTwoGridData.horizontalAlignment = GridData.END;
        }
        selectionOneToTwoGridData.grabExcessHorizontalSpace = true;
        m_selectionOneToTwoButton.setLayoutData(selectionOneToTwoGridData);
        m_selectionOneToTwoButton.setImage((Image)m_disabledButtonContents[0]);
        m_selectionOneToTwoButton.setEnabled(false);
        GridData allOneToTwoGridData = new GridData();
        allOneToTwoGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            allOneToTwoGridData.horizontalAlignment = GridData.BEGINNING;
        }
        allOneToTwoGridData.grabExcessHorizontalSpace = true;
        m_allOneToTwoButton.setLayoutData(allOneToTwoGridData);
        m_allOneToTwoButton.setImage((Image)m_disabledButtonContents[1]);
        m_allOneToTwoButton.setEnabled(false);
        GridData selectionTwoToOneGridData = new GridData();
        selectionTwoToOneGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            selectionTwoToOneGridData.horizontalAlignment = GridData.END;
        }
        selectionTwoToOneGridData.grabExcessHorizontalSpace = true;
        m_selectionTwoToOneButton.setLayoutData(selectionTwoToOneGridData);
        m_selectionTwoToOneButton.setImage((Image)m_disabledButtonContents[2]);
        m_selectionTwoToOneButton.setEnabled(false);
        GridData allTwoToOneGridData = new GridData();
        allTwoToOneGridData.horizontalAlignment = GridData.FILL;
        if (style == HORIZONTAL) {
            allTwoToOneGridData.horizontalAlignment = GridData.BEGINNING;
        }
        allTwoToOneGridData.grabExcessHorizontalSpace = true;
        m_allTwoToOneButton.setLayoutData(allTwoToOneGridData);
        m_allTwoToOneButton.setImage((Image)m_disabledButtonContents[3]);
        m_allTwoToOneButton.setEnabled(false);
        if (m_buttonContents instanceof Image[]) {
            m_selectionOneToTwoButton.setImage((Image)m_buttonContents[0]);
            m_allOneToTwoButton.setImage((Image)m_buttonContents[1]);
            m_selectionTwoToOneButton.setImage((Image)m_buttonContents[2]);
            m_allTwoToOneButton.setImage((Image)m_buttonContents[3]);
        } else {
            m_selectionOneToTwoButton.setText((String)m_buttonContents[0]);
            m_allOneToTwoButton.setText((String)m_buttonContents[1]);
            m_selectionTwoToOneButton.setText((String)m_buttonContents[2]);
            m_allTwoToOneButton.setText((String)m_buttonContents[3]);
        }
        m_selectionOneToTwoButton.setToolTipText(buttonToolTips[0]);
        m_allOneToTwoButton.setToolTipText(buttonToolTips[1]);
        m_selectionTwoToOneButton.setToolTipText(buttonToolTips[2]);
        m_allTwoToOneButton.setToolTipText(buttonToolTips[3]);
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
     * Fills the list of available languages with all isoLanguages
     * and the list of project languages.
     * @param listOneList The content of the first ListBox.
     * @param listTwoList The content of the second ListBox.
     */
    protected void fillLists(java.util.List listOneList, 
        java.util.List listTwoList) {  
        
        Object[] listOne = listOneList.toArray();
        Object[] listTwo = listTwoList.toArray();
        Arrays.sort(listOne);
        Arrays.sort(listTwo);
        for (int i = 0; i < listOne.length; i++) {
            boolean isInListTwo = false;
            for (Object object : listTwo) {
                if (listOne[i].toString().equals(object.toString())) {
                    isInListTwo = true;
                    break;
                }
            }
            if (!isInListTwo) {
                m_listOne.add(listOne[i].toString());
            }
        }
        for (int i = 0; i < listTwo.length; i++) {
            m_listTwo.add(listTwo[i].toString());
        }
    }
    
    /**
     * Handles the selectionEvent of the selectionOneToTwoButton.
     */
    protected void handleSelectionOneToTwoButtonEvent() {
        m_listOneSelection = m_listOne.getSelection();
        String[] selection = m_listOne.getSelection();
        for (int i = 0; i < selection.length; i++) {
            m_listTwo.add(selection[i]);
            m_listOne.remove(selection[i]);
        }
        selection = m_listTwo.getItems();
        Arrays.sort(selection);
        m_listTwo.removeAll();
        m_listTwo.setItems(selection);
        checkButtons();
    }
    
    /**
     * Handles the selectionEvent of the selectionTwoToOneButton.
     */
    protected void handleSelectionTwoToOneButtonEvent() {
        m_listTwoSelection = m_listTwo.getSelection();
        String[] selection = m_listTwo.getSelection();
        for (int i = 0; i < selection.length; i++) {
            m_listOne.add(selection[i]);
            m_listTwo.remove(selection[i]);
        }
        selection = m_listOne.getItems();
        Arrays.sort(selection);
        m_listOne.removeAll();
        m_listOne.setItems(selection);
        checkButtons();
    }
    
    /**
     * Handles the selectionEvent of the allOneToTwoButton.
     */
    protected void handleAllOneToTwoButtonEvent() {
        while (m_listOne.getItemCount() > 0) {
            m_listTwo.add(m_listOne.getItem(0));
            m_listOne.remove(0);
        }
        String[] selection = m_listTwo.getItems();
        Arrays.sort(selection);
        m_listTwo.removeAll();
        m_listTwo.setItems(selection);
        checkButtons();
    }
    
    /**
     * Handles the selectionEvent of the allTwoToOneButton.
     */
    protected void handleAllTwoToOneButtonEvent() {
        while (m_listTwo.getItemCount() > 0) {
            m_listOne.add(m_listTwo.getItem(0));
            m_listTwo.remove(0);
        }
        String[] selection = m_listOne.getItems();
        Arrays.sort(selection);
        m_listOne.removeAll();
        m_listOne.setItems(selection);
        checkButtons();
    }
        
    /**
     * Dis-/Enables the UP-/DownButtons , if the languageLists are empty.
     */
    public void checkButtons() {
        if (m_listOne.getItemCount() == 0) {
            m_selectionOneToTwoButton.setImage(
                    (Image)m_disabledButtonContents[0]);
            m_selectionOneToTwoButton.setEnabled(false);
            m_allOneToTwoButton.setImage(
                    (Image)m_disabledButtonContents[1]);
            m_allOneToTwoButton.setEnabled(false);
        } else {
            m_selectionOneToTwoButton.setImage(
                    (Image)m_buttonContents[0]);
            m_selectionOneToTwoButton.setEnabled(true);
            m_allOneToTwoButton.setImage((Image)m_buttonContents[1]);
            m_allOneToTwoButton.setEnabled(true);
        }
        if (m_listTwo.getItemCount() == 0) {
            m_selectionTwoToOneButton.setImage(
                    (Image)m_disabledButtonContents[2]);
            m_selectionTwoToOneButton.setEnabled(false);  
            m_allTwoToOneButton.setImage((Image)m_disabledButtonContents[3]);
            m_allTwoToOneButton.setEnabled(false);
        } else {
            m_selectionTwoToOneButton.setImage(
                    (Image)m_buttonContents[2]);
            m_selectionTwoToOneButton.setEnabled(true);  
            m_allTwoToOneButton.setImage((Image)m_buttonContents[3]);
            m_allTwoToOneButton.setEnabled(true);
        }
        if (m_listTwo.getSelectionCount() == 0) {
            m_selectionTwoToOneButton.setImage(
                    (Image)m_disabledButtonContents[2]);
            m_selectionTwoToOneButton.setEnabled(false);                
        }
        if (m_listOne.getSelectionCount() == 0) {
            m_selectionOneToTwoButton.setImage(
                    (Image)m_disabledButtonContents[0]);
            m_selectionOneToTwoButton.setEnabled(false);                
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
     * Adds necessary listeners.
     */
    private void addListener() {       
        m_selectionTwoToOneButton.addSelectionListener(m_selectionListener);
        m_selectionOneToTwoButton.addSelectionListener(m_selectionListener);
        m_allOneToTwoButton.addSelectionListener(m_selectionListener);
        m_allTwoToOneButton.addSelectionListener(m_selectionListener);
    }
    
    /**
     * Removes all listeners.
     */
    private void removeListener() {
        m_selectionOneToTwoButton.removeSelectionListener(m_selectionListener);
        m_selectionTwoToOneButton.removeSelectionListener(m_selectionListener);
        m_allOneToTwoButton.removeSelectionListener(m_selectionListener);
        m_allTwoToOneButton.removeSelectionListener(m_selectionListener);
        m_listOne.removeSelectionListener(m_selectionListener);
        m_listTwo.removeSelectionListener(m_selectionListener);
    }
    
    /**
     * This private inner class contains a new SelectionListener.
     * @author BREDEX GmbH
     * @created 10.02.2005
     */
    private class WidgetSelectionListener implements SelectionListener {

        /**
         * {@inheritDoc}
         */
        public void widgetSelected(SelectionEvent e) {
            Object o = e.getSource();
            if (o.equals(m_selectionOneToTwoButton)) {
                handleSelectionOneToTwoButtonEvent();
                return;
            } else if (o.equals(m_selectionTwoToOneButton)) {
                handleSelectionTwoToOneButtonEvent();
                return;
            } else if (o.equals(m_allOneToTwoButton)) {
                handleAllOneToTwoButtonEvent();
                return;
            } else if (o.equals(m_allTwoToOneButton)) {
                handleAllTwoToOneButtonEvent();
                return;
            } else if (o.equals(m_listOne)) {
                checkButtons();
                return;
            } else if (o.equals(m_listTwo)) {
                checkButtons();
                return;
            } 
            Assert.notReached(Messages.EventActivatedByUnknownWidget);
        }

        /**
         * {@inheritDoc}
         */
        public void widgetDefaultSelected(SelectionEvent e) { 
            Object o = e.getSource();
            if (o.equals(m_listOne)) {
                handleSelectionOneToTwoButtonEvent();
                return;
            } else if (o.equals(m_listTwo)) {
                handleSelectionTwoToOneButtonEvent();
                return;
            } 
            Assert.notReached(Messages.EventActivatedByUnknownWidget);
        }        
    }
    /**
     * @return Returns the allOneToTwoButton.
     */
    public Button getAllOneToTwoButton() {
        return m_allOneToTwoButton;
    }
    /**
     * @return Returns the allTwoToOneButton.
     */
    public Button getAllTwoToOneButton() {
        return m_allTwoToOneButton;
    }
    /**
     * @return Returns the listOne.
     */
    public List getListOne() {
        return m_listOne;
    }
    /**
     * @return Returns the listTwo.
     */
    public List getListTwo() {
        return m_listTwo;
    }
    /**
     * @return Returns the selectionOneToTwoButton.
     */
    public Button getSelectionOneToTwoButton() {
        return m_selectionOneToTwoButton;
    }
    /**
     * @return Returns the selectionTwoToOneButton.
     */
    public Button getSelectionTwoToOneButton() {
        return m_selectionTwoToOneButton;
    }
    
    /**
     * @return Returns the label of ListBox one.
     */
    public Label getListOneLabel() {
        return (Label)m_listOne.getData(LABEL);
    }
    
    /**
     * @return Returns the label of ListBox two.
     */
    public Label getListTwoLabel() {
        return (Label)m_listTwo.getData(LABEL);
    }
    /**
     * @return The actual selection of ListBox one.
     */
    public String[] getListOneSelection() {
        return m_listOneSelection;
    }
    /**
     * @return The actual selection of ListBox two.
     */
    public String[] getListTwoSelection() {
        return m_listTwoSelection;
    }
}