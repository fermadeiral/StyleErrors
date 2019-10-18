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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jubula.extensions.wizard.model.Parameter;

/**
 * Adds editing support for the parameter type
 * 
 * @author BREDEX GmbH
 */
public final class ParameterTypeEditingSupport extends EditingSupport {
    
    /** The possible parameter type choices */
    private static final List<String> COMBO_ITEMS = 
            new ArrayList<>(
                    Arrays.asList(new String[] {"String", "Integer", "Boolean"}) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            );
    
    /** The tableviewer */
    private final TableViewer m_tableViewer;
    
    /** The celleditor */
    private final CellEditor m_cellEditor;
    
    /**
     * The constructor
     * @param viewer the tableviewer that should get this editing support
     */
    public ParameterTypeEditingSupport(TableViewer viewer) {
        super(viewer);
        m_tableViewer = viewer;
        m_cellEditor = new ComboBoxCellEditor(viewer.getTable(), 
                COMBO_ITEMS.toArray(new String[0]));
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
            return COMBO_ITEMS.indexOf(((Parameter) element).getType());
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void setValue(Object element, Object value) {
        if (element instanceof Parameter && value instanceof Integer) {
            Integer val = (Integer) value;
            if (val >= 0 && val < COMBO_ITEMS.size()) {
                ((Parameter) element).setType(COMBO_ITEMS.get(val));
                m_tableViewer.update(element, null);
            }
            
        }
    }

}
