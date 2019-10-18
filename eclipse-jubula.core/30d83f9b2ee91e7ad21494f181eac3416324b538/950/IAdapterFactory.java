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
package org.eclipse.jubula.rc.common.adaptable;

/**
 * Factory that handles the adapting process from a object given set of target
 * classes
 */
public interface IAdapterFactory {

    /**
     * @return all classes that will be supported by this adapter factory
     */
    Class[] getSupportedClasses();

    /**
     * Adapts object to adapt to a new object of type targetAdapterClass
     * 
     * @param targetAdapterClass
     *            class to adapt to
     * @param objectToAdapt
     *            object that should be adapted
     * @return the adapter for the object to adapt of type targetAdapterClass
     */
    Object getAdapter(Class targetAdapterClass, Object objectToAdapt);

}
