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

import org.eclipse.jubula.rc.common.components.HierarchyContainer;
import org.eclipse.swt.widgets.Widget;

/**
 * @author BREDEX GmbH
 * @created 04.05.2006
 */
public class SwtHierarchyContainer extends HierarchyContainer<Widget> {
    /**
     * @param component
     *            the SwtComponentIdentifier
     * @param parent
     *            the SwtHierarchyContainer
     */
    public SwtHierarchyContainer(SwtComponent component,
            HierarchyContainer<Widget> parent) {
        super(component, parent);
    }

    /**
     * @param component
     *            the SwtComponentIdentifier
     */
    public SwtHierarchyContainer(SwtComponent component) {
        this(component, null);
    }
}