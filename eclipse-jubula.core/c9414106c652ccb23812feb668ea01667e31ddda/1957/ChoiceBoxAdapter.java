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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.concurrent.Callable;

import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComboComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.MenuTester;
import org.eclipse.jubula.rc.javafx.tester.util.compatibility.WindowsUtil;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;

/**
 * Adapter for a ChoiceBox. This Control is like a ComboBox, but is not
 * editable. To display the Items the ComboBox uses a ContextMenu, unlike the
 * ComboBox which uses a ListView.
 * 
 * @author BREDEX GmbH
 * @created 24.3.2014
 */
public class ChoiceBoxAdapter extends JavaFXComponentAdapter<ChoiceBox<?>>
        implements IComboComponent {

    /**
     * Creates an object with the adapted ChoiceBox.
     * 
     * @param objectToAdapt
     *            this must be an object of the Type <code>ChoiceBox</code>
     */
    public ChoiceBoxAdapter(ChoiceBox<?> objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public String getText() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getText", new Callable<String>() { //$NON-NLS-1$

                    @Override
                    public String call() throws Exception {
                        ObservableList<Node> children = getRealComponent()
                                .getChildrenUnmodifiable();
                        Label text = null;
                        for (Node node : children) {
                            if (node instanceof Label) {
                                text = (Label) node;
                            }
                        }
                        if (text != null) {
                            return text.getText();
                        }
                        return null;
                    }
                });
    }

    @Override
    public boolean isEditable() {
        // The ChoiceBox is not editable
        return false;
    }

    @Override
    public void selectAll() {
        // Not possible, because the ChoiceBox is not editable and therefore
        // there is
        // no Text which could be select.
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public int getSelectedIndex() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "getSelectedIndex", new Callable<Integer>() { //$NON-NLS-1$

                    @Override
                    public Integer call() throws Exception {
                        return getRealComponent().getSelectionModel()
                                .getSelectedIndex();
                    }
                });
    }

    @Override
    public void select(int index) {
        MenuTester tester = openMenu();
        tester.selectMenuItemByIndexpath(Integer.toString(
                IndexConverter.toUserIndex(index)));
    }

    @Override
    public void input(String text, boolean replace) {
        // Not possible, because the ChoiceBox is not editable.
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public String[] getValues() {
        MenuTester tester = openMenu();
        IMenuItemComponent[] items = tester.getMenuAdapter().getItems();
        String[] result = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            result[i] = items[i].getText();
        }
        closeMenu((ContextMenu) tester.getComponent().getRealComponent());
        return result;
    }

    /**
     * Opens the list of choices for the ChoiceBox.
     * 
     * @return A MenuTester object to select Items. This is Possible because the
     *         ChoiceBox uses a ContextMenu.
     */
    private MenuTester openMenu() {
        getRobot().click(getRealComponent(), null);
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "select", new Callable<MenuTester>() { //$NON-NLS-1$

                    @Override
                    public MenuTester call() throws Exception {
                        MenuTester menuTester = null;
                        Iterator<Window> iter = WindowsUtil.getWindowIterator();
                        long timeout = TimeoutConstants.
                                SERVER_TIMEOUT_WAIT_FOR_POPUP;
                        long done = System.currentTimeMillis() + timeout;
                        long now;
                        do {
                            if (!iter.hasNext()) {
                                iter = WindowsUtil.getWindowIterator();
                            }
                            Window w = iter.next();
                            if (w instanceof ContextMenu
                                    && ((ContextMenu) w).getOwnerNode().equals(
                                            getRealComponent())) {
                                menuTester = new MenuTester();
                                menuTester.setComponent(w);
                                break;
                            }
                            now = System.currentTimeMillis();
                            timeout = done - now;
                        } while (timeout > 0);
                        return menuTester;
                    }
                });
    }
    
    /**
     * Closes the Menu of a ChoiceBox and waits for it to be closed
     * 
     * @param m
     *            the menu
     * @return true if menu was closed successfully, false if not;
     */
    private boolean closeMenu(final ContextMenu m) {
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
}
