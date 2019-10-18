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
/**
 * 
 */
package org.eclipse.jubula.rc.rcp.tabbedproperties.adapter;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListComponent;
import org.eclipse.jubula.rc.common.util.SelectionUtil;
import org.eclipse.jubula.rc.swt.tester.adapter.ControlAdapter;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList.BottomNavigationElement;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList.TopNavigationElement;

/**
 * The adapter for the {@link TabbedPropertyList}
 * @author BREDEX GmbH
 *
 */
public class TabbedPropertiesAdapter extends ControlAdapter
        implements IListComponent<TabbedPropertyList> {
    
    /** the {@link TabbedPropertyList}*/
    private TabbedPropertyList m_list;
    
    /**
     * constructor
     * @param objectToAdapt the {@link TabbedPropertyList}
     */
    public TabbedPropertiesAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_list = (TabbedPropertyList) objectToAdapt;
            
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
    public String getPropteryValue(String propertyname) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {
        return m_list;
    }

    /**
     * {@inheritDoc}
     */
    public int[] getSelectedIndices() {
        return new int[]{m_list.getSelectionIndex()};
    }

    /**
     * {@inheritDoc}
     */
    public void clickOnIndex(final Integer index, final ClickOptions co) {
        Object element = scrollToElement(index);
        getRobot().click(element, null, co);
        
    }

    /**
     * @param target target number
     * @return the wanted element
     */
    private Object scrollToElement(Integer target) {
        final Control wantedElement =
                (Control) m_list.getElementAt(target.intValue());
        if (wantedElement == null) {
            throw new StepExecutionException(
                    "List index '" + target.intValue() //$NON-NLS-1$
                            + "' is out of range", //$NON-NLS-1$
                    EventFactory
                            .createActionError(TestErrorEvent.INVALID_INDEX));
        }
        TopNavigationElement top = null;
        BottomNavigationElement bottom = null;
        int firstVisible = -1;
        Control[] children = getChildren(m_list);
        for (int j = 0; j < children.length; j++) {
            Control control = children[j];
            if (control instanceof TopNavigationElement) {
                top = (TopNavigationElement) control;
            } else if (control instanceof BottomNavigationElement) {
                bottom = (BottomNavigationElement) control;
            }
            if (top != null && bottom != null) {
                break;
            }
        }
        int i = 0;
        while (m_list.getElementAt(i) != null) {
            Control element = (Control) m_list.getElementAt(i);
            if (isVisible(element)) {
                firstVisible = i;
                break;
            }
            i++;
        }
        ClickOptions options = new ClickOptions().setClickCount(1);
        int maxCount = getValues().length;
        int j = 0;
        while (!isVisible(wantedElement) &&  j <= maxCount) {
            if (firstVisible > target) {
                if (isVisible(top)) {
                    getRobot().click(top, null, options);
                } else {
                    break;
                }
            } else {
                if (isVisible(bottom)) {
                    getRobot().click(bottom, null, options);
                } else {
                    break;
                }
            }
            j++;
        }
        return wantedElement;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getSelectedValues() {
        return new String[] {
                getTextOfElementAtIndex(
                        m_list.getSelectionIndex()) };
    }

    /**
     * {@inheritDoc}
     */
    public String[] getValues() {
        List<String> values = new ArrayList<String>();
        try {
            for (int i = 0; i < m_list.getNumberOfElements(); i++) {
                values.add(getTextOfElementAtIndex(i));
            }
        } catch (NoSuchMethodError e) {
            // 3.5.200 and older versions does not have the Method getNumberOfElements
            int i = 0;
            while (m_list.getElementAt(i) != null) {
                values.add(getTextOfElementAtIndex(i));
                i++;
            }
        }
        return values.toArray(new String[values.size()]);
    }

    /**
     *
     * @param i the index
     * @return the text of the
     */
    private String getTextOfElementAtIndex(int i) {
        Object element = m_list.getElementAt(i);
        return element != null ? element.toString() : StringConstants.EMPTY;
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, TabbedPropertyList cell) {
        StepExecutionException.throwUnsupportedAction();
        return null;
    }
    
    /**
     * Wrapper for {@link Composite#getChildren()}
     * @param c the composite
     * @return the children of the composite
     */
    private Control[] getChildren(final Composite c) {
        return getEventThreadQueuer().invokeAndWait("getChildren", new IRunnable<Control[]>() { //$NON-NLS-1$
            public Control[] run() throws StepExecutionException {
                return c.getChildren();
            }
        });
    }

    /**
     * Wrapper for {@link Control#isVisible()}
     * @param c the control
     * @return is the control visible
     */
    private boolean isVisible(final Control c) {
        return getEventThreadQueuer().invokeAndWait("isVisible", new IRunnable<Boolean>() { //$NON-NLS-1$
            public Boolean run() throws StepExecutionException {
                return c.isVisible();
            }
        });
    }

}
