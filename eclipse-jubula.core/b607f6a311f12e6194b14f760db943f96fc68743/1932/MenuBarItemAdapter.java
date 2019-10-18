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

import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.SkinBase;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.listener.ComponentHandler;

/**
 * Adapter for a Menu in a MenuBar. This is handled as MenuItem to realize the
 * opening of this Menu.
 * 
 * @author Bredex GmbH
 * @created 10.3.2014
 */
public class MenuBarItemAdapter extends MenuItemAdapter<Menu> {
    /**
     * Creates an adapter for a MenuBarItem.
     * 
     * @param objectToAdapt
     *            the object which needs to be adapted
     */
    public MenuBarItemAdapter(Menu objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    protected void clickMenuItem() {

        Node menuButton = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "clickMenuBarItem", //$NON-NLS-1$ 
                new Callable<Node>() {

                    @Override
                    public Node call() throws Exception {
                        List<? extends MenuBar> bars = ComponentHandler
                                .getAssignableFrom(MenuBar.class);
                        MenuBar menuBar = bars.get(0);
                        SkinBase<?> menuBarSkin = (SkinBase<?>) menuBar.
                                getSkin();
                        Parent buttonBox = (Parent) menuBarSkin.
                                getChildren().get(0);
                        IMenuItemComponent[] items =
                                new MenuBarAdapter(menuBar).getItems();
                        int index = -1;
                        for (int i = 0; i < items.length; i++) {
                            if (getRealComponent()
                                    .equals(items[i].getRealComponent())) {
                                index = i;
                                break;
                            }
                        }
                        return buttonBox.getChildrenUnmodifiable().get(index);
                    }
                });
        getRobot().click(menuButton, null);
    }
}
