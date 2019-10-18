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
package org.eclipse.jubula.rc.javafx.driver;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javafx.application.Platform;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;

/**
 * Executes the given Callable on the JavaFX-Thread and waits for the
 * termination
 *
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public class EventThreadQueuerJavaFXImpl implements IEventThreadQueuer {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
            EventThreadQueuerJavaFXImpl.class);

    /**
     * Executes the given Callable on the JavaFX-Thread and waits for the
     * termination
     *
     * @param name
     *            a name to identifier which Callable is being executed
     * @param <V>
     *            return value type
     * @param call
     *            the Callable
     * @return
     * @return the return value of the given Callable
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public static <V> V invokeAndWait(String name, Callable<V> call) {

        if (Platform.isFxApplicationThread()) {
            try {
                return call.call();
            } catch (Exception e) {
                // the run() method from IRunnable has thrown an exception
                // -> log on info
                // -> throw a StepExecutionException
                Throwable thrown = e.getCause();
                if (thrown == null) {
                    thrown = e;
                }
                if (thrown instanceof StepExecutionException) {
                    if (log.isInfoEnabled()) {
                        log.info(e);
                    }
                    throw (StepExecutionException) thrown;
                }

                // any other (unchecked) Exception from IRunnable.run()
                log.error("exception thrown by '" + name //$NON-NLS-1$
                        + "':", thrown); //$NON-NLS-1$
                throw new StepExecutionException(thrown);
            }
        }
        try {
            FutureTask<V> task = new FutureTask<>(call);
            Platform.runLater(task);
            return task.get();

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

    /**
     * Posts an empty {@link Callable} to the FX Thread and waits for the
     * posted event to be processed.
     */
    public static void waitForIdle() {
        invokeAndWait("waitForIdle", new Callable<Void>() { //$NON-NLS-1$
            @Override
            public Void call() throws Exception {
                return null;
            }
        });
    }

    /**
     * 
     * @throws IllegalStateException if the current thread is not the FX Thread.
     */
    public static void checkEventThread() throws IllegalStateException {
        if (!Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Not on FX application thread; currentThread = " //$NON-NLS-1$
                    + Thread.currentThread().getName());
        }
    }

    /**
     * 
     * @throws IllegalStateException if the current thread is the FX Thread.
     */
    public static void checkNotEventThread() throws IllegalStateException {
        if (Platform.isFxApplicationThread()) {
            throw new IllegalStateException("On FX application thread, although this is not allowed."); //$NON-NLS-1$
        }
    }
    
    @Override
    public <V> V invokeAndWait(String name, final IRunnable<V> runnable) {

        return invokeAndWait(name, new Callable<V>() {

            @Override
            public V call() throws Exception {
                return runnable.run();
            }
        });
    }

    @Override
    public void invokeLater(String name, Runnable runnable) {

        Validate.notNull(runnable, "runnable must not be null"); //$NON-NLS-1$
        Platform.runLater(runnable);
    }

    @Override
    public <V> V invokeAndWait(String name, Callable<V> call, long timeout)
            throws StepExecutionException, TimeoutException {
        if (Platform.isFxApplicationThread()) {
            throw new IllegalStateException("Called from FX-Thread: " //$NON-NLS-1$
                    + Thread.currentThread().getName());
        }
        try {
            FutureTask<V> task = new FutureTask<>(call);
            Platform.runLater(task);
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
