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

import java.util.concurrent.LinkedBlockingQueue;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.stage.Window;

import org.eclipse.jubula.rc.common.driver.IRobotEventConfirmer;
import org.eclipse.jubula.rc.common.driver.IRobotEventInterceptor;
import org.eclipse.jubula.rc.common.driver.InterceptorOptions;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * This class intercepts JavaFX events defined by the
 * <code>InterceptorOptions</code>. When <code>intercept()</code> is called, a
 * new
 * {@link org.eclipse.jubula.rc.RobotEventConfirmerJavaFXImpl.driver.awtimpl.RobotEventConfirmerAwtImpl}
 * is created and enabled, so that the confirmer starts collecting events at
 * once.
 * 
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public class RobotEventInterceptorJavaFXImpl implements IRobotEventInterceptor {

    /**
     * Stores Windows on wich Events could occur, this includes Popups such as
     * contextmenus
     */
    private LinkedBlockingQueue<ReadOnlyObjectProperty<? extends Window>> 
        m_sceneGraphs = 
        new LinkedBlockingQueue<ReadOnlyObjectProperty<? extends Window>>();

    /**
     * {@inheritDoc}
     */
    public IRobotEventConfirmer intercept(InterceptorOptions options) {
        RobotEventConfirmerJavaFXImpl confirmer = 
                new RobotEventConfirmerJavaFXImpl(
                options, m_sceneGraphs);
        confirmer.setEnabled(true);
        return confirmer;
    }

    /**
     * Adds a property containing a window and its Scene-Graph, on which we
     * should listen for events to confirm. This is necessary because there are
     * no system wide events. We are using a property because some components
     * don't have their window set, when they are not visible.
     * 
     * @param windowProp
     *            the Window, containing a Scene-Graph
     */
    public void addSceneGraph(
            ReadOnlyObjectProperty<? extends Window> windowProp) {
        if (!m_sceneGraphs.contains(windowProp)) {
            try {
                m_sceneGraphs.put(windowProp);
            } catch (InterruptedException e) {
                new RobotException(
                        "Could not add Scene-Graph for event-listening: " //$NON-NLS-1$
                                + windowProp,
                        EventFactory
                                .createActionError(TestErrorEvent.
                                        EXECUTION_ERROR));
            }
        }
    }
}
