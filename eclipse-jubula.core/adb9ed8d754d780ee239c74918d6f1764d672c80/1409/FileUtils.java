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
     * @return the path if is absolute otherwise a resolved absolute path based on the basePath.
     * Or null if it is not resolvable
     */
    public static String resolveAgainstBasePath(String path, String basePath) {
        if (path == null && basePath == null) {
            return null;
        }
        File baseDir = basePath != null ? new File(basePath) : null;
        File fpath = path != null ? new File(path) : null;
        if (baseDir != null && fpath != null && !fpath.isAbsolute()) {
            fpath = new File(baseDir, path);
        }
        return fpath != null ? fpath.toString() : null;
    }
}
