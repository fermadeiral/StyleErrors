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
package org.eclipse.jubula.rc.swt.tester;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.common.util.MenuUtilBase;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.driver.RobotSwtImpl;
import org.eclipse.jubula.rc.swt.tester.adapter.MenuItemAdapter;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
/**
 * Toolkit specific commands for the <code>Menu</code>
 * 
 * @author BREDEX GmbH
 */
public class MenuTester extends AbstractMenuTester {
    /** Test variable for contextMenus*/
    private boolean m_isCM = false;

    /**
     * {@inheritDoc}
     */
    public IComponent getComponent() {
        if (m_isCM) {
            return super.getComponent();
        }
        final Shell shell = ((RobotSwtImpl)getRobot()).getActiveWindow();
        if (shell == null) {
            setComponent(null);
        } else {
            final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
            
            queuer.invokeAndWait("setMenuBarComponent", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    Menu menu = shell.getMenuBar();
                    setComponent(menu);
                    
                    return null;
                }
            });
        }
        return super.getComponent();
    }
    
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }
    
    /**
     * Tries to select a menu item in a menu defined by a Text-Path
     * @param namePath the menu item to select
     * @param operator operator used for matching
     */
    public void selectMenuItem(String namePath, final String operator) {        
        final String[] pathItems = MenuUtilBase.splitPath(namePath);
        if (pathItems.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }
        
        try {

            final MenuItemAdapter itemAdapter = 
                    (MenuItemAdapter) navigateToMenuItem(
                            getAndCheckMenu(), pathItems, operator);
            if (itemAdapter.getRealComponent() == null) {
                throwMenuItemNotFound();
            }
            
            Rectangle bounds = itemAdapter.getMenuItemBounds();
            Rectangle nullBounds = new Rectangle(0, 0, 0, 0);
            
            if (bounds.equals(nullBounds)) {
                itemAdapter.selectProgramatically();
            } else {
                itemAdapter.selectMenuItem();
            }
        } catch (StepExecutionException e) {
            try {
                closeMenu(getAndCheckMenu(), pathItems, operator);
            } catch (StepExecutionException e1) {
                if (getLog().isInfoEnabled()) {
                    getLog().info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
            throw e;
        }
        
    }
    
    /**
     * Tries to select a menu item in a menu defined by an Index-Path
     * @param indexPath the menu item to select
     */
    public void selectMenuItemByIndexpath(String indexPath) {
        final int[] indexItems = MenuUtilBase.splitIndexPath(indexPath);
        if (indexItems.length == 0) {
            throw new StepExecutionException("empty path to menuitem not allowed", //$NON-NLS-1$
                EventFactory.createActionError());
        }

        try {
            MenuItemAdapter menuItemAdapter = (MenuItemAdapter) 
                    navigateToMenuItem(getAndCheckMenu(), indexItems);
            if (menuItemAdapter.getRealComponent() == null) {
                throwMenuItemNotFound();
            }
            
            Rectangle bounds = menuItemAdapter.getMenuItemBounds();
            Rectangle nullBounds = new Rectangle(0, 0, 0, 0);
            
            if (bounds.equals(nullBounds)) {
                menuItemAdapter.selectProgramatically();
            } else {
                menuItemAdapter.selectMenuItem();
            }
        } catch (StepExecutionException e) {
            try {
                closeMenu(getAndCheckMenu(), indexItems);
            } catch (StepExecutionException e1) {
                if (getLog().isInfoEnabled()) {
                    getLog().info("Tried to close a disabled or already closed menu."); //$NON-NLS-1$
                }
            }
            throwMenuItemNotFound();
        }
    }

    /**
     * 
     * @return the IMenuAdapter.
     * @throws StepExecutionException
     *             if the active window has no menu bar.
     */
    protected IMenuComponent getAndCheckMenu() throws StepExecutionException {
        // Verify that there is an active window
        if (((RobotSwtImpl)getRobot()).getActiveWindow() == null) {
            throw new StepExecutionException(
                I18n.getString(TestErrorEvent.NO_ACTIVE_WINDOW), 
                EventFactory.createActionError(
                    TestErrorEvent.NO_ACTIVE_WINDOW));
        }
        return super.getAndCheckMenu();
    }
    
    
    /**
     * 
     */
    private void throwMenuItemNotFound() {
        throw new StepExecutionException("no such menu item found", //$NON-NLS-1$
            EventFactory.createActionError(TestErrorEvent.NOT_FOUND));
    }
 
    /**
     * {@inheritDoc}
     */
    protected IMenuItemComponent newMenuItemAdapter(Object component) {
        return new MenuItemAdapter(component);
    }
    
    /**
     * {@inheritDoc}
     */
    protected void closeMenu(IMenuComponent menuBar, int[] path) {
        Validate.notNull(getMenu(menuBar));
        closeMenu(menuBar, path.length);
        
    }
    
    /**
     * {@inheritDoc}
     */
    protected void closeMenu(IMenuComponent menuBar, String[] textPath,
            String operator) {
        Validate.notNull(getMenu(menuBar));
        closeMenu(menuBar, textPath.length);
    }
    
    /**
     * Closes the menu cascade with the KEY ESC
     * @param menuBar menu
     * @param maxCascadeLength an integer so that the closing operation is not infinite
     */
    private void closeMenu(final IMenuComponent menuBar, int maxCascadeLength) {
        final MenuHiddenListener menuListener = 
                new MenuHiddenListener();
        getMenu(menuBar).getDisplay().syncExec(new Runnable() {
            public void run() {
                getMenu(menuBar).addMenuListener(menuListener);
            }
        });
        // Press 'ESC' key until the first menu is gone or we reach
        // the maxCascadeLength. This prevents infinite loops if this
        // is used on a platform that does not use 'ESC' to close menus.
        for (int i = 0; 
            i < maxCascadeLength && !menuListener.isMenuHidden();
            i++) {

            getRobot().keyType(getMenu(menuBar), SWT.ESC);
        }
        
    }

    /**
     * 
     * @param menu the menu adapter
     * @return the real SWT menu 
     */
    private Menu getMenu(final IMenuComponent menu) {
        return (Menu) menu.getRealComponent();
    }
    /**
     * 
     * @return -
     */
    public boolean isContextMenu() {
        return m_isCM;
    }
    /**
     * 
     * @param isCM 
     */
    public void setContextMenu(boolean isCM) {
        this.m_isCM = isCM;
    }

    /**
     * Listens for a menu to be hidden, the removes itself from the menu's
     * listener list.
     * 
     * @author BREDEX GmbH
     * @created Nov 01, 2011
     */
    private static final class MenuHiddenListener implements MenuListener {

        /** whether the expected event has occurred */
        private boolean m_eventOccurred = false;
        
        /**
         * 
         * {@inheritDoc}
         */
        public void menuHidden(MenuEvent e) {
            m_eventOccurred = true;
            ((Menu)e.widget).removeMenuListener(this);
        }

        /**
         * 
         * {@inheritDoc}
         */
        public void menuShown(MenuEvent e) {
            // no-op
        }

        /**
         * 
         * @return <code>true</code> if the menu has been hidden since this 
         *         listener was registered. Otherwise, <code>false</code>.
         */
        public boolean isMenuHidden() {
            return m_eventOccurred;
        }
    }
    
}
