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
package org.eclipse.jubula.client.ui.rcp.editors;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jubula.client.core.model.IPersistentObject;


/**
 * Element comparer for persistent objects. Compares IDs if both elements are 
 * persistent objects and neither ID is null. Otherwise, compares using 
 * {@link Object#equals(Object)}.
 *
 * @author BREDEX GmbH
 * @created Aug 23, 2010
 */
public class PersistentObjectComparer implements IElementComparer {

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object a, Object b) {
        if (a instanceof IPersistentObject
                && b instanceof IPersistentObject) {
            IPersistentObject po1 = (IPersistentObject)a;
            IPersistentObject po2 = (IPersistentObject)b;
            if (po1.getId() != null && po2.getId() != null) {
                return new EqualsBuilder()
                    .append(po1.getId(), po2.getId()).isEquals();
            }
        }

        return ObjectUtils.equals(a, b);
    }

    /**
     * {@inheritDoc}
     */
    public int hashCode(Object element) {
        if (element instanceof IPersistentObject) {
            IPersistentObject po = (IPersistentObject)element;
            return new HashCodeBuilder().append(po.getId()).toHashCode();
        }

        return ObjectUtils.hashCode(element);
    }

}
