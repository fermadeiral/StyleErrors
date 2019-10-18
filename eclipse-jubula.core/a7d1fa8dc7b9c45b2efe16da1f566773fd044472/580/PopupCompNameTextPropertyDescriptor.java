/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jubula.client.core.businessprocess.IComponentNameCache;
import org.eclipse.jubula.client.ui.controllers.propertysources.IPropertyController;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.editors.IJBEditor;
import org.eclipse.jubula.client.ui.rcp.widgets.CompNamePopupTextCellEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * This Class extends the <code>TextPropertyDescriptor</code>.
 * It has more options to set properties.
 *
 * @author BREDEX GmbH
 * @created 27.01.2005
 */
public class PopupCompNameTextPropertyDescriptor extends PropertyDescriptor {

    /** the filter (= the selectedt compType) */
    private String m_filter;
    
    /**
     * Creates a property descriptor with the given id and display name.
     * 
     * @param id the id of the property
     * @param displayName the name to display for the property
     * @param filter The filter (= the selected compType)
     */
    public PopupCompNameTextPropertyDescriptor(IPropertyController id, 
            String displayName, String filter) {
        
        super(id, displayName);
        m_filter = filter;
    }
    
    /**
     * {@inheritDoc}
     */
    public CellEditor createPropertyEditor(Composite parent) {
        IComponentNameCache compCache = null;
        IEditorPart activeEditor = Plugin.getActiveEditor();
        if (activeEditor instanceof IJBEditor) {
            compCache = ((IJBEditor)activeEditor).getCompNameCache();
        }

        CompNamePopupTextCellEditor editor = 
            new CompNamePopupTextCellEditor(compCache, parent);
        editor.setFilter(m_filter);
        return editor; 

    }
    
}
