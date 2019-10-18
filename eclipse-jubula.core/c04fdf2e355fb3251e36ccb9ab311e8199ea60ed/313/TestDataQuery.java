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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.businessprocess.TestDataCubeBP;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.INodePO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IPersistentObject;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.ModelParamValueConverter;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.search.data.SearchOptions;
import org.eclipse.jubula.client.ui.rcp.search.data.TypeName;


/**
 * Test data value search query for values of nodes in Test Suite Browser or
 * Test Case Browsers including the central test data.
 * @author BREDEX GmbH
 * @created Apr 29, 2013
 */
public class TestDataQuery
        extends AbstractTraverserQuery {

    /**
     * @param searchData The search data to use for this query.
     */
    public TestDataQuery(SearchOptions searchData) {
        super(searchData, Constants.JB_DATASET_VIEW_ID);
    }

    /**
     * Search in the whole project or in selected nodes for test data using the
     * {@link TextFinder} depending on the {@link SearchOptions} given
     * to the constructor.
     * {@inheritDoc}
     */
    public IStatus run(IProgressMonitor monitor) {
        setMonitor(monitor);
        // search in nodes
        traverse(); // calls operate()
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        // search in central test data
        searchInCentralTestData(collectCentralTestData());
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        finished();
        return Status.OK_STATUS;
    }

    /**
     * Add the given node to the result, if it has the matching type and
     * one or more test data values contain the search string.
     * {@inheritDoc}
     */
    protected boolean operate(INodePO node) {
        if (node instanceof IParamNodePO) {
            // found node with parameters
            if (matchingSearchType(node)) {
                // found node with matching type
                IParamNodePO paramNode = (IParamNodePO) node;
                if (containsTestDataValue(paramNode)) {
                    // found node with test data value, which contains search string
                    add((INodePO) paramNode); // add node to result list
                }
            }
        }
        return true;
    }

    /**
     * Collect central test data.
     * @return The collected set of central test data.
     */
    private Set<IParameterInterfacePO> collectCentralTestData() {
        List<TypeName> types = getSearchOptions()
                .getSelectedSearchableTypes();
        IProgressMonitor monitor = getMonitor();
        monitor.beginTask(
                "Collecting central test data elements...", types.size()); //$NON-NLS-1$
        Set<IParameterInterfacePO> centralTestData =
                new HashSet<IParameterInterfacePO>();
        for (TypeName type : types) {
            Class<? extends IPersistentObject> searchType = type.getType();
            if (!INodePO.class.isAssignableFrom(searchType)
                    && ITestDataCubePO.class.isAssignableFrom(searchType)) {
                IProjectPO cProject = GeneralStorage.getInstance().getProject();
                centralTestData.addAll(Arrays.asList(
                        TestDataCubeBP.getAllTestDataCubesFor(cProject)));
            }
            monitor.worked(1);
            if (monitor.isCanceled()) {
                break;
            }
        }
        return centralTestData;
    }

    /**
     * Search in central test data.
     * @param centralTestData The set of central test data.
     */
    private void searchInCentralTestData(
            Set<IParameterInterfacePO> centralTestData) {
        IProgressMonitor monitor = getMonitor();
        for (IParameterInterfacePO testDataCube : centralTestData) {
            if (containsTestDataValue(testDataCube)) {
                add(testDataCube);
            }
            monitor.worked(1);
            if (monitor.isCanceled()) {
                return;
            }
        }
    }

    /**
     * @param paramObj
     *            the param obj to search the test data value in
     * @return true if value has been found for this node; false otherwise
     */
    private boolean containsTestDataValue(IParameterInterfacePO paramObj) {
        List<IParamDescriptionPO> usedParameters = paramObj.getParameterList();
        IParameterInterfacePO refDataCube = paramObj.getReferencedDataCube();
        ITDManager testDataManager = paramObj.getDataManager();
        for (IDataSetPO dataSet : testDataManager.getDataSets()) {
            for (IParamDescriptionPO paramDesc : usedParameters) {
                int column = testDataManager.findColumnForParam(paramDesc
                        .getUniqueId());

                if (refDataCube != null) {
                    IParamDescriptionPO dataCubeParam = refDataCube
                            .getParameterForName(paramDesc.getName());
                    if (dataCubeParam != null) {
                        column = testDataManager
                                .findColumnForParam(
                                        dataCubeParam.getUniqueId());
                    }
                }

                if (column != -1 && column < dataSet.getColumnCount()) {
                    String testData = dataSet.getValueAt(column);
                    if (testData != null) {
                        ModelParamValueConverter converter =
                            new ModelParamValueConverter(
                                testData, paramObj, null);
                        String value = converter.getGuiString();
                        if (value != null && matchSearchString(value)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

}
