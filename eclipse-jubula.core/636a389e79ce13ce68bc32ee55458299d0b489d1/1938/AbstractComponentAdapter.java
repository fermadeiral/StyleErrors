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
package org.eclipse.jubula.rc.javafx.tester.adapter;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.stage.Window;

import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.javafx.driver.RobotFactoryJavaFXImpl;
import org.eclipse.jubula.rc.javafx.driver.RobotJavaFXImpl;

/**
 * @param <T>
 *            Type of the component
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public abstract class AbstractComponentAdapter<T> implements IComponent {

    /** the component */
    private T m_component;

    /**
     * Used to store the component into the adapter.
     * 
     * @param objectToAdapt
     *            the object to adapt
     */
    public AbstractComponentAdapter(T objectToAdapt) {
        m_component = objectToAdapt;
        // Nullcheck because it is possible to create an adapter for an Object
        // that doesn't exist. E.g. an adapter for an MenuItem that doesn't
        // exist.
        if (objectToAdapt != null) {
            getRobot().getInterceptor().addSceneGraph(getWindow());
        }
    }

    @Override
    public T getRealComponent() {
        return m_component;
    }

    @Override
    public RobotFactoryJavaFXImpl getRobotFactory() {
        return RobotFactoryJavaFXImpl.INSTANCE;
    }

    /**
     * Gets the Robot.
     * 
     * @return The Robot
     * @throws RobotException
     *             If the Robot cannot be created.
     */
    protected RobotJavaFXImpl getRobot() throws RobotException {
        return getRobotFactory().getRobot();
    }

    /**
     * Returns the Window this Element belongs to. This could be a Stage or a
     * popup. The reason for this is to add a listener to it, to confirm Events.
     * Because events are local to the Window on which they occur.
     * 
     * @return an instance of Window
     */
    public abstract ReadOnlyObjectProperty<? extends Window> getWindow();
}
