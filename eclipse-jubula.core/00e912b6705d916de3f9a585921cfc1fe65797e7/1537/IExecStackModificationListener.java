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
 * Interface for listening to modifications to the execution stack 
 * during test execution.
 * 
 * @author BREDEX GmbH
 * @created 20.04.2005
 *
 */
public interface IExecStackModificationListener {
    /**
     * signal for push-operation on stack
     * @param node node, which added to stack
     */
    void stackIncremented(INodePO node);
    
    /**
     * signal for pop-operation on stack
     */
    void stackDecremented();
    
    /**
     * signal for changing to next dataset inside of testcase
     */
    void nextDataSetIteration();
    
    /**
     * signal for an infinite loop
     * the top of the exec stack is the loop node
     */
    void infiniteLoop();
    
    /**
     * @param cap actual proceeded cap in testexecution
     */
    void nextCap(ICapPO cap);

    /**
     * 
     * @param cap The cap that is about to be retried.
     */
    void retryCap(ICapPO cap);
}
