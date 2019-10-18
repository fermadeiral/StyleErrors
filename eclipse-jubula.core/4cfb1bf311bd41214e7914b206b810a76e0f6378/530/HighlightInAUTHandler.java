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
package org.eclipse.jubula.client.ui.rcp.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.commands.AUTHighlightComponentCommand;
import org.eclipse.jubula.client.core.commands.AUTModeChangedCommand;
import org.eclipse.jubula.client.core.communication.AUTConnection;
import org.eclipse.jubula.client.core.events.AUTEvent;
import org.eclipse.jubula.client.core.events.IAUTEventListener;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.internal.BaseConnection.NotConnectedException;
import org.eclipse.jubula.client.ui.handlers.AbstractSelectionBasedHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.communication.internal.message.AUTHighlightComponentMessage;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.widgets.Display;

/**
 * @author BREDEX GmbH
 * @created 27.04.2005
 */
public class HighlightInAUTHandler 
    extends AbstractSelectionBasedHandler implements IAUTEventListener {
    /** {@inheritDoc} */
    public void stateChanged(AUTEvent event) {
        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                ErrorHandlingUtil.createMessageDialog((new JBException(
                        Messages.ComponentCouldNotBeFoundInRunningAut,
                        MessageIDs.E_COMPONENT_NOT_FOUND)), null, null);
            }
        });
    }

    /** {@inheritDoc} */
    public Object executeImpl(ExecutionEvent event) {
        IStructuredSelection sSel = getSelection();
        if (AUTModeChangedCommand.getAutMode() 
                == ChangeAUTModeMessage.OBJECT_MAPPING
                && sSel.size() == 1
                && sSel.getFirstElement() 
                instanceof IObjectMappingAssoziationPO) {

            IComponentIdentifier assoCompId = 
                    ((IObjectMappingAssoziationPO) sSel
                    .getFirstElement()).getTechnicalName();
            IComponentIdentifier compId = new ComponentIdentifier();
            if (assoCompId != null) {
                compId.setComponentClassName(
                        assoCompId.getComponentClassName());
                compId.setHierarchyNames(new ArrayList<String>(
                        assoCompId.getHierarchyNames()));
                compId.setNeighbours(new ArrayList<String>(
                        assoCompId.getNeighbours()));
                compId.setSupportedClassName(
                        assoCompId.getSupportedClassName());
                compId.setAlternativeDisplayName(assoCompId
                        .getAlternativeDisplayName());
                compId.setProfile(assoCompId.getProfile());
            }
            AUTHighlightComponentCommand response = 
                    new AUTHighlightComponentCommand(
                    this);
            try {
                AUTHighlightComponentMessage message = 
                        new AUTHighlightComponentMessage();
                message.setComponent(compId);
                AUTConnection.getInstance().request(message, response, 5000);
            } catch (NotConnectedException nce) {
                // HERE: notify the listeners about unsuccessful mode change
            } catch (CommunicationException ce) {
                // HERE: notify the listeners about unsuccessful mode change
            }
        }
        return null;
    }
}
