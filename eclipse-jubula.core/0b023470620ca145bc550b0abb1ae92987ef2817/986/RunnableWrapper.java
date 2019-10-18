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
 * A wrapper around <code>IRunnable</code>. The wrapper is actually invoked
 * in the AWT event queue thread. <br>
 * 
 * This class stores a the result from <code>IRunnable.run()</code> and a
 * possibly thrown exception.
 * 
 * @author BREDEX GmbH
 * @created 07.04.2005
 * 
 * @param <V> the result type of embedded runnable method <tt>run</tt>
 */
public class RunnableWrapper<V> implements Runnable {
    /** a name describing this instance, for logging purpose */
    private String m_name;
    /**
     * The runnable.
     */
    private IRunnable<V> m_target;

    /** the returned object from run() */
    private V m_result;

    /** the exception thrown by run() */
    private StepExecutionException m_exception;
    /**
     * Creates a new wrapper instance.
     * 
     * @param name
     *            of this instance, for logging purpose
     * @param target
     *            The runnable which will be executed by this wrapper.
     */
    public RunnableWrapper(String name, IRunnable<V> target) {
        m_name = name;
        m_target = target;
    }
    /**
     * @return Returns the name.
     */
    public String getName() {
        return m_name;
    }
    /**
     * @return Returns the result from run(), may be <code>null</code>.
     */
    public V getResult() {
        return m_result;
    }
    /**
     * @return Returns the exception thrown by run() or <code>null</code> if
     *         no exception was thrown.
     */
    public StepExecutionException getException() {
        return m_exception;
    }
    /**
     * executes the method {@link IRunnable#run()}and stores the return value
     * and a thrown <code>StepExecutionEception</code>.
     */
    public final void run() {
        try {
            m_result = m_target.run();
        } catch (StepExecutionException bsee) {
            m_exception = bsee;
        } catch (Throwable t) {
            m_exception = new StepExecutionException(t);
        }
    }
}
