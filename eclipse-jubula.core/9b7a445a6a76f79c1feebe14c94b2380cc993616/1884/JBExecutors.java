/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang.Validate;

/**
 * Factory and utility methods for 
 * {@link java.util.concurrent.Executor} and {@link ThreadFactory}.
 * 
 */
public class JBExecutors {

    /**
     * {@link ThreadFactory} implementation that creates daemon threads.
     */
    private static class DaemonThreadFactory implements ThreadFactory {

        /**
         * prefix for naming created threads
         */
        private final String m_poolName;

        /**
         * number of threads created by this factory
         */
        private final AtomicInteger m_threadNumber;

        /**
         * 
         * @param poolName Prefix for naming threads created by this factory.
         *                 May not be <code>null</code>.
         */
        public DaemonThreadFactory(String poolName) {
            Validate.notNull(poolName);
            m_poolName = poolName + "-"; //$NON-NLS-1$
            m_threadNumber = new AtomicInteger();
        }
            
        @Override
        public Thread newThread(Runnable r) {
            Thread newThread = 
                new Thread(r, m_poolName + m_threadNumber.incrementAndGet());

            newThread.setDaemon(true);
            return newThread;
        }
    }
    
    /**
     * private Constructor
     */
    private JBExecutors() {
        // private Constructor
    }

    /**
     * 
     * @param poolName The prefix to use for naming {@link Thread}s created by
     *                 the returned {@link ThreadFactory}. May not be 
     *                 <code>null</code>.
     * @return a {@link ThreadFactory} that creates daemon threads.
     */
    public static ThreadFactory daemonThreadFactory(String poolName) {
        return new DaemonThreadFactory(poolName);
    }
    
    /**
     * 
     * @param poolName The prefix to use for naming worker threads. 
     *                 May not be <code>null</code>.
     * @return the newly created single-threaded daemon {@link ExecutorService}.
     */
    public static ExecutorService newSingleDaemonThreadExecutor(
            String poolName) {
        
        return Executors.newSingleThreadExecutor(daemonThreadFactory(poolName));
    }
    
    /**
     * 
     * @param poolName The prefix to use for naming worker threads. 
     *                 May not be <code>null</code>.
     * @return the newly created single-threaded daemon 
     *         {@link ScheduledExecutorService}.
     */
    public static ScheduledExecutorService 
        newSingleDaemonThreadScheduledExecutor(String poolName) {
        
        return Executors.newSingleThreadScheduledExecutor(
                daemonThreadFactory(poolName));
    }
}
