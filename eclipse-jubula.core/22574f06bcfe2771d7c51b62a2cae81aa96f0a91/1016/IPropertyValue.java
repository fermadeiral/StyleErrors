/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
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
 * Adaptable type for non-primitive property value rendering
 * 
 * @param <T>
 *            the type of the property to render
 */
public interface IPropertyValue<T> {
    /**
     * @return Return a string representation of the given object. May also
     *         return <code>null</code> which will be treated as an empty
     *         String.
     * @param o
     *            the object to get a string representation for
     */
    public String getStringRepresentation(T o);
}
