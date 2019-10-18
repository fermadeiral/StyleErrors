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
package org.eclipse.jubula.ext.rc.common.adapter;

import java.awt.Component;

import org.eclipse.jubula.rc.common.adaptable.ITextRendererAdapter;

/**
 * The java.awt.Component Adapter
 */
public class ComponentToStringAdapter implements ITextRendererAdapter {
    /**
     * the component
     */
    private final Component m_component;

    /**
     * Constructor
     * 
     * @param c
     *            the component to adapt
     */
    public ComponentToStringAdapter(Component c) {
        m_component = c;
    }

    /** {@inheritDoc} */
    public String getText() {
        return m_component.toString();
    }
}