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

import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang.Validate;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.utils.NameValidationUtil;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.osgi.util.NLS;

/**
 * Validates the uniqueness of AUT IDs within the context of a Project.
 * 
 * @author BREDEX GmbH
 * @created Jan 20, 2010
 */
public class AutIdValidator implements IValidator {

    /** the context in which AUT IDs will be validated */
    private IProjectPO m_project;

    /** optional additional context for the validation */
    private IAUTMainPO m_additionalAut;

    /**
     * Optional currently edited AUT Configuration. The AUT ID for this
     * configuration will be ignored when evaluating uniqueness.
     */
    private IAUTConfigPO m_editedConfig;

    /**
     * Constructor
     * 
     * @param project
     *            The context in which AUT IDs will be validated.
     */
    public AutIdValidator(IProjectPO project) {
        this(project, null);
    }

    /**
     * Constructor
     * 
     * @param project
     *            The context in which AUT IDs will be validated. May *not* be
     *            <code>null</code>.
     * @param additionalAut
     *            An additional AUT to use as context for validation. this can
     *            be used if an AUT belongs in the validation context even
     *            though it is not a part of the given project. May be
     *            <code>null</code>, in which case only the given Project is
     *            used as the validation context.
     */
    public AutIdValidator(IProjectPO project, IAUTMainPO additionalAut) {
        this(project, additionalAut, null);
    }

    /**
     * Constructor
     * 
     * @param project
     *            The context in which AUT IDs will be validated. May *not* be
     *            <code>null</code>.
     * @param additionalAut
     *            An additional AUT to use as context for validation. This can
     *            be used if an AUT belongs in the validation context even
     *            though it is not a part of the given project. May be
     *            <code>null</code>, in which case only the given Project is
     *            used as the validation context.
     * @param editedConfig
     *            The AUT Configuration currently being edited. The AUT ID for
     *            this configuration will be ignored when evaluating
     *            uniqueness.. May be <code>null</code>, in which case only the
     *            given Project is used as the validation context.
     */
    public AutIdValidator(IProjectPO project, IAUTMainPO additionalAut,
            IAUTConfigPO editedConfig) {
        Validate.notNull(project);
        m_project = project;
        m_additionalAut = additionalAut;
        m_editedConfig = editedConfig;
    }

    /**
     * {@inheritDoc}
     */
    public IStatus validate(Object value) {
        String stringValue = String.valueOf(value);
        if (stringValue.length() == 0) {
            return ValidationStatus
                    .error(Messages.AutIdValidatorErrorEmptyString);
        }

        if (!stringValue.trim().equals(stringValue)) {
            return ValidationStatus
                    .error(Messages.AutIdValidatorErrorTrimWhitespace);
        }

        if (!NameValidationUtil.containsNoIllegalChars(stringValue)) {
            return ValidationStatus
                    .error(Messages.AutIdValidatorErrorIllegalChars);
        }

        Set<IAUTMainPO> validationContext = new HashSet<IAUTMainPO>();
        validationContext.addAll(m_project.getAutMainList());
        if (m_additionalAut != null) {
            validationContext.add(m_additionalAut);
        }

        for (IAUTMainPO aut : validationContext) {
            if (aut.getAutIds().contains(value)) {
                return ValidationStatus.error(NLS.bind(
                        Messages.AutIdValidatorErrorAlreadyExistsInAut,
                        new String[] { stringValue, aut.getName() }));
            }
            for (IAUTConfigPO autConfig : aut.getAutConfigSet()) {
                if (!autConfig.equals(m_editedConfig)
                        && autConfig.getValue(AutConfigConstants.AUT_ID,
                                StringConstants.EMPTY).equals(value)) {
                    return ValidationStatus.error(NLS.bind(Messages.
                            AutIdValidatorErrorAlreadyExistsInAutConfiguration,
                                            new String[] { stringValue,
                                                    autConfig.getName(),
                                                    aut.getName() }));
                }
            }
        }

        return ValidationStatus.ok();
    }

}
