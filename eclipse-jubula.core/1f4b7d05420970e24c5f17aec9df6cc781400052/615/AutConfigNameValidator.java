/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.databinding.validators;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.osgi.util.NLS;

/**
 * Checks the AUT Configuration Name for duplicates only.
 * 
 * @author BREDEX GmbH
 * @created Jun 16, 2016
 */
public class AutConfigNameValidator implements IValidator {


    /** the AUT in which the AUT Configuration Name should be checked */
    private IAUTMainPO m_aut;

    /** The currently edited AUT Configuration */
    private IAUTConfigPO m_currentConfig;

    /**
     * Constructor
     * 
     * @param aut
     *            The current AUT in which the AUT Configuration Name will be
     *            checked for duplicates.
     * @param currentConfig
     *            The current AUT Configuration.
     */
    public AutConfigNameValidator(IAUTMainPO aut,
            IAUTConfigPO currentConfig) {
        
        m_aut = aut;
        m_currentConfig = currentConfig;
    }
    
    /**
     * Validates whether the AUT Configuration Name is a duplicate.
     * @param value the value to validate
     * @return a status object indicating whether the validation succeeded 
     * IStatus.isOK() or not. Never null.
     */
    public IStatus validate(Object value) {
        String stringValue = String.valueOf(value);
        
        List<String> componentNames = new ArrayList<>();
        Set<IAUTConfigPO> autConfigs = m_aut.getAutConfigSet();
        for (IAUTConfigPO config : autConfigs) {
            if ((m_currentConfig != null) 
                    && !config.equals(m_currentConfig)) {
                
                componentNames.add(config.getName());
            }
        }
        
        if (componentNames.contains(stringValue)) {
            return ValidationStatus.error(NLS.bind(Messages.
                    AUTConfigComponentDuplicateAUTConfigName,
                    new String[] { stringValue,
                            m_aut.getName()}));
        }
        return ValidationStatus.ok();
    }

}
