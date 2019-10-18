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

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IWidgetComponent;
import org.eclipse.jubula.rc.swt.tester.util.EventListener;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
/**
 * Implements the interface for widgets and supports basic methods
 * which are needed for nearly a lot of components.
 * 
 *  @author BREDEX GmbH
 */
public class ControlAdapter extends WidgetAdapter
    implements IWidgetComponent {
    
 
    /**   */
    private Control m_component;
    
    /**
     * Is true, if a popup menu is shown 
     */
    protected static class PopupShownCondition implements
            EventListener.Condition {

        /**
         * the popup menu
         */
        private Menu m_popup = null;
        
        /**
         * 
         * @return the popup menu
         */
        public Menu getPopup() {
            return m_popup;
        }
        
        /**
         * {@inheritDoc}
         * @param event event
         * @return result of the condition
         */
        public boolean isTrue(Event event) {

            if (event.type == SWT.Show && event.widget instanceof Menu) {
                m_popup = (Menu)event.widget;
                return true;
            } 
            
            return false;
        }
    }
    
    /**
     * 
     * @param objectToAdapt 
     */
    public ControlAdapter(Object objectToAdapt) {
        super(objectToAdapt);
        m_component = (Control) objectToAdapt;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {
        return m_component;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getPropteryValue(final String propertyname) {
        String prop = getEventThreadQueuer().invokeAndWait("getProperty",  //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() throws StepExecutionException {
                        try {
                            return getRobot().getPropertyValue(
                                    getRealComponent(), propertyname);
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
    public boolean isShowing() {
        return getEventThreadQueuer().invokeAndWait(
                "isShowing", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return m_component.isVisible();
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
                        return m_component.isEnabled();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasFocus() {
        return getEventThreadQueuer().invokeAndWait(
                "hasFocus", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return m_component.isFocusControl();
                    }
                });
    }
}