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
package org.eclipse.jubula.client.ui.rcp.wizards.refactor.param;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;

/**
 * This validator checks a list of Test Cases before it can be used to
 * change the CTDS column usage.
 *
 * @author BREDEX GmbH
 */
public class TestCasesValidator {

    /** The current project ID. */
    private Long m_projectId = GeneralStorage
            .getInstance()
            .getProject()
            .getId();

    /**
     * True, if all Test Cases are using the same Central Test Data Set
     * and this Central Test Data Set has got one or more columns,
     * otherwise false.
     */
    private boolean m_isReferencedCtdsOk = false;

    /** The list of referenced Test Cases with different CTDS. */
    private List<IExecTestCasePO> m_execsWithDifferentCTDS =
            new ArrayList<IExecTestCasePO>();

    /** The list of Test Cases with a specification not defined in the current project. */
    private List<ITestCasePO> m_testCasesWithSpecNotDefinedInCurrentProject =
            new ArrayList<ITestCasePO>();

    /** The list of valid Test Cases. */
    private List<ITestCasePO> m_validTestCases =
            new ArrayList<ITestCasePO>();

    /**
     * Creates the distinct invalid and valid lists from the given list of Test Cases.
     * @param testCases The list of Test Cases to check.
     * @see #getExecsWithDifferentCTDS()
     * @see #getTCsWithSpecNotDefinedInCurrentProject()
     * @see #getValidTestCases()
     */
    public TestCasesValidator(Collection<ITestCasePO> testCases) {
        IParameterInterfacePO dataCube = null;
        for (ITestCasePO testCase: testCases) {
            IParameterInterfacePO currentDataCube =
                    testCase.getReferencedDataCube();
            if (dataCube == null && currentDataCube != null) {
                dataCube = currentDataCube;
                if (dataCube.getParameterListSize() == 0) {
                    return;
                }
            } else if (currentDataCube == null
                    || !dataCube.equals(currentDataCube)) {
                return;
            }
            boolean isValid = false;
            if (testCase instanceof IExecTestCasePO) {
                IExecTestCasePO exec = (IExecTestCasePO) testCase;
                isValid = checkSpecDefinedInCurrentProject(exec)
                        && checkHasOnlyOneCTDS(exec);
            } else {
                // should be a specification Test Case
                ISpecTestCasePO spec = (ISpecTestCasePO) testCase;
                isValid = checkSpecDefinedInCurrentProject(spec);
            }
            if (isValid) {
                m_validTestCases.add(testCase);
            }
        }
        m_isReferencedCtdsOk = true;
    }

    /**
     * @return True, if all Test Cases are using the same Central Test Data Set
     *         and this Central Test Data Set has got one or more columns,
     *         otherwise false.
     */
    public boolean isReferencedDataCubeOk() {
        return m_isReferencedCtdsOk;
    }

    /**
     * Add invalid Test Case automatically to list returned by
     * {@link #getTCsWithSpecNotDefinedInCurrentProject()}.
     * @param exec The execution Test Case.
     * @return True, if the specification Test Case of the given execution Test Case
     *         is defined in the current project, otherwise false.
     */
    private boolean checkSpecDefinedInCurrentProject(IExecTestCasePO exec) {
        if (m_projectId.equals(exec.getSpecTestCase()
                .getParentProjectId())) {
            return true;
        }
        m_testCasesWithSpecNotDefinedInCurrentProject.add(exec);
        return false;
    }

    /**
     * Add invalid Test Case automatically to list returned by
     * {@link #getTCsWithSpecNotDefinedInCurrentProject()}.
     * @param spec The specification Test Case.
     * @return True, if the specification Test Case is defined in the
     *         current project, otherwise false.
     */
    private boolean checkSpecDefinedInCurrentProject(ISpecTestCasePO spec) {
        if (m_projectId.equals(spec.getParentProjectId())) {
            return true;
        }
        m_testCasesWithSpecNotDefinedInCurrentProject.add(spec);
        return false;
    }

    /**
     * @return The list of Test Cases with a specification not defined in the current project.
     */
    public List<ITestCasePO> getTCsWithSpecNotDefinedInCurrentProject() {
        return m_testCasesWithSpecNotDefinedInCurrentProject;
    }

    /**
     * Add invalid Test Case automatically to list returned by
     * {@link #getExecsWithDifferentCTDS()}.
     * @param exec The execution Test Case to check.
     * @return True, if the specification Test Case has no CTDS,
     *         or it is the same as the CTDS at the execution Test Case.
     */
    private boolean checkHasOnlyOneCTDS(IExecTestCasePO exec) {
        if (exec.getSpecTestCase().getReferencedDataCube() == null
                || exec.getReferencedDataCube()
                        == exec.getSpecTestCase().getReferencedDataCube()) {
            return true;
        }
        m_execsWithDifferentCTDS.add(exec);
        return false;
    }

    /**
     * @return The list of execution Test Cases with different CTDS.
     */
    public List<IExecTestCasePO> getExecsWithDifferentCTDS() {
        return m_execsWithDifferentCTDS;
    }

    /**
     * @return True, if all invalid lists are empty, otherwise false.
     *         A Test Case is invalid, if the specification Test Case is not defined in
     *         the current project or if the specification of a referenced
     *         Test Case use a different CTDS.
     */
    public boolean areAllTestCasesOk() {
        return m_testCasesWithSpecNotDefinedInCurrentProject.isEmpty()
                && m_execsWithDifferentCTDS.isEmpty();
    }

    /**
     * @return The list of valid Test Cases.
     */
    public List<ITestCasePO> getInvalidTestCases() {
        List<ITestCasePO> testCases = new ArrayList<ITestCasePO>();
        testCases.addAll(m_testCasesWithSpecNotDefinedInCurrentProject);
        testCases.addAll(m_execsWithDifferentCTDS);
        return testCases;
    }

    /**
     * @return The list of valid Test Cases.
     */
    public List<ITestCasePO> getValidTestCases() {
        return m_validTestCases;
    }

}
