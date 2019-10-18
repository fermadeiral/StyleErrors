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
package org.eclipse.jubula.client.ui.rcp.contributionitems;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.utils.HTMLAutWindowManager;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
/**
 * Populates the dropdown list for the HTML window chooser toolbar item
 * @author BREDEX GmbH
 *
 */
public class ChooseHTMLWindowContributionItem extends CompoundContributionItem {
    /**
     * {@inheritDoc}
     */
    protected IContributionItem[] getContributionItems() {
        String[] titles = HTMLAutWindowManager.getInstance().getWindowTitles();
        List<IContributionItem> contributionItems = 
                new ArrayList<IContributionItem>();
        for (int i = 0; i < titles.length; i++) {
            Map<String, Object> params = new HashMap<String, Object>();
            params.put("org.eclipse.jubula.client.ui.rcp.commands.html.ChooseAuTWindow.parameter.openWindow", titles[i]); //$NON-NLS-1$ 
                    
            IContributionItem test = CommandHelper
                .createContributionItem(RCPCommandIDs.CHOOSE_HTML_WINDOW, 
                        params, titles[i], CommandContributionItem.STYLE_CHECK);
            contributionItems.add(test); 
        }
        return contributionItems
                .toArray(new IContributionItem[contributionItems.size()]);
    }


}
