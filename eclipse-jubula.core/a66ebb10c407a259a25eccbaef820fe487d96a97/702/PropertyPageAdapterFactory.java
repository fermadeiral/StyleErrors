/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.adapter;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.jubula.client.ui.rcp.views.JBPropertiesPage;
import org.eclipse.search.ui.ISearchResultViewPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.properties.IPropertySheetPage;

/**
 * Adapter factory to supply PropertyPage(s)
 * @author BREDEX GmbH
 *
 */
public class PropertyPageAdapterFactory implements IAdapterFactory {

    /** types for which adapters are available */
    private final Class[] m_types = {ISearchResultViewPart.class};

    @Override
    @SuppressWarnings("unchecked")
    public IPage getAdapter(Object adaptableObject, Class adapterType) {
        if (adaptableObject instanceof ISearchResultViewPart
                && adapterType == IPropertySheetPage.class) {
            return new JBPropertiesPage(false, null);
        }
        return null;
    }

    @Override
    public Class<?>[] getAdapterList() {
        return m_types;
    }

}
