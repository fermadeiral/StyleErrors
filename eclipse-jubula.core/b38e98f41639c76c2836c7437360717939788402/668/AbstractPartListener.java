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
package org.eclipse.jubula.client.ui.rcp.controllers;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

/**
 * @author BREDEX GmbH
 * @created 20.09.2006
 */
public abstract class AbstractPartListener implements IPartListener {
    /**
     * {@inheritDoc}
     */
    public void partActivated(IWorkbenchPart part) {
        // nothing 
    }
    /**
     * {@inheritDoc}
     */
    public void partBroughtToTop(IWorkbenchPart part) {
        // nothing 
    }
    /**
     * {@inheritDoc}
     */
    public void partClosed(IWorkbenchPart part) {
        // nothing 
    }
    /**
     * {@inheritDoc}
     */
    public void partDeactivated(IWorkbenchPart part) {
        // nothing 
    }
    /**
     * {@inheritDoc}
     */
    public void partOpened(IWorkbenchPart part) {
        // nothing 
    }
}