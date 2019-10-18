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

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
/**
 * Implementation of the Interface <code>IComboBoxAdapter</code> as a
 * adapter for the <code>Combo</code> component.
 * This class is sub classing <code>AbstractComboBoxAdapter</code> because
 * <code>Combo</code> and <code>CCombo</code> have common parts
 * 
 * @author BREDEX GmbH
 *
 */
public class ComboAdapter extends AbstractComboBoxAdapter {

    /**  */
    private Combo m_combobox;

    /**
     * 
     * @param objectToAdapt 
     */
    public ComboAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_combobox = (Combo) objectToAdapt;
    }

    /**
     * Select the whole text of the textfield.
     */
    public void selectAll() {
        click(new Integer(1));
        
        // fix for https://bugzilla.bredex.de/201
        // The keystroke "command + a" sometimes causes an "a" to be entered
        // into the text field instead of selecting all text (or having no 
        // effect).
        if (EnvironmentUtils.isMacOS()) {
            getEventThreadQueuer().invokeAndWait("combo.selectAll", //$NON-NLS-1$
                new IRunnable<Void>() {
                    public Void run() {
                        int textLength = StringUtils.length(
                                m_combobox.getText());
                        m_combobox.setSelection(new Point(0, textLength));
                        return null;
                    }
                });
        } else {
            getRobot().keyStroke(getRobot().getSystemModifierSpec() + " A"); //$NON-NLS-1$
        }
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        return getEventThreadQueuer().invokeAndWait(
                ComboAdapter.class.getName()
                + "getSelectedIndex", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() throws StepExecutionException {
                        return m_combobox.getSelectionIndex();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        String o = getEventThreadQueuer().invokeAndWait(
                "getText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        return CAPUtil.getWidgetText(m_combobox,
                                m_combobox.getText());
                    }
                });
        return String.valueOf(o);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected boolean isComboEnabled() {
        return getEventThreadQueuer().invokeAndWait(
                ComboAdapter.class.getName()
                + "isComboEnabled", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() throws StepExecutionException {
                        return m_combobox.isEnabled();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    protected void selectImpl(int index) {
        
        // Press 'Escape' key to close the dropdown list

        getRobot().keyType(m_combobox, SWT.ESC);

        // Currently no method to select elements via mouse clicks 
        selectComboIndex(index);
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void openDropdownList() {
        // FIXME zeb: Figure out a way to check the status of the dropdown list
        toggleDropdownList();
    }

    /**
     * Returns the number of items contained in the combo list.
     * @return  the number of items.
     */
    protected int getItemCount() {
        return getEventThreadQueuer().invokeAndWait("getItemCount", //$NON-NLS-1$
                new IRunnable<Integer>() {
                    public Integer run() {
                        return m_combobox.getItemCount();
                    }
                });
    }
        
    /**
     * 
     * @param index the index to select.
     * @see Combo#select(int)
     */
    private void selectComboIndex(final int index) {
        final Combo combo = m_combobox;
        getEventThreadQueuer().invokeAndWait("selectComboIndex", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() throws StepExecutionException {
                combo.select(index);
                Event selectionEvent = new Event();
                selectionEvent.type = SWT.Selection;
                selectionEvent.widget = combo;
                combo.notifyListeners(SWT.Selection, selectionEvent);

                return null;
            }
        });   
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        return getEventThreadQueuer().invokeAndWait("getItem", //$NON-NLS-1$
                new IRunnable<String[]>() {
                    public String[] run() {
                        return m_combobox.getItems();
                    }
                });
    }
}