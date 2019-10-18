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
package org.eclipse.jubula.client.ui.utils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * A timer with a task which can be cancelled an (re-)scheduled
 */
public class DelayableTimer extends Timer {
    /** the task to be executed */
    private Runnable m_task;
    
    /** the timerTask to execute the task */
    private TimerTask m_timerTask;
    
    /** the time to delay the execution of the task */
    private long m_delay;
    
    /**
     * the constructor
     * @param task the task to execute
     * @param delay the delay before an execution of the task
     */
    public DelayableTimer(long delay, Runnable task) {
        m_task = task;
        m_delay = delay;
    }
    
    /**
     * schedules a new delayed execution
     */
    public void schedule() {
        m_timerTask = new TimerTask() { 
            public void run() { 
                m_task.run();
            }
        };
        this.schedule(m_timerTask, m_delay);        
    }
    
    /**
     * cancels the current plan to execute the task
     */
    public void cancel() {
        if (m_timerTask != null) {
            m_timerTask.cancel();
        }
    }
}