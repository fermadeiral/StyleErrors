/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;
import org.eclipse.jubula.rc.javafx.tester.adapter.MenuAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.MenuItemAdapter;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.WindowEvent;

/**
 * Toolkit specific commands for the <code>Menu</code> and <code>MenuBar</code>.
 * 
 * @author BREDEX GmbH
 * @created 10.2.2014
 */
public class MenuTester extends AbstractMenuTester {

    @Override
    public String[] getTextArrayFromComponent() {
        return null;
    }

    @Override
    protected IMenuItemComponent newMenuItemAdapter(Object component) {
        return new MenuItemAdapter<MenuItem>((MenuItem) component);
    }

    @Override
    protected void closeMenu(IMenuComponent menu, String[] textPath,
            String operator) {
        IMenuComponent currMenu = menu;
        List<IMenuComponent> openMenus = new ArrayList<>();
        for (String name : textPath) {
            IMenuItemComponent[] items = currMenu.getItems();
            int index = getIndexForName(currMenu, name, operator);
            IMenuItemComponent item = null;
            // Because the index returned by getIndexForname only counts real
            // menu items not things
            // like separators or disable menu items, we have to find the right
            // item for the index
            for (int j = index; j < items.length; j++) {
                item = items[j];
                if ((!item.isSeparator()) && item.isEnabled()
                        && item.isShowing()) {
                    break;
                }
            }
            if (item != null && item.hasSubMenu()) {
                currMenu = new MenuAdapter((Menu) item.getRealComponent());
                openMenus.add(currMenu);
            }
        }

        for (int i = openMenus.size() - 1; i >= 0; i--) {
            boolean successful = waitForMenuToClose((Menu) openMenus.get(i)
                    .getRealComponent());
            if (!successful) {
                throw new StepExecutionException("Popup could not be closed", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.
                                        POPUP_NOT_FOUND));
            }

        }
        if (menu.getRealComponent() instanceof ContextMenu) {
            boolean successful = closeContextMenu(
                    (ContextMenu) menu.getRealComponent());
            if (!successful) {
                throw new StepExecutionException("Popup could not be closed", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.
                                        POPUP_NOT_FOUND));
            }
        }
    }

    @Override
    protected void closeMenu(IMenuComponent menu, int[] path) {
        IMenuComponent currMenu = menu;
        List<IMenuComponent> openMenus = new ArrayList<>();
        for (int index : path) {
            IMenuItemComponent[] items = currMenu.getItems();
            // Because the index in path only counts real menu items not things
            // like separators or disable menu items, we have to find the right
            // item for the index
            IMenuItemComponent item = null;
            for (int j = index; j < items.length; j++) {
                item = items[j];
                if ((!item.isSeparator()) && item.isEnabled()
                        && item.isShowing()) {
                    break;
                }
            }
            if (!(item == null || (item.isSeparator()) || (!item.isEnabled())
                    || (!item.isShowing())) && item.hasSubMenu()) {
                currMenu = new MenuAdapter((Menu) item.getRealComponent());
                openMenus.add(currMenu);
            }
        }

        for (int i = openMenus.size() - 1; i >= 0; i--) {
            boolean successful = waitForMenuToClose((Menu) openMenus.get(i)
                    .getRealComponent());
            if (!successful) {
                throw new StepExecutionException("Popup could not be closed", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.
                                        POPUP_NOT_FOUND));
            }

        }
        if (menu.getRealComponent() instanceof ContextMenu) {
            boolean successful = closeContextMenu(
                    (ContextMenu) menu.getRealComponent());
            if (!successful) {
                throw new StepExecutionException("Popup could not be closed", //$NON-NLS-1$
                        EventFactory
                                .createActionError(TestErrorEvent.
                                        POPUP_NOT_FOUND));
            }
        }
    }
    
    /**
     * Closes a ContextMenu and waits for it to be closed
     * 
     * @param m
     *            the menu
     * @return true if menu was closed successfully, false if not;
     */
    private boolean closeContextMenu(final ContextMenu m) {
        final EventLock eventLock = new EventLock();
        final EventHandler<Event> shownHandler = new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                synchronized (eventLock) {
                    eventLock.notifyAll();
                }
            }
        };
        EventThreadQueuerJavaFXImpl.invokeAndWait(
                "addCloseHandler", new Callable<Void>() { //$NON-NLS-1$

                    @Override
                    public Void call() throws Exception {
                        m.addEventHandler(
                                WindowEvent.WINDOW_HIDDEN, shownHandler);
                        return null;
                    }
                });
        boolean successful = false;
        getRobot().keyType(null, KeyEvent.VK_ESCAPE);
        try {
            if (m.isShowing()) {
                synchronized (eventLock) {
                    eventLock
                            .wait(TimeoutConstants.
                                    SERVER_TIMEOUT_WAIT_FOR_POPUP);
                }
            }
        } catch (InterruptedException e) {
            // ignore
        } finally {
            successful = EventThreadQueuerJavaFXImpl.invokeAndWait("closeMenu", //$NON-NLS-1$
                    new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            m.removeEventHandler(
                                    WindowEvent.WINDOW_HIDDEN, shownHandler);
                            return !m.isShowing();
                        }
                    });
        }
        return successful;
    }

    /**
     * Closes a menu and waits for it to be closed
     * 
     * @param m
     *            the menu
     * @return true if menu was closed successfully, false if not;
     */
    private boolean waitForMenuToClose(final Menu m) {
        final EventLock eventLock = new EventLock();
        final EventHandler<Event> shownHandler = new EventHandler<Event>() {

            @Override
            public void handle(Event event) {
                synchronized (eventLock) {
                    eventLock.notifyAll();
                }
            }
        };
        EventThreadQueuerJavaFXImpl.invokeAndWait(
                "addCloseHandler", new Callable<Void>() { //$NON-NLS-1$

                    @Override
                    public Void call() throws Exception {
                        m.addEventHandler(Menu.ON_HIDDEN, shownHandler);
                        return null;
                    }
                });
        boolean successful = false;
        getRobot().keyType(null, KeyEvent.VK_ESCAPE);
        try {
            if (m.isShowing()) {
                synchronized (eventLock) {
                    eventLock
                            .wait(TimeoutConstants.
                                    SERVER_TIMEOUT_WAIT_FOR_POPUP);
                }
            }
        } catch (InterruptedException e) {
            // ignore
        } finally {
            successful = EventThreadQueuerJavaFXImpl.invokeAndWait("closeMenu", //$NON-NLS-1$
                    new Callable<Boolean>() {

                        @Override
                        public Boolean call() throws Exception {
                            m.removeEventHandler(Menu.ON_HIDDEN, shownHandler);
                            return !m.isShowing();
                        }
                    });
        }
        return successful;
    }

    @Override
    public IComponent getComponent() {
        IComponent adapt = super.getComponent();
        if (adapt != null 
                && (adapt.getRealComponent() instanceof ContextMenu)) {
            return adapt;
        }

        List<? extends MenuBar> bars = ComponentHandler
                .getAssignableFrom(MenuBar.class);

        switch (bars.size()) {
            case 1:
                setComponent(bars.get(0));
                break;
            case 0:
                setComponent(null);
                break;
            default:
                throw new StepExecutionException("Multiple MenuBars found", //$NON-NLS-1$
                    EventFactory
                            .createActionError(TestErrorEvent.
                                    UNSUPPORTED_OPERATION_IN_TOOLKIT_ERROR));

        }
        return super.getComponent();

    }
}
