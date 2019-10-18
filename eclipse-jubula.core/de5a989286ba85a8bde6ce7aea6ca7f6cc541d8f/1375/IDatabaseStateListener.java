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
package org.eclipse.jubula.client.core.utils;

/**
 * Listener interface to show a ProgressMonitor for a long running operation.
 * 
 * @author BREDEX GmbH
 * @created 25.08.2005
 */
public interface IDatabaseStateListener {

    /**
     * Updates the object, which listens to this listener.
     * 
     * @param e
     *            The ProgressEvent.
     */
    public void reactOnDatabaseEvent(DatabaseStateEvent e);
}