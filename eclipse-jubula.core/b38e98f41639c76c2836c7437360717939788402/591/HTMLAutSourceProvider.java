/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.sourceprovider;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.events.DataEventDispatcher;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMAUTListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMStateListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.IOMWindowsListener;
import org.eclipse.jubula.client.core.events.DataEventDispatcher.OMState;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.utils.HTMLAutWindowManager;
import org.eclipse.jubula.communication.internal.message.html.OMSelWinResponseMessage;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.ISources;
/**
 * Provides variables related to the status of an HTML AuT.
 * 1. is it an HTML AuT
 * 2. the window count of the browser
 * @author BREDEX GmbH
 */
public class HTMLAutSourceProvider extends AbstractJBSourceProvider implements
        IOMAUTListener, IOMStateListener, IOMWindowsListener {
    /**
     * the id of this source provider
     */
    public static final String ID = "org.eclipse.jubula.client.ui.rcp.sourceprovider.HTMLAutSourceProvider"; //$NON-NLS-1$
    
    /** 
     * ID of variable that indicates whether the client is currently connected 
     * to an AUT Agent
     */
    public static final String IS_HTML_AUT = 
        "org.eclipse.jubula.client.ui.rcp.variable.isHtmlAut"; //$NON-NLS-1$

    /** 
     * ID of variable that indicates whether the client is currently connecting 
     * to an AUT Agent
     */
    public static final String WINDOW_TITLES = 
        "org.eclipse.jubula.client.ui.rcp.variable.html.windowCount"; //$NON-NLS-1$
    
    /** is it an HTML AUT in OMM */
    private boolean m_isHTMLAut = false;
    
    /**
     * Constructor for adding listeners to the DataEventDispatcher
     */
    public HTMLAutSourceProvider() {
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.addOMAUTListener(this, false);
        dispatch.addOMStateListener(this, true);
        dispatch.addAUTWindowsListener(this, false);
        HTMLAutWindowManager.getInstance();
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        DataEventDispatcher dispatch = DataEventDispatcher.getInstance();
        dispatch.removeOMAUTListener(this);
        dispatch.removeOMStateListener(this);
        dispatch.removeAUTWindowsListener(this);
    }

    /**
     * {@inheritDoc}
     */
    public Map getCurrentState() {
        Map<String, Object> values = new HashMap<String, Object>();

        values.put(IS_HTML_AUT, m_isHTMLAut);
        values.put(WINDOW_TITLES, new LinkedList<String>());
        return values;
    }

    /**
     * {@inheritDoc}
     */
    public String[] getProvidedSourceNames() {
        return new String[] { IS_HTML_AUT };
    }

    /**
     * {@inheritDoc}
     */
    public void handleAUTChanged(AutIdentifier identifier) {
        if (identifier != null) {
            IProjectPO project = GeneralStorage.getInstance().getProject();
            IAUTMainPO aut = AutAgentRegistration.getAutForId(identifier,
                    project);
            String toolkit = aut.getToolkit();
            m_isHTMLAut = toolkit
                    .equalsIgnoreCase(CommandConstants.HTML_TOOLKIT);
        } else {
            m_isHTMLAut = false;
        }
        fireModeChanged();
    }

    /**
     * {@inheritDoc}
     */
    public void handleOMStateChanged(OMState state) {
        if (state == OMState.notRunning) {
            m_isHTMLAut = false;
        }
    }

    /**
     * Fires a source changed event for <code>IS_HTML_AUT</code>.
     */
    private void fireModeChanged() {
        gdFireSourceChanged(ISources.WORKBENCH, IS_HTML_AUT, m_isHTMLAut);
    }

    /**
     * {@inheritDoc}
     */
    public void handleAUTChanged(String[] windowTitles) {
        LinkedList<String> listOfTitles = new LinkedList<String>();
        for (int i = 0; i < windowTitles.length; i++) {
            listOfTitles.add(windowTitles[i]);
        }
        gdFireSourceChanged(ISources.WORKBENCH, WINDOW_TITLES, listOfTitles);
    }

    /** {@inheritDoc} */
    public void handleNewWindowSelected(OMSelWinResponseMessage msg) {
        // we aren't interested yet...
    }

}
