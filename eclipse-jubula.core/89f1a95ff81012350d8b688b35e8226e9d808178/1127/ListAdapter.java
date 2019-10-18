/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListComponent;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.List;
/**
 * @author BREDEX GmbH
 */
public class ListAdapter extends ControlAdapter implements IListComponent {
    /** the list */
    private List m_list;
    
    /**
     * @param objectToAdapt -
     */
    public ListAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_list = (List) objectToAdapt;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        String[] selected = getSelectedValues();
        SelectionUtil.validateSelection(selected);
        return selected[0];
    }
    
    /**
     * {@inheritDoc}
     */
    public int[] getSelectedIndices() {
        return getEventThreadQueuer().invokeAndWait(
                "getSelectedIndices", new IRunnable<int[]>() { //$NON-NLS-1$
                    public int[] run() {
                        return m_list.getSelectionIndices();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public void clickOnIndex(Integer i, ClickOptions co) {
        final int iVal = i.intValue();
        scrollIndexToVisible(iVal);
        
        final Rectangle clickConstraints = getEventThreadQueuer().invokeAndWait(
                "setClickConstraints",  //$NON-NLS-1$
                new IRunnable<Rectangle>() {

                    public Rectangle run() throws StepExecutionException {
                        Rectangle constraints = new Rectangle(0, 0, 0, 0);
                        int displayedItemCount = getDisplayedItemCount();
                        int numberBelowTop = 0;
                        if (displayedItemCount >= m_list.getItemCount()) {
                            numberBelowTop = iVal;
                        } else {
                            numberBelowTop = Math.max(0, iVal 
                                - m_list.getItemCount() + displayedItemCount);
                        }
                        
                        // Set the constraints based on the numberBelowTop
                        constraints.height = m_list.getItemHeight();
                        constraints.width = m_list.getBounds().width;
                        constraints.y += (numberBelowTop * constraints.height);
                        // explicitly use list relative bounds here - as e.g. on
                        // Mac OS systems list.getClientArea() is not relative
                        // see bug http://eclip.se/353905
                        Rectangle actualListBounds =
                            new Rectangle(0, 0, m_list.getClientArea().width, 
                                    m_list.getClientArea().height);
                        return constraints.intersection(actualListBounds);
                    }
                });
        
        // Note that we set scrollToVisible false because we have already done
        // the scrolling.
        getRobot().click(m_list, clickConstraints, 
                co.setScrollToVisible(false));

    }

    /**
     * {@inheritDoc}
     */
    public String[] getSelectedValues() {
        return getEventThreadQueuer().invokeAndWait(
                "getSelectedValues", new IRunnable<String[]>() { //$NON-NLS-1$
                    public String[] run() {
                        return m_list.getSelection();
                    }
                });
    }

    /**
     * @return the number of items displayed in the list.
     */
    private int getDisplayedItemCount() {
        return getEventThreadQueuer().invokeAndWait("getDisplayedItemCount", //$NON-NLS-1$
            new IRunnable<Integer>() {
                public Integer run() throws StepExecutionException {
                    int listHeight = SwtUtils.getWidgetBounds(m_list).height;
                    int itemHeight = m_list.getItemHeight();
                    return listHeight / itemHeight;
                }
            });
    }
    
    /**
     * @param index
     *            The index to make visible
     */
    private void scrollIndexToVisible(final int index) {
        getEventThreadQueuer().invokeAndWait("scrollIndexToVisible", //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() throws StepExecutionException {
                        m_list.setTopIndex(index);
                        return null;
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        return getEventThreadQueuer().invokeAndWait("findIndices", //$NON-NLS-1$
                new IRunnable<String[]>() {
                    public String[] run() {

                        final int listItemCount = m_list.getItemCount();
                        String[] values = new String[m_list.getItemCount()];
                        for (int i = 0; i < listItemCount; i++) {
                            values[i] = m_list.getItem(i);
                        }
                        return values;
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, Object cell) {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }
}
