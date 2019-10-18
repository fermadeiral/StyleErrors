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

/**
 * Interface for classes capable of resolving conflicts between the database
 * version expected by Jubula and the actual version of the database to which 
 * the Client is trying to connect.
 *
 * @author BREDEX GmbH
 * @created May 18, 2010
 */
public interface IDatabaseVersionErrorHandler {

    /**
     * Attempts to resolve the database conflict.
     * 
     * @return <code>true</code> if the conflict has been resolved as a result
     *         of this method call. Otherwise, <code>false</code>.
     */
    public boolean handleDatabaseError();
    
    /**
     * @return the minimum database major version number this version error handler
     *         requires
     */
    public int getMinimumDatabaseMajorVersionNumber();
}
