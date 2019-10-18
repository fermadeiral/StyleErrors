/*******************************************************************************
 * Copyright (c) 2004, 2011 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.launch.ui.actions;

import org.eclipse.debug.ui.actions.AbstractLaunchToolbarAction;

/**
 * Toolbar action for starting Java / Swing AUTs.
 * 
 * @author BREDEX GmbH
 * @created 13.07.2011
 */
public class LaunchAutToolbarAction extends AbstractLaunchToolbarAction {

    /** ID of the "Start AUT" launch group */
    private static final String AUT_LAUNCH_GROUP_ID =
        "org.eclipse.jubula.launch.ui.launchGroup.aut"; //$NON-NLS-1$
    
    /**
     * Constructor
     */
    public LaunchAutToolbarAction() {
        super(AUT_LAUNCH_GROUP_ID);
    }
    
}
