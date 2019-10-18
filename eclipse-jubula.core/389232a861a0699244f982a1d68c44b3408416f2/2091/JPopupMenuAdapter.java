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
package org.eclipse.jubula.rc.swing.tester.adapter;

import java.util.LinkedList;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;
/**
 * Implementation of the menu interface for adapting the <code>JPopupMenu</code>.
 * In Swing we have three implementations of the menu interface because
 * the Interface is used for the <code>JMenubar</code>, the <code>JPopupMenu</code> and the <code>JMenu</code>.
 * All these behave same in the implementation.
 * 
 * @author BREDEX GmbH
 *
 */
public class JPopupMenuAdapter extends AbstractComponentAdapter
    implements IMenuComponent {
    /** */
    private JPopupMenu m_contextMenu;
    
    /**
     * 
     * @param adaptee 
     */
    public JPopupMenuAdapter(Object adaptee) {
        m_contextMenu = (JPopupMenu) adaptee;
    }
    
    /** {@inheritDoc} */
    public Object getRealComponent() {
        return m_contextMenu;
    }

    /** {@inheritDoc} */
    public IMenuItemComponent[] getItems() {
        Object[] menuItems = m_contextMenu.getSubElements();
        List<JMenuItemAdapter> adapters = new LinkedList<JMenuItemAdapter>();
        for (int i = 0; i < menuItems.length; i++) {
            if (menuItems[i] instanceof JMenuItem) {
                adapters.add(new JMenuItemAdapter(menuItems[i]));
            }
        }
        IMenuItemComponent[] allitems = new IMenuItemComponent[adapters.size()];
        adapters.toArray(allitems);
        return allitems;
    }

    /** {@inheritDoc} */
    public int getItemCount() {
        return m_contextMenu.getSubElements().length;
    }

    
}
