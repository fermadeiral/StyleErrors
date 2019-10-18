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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestDataCategoryPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.persistence.TestDataCubePM;


/**
 * Business logic operations for Test Data Cubes.
 *
 * @author BREDEX GmbH
 * @created Jul 12, 2010
 */
public class TestDataCubeBP {

    /**
     * Private constructor to prevent instantiation.
     */
    private TestDataCubeBP() {
        // Nothing to initialize
    }
    
    /**
     * 
     * @param cubeName The name of the Test Data Cube to find. 
     *                 Must not be <code>null</code>
     * @param containingProject The Project in which to search for the 
     *                          Test Data Cube.
     * @return the Test Data Cube with the given name within the given project,
     *         or <code>null</code> if no such Data Cube could be found.
     */
    public static IParameterInterfacePO getTestDataCubeByName(String cubeName, 
            IProjectPO containingProject) {
        Validate.notNull(cubeName);
        Validate.notNull(containingProject);
        
        for (IParameterInterfacePO testDataCube 
                : getAllTestDataCubesFor(containingProject)) {
            
            if (cubeName.equals(testDataCube.getName())) {
                return testDataCube;
            }
        }
        
        return null;
    }

    /**
     * Convenience method to get all Central Test Data for a Project, regardless
     * of how the Central Test Data is nested in categories. 
     * 
     * @param project The project containing the Central Test Data.
     * @return all Central Test Data instances contained in the given Project.
     */
    public static ITestDataCubePO[] getAllTestDataCubesFor(IProjectPO project) {
        if (project == null) {
            return new ITestDataCubePO[0];
        }
        
        Set<ITestDataCubePO> dataCollection = new HashSet<ITestDataCubePO>();
        getAllTestDataCubesFor(project.getTestDataCubeCont(), dataCollection);
        
        return dataCollection.toArray(
                new ITestDataCubePO[dataCollection.size()]);
    }

    /**
     * 
     * @param category The category containing the Central Test Data.
     * @param dataCollection The collection to fill.
     */
    private static void getAllTestDataCubesFor(
            ITestDataCategoryPO category, Set<ITestDataCubePO> dataCollection) {

        if (category != null) {
            for (ITestDataCubePO testData : category.getTestDataChildren()) {
                dataCollection.add(testData);
            }
            for (ITestDataCategoryPO subCategory 
                    : category.getCategoryChildren()) {
                getAllTestDataCubesFor(subCategory, dataCollection);
            }
        }
    }

    /**
     * @param pio
     *            the param interface object to check for inner project reusage
     * @return true if the cube is reused
     */
    public static boolean isCubeReused(IParameterInterfacePO pio) {
        GeneralStorage gs = GeneralStorage.getInstance();
        return TestDataCubePM.computeReuser(
                pio, gs.getMasterSession()).size() > 0;
    }

    /**
     * @param pio
     *            the param interface object to check for inner project reusage
     * @return true if the cube is reused
     */
    public static List<IParamNodePO> getReuser(IParameterInterfacePO pio) {
        GeneralStorage gs = GeneralStorage.getInstance();
        IProjectPO proj = gs.getProject();
        return TestDataCubePM
                .computeParamNodeReuser(
                        pio, gs.getMasterSession(), proj);
    }

    /**
     * @param po a Test Data Category
     * @return the used CTDS names
     */
    public static Set<String> getSetOfUsedNames(ITestDataCategoryPO po) {
        Set<String> usedNames = new HashSet<String>();

        getSetOfUsedNames(po, usedNames);
        
        return usedNames;
    }

    /**
     * Recursively adds the names of all contained Central Test Data instances
     * to the provided collection.
     * 
     * @param category The category containing the Central Test Data.
     * @param usedNames The collection to modify.
     */
    private static void getSetOfUsedNames(
            ITestDataCategoryPO category, Set<String> usedNames) {
        
        for (ITestDataCategoryPO subCategory : category.getCategoryChildren()) {
            getSetOfUsedNames(subCategory, usedNames);
        }
        for (ITestDataCubePO cube : category.getTestDataChildren()) {
            usedNames.add(cube.getName());
        }
    }
}
