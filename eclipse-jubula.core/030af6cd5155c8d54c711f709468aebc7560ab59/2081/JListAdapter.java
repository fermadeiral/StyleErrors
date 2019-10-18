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
package org.eclipse.jubula.rc.swing.tester.adapter;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListComponent;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.swing.tester.util.TesterUtil;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class JListAdapter extends JComponentAdapter implements IListComponent {
    /** */
    private JList m_list;
    
    /**
     * 
     * @param objectToAdapt 
     */
    public JListAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_list = (JList) objectToAdapt;
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
                        return m_list.getSelectedIndices();
                    }
                });
    }

    /**
     * Clicks on the index of the passed list.
     *
     * @param i The index to click
     * @param co the click options to use
     * @param maxWidth the maximal width which is used to select the item
     */
    public void clickOnIndex(final Integer i,
            ClickOptions co, double maxWidth) {
        final int index = i.intValue();
        final ListModel model = m_list.getModel();
        if ((model == null) || (index >= model.getSize())
            || (index < 0)) {
            throw new StepExecutionException("List index '" + i //$NON-NLS-1$
                + "' is out of range", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.INVALID_INDEX));
        }
        // Call of JList.ensureIndexIsVisible() is not required,
        // because the Robot scrolls the click rectangle to visible.
        final Rectangle r = getRobotFactory().getEventThreadQueuer()
                .invokeAndWait("getCellBounds", new IRunnable<Rectangle>() { //$NON-NLS-1$

                    public Rectangle run() throws StepExecutionException {
                        return m_list.getCellBounds(index, index);
                    }
                });        
        
        if (r == null) {
            throw new StepExecutionException(
                "List index '" + i + "' is not visible", //$NON-NLS-1$ //$NON-NLS-2$
                EventFactory.createActionError(TestErrorEvent.NOT_VISIBLE));
        }
        
        if (co.isScrollToVisible()) {
            getRobot().scrollToVisible(m_list, r);
        }
        
        // if possible adjust height and width for items
        getRobotFactory().getEventThreadQueuer().invokeAndWait("setItemSize", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() throws StepExecutionException {
                ListCellRenderer lcr = m_list.getCellRenderer();
                if (lcr != null) {
                    Object modelObject =  model.getElementAt(index);
                    if (modelObject != null
                            && StringUtils.isNotBlank(modelObject.toString())) {
                        Component listItem = lcr.getListCellRendererComponent(
                                             m_list, model.getElementAt(index),
                                             index, false, false);
                        Dimension preferredSize = listItem.getPreferredSize();
                        r.setSize(preferredSize);
                    }
                }                
                return null;
            }
        });     
        
      
        // If list visible width is less than the cell width, need to adjust the to
        // clickable rectangle to the visible part
      
        double listVisibleWidth = m_list.getVisibleRect().getWidth();
        if (listVisibleWidth < r.getWidth())  {
            double listVisibleX = m_list.getVisibleRect().getX();
            Dimension d = new Dimension();
            d.setSize(listVisibleWidth, r.getHeight());
            r.setBounds((int) listVisibleX, (int) r.getY(), (int) r.getWidth(),
                    (int) r.getHeight());
            r.setSize(d);
        }
            
        if (maxWidth != JComboBoxAdapter.NO_MAX_WIDTH
                && r.getWidth() > maxWidth) {
            Dimension d = new Dimension();
            d.setSize(maxWidth, r.getHeight());
            r.setSize(d);
        }
     
        getRobot().click(m_list, r,
                co.setClickType(ClickOptions.ClickType.RELEASED));
    }

    /**
     * {@inheritDoc}
     */
    public String[] getSelectedValues() {
        final int[] indices = getSelectedIndices();

        return getEventThreadQueuer().invokeAndWait(
            "getSelectedValues", new IRunnable<String[]>() { //$NON-NLS-1$
                public String[] run() {
                    Object[] values = m_list.getSelectedValues();
                    String[] selected = new String[values.length];
                    ListCellRenderer renderer = m_list.getCellRenderer();
                    for (int i = 0; i < values.length; i++) {
                        Object value = values[i];
                        Component c = renderer.getListCellRendererComponent(
                            m_list, value, indices[i], true, false);
                        selected[i] = TesterUtil.getRenderedText(c);
                    }
                    return selected;
                }
            });
    }
    
    /**
     * Clicks on the index of the passed list.
     *
     * @param i
     *            The index to click
     * @param co the click options to use
     */
    public void clickOnIndex(final Integer i, ClickOptions co) {
        clickOnIndex(i, co, JComboBoxAdapter.NO_MAX_WIDTH);
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        return getEventThreadQueuer().invokeAndWait("getValues", //$NON-NLS-1$
                new IRunnable<String[]>() {
                    public String[] run() {
                        String[] values;
                        ListCellRenderer renderer = m_list.getCellRenderer();
                        ListModel model = m_list.getModel();
                        values = new String[model.getSize()];
                        for (int i = 0; i < model.getSize(); ++i) {
                            Object obj = model.getElementAt(i);
                            m_list.ensureIndexIsVisible(i);
                            Component comp = renderer
                                    .getListCellRendererComponent(
                                m_list, obj, i, false, false);
                            String str = TesterUtil.getRenderedText(comp);
                            values[i] = str;
                        }
                        return values; // return value is not used
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
