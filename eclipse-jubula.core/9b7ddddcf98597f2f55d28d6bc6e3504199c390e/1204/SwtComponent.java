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
package org.eclipse.jubula.rc.swt.components;

import org.eclipse.jubula.rc.common.components.AUTComponent;
import org.eclipse.swt.widgets.Widget;


/**
 * @author BREDEX GmbH
 * @created 26.04.2006
 */
public final class SwtComponent extends AUTComponent<Widget> {
    /**
     * create an instance from a SWT component. This constructor is used when
     * working with real instances instead of mere class descriptions.
     * 
     * @param component
     *            Base for identification
     * 
     */
    public SwtComponent(Widget component) {
        super(component);
    }
}