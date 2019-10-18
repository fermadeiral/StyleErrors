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
package org.eclipse.jubula.client.ui.rcp.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.ui.rcp.search.SearchResultPage;
import org.eclipse.jubula.client.ui.rcp.search.result.BasicSearchResult.SearchResultElement;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBookView;

/**
 * This util is for the getting the SearchResultPage from an event
 * and also selecting test cases in a SearchResultPage
 * @author BREDEX GmbH
 *
 */
public class SearchPageUtils {
    
    /**
     * Private constructor
     */
    private SearchPageUtils() {
        
    }
    /**
     * @param event The event.
     * @return The search result page from the given event.
     */
    public static SearchResultPage getSearchResultPage(ExecutionEvent event) {
        SearchResultPage resultPage = null;
        IWorkbenchPart activePart = HandlerUtil.getActivePart(event);
        if (activePart instanceof PageBookView) {
            PageBookView pageView = (PageBookView) activePart;
            IPage currentPage = pageView.getCurrentPage();
            if (currentPage instanceof SearchResultPage) {
                resultPage = (SearchResultPage) currentPage;
            }
        }
        return resultPage;
    }

    /**
     * Select the Test Cases, which can be used for changing CTDS column usage.
     * @param page The search result page.
     * @param oldSelection The old selection.
     * @param testCases The list of valid Test Cases.
     */
    public static void selectTestCases(SearchResultPage page,
            List<SearchResultElement> oldSelection,
            List<ITestCasePO> testCases) {
        // create a new list for selection
        List<SearchResultElement> newSelection =
                new ArrayList<SearchResultElement>();
        for (SearchResultElement resultElement: oldSelection) {
            if (resultElement.getData() instanceof Long
                    && testCasesContainId(testCases,
                            (Long) resultElement.getData())) {
                newSelection.add(resultElement);
            }
        }
        // set the new list for selection
        page.setSelection(new StructuredSelection(newSelection));
    }

    /**
     * @param testCases The list of valid Test Cases
     * @param id The persistence (JPA / EclipseLink) ID.
     * @return True, if the given list of Test Cases contains a Test Case
     *         with the given ID.
     */
    private static boolean testCasesContainId(
            List<ITestCasePO> testCases, Long id) {
        for (ITestCasePO tc: testCases) {
            if (id.equals(tc.getId())) {
                return true;
            }
        }
        return false;
    }

}
