/*******************************************************************************
 * Copyright (c) 2004, 2010 BREDEX GmbH.
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

/**
 * @author BREDEX GmbH
 * @created 26.04.2006
 */
public final class SwingComponent extends AUTComponent<Component> {
    /**
     * create an instance from a Swing component. This constructor is used when
     * working with real instances instead of mere class descriptions.
     * 
     * @param component
     *            Base for identification
     * 
     */
    public SwingComponent(Component component) {
        super(component);
    }
}