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

import org.eclipse.jubula.client.ui.controllers.propertysources.IPropertyController;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedIntText;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 26.10.2005
 */
public class IntegerTextPropertyDescriptor extends TextPropertyDescriptor {
    
    /** emptyAllowed is an empty field considered valid (implicite value is 0) */
    private boolean m_emptyAllowed;
    /** minValue lower value for this field */
    private int m_minValue;
    /** minValue lower value for this field */
    private int m_maxValue;
    
    /**
     * @param id The associated property controller.
     * @param displayName The name to display for the property.
     * @param emptyAllowed is an empty field considered valid (implicite 
     * value is 0)
     * @param minValue lower value for this field
     * @param maxValue minValue lower value for this field
     */
    public IntegerTextPropertyDescriptor(IPropertyController id, 
        String displayName, boolean emptyAllowed, int minValue, int maxValue) {
        
        super(id, displayName);
        m_emptyAllowed = emptyAllowed;
        m_minValue = minValue;
        m_maxValue = maxValue;
    }

    /**
     * The <code>TextPropertyDescriptor</code> implementation of this
     * <code>IPropertyDescriptor</code> method creates and returns a
     * new <code>Text</code> field.
     * @param parent the parent of this widget.
     * @return a <code>Text</code> field.
     */
    public Control createPropertyWidget(Composite parent) {
        CheckedIntText text = new CheckedIntText(parent, SWT.NONE, 
                m_emptyAllowed, m_minValue, m_maxValue);

        return text;
    }

    /**
     * @return Returns the emptyAllowed.
     */
    public boolean isEmptyAllowed() {
        return m_emptyAllowed;
    }

    /**
     * @return Returns the maxValue.
     */
    public int getMaxValue() {
        return m_maxValue;
    }

    /**
     * @return Returns the minValue.
     */
    public int getMinValue() {
        return m_minValue;
    }
}