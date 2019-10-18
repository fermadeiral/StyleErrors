/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.api.converter.utils;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.ui.rcp.views.dataset.AbstractDataSetPage;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;

/**
 * @created 05.11.2014
 */
public class ParamUtils {
    
    /** Pattern for detecting parameters like ={PARAM} */
    private static Pattern simpleParameter = Pattern.compile("^=\\{?([a-zA-Z0-9_]+)\\}?"); //$NON-NLS-1$
    
    /** Pattern for detecting parameters like C:/Users/={USER}/workspace */
    private static Pattern oneParameter = Pattern.compile(
            "^(.*)=\\{?([a-zA-Z0-9_]+)\\}?(.*)"); //$NON-NLS-1$

    /** Pattern for detecting multiple variables like /dir/$USER/workspace */
    private static Pattern variable = Pattern.compile("(.*)\\$\\{?([a-zA-Z0-9_]+)\\}?(.*)"); //$NON-NLS-1$
    
    /** Pattern for detecting functions like ?add(1,2) */
    private static Pattern function = Pattern.compile(".*\\?[a-zA-Z_]+\\(.*?"); //$NON-NLS-1$
    
    /** Pattern for detecting bulk masked characters by using single quotes e.g. 'string2mask' */
    private static Pattern bulkMask = Pattern.compile("^(.*?)'(.*?)'(.*?)"); //$NON-NLS-1$
    
    /**
     * private constructor
     */
    private ParamUtils() {
        // private
    }

    /**
     * Returns a parameter value for a node
     * @param node the node
     * @param param the parameter
     * @param row the row
     * @return the value
     */
    public static String getValueForParam(IParameterInterfacePO node,
            IParamDescriptionPO param, int row) {
        String paramType = param.getType();
        String value = AbstractDataSetPage.getGuiStringForParamValue(
                node, param, row);
        //CHECKSTYLE:OFF
        if (value == null) {
            value = "null // TODO: <code>null</code> found as test data - check and fix in ITE"; //$NON-NLS-1$
        } else {
            value = executeEscapes(value);
            if (function.matcher(value).matches()) {
                return "null // TODO: Function usage - call a corresponding method instead of this ITE function: \"" //$NON-NLS-1$
                        + value + "\" "; //$NON-NLS-1$
            } else if (simpleParameter.matcher(value).matches()) {
                value = value.replaceAll(simpleParameter.pattern(), "$1"); //$NON-NLS-1$
            } else if (variable.matcher(value).matches()
                    || oneParameter.matcher(value).matches()) {
                while (variable.matcher(value).matches()) {
                    value = value.replaceAll(variable.pattern(),
                            "$1\" + VariableStore.getInstance().getValue(\"$2\") + \"$3"); //$NON-NLS-1$
                }
                while (oneParameter.matcher(value).matches()) {
                    value = value.replaceAll(oneParameter.pattern(),
                            "$1\" + $2 + \"$3"); //$NON-NLS-1$
                }
                value = StringConstants.QUOTE + value + StringConstants.QUOTE;
            } else if (paramType.equals(TestDataConstants.STR)
                    || paramType.equals(TestDataConstants.VARIABLE)) {
                value = StringConstants.QUOTE + value + StringConstants.QUOTE;
            } else if (StringUtils.isEmpty(value)) {
                value = "null // TODO: no test data found - check and fix in ITE"; //$NON-NLS-1$
            }
        }
        return value;
        //CHECKSTYLE:ON
    }

    /**
     * escapes characters in a string
     * @param value the string
     * @return the adjusted string
     */
    private static String executeEscapes(String value) {
        String adjustedValue = value;
        adjustedValue = adjustedValue.replaceAll(bulkMask.pattern(), "$1$2$3"); //$NON-NLS-1$
        adjustedValue = adjustedValue.replace(StringConstants.BACKSLASH,
                StringConstants.BACKSLASH + StringConstants.BACKSLASH);
        adjustedValue = adjustedValue.replace(StringConstants.QUOTE, "\\\""); //$NON-NLS-1$
        adjustedValue = adjustedValue.replace(StringConstants.APOSTROPHE, "\\'"); //$NON-NLS-1$
        return adjustedValue;
    }
    
    
}
