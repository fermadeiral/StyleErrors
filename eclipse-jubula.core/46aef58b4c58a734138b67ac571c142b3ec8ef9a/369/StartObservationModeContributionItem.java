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

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jubula.client.core.businessprocess.RunningAutBP;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.handlers.StartObservationModeHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;


/**
 * @author BREDEX GmbH
 * @created Mar 19, 2010
 */
public class StartObservationModeContributionItem extends
        CompoundContributionItem {

    /**
     * {@inheritDoc}
     */
    protected IContributionItem[] getContributionItems() {
        List<IContributionItem> contributionItems = 
            new ArrayList<IContributionItem>();
        
        if (GeneralStorage.getInstance().getProject() != null) {
            for (AutIdentifier autId : RunningAutBP
                    .getListOfDefinedRunningAuts()) {
                contributionItems.add(createItem(autId));
            }
        }

        return contributionItems.toArray(
                new IContributionItem [contributionItems.size()]);
    }

    /**
     * Creates and returns a contribution item representing the command
     * to start the Mapping Mode for a specific Running AUT.
     * 
     * @param autId The ID of the Running AUT for which the Mapping Mode will 
     *              be started.
     * @return the created contribution item.
     */
    private static IContributionItem createItem(AutIdentifier autId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(StartObservationModeHandler.RUNNING_AUT, autId);
        StringBuilder labelBuilder = new StringBuilder();
        labelBuilder.append(autId.getExecutableName());
        return CommandHelper.createContributionItem(
                RCPCommandIDs.START_OBSERVATION_MODE, params,
                labelBuilder.toString(), CommandContributionItem.STYLE_CHECK);
    }
}
