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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;

/**
 * Adapter for the ContextMenu. 
 * 
 * @author BREDEX GmbH
 * @created 10.2.2014
 */
public class ContextMenuAdapter extends AbstractComponentAdapter<ContextMenu> 
                                implements IMenuComponent {
    /**
     * Creates an adapter for a Menu.
     * 
     * @param objectToAdapt
     *            the object which needs to be adapted
     */
    public ContextMenuAdapter(ContextMenu objectToAdapt) {
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
                            IMenuItemComponent[] itemAdapters = 
                                    new IMenuItemComponent[items.size()];
                            for (int i = 0; i < items.size(); i++) {
                                itemAdapters[i] = new MenuItemAdapter<MenuItem>(
                                        items.get(i));
                            }
                            return itemAdapters;
                        }

                        return null;
                    }
                });
    }

    @Override
    public int getItemCount() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getItemCount", //$NON-NLS-1$
                new Callable<Integer>() {

                    @Override
                    public Integer call() throws Exception {
                        return getRealComponent().getItems().size();
                    }

                });
    }

    @Override
    public ReadOnlyObjectProperty<ContextMenu> getWindow() {
        return new ReadOnlyObjectWrapper<ContextMenu>(getRealComponent());
    }

}
