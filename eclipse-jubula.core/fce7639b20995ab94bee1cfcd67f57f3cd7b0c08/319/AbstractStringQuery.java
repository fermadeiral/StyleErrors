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
package org.eclipse.jubula.client.ui.rcp.search.query;

import org.eclipse.jubula.client.ui.rcp.search.data.SearchOptions;


/**
 * @author BREDEX GmbH
 * @created May 02, 2013
 */
public abstract class AbstractStringQuery extends AbstractQuery {

    /** The search options. */
    private SearchOptions m_searchData;

    /** The text finder providing the search operation. */
    private TextFinder m_textFinder;

    /**
     * @param searchData The search options. Must not be null.
     * @param viewId The view Id to open. Can be null.
     */
    protected AbstractStringQuery(SearchOptions searchData, String viewId) {
        m_searchData = searchData;
        m_textFinder = new TextFinder(
                searchData.getSearchString(), searchData.getOperation());
    }

    /**
     * @return The search options.
     */
    protected SearchOptions getSearchOptions() {
        return m_searchData;
    }

    /**
     * @param text The text searching in.
     * @return True, if the search string given to the constructor has been
     *         found in the given text respecting the search operation,
     *         otherwise false.
     */
    protected boolean matchSearchString(String text) {
        return m_textFinder.matchSearchString(text);
    }

    /**
     * {@inheritDoc}
     */
    public String getLabel() {
        return getTimestamp() + " " + m_searchData.getSearchName(); //$NON-NLS-1$
    }

}
