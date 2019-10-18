/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jubula.client.core.businessprocess.CompNameManager;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IComponentNamePO;
import org.eclipse.jubula.client.core.model.IObjectMappingAssoziationPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.search.query.ShowWhereUsedComponentNameQuery;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.ISearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.NodeSearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.ObjectMappingSearchResultElementAction;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.jubula.client.ui.utils.CommandHelper;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.CompoundContributionItem;
import org.eclipse.ui.menus.CommandContributionItem;

/**
 * @author BREDEX GmbH
 * @created 30.06.2016
 */
public class ShowInOMEContributionItem extends CompoundContributionItem {

    /**  The name of the "Show in Object Mapping Editor" command */
    private static final String SHOW_IN_OME_COMMANDNAME =
            "org.eclipse.jubula.client.ui.rcp.commands.ShowInOME"; //$NON-NLS-1$
    
    /** The parameter id of the ID of the association to jump to */
    private static final String JUMP_ID_COMMANDID = "org.eclipse.jubula.client.ui.rcp.commands.ShowInOME.parameter.jumpId"; //$NON-NLS-1$
    
    /** The parameter id of the type of action that should perform the jump to */
    private static final String JUMP_ACTION_COMMANDID = "org.eclipse.jubula.client.ui.rcp.commands.ShowInOME.parameter.jumpAction"; //$NON-NLS-1$

    /** The parameter id of the aut of which the object mapping editor should be opened */
    private static final String JUMP_AUTNAME_COMMANDID = "org.eclipse.jubula.client.ui.rcp.commands.ShowInOME.parameter.jumpAutName"; //$NON-NLS-1$
    
    /** The ObjectMappingSearchResultElementAction */
    private static final String ACTION_OBJ_MAP = "ObjectMappingSearchResultElementAction"; //$NON-NLS-1$
    
    /** The NodeSearchResultElementAction */
    private static final String ACTION_NODE_ELEM = "NodeSearchResultElementAction"; //$NON-NLS-1$
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected IContributionItem[] getContributionItems() {
        ISelection selection = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getActivePage().getSelection();
        if (selection instanceof TreeSelection) { 
            TreeSelection treeSelection = (TreeSelection) selection;
            Object element = treeSelection.getFirstElement();
            if (treeSelection.size() == 1 
                    && element instanceof IComponentNamePO) {
                IComponentNamePO componentName = (IComponentNamePO) element;
                if (CompNameManager.getInstance()
                        .getUsageByGuid(componentName.getGuid()) > 0) {
                    List<SearchResultElement<?>> resultElements = 
                            getOMEOccurences(componentName);
                    if (resultElements != null) {
                        IContributionItem[] contributionArr =
                                createContributionItemArray(resultElements,
                                        componentName);
                        if (contributionArr != null) {
                            return contributionArr;
                        }
                    }
                }
            }
        }
        return new IContributionItem[] {
                CommandHelper.createContributionItem(
                        SHOW_IN_OME_COMMANDNAME, null,
                        "(no occurrences found)", //$NON-NLS-1$
                        CommandContributionItem.STYLE_PUSH)
        };
    }

    /**
     * Creates the array of contribution items that will be shown in the
     * Show In OME menu
     * 
     * @param resultElements the elements that should be added to the menu
     * @param componentName the component the contribution array should be 
     * created for
     * @return the contribution items array
     */
    private IContributionItem[] createContributionItemArray(
            List<SearchResultElement<?>> resultElements,
            IComponentNamePO componentName) {
        
        List<IContributionItem> assignedContributionItems =
                new ArrayList<>();
        List<IContributionItem> unassignedContributionItems =
                new ArrayList<>();
        List<SearchResultElement<?>> unassignedSearchResults =
                new ArrayList<>();
        
        Set<String> autNames = new HashSet<>();
        
        for (SearchResultElement<?> resultElement 
                : resultElements) {
            ISearchResultElementAction<?> action = resultElement.getAction();
            if (action instanceof ObjectMappingSearchResultElementAction) {
                String resultElementName = resultElement.getName();
                autNames.add(resultElementName
                        .substring(0, resultElementName.indexOf('/') - 1));
                createAssignedContributionItem(assignedContributionItems,
                        resultElement);
            } else if (action instanceof NodeSearchResultElementAction) {
                unassignedSearchResults.add(resultElement);
            }
        }
        
        
        if (unassignedSearchResults.size() > 0) {
            Map<IAUTMainPO, String> auts = determineAUTs(componentName);
            if (auts.size() > 0) {
                for (IAUTMainPO aut : auts.keySet()) {
                    if (!autNames.contains(aut.getName())) {
                        createUnassignedContributionItem(
                            unassignedContributionItems, aut);
                    }
                }
            }
        }
        
        List<IContributionItem> allContributionItems = new ArrayList<>();
        allContributionItems.addAll(unassignedContributionItems);
        if (unassignedContributionItems.size() > 0 
                && assignedContributionItems.size() > 0) {
            allContributionItems.add(new Separator());
        }
        allContributionItems.addAll(assignedContributionItems);
        
        if (allContributionItems.size() > 0) {
            return allContributionItems
                    .toArray(new IContributionItem[
                             allContributionItems.size()]);
        }
        return null;
    }


    /**
     * Determines the AUTs the given component name is being used in
     * @param componentName the component name the AUTs are being used for
     * @return the list of AUTs the given componentName is used in
     */
    private Map<IAUTMainPO, String> determineAUTs(
            IComponentNamePO componentName) {
        
        Map<IAUTMainPO, String> auts = new HashMap<>();
        for (IAUTMainPO aut : GeneralStorage.getInstance()
                .getProject().getAutMainList()) {
            assocLoop:
            for (IObjectMappingAssoziationPO assoc 
                    : aut.getObjMap().getMappings()) {
                for (String compNameGuid : assoc.getLogicalNames()) {
                    if (componentName.getGuid().equals(compNameGuid)) {
                        auts.put(aut, componentName.getGuid());
                        break assocLoop;
                    }
                }
            }
        }
        return auts;
    }

    /**
     * Creates a contribution item for the given result element
     * @param assignedContributionItems the list of contributionitems the
     * contribution item is supposed to be added to
     * @param resultElement the result element the contributionitem should be
     * created for
     */
    private void createAssignedContributionItem(
            List<IContributionItem> assignedContributionItems,
            SearchResultElement<?> resultElement) {
        Map<String, Object> params =
                new HashMap<>();
        params.put(JUMP_ID_COMMANDID, String
                .valueOf(resultElement.getData()));
        params.put(JUMP_ACTION_COMMANDID, ACTION_OBJ_MAP);
        IContributionItem contributionItem = 
                CommandHelper.createContributionItem(
                        SHOW_IN_OME_COMMANDNAME,
                        params,
                        resultElement.getName(),
                        CommandContributionItem.STYLE_PUSH);
        assignedContributionItems.add(contributionItem);
    }
    

    /**
     * Creates a contribution item for the given aut
     * @param unassignedContributionItems the list of contributionitems the
     * contribution item is supposed to be added to
     * @param aut the aut the contributionitem should be created for
     */
    private void createUnassignedContributionItem(
            List<IContributionItem> unassignedContributionItems,
            IAUTMainPO aut) {
        Map<String, Object> params = new HashMap<>();
        params.put(JUMP_AUTNAME_COMMANDID, aut.getName());
        params.put(JUMP_ACTION_COMMANDID, ACTION_NODE_ELEM);
        IContributionItem contributionItem = 
                CommandHelper.createContributionItem(
                        SHOW_IN_OME_COMMANDNAME,
                        params,
                        aut.getName() + " (AUT)", //$NON-NLS-1$
                        CommandContributionItem.STYLE_PUSH);
        unassignedContributionItems.add(contributionItem);
    }
    
    /**
     * Gets all occurences of the given ComponentNamePO in the OMEs
     * @param componentName the Component Name whose occurences in OMEs should
     *                      be found
     * @return the occurences of the Component Name in OMEs
     */
    private List<SearchResultElement<?>> getOMEOccurences(
            IComponentNamePO componentName) {
        
        if (componentName != null) {
            ShowWhereUsedComponentNameQuery query = 
                    new ShowWhereUsedComponentNameQuery(componentName);
            query.run(new NullProgressMonitor());
            ISearchResult searchResult = query.getSearchResult();
            List<SearchResultElement<?>> objectMappingResults =
                    new ArrayList<SearchResultElement<?>>();
            if (searchResult instanceof BasicSearchResult) {
                BasicSearchResult basicResult = 
                        (BasicSearchResult) query.getSearchResult();
                @SuppressWarnings("unchecked")
                List<SearchResultElement<?>> resultList = 
                        basicResult.getResultList();
                for (SearchResultElement<?> element : resultList) {
                    ISearchResultElementAction<?> action = element.getAction();
                    if (action instanceof ObjectMappingSearchResultElementAction
                        || action instanceof NodeSearchResultElementAction) {
                        
                        objectMappingResults.add(element);
                    }
                }
            }
            return objectMappingResults;
        }
        return null;
    }

}
