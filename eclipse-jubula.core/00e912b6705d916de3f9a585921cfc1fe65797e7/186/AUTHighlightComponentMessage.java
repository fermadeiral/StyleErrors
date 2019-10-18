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
 * The response of a <code>ChangeAUTModeMessage</code>. <br>
 * Contains the new mode of the AUTServer.
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class AUTHighlightComponentMessage extends Message {
    /** component to highlight */
    private IComponentIdentifier m_component;

    /** default constructor */
    public AUTHighlightComponentMessage() {
        super();
    }

    /** {@inheritDoc} */
    public String getCommandClass() {
        return CommandConstants.AUT_HIGHLIGHT_COMMAND;
    }

    /** @return Returns the component. */
    public IComponentIdentifier getComponent() {
        return m_component;
    }

    /**
     * @param component
     *            The component to set.
     */
    public void setComponent(IComponentIdentifier component) {
        m_component = component;
    }
}