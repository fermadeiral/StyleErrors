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
package org.eclipse.jubula.client.ui.rcp.contributionitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jubula.client.ui.rcp.command.parameters.ProfileTypeParameter;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
/**
 * Contribution item for the Profiles.
 *
 * @author BREDEX GmbH
 * @created 21.12.2015
 */
public class ProfileContributionItem extends CompoundContributionItem {

    @Override
    protected IContributionItem[] getContributionItems() {
        List<IContributionItem> contributionItems = 
                new ArrayList<IContributionItem>();
        for (String profileName : ProfileTypeParameter.getValues()) {
            ProfileTypeParameter ptp = new ProfileTypeParameter();
            ptp.setType(profileName);
            
            Map<String, ProfileTypeParameter> params = new HashMap<>();
            if (this.getId().equals("menu:org.eclipse.jubula.client.ui.rcp.editors.ObjectMappingEditor.dropdown")) { //$NON-NLS-1$
                params.put("org.eclipse.jubula.client.ui.rcp.command.parameters.profilesParameter", ptp); //$NON-NLS-1$
                contributionItems.add(CommandHelper.createContributionItem(
                        RCPCommandIDs.SET_PROFILE, 
                        params,
                        profileName, CommandContributionItem.STYLE_CHECK));
            } else {
                params.put("org.eclipse.jubula.client.ui.rcp.command.parameters.filterProfilesParameter", ptp); //$NON-NLS-1$
                contributionItems.add(CommandHelper.createContributionItem(
                        RCPCommandIDs.FILTER_PROFILE, 
                        params,
                        profileName, CommandContributionItem.STYLE_CHECK));
            }
            if (profileName == ProfileTypeParameter.GLOBAL) {
                contributionItems.add(new Separator());
            }
        }
        return contributionItems.toArray(
                new IContributionItem [contributionItems.size()]);
    }

}
