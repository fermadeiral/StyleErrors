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
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;


/**
 * This interface is for the functionality of menus. This is only for the
 * not visible part of menus which only contains the next menuitems in the
 * Hierarchy of the menu.
 * 
 * @author BREDEX GmbH
 */
public interface IMenuComponent extends IComponent {

    /**
     * 
     * @return the items of the menu
     */
    public IMenuItemComponent[] getItems();

    /**
     * Gets the amount of items in the menu
     * 
     * @return the amount of Items in the menu (with Seperator)
     */
    public int getItemCount();
}
