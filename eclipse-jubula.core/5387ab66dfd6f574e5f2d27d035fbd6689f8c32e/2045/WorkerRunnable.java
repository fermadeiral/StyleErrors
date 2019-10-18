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
package org.eclipse.jubula.rc.swing.utils;

import java.util.LinkedList;

/**
 * Runnable that queues and performs work. Work is guaranteed to be performed in
 * same order in which is was enqueued.
 * 
 * @author BREDEX GmbH
 * @created 05.01.2011
 */
public class WorkerRunnable implements Runnable {

    /** 
     * queue of work to be be performed. 
     * contains only instances of {@link Runnable}
     */
    private LinkedList<Runnable> m_workQueue = new LinkedList<Runnable>();
    
    /**
     * Add a {@link Runnable} to the work queue.
     * 
     * @param work The work to be added to the work queue.
     */
    public synchronized void addWork(Runnable work) {
        m_workQueue.addLast(work);
        notify();
    }

    /**
     * 
     * {@inheritDoc}
     */
    public void run() {
        try {
            while (true) {
                Runnable work = waitForWork();
                work.run();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Waits (blocks) until there is at least one unit of work in the queue.
     * 
     * @return the next unit of work to perform.
     */
    private synchronized Runnable waitForWork() throws InterruptedException {
        while (m_workQueue.isEmpty()) {
            wait();
        }
        return m_workQueue.removeFirst();
    }
}
