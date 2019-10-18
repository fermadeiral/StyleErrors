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
package org.eclipse.jubula.rc.swing.tester.adapter;

import org.eclipse.jubula.rc.common.driver.IRobotFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.swing.driver.RobotFactoryConfig;
/**
 * 
 * @author BREDEX GmbH
 *
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

}
