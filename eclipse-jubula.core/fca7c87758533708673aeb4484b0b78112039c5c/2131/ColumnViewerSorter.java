/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tom Schindl - initial API and implementation
 *     BREDEX GmbH - initial documentation and removal of "NONE" sort order 
 *******************************************************************************/
package org.eclipse.jubula.client.ui.views;

import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Table;

/**
 * Helper class for sorting on TableViewerColumns. Taken from 
 * Snippet040TableViewerSorting and modified to pass Checkstyle.
 * 
 * @author Tom Schindl <tom.schindl@bestsolution.at>
 * @author BREDEX GmbH
 */
public abstract class ColumnViewerSorter extends ViewerComparator {

    /** constant for ascending sort order */
    public static final int ASC = 1;

    /** constant for descending sort order */
    public static final int DESC = -1;

    /** current sort direction */
    private int m_direction = 0;

    /** the column on which to sort */
    private TableViewerColumn m_column;

    /** the viewer to sort */
    private ColumnViewer m_viewer;

    /**
     * Constructor
     * 
     * @param viewer The viewer to sort.
     * @param column The column on which to sort.
     */
    public ColumnViewerSorter(ColumnViewer viewer, TableViewerColumn column) {
        m_column = column;
        m_viewer = viewer;
        m_column.getColumn().addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e) {
                if (m_viewer.getComparator() == ColumnViewerSorter.this
                        && m_direction == ASC) {

                    setSorter(DESC);
                } else {
                    setSorter(ASC);
                }
            }
        });
    }

    /**
     * Assigns the receiver as its viewer's sorter.
     * 
     * @param direction
     *            The sort direction to use.
     */
    public void setSorter(int direction) {
        final Table parent = m_column.getColumn().getParent();
        parent.setSortColumn(m_column.getColumn());
        m_direction = direction;

        if (direction == ASC) {
            parent.setSortDirection(SWT.UP);
        } else {
            parent.setSortDirection(SWT.DOWN);
        }

        if (m_viewer.getComparator() == this) {
            m_viewer.refresh();
        } else {
            m_viewer.setComparator(this);
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    public int compare(Viewer viewer, Object e1, Object e2) {
        return m_direction * doCompare(viewer, e1, e2);
    }

    /**
     * Performs the actual comparison.
     * 
     * Returns a negative, zero, or positive number depending on whether
     * the first element is less than, equal to, or greater than
     * the second element.
     * 
     * @param viewer the viewer
     * @param e1 the first element
     * @param e2 the second element
     * @return a negative number if the first element is less  than the 
     *  second element; the value <code>0</code> if the first element is
     *  equal to the second element; and a positive number if the first
     *  element is greater than the second element
     */
    protected abstract int doCompare(Viewer viewer, Object e1, Object e2);
}
