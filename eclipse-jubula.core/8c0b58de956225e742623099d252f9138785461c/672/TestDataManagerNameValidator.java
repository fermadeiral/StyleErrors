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
package org.eclipse.jubula.client.ui.rcp.databinding.validators;

import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.StringConstants;


/**
 * Validates a test data cube name.
 *
 * @author BREDEX GmbH
 * @created Jun 29, 2010
 */
public class TestDataManagerNameValidator implements IValidator {
    /**
     * <code>m_alreadyUsedNames</code>
     */
    private Set<String> m_alreadyUsedNames;

    /**
     * Constructor
     * 
     * @param oldName if not null allow this name the support rename
     * @param usedNames a set of already used names
     */
    public TestDataManagerNameValidator(String oldName, Set<String> usedNames) {
        m_alreadyUsedNames = usedNames;
    }
    
    /**
     * {@inheritDoc}
     */
    public IStatus validate(Object value) {
        return isTestDataCubeName(String.valueOf(value));
    }

    /**
     * @param stringValue component name
     * @return IStatus.OK if this is a valid component name, error status
     * otherwise
     */
    public IStatus isTestDataCubeName(String stringValue) {
        IStatus is = isValidTestDataCubeString(stringValue);
        if (!is.isOK()) {
            return is;
        }
        if (!m_alreadyUsedNames.contains(stringValue)) {
            return ValidationStatus.ok();
        }

        return ValidationStatus.error(
                Messages.TestDataCubeErrorExists);
    }

    /**
     * @param stringValue name to check
     * @return IStatus.OK if this is a valid component name, error status
     * otherwise
     */
    public static IStatus isValidTestDataCubeString(String stringValue) {
        if (StringUtils.isEmpty(stringValue)) {
            return ValidationStatus.error(
                    Messages.TestDataCubeErrorEmpty);
        }
        if (stringValue.startsWith(StringConstants.SPACE)
            || stringValue.charAt(
                    stringValue.length() - 1) == ' ') {

            return ValidationStatus.error(
                    Messages.TestDataCubeErrorNoSpaceAtStartOrEnd);
        }
        for (char ch : stringValue.toCharArray()) {
            if (Character.isISOControl(ch)) {
                return ValidationStatus.error(
                        Messages.TestDataCubeErrorInvalidChar);
            }
        }
        return ValidationStatus.ok();
    }

}
