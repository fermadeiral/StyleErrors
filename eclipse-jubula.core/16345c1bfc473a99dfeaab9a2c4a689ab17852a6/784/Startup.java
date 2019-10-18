/*******************************************************************************
 * Copyright (c) 2004, 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.rcp.e3.specific;

import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.internal.WorkbenchPartReference;

import org.eclipse.jubula.rc.rcp.e3.accessor.E3Startup;

/**
 * Inherits everything from the parent class.
 *
 * @author BREDEX GmbH
 * @created Dec 17, 2012
 */
public class Startup extends E3Startup {
    /**
     * Gets the control of the tool bar from the pane of the workbench
     * part reference in Eclipse 3.x.
     * @param workbenchPartRef The workbench part reference.
     * @return The control of the tool bar from the given workbench part reference
     *         or null, if it does not exist.
     */
    public Control getToolBarFromWorkbenchPartRef(
            WorkbenchPartReference workbenchPartRef) {
        return workbenchPartRef.getPane().getToolBar();
    }
}
