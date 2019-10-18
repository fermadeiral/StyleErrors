/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers.project;

import org.eclipse.jubula.client.ui.handlers.project.AbstractSelectDatabaseHandler;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.utils.Utils;

/**
 *
 * @author BREDEX GmbH
 * @created Oct 05, 2011
 */
public class RcpSelectDatabaseHandler extends AbstractSelectDatabaseHandler {

    @Override
    protected void clearClient() {
        Utils.clearClient(true);
    }

    @Override
    protected void writeLineToConsole(String line) {
        Plugin.getDefault().writeLineToConsole(line, true);
    }

}