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

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventMatcher;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.IRobotEventInterceptor;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.driver.RobotFactorySwtImpl;
import org.eclipse.jubula.rc.swt.driver.SelectionSwtEventMatcher;
import org.eclipse.jubula.rc.swt.driver.ShowSwtEventMatcher;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.tester.util.EventListener;
import org.eclipse.jubula.rc.swt.tester.util.EventListener.Condition;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;

/**
 * Implements the MenuItem interface for adapting a <code>SWT.MenuItem</code>
 * 
 *  @author BREDEX GmbH
 */
public class MenuItemAdapter extends AbstractComponentAdapter
    implements IMenuItemComponent {

    /** The logging. */
    private static AutServerLogger log = 
        new AutServerLogger(MenuItemAdapter.class);
    
    /** the MenuItem from the AUT*/
    private MenuItem m_menuItem;
    
    /**
     * 
     * @param component graphics component which will be adapted
     */
    public MenuItemAdapter(Object component) {
        super();
        m_menuItem = (MenuItem) component;
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
        return m_menuItem;
    }
    
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object element) {
        m_menuItem = (MenuItem) element;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getText() {
        return getEventThreadQueuer().invokeAndWait(
                "getText", new IRunnable<String>() { //$NON-NLS-1$
                    public String run() {
                        return CAPUtil.getWidgetText(m_menuItem,
                                SwtUtils.removeMnemonics(m_menuItem.getText()));
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled() {
        return getEventThreadQueuer().invokeAndWait(
                "isEnabled", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() throws StepExecutionException {
                        return m_menuItem.isEnabled();
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isExisting() {
        if (m_menuItem != null) {
            return true;
        }
        return false;
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean isSelected() {
        return getEventThreadQueuer().invokeAndWait(
                "isSelected", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() throws StepExecutionException {
                        return m_menuItem.getSelection();
                    }
                });
    }

    /**
     * {@inheritDoc}
     * 
     */
    public boolean isShowing() {
        if (m_menuItem == null) { // There is no check for showing            
            return false;
        }
        return true; 
    }
    
    /**
     * {@inheritDoc}
     */
    public IMenuComponent getMenu() {
        Menu menu = getEventThreadQueuer().invokeAndWait(
                "getItems", new IRunnable<Menu>() { //$NON-NLS-1$
                    public Menu run() {
                        return m_menuItem.getMenu();
                    }
                });
        
        return new MenuAdapter(menu);
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean hasSubMenu() {
        if (getMenu() != null) {
            return true;
        }
        return false;
    }

    /**
     * Checks whether the given menu item is a separator. 
     * This method runs in the GUI thread.
     * @return <code>true</code> if <code>menuItem</code> is a separator item.
     *         Otherwise <code>false</code>.
     */
    public boolean isSeparator() {
        return getEventThreadQueuer().invokeAndWait(
                ".isSeparator", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() throws StepExecutionException {
                        return (m_menuItem.getStyle() & SWT.SEPARATOR) != 0;
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public void selectMenuItem() {
        Rectangle bounds = getMenuItemBounds();
        Rectangle nullBounds = new Rectangle(0, 0, 0, 0);
        
        if (bounds.equals(nullBounds)) {
            selectProgramatically();
        } else {
            clickMenuItem(getRobot(), m_menuItem, 1);
        }
        
    }
    
    /**
     * {@inheritDoc}
     */
    public IMenuComponent openSubMenu() {
        final MenuItem menuItem = m_menuItem;
        MenuShownCondition cond = new MenuShownCondition(menuItem);
        EventLock lock = new EventLock();
        final EventListener listener = new EventListener(lock, cond);
        final Display d = menuItem.getDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addMenuShownListeners", new IRunnable<Void>() { //$NON-NLS-1$
            public Void run() {
                d.addFilter(SWT.Show, listener);
                return null;
            }
        });
        try {
            // Menu bar items require a click in order to open the submenu.
            // Cascading menus are opened with a mouse-over and 
            // may be closed by a click.
            int clickCount = isMenuBarItem(menuItem) ? 1 : 0;
            Menu menu = getEventThreadQueuer().invokeAndWait(
                    "openSubMenu", new IRunnable<Menu>() { //$NON-NLS-1$
                        public Menu run() {
                            return menuItem.getMenu();
                        }            
                    });
            Rectangle bounds = getMenuItemBounds();
            Rectangle nullBounds = new Rectangle(0, 0, 0, 0);            
            if (bounds.equals(nullBounds)) {                               
                openSubMenuProgramatically(menu);
            } else {
                clickMenuItem(getRobot(), menuItem, clickCount);
            }
            synchronized (lock) {
                long timeout = TimeoutConstants.SERVER_TIMEOUT_WAIT_FOR_POPUP;
                long done = System.currentTimeMillis() + timeout; 
                long now;                
                while (!lock.isReleased() && timeout > 0) {
                    lock.wait(timeout);
                    now = System.currentTimeMillis();
                    timeout = done - now;
                }
            } 
        } catch (InterruptedException e) { // ignore
        } finally {
            queuer.invokeAndWait("removeMenuShownListeners", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    d.removeFilter(SWT.Show, listener);
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            String itemText = getEventThreadQueuer().invokeAndWait(
                    "getItemText", new IRunnable<String>() { //$NON-NLS-1$
                        public String run() throws StepExecutionException {
                            if (menuItem != null && !menuItem.isDisposed()) {
                                return CAPUtil.getWidgetText(menuItem,
                                    SwtUtils.removeMnemonics(
                                            menuItem.getText()));
                            }
                            return "unknown menu item"; //$NON-NLS-1$
                        }
                    });
            itemText = SwtUtils.removeMnemonics(itemText);
            throw new StepExecutionException(
                    I18n.getString("TestErrorEvent.MenuDidNotAppear",  //$NON-NLS-1$
                            new String [] {itemText}), 
                    EventFactory.createActionError(
                            "TestErrorEvent.MenuDidNotAppear", //$NON-NLS-1$ 
                            new String [] {itemText}));
        }        
        return new MenuAdapter(cond.getMenu());
    }
    
    /**
     * @param menuItem the menu item to check
     * @return <code>true</code> of the given menu item is part of a menu
     *         bar. Otherwise, <code>false</code>.
     */
    private boolean isMenuBarItem(final MenuItem menuItem) {
        return getEventThreadQueuer().invokeAndWait(
                "isMenuBarItem", new IRunnable<Boolean>() { //$NON-NLS-1$

                    public Boolean run() throws StepExecutionException {
                        if (menuItem != null && !menuItem.isDisposed()) {
                            Menu parent = menuItem.getParent();
                            if (parent != null && !parent.isDisposed()) {
                                return (parent.getStyle() & SWT.BAR) != 0;
                            }
                        }
                        return false;
                    }
                });
    }
    
    /**
     * Waits for a submenu to appear. Examples of submenus are cascading menus
     * and pulldown menus.
     *
     * @author BREDEX GmbH
     * @created Oct 30, 2008
     */
    public static class MenuShownCondition implements Condition {
        /** the menu that was shown */
        private Menu m_shownMenu = null;

        /** the parent item of the expected menu */
        private MenuItem m_parentItem;

        /**
         * Constructor
         *  
         * @param parentItem The parent item of the expected menu. This 
         *                   condition only matches if a menu with parent item
         *                   <code>parentItem</code> appears.
         */
        MenuShownCondition(MenuItem parentItem) {
            m_parentItem = parentItem;
        }
        
        /**
         * 
         * @return the menu that appeared
         */
        public Menu getMenu() {
            return m_shownMenu;
        }
        
        /**
         * 
         * {@inheritDoc}
         */
        public boolean isTrue(Event event) {
            if (event.type == SWT.Show && event.widget instanceof Menu
                    && ((Menu)(event.widget)).getParentItem() == m_parentItem) {
                m_shownMenu = (Menu)event.widget;
                return true;
            } 
            
            return false;
        }
    }
    
    /**
     * Clicks on a menu item
     * 
     * @param robot the robot
     * @param item the menu item
     * @param clickCount the number of times to click the menu item
     */
    private void clickMenuItem(IRobot robot, final MenuItem item, 
            int clickCount) {
        boolean isSecondInMenu = getEventThreadQueuer()
                .invokeAndWait(
                    "isMenuBar", new IRunnable<Boolean>() { //$NON-NLS-1$
                        public Boolean run() throws StepExecutionException {
                            try {                            
                                if ((item.getParent()
                                        .getParentMenu().getStyle() 
                                    & SWT.BAR) != 0) {
                                    return Boolean.TRUE;
                                }
                                Menu parent = item.getMenu().getParentMenu();
                                if (parent != null) {
                                    Menu preparent = parent.getParentMenu();

                                    if (preparent != null) {
                                        return (preparent.getStyle() & SWT.BAR) 
                                            != 0;
                                    }
                                }
                            } catch (NullPointerException ne) {
                            // Nothing here, there is no parent of parent.
                            }
                            return Boolean.FALSE;
                        }    
                    });
        if (isSecondInMenu) {
            robot.click(item, null, 
                    ClickOptions.create()
                    .setClickType(ClickOptions.ClickType.RELEASED)
                    .setStepMovement(true).setClickCount(clickCount)
                    .setFirstHorizontal(false)); 

        } else {
            robot.click(item, null, 
                    ClickOptions.create()
                    .setClickType(ClickOptions.ClickType.RELEASED)
                    .setStepMovement(true).setClickCount(clickCount));
        }  
    }
    
    /**
     * 
     * @return bounds of MenuItem
     */
    public Rectangle getMenuItemBounds() {
        return getEventThreadQueuer().invokeAndWait(
                "getMenuItemBounds", new IRunnable<Rectangle>() { //$NON-NLS-1$
                    public Rectangle run() {
                        return SwtUtils.getBounds(m_menuItem);
                    }            
                });        
    }
    
    /**
     * open SubMenu programatically (for Mac OS)
     * @param menu the Menu
     */
    public void openSubMenuProgramatically(final Menu menu) {
        if (!isMenuEnabled(menu)) {
            throw new StepExecutionException("menu item not enabled", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.MENU_ITEM_NOT_ENABLED));
        }
        
        final InterceptorOptions options = new InterceptorOptions(
                new long[]{SWT.Show});
        final IEventMatcher matcher = 
            new ShowSwtEventMatcher();  
        RobotFactorySwtImpl robotSwt = new RobotFactorySwtImpl();
        IRobotEventInterceptor interceptor =
            robotSwt.getRobotEventInterceptor();
        final IRobotEventConfirmer confirmer = interceptor
            .intercept(options);
        
        final Event event = new Event();
        event.time = (int) System.currentTimeMillis();
        event.widget = menu;
        event.display = menu.getDisplay();
        event.type = SWT.Show;
        
        getEventThreadQueuer().invokeAndWait(
                "openSubMenuProgramatically", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() {
                        menu.notifyListeners(SWT.Show, event);
                        return null;
                    }
                });

        try {
            confirmer.waitToConfirm(menu, matcher);
        } catch (RobotException re) {
            final StringBuffer sb = new StringBuffer(
                "Robot exception occurred while clicking...\n"); //$NON-NLS-1$
            sb.append("Component: "); //$NON-NLS-1$

            getEventThreadQueuer().invokeAndWait(
                    "getBounds", new IRunnable<Void>() { //$NON-NLS-1$
                        public Void run()
                            throws StepExecutionException {
                            sb.append(menu);
                            // Return value not used
                            return null;
                        }
                    });
            log.error(sb.toString(), re);
            throw re;
        }
    }
    
    /**
     * select MenuItem programatically (for Mac OS)
     */
    public void selectProgramatically() {
        if (!isMenuItemEnabled(m_menuItem)) {
            throw new StepExecutionException("menu item not enabled", //$NON-NLS-1$
                   EventFactory.createActionError(
                            TestErrorEvent.MENU_ITEM_NOT_ENABLED));
        }
        final MenuItem menuItem = m_menuItem;
        final InterceptorOptions options = new InterceptorOptions(
                new long[]{SWT.Selection});
        final IEventMatcher matcher = 
            new SelectionSwtEventMatcher();        
        RobotFactorySwtImpl robotSwt = new RobotFactorySwtImpl();
        IRobotEventInterceptor interceptor =
            robotSwt.getRobotEventInterceptor();        
        final IRobotEventConfirmer confirmer = interceptor
            .intercept(options);
        
        final Event event = new Event();
        event.time = (int) System.currentTimeMillis();
        event.widget = menuItem;
        event.display = menuItem.getDisplay();
        event.type = SWT.Selection;
        closeUnderMac();

        getEventThreadQueuer().invokeLater(
                "selectProgramatically", new Runnable() { //$NON-NLS-1$
                    public void run() {  
                        //if menuitem is checkbox or radiobutton set Selection
                        if ((menuItem.getStyle() & SWT.CHECK) == 0
                                || (menuItem.getStyle() & SWT.RADIO) == 0) {
                            if (menuItem.getSelection()) {
                                menuItem.setSelection(false);
                            } else {
                                menuItem.setSelection(true);
                            }                            
                        }

                        menuItem.notifyListeners(SWT.Selection, event);
                        
                    }            
                });

        try {
            confirmer.waitToConfirm(menuItem, matcher);
        } catch (RobotException re) {
            final StringBuffer sb = new StringBuffer(
                "Robot exception occurred while clicking...\n"); //$NON-NLS-1$
            //logRobotException(menuItem, re, sb);
            sb.append("Component: "); //$NON-NLS-1$

            getEventThreadQueuer().invokeAndWait(
                    "getBounds", new IRunnable<Void>() { //$NON-NLS-1$
                        public Void run() throws StepExecutionException {
                            sb.append(menuItem);
                            // Return value not used
                            return null;
                        }
                    });

            log.error(sb.toString(), re);
            throw re;
        }
    
    }
    
    /**
     * "close" (hide) the context menu. this is necessary because if you
     * select programatically the contextmenu is not closed.
     */
    private void closeUnderMac() {
        if (EnvironmentUtils.isMacOS()) {
            // "close" (hide) the context menu. this is necessary because
            // the selection event will not close the context menu.
            // we do this before firing the selection event so that the menu
            // disappears before the effects of the selection event (e.g.
            // showing a dialog) are presented.
            m_menuItem.getDisplay().syncExec(new Runnable() {
                public void run() {
                    Menu parentMenu = m_menuItem.getParent();
                    while (parentMenu.getParentMenu() != null) {
                        parentMenu = parentMenu.getParentMenu();
                    }
                    parentMenu.setVisible(false);
                }
            });
        }
    }
    
    /**
     * Calls MenuItem.isEnabled() in the GUI-Thread
     * @param menuItem the MenuItem
     * @return true if enabled, false otherwise
     * @see MenuItem#isEnabled()
     */
    private boolean isMenuItemEnabled(final MenuItem menuItem) {
        return getEventThreadQueuer().invokeAndWait(
            MenuItemAdapter.class + ".isMenuItemEnabled", new IRunnable<Boolean>() { //$NON-NLS-1$
                public Boolean run() throws StepExecutionException {
                    return menuItem.isEnabled();
                }
            });
    }
    
    /**
     * Calls MenuItem.isEnabled() in the GUI-Thread
     * @param menu the Menu
     * @return true if enabled, false otherwise
     * @see MenuItem#isEnabled()
     */
    private boolean isMenuEnabled(final Menu menu) {
        return getEventThreadQueuer().invokeAndWait(
            MenuItemAdapter.class + ".isMenuEnabled", new IRunnable<Boolean>() { //$NON-NLS-1$
                public Boolean run() throws StepExecutionException {
                    return menu.isEnabled();
                }
            });
    }
}