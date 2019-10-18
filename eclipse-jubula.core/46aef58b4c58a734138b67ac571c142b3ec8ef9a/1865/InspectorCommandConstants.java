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
package org.eclipse.jubula.client.inspector.ui.constants;

/**
 * Contains constants necessary for interacting with commands.
 *
 * @author BREDEX GmbH
 * @created Mar 23, 2010
 */
public final class InspectorCommandConstants {

    /** id for "Activate Inspector" command */
    public static final String ACTIVATE_INSPECTOR_COMMAND_ID = 
        "org.eclipse.jubula.client.inspector.ui.commands.activateInspector"; //$NON-NLS-1$
    
    /**
     * Private constructor to prevent instantiation.
     */
    private InspectorCommandConstants() {
        // Nothing to initialize
    }

}
