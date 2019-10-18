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
package org.eclipse.jubula.rc.swing.driver;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RunnableWrapper;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * This class executes an <code>IRunnable</code> instance in the AWT event
 * thread.
 * 
 * @author BREDEX GmbH
 * @created 05.04.2005
 */
public class EventThreadQueuerAwtImpl implements IEventThreadQueuer {
    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(EventThreadQueuerAwtImpl.class);

    /** {@inheritDoc} */
    public <V> V invokeAndWait(String name, IRunnable<V> runnable)
        throws StepExecutionException {

        Validate.notNull(runnable, "runnable must not be null"); //$NON-NLS-1$
        
        RunnableWrapper<V> wrapper = new RunnableWrapper<V>(name, runnable);
        try {
            if (SwingUtilities.isEventDispatchThread()) {
                wrapper.run();
            } else {
                SwingUtilities.invokeAndWait(wrapper);
            }
            
            StepExecutionException exception = wrapper.getException();
            if (exception != null) {
                throw new InvocationTargetException(exception);
            }
        } catch (InterruptedException ie) {
            // this (the waiting) thread was interrupted -> error
            log.error(ie);
            throw new StepExecutionException(ie);
        } catch (InvocationTargetException ite) {
            // the run() method from IRunnable has thrown an exception
            // -> log on info
            // -> throw a StepExecutionException
            Throwable thrown = ite.getTargetException();
            if (thrown instanceof StepExecutionException) {
                if (log.isInfoEnabled()) {
                    log.info(ite);
                }
                throw (StepExecutionException)thrown;
            } 
            
            // any other (unchecked) Exception from IRunnable.run()
            log.error("exception thrown by '" + wrapper.getName() //$NON-NLS-1$
                + "':", thrown); //$NON-NLS-1$
            throw new StepExecutionException(thrown);
        }
        
        return wrapper.getResult();
    }
    
    /** {@inheritDoc} */
    public void invokeLater(String name, Runnable runnable) 
        throws StepExecutionException {
 
        Validate.notNull(runnable, "runnable must not be null"); //$NON-NLS-1$
        SwingUtilities.invokeLater(runnable);
    }

    /** {@inheritDoc} */
    public <V> V invokeAndWait(String name, Callable<V> callable, long timeout)
            throws StepExecutionException, TimeoutException {
        if (SwingUtilities.isEventDispatchThread()) {
            throw new IllegalStateException("Called from AWT-Thread: " //$NON-NLS-1$
                    + Thread.currentThread().getName());
        }
        try {
            FutureTask<V> task = new FutureTask<V>(callable);
            SwingUtilities.invokeLater(task);
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