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
package org.eclipse.jubula.rc.common.listener;

/**
 * Encapsulates the ability to add an event listener to an AUT Server.
 *
 * @author BREDEX GmbH
 * @created Jun 10, 2009
 */
public interface IAutListenerAppender {

    /**
     * Adds an event listener to an AUT Server. The added listener is 
     * responsible for deregistering itself when necessary.
     */
    public void addAutListener();
    
}
