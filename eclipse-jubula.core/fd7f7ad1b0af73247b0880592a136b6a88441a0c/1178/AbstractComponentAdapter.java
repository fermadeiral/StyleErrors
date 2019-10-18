/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.rc.swt.driver.RobotFactoryConfig;
import org.eclipse.swt.graphics.Rectangle;
/**
 * @author BREDEX GmBH
 */
public abstract class AbstractComponentAdapter implements IComponent {
    /** the RobotFactory from the AUT */
    private IRobotFactory m_robotFactory;     

    /**
     * Gets the Robot factory. The factory is created once per instance.
     *
     * @return The Robot factory.
     */
    public IRobotFactory getRobotFactory() {
        if (m_robotFactory == null) {
            m_robotFactory = new RobotFactoryConfig().getRobotFactory();
        }
        return m_robotFactory;
    }
    
    /**
     * @return The event thread queuer.
     */
    public IEventThreadQueuer getEventThreadQueuer() {
        return getRobotFactory().getEventThreadQueuer();
    }
    
    /**
     * Gets the Robot. 
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
     */
    protected IRobot<Rectangle> getRobot() throws RobotException {
        return AUTServer.getInstance().getRobot();
    }
    
    /**
     * {@inheritDoc}
     */
    public int getKeyCode(String mod) {
        return KeyCodeConverter.getKeyCode(mod);
    }
}
