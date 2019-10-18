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
package org.eclipse.jubula.rc.common.util;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;


/**
 * Utility methods for creating, simulating, and managing key strokes.
 *
 * @author BREDEX GmbH
 * @created Sep 8, 2010
 */
public class KeyStrokeUtil {

    /**
     * Private constructor for utility class.
     */
    private KeyStrokeUtil() {
        // Nothing to initialize
    }
    
    /**
     * Converts the given string into the "modifiers" format defined in 
     * {@link javax.swing.KeyStroke#getKeyStroke(String)} and returns the 
     * result. The given string must consist solely of strings defined in the  
     * "modifiers" section of {@link CompSystemConstants} 
     * (ex. {@link CompSystemConstants#MODIFIER_CONTROL}), separated by 
     * {@link TestDataConstants#COMBI_VALUE_SEPARATOR}. If the given string 
     * does not fulfill this requirement, no guarantees can be made about this 
     * method's return value.
     * 
     * @param modifierSpec The string to convert.
     * @return the converted string.
     */
    public static String getModifierString(String modifierSpec) {
        String [] modArray = StringUtils.defaultString(modifierSpec).split(
                TestDataConstants.COMBI_VALUE_SEPARATOR);
        StringBuffer mod = new StringBuffer();
        for (int i = 0; i < modArray.length; i++) {
            String modElement = modArray[i];
            if (modElement.equals(ValueSets.Modifier.none.rcValue())) {
                modElement = StringUtils.EMPTY;
            } else if (modElement.equals(ValueSets.Modifier.cmd.rcValue())) {
                modElement = ValueSets.Modifier.meta.rcValue();
            } else if (modElement.equals(ValueSets.Modifier.mod.rcValue())) {
                modElement = 
                    AUTServer.getInstance().getRobot().getSystemModifierSpec();
            }
            mod.append(modElement);
            if (modElement.length() > 1) {
                mod.append(" "); //$NON-NLS-1$
            }
        }
        
        return mod.toString();
    }
    
}
