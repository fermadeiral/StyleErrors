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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;

/**
 * 
 * Default implementation of {@link IEventThreadQueuer}. Has no association 
 * with any graphics toolkit. Executes operations on either the current thread 
 * (sync) or a newly created thread (async).
 *  
 */
public class DefaultEventThreadQueuer implements IEventThreadQueuer {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            DefaultEventThreadQueuer.class);
    
    /** {@inheritDoc} */
    public <V> V invokeAndWait(String name, IRunnable<V> runnable)
        throws StepExecutionException {

        return runnable.run();
    }

    /** {@inheritDoc} */
    public void invokeLater(String name, Runnable runnable)
        throws StepExecutionException {

        new Thread(runnable, name).start();
    }

    /** {@inheritDoc} */
    public <V> V invokeAndWait(String name, Callable<V> callable, long timeout)
            throws StepExecutionException, TimeoutException {
        FutureTask<V> task = new FutureTask<V>(callable);
        new Thread(task, name).start();
        try {
            return task.get(timeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException ie) {
            // this (the waiting) thread was interrupted -> error
            log.error(ie);
            throw new StepExecutionException(ie);
        } catch (ExecutionException ee) {
            // the run() method from IRunnable has thrown an exception
            // -> log on info
            // -> throw a StepExecutionException
            Throwable thrown = ee.getCause();
            if (thrown instanceof StepExecutionException) {
                if (log.isInfoEnabled()) {
                    log.info(ee);
                }
                throw (StepExecutionException) thrown;
            }

            // any other (unchecked) Exception from IRunnable.run()
            log.error("exception thrown by '" + name //$NON-NLS-1$
                    + "':", thrown); //$NON-NLS-1$
            throw new StepExecutionException(thrown);
        }
    }
}