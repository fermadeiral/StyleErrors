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
package org.eclipse.jubula.client.ui.handlers.project;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.persistence.Persistor;
import org.eclipse.jubula.client.ui.constants.CommandIDs;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.utils.CommandHelper;

/**
 * @author BREDEX GmbH
 * @created Mar 31, 2011
 */
public abstract class AbstractProjectHandler extends AbstractHandler {
    /**
     * {@inheritDoc}
     */
    public final Object execute(ExecutionEvent event) 
        throws ExecutionException {
        if (Persistor.instance() == null) {
            Object result = CommandHelper
                    .executeCommand(CommandIDs.SELECT_DATABASE_COMMAND_ID);
            if (Status.OK_STATUS.equals(result)) {
                return super.execute(event);    
            }
        } else {
            return super.execute(event);
        }

        return null;
    }
}
