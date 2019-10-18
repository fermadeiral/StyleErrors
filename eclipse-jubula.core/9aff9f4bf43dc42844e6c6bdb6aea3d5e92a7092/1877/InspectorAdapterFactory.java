/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.inspector.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.inspector.ui.controller.InspectedPropertySource;
import org.eclipse.jubula.client.inspector.ui.model.InspectorTreeNode;
import org.eclipse.ui.views.properties.IPropertySource;

/**
 * @author BREDEX GmbH
 */
public class InspectorAdapterFactory implements IAdapterFactory {
    /** types for which adapters are available */
    private final Class[] m_types = { InspectorTreeNode.class };

    /** Constructor */
    public InspectorAdapterFactory() {
    }

    /** {@inheritDoc} */
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        if (adapterType == IPropertySource.class) {
            if (adaptableObject instanceof InspectorTreeNode) {
                return new InspectedPropertySource(
                        (InspectorTreeNode)adaptableObject);
            }
        }
        return null;
    }

    /** {@inheritDoc} */
    public Class[] getAdapterList() {
        return m_types;
    }
}