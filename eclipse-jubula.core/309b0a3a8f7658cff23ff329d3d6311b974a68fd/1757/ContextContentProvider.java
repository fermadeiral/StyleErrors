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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;


/**
 * This class provides the attributes.
 */
public final class ContextContentProvider implements 
    IStructuredContentProvider {

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    // Here is nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    // Here is nothing aswell
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    public Object[] getElements(Object inputElement) {
        Map<BaseContext, Boolean> elements = 
            (Map<BaseContext, Boolean>)inputElement;
        @SuppressWarnings("rawtypes")
        List<BaseContext> keys = new ArrayList(elements.keySet());
        Collections.sort(keys);
        return keys.toArray(new BaseContext[keys.size()]);

    }
}