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
package org.eclipse.jubula.rc.rcp.e3.accessor;

import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jubula.rc.rcp.swt.aut.RcpSwtComponentNamer;
import org.eclipse.ui.PlatformUI;

/**
 * This listener assigns names to components as they become visible. The
 * assigned name is determined by supporting data of the component and its
 * surroundings.
 *
 * @author BREDEX GmbH
 * @created Oct 19, 2007
 */
public class E3ComponentNamer extends RcpSwtComponentNamer {

    /**
     * {@inheritDoc}
     */
    protected PreferenceManager getPreferenceManager() {
        return PlatformUI.getWorkbench()
                .getPreferenceManager();
    }
}
