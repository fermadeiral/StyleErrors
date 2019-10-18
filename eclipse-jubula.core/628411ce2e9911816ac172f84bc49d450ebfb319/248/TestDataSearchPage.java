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
package org.eclipse.jubula.client.ui.rcp.search.page;

import org.eclipse.jubula.client.core.model.NodeMaker;
import org.eclipse.jubula.client.core.model.PoMaker;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.data.SearchOptions;
import org.eclipse.jubula.client.ui.rcp.search.data.TypeName;
import org.eclipse.jubula.client.ui.rcp.search.query.TestDataQuery;
import org.eclipse.search.ui.ISearchQuery;


/**
 * @author BREDEX GmbH
 * @created Aug 10, 2010
 */
public class TestDataSearchPage extends AbstractSearchPage {
    /** The static list of checked state for controls. */
    private static ButtonSelections enablements = new ButtonSelections();

    /**
     * The static search data for this test data search page
     * while Jubula is running.
     */
    private static SearchOptions searchData;

    static {
        // create the static search data object
        TypeName[] searchableTypes = new TypeName[] {
            new TypeName(PoMaker.getTestDataCubeClass(), true),
            new TypeName(NodeMaker.getSpecTestCasePOClass(), true),
            new TypeName(NodeMaker.getExecTestCasePOClass(), true),
            new TypeName(NodeMaker.getCapPOClass(), true)
        };
        searchData = new SearchOptions(
                Messages.SimpleSearchPageResultTestData,
                searchableTypes, null);
    }

    /** {@inheritDoc} */
    protected ButtonSelections getButtonSelections() {
        return enablements;
    }

    /** {@inheritDoc} */
    protected ISearchQuery getNewQuery() {
        return new TestDataQuery(new SearchOptions(searchData));
    }

    /** {@inheritDoc} */
    protected SearchOptions getSearchData() {
        return searchData;
    }
}