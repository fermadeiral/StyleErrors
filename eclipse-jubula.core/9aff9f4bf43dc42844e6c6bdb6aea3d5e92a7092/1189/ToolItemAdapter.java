/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IButtonComponent;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.ToolItem;
/**
 * @author BREDEX GmbH
 * @created 18.02.2013
 */
public class ToolItemAdapter extends WidgetAdapter implements
        IButtonComponent {

    /** The ToolItem */
    private ToolItem m_item = null;
    
    /**
     * 
     * @param objectToAdapt the graphics component
     */
    public ToolItemAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_item = (ToolItem) objectToAdapt;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isShowing() {
       /*
        * The actual testing of the component's existence/non-existence is 
        * implemented in CAPTestCommand.getImplClass. This method only checks
        * that the item has not been disposed.
        */
        return getEventThreadQueuer().invokeAndWait(
                "isShowing", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return !m_item.isDisposed();
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return getEventThreadQueuer().invokeAndWait(
                "isEnabled", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return m_item.isEnabled();
                    }

                });
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasFocus() {
        /* 
         * Due to the way focus is handled in SWT, we never receive focus 
         * events, and only a Control can be listed as having focus. This means
         * that the tool item's toolbar can have focus, but NEVER the tool item
         * itself. We therefore assume that the tool item does not have focus.
         */
        return false;
    }
    /**
     * {@inheritDoc}
     */
    public String getPropteryValue(final String propertyname) {
        final Item bean = m_item;
        String prop = getEventThreadQueuer().invokeAndWait("getProperty",  //$NON-NLS-1$
            new IRunnable<String>() {
                public String run() throws StepExecutionException {
                    try {
                        return getRobot().getPropertyValue(bean, propertyname);
                    } catch (RobotException e) {
                        throw new StepExecutionException(
                            e.getMessage(), 
                            EventFactory.createActionError(
                                TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
                    }
                }
            });
        return String.valueOf(prop);
    }

    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {
        return m_item;
    }

    /**
     * {@inheritDoc}
     */
    public String getText() {
        return getEventThreadQueuer().invokeAndWait("getText", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() throws StepExecutionException {
                        return SwtUtils.removeMnemonics(m_item.getText());
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected() {
        return getEventThreadQueuer().invokeAndWait(
                "isSelected", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return m_item.getSelection();
                    }
                });
    }
}