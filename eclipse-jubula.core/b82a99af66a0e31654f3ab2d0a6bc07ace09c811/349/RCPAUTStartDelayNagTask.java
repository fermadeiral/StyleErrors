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
package org.eclipse.jubula.client.ui.rcp.dialogs.nag;

import java.util.TimerTask;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent;
import org.eclipse.jubula.client.core.agent.AutRegistrationEvent.RegistrationStatus;
import org.eclipse.jubula.client.core.agent.IAutRegistrationListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.AutState;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IAutStateListener;
import org.eclipse.jubula.client.ui.constants.ContextHelpIds;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.dialogs.NagDialog;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;


/**
 * Displays a nag dialog if a given AUT does not start in good time.
 * 
 * @author BREDEX GmbH
 * @created 23.03.2009
 */
public class RCPAUTStartDelayNagTask extends TimerTask 
    implements IAutStateListener, IAutRegistrationListener {

    /** ID of the AUT expected to start */
    private AutIdentifier m_autId;
    
    /**
     * Constructor
     * 
     * @param autId The ID of the AUT for which this task was created. If the
     *              AUT with this ID does not start in good time, a nag dialog
     *              will appear. May not be <code>null</code>.
     */
    public RCPAUTStartDelayNagTask(AutIdentifier autId) {
        Validate.notNull(autId);
        m_autId = autId;
        AutAgentRegistration.getInstance().addListener(this);
        DataEventDispatcher.getInstance().addAutStateListener(this, false);
    }
    
    /** {@inheritDoc} */
    public void run() {
        dispose();
        Plugin.getDisplay().asyncExec(new Runnable() {
            /** {@inheritDoc} */
            public void run() {
                NagDialog.runNagDialog(
                    Plugin.getDisplay().getActiveShell(), "InfoNagger.DelayedRCPAUTStart",  //$NON-NLS-1$
                    ContextHelpIds.RCP_AUT_START_DELAY_DIALOG);
            }
        });
    }

    /**
     * Removes listeners registered by the receiver.
     */
    private void dispose() {
        DataEventDispatcher.getInstance().removeAutStateListener(this);
        AutAgentRegistration.getInstance().removeListener(this);
    }

    /** {@inheritDoc} */
    public void handleAutStateChanged(AutState state) {
        switch (state) {
            case running:
                cancel();
                break;
            case notRunning:
                cancel();
                break;
            default:
                break;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public boolean cancel() {
        dispose();
        return super.cancel();
    }

    /**
     * {@inheritDoc}
     */
    public void handleAutRegistration(AutRegistrationEvent event) {
        if (m_autId.equals(event.getAutId()) 
                && event.getStatus() == RegistrationStatus.Register) {
            cancel();
        }
    }
}
