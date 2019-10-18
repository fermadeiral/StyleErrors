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
import java.util.SortedMap;
import java.util.SortedSet;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.ui.rcp.businessprocess.StartAutBP;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.handlers.StartAutHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;


/**
 * Populates the dropdown list for the "Start AUT" toolbar item.
 *
 * @author BREDEX GmbH
 * @created Apr 29, 2009
 */
public class StartAutContributionItem extends CompoundContributionItem {

    /**
     * 
     * {@inheritDoc}
     */
    protected IContributionItem[] getContributionItems() {
        
        SortedMap<IAUTMainPO, SortedSet<IAUTConfigPO>> auts = 
            StartAutBP.getInstance().getAllAUTs();
        List<IContributionItem> contributionItems = 
            new ArrayList<IContributionItem>();
        
        for (IAUTMainPO aut : auts.keySet()) {
            SortedSet<IAUTConfigPO> confs = auts.get(aut);
            if (confs != null) {
                for (IAUTConfigPO conf : confs) {
                    Map<String, Object> params = new HashMap<String, Object>();
                    params.put(StartAutHandler.AUT_TO_START, aut);
                    params.put(StartAutHandler.AUT_CONF_TO_START, conf);
                    String itemName = aut.getName();
                    String autId = conf.getValue(AutConfigConstants.AUT_ID,
                            null);
                    if (autId != null) {
                        itemName += StringConstants.SPACE
                            + StringConstants.LEFT_PARENTHESIS
                            + autId + StringConstants.RIGHT_PARENTHESIS;
                    }
                    itemName += " : " + conf.getName(); //$NON-NLS-1$;
                    contributionItems.add(CommandHelper.createContributionItem(
                            RCPCommandIDs.START_AUT, 
                            params,
                            itemName, CommandContributionItem.STYLE_CHECK));
                }
            }
            contributionItems.add(new Separator());
        }
        
        if (!contributionItems.isEmpty()) {
            // Remove last separator
            contributionItems.remove(contributionItems.size() - 1);
        }

        return contributionItems.toArray(
                new IContributionItem [contributionItems.size()]);
    }

}
