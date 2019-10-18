/*******************************************************************************
 * Copyright (c) 2017 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.handlers;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.jubula.client.ui.handlers.AbstractHandler;
import org.eclipse.jubula.client.ui.rcp.businessprocess.JBNavigationHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Handler for the Back- and Forward actions
 * @author BREDEX GmbH
 *
 */
public class NavigateHandler extends AbstractHandler {
    /** Navigation parameter id */
    public static final String NAVPARAM_ID = "org.eclipse.jubula.client.ui.rcp.commands.navigation.Navigate.parameter.NavigateItem"; //$NON-NLS-1$

    /** Jump to prefix of parameter */
    public static final String JUMP = "jumpTo:"; //$NON-NLS-1$

    /** Edited prefix, used as a static prefix in plugin.xml */
    public static final String EDITED = "Edited,"; //$NON-NLS-1$

    /** The logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            NavigateHandler.class);

    /** {@inheritDoc} */
    protected Object executeImpl(ExecutionEvent event) {
        String par = event.getParameter(NAVPARAM_ID);
        if (par == null) {
            if (StringUtils.endsWith(event.getCommand().getId(), "GoBack")) { //$NON-NLS-1$
                par = "-1"; //$NON-NLS-1$
            } else {
                par = "1"; //$NON-NLS-1$
            }
        }
        try {
            if (StringUtils.startsWith(par, JUMP)) {
                int pos = Integer.parseInt(par.substring(JUMP.length()));
                JBNavigationHistory.getInstance().move(pos, true);
            } else if (par != null) {
                if (par.startsWith(EDITED)) {
                    int direction = Integer.parseInt(
                            par.substring(EDITED.length()));
                    JBNavigationHistory.getInstance().editedMove(direction);
                } else {
                    int direction = Integer.parseInt(par);
                    JBNavigationHistory.getInstance().move(direction, false);
                }
            }
        } catch (NumberFormatException e) {
            LOG.error("Failed parsing " + par); //$NON-NLS-1$
        }
        return null;
    }
}
