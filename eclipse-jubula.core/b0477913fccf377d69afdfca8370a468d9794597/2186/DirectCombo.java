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
package org.eclipse.jubula.client.ui.widgets;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author BREDEX GmbH
 * @created 07.02.2006
 * 
 * @param <TheObject> the class which entities are the base for the set of choices
 */
public class DirectCombo<TheObject> extends Combo {


    /** List of Objects to display */
    private List<TheObject> m_values;
    
    // display options
    /** 
     * is a null selection allowed? this implies an empty entry
     * in the items.
     */
    private boolean m_nullSelectionAllowed;
    /** optional comparator, may be null if no sort is required */
    private Comparator<String> m_comparator;
    
    /**
     * A Combo which supports Objects as keys and a String as displayable
     * value.
     * 
     * {@inheritDoc}
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param values
     *            Object represented by combobox
     * @param displayValues           
     *            Strings to be display for each value object
     * @param isNullSelectionAllowed
     *            true if the Combo should start with an empty entry to support
     *            null selection
     * @param comparator Sorting criteria for display values. <code>null</code>
     * is allowed. In this case no sorting is done.
     */
    public DirectCombo(Composite parent, int style, List<TheObject> values,
        List<String> displayValues, boolean isNullSelectionAllowed,
        Comparator<String> comparator) {
        super(parent, style | SWT.READ_ONLY);
        
        m_nullSelectionAllowed = isNullSelectionAllowed;
        m_comparator = comparator;
        
        init(values, displayValues, isNullSelectionAllowed, comparator);
    }

    /**
     * A Combo which supports I18N
     * 
     * {@inheritDoc}
     * @param parent {@inheritDoc}
     * @param style {@inheritDoc}
     * @param values
     *            Object represented by combobox
     * @param displayValues           
     *            Strings to be display for each value object
     * @param isNullSelectionAllowed
     *            true if the Combo should start with an empty entry to support
     *            null selection
     * @param sortEntries
     *            Sort the display values by the standard String compareTo()
     *            method.
     */
    public DirectCombo(Composite parent, int style, List<TheObject> values,
        List<String> displayValues, boolean isNullSelectionAllowed,
        boolean sortEntries) {
        this(parent, style, values, displayValues, isNullSelectionAllowed,
            sortEntries ? new Comparator<String>() {
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            } : null);
    }

    /**
     * @param values values
     * @param displayValues displayValues
     * @param isNullSelectionAllowed isNullSelectionAllowed
     * @param comparator comparator
     */
    private void init(List<TheObject> values, List<String> displayValues,
        boolean isNullSelectionAllowed, Comparator<String> comparator) {
        Validate.isTrue(values.size() == displayValues.size(),
            "values and displayValues don't have the same number of items."); //$NON-NLS-1$
        
        List<String> display = displayValues;
        if (comparator == null) { // no sorting requested
            m_values = new ArrayList<TheObject>(values);
            if (isNullSelectionAllowed) {
                m_values.add(0, null);
                display.add(0, StringConstants.EMPTY);
            }
        } else {
            display = sortKeys(values, display, comparator,
                isNullSelectionAllowed);
        }

        removeAll();
        for (String displayValue : display) {
            super.add(displayValue);
        }
        TheObject defaultSelectedObject;
        if (isNullSelectionAllowed) {
            defaultSelectedObject = null;
        } else {
            if (values.size() > 0) {
                defaultSelectedObject = values.get(0);
            } else {
                defaultSelectedObject = null;
            }
        }
        setSelectedObject(defaultSelectedObject);
    }

    /**
     * Sort the keys according to the comparator which is applied to the display
     * values. For performance reasons the display values are generated and 
     * returned, the keys are stored internally.
     * @param values the original objects
     * @param displayValues the Strings to be displayed in the Combo
     * @param comparator how to compare the display string values
     * @param isNullSelectionAllowed add an empty entry at the start of the list?
     * @return a sorted list of display values
     */
    private List<String> sortKeys(List<TheObject> values,
        List<String> displayValues, Comparator<String> comparator,
        boolean isNullSelectionAllowed) {
        SortedMap<String, TheObject> sorter = new TreeMap<String, TheObject>(
            comparator);
        int size = values.size();
        for (int i = 0; i < size; ++i) {
            sorter.put(displayValues.get(i), values.get(i));
        }
        if (isNullSelectionAllowed) {
            size++;
        }
        m_values = new ArrayList<TheObject>(size);
        List<String> result = new ArrayList<String>(size);
        if (isNullSelectionAllowed) {
            m_values.add(null);
            result.add(StringConstants.EMPTY);
        }
        for (Entry<String, TheObject> entry : sorter.entrySet()) {
            m_values.add(entry.getValue());
            result.add(entry.getKey());
        }
        return result;

    }
    /**
     * @param o the Object which the Combo should display
     */
    public void setSelectedObject(TheObject o) {
        int index = m_values.indexOf(o);
        select(index);
    }
    
    /**
     * @return the associated Object for the selected display entry
     */
    public TheObject getSelectedObject() {
        int index = getSelectionIndex();
        if (index == -1) { // nothing selected
            return null;
        }
        return m_values.get(index);
    }

    /**
     * @param values
     *            Object represented by combobox
     * @param displayValues
     *            Strings to be display for each value object
     */            
    public void setItems(List<TheObject> values, List<String> displayValues) {
        init(values, displayValues, m_nullSelectionAllowed, m_comparator);
    }
    /**
     * @return Returns the comparator.
     */
    protected Comparator<String> getComparator() {
        return m_comparator;
    }

    /**
     * @return Returns the nullSelectionAllowed.
     */
    protected boolean isNullSelectionAllowed() {
        return m_nullSelectionAllowed;
    }

    /**
     * @return Returns the values.
     */
    public List<TheObject> getValues() {
        return m_values;
    }

    /**
     * {@inheritDoc}
     */
    protected void checkSubclass() {
        // do nothing, therefore allowing subclassing 
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called.
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void add(String string, int index) {
        super.add(string, index);
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called. 
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void add(String string) {
        super.add(string);
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called. 
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void remove(int start, int end) {
        super.remove(start, end);
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called. 
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void remove(int index) {
        super.remove(index);
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called.
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void remove(String string) {
        super.remove(string);
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called. 
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void setItem(int index, String string) {
        super.setItem(index, string);
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called. 
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void setItems(String[] items) {
        super.setItems(items);
    }

    /**
     * This methods has no meaning for this subclass and must not
     * be called.
     * {@inheritDoc}
     * @deprecated
     */
    @Deprecated
    public void setText(String string) {
        super.setText(string);
    }    
}
