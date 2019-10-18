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
package org.eclipse.jubula.client.core.persistence;

import org.eclipse.jubula.client.core.i18n.Messages;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;

/**
 * An Exception thrown when an operation was successfully executed, but refreshing
 *    the master session failed
 * @author BREDEX GmbH
 *
 */
public class PMRefreshFailedException extends PMException {
    /**
     * Constructor
     * @param e the cause exception
     */
    public PMRefreshFailedException(Exception e) {
        super(Messages.RefreshFailed, e, MessageIDs.E_REFRESH_MS_FAILED);
    }
}
