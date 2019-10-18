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
package org.eclipse.jubula.autagent.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;


/**
 * @author BREDEX GmbH
 * @created Jul 6, 2007
 * 
 */
public class StartSwtAutServerCommand extends StartSwingAutServerCommand {
    /** 
     * the separator to use when generating connection properties to add to 
     * the system environment variables 
     */
    public static final String ENV_VALUE_SEP = IStartAut.PROPERTY_DELIMITER;
    
    /**
     * {@inheritDoc}
     */
    protected String getServerClassName() {
        return CommandConstants.AUT_SWT_SERVER;
    }

    /**
     * {@inheritDoc}
     */
    protected String[] createEnvArray(Map<String, String> parameters, 
        boolean isAgentSet) {
        String [] envArray = super.createEnvArray(parameters, isAgentSet);
        if (envArray == null) {
            envArray = EnvironmentUtils.propToStrArray(
                    EnvironmentUtils.getProcessEnvironment(), 
                    IStartAut.PROPERTY_DELIMITER);
        }
        Vector<String> envList = new Vector<String>(Arrays.asList(envArray));
        envList.addAll(getConnectionProperties(parameters, ENV_VALUE_SEP));
        envArray = envList.toArray(new String [envList.size()]);
        return envArray;
    }

    /**
     * 
     * @param parameters The AUT Configuration parameters.
     * @param valueSeparator The string to use to separate property names from
     *                       property values.
     * @return the list of properties.
     */
    private List<String> getConnectionProperties(Map parameters,
        String valueSeparator) {

        List<String> props = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        sb = new StringBuffer();
        sb.append(AutConfigConstants.KEYBOARD_LAYOUT).append(valueSeparator)
            .append((String) parameters.get(
                AutConfigConstants.KEYBOARD_LAYOUT));
        props.add(sb.toString());

        return props;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public String getRcBundleId() {
        return CommandConstants.RC_SWT_BUNDLE_ID;
    }

}
