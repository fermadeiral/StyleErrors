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
package org.eclipse.jubula.client.ui.rcp.validator;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jubula.client.core.model.IPersistentObject;

/**
 * @author BREDEX GmbH
 */
public class MaxStringLengthValidator implements IValidator {
    /** {@inheritDoc} */
    public IStatus validate(Object value) {
        if (value instanceof String) {
            return (((String) value).length()
                        < IPersistentObject.MAX_STRING_LENGTH) 
                            ? Status.OK_STATUS : Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }
}
