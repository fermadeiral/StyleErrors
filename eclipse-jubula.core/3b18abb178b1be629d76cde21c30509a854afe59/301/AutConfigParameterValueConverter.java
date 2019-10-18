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
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;


/**
 * Converts AUT Configurations to Strings and back again. The String 
 * representation of an AUT Configuration for the purposes of conversion is 
 * the database ID of the AUT Configuration object.
 *
 * @author BREDEX GmbH
 * @created Apr 28, 2009
 */
public class AutConfigParameterValueConverter extends
        AbstractParameterValueConverter {

    /**
     * {@inheritDoc}
     */
    public IAUTConfigPO convertToObject(String parameterValue)
        throws ParameterValueConversionException {

        Long id = ParameterValueConverterUtil.parseId(parameterValue);
        IProjectPO activeProject = GeneralStorage.getInstance().getProject();
        if (activeProject != null) {
            for (IAUTMainPO aut : activeProject.getAutMainList()) {
                for (IAUTConfigPO config : aut.getAutConfigSet()) {
                    if (id.equals(config.getId())) {
                        return config;
                    }
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
                parameterValue, IAUTConfigPO.class);
        return ParameterValueConverterUtil.getIdString(
                (IAUTConfigPO)parameterValue);
    }

}
