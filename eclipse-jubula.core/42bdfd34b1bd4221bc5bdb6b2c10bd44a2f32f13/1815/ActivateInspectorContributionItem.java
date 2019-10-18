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
package org.eclipse.jubula.client.inspector.ui.contribution;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jubula.client.inspector.ui.constants.InspectorCommandConstants;
import org.eclipse.jubula.client.inspector.ui.handlers.ActivateInspectorHandler;
import org.eclipse.jubula.client.inspector.ui.provider.sourceprovider.InspectableAutSourceProvider;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.services.ISourceProviderService;


/**
 * Dropdown menu for "Activate Inspector".
 *
 * @author BREDEX GmbH
 * @created Mar 23, 2010
 */
public class ActivateInspectorContributionItem 
        extends CompoundContributionItem {

    /**
     * 
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    protected IContributionItem[] getContributionItems() {
        List<IContributionItem> contributionItems = 
            new ArrayList<IContributionItem>();

        ISourceProviderService service = PlatformUI.getWorkbench().getService(
                    ISourceProviderService.class);
        ISourceProvider inspectableAutsProvider = 
            service.getSourceProvider(
                    InspectableAutSourceProvider.INSPECTABLE_AUTS);
        
        if (inspectableAutsProvider != null) {
            Map<?, ?> state = inspectableAutsProvider.getCurrentState();
            if (state != null) {
                Collection<AutIdentifier> inspectableAuts = 
                    (Collection<AutIdentifier>)state.get(
                            InspectableAutSourceProvider.INSPECTABLE_AUTS);
                for (AutIdentifier autId : inspectableAuts) {
                    contributionItems.add(createItem(autId));
                }
            }
        }
        
        
        return contributionItems.toArray(
                new IContributionItem[contributionItems.size()]);
    }

    /**
     * Creates and returns a contribution item representing the command
     * to activate inspection for a specific Running AUT.
     * 
     * @param autId The ID of the Running AUT to be inspected by selecting the
     *              returned contribution item.
     * @return the created contribution item.
     */
    private IContributionItem createItem(AutIdentifier autId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(ActivateInspectorHandler.AUT_ID, autId);
        StringBuilder labelBuilder = new StringBuilder();
        labelBuilder.append(autId.getExecutableName());
        return CommandHelper.createContributionItem(
                InspectorCommandConstants.ACTIVATE_INSPECTOR_COMMAND_ID,
                params, labelBuilder.toString(),
                CommandContributionItem.STYLE_PUSH);
    }

}
