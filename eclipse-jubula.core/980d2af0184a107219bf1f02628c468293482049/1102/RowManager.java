/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * Class used to simulate the JavaFX behaviour of TreeTables in SWT.
 *      That is, JavaFX provides methods to directly address the rows by index.
 *      This index is amongst the visible rows (w.r.t folding).
 * 
 * Note that the Manager assumes that the Tree remains unchanged during the Manager's
 *      life.
 * 
 * @author BREDEX GmbH
 *
 */
public class RowManager {

    /** We always scan this many more rows than requested */
    private static final int EXTRA_ROWS = 20;

    /** The underlying SWT Tree */
    private Tree m_tree;

    /** The cached rows */
    private List<TreeItem> m_cachedRows = new ArrayList<TreeItem>();

    /** Internal flag to indicate that we have already scanned all rows */
    private boolean m_fullScanned;

    /** The TreeItem index we search for */
    private int m_rowIndex;

    /** The stack to set before starting the search */
    private Stack<Object> m_stackToSet;

    /** The row we are looking for */
    private TreeItem m_searchRow = null;

    /**
     * @param tree the underlying SWT Tree
     */
    public RowManager(Tree tree) {
        m_tree = tree;
    }

    /**
     * Returns the row identified by its index
     * @param ind the index
     * @return the row or null if row does not exist
     */
    public TreeItem getRow(int ind) {
        if (ind < 0) {
            return null;
        }
        m_stackToSet = new Stack<Object>();
        if (ind >= m_cachedRows.size() && !m_fullScanned) {
            if (!m_cachedRows.isEmpty()) {
                TreeItem curr = m_cachedRows.get(m_cachedRows.size() - 1);
                while (curr != null) {
                    m_stackToSet.add(curr);
                    curr = curr.getParentItem();
                }
            }
            m_stackToSet.add(m_tree);
            m_rowIndex = ind;
            traverse(m_tree);
        }
        if (ind < m_cachedRows.size()) {
            return m_cachedRows.get(ind);
        }
        return null;
    }

    /**
     * Traverses the Tree staring with a node
     *      Pre-sets the traverse stack to equal m_stackToSet
     *      This is used when extending the row cache
     * @param current the current node
     */
    private void traverse(Object current) {
        TreeItem[] children;
        if (current instanceof Tree) {
            children = ((Tree) current).getItems();
        } else {
            children = ((TreeItem) current).getItems();
        }
        Object targChild = null;
        if (m_stackToSet.isEmpty()) {
            if (current instanceof TreeItem) {
                m_cachedRows.add((TreeItem) current);
                if (current == m_searchRow) {
                    // if we are searching for a row, we have just found it
                    m_rowIndex = -EXTRA_ROWS;
                    return;
                }
            }
        } else {
            m_stackToSet.pop();
            if (!m_stackToSet.isEmpty()) {
                targChild = m_stackToSet.peek();
            }
        }
        if (m_stackToSet.isEmpty() && current instanceof TreeItem
                && !((TreeItem) current).getExpanded()) {
            return;
        }
        int currInd = 0;
        while (currInd < children.length) {
            if (children[currInd] == targChild) {
                targChild = null; 
            }
            if (targChild == null) {
                traverse(children[currInd]);
            }
            if (m_cachedRows.size() > m_rowIndex + EXTRA_ROWS) {
                return;
            }
            currInd++;
        }
        m_fullScanned = true;
    }

    /**
     * @return the number of rows
     */
    public int getRowCount() {
        getRow(Integer.MAX_VALUE - 2 * EXTRA_ROWS); //just in case...
        return m_cachedRows.size();
    }

    /**
     * Returns the index of a row
     * @param row the row
     * @return its index
     */
    public int getRowIndex(TreeItem row) {
        m_searchRow = row;
        m_stackToSet = new Stack<Object>();
        traverse(m_tree);
        int i = 0;
        while (i < m_cachedRows.size() && m_cachedRows.get(i) != row) {
            i++;
        }
        return i;
    }
}