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

import javafx.scene.Node;
import javafx.scene.chart.PieChart;

import org.eclipse.jubula.rc.common.adaptable.IAdapterFactory;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.javafx.tester.adapter.JavaFXComponentAdapter;


/**
 * Adapter Factory for new adapters. This class makes your new adapters usable
 * for testing. One factory could be used for all adapters implemented.
 * 
 * @author BREDEX GmbH
 */
public class JavaFXExtensionExampleAdapterFactory implements IAdapterFactory {
    /** {@inheritDoc} */
    public Class[] getSupportedClasses() {
        return new Class[] { PieChart.class };
    }

    /** {@inheritDoc} */
    public Object getAdapter(Class targetedClass, Object objectToAdapt) {
        if (targetedClass.isAssignableFrom(IComponent.class)) {
            if (objectToAdapt instanceof PieChart) {
                return new JavaFXComponentAdapter<Node>((Node) objectToAdapt);
            }
        }
        return null;
    }
}
