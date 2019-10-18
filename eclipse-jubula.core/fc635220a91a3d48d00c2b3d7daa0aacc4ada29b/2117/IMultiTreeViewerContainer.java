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
package org.eclipse.jubula.client.ui.views;

import org.eclipse.jface.viewers.TreeViewer;

/**
 * Interface that allows tree viewer containers to support multiple tree 
 * viewers, with at most one tree viewer active at any given time.
 *
 * @author BREDEX GmbH
 * @created Sep 20, 2010
 */
public interface IMultiTreeViewerContainer extends ITreeViewerContainer {

    /**
     * 
     * @return the active tree viewer, or <code>null</code> if no tree viewer
     *         is currently active.
     */
    public TreeViewer getActiveTreeViewer();
    
    /**
     * 
     * @return all tree viewers associated with this container. 
     *         Implementors must never return <code>null</code>.
     */
    public TreeViewer[] getTreeViewers();
}
