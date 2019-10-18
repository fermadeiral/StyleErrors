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
package org.eclipse.jubula.tools.internal.xml.businessmodell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Container for permitted values.
 * @author BREDEX GmbH
 * @created Sep 6, 2010
 */
public class ParamValueSet {
    /** 
     * The values contained within this value set. This must be a list 
     * (specifically, an {@link ArrayList}): See the javadoc for 
     * {@link com.thoughtworks.xstream.XStream#addImplicitCollection(Class, String)}.
     */
    private List<ValueSetElement> m_valueSet;

    /** whether the values contained in the set can be combined */
    private boolean m_isCombinable;

    /**
     * Constructor. Equivalent to calling 
     * {@link ParamValueSet#ParamValueSet(boolean)} with <code>false</code>.
     */
    public ParamValueSet() {
        this(false);
    }

    /**
     * Constructor
     * @param valuesAreCombinable Whether values in the set can be combined.
     */
    public ParamValueSet(boolean valuesAreCombinable) {
        setCombinable(valuesAreCombinable);
    }

    /** @return an iterator over the contained set of values. */
    public Iterator<ValueSetElement> iterator() {
        return getValueSet().iterator();
    }

    /**
     * @return <code>true</code> if the contained set of values is empty. 
     *         Otherwise <code>false</code>.
     */
    public boolean isEmpty() {
        return getValueSet().isEmpty();
    }

    /**
     * Returns whether the contained values can be combined during use. 
     * Non-combinable values are mutually exclusive. An example of combinable 
     * values is key modifiers (alt, control, shift, etc.), as multiple 
     * modifiers can be used simultaneously. Boolean values, on the other hand, 
     * are an example of non-combinable values, as Jubula does not support 
     * boolean math.
     * @return <code>true</code> if the contained values can be combined. 
     *         Otherwise <code>false</code>.
     */
    public boolean isCombinable() {
        return m_isCombinable;
    }
    
    /**
     * @param valuesAreCombinable Whether the receiver's contained values 
     *          should be combinable.
     */
    public void setCombinable(boolean valuesAreCombinable) {
        m_isCombinable = valuesAreCombinable;
    }

    /**
     * The value set needs to be initialized lazily in order to avoid a 
     * {@link NullPointerException} during {@link #iterator()}.
     * @return the contained set of values.
     */
    private Collection<ValueSetElement> getValueSet() {
        if (m_valueSet == null) {
            m_valueSet = new ArrayList<ValueSetElement>();
        }
        return m_valueSet;
    }
}