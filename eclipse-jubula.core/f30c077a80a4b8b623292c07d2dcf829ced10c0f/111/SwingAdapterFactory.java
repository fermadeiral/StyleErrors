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

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;

/**
 * Example Swing Adapter Factory
 */
public class SwingAdapterFactory implements IAdapterFactory {
    /**
     * a list of supported renderer classes to adapt
     */
    private static final Class[] SUPPORTED_RENDERER_CLASSES = 
            new Class[] { Component.class };

    /** {@inheritDoc} */
    public Class[] getSupportedClasses() {
        return SUPPORTED_RENDERER_CLASSES;
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class targetAdapterClass, Object objectToAdapt) {
        if (ComponentToStringAdapter.class.isAssignableFrom(targetAdapterClass)
                && objectToAdapt instanceof Component) {
            return new ComponentToStringAdapter((Component) objectToAdapt);
        }
        return null;
    }
}
