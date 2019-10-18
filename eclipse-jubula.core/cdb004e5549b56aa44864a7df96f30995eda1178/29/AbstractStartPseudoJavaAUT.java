/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.autagent.common.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author BREDEX GmbH
 */
public abstract class AbstractStartPseudoJavaAUT 
    extends AbstractStartToolkitAut {
    /** the logger */
    private static final Logger LOG = LoggerFactory
            .getLogger(AbstractStartPseudoJavaAUT.class);
    
    /** {@inheritDoc} */
    protected String createBaseCmd(Map<String, String> parameters) 
        throws IOException {
        String jre = System.getProperty("java.home") + FILE_SEPARATOR//$NON-NLS-1$ 
                + "bin" + FILE_SEPARATOR + "java"; //$NON-NLS-1$ //$NON-NLS-2$
        
        if (EnvironmentUtils.isWindowsOS()) {
            jre += ".exe"; //$NON-NLS-1$
        }
        
        if (jre != null && jre.length() > 0) {
            File exe = new File(jre);
            if (exe.isFile() && exe.exists()) {
                return exe.getCanonicalPath();
            }
        }
        
        String errorMsg = jre + " does not point to a valid executable."; //$NON-NLS-1$
        LOG.error(errorMsg);
        throw new FileNotFoundException(errorMsg);
    }
}
