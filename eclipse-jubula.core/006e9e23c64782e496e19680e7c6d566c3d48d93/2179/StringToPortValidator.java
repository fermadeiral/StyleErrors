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
package org.eclipse.jubula.client.ui.databinding.validators;

import org.apache.commons.lang.Validate;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.ui.validator.cell.PortCellEditorValidator;

/**
 * Converts from String to int, ignoring localization (e.g. grouping).
 * 
 * @author BREDEX GmbH
 * @created 19.01.2011
 */
public class StringToPortValidator implements IValidator {

    /** validation delegate */
    private PortCellEditorValidator m_validationDelegate;
    
    /**
     * Constructor
     * 
     * @param fieldName The name of the field containing the value to convert.
     *                  Must not be <code>null</code>.
     */
    public StringToPortValidator(String fieldName) {
        Validate.notNull(fieldName);
        m_validationDelegate = new PortCellEditorValidator(fieldName);
    }

    /**
     * 
     * {@inheritDoc}
     */
    public IStatus validate(Object value) {
        String validationErrorMessage = m_validationDelegate.isValid(value);
        if (validationErrorMessage != null) {
            return ValidationStatus.error(validationErrorMessage);
        }
        
        return ValidationStatus.ok();
    }
}
