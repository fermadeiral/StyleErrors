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

import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Implements the Menu interface for adapting a <code>SWT.Menu</code>
 * 
 * @author BREDEX GmbH
 */
public class MenuAdapter extends AbstractComponentAdapter
    implements IMenuComponent {
    /** the Menu from the AUT */
    private Menu m_menu;    

    /**
     * 
     * @param component graphics component which will be adapted
     */
    public MenuAdapter(Object component) {
        m_menu = (Menu) component;
    }

    /**
     * Gets the IEventThreadQueuer.
     * 
     * @return The Robot
     * @throws RobotException
     *             If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return getRobotFactory().getRobot();
    }

    /**
     * {@inheritDoc}
     */   
    public Object getRealComponent() {
        return m_menu;
    }
    /**
     * {@inheritDoc}
     */  
    public void setComponent(Object element) {
        m_menu = (Menu) element;
        
    }
    /**
     * {@inheritDoc}
     */
    public IMenuItemComponent[] getItems() {
        MenuItem[] items = getEventThreadQueuer().invokeAndWait(
                "getItems", new IRunnable<MenuItem[]>() { //$NON-NLS-1$
                    public MenuItem[] run() {
                        return m_menu.getItems();
                    }
                });
        IMenuItemComponent[] adapters = new IMenuItemComponent[items.length];
        for (int i = 0; i < items.length; i++) {
            IMenuItemComponent menuItem = new MenuItemAdapter(items[i]);
            adapters[i] = menuItem;
        }
        return adapters;
    }

    /**
     * {@inheritDoc}
     */
    public int getItemCount() {
        return getEventThreadQueuer().invokeAndWait(
                "getItemCount", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() {
                        return m_menu.getItemCount();
                    }
                });
    }
}
