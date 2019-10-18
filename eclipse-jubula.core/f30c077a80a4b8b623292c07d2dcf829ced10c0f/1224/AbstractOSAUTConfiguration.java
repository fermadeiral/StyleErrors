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
package org.eclipse.jubula.toolkit.base.config;

import org.apache.commons.lang.Validate;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

/**
 * @author BREDEX GmbH
 * @noextend This class is not intended to be extended by clients.
 */
public abstract class AbstractOSAUTConfiguration extends
        AbstractAUTConfiguration {
    /** the dir */
    @NonNull
    private String m_workingDir;

    /**
     * Constructor
     * 
     * @param name
     *            the name
     * @param autID
     *            the AUT ID
     * @param workingDir
     *            the working directory for the AUT process. If a relative path
     *            is given the base path is relative to the process working
     *            directory of the connected
     *            {@link org.eclipse.jubula.client.AUTAgent AUTAgent}
     */
    public AbstractOSAUTConfiguration(@Nullable String name,
            @NonNull String autID, @NonNull String workingDir) {
        super(name, autID);

        Validate.notEmpty(workingDir, "The working directory must not be empty"); //$NON-NLS-1$
        m_workingDir = workingDir;
    }

    /**
     * @return the workingDir
     */
    @NonNull
    public String getWorkingDir() {
        return m_workingDir;
    }
}