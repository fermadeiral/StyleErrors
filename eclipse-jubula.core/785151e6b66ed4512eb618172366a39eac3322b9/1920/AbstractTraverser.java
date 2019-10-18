/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.util;

/**
 * 
 * @author BREDEX GmbH
 *
 * @param <T>
 *            The type of the instances which you are looking for
 * @param <ParentT>
 *            the type of the objects which are considered to be the parents
 */
public abstract class AbstractTraverser<T, ParentT extends Object> {
    /** the object which will be traversed **/
    private ParentT m_parent;
    
    /**
     * Constructor
     * @param parent the object which will be traversed
     */
    public AbstractTraverser(ParentT parent) {
        m_parent = parent;
    }

    /**
     * 
     * @return the object which will be traversed
     */
    public ParentT getObject() {
        return m_parent;
    }

    /**
     * 
     * @param object the object which will be traversed
     */
    public void setObject(ParentT object) {
        this.m_parent = object;
    }

    /**
     * Get the data structure which holds object of the type T
     * @return the data structure
     */
    public abstract Iterable<T> getTraversableData();
    
}