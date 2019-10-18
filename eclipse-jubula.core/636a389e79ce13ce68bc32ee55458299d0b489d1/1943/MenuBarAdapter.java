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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
/**
 * Adapter for a MenuBar.
 * 
 * @author BREDEX GmbH
 * @created 10.2.2014
 */
public class MenuBarAdapter extends JavaFXComponentAdapter<MenuBar> implements
        IMenuComponent {

    /**
     * Creates an adapter for a MenuBar.
     * 
     * @param objectToAdapt
     *            the object which needs to be adapted
     */
    public MenuBarAdapter(MenuBar objectToAdapt) {
        super(objectToAdapt);
    }

    
    /**
     * Returns the adapted Menus of this MenuBar
     * @return the Menus
     */
    @Override
    public IMenuItemComponent[] getItems() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getItems", //$NON-NLS-1$
                new Callable<IMenuItemComponent[]>() {

                    @Override
                    public IMenuItemComponent[] call() throws Exception {
                        List<Menu> menus = getRealComponent().getMenus();
                        if (menus.size() > 0) {
                            List<IMenuItemComponent> adapters =
                                    new ArrayList<>();
                            for (int i = 0; i < menus.size(); i++) {
                                Menu item = menus.get(i);
                                if (item.isVisible()) {
                                    adapters.add(new MenuBarItemAdapter(
                                            item));
                                }
                            }
                            return adapters.toArray(
                                    new IMenuItemComponent[adapters.size()]);
                        }

                        return null;
                    }
                });
    }

    @Override
    public int getItemCount() {
        return getItems().length;
    }

}
