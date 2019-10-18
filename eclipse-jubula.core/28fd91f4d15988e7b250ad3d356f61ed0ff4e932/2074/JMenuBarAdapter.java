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

import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IMenuItemComponent;

/**
 * Implementation of the menu interface for adapting the <code>JMenuBar</code>.
 * In Swing we have three implementations of the menu interface because
 * the Interface is used for the <code>JMenubar</code>, the <code>JPopupMenu</code> and the <code>JMenu</code>.
 * All these behave same in the implementation.
 * 
 * @author BREDEX GmbH
 *
 */
public class JMenuBarAdapter extends AbstractComponentAdapter
    implements IMenuComponent {
    /** The JMenuBar */
    private JMenuBar m_menuBar;
    
    /**
     * @param objectToAdapt 
     */
    public JMenuBarAdapter(Object objectToAdapt) {
        m_menuBar = (JMenuBar) objectToAdapt;
    }
        
    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {

        return m_menuBar;
    }
    /**
     * {@inheritDoc}
     */
    public IMenuItemComponent[] getItems() {
        Object[] menus = m_menuBar.getSubElements();
        List<IMenuItemComponent> adapters = 
                new LinkedList<IMenuItemComponent>();
        for (int i = 0; i < menus.length; i++) {
            if (menus[i] instanceof JMenuItem) {
                adapters.add(new JMenuItemAdapter(menus[i]));
            }
        }
        IMenuItemComponent[] allitems = new IMenuItemComponent[adapters.size()];
        adapters.toArray(allitems);
        return allitems;
    }

    /**
     * {@inheritDoc}
     */
    public int getItemCount() {
        return m_menuBar.getMenuCount();
    }

}
