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
package org.eclipse.jubula.client.ui.rcp.controllers.propertysources;

import org.eclipse.jubula.client.core.model.IAUTConfigPO;
import org.eclipse.jubula.client.core.model.IAUTMainPO;
import org.eclipse.jubula.client.core.model.IProjectPO;
import org.eclipse.jubula.client.core.persistence.GeneralStorage;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.constants.AutConfigConstants;
import org.eclipse.jubula.tools.internal.registration.AutIdentifier;
import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * Property source for AutIdentifier model objects.
 *
 * @author BREDEX GmbH
 * @created Feb 1, 2010
 */
public class AutIdentifierPropertySource implements IPropertySource {

    /** 
     * ID for the "matchedAut" property.
     * This property indicates which AUT corresponds to the AUT ID. 
     */
    private static final String PROP_MATCHED_AUT = "matchedAut"; //$NON-NLS-1$
    
    /** the backing model object */
    private AutIdentifier m_autId;
    
    /** the cached property descriptors */
    private IPropertyDescriptor [] m_descriptors = null;
    
    /**
     * Constructor
     * 
     * @param autId The model object wrapped by this property source.
     */
    public AutIdentifierPropertySource(AutIdentifier autId) {
        m_autId = autId;
    }

    /**
     * {@inheritDoc}
     */
    public Object getEditableValue() {
        return m_autId;
    }

    /**
     * {@inheritDoc}
     */
    public IPropertyDescriptor[] getPropertyDescriptors() {
        if (m_descriptors == null) {
            m_descriptors = new IPropertyDescriptor [] {
                new PropertyDescriptor(AutIdentifier.PROP_EXECUTABLE_NAME, 
                    Messages.
                    AutIdentifierPropertySourcePropertyLabelExecutable),
                new PropertyDescriptor(PROP_MATCHED_AUT, 
                    Messages.
                    AutIdentifierPropertySourcePropertyLabelMatchedAut)
            };
        }
        return m_descriptors;
    }

    /**
     * {@inheritDoc}
     */
    public Object getPropertyValue(Object id) {
        
        if (AutIdentifier.PROP_EXECUTABLE_NAME.equals(id)) {
            return m_autId.getExecutableName();
        }

        if (PROP_MATCHED_AUT.equals(id)) {
            IProjectPO currentProject = 
                GeneralStorage.getInstance().getProject();
            String runningAutId = m_autId.getExecutableName();
            if (runningAutId != null && currentProject != null) {
                for (IAUTMainPO aut : currentProject.getAutMainList()) {
                    if (aut.getAutIds().contains(runningAutId)) {
                        return aut.getName();
                    }
                    for (IAUTConfigPO autConfig : aut.getAutConfigSet()) {
                        if (runningAutId.equals(autConfig.getConfigMap().get(
                                AutConfigConstants.AUT_ID))) {
                            return aut.getName();
                        }
                    }
                }
            }
        }
        
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isPropertySet(Object id) {
        // Not meaningful for objects without editable values. 
        // Just return false.
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void resetPropertyValue(Object id) {
        // Not meaningful for objects without editable values. 
        // Do nothing.
    }

    /**
     * {@inheritDoc}
     */
    public void setPropertyValue(Object id, Object value) {
        // Not meaningful for objects without editable values. 
        // Do nothing.
    }

}
