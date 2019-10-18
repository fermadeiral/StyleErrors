/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.adapter;

import javafx.scene.control.ButtonBar;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.javafx.j8u40.tester.adapter.ButtonBarContainerAdapter;
import org.eclipse.jubula.rc.javafx.tester.adapter.IContainerAdapter;

/**
 * Adapter Factory for new adapters required for classes available in Java 8
 * update 40 and higher
 * 
 * @author BREDEX GmbH
 */
public class JavaFXJ8U40ContainerAdapterFactory implements IAdapterFactory {
    /** {@inheritDoc} */
    public Class[] getSupportedClasses() {
        return new Class[] { ButtonBar.class };
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class targetedClass, Object objectToAdapt) {
        if (targetedClass.isAssignableFrom(IContainerAdapter.class)) {
            if (objectToAdapt instanceof ButtonBar) {
                return new ButtonBarContainerAdapter<ButtonBar>(
                        (ButtonBar) objectToAdapt);
            }
        }
        return null;
    }
}
