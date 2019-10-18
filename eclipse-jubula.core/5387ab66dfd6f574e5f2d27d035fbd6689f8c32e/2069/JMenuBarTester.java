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
package org.eclipse.jubula.rc.swing.tester;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;

import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.swing.tester.adapter.JMenuItemAdapter;
import org.eclipse.jubula.rc.swing.tester.util.WindowHelper;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;

/**
 * Toolkit specific commands for the <code>JMenuBar</code>.
 * 
 * @author BREDEX GmbH
 */
public class JMenuBarTester extends AbstractMenuTester {
            
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        
        return null;
    }
    
    /**
     * Workaround to get the menu bar existing somewhere in the given 
     * container's hierarchy. This method should <b>only</b> be used if 
     * {@link JFrame#getJMenuBar()} / {@link JDialog#getJMenuBar()} return 
     * <code>null</code>, which is a very rare case.
     * 
     * This method also performs some unorthodox visibility testing in order
     * to avoid retrieving the wrong menu.
     * 
     * @param rootPane The root container from which to start the search for
     *                 the menu bar.
     * @return the first menu bar found in the hierarchy that: <ul>
     *          <li>is showing</li>
     *          <li>contains at least one visible menu</li>
     */
    private JMenuBar getMenuBarWorkaround(Container rootPane) {
        JMenuBar menuBar = null;
        List menuList = new ArrayList();
        collectMenuBarsWorkaround(rootPane, menuList);
        Iterator menuIter = menuList.iterator();
        while (menuIter.hasNext() && menuBar == null) {
            JMenuBar menu = (JMenuBar)menuIter.next();
            boolean hasAtLeastOneItem = false;
            MenuElement [] subElements = menu.getSubElements();
            for (int i = 0; 
                    i < subElements.length && !hasAtLeastOneItem; 
                    i++) {
                if (subElements[i] instanceof JMenu) {
                    JMenu subMenu = (JMenu)subElements[i];
                    hasAtLeastOneItem = 
                        subMenu != null && subMenu.isShowing();
                }
            }
            if (hasAtLeastOneItem) {
                menuBar = menu;
            }
        }
        return menuBar;
    }
    
    /**
     * Adds all menu bars found in the hierarchy <code>container</code> to 
     * <code>menuBarList</code>. This is part of a workaround for finding menus
     * in AUTs that don't make proper use of 
     * {@link JFrame#setJMenuBar()} / {@link JDialog#setJMenuBar()}.
     * 
     * @see #getMenuBarWorkaround(Container)
     * 
     * @param container The root container from which to start the search for
     *                  the menu bars. 
     * @param menuBarList The list to which each menu bar found will be added.
     *                    Only objects of type {@link JMenuBar} will be added
     *                    to this list.
     */
    private void collectMenuBarsWorkaround(
            Container container, List menuBarList) {
        Component [] children = container.getComponents();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof JMenuBar
                    && children[i].isShowing()) {
                menuBarList.add(children[i]);
            }
        }
        
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Container
                    && children[i].isVisible()) {
                collectMenuBarsWorkaround((Container)children[i], menuBarList);
            }
        }
        
    }
    /**
     * @return the component
     */
    public IComponent getComponent() { 
        IComponent component = super.getComponent();
        if (component != null && component.getRealComponent() 
                instanceof JPopupMenu) {
            return component;
        }
        Window activeWindow = WindowHelper.getActiveWindow();
        if (activeWindow == null) {
            getLog().warn("JMenuBarImplClass.getComponent(): No active window."); //$NON-NLS-1$
        } else {
            JMenuBar menuBar = null;
            Container rootPane = null;
            if (activeWindow instanceof JDialog) {
                JDialog dialog = (JDialog)activeWindow;
                menuBar = dialog.getJMenuBar();
                rootPane = dialog.getRootPane();
            } else if (activeWindow instanceof JFrame) {
                JFrame frame = (JFrame)activeWindow;
                menuBar = frame.getJMenuBar();
                rootPane = frame.getRootPane();
            }

            if (menuBar == null) {
                menuBar = getMenuBarWorkaround(rootPane);
            }

            setComponent(menuBar);
        }
        return super.getComponent();
    }

    /**
     *{@inheritDoc}
     */
    protected void closeMenu(IMenuComponent menuBar, String[] textPath,
            String operator) {
        if (closMacMenus()) {
            return;
        }
        if (menuBar.getRealComponent() instanceof JPopupMenu) {
            for (int i = 0; i < textPath.length; i++) {
                if (((JPopupMenu)menuBar.getRealComponent()).isVisible()) {
                    getRobot().keyType(menuBar.getRealComponent(),
                            KeyEvent.VK_ESCAPE);    
                }
                
            }
            return;
        }
        super.closeMenu(menuBar, textPath, operator);
            
    }

    /**
     * This methods closes the opened context menu via the {@link MenuSelectionManager}
     * @return returns <code>true</code> if it is mac and has closed the menu
     */
    private boolean closMacMenus() {
        if (EnvironmentUtils.isMacOS()) {
            final MenuSelectionManager manager = MenuSelectionManager
                    .defaultManager();
            if (manager != null) {
                getEventThreadQueuer().invokeAndWait("closeMac Menus", //$NON-NLS-1$
                        new IRunnable<Object>() {
                            public Rectangle run() {
                                manager.clearSelectedPath();
                                return null;
                            }
                        });
                return true;
            }
        }
        return false;
    }

    /**
     *{@inheritDoc}
     */
    protected void closeMenu(IMenuComponent menuBar, int[] path) {
        if (closMacMenus()) {
            return;
        }
        if (menuBar.getRealComponent() instanceof JPopupMenu) {
            for (int i = 0; i < path.length; i++) {
                if (((JPopupMenu)menuBar.getRealComponent()).isVisible()) {
                    getRobot().keyType(menuBar.getRealComponent(),
                            KeyEvent.VK_ESCAPE);    
                }
            }
            return;
        }
        super.closeMenu(menuBar, path);
    }
    
    /**
     * {@inheritDoc}
     */
    protected IMenuItemComponent newMenuItemAdapter(Object component) {
        return new JMenuItemAdapter(component);
    }

}
