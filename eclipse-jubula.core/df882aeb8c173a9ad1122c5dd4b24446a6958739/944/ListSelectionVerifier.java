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
package org.eclipse.jubula.rc.common.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;


/**
 * @author BREDEX GmbH
 * @created 28.08.2007
 */
public class ListSelectionVerifier {
    /**
     * Storage for selection items
     */
    private class SelectionItem {
        /** display value of item */
        private String m_value;
        /** is this item selected */
        private boolean m_selected;
        
        /**
         * set all data
         * @param value display value of item
         * @param selected is this item selected
         */
        public SelectionItem(String value, boolean selected) {
            m_value = value;
            m_selected = selected;
        }

        /**
         * @return the selected
         */
        public boolean isSelected() {
            return m_selected;
        }

        /**
         * @return the value
         */
        public String getValue() {
            return m_value;
        }        
    }
    
    /** list of selection items */
    private List<SelectionItem> m_itemList = new ArrayList<SelectionItem>();
    
    /**
     * Add an item to the list of selection items
     * @param index position in the list
     * @param value text value of item
     * @param isSelected selection state of item
     */
    public void addItem(int index, String value, boolean isSelected) {
        m_itemList.add(index, new SelectionItem(value, isSelected));
    }
    
    /**
     * Verifies that a user supplied pattern is consisted with the currently
     * selected items.
     * 
     * @param patternString
     *            The pattern to match.
     * @param op
     *            the operation, i.e. equals or match
     * @param isSelected The expected selection state.
     * @throws StepExecutionException if <code>patternString</code> is expected 
     *              to match a selected item and does not, or if 
     *              <code>patternString</code> is expected not to match any 
     *              selected item and does. 
     */
    public void verifySelection(String patternString, String op, 
            boolean isSelected) throws StepExecutionException {

        String hit = null;
        for (Iterator<SelectionItem> iter = m_itemList.iterator(); 
                hit == null && iter.hasNext();) {
            SelectionItem item = iter.next();
            String itemValue = item.getValue();
            if (MatchUtil.getInstance().match(itemValue, patternString, op)) {
                hit = itemValue;
            }

        }
        if (hit == null && isSelected) {
            throw new StepExecutionException("No selected list element matches " //$NON-NLS-1$
                + patternString, EventFactory.createVerifyFailed(
                    this.toString(), patternString, op));
        } else if (hit != null && !isSelected) {
            throw new StepExecutionException("Selected list element matches " //$NON-NLS-1$
                    + patternString, EventFactory.createVerifyFailed(
                        hit, patternString, op));
        }

    }

    /**
     * {@inheritDoc}
     */
    public String toString() {
        StringBuffer res = new StringBuffer();
        for (Iterator<SelectionItem> iter = m_itemList.iterator(); iter
                .hasNext();) {
            SelectionItem element = iter.next();
            if (element.isSelected()) {
                res.append(element.getValue());
                res.append(TestDataConstants.VALUE_CHAR_DEFAULT);
            }
        }
        if (res.length() > 0) {
            res.deleteCharAt(res.length() - 1);
        }
        return res.toString();
    }
    
}
