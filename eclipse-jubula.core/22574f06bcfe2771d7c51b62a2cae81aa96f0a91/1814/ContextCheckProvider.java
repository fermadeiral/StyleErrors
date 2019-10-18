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

import java.util.Map;

import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jubula.client.teststyle.checks.contexts.BaseContext;


/**
 * @author marcell
 * @created Oct 21, 2010
 */
public class ContextCheckProvider implements ICheckStateProvider {

    /** Contexts which will be checked for the state */
    private Map<BaseContext, Boolean> m_contexts;

    /**
     * @param contexts
     *            Contexts of this checks
     */
    public ContextCheckProvider(Map<BaseContext, Boolean> contexts) {
        m_contexts = contexts;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isChecked(Object element) {
        return m_contexts.get(element);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGrayed(Object element) {
        return false;
    }

}
