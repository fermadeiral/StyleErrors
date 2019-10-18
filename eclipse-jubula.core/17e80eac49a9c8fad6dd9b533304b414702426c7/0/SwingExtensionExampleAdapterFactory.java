/*******************************************************************************
 * Copyright (c) 2013, 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.common.adapter;

import javax.swing.JSpinner;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.swing.tester.adapter.JComponentAdapter;

/**
 * Adapter Factory for new adapters. This class makes your new adapters usable
 * for testing. One factory could be used for all adapters implemented.
 * 
 * @author BREDEX GmbH
 */
public class SwingExtensionExampleAdapterFactory implements IAdapterFactory {
    /** {@inheritDoc} */
    public Class[] getSupportedClasses() {
        return new Class[] { JSpinner.class };
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class targetedClass, Object objectToAdapt) {
        if (targetedClass.isAssignableFrom(IComponent.class)) {
            if (objectToAdapt instanceof JSpinner) {
                return new JComponentAdapter(objectToAdapt);
            }
        }
        return null;
    }
}
