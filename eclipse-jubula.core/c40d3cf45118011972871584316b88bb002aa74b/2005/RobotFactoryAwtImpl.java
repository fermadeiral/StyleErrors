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

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IMouseMotionTracker;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotEventInterceptor;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.RobotException;

/**
 * This factory creates AWT/Swing implementations of the Robot, the
 * interceptor and the mouse motion tracker.
 *
 * @author BREDEX GmbH
 * @created 17.03.2005
 */
public class RobotFactoryAwtImpl implements IRobotFactory {
    /**
     * The Robot.
     */
    private IRobot m_robot;
    
    /**
     * The AWT/Swing interceptor.
     */
    private IRobotEventInterceptor m_interceptor;
    
    /**
     * The AWT/Swing mouse motion tracker.
     */
    private IMouseMotionTracker m_mouseMotionTracker;
    
    /**
     * The AWT event thread queuer.
     */
    private IEventThreadQueuer m_eventThreadQueuer;
    
    /**
     * {@inheritDoc}
     */
    public IRobotEventInterceptor getRobotEventInterceptor() {
        if (m_interceptor == null) {
            m_interceptor = new RobotEventInterceptorAwtImpl();
        }
        return m_interceptor;
    }
    
    /**
     * {@inheritDoc}
     */
    public IRobot getRobot() throws RobotException {
        if (m_robot == null) {
            m_robot = new RobotAwtImpl(this);
        }
        return m_robot;
    }
    
    /**
     * {@inheritDoc}
     */
    public IMouseMotionTracker getMouseMotionTracker() {
        if (m_mouseMotionTracker == null) {
            m_mouseMotionTracker = new MouseMotionTrackerAwtImpl();
        }
        return m_mouseMotionTracker;
    }
    
    /**
     * {@inheritDoc}
     */
    public IEventThreadQueuer getEventThreadQueuer() {
        if (m_eventThreadQueuer == null) {
            m_eventThreadQueuer = new EventThreadQueuerAwtImpl();
        }
        return m_eventThreadQueuer;
    }
}
