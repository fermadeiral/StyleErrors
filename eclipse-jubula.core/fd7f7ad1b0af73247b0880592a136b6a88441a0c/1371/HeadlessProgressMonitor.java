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
package org.eclipse.jubula.client.cmd.progess;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;

/**
 * @author BREDEX GmbH
 * @created Apr 1, 2011
 */
public class HeadlessProgressMonitor extends NullProgressMonitor {
    /**
     * {@inheritDoc}
     */
    public void beginTask(String name, int totalWork) {
        AbstractCmdlineClient.printConsoleLn(name, true);
        super.beginTask(name, totalWork);
    }

    /**
     * {@inheritDoc}
     */
    public void setTaskName(String name) {
        AbstractCmdlineClient.printConsoleLn(name, true);
        super.setTaskName(name);
    }

    /**
     * {@inheritDoc}
     */
    public void subTask(String name) {
        AbstractCmdlineClient.printConsoleLn(name, true);
        super.subTask(name);
    }
}
