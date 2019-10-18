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
import org.eclipse.jubula.client.core.businessprocess.db.TestJobBP;
import org.eclipse.jubula.client.core.model.ITestJobPO;


/**
 * Converts Test Jobs to Strings and back again. The String 
 * representation of a Test Job for the purposes of conversion is 
 * the database ID of the Test Job object.
 *
 * @author BREDEX GmbH
 * @created Mar 17, 2010
 */
public class TestJobParameterValueConverter extends
        AbstractParameterValueConverter {

    /**
     * 
     * {@inheritDoc}
     */
    public ITestJobPO convertToObject(String parameterValue)
        throws ParameterValueConversionException {

        Long id = ParameterValueConverterUtil.parseId(parameterValue);
        for (ITestJobPO testJob : TestJobBP.getListOfTestJobs()) {
            if (id.equals(testJob.getId())) {
                return testJob;
            }
        }

        return null;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String convertToString(Object parameterValue)
        throws ParameterValueConversionException {

        ParameterValueConverterUtil.checkType(
                parameterValue, ITestJobPO.class);
        return ParameterValueConverterUtil.getIdString(
                (ITestJobPO)parameterValue);
    }

}
