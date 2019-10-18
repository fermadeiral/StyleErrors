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
package org.eclipse.jubula.client.ui.rcp.databinding;

import org.eclipse.core.databinding.conversion.Converter;

/**
 * Databinding converter for boolean values. Converts the value to
 * its opposite. Ex. Converts "true" to "false".
 *
 * @author BREDEX GmbH
 * @created Nov 21, 2008
 */
public class InverseBooleanConverter extends Converter {

    /**
     * Constructor
     */
    public InverseBooleanConverter() {
        super(true, true);
    }
    
    /**
     * {@inheritDoc}
     */
    public Object convert(Object fromObject) {
        return !((Boolean)fromObject).booleanValue();
    }

}
