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

import java.util.Arrays;

import javax.persistence.EntityManager;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.teststyle.properties.nodes.CategoryNode;
import org.eclipse.jubula.client.teststyle.properties.nodes.INode;


/**
 * @author marcell
 * @created Oct 21, 2010
 */
public class TeststyleContentProvider implements ITreeContentProvider {

    /** all nodes which are used for this provider */
    private INode[] m_nodes;

    /**
     *  calls save for every node in the provider 
     * @param s The entity manager which persists it.
     */
    public void save(EntityManager s) {
        for (INode node : m_nodes) {
            node.save(s);
        }
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        INode node = (INode)parentElement;
        INode[] tmp = node.getChildren();
        Arrays.sort(tmp);
        return tmp;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        return m_nodes;
    }

    /**
     * {@inheritDoc}
     */
    public Object getParent(Object element) {
        INode node = (INode)element;
        return node.getParent();
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        return element instanceof CategoryNode;
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
    // Does nothing
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.m_nodes = (INode[])newInput;
    }

}
