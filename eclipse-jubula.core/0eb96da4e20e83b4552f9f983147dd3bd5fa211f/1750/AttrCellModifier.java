/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.properties.dialogs.attributes.provider;

import java.util.Map;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jubula.client.teststyle.properties.dialogs.attributes.EditAttributeDialog;
import org.eclipse.swt.widgets.TableItem;


/**
 * @author marcell
 * @created Oct 22, 2010
 */
public class AttrCellModifier implements ICellModifier {

    /** attributes */
    private Map<String, String> m_attributes;
    /** TreeViewer of this modifier */
    private TableViewer m_view;

    /**
     * 
     * @param attributes
     *            The modifier needs this aswell.
     * @param v
     *            It also needs the viewer to refresh the tree.
     */
    public AttrCellModifier(Map<String, String> attributes, TableViewer v) {
        this.m_attributes = attributes;
        this.m_view = v;
    }

    /**
     * {@inheritDoc}
     */
    public boolean canModify(Object element, String property) {
        return property.equals(EditAttributeDialog.VALUE_COLUMN);
    }

    /**
     * {@inheritDoc}
     */
    public Object getValue(Object element, String property) {
        return m_attributes.get(element);
    }

    /**
     * {@inheritDoc}
     */
    public void modify(Object element, String property, Object value) {
        // Element must be casted, because its an tableitem but contains the
        // data I need
        TableItem item = (TableItem)element;
        m_attributes.put((String)item.getData(), (String)value);
        
        // The tableviewer which contains this element must be refreshed
        m_view.refresh();
    }
}
