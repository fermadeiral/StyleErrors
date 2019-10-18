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

import org.eclipse.jubula.rc.common.exception.RobotException;

/**
 * This factory provides access to the Robot implementation.
 * Implementing classes create Graphics API specific instances of
 * the Robot, the event interceptor and the mouse motion tracker.
 * See {@link org.eclipse.jubula.rc.swing.driver.RobotFactoryConfig} to learn
 * more about the programming model.
 * 
 * @author BREDEX GmbH
 * @created 17.03.2005
 */
public interface IRobotFactory {
    /**
     * @return The event interceptor.
     */
    public IRobotEventInterceptor getRobotEventInterceptor();
    /**
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
     */
    public IRobot getRobot() throws RobotException;
    /**
     * @return The mouse motion tracker.
     */
    public IMouseMotionTracker getMouseMotionTracker();
    /**
     * @return The event thread queuer.
     */
    public IEventThreadQueuer getEventThreadQueuer();
}
