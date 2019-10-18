/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.edit;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jubula.extensions.wizard.model.Parameter;

/**
 * Adds editing support for parameter valuesets
 * 
 * @author BREDEX GmbH
 */
public final class ParameterValueSetEditingSupport extends EditingSupport {
    
    /** The table viewer */
    private final TableViewer m_tableViewer;
    
    /** The celleditor */
    private final CellEditor m_cellEditor;
    
    /**
     * Constructor
     * @param viewer the table viewer
     */
    public ParameterValueSetEditingSupport(TableViewer viewer) {
        super(viewer);
        m_tableViewer = viewer;
        m_cellEditor = new TextCellEditor(viewer.getTable());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected CellEditor getCellEditor(Object element) {
        return m_cellEditor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean canEdit(Object element) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Object getValue(Object element) {
        if (element instanceof Parameter) {
            return ((Parameter) element).getValueSet().toString();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof Parameter && value instanceof String) {
            ((Parameter) element).getValueSet().setElements((String) value);
            m_tableViewer.update(element, null);
        }
    }

}
