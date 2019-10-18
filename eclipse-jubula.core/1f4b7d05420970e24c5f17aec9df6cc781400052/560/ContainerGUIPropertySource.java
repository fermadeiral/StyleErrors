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
package org.eclipse.jubula.client.ui.rcp.controllers.propertysources;

import org.eclipse.jubula.client.core.model.IAbstractContainerPO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author BREDEX GmbH
 */
public class ContainerGUIPropertySource
    extends AbstractNodePropertySource {

    /** cached property descriptor for name */
    private IPropertyDescriptor m_namePropDesc = null;
    
    /**
     * @param container the node
     */
    public ContainerGUIPropertySource(IAbstractContainerPO container) {
        super(container);
    }

    /** {@inheritDoc} */
    protected void initPropDescriptor() {
        if (!getPropertyDescriptorList().isEmpty()) {
            clearPropertyDescriptors();
        }
        // Name
        if (m_namePropDesc == null) {
            m_namePropDesc = new TextPropertyDescriptor(
                    new ElementNameController(),
                    Messages.ContainerGUIPropertySourceName);
        }
        addPropertyDescriptor(m_namePropDesc);
        // Comment
        super.initPropDescriptor();
    }
}
