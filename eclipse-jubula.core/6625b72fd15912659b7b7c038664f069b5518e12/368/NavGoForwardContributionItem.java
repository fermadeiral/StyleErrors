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

import java.util.SortedMap;

import org.eclipse.jubula.client.ui.rcp.businessprocess.JBNavigationHistory;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;

/**
 * Contribution items for the Go Forward command
 * @author BREDEX GmbH
 *
 */
public class NavGoForwardContributionItem extends AbstractNavContributionItem {

    @Override
    protected SortedMap<Integer, String> getItems() {
        return JBNavigationHistory.getInstance().getContribItems(false, false);
    }

    @Override
    protected String getCommID() {
        return RCPCommandIDs.GO_FORWARD;
    }

}
