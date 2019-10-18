/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.views;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jubula.client.ui.handlers.CopyPropertyValueHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.IPropertySheetEntry;
import org.eclipse.ui.views.properties.PropertySheetPage;
import org.eclipse.ui.views.properties.PropertySheetSorter;

/**
 * @author BREDEX GmbH
 * @created 23.11.2011
 */
public class NonSortedPropertySheetPage extends PropertySheetPage {
    
    /** The system clipboard */
    private Clipboard m_clipboard;
    
    /** The copy property value handler */
    private CopyPropertyValueHandler m_copyPropertyValueHandler;
    
    /** Constructor */
    public NonSortedPropertySheetPage() {
        setSorter(new NonSortingPropertySheetSorter());
    }
    
    @Override
    public void createControl(Composite parent) {
        super.createControl(parent);

        m_clipboard = new Clipboard(getControl().getDisplay());
        m_copyPropertyValueHandler = new CopyPropertyValueHandler(getControl(), 
                m_clipboard);
        
        // Overwrite the default right-click menu
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.add(m_copyPropertyValueHandler);
        Menu menu = menuMgr.createContextMenu(getControl());
        getControl().setMenu(menu);
    }
   
    
    @Override
    public void selectionChanged(IWorkbenchPart part, ISelection selection) {
        super.selectionChanged(part, selection);
        Control c = getControl();
        if (c instanceof Tree) {
            Tree tre = (Tree) c;
            TreeItem[] items = tre.getItems();
            for (TreeItem item : items) {
                Event e = new Event();
                e.time = (int)System.currentTimeMillis();
                e.item = item;
                e.widget = tre;
                e.type = SWT.Expand;
                tre.notifyListeners(SWT.Expand, e);
                item.setExpanded(true);
            }
        }
    }
    
    /**
     * @author BREDEX GmbH
     * @created 23.11.2011
     */
    private static class NonSortingPropertySheetSorter extends
            PropertySheetSorter {
        @Override
        public int compare(IPropertySheetEntry entryA,
                IPropertySheetEntry entryB) {
            return 0;
        }

        @Override
        public int compareCategories(String categoryA, String categoryB) {
            return 0;
        }

        @Override
        public void sort(IPropertySheetEntry[] entries) {
            // do not sort!
        }
    }
}
