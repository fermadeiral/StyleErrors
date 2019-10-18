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

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IDataSetPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITDManager;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.core.utils.FunctionArgumentSeparatorToken;
import org.eclipse.jubula.client.core.utils.FunctionToken;
import org.eclipse.jubula.client.core.utils.IParamValueToken;
import org.eclipse.jubula.client.core.utils.LiteralToken;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.SimpleStringConverter;
import org.eclipse.jubula.client.core.utils.SimpleValueToken;
import org.eclipse.jubula.tools.internal.exception.InvalidDataException;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;


/**
 * @author BREDEX GmbH
 * @created 21.12.2005
 */
public class TestDataBP {

    /** Function name from org.eclipse.jubula.core.functions plugin.xml */
    private static final String CTDS = "getCentralTestDataSetValue"; //$NON-NLS-1$
    
    /** Constructor */
    private TestDataBP() {
        // empty
    }

    /**
     * Checks if the given value is a value of the value set of the given CAP
     * of the given parameter description.
     * @param cap the cap
     * @param paramDesc The parameter description
     * @param paramValue the value to check
     * @return true if the value is in the value set, false otherwise
     */
    public static boolean isValueSetParam(ICapPO cap,
            IParamDescriptionPO paramDesc, String paramValue) {
        
        Action action = CapBP.getAction(cap);
        Param param = action.findParam(paramDesc.getUniqueId());
        return param.findValueSetElementByValue(paramValue) != null;
    }
    
    /**
     * Retrieves the Test Data for the given arguments.
     * 
     * @param paramNode The execution node for which the Test Data will be
     *                  retrieved.
     * @param testDataManager The data manager from which the Test Data will be
     *                        retrieved.
     * @param paramDesc The Parameter for which the Test Data will be retrieved.
     * @param dataSetNum The number (index) of the Data Set (within the given 
     *                   data manager) from which to retrieve the Test Data. 
     * @return the retrieved Test Data, or <code>null</code> if no such Test 
     *         Data exists.
     */
    public static String getTestData(IParamNodePO paramNode, 
            ITDManager testDataManager, IParamDescriptionPO paramDesc,
            int dataSetNum) {
        IParameterInterfacePO refDataCube = paramNode.getReferencedDataCube();
        int column = 
            testDataManager.findColumnForParam(
                    paramDesc.getUniqueId());
        
        if (refDataCube != null) {
            // if referencing a Data Cube, then the Parameter needs to be
            // referenced (indirectly) by name
            IParamDescriptionPO dataCubeParam = 
                refDataCube.getParameterForName(
                    paramDesc.getName());
            if (dataCubeParam != null) {
                column = testDataManager.findColumnForParam(
                        dataCubeParam.getUniqueId());
            }
        }
 
        IDataSetPO dataSet = testDataManager.getDataSet(dataSetNum);
        if (column != -1 && column < dataSet.getColumnCount()) {
            return dataSet.getValueAt(column);
        }
        
        return null;
    }

    /**
     * Checks whether a central test data set is referenced from a string (through ?getCentralTestData calls)
     * @param ctds the ctds name
     * @param value the parameter value
     * @return whether
     */
    public static boolean isCTDSReferenced(String ctds, String value) {
        for (String[] res : getAllCTDSReferences(value)) {
            if (StringUtils.equals(ctds, res[0])) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns all resolvable CTDS references
     * @param value the parameter value
     * @return the CTDS references: 4-element String arrays
     *      containing the 4 arguments of a ?getCTDSValue function.
     *      Only purely literal references are resolved, any other references are
     *      replaced by nulls.
     */
    public static List<String[]> getAllCTDSReferences(String value) {
        List<String[]> res = new ArrayList<>();
        ParamValueConverter conv = new SimpleStringConverter(
                value);
        addAllCTDSRefsNotGetCTDS(res,
                conv.getTokens().toArray(new IParamValueToken[0]));
        List<String[]> realRes = new ArrayList<>(res.size());
        Set<String> usedNames = TestDataCubeBP.getSetOfUsedNames(
            GeneralStorage.getInstance().getProject().getTestDataCubeCont());
        for (String[] arr : res) {
            if (usedNames.contains(arr[0])) {
                realRes.add(arr);
            }
        }
        return res;
    }

    /**
     * Searches through CTDS references within a list of tokens
     * @param res the resulting list of CTDS refs (4-element String arrays containing the arguments)
     * @param tokens the tokens
     */
    private static void addAllCTDSRefsNotGetCTDS(List<String[]> res,
            IParamValueToken[] tokens) {
        int ind = 0;
        while (ind < tokens.length) {
            IParamValueToken next = tokens[ind++];
            if (!(next instanceof FunctionToken)) {
                continue;
            }
            FunctionToken funct = (FunctionToken) next;
            if (!StringUtils.equals(funct.getFunctionName(), CTDS)) {
                continue;
            }
            addAllCTDSRefsGetCTDS(res, funct.getArguments());
        }
    }

    /**
     * Searches through CTDS references within argument tokens of a getCTDSValue function
     * @param res the resulting list of CTDS refs (4-element String arrays, containing the 4 arguments)
     * @param tokens the tokens
     */
    private static void addAllCTDSRefsGetCTDS(List<String[]> res,
            IParamValueToken[] tokens) {
        String[] args = new String[4];
        int parInd = 0;
        int ind = 0;
        StringBuilder curr = new StringBuilder();
        while (ind < tokens.length && parInd < 4) {
            if (tokens[ind] instanceof FunctionArgumentSeparatorToken) {
                if (curr == null) {
                    // end of non-literal parameter
                    args[parInd] = null;
                } else {
                    args[parInd] = curr.toString();
                }
                parInd++;
                curr = new StringBuilder();
                ind++;
                continue;
            }
            if (tokens[ind] instanceof FunctionToken) {
                FunctionToken next = (FunctionToken) tokens[ind++];
                curr = null; // no longer a literal parameter
                if (StringUtils.equals(next.getFunctionName(), CTDS)) {
                    addAllCTDSRefsGetCTDS(res, next.getArguments());
                } else {
                    addAllCTDSRefsNotGetCTDS(res, next.getArguments());
                }
                ind++;
                continue;
            }
            if (curr != null && (tokens[ind] instanceof SimpleValueToken
                    || tokens[ind] instanceof LiteralToken)) {
                // extending a literal parameter
                try {
                    curr.append(tokens[ind].getExecutionString(null));
                } catch (InvalidDataException e) {
                    // unlikely
                    curr = null;
                }
            } else {
                // end of literal parameter
                curr = null;
            }
            ind++;
        }
        if (parInd != 3 || args[0] == null) {
            // incorrect number of parameters or nonliteral CTDS name
            return;
        }
        if (curr != null) {
            args[3] = curr.toString();
        }
        res.add(args);
    }

}
