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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.EventListener;

import org.eclipse.jubula.client.core.model.TestResultNode;


/**
 * @author BREDEX GmbH
 * @created 19.10.2004
 */
public interface ITestResultEventListener extends EventListener {
    /**
     * This method is called when the state of testExecution changes.
     * @param data The changed TestResultNode
     */
    public void testResultChanged(TestResultNode data);

    /**
     * @param parent
     *      TestResultNode
     * @param pos
     *      int
     * @param newNode
     *      TestResultNode
     */
    public void testResultNodeUpdated(TestResultNode parent, 
            int pos, TestResultNode newNode);
    
}
