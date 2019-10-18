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

import java.util.ArrayList;
import java.util.List;

/**
 * A more generic implementation to traverse a given data structure to find
 * instances of a given type
 * 
 * @author BREDEX GmbH
 * @created 27.08.2014
 *
 * @param <Type>
 *            The type of the instances which you are looking for
 * @param <ParentType>
 *            the type of the objects which are considered to be the parents
 */
public class GenericTraverseHelper<Type extends Object,
    ParentType extends Object> {
    /** The result where all instances of the given type are stored */
    private List<Type> m_result = new ArrayList<Type>();

    /**
     * Finds instances of a certain type in the hierarchy
     * 
     * @param traverser
     *            the parent
     * @param type
     *            the type
     */
    private void findInstancesOf(AbstractTraverser<Type, ParentType> traverser,
            Class<Type> type) {
        for (Type object : traverser.getTraversableData()) {
            if (type.isAssignableFrom(object.getClass())) {
                m_result.add(object);
            }
            if (traverser.getObject().getClass()
                    .isAssignableFrom(object.getClass())) {
                traverser.setObject((ParentType) object);
                findInstancesOf(traverser, type);
            }
        }
    }

    /**
     * Gives instances of a certain type in the hierarchy
     * 
     * @param traverser
     *            the traverser
     * @param type
     *            the type
     * @return returns all instances of the given type which are below the
     *         parent in the hierarchy
     */
    public List<Type> getInstancesOf(
            AbstractTraverser<Type, ParentType> traverser, Class<Type> type) {
        m_result.clear();
        findInstancesOf(traverser, type);
        return m_result;
    }
}