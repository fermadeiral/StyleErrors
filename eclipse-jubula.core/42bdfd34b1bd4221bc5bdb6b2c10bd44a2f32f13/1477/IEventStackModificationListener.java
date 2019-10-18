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
package org.eclipse.jubula.client.core.model;

/**
 * Interface for listening to modifications to the event stack 
 * during test execution.
 *
 * @author BREDEX GmbH
 * @created Aug 6, 2008
 */
public interface IEventStackModificationListener {
    
    /**
     * signal for push-operation on stack
     */
    void eventStackIncremented();
    
    /**
     * signal for pop-operation on stack
     */
    void eventStackDecremented();
    
}
