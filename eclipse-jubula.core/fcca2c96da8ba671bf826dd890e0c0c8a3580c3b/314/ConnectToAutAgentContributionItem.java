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
package org.eclipse.jubula.client.ui.rcp.contributionitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.handlers.AUTAgentConnectHandler;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager;
import org.eclipse.jubula.client.ui.rcp.utils.AutAgentManager.AutAgent;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.tools.internal.constants.EnvConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;


/**
 * Populates the dropdown list for the "Connect to AUT Agent" toolbar item.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class ConnectToAutAgentContributionItem 
    extends CompoundContributionItem {

    /** {@inheritDoc} */
    protected IContributionItem[] getContributionItems() {
        List<IContributionItem> contributionItems = 
                new ArrayList<IContributionItem>();
        int envPort = EnvironmentUtils.getAUTAgentEnvironmentPortNo();
        SortedSet<AutAgent> autAgents = new TreeSet<AutAgentManager.AutAgent>();
        if (envPort > 0) {
            autAgents.add(new AutAgent(EnvConstants.LOCALHOST_ALIAS, envPort));
        }
        contributionItems.add(new Separator());
        AutAgentManager serverMgr = AutAgentManager.getInstance();
        autAgents.addAll(serverMgr.getAutAgents());
        // read all servers from preference store
        for (AutAgent autAgent : autAgents) {
            String name = autAgent.getName();
            Integer port = autAgent.getPort();
            String itemName = NLS.bind(
                    Messages.ConnectToAutAgentPulldownItemName, new Object[] {
                        name, port });
            Map<String, Object> params = new HashMap<String, Object>();
            
            params.put(AUTAgentConnectHandler.AUT_AGENT_NAME_TO_CONNECT, name);
            params.put(AUTAgentConnectHandler.AUT_AGENT_PORT_TO_CONNECT, 
                    String.valueOf(port));
            contributionItems.add(CommandHelper.createContributionItem(
                    RCPCommandIDs.CONNECT_TO_AUT_AGENT, params,
                    itemName, CommandContributionItem.STYLE_CHECK));
        }
        
        return contributionItems.toArray(
                new IContributionItem [contributionItems.size()]);
    }
}