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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Rectangle;

import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComboComponent;
import org.eclipse.jubula.rc.swing.tester.util.TesterUtil;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
/**
 * Implementation of the Interface <code>IComboBoxAdapter</code> as a
 * adapter for the <code>JComboBox</code> component.
 * @author BREDEX GmbH
 *
 */
public class JComboBoxAdapter extends JComponentAdapter implements
        IComboComponent {
    /**
     * <code>INVALID_MAX_WIDTH</code>
     */
    public static final int NO_MAX_WIDTH = -1;
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            JComboBoxAdapter.class);
    
    /**   */
    private JComboBox m_comboBox;
    /**
     * 
     * @param objectToAdapt 
     */
    public JComboBoxAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_comboBox = (JComboBox) objectToAdapt;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        String comboBoxText;
        if (isEditable()) { 
            comboBoxText = TesterUtil.getRenderedText(
                    getComboBoxEditorComponent(m_comboBox), true);
        } else {
            final int selIndex = getSelectedIndex();
            if (selIndex == -1) {
                try {
                    comboBoxText = getTextForSelectedItem();
                } catch (Exception e) {
                    comboBoxText = getEventThreadQueuer().invokeAndWait(
                            "getSelectedItemText", //$NON-NLS-1$
                            new IRunnable<String>() {
                                public String run() {
                                    return String.valueOf(
                                            m_comboBox.getSelectedItem());
                                }
                            });

                }
            } else {
                final JList jlist = new JList(m_comboBox.getModel());
                String o = getEventThreadQueuer().invokeAndWait(
                        "getText", new IRunnable<String>() { //$NON-NLS-1$
                            public String run() {
                                Component disp = m_comboBox.getRenderer()
                                    .getListCellRendererComponent(jlist,
                                        jlist.getModel().getElementAt(selIndex),
                                        selIndex, true, m_comboBox.hasFocus());
                                return TesterUtil.getRenderedText(disp, false);
                            }
                        });
                comboBoxText = String.valueOf(o);
            }
        }
        return comboBoxText;
    
    }
    
    /**
     * gets the text from the renderer for the selected Item
     * @return the text of the selected item
     */
    private String getTextForSelectedItem() {
        String o = getEventThreadQueuer().invokeAndWait("getText", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() {
                        Component disp = m_comboBox.getRenderer()
                                .getListCellRendererComponent(new JList(),
                                        m_comboBox.getSelectedItem(), 0, true,
                                        m_comboBox.hasFocus());
                        return TesterUtil.getRenderedText(disp, false);
                    }
                });
        return String.valueOf(o);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEditable() {
        return getEventThreadQueuer().invokeAndWait("isEditable", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() {
                        return m_comboBox.isEditable();
                    }
                });
    }
    
    /**
     * select the whole text of  the textfield by clicking three times.
     */
    public void selectAll() {
        click(new Integer(1));
        getRobot().keyStroke(getRobot().getSystemModifierSpec() + " A"); //$NON-NLS-1$
    }

    /**
     * {@inheritDoc}
     */
    public int getSelectedIndex() {
        Integer actual = getEventThreadQueuer().invokeAndWait(
                JComboBoxAdapter.class.getName() + ".getSelectedIndex", //$NON-NLS-1$
                new IRunnable<Integer>() {
                    public Integer run() {
                        return new Integer(m_comboBox.getSelectedIndex());
                    }
                });
        return actual.intValue();
    }

    /**
     * {@inheritDoc}
     */
    public void select(int index) {
        JListAdapter list = new JListAdapter(findJList());
        list.clickOnIndex(new Integer(index), ClickOptions
                .create().setClickCount(1), getMaxWidth());
    }

    /**
     * Inputs <code>text</code> to <code>component</code>.<br>
     * @param text the text to type in
     * @param replace whether to rplace the text or not
     * @throws StepExecutionException if an error occurs during typing <code>text</code>
     * @throws IllegalArgumentException if <code>component</code> or <code>text</code> are null
     */
    public void input(String text, boolean replace)
        throws StepExecutionException, IllegalArgumentException {

        Validate.notNull(text, "text must not be null"); //$NON-NLS-1$
        Component editor = getComboBoxEditorComponent(m_comboBox);
        if (editor == null) {
            throw new StepExecutionException("could not find editor", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        if (replace) {
            selectAll();
        }
        getRobot().type(editor, text);
    }

    /**
     * performs a <code>count</code> -click on the textfield.
     * @param count the number of clicks
     */
    public void click(Integer count) {
        Component editor = getComboBoxEditorComponent(m_comboBox);
        if (editor == null) {
            throw new StepExecutionException("no editor found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        getRobot().click(editor, null, ClickOptions.create().setClickCount(
                count.intValue()));
    }
    
    /**
     * @param component
     *            the combobox
     * @return the editor used to render and edit the selected item in the
     *         JComboBox field.
     * @throws StepExecutionException
     *             if the editor component could not be found
     */
    private Component getComboBoxEditorComponent(JComboBox component)
        throws StepExecutionException {

        ComboBoxEditor cbe = component.getEditor();
        if (cbe == null) {
            throw new StepExecutionException("no ComboBoxEditor found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        Component c = cbe.getEditorComponent();
        if (c == null) {
            throw new StepExecutionException("no EditorComponent found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        return c;
    }
    
    /**
     * Finds the <code>JList</code> of the combobox.
     * @return The list
     */
    private JList findJList() {
        JList list = (JList)getComponentViaHierarchy(openPopupMenu(),
                JList.class);
        if (list == null) {
            throw new StepExecutionException("list component not found", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        return list;
    }
    
    /**
     * Opens the combobox popup menu and returns the popup instance. May also be
     * called if the popup is already visible
     * @return The popup menu
     */
    private JPopupMenu openPopupMenu() {
        if (!isPopupVisible()) {
            Component c = getComponentViaHierarchy(m_comboBox, JButton.class);
            Rectangle r = null;
            if ((c == null) && (!m_comboBox.isEditable())) {
                c = m_comboBox;
            } else if ((c == null) && (m_comboBox.isEditable())) {
                c = m_comboBox;
                r = findArrowIconArea();
            }
            if (log.isDebugEnabled()) {
                log.debug("Opening popup by clicking on: " + c); //$NON-NLS-1$
            }
            getRobot().click(c, r);
        }
        if (!isPopupVisible()) {
            log.debug("Dropdown list still not visible, must be an error"); //$NON-NLS-1$
            throw new StepExecutionException("dropdown list not visible", //$NON-NLS-1$
                EventFactory.createActionError(
                        TestErrorEvent.DROPDOWN_LIST_NOT_FOUND));
        }
        return getPopupMenu(m_comboBox);
    }
    
    /**
     * Tries to find the popup menu from the combobox
     * @param component the combobox
     * @return the popup of the combobox
     * @throws StepExecutionException if the popup could not be found
     */
    private JPopupMenu getPopupMenu(JComboBox component)
        throws StepExecutionException {

        AccessibleContext ac = component.getAccessibleContext();
        for (int i = 0; i < ac.getAccessibleChildrenCount(); i++) {
            Accessible a = ac.getAccessibleChild(i);
            if (a instanceof JPopupMenu) {
                return (JPopupMenu)a;
            }
        }
        throw new StepExecutionException("cannot find dropdown list", //$NON-NLS-1$
            EventFactory.createActionError(
                    TestErrorEvent.DROPDOWN_LIST_NOT_FOUND));
    }
    /**
     * Tries to find the component in the component hierarchy
     * @param component where to search
     * @param c type of the component which should be found
     * @return the desired component
     */
    private Component getComponentViaHierarchy(Container component, Class c) {
        Component[] comps = component.getComponents();
        for (int i = 0; i < comps.length; i++) {
            if (c.isInstance(comps[i])) {
                return comps[i];
            }
        }
        for (int i = 0; i < comps.length; i++) {
            if (comps[i] instanceof Container) {
                Component ct = getComponentViaHierarchy((Container)comps[i], c);
                if (ct != null) {
                    return ct;
                }
            }
        }
        return null;
    }
    
    /**
     * @return true, if the popup of the combobox is visible
     */
    private boolean isPopupVisible() {
        return getEventThreadQueuer().invokeAndWait(
                JComboBoxAdapter.class.getName()
                + "isPopupVisible", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() throws StepExecutionException {
                        return m_comboBox.isPopupVisible();
                    }
                });
    }

    /**
     * @return a rectangle, where the arrow icon is expected.
     */
    private Rectangle findArrowIconArea() {
        JComboBox comboBox = m_comboBox;
        Component editor = getComboBoxEditorComponent(comboBox);
        Rectangle r = null;
        if (editor == null) {
            throw new StepExecutionException("could not find editor", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        Rectangle ra[] =
            SwingUtilities.computeDifference(comboBox.getBounds(),
                editor.getBounds());
        if ((ra == null) || (ra.length < 1)) {
            throw new StepExecutionException("could not arrow icon", //$NON-NLS-1$
                EventFactory.createActionError(TestErrorEvent.COMP_NOT_FOUND));
        }
        r = ra[0];
        // find the largest area of the returned rectangles.
        double bestAreaIndex = Double.MAX_VALUE;
        for (int i = 0; i < ra.length; i++) {
            if ((ra[i].height > 0) && (ra[i].width > 0)) {
                double areaIndex = ((double)ra[i].width) / ra[i].height - 1.0;
                if (areaIndex < 0) {
                    areaIndex *= (-1);
                }
                if (areaIndex < bestAreaIndex) {
                    bestAreaIndex = areaIndex;
                    r = ra[i];
                }
            }
        }
        return r;
    }
    
    /**
     * @return the maximal width for the selection; -1 if none available
     * e.g. the preferred width of the combo box itself is 100 pixel although
     * the preferred size of the embedded items is more than two times bigger
     * --> click outside of component (JList) #3013 
     */
    private double getMaxWidth() {
        double maxWidth = NO_MAX_WIDTH;
        Dimension d = m_comboBox.getPreferredSize();
        if (d != null) {
            maxWidth = d.getWidth();
        }
        return maxWidth;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasFocus() {
        return getEventThreadQueuer().invokeAndWait(
                "hasFocus", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        if (m_comboBox.isEditable()) {
                            boolean editorFocus = m_comboBox.getEditor()
                                    .getEditorComponent().hasFocus();
                            if (editorFocus) {
                                return Boolean.TRUE;
                            }
                        }
                        return m_comboBox.hasFocus();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        JListAdapter list = new JListAdapter(findJList());
        return list.getValues();
    }
}
