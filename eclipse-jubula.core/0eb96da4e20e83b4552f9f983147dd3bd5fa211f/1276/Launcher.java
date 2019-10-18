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
package org.eclipse.jubula.app.dbtool;

import org.eclipse.jubula.app.dbtool.core.DBToolClient;
import org.eclipse.jubula.client.cmd.AbstractCmdlineClient;
import org.eclipse.jubula.client.cmd.AbstractLauncher;

/**
 * @author BREDEX GmbH
 * @created Mar 11, 2009
 */
public final class Launcher extends AbstractLauncher {
    /**
     * {@inheritDoc}
     */
    protected AbstractCmdlineClient getAbstractCmdLineClient() {
        return DBToolClient.getInstance();
    }
}
