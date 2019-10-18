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
package org.eclipse.jubula.communication.internal.message;

import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;

/**
 * This message is send from the AUTServer to the client after the user has
 * selected a component to inspect. <br>
 * There is no response message.
 * 
 * @author BREDEX GmbH
 * @created 10.06.2009
 */
public class InspectorComponentSelectedMessage extends Message {
    // the data of this message BEGIN
    /** the identifier of the component. */
    private IComponentIdentifier m_componentIdentifier;

    // the data of this message END

    /** default constructor */
    public InspectorComponentSelectedMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.INSPECTOR_COMPONENT_SELECTED_COMMAND;
    }

    /** @return Returns the componentIdentifier. */
    public IComponentIdentifier getComponentIdentifier() {
        return m_componentIdentifier;
    }

    /**
     * @param componentIdentifier
     *            The componentIdentifier to set.
     */
    public void setComponentIdentifier(IComponentIdentifier 
        componentIdentifier) {
        m_componentIdentifier = componentIdentifier;
    }
}