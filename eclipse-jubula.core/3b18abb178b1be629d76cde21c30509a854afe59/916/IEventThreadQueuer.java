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

import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * Executes an {@link org.eclipse.jubula.rc.common.driver.IRunnable} in the
 * Graphics API specific event queue. All implementation classes which
 * access AWT/Swing components require this mechanism, as the
 * AWT/Swing components are not thread-safe. The programming model in an
 * implementation class is as follows:
 * 
 * <pre>
 * IRobotFactory factory = new RobotFactoryConfig().getRobotFactory();
 * IEventThreadQueuer queuer = factory.getEventThreadQueuer();
 * queuer.invokeAndWait(threadName, new IRunnable() {
 *     public Object run() {
 *         //...
 *         return result;
 *     }
 * });
 * </pre>
 *
 * @author BREDEX GmbH
 * @created 05.04.2005
 * 
 */
public interface IEventThreadQueuer {
    /**
     * Invokes the <code>runnable</code> in the Graphics API specific event
     * queue and blocks until termination of <code>runnable</code>.
     * 
     * @param name
     *            The name of this invocation.
     * @param runnable
     *            The runnable.
     * @param <V> the result type of method <tt>invokeAndWait</tt>
     * @return The result returned by the runnable, maybe <code>null</code>.
     * @throws StepExecutionException
     *             If the invocation fails or if the runnable throws a
     *             <code>StepExecutionException</code>.
     */
    public <V> V invokeAndWait(String name, IRunnable<V> runnable)
        throws StepExecutionException;
    
    /**
     * Invokes the <code>callable</code> in the toolkit event queue and blocks
     * until termination of <code>callable</code> or when the timeout is
     * reached.
     * 
     * @param name The name of this invocation
     * @param callable the callable
     * @param timeout the time out in ms
     * @param <V> the result type of method <tt>invokeAndWait</tt>
     * @return the result of the execution, might be null
     * @throws StepExecutionException
     * @throws TimeoutException
     */
    public <V> V invokeAndWait(String name, Callable<V> callable, long timeout)
            throws StepExecutionException, TimeoutException;
    
    
    /**
     * Invokes the <code>runnable</code> in the Graphics API specific event
     * queue asynchronous. The void return type is due to the asynchronous
     * execution. This should prevent any confusion due to concurrency.
     * 
     * @param name
     *            The name of this invocation.
     * @param runnable
     *            The runnable.
     * @throws StepExecutionException
     *             If the invocation fails or if the runnable throws a
     *             <code>StepExecutionException</code>.
     */
    public void invokeLater(String name, Runnable runnable)
        throws StepExecutionException;
}
