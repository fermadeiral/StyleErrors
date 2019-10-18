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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jubula.client.core.model.IExecTestCasePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ISpecTestCasePO;
import org.eclipse.jubula.client.core.model.ITestCasePO;
import org.eclipse.jubula.client.ui.rcp.dialogs.AbstractEditParametersDialog.Parameter;

/**
 * A data class for parameter names used in the {@link ChangeCtdsColumnUsageWizard}.
 *
 * The constructor takes a list of Test Cases and creates internally
 * a map between a parameter description and the set of the used Test Cases.
 *
 * @author BREDEX GmbH
 */
public class ExistingAndNewParameterData
        implements Comparator<IParamDescriptionPO> {

    /**
     * The map between the data cube parameter description and the set of Test Cases,
     * which are using a parameter description with the same name and type.
     */
    private Map<IParamDescriptionPO, Set<ISpecTestCasePO>> m_map =
            new HashMap<IParamDescriptionPO, Set<ISpecTestCasePO>>();

    /** The Central Test Data Set changing column usage at. */
    private IParameterInterfacePO m_dataCube;

    /** The old parameter description. */
    private IParamDescriptionPO m_oldParamDescription;

    /** The new parameter description. */
    private IParamDescriptionPO m_newParamDescription;

    /**
     * Creates a map between all parameter names and the corresponding
     * Test Cases given by the set of Test Cases.
     * @param testCases The set of execution and specification Test Cases.
     */
    public ExistingAndNewParameterData(Collection<ITestCasePO> testCases) {
        for (ITestCasePO testCase: testCases) {
            for (IParamDescriptionPO param: testCase.getParameterList()) {
                IParameterInterfacePO dataCube =
                        testCase.getReferencedDataCube();
                IParamDescriptionPO dcParam =
                        findParameterByNameAndType(
                                dataCube, param);
                if (dcParam != null && findParameterByType(
                        dataCube, param).size() > 1) {
                    Set<ISpecTestCasePO> paramNameTestCases =
                            m_map.get(dcParam);
                    if (paramNameTestCases == null) {
                        // parameter description with the same name and type has not been found
                        paramNameTestCases =
                                new HashSet<ISpecTestCasePO>();
                        // add new pair of parameter description and set of Test Cases
                        m_map.put(dcParam, paramNameTestCases);
                        if (m_dataCube == null) {
                            m_dataCube = dataCube;
                        }
                    }
                    // add Test Cases to the set corresponding to parameter description
                    paramNameTestCases.add(getSpecTestCase(testCase));
                }
            }
        }
    }

    /**
     * Search for a column in the CTDS matching by name and type.
     * @param dataCube The data cube containing the columns searching in.
     * @param param The parameter description searching for.
     * @return The parameter description of the column in the given CTDS
     *         with the same name and type as the given parameter description,
     *         or null if it has not been found.
     */
    private static IParamDescriptionPO findParameterByNameAndType(
            IParameterInterfacePO dataCube, IParamDescriptionPO param) {
        for (IParamDescriptionPO dcParam : dataCube.getParameterList()) {
            if (param.getName().equals(dcParam.getName())
                    && param.getType().equals(dcParam.getType())) {
                return dcParam;
            }
        }
        return null;
    }

    /**
     * Search for a column in the CTDS matching by type.
     * @param dataCube The data cube containing the columns searching in.
     * @param param The parameter description searching for.
     * @return The parameter description of the column in the given CTDS
     *         with the same name and type as the given parameter description,
     *         or null if it has not been found.
     */
    private static List<IParamDescriptionPO> findParameterByType(
            IParameterInterfacePO dataCube, IParamDescriptionPO param) {
        List<IParamDescriptionPO> paramDescs =
                new ArrayList<IParamDescriptionPO>();
        for (IParamDescriptionPO dcParam : dataCube.getParameterList()) {
            if (param.getType().equals(dcParam.getType())) {
                paramDescs.add(dcParam);
            }
        }
        return paramDescs;
    }

    /**
     * @param testCase The Test Case to check.
     * @return The specification of the execution Test Case, otherwise the
     *         given Test Case.
     */
    private ISpecTestCasePO getSpecTestCase(ITestCasePO testCase) {
        if (testCase instanceof IExecTestCasePO) {
            IExecTestCasePO exec = (IExecTestCasePO) testCase;
            return exec.getSpecTestCase();
        }
        return (ISpecTestCasePO) testCase;
    }

    /**
     * @return An array of all parameter descriptions in ascending order of
     *         names, which can be selected.
     */
    public Object[] getAllParamDescriptions() {
        List<IParamDescriptionPO> paramDescs =
                new ArrayList<IParamDescriptionPO>(m_map.keySet());
        Collections.sort(paramDescs, this);
        return paramDescs.toArray();
    }

    /**
     * @param paramDesc The parameter description as a key for the map.
     * @return An array of Test Cases corresponding to the given parameter description.
     */
    public Object[] getTestCasesOfParamDescription(
            IParamDescriptionPO paramDesc) {
        return m_map.get(paramDesc).toArray();
    }

    /**
     * @param paramDesc The old parameter description to set. Must not be null.
     * @return An array of column names used in the Central Test Data Set
     *         with the same type and a different parameter name than the
     *         given one. If no matching parameter description has been found,
     *         the array has the length of 0.
     */
    public String[] setOldParamDescription(IParamDescriptionPO paramDesc) {
        m_oldParamDescription = paramDesc;
        List<String> columnNames = new ArrayList<String>();
        for (String paramName : m_dataCube.getParamNames()) {
            IParamDescriptionPO currentParamDesc =
                    m_dataCube.getParameterForName(paramName);
            if (!paramDesc.getName().equals(currentParamDesc.getName())
                    && paramDesc.getType().equals(currentParamDesc.getType())) {
                // current parameter has different name and same type
                columnNames.add(paramName);
            }
        }
        return columnNames.toArray(new String[columnNames.size()]);
    }

    /**
     * @return The old parameter description.
     */
    public IParamDescriptionPO getOldParamDescription() {
        return m_oldParamDescription;
    }

    /**
     * @return The set of Test Cases, which are indirectly selected
     *         by the old parameter description, or null if nothing is selected.
     */
    public Set<ISpecTestCasePO> getSelectedTestCases() {
        return m_map.get(m_oldParamDescription);
    }

    /**
     * Sets the new parameter description, if it has been found in
     * the selectable list of parameters, otherwise {@link #getNewParamDescription()}==null.
     * 
     * @param paramName The new parameter name to set.
     */
    public void setNewParamName(String paramName) {
        if (m_dataCube != null) {
            m_newParamDescription = m_dataCube.getParameterForName(paramName);
        }
    }

    /**
     * @return The new parameter description, if it has been set correctly by
     *         {@link #setNewParamName(String)}, otherwise null.
     */
    public IParamDescriptionPO getNewParamDescription() {
        return m_newParamDescription;
    }

    /**
     * @return True, if the old and new parameter description have been set,
     *         otherwise false.
     */
    public boolean isComplete() {
        return m_oldParamDescription != null && m_newParamDescription != null;
    }

    /**
     * @param testCase The Test Case getting the new parameter for.
     * @return The list of new parameters for the given Test Case with the
     *         renamed new parameter.
     */
    public List<Parameter> getNewParametersFromSpecTestCase(
            ISpecTestCasePO testCase) {
        ArrayList<Parameter> params = new ArrayList<Parameter>();
        for (IParamDescriptionPO param : testCase.getParameterList()) {
            Parameter newParam = new Parameter(param);
            if (param.getName().equals(m_oldParamDescription.getName())) {
                newParam.setName(m_newParamDescription.getName());
            }
            params.add(newParam);
        }
        return params;
    }

    /**
     * Sort parameter descriptions in ascending order of names used by
     * {@link #getAllParamDescriptions()}.
     * @param o1 The first parameter description to be compared.
     * @param o2 The second parameter description to be compared.
     * @return A negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     * {@inheritDoc}
     */
    public int compare(IParamDescriptionPO o1, IParamDescriptionPO o2) {
        return o1.getName().compareTo(o2.getName());
    }

}
