/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.rc.commands;

import org.eclipse.jubula.tools.internal.exception.JBException;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * Interface for commands to execute after the execution of the dependent
 * CAP. The implementing class of this interface must be inscribed 
 * with the full qualified name in the componentConfiguration.xml 
 * in the "postExecutionCommand"-tag in the dependent Action.
 * 
 * @author BREDEX GmbH
 * @created 24.07.2006
 */
public interface IPostExecutionCommand {
    /**
     * Implementation of this IPostExecutionCommand
     * @throws JBException in case of error while execution
     * @return a TestErrorEvent representing an error that occurred during  
     *         execution, or <code>null</code> if no such error occurs. 
     */
    public TestErrorEvent execute() throws JBException;
}
