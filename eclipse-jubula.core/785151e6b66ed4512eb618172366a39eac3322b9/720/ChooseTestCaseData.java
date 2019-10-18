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

package org.eclipse.jubula.client.ui.rcp.wizards.search.refactor.pages;

import java.util.Set;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;

/**
 * Data class for storing the old and new specification Test Cases.
 *
 * @author BREDEX GmbH
 */
public class ChooseTestCaseData {

    /** The old execution Test Cases. */
    private Set<IExecTestCasePO> m_oldExecTestCases;

    /** The old specification Test Case. */
    private ISpecTestCasePO m_oldSpecTestCase;

    /** The new specification Test Case. */
    private ISpecTestCasePO m_newSpecTestCase;

    /**
     * @param execTestCases The old execution Test Cases, for which to
     *                      choose a new specification Test Case.
     */
    public ChooseTestCaseData(Set<IExecTestCasePO> execTestCases) {
        this.m_oldExecTestCases = execTestCases;
        if (!execTestCases.isEmpty()) {
            this.m_oldSpecTestCase = m_oldExecTestCases.iterator().next()
                    .getSpecTestCase();
        }
    }

    /**
     * @return The list of old execution Test Cases given to the constructor.
     * @see #ChooseTestCaseData(Set)
     */
    public Set<IExecTestCasePO> getOldExecTestCases() {
        return m_oldExecTestCases;
    }

    /**
     * @return The old specification Test Case used by all
     *         execution Test Cases given to the constructor,
     *         or null if the given list is empty.
     */
    public ISpecTestCasePO getOldSpecTestCase() {
        return m_oldSpecTestCase;
    }

    /**
     * @param newSpecTestCase The new specification Test Case.
     */
    public void setNewSpecTestCase(ISpecTestCasePO newSpecTestCase) {
        this.m_newSpecTestCase = newSpecTestCase;
    }

    /**
     * @return The new specification Test Case given by
     *         {@link #setNewSpecTestCase(ISpecTestCasePO)}.
     */
    public ISpecTestCasePO getNewSpecTestCase() {
        return m_newSpecTestCase;
    }

}
