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
package org.eclipse.jubula.client.ui.rcp.search.data;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.search.query.Operation;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;


/**
 * @author BREDEX GmbH
 * @created Jul 26, 2010
 */
public class SearchOptions {

    /** The maximum number of elements in the recent search list. */
    private static final int MAX_RECENT = 12;

    /** A human readable search name for NLS. */
    private final String m_searchNameNLS;

    /** The list of fields this search data is able to search in. */
    private final FieldName[] m_searchableFieldNames;

    /** The list of types this search data is able to search for. */
    private final TypeName[] m_searchableTypes;

    /** the list of recent search strings */
    private final List<String> m_recent;

    /** A temporary list of types this search data is searching for. */
    private List<TypeName> m_selectedSearchableTypes;

    /** The description for this search. */
    private String m_searchName;

    /** The text searching for. */
    private String m_searchString;

    /** The operation mode for searching. */
    private Operation m_operation = Operation.MATCH_CASE;

    /** True, if the nodes have to be selected, otherwise false. */
    private boolean m_hasNodesToBeSelected;

    /** True, if searching in Test Suite Browser has selected, otherwise false. */
    private boolean m_isSearchingInTestSuiteBrowser;

    /** True, if searching in Test Case Browser has selected, otherwise false. */
    private boolean m_isSearchingInTestCaseBrowser;

    /** True, if searching in all Test Case Browsers has selected, otherwise false for master. */
    private boolean m_isSearchingInTestCaseBrowsersAll;

    /** True, if searching in reused project is allowed, otherwise false. */
    private boolean m_isSearchingInReusedProjects;

    /** the project scope to search in */
    private IProjectPO m_project;

    /**
     * Constructor. Default search operation is MATCH_CASE.
     * @param searchNameNLS A human readable search name for NLS.
     * @param searchableTypes The list of node types searching for.
     * @param searchableFieldNames the 
     */
    public SearchOptions(String searchNameNLS,
            TypeName[] searchableTypes, FieldName[] searchableFieldNames) {
        m_searchNameNLS = searchNameNLS;
        m_searchableTypes = searchableTypes;
        m_searchableFieldNames = searchableFieldNames;
        m_recent = new ArrayList<String>(MAX_RECENT);
    }

    /**
     * Constructor used for simple searches.
     */
    public SearchOptions() {
        this(StringConstants.EMPTY, new TypeName[] {}, null);
    }

    /**
     * Copy constructor.
     * @param searchData The search data copying from.
     */
    public SearchOptions(SearchOptions searchData) {
        setProject(GeneralStorage.getInstance().getProject());
        m_searchNameNLS = searchData.m_searchNameNLS;
        m_searchableTypes = searchData.m_searchableTypes;
        m_searchableFieldNames = searchData.getSearchableFieldNames();
        m_selectedSearchableTypes = searchData.m_selectedSearchableTypes;
        m_searchName = searchData.m_searchName;
        m_searchString = searchData.m_searchString;
        m_operation = searchData.m_operation;
        m_recent = searchData.m_recent;
        m_hasNodesToBeSelected = searchData.m_hasNodesToBeSelected;
        m_isSearchingInTestSuiteBrowser =
                searchData.m_isSearchingInTestSuiteBrowser;
        m_isSearchingInTestCaseBrowser =
                searchData.m_isSearchingInTestCaseBrowser;
        m_isSearchingInTestCaseBrowsersAll =
                searchData.m_isSearchingInTestCaseBrowsersAll;
        m_isSearchingInReusedProjects =
                searchData.m_isSearchingInReusedProjects;
    }

    /**
     * Set new search data.
     * @param searchString
     *            the string to search for
     * @param caseSensitive
     *            the case sensitive option
     * @param useRegex
     *            the use regex option
     * @param hasNodesToBeSelected True, if search node has to be selected,
     *            otherwise false.
     * @param isSearchingInTestSuiteBrowser True, if searching in
     *            Test Suite Browser is selected, otherwise false.
     * @param isSearchingInTestCaseBrowser True, if searching in
     *            Test Case Browser is selected, otherwise false.
     * @param isSearchingInTestCaseBrowsersAll True, if searching in
     *            all Test Case Browsers is selected, otherwise false.
     * @param isSearchingInReusedProject True, if searching in reused
     *            project is allowed, otherwise false.
     */
    public void setData(
            String searchString,
            boolean caseSensitive,
            boolean useRegex,
            boolean hasNodesToBeSelected,
            boolean isSearchingInTestSuiteBrowser,
            boolean isSearchingInTestCaseBrowser,
            boolean isSearchingInTestCaseBrowsersAll,
            boolean isSearchingInReusedProject) {
        m_operation = Operation.create(caseSensitive, useRegex);
        m_selectedSearchableTypes = new ArrayList<TypeName>();
        for (TypeName type : m_searchableTypes) {
            if (type.isSelected()) {
                m_selectedSearchableTypes.add(type);
            }
        }
        m_hasNodesToBeSelected = hasNodesToBeSelected;
        m_isSearchingInTestSuiteBrowser = isSearchingInTestSuiteBrowser;
        m_isSearchingInTestCaseBrowser = isSearchingInTestCaseBrowser;
        m_isSearchingInTestCaseBrowsersAll = isSearchingInTestCaseBrowsersAll;
        m_isSearchingInReusedProjects = isSearchingInReusedProject;
        setSearchString(searchString);
    }

    /**
     * @return the searchName
     */
    public String getSearchName() {
        return m_searchName;
    }

    /**
     * @return the caseSensitive
     */
    public Operation getOperation() {
        return m_operation;
    }

    /**
     * @param searchString the searchString to set
     */
    private void setSearchString(String searchString) {
        m_searchString = searchString;
        String scopeName = Messages.SimpleSearchTaskScopeAll;
        if (hasNodesToBeSelected()) {
            scopeName = Messages.SimpleSearchTaskScopeSelectedNodes;
        }
        m_searchName = NLS.bind(
                m_searchNameNLS,
                scopeName,
                searchString);
        if (m_recent.contains(searchString)) {
            m_recent.remove(searchString);
        }
        if (m_recent.size() >= MAX_RECENT) {
            m_recent.remove(getRecent().size() - 1);
        }
        m_recent.add(0, searchString);
    }

    /**
     * @return the searchString
     */
    public String getSearchString() {
        return m_searchString;
    }

    /**
     * @return The recent search list.
     */
    public List<String> getRecent() {
        return m_recent;
    }

    /**
     * @return the typesToSearchFor
     */
    public TypeName[] getSearchableTypes() {
        return m_searchableTypes;
    }

    /**
     * @return the typesToSearchFor
     */
    public List<TypeName> getSelectedSearchableTypes() {
        return m_selectedSearchableTypes;
    }

    /**
     * @return True, if the nodes have to be selected, otherwise false.
     */
    public boolean hasNodesToBeSelected() {
        return m_hasNodesToBeSelected;
    }

    /**
     * @return True, if searching in Test Suite Browser has been selected,
     *         otherwise false.
     */
    public boolean isSearchingInTestSuiteBrowser() {
        return m_isSearchingInTestSuiteBrowser;
    }

    /**
     * @return True, if searching in Test Suite Browser has been selected,
     *         otherwise false.
     */
    public boolean isSearchingInTestCaseBrowser() {
        return m_isSearchingInTestCaseBrowser;
    }

    /**
     * @return True, if searching in all Test Case Browsers has been selected,
     *         otherwise false for master.
     */
    public boolean isSearchinInTestCaseBrowsersAll() {
        return m_isSearchingInTestCaseBrowsersAll;
    }

    /**
     * @return True, if searching in reused projects is allowed, otherwise false.
     */
    public boolean isSearchingInReusedProjects() {
        return m_isSearchingInReusedProjects;
    }

    /**
     * @return the searchableFieldNames
     */
    public FieldName[] getSearchableFieldNames() {
        return m_searchableFieldNames;
    }

    /**
     * @return the project
     */
    public IProjectPO getProject() {
        return m_project;
    }

    /**
     * @param project the project to set
     */
    private void setProject(IProjectPO project) {
        m_project = project;
    }
}