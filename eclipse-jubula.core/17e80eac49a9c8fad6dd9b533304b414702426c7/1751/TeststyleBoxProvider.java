/*******************************************************************************
 * Copyright (c) 2004, 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.teststyle.properties.provider;

import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jubula.client.teststyle.properties.nodes.INode;
import org.eclipse.jubula.client.teststyle.properties.nodes.INode.TreeState;


/**
 * @author marcell
 * @created Oct 21, 2010
 */
public class TeststyleBoxProvider implements ICheckStateProvider {

    /**
     * {@inheritDoc}
     */
    public boolean isChecked(Object element) {
        INode node = (INode)element;
        return !node.getState().equals(TreeState.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isGrayed(Object element) {
        INode node = (INode)element;
        return node.getState().equals(TreeState.GRAYED);
    }

}
