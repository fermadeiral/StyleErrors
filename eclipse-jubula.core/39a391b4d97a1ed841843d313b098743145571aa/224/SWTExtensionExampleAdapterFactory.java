/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.common.adapter;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.swt.tester.adapter.WidgetAdapter;
import org.eclipse.swt.widgets.Group;

/**
 * Adapter Factory for new adapters. This class makes your new adapters usable
 * for testing. One factory could be used for all adapters implemented.
 * 
 * @author BREDEX GmbH
 */
public class SWTExtensionExampleAdapterFactory implements IAdapterFactory {
    /** {@inheritDoc} */
    public Class[] getSupportedClasses() {
        return new Class[] { Group.class };
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class targetedClass, Object objectToAdapt) {
        if (targetedClass.isAssignableFrom(IComponent.class)) {
            if (objectToAdapt instanceof Group) {
                return new WidgetAdapter(objectToAdapt);
            }
        }
        return null;
    }
}
