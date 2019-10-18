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
 * Presents an error message to the user.
 *
 * @author BREDEX GmbH
 * @created Jun 4, 2010
 */
public interface IErrorMessagePresenter {

    /**
     * 
     * @param ex the actual JBException
     * @param params Parameter of the message text or null, if not needed.
     * @param details use null, or overwrite in MessageIDs hardcoded details.
     */
    public void showErrorMessage(JBException ex, 
            Object[] params, String[] details);

    /**
     * 
     * @param messageID the actual messageID
     * @param params Parameter of the message text or null, if not needed.
     * @param details use null, or overwrite in MessageIDs hardcoded details.
     */
    public void showErrorMessage(final Integer messageID,
            final Object[] params, final String[] details);

}
