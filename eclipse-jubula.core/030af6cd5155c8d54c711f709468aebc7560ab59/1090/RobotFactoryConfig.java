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

/**
 * <p>This class is the entry point when using the Robot API. It creates a
 * Robot factory instance. At this point of implementation, the
 * configuration always returns a SWT factory implementation.</p>
 * The programming model is as follows:
 * 
 * <pre>
 * IRobotFactory factory = new RobotFactoryConfig().getRobotFactory();
 * IRobot robot = factory.getRobot();
 * robot.click(component, null);
 * </pre>
 *
 * @author BREDEX GmbH
 * @created 26.07.2006
 */
public class RobotFactoryConfig {
    /**
     * @return A new AWT/Swing factory implementation.
     */
    public RobotFactorySwtImpl getRobotFactory() {
        return new RobotFactorySwtImpl();
    }
}