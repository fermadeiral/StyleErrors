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
package org.eclipse.jubula.client.ui.databinding;

import org.eclipse.core.databinding.conversion.IConverter;

/**
 * Converts from String to int, ignoring localization (e.g. grouping).
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public class SimpleStringToIntegerConverter implements IConverter {

    /**
     * 
     * {@inheritDoc}
     */
    public Object getToType() {
        return int.class;
    }

    /**
     * 
     * {@inheritDoc}
     */
    public Object getFromType() {
        return String.class;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public Object convert(Object fromObject) {
        try {
            return Integer.parseInt(String.valueOf(fromObject));
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(nfe);
        }
    }

}
