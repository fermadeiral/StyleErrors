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
package org.eclipse.jubula.client.teststyle.properties.dialogs.contexts.provider;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;
import org.eclipse.swt.graphics.Image;


/**
 * @author marcell
 * @created Oct 22, 2010
 */
public class ContextLabelProvider implements ITableLabelProvider {
    
    /** Index for the name column */
    private static final int NAME_COLUMN_INDEX = 0;
    /** Index for the value column */
    private static final int DESCRIPTION_COLUMN_INDEX = 1;

    /**
     * {@inheritDoc}
     */
    public Image getColumnImage(Object element, int columnIndex) {
        // No images :(
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public String getColumnText(Object element, int columnIndex) {
        BaseContext context = (BaseContext)element;
        if (columnIndex == NAME_COLUMN_INDEX) { 
            return context.getName();
        } else if (columnIndex == DESCRIPTION_COLUMN_INDEX) {
            return context.getDescription();
        }
        return null; // when the columnIndex doesn't match
    }

    /**
     * {@inheritDoc}
     */
    public void addListener(ILabelProviderListener listener) {
    // 
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    // 
    }

    /**
     * {@inheritDoc}
     */
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void removeListener(ILabelProviderListener listener) {
    // 
    }

}
