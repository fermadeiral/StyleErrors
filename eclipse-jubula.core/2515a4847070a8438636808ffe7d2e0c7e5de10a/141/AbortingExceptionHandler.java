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
package org.eclipse.jubula.communication.internal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An exception handler which always returns false.
 *
 * @author BREDEX GmbH
 * @created 21.09.2004
 */
public class AbortingExceptionHandler implements IExceptionHandler {
    /** the logger */
    private static Logger log = LoggerFactory
            .getLogger(AbortingExceptionHandler.class);

    /** {@inheritDoc} */
    public boolean handle(Throwable t) {
        log.info("aborting: ", t); //$NON-NLS-1$
        return false;
    }
}
