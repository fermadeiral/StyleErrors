/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester.adapter.interfaces;

import org.eclipse.jubula.rc.common.driver.IRobotFactory;

/**
 * This is the main interface for classes which will hold or be the
 * component that implement the methods we need for this
 * specific component to test.
 * 
 * @author BREDEX GmbH
 */
public interface IComponent {
    /**
     * Gets the toolkit specific component
     * 
     * @return toolkit specific component
     */
    public Object getRealComponent();

    /**
     * Gets the toolkit specific RobotFactory
     * 
     * @return the RobotFactory
     */
    public IRobotFactory getRobotFactory();
}