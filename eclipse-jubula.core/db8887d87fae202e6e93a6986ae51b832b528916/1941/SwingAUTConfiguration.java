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
package org.eclipse.jubula.toolkit.swing.config;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.jubula.toolkit.base.config.AbstractOSProcessAUTConfiguration;
import org.eclipse.jubula.tools.internal.constants.CommandConstants;
import org.eclipse.jubula.tools.internal.constants.ToolkitConstants;

/** 
 *  @author BREDEX GmbH 
 *  @noextend This class is not intended to be extended by clients.
 */
public class SwingAUTConfiguration extends AbstractOSProcessAUTConfiguration {
    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param command
     *            the command
     * @param workingDir
     *            the working directory for the AUT process. If a relative path
     *            is given the base path is relative to the process working
     *            directory of the connected
     *            {@link org.eclipse.jubula.client.AUTAgent AUTAgent}
     * @param args
     *            the arguments
     * @since 4.0
     */
    public SwingAUTConfiguration(
            @Nullable String name, 
            @NonNull String autID,
            @NonNull String command, 
            @NonNull String workingDir,
            @Nullable String[] args) {
        super(name, autID, command, workingDir, args);
        
        // Toolkit specific information
        add(ToolkitConstants.ATTR_TOOLKITID, CommandConstants.SWING_TOOLKIT);
    }
}