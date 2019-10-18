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
package org.eclipse.jubula.client.ui.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * Iterator over the elements contained in a tree viewer.
 *
 * @author BREDEX GmbH
 * @created Jun 2, 2010
 */
public class TreeViewerIterator {

    /** Linear List of tree elements */
    private List<Object> m_elements = 
        new ArrayList<Object>(25);
    /** Iterator to iterate over the element list */
    private Iterator<Object> m_iter;
    /** tree viewer */
    private TreeViewer m_viewer;

    /**
     * Constructor
     * 
     * @param viewer The viewer containing the contents to iterate over.
     *               Note that the contents of the iterator are queried at 
     *               construction time, so changes to the contents after 
     *               creation of this iterator do not effect the 
     *               results/contents of this iterator.
     */
    public TreeViewerIterator(TreeViewer viewer) {
        this(viewer, null, true);
    }

    /**
     * Constructor
     * 
     * @param viewer The viewer containing the contents to iterate over.
     *               Note that the contents of the iterator are queried at 
     *               construction time, so changes to the contents after 
     *               creation of this iterator do not effect the 
     *               results/contents of this iterator.
     * @param startNode The node <b>after</b> which to start iteration. If 
     *                  <code>null</code> or not contained within the viewer, 
     *                  iteration will start from the root node.
     * @param iterateForward Flag indicating whether iteration should be 
     *                       forward, rather than backward.
     */
    public TreeViewerIterator(TreeViewer viewer, Object startNode, 
            boolean iterateForward) {
        m_viewer = viewer;
        // We explicitly do *not* want to add the viewer input to the list of 
        // elements to search, as it will definitely not be displayed. See the 
        // "NOTE" in the javadoc for 
        // IStructuredContentProvider.getElements(Object), which should refer 
        // to Eclipse bug 9262.
        Object[] elements = ((ITreeContentProvider)m_viewer
                .getContentProvider()).getElements(viewer.getInput());
        for (Object element : elements) {
            setElements(element);
        }
        if (startNode != null && m_elements.contains(startNode)) {
            Collections.rotate(m_elements, 
                    m_elements.size() - m_elements.indexOf(startNode));
            
            // We don't want to include the starting node in our iteration
            m_elements.remove(0);
        }
        if (!iterateForward) {
            Collections.reverse(m_elements);
        }
        m_iter = m_elements.iterator();
    }
    
    /**
     * @return The next element in the iteration.
     */
    public Object next() {
        return m_iter.next();
    }

    /**
     * Returns <tt>true</tt> if the iteration has more elements.
     * @return <tt>true</tt> if the iterator has more elements.
     */
    public boolean hasNext() {
        return m_iter.hasNext();
    }
    
    /**
     * Creates the linear list of tree elements, including the root.
     * 
     * @param root The root of the tree or sub-tree.
     */
    private void setElements(Object root) {
        if (root == null) {
            return;
        }
        Object[] children = ((ITreeContentProvider)m_viewer
                .getContentProvider()).getChildren(root);
        // Filter the same elements as in GUI
        for (ViewerFilter vf : m_viewer.getFilters()) {
            children = vf.filter(m_viewer, root, children);
        }
        // Sort elements like sorted in GUI to obtain correct traversal order
        ViewerComparator sorter = m_viewer.getSorter();
        if (sorter != null) {
            sorter.sort(m_viewer, children);
        }
        for (Object node : children) {
            if (node != null) {
                m_elements.add(node);
            }
            setElements(node);
        }
    }

    /** returns a flatten List of all elements 
     * @return  List
     * */
    public List<Object> getElements() {
        return m_elements;
    }
}