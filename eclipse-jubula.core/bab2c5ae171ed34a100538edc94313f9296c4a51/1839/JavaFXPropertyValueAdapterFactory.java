/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.adapter;

import javafx.scene.layout.Background;
import javafx.scene.layout.Border;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.adaptable.IPropertyValue;
import org.eclipse.jubula.rc.javafx.adapter.BackgroundPropertyValueAdapter;
import org.eclipse.jubula.rc.javafx.adapter.BorderPropertyValueAdapter;

/**
 * This is the adapter factory for all JavaFX components non-primitive property
 * values
 * 
 * @author BREDEX GmbH
 */
public class JavaFXPropertyValueAdapterFactory implements IAdapterFactory {
    /**
     * the supported classes
     */
    private static final Class[] SUPPORTED_CLASSES = new Class[] { 
        Background.class, Border.class };

    @Override
    public Class[] getSupportedClasses() {
        return SUPPORTED_CLASSES;
    }

    @Override
    public Object getAdapter(Class targetAdapterClass, Object objectToAdapt) {
        IPropertyValue returnvalue = null;
        if (targetAdapterClass.isAssignableFrom(IPropertyValue.class)) {
            if (objectToAdapt instanceof Background) {
                returnvalue = new BackgroundPropertyValueAdapter();
            } else if (objectToAdapt instanceof Border) {
                returnvalue = new BorderPropertyValueAdapter();
            }
        }
        return returnvalue;
    }
}
