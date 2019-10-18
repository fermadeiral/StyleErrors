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
package org.eclipse.jubula.client.core.errorhandling;

import org.eclipse.jubula.tools.internal.exception.JBException;

/**
 * Noop implementation for presenting error messages to the user (so, 
 * basically, nothing gets shown).
 *
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public class NoopErrorMessagePresenter implements IErrorMessagePresenter {

    /**
     * {@inheritDoc}
     */
    public void showErrorMessage(JBException ex, 
            Object[] params, String[] details) {

        // No-op
    }

    /**
     * {@inheritDoc}
     */
    public void showErrorMessage(Integer messageID, Object[] params,
            String[] details) {

        // No-op
    }

}
