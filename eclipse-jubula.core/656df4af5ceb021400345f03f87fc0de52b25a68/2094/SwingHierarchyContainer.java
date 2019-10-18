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
package org.eclipse.jubula.rc.swing.components;

import java.awt.Component;

import org.eclipse.jubula.rc.common.components.AUTComponent;
import org.eclipse.jubula.rc.common.components.HierarchyContainer;

/**
 * @author BREDEX GmbH
 */
public class SwingHierarchyContainer extends HierarchyContainer<Component> {

    /**
     * @param component
     *            the component
     * @param parent
     *            the parent
     */
    public SwingHierarchyContainer(AUTComponent<Component> component,
            HierarchyContainer<Component> parent) {
        super(component, parent);
    }

    /**
     * @param component
     *            the component
     */
    public SwingHierarchyContainer(AUTComponent<Component> component) {
        super(component);
    }
}