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
package org.eclipse.jubula.client.core.utils;

import java.io.File;

/**
 * @author BREDEX GmbH
 * @created Mai 15, 2014
 */
public class FileUtils {
    
    /** private constructor */
    private FileUtils() {
        // utility class
    }
    
    /** checks if the given URI is relative and resolve it to the absolute against the base URL
     * @param basePath the base directory
     * @param path a text path given by user
     * @return absolute against the base URL path or EXIT_INVALID_ARG_VALUE ("-2") if invalid URL was given
     */
    public static String resolveAgainstBasePath(
            String path, String basePath) {
        if (path == null || basePath == null) {
            return null;
        }
        File baseDir = new File(basePath);
        File fpath = new File(path);
        if (!fpath.isAbsolute()) {
            fpath = new File(baseDir, path);
        }
        return fpath.toString();
    }
}
