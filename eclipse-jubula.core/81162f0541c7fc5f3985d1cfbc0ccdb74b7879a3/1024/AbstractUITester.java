/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.interfaces.ITester;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
/**
 * Implementation of basic functions for all tester classes. This class
 * gives the basic functions which are needed for testing.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractUITester implements ITester {
    /** The default separator of a list of values */
    protected static final char VALUE_SEPARATOR = 
        TestDataConstants.VALUE_CHAR_DEFAULT;
    
    /** The default separator for enumerations of list values. */
    protected static final char INDEX_LIST_SEP_CHAR = 
        TestDataConstants.VALUE_CHAR_DEFAULT;
    
    /** the component adapter */
    private IComponent m_adapter;

    /**
     * Gets the Robot. 
     * @return The Robot
     * @throws RobotException If the Robot cannot be created.
     */
    protected IRobot getRobot() throws RobotException {
        return AUTServer.getInstance().getRobot();
    }
    
    /**
     * @return The event thread queuer.
     */
    protected IEventThreadQueuer getEventThreadQueuer() {
        return getComponent().getRobotFactory().getEventThreadQueuer();
    }
    
    /**
     * {@inheritDoc}
     */
    public void setComponent(Object graphicsComponent) {
        AdapterFactoryRegistry afr =  AdapterFactoryRegistry.getInstance();
        m_adapter = (IComponent) afr.getAdapter(
                IComponent.class, graphicsComponent);
    }
    
    /**
     * This methods is only for special cases. If you only have one tester class
     * which reuses one of our adapters. Otherwise write an adapter factory if
     * you have more tester classes and adapter.
     * 
     * @param adapter
     *            the specific adapter to set
     */
    protected void setAdapter(IComponent adapter) {
        m_adapter = adapter;
    }
    
    /**
     * @return the adapted graphical component instance
     */
    public IComponent getComponent() {
        return m_adapter;
    }
    
    /**
     * @return the "real" graphical component instance
     */
    protected Object getRealComponent() {
        return m_adapter.getRealComponent();
    }
}