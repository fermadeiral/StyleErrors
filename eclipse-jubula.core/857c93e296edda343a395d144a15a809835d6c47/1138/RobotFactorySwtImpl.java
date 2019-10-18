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
package org.eclipse.jubula.rc.swt.driver;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IMouseMotionTracker;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotEventInterceptor;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.RobotException;

/**
 * This factory creates SWT implementations of the Robot, the
 * interceptor and the mouse motion tracker.
 *
 * @author BREDEX GmbH
 * @created 26.07.2006
 */
public class RobotFactorySwtImpl implements IRobotFactory {
    
    /** The SWT Robot. */
    private RobotSwtImpl m_robot;
    
    /** The SWT interceptor. */
    private IRobotEventInterceptor m_interceptor;
    
    /** The SWT event thread queuer. */
    private IEventThreadQueuer m_eventThreadQueuer;
    
    /**
     * {@inheritDoc}
     */
    public IRobotEventInterceptor getRobotEventInterceptor() {
        if (m_interceptor == null) {
            m_interceptor = new RobotEventInterceptorSwtImpl();
        }
        return m_interceptor;
    }
    
    /**
     * Returns a {@link org.eclipse.jubula.rc.swt.driver.RobotSwtImpl}.
     * 
     * {@inheritDoc}
     */
    public IRobot getRobot() throws RobotException {
        if (m_robot == null) {
            m_robot = new RobotSwtImpl(this);
        }
        return m_robot;
    }
    
    /**
     * {@inheritDoc}
     */
    public IMouseMotionTracker getMouseMotionTracker() {
        // no mouseMotionTracker needed in SWT
        return null;
    }
    
    /**
     * {@inheritDoc}
     */
    public IEventThreadQueuer getEventThreadQueuer() {
        if (m_eventThreadQueuer == null) {
            m_eventThreadQueuer = new EventThreadQueuerSwtImpl();
        }
        return m_eventThreadQueuer;
    }
}