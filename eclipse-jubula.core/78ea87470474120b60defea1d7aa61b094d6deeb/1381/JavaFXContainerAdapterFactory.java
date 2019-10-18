/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.ext.rc.common.adapter;

import javafx.scene.control.ListCell;

import org.eclipse.jubula.ext.rc.javafx.tester.adapter.CustomContainerAdapter;
import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.javafx.tester.adapter.IContainerAdapter;


/**
 * Adapter Factory for new adapters. This class makes your new adapters usable
 * for testing. One factory could be used for all adapters implemented.
 * 
 * @author BREDEX GmbH
 */
public class JavaFXContainerAdapterFactory implements IAdapterFactory {
    /** {@inheritDoc} */
    public Class<?>[] getSupportedClasses() {
        return new Class[] { ListCell.class };
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class targetedClass, Object objectToAdapt) {
        if (targetedClass.isAssignableFrom(IContainerAdapter.class)) {
            if (objectToAdapt instanceof ListCell) {
                return new CustomContainerAdapter((ListCell<?>) objectToAdapt);
            }
        }
        return null;
    }
}
