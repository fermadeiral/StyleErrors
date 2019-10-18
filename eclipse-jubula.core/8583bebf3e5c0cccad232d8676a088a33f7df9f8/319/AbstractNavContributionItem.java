/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.contributionitems;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jubula.client.ui.rcp.handlers.NavigateHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * Abstract class to provide navigation contribution items
 * @author BREDEX GmbH
 *
 */
public abstract class AbstractNavContributionItem
        extends CompoundContributionItem {

    /**
     * @return the (position => name) map of the navigation items
     */
    protected abstract SortedMap<Integer, String> getItems();

    /**
     * @return the command id
     */
    protected abstract String getCommID();

    @Override
    protected IContributionItem[] getContributionItems() {
        SortedMap<Integer, String> items = getItems();
        IContributionItem[] result = new IContributionItem[items.size()];
        int num = 0;
        Map<String, Object> params = new HashMap<>();
        for (Integer key : items.keySet()) {
            params.put(NavigateHandler.NAVPARAM_ID,
                    NavigateHandler.JUMP + Integer.toString(key));
            result[num] = CommandHelper
                    .createContributionItem(getCommID(), params,
                        items.get(key), CommandContributionItem.STYLE_PUSH);
            num++;
        }
        return result;
    }
}