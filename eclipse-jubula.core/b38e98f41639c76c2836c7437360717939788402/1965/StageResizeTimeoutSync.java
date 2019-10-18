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
package org.eclipse.jubula.rc.javafx.listener.sync;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.utils.JBExecutors;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.stage.Stage;
import javafx.stage.Window;

/**
 * "Synchronizes" by waiting a set amount of time. Even if the Stage resizes 
 * before the timeout has elapsed, the full time is waited. After waiting the
 * set amount of time, the lock is released (whether the Stage has been 
 * appropriately resized or not). 
 */
class StageResizeTimeoutSync implements IStageResizeSync {

    /**
     * Acquires the lock
     */
    private class Locker implements ChangeListener<Boolean> {

        /**
         * Releases the lock
         */
        private Unlocker m_unlocker = new Unlocker();
        
        @Override
        public void changed(
                ObservableValue<? extends Boolean> observable,
                Boolean oldValue, Boolean newValue) {

            lock();
        }
        
        /**
         * Acquires the lock and defines the criteria necessary to release the
         * lock. This method may only be called on the FX Thread.
         */
        public void lock() {
            EventThreadQueuerJavaFXImpl.checkEventThread();

            m_lock.lock();
            
            m_executor.schedule(
                    m_unlocker, 2000, TimeUnit.MILLISECONDS);
        }
    }
    
    /**
     * Releases the lock. This can be used as a fallback if the condition for 
     * releasing the lock is not fulfilled within a specified amount of time.
     */
    private class Unlocker implements Runnable {
        @Override
        public void run() {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    m_lock.unlock();
                }
            });
        }
    }
    
    /** The lock used for synchronization. */
    private final ReentrantLock m_lock = new ReentrantLock();

    /** the executor for fallback unlocking */
    private final ScheduledExecutorService m_executor = 
            JBExecutors.newSingleDaemonThreadScheduledExecutor(
                    StageResizeTimeoutSync.class.getSimpleName());
    
    /**
     * Acquires the lock
     */
    private Locker m_locker = new Locker();
    
    @Override
    public void register(Window win) {
        if (win instanceof Stage) {
            ((Stage) win).maximizedProperty().addListener(m_locker);
            m_locker.lock();
        }
    }

    @Override
    public void deregister(Window win) {
        if (win instanceof Stage) {
            ((Stage) win).maximizedProperty().removeListener(m_locker);
        }
    }
    
    @Override
    public void await() {
        EventThreadQueuerJavaFXImpl.checkNotEventThread();

        EventThreadQueuerJavaFXImpl.waitForIdle();
        m_lock.lock();
        m_lock.unlock();
    }
}
