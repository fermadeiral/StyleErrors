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
package org.eclipse.jubula.rc.common.adapter;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.rcp.tabbedproperties.adapter.TabbedPropertiesAdapter;
import org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList;
/**
 * 
 * @author BREDEX GmbH
 *
 */
public class TabbedPropertiesListAdapterFactory implements IAdapterFactory {
    /**
     * {@inheritDoc}
     */
    public Class[] getSupportedClasses() {
        return new Class[]{TabbedPropertyList.class};
    }
    /**
     * {@inheritDoc}
     */
    public Object getAdapter(Class targetAdapterClass, Object objectToAdapt) {
        if (IComponent.class.equals(targetAdapterClass)) {
            if (objectToAdapt instanceof TabbedPropertyList) {
                return new TabbedPropertiesAdapter(objectToAdapt);
            }
        }
        return null;
    }

}
