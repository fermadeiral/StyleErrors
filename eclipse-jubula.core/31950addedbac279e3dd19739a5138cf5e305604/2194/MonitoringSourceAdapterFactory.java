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
package org.eclipse.jubula.client.ui.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.core.model.ITestResultSummaryPO;
import org.eclipse.jubula.client.ui.controllers.propertysources.MonitoringValuePropertySource;

/**
 * @author BREDEX GmbH
 */
public class MonitoringSourceAdapterFactory implements IAdapterFactory {

    /** types for which adapters are available */
    private final Class<?>[] m_types = { ITestResultSummaryPO.class };

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object getAdapter(Object adaptableObject, Class adapterType) {
        
        if (adaptableObject instanceof ITestResultSummaryPO) {            
            return new MonitoringValuePropertySource(
                    (ITestResultSummaryPO)adaptableObject);
        }  
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public Class<?>[] getAdapterList() {

        return m_types;
    }

}
