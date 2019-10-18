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

import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.utils.StringParsing;


/**
 * @author BREDEX GmbH
 * @created 02.04.2007
 */
public abstract class MenuUtilBase {
    
    /**
     * protected utility contructor
     */
    protected MenuUtilBase() {
        // OK
    }
    

    /**
     * Splits a path into its components. The separator is '/'.
     * 
     * @param path the path
     * @return the splitted path
     */
    public static String[] splitPath(String path) {
        return StringParsing.splitToArray(path, 
                TestDataConstants.PATH_CHAR_DEFAULT, 
                TestDataConstants.ESCAPE_CHAR_DEFAULT);
    }
    
    /**
     * Splits a path into integers
     * 
     * @param path the path
     * @return an array of int values
     */
    public static int[] splitIndexPath(String path) {
        String[] textPath = splitPath(path);
        final int textPathLength = textPath.length;
        int[] result = new int[textPathLength];
        for (int i = 0; i < textPathLength; ++i) {
            result[i] = IndexConverter.intValue(textPath[i]);
        }
        return IndexConverter.toImplementationIndices(result);
    }
    
    
}
