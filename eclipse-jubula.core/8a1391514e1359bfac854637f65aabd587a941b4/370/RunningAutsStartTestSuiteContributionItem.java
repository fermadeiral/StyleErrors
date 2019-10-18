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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jubula.client.core.agent.AutAgentRegistration;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.businessprocess.ChooseTestSuiteBP;
import org.eclipse.jubula.client.ui.rcp.constants.RCPCommandIDs;
import org.eclipse.jubula.client.ui.rcp.handlers.StartTestSuiteHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;


/**
 * Populates the dropdown list for the "Start Test Suite" toolbar item based on
 * currently selected AUT IDs / Running AUTs.
 *
 * @author BREDEX GmbH
 * @created Feb 2, 2010
 */
public class RunningAutsStartTestSuiteContributionItem extends
        CompoundContributionItem {

    /**
     * {@inheritDoc}
     */
    protected IContributionItem[] getContributionItems() {
        List<IContributionItem> contributionItems = 
            new ArrayList<IContributionItem>();
        Collection<AutIdentifier> selectedAutIds = 
            getSelectedRunningAuts();
        
        IProjectPO currentProject = 
            GeneralStorage.getInstance().getProject();
        if (currentProject != null) {
            if (selectedAutIds.isEmpty()) {
                selectedAutIds.addAll(AutAgentRegistration.getInstance()
                        .getRegisteredAuts());
            }
            Map<IAUTMainPO, Collection<AutIdentifier>> runningAuts = 
                AutAgentRegistration
                    .getRunningAuts(currentProject, selectedAutIds);

            List<ITestSuitePO> startableSuiteList = new ArrayList<ITestSuitePO>(
                    ChooseTestSuiteBP.getInstance().getAllTestSuites());
            Collections.sort(startableSuiteList, new NodePOComparator());

            for (ITestSuitePO suite : startableSuiteList) {
                Collection<AutIdentifier> autIds = runningAuts.get(suite
                        .getAut());
                if (autIds != null) {
                    for (AutIdentifier autId : autIds) {
                        IContributionItem item = createItem(suite, autId);
                        contributionItems.add(item);
                    }
                }
                contributionItems.add(new Separator());
            }
        }

        if (!contributionItems.isEmpty()) {
            // Remove last separator
            contributionItems.remove(contributionItems.size() - 1);
        }
        
        return contributionItems.toArray(
                new IContributionItem [contributionItems.size()]);
    }

    /**
     * 
     * @return all currently selected (in the GUI) Running AUTs. Note that the 
     *         current selection is generally based on the selection provided
     *         by a single view or editor.
     */
    private static Collection<AutIdentifier> getSelectedRunningAuts() {
        Set<AutIdentifier> selectedRunningAuts = new HashSet<AutIdentifier>();
        IWorkbenchWindow activeWindow = 
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow != null) {
            ISelectionService selectionService = 
                activeWindow.getSelectionService();
            ISelection sel = selectionService.getSelection();
            if (sel instanceof IStructuredSelection) {
                IStructuredSelection structuredSel = 
                    ((IStructuredSelection)sel);
                for (Object selectedObj : structuredSel.toArray()) {
                    if (selectedObj instanceof AutIdentifier) {
                        selectedRunningAuts.add((AutIdentifier)selectedObj);
                    }
                }
            }
        }
        
        return selectedRunningAuts;
    }

    /**
     * Creates and returns a contribution item representing the command
     * to start a specific Test Suite.
     * 
     * @param suiteToStart The Test Suite that will be started by selecting
     *                     the returned contribution item.
     * @param autId The ID of the Running AUT for which the Test Suite is valid.
     * @return the created contribution item.
     */
    private static IContributionItem createItem(
            ITestSuitePO suiteToStart, AutIdentifier autId) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put(StartTestSuiteHandler.TEST_SUITE_TO_START, suiteToStart);
        params.put(StartTestSuiteHandler.RUNNING_AUT, autId);
        StringBuilder labelBuilder = new StringBuilder();
        labelBuilder.append(suiteToStart.getAut().getName())
            .append(StringConstants.SPACE)
            .append(StringConstants.LEFT_PARENTHESIS)
            .append(autId.getExecutableName())
            .append(StringConstants.RIGHT_PARENTHESIS)
            .append(StringConstants.SPACE)
            .append(StringConstants.COLON)
            .append(StringConstants.SPACE)
            .append(suiteToStart.getName());
        return CommandHelper.createContributionItem(
                RCPCommandIDs.START_TEST_SUITE, params, labelBuilder
                        .toString(), CommandContributionItem.STYLE_CHECK);
    }
}
