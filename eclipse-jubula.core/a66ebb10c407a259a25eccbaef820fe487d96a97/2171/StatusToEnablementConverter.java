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

import org.eclipse.core.databinding.conversion.Converter;
import org.eclipse.core.runtime.IStatus;

/**
 * Converts IStatus objects to boolean values. Any non-OK IStatus results
 * in a value of <code>false</code>.
 *
 * @author BREDEX GmbH
 * @created Jan 12, 2009
 */
public class StatusToEnablementConverter extends Converter {
    
    /**
     * Constructor
     * 
     */
    public StatusToEnablementConverter() {
        super(IStatus.class, Boolean.class);
    }

    /**
     * {@inheritDoc}
     */
    public Object convert(Object fromObject) {
        if (fromObject instanceof IStatus) {
            return ((IStatus)fromObject).isOK();
        }
        
        return false;
    }
}

