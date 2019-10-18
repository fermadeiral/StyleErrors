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
package org.eclipse.jubula.rc.common.driver;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * The <code>run()</code> method of this interface is executed in the Graphics
 * API specific event queue, see
 * {@link org.eclipse.jubula.rc.common.driver.IEventThreadQueuer}. Usually, this
 * interface is implemented by an anonymous inner class.
 * 
 * @author BREDEX GmbH
 * @created 05.04.2005
 * 
 * @param <V> the result type of method <tt>run</tt>
 */
public interface IRunnable<V> {
    /**
     * This method is run in the Graphics API specific event queue. Put all
     * operations on graphics components which are not thread-safe into this
     * method.
     * 
     * @throws StepExecutionException
     *             if an error occurs
     * @return Any object as a return value of the <code>run()</code>
     *         implementation
     */
    public V run() throws StepExecutionException;
}
