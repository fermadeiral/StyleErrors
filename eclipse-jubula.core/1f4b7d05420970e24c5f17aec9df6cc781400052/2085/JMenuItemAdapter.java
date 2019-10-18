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

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;

/**
 * Implementation of the MenuItem interface for adapting <code>JMenuItem</code>.
 * @author BREDEX GmbH
 * 
 */
public class JMenuItemAdapter extends AbstractComponentAdapter
    implements IMenuItemComponent {

    /** the JMenuItem from the AUT    */
    private JMenuItem m_menuItem;

    /**
     * 
     * @param objectToAdapt 
     */
    public JMenuItemAdapter(Object objectToAdapt) {
        m_menuItem = (JMenuItem) objectToAdapt;        
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
     * @return The event thread queuer.
     */
    public IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    

    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {

        return m_menuItem;
    }

    /**
     * {@inheritDoc}
     */
    public void setComponent(Object element) {
        m_menuItem = (JMenuItem) element;

    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return getEventThreadQueuer().invokeAndWait(
                "isEnabled", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return ((m_menuItem != null) && m_menuItem.isEnabled());
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText() {
        return getEventThreadQueuer().invokeAndWait(
                "getText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        return m_menuItem.getText();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isShowing() {
        return getEventThreadQueuer().invokeAndWait(
                "isShowing", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() {
                        return ((m_menuItem != null) && m_menuItem.isShowing());
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isExisting() {
        return m_menuItem != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSelected() {
        return getEventThreadQueuer().invokeAndWait(
            "isSelected", new IRunnable<Boolean>() { //$NON-NLS-1$
                public Boolean run() {
                    return ((m_menuItem != null) && m_menuItem.isSelected());
                }
            });
    }
    
    /**
     * {@inheritDoc}
     */
    public IMenuComponent getMenu() {
        if (m_menuItem instanceof JMenu) {
            return new JMenuAdapter(m_menuItem);
        }
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasSubMenu() {
        return m_menuItem.getSubElements().length > 0;
    }
    /**
     * {@inheritDoc}
     */
    public boolean isSeparator() {
        return m_menuItem == null;
    }
    
    /**
     * {@inheritDoc}
     */
    public void selectMenuItem() {
        clickMenuItem(getRobot(), m_menuItem);
        
    }
    
    /**
     * {@inheritDoc}
     */
    public IMenuComponent openSubMenu() {
        if (!m_menuItem.isEnabled()) {
            throw new StepExecutionException("menu item not enabled", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.MENU_ITEM_NOT_ENABLED));
        }
        if (!(m_menuItem instanceof JMenu)) {
            throw new StepExecutionException("unexpected item found", //$NON-NLS-1$
                    EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
        }
        JMenu menu = (JMenu) m_menuItem;        
        clickMenuItem(getRobot(), m_menuItem);
        RobotTiming.sleepPostShowSubMenuItem(menu.getDelay());
        return getMenu();
    }
    
    /**
     * Clicks on a menu item
     * 
     * @param robot the robot
     * @param item  the menu item
     */
    private void clickMenuItem(IRobot robot, JMenuItem item) {
        if (EnvironmentUtils.isMacOS()) {
            TimeUtil.delay(300);
        }
        if (!item.isEnabled()) {
            throw new StepExecutionException("menu item not enabled", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.MENU_ITEM_NOT_ENABLED));
        }
        MenuSelectionManager selectionmanager = MenuSelectionManager
                .defaultManager();
        MenuElement[] menus = new MenuElement[0];
        if (selectionmanager != null) {
            menus = MenuSelectionManager.defaultManager().getSelectedPath();
        }
        if (item.getParent() instanceof JPopupMenu
                && ((JPopupMenu) item.getParent()).getInvoker()
                        .getParent() instanceof JMenuBar) {
            if (!EnvironmentUtils.isMacOS()
                    || (menus.length > 0 && menus[0] instanceof JPopupMenu)) {
                robot.click(item, null,
                        ClickOptions.create()
                                .setClickType(ClickOptions.ClickType.RELEASED)
                                .setFirstHorizontal(false));
            } else {
                item.doClick();
            }
        } else {
            if (!EnvironmentUtils.isMacOS()
                    || (menus.length > 0 && menus[0] instanceof JPopupMenu)) {
                robot.click(item, null, ClickOptions.create()
                        .setClickType(ClickOptions.ClickType.RELEASED));
            } else {
                item.doClick();
            }
        }
    }
}
