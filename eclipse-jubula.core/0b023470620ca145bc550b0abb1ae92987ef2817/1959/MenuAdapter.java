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
import javafx.scene.control.MenuItem;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Adapter for a Menu. A Menu is a Subclass of MenuItem which represents a
 * sub-menu.
 * 
 * @author BREDEX GmbH
 * @created 10.2.2014
 */
public class MenuAdapter extends AbstractMenuAdapter<Menu> implements
        IMenuComponent {

    /**
     * Creates an adapter for a Menu.
     * 
     * @param objectToAdapt
     *            the object which needs to be adapted
     */
    public MenuAdapter(Menu objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public IMenuItemComponent[] getItems() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getItems", //$NON-NLS-1$
                new Callable<IMenuItemComponent[]>() {

                    @Override
                    public IMenuItemComponent[] call() throws Exception {
                        List<MenuItem> items = getRealComponent().getItems();
                        if (items.size() > 0) {
                            List<IMenuItemComponent> adapters =
                                    new ArrayList<>();
                            for (int i = 0; i < items.size(); i++) {
                                MenuItem item = items.get(i);
                                if (item.isVisible()) {
                                    adapters.add(new MenuItemAdapter<MenuItem>(
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
