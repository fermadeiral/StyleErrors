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
package org.eclipse.jubula.rc.javafx.listener;

import org.eclipse.jubula.rc.javafx.utils.JBExecutors;

import javafx.application.Platform;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;

/**
 * Alters the JavaFx mouse moved event, which is always fired when the mouse
 * moves on a node, to an event that is only fired when a mouse move is over.
 *
 * @author BREDEX GmbH
 * @created 18.10.2013
 */
public class MouseMoveDone implements EventHandler<MouseEvent> {

    /** deadzone on the x-axis **/
    private double m_deltaX;

    /** deadzone on the y-axis **/
    private double m_deltaY;

    /**
     * threshold the threshold in milliseconds when a mouse move is interpreted
     * as done
     **/
    private final long m_threshold;

    /** Last event **/
    private volatile MouseEvent m_lastEvent;

    /** Timer Service **/
    private Service<MouseEvent> m_timeService;

    /**
     *
     * @param threshold
     *            threshold the threshold in milliseconds when a mouse move is
     *            interpreted as done
     * @param deltaX
     *            deadzone on the x-axis
     * @param deltaY
     *            deadzone on the y-axis
     */
    public MouseMoveDone(long threshold, double deltaX, double deltaY) {
        m_threshold = threshold;
        m_deltaX = deltaX;
        m_deltaY = deltaY;

        m_timeService = new Service<MouseEvent>() {
            @Override
            protected Task<MouseEvent> createTask() {
                return new WaitingTask();
            }
        };
        
        m_timeService.setExecutor(
                JBExecutors.newSingleDaemonThreadExecutor(
                        MouseMoveDone.class.getSimpleName()));
    }

    @Override
    public void handle(MouseEvent event) {
        if (m_lastEvent == null) {
            m_lastEvent = event;
        }
        double currDX = Math.abs((event.getSceneX() - m_lastEvent.getSceneX()));
        double currDY = Math.abs((event.getSceneX() - m_lastEvent.getSceneX()));
        if (currDX >= m_deltaX && currDY >= m_deltaY) {
            m_timeService.restart();
            m_lastEvent = event;
        }
    }

    /**
     * Adds a handler to the Service. This handler will be notified when the
     * Task in this service succeeds. Therefore when a mouse move is completed.
     *
     * @param hl
     *            the handler
     */
    public void addMoveDoneHandler(final EventHandler<WorkerStateEvent> hl) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                m_timeService.setOnSucceeded(hl);
            }
        });

    }

    /**
     * Removes a handler from the Service.
     *
     * @param hl
     *            the handler
     */
    public void removeMoveDoneHandler(final EventHandler<WorkerStateEvent> hl) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                m_timeService.removeEventHandler(
                        WorkerStateEvent.WORKER_STATE_SUCCEEDED, hl);
            }
        });

    }

    /**
     * Realizes the waiting
     *
     */
    private class WaitingTask extends Task<MouseEvent> {
        @Override
        protected MouseEvent call() throws Exception {
            Thread.sleep(m_threshold);
            return m_lastEvent;
        }
    }
}
