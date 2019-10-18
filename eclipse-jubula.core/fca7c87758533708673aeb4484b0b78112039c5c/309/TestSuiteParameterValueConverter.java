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
package org.eclipse.jubula.client.ui.rcp.command.parameters;

import org.eclipse.core.commands.AbstractParameterValueConverter;
import org.eclipse.core.commands.ParameterValueConversionException;
import org.eclipse.jubula.client.core.businessprocess.db.TestSuiteBP;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.model.ITestSuitePO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;


/**
 * Converts Test Suites to Strings and back again. The String 
 * representation of a Test Suite for the purposes of conversion is 
 * the database ID of the Test Suite object.
 *
 * @author BREDEX GmbH
 * @created Feb 2, 2010
 */
public class TestSuiteParameterValueConverter extends
        AbstractParameterValueConverter {

    /**
     * {@inheritDoc}
     */
    public ITestSuitePO convertToObject(String parameterValue)
        throws ParameterValueConversionException {
        
        Long id = ParameterValueConverterUtil.parseId(parameterValue);
        IProjectPO activeProject = GeneralStorage.getInstance().getProject();
        if (activeProject != null) {
            for (ITestSuitePO testSuite 
                    : TestSuiteBP.getListOfTestSuites(activeProject)) {
                if (id.equals(testSuite.getId())) {
                    return testSuite;
                }
            }
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    public String convertToString(Object parameterValue)
        throws ParameterValueConversionException {
        
        ParameterValueConverterUtil.checkType(
                parameterValue, ITestSuitePO.class);
        return ParameterValueConverterUtil.getIdString(
                (ITestSuitePO)parameterValue);
    }

}
