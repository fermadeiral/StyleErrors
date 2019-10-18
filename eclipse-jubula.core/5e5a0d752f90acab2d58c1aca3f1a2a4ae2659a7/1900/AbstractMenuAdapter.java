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

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

/**
 * Abstract Adapter for MenuItems
 * 
 * @author BREDEX GmbH
 * @created 10.2.2014
 * @param <M> The type of MenuItem
 */
public class AbstractMenuAdapter<M extends MenuItem> 
    extends AbstractComponentAdapter<M> {

    /**
     * 
     * @param objectToAdapt the Object 
     */
    public AbstractMenuAdapter(M objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public ReadOnlyObjectProperty<ContextMenu> getWindow() {
        return getRealComponent().parentPopupProperty();
    }
    
}
