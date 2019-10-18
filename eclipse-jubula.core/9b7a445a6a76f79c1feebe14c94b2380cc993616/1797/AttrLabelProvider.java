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

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * @author marcell
 * @created Oct 22, 2010
 */
public class AttrLabelProvider implements ITableLabelProvider {

    /** Index for the description column */
    private static final int DESCRIPTION_COLUMN_INDEX = 0;
    /** Index for the value column */
    private static final int VALUE_COLUMN_INDEX = 1;

    /** Attributes */
    private Map<String, String> m_attributes;
    /** descriptions */
    private Map<String, String> m_descriptions;

    /**
     * @param attributes
     *            The label provides needs to know them to display the value
     *            properly
     * @param descriptions
     *            the descriptions of the attribute to deliver the column
     */
    public AttrLabelProvider(Map<String, String> attributes,
            Map<String, String> descriptions) {
        m_attributes = attributes;
        m_descriptions = descriptions;
    }

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
        switch (columnIndex) {
            case DESCRIPTION_COLUMN_INDEX:
                return m_descriptions.get(element);
            case VALUE_COLUMN_INDEX:
                return m_attributes.get(element);
            default:
                return null; // when the columnIndex doesn't exist
        }

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
