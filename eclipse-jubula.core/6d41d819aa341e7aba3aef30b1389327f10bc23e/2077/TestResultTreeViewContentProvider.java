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
package org.eclipse.jubula.client.ui.provider.contentprovider;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jubula.client.core.model.TestResult;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 18.10.2004
 *
 */
public class TestResultTreeViewContentProvider implements ITreeContentProvider {

    /** the logger */
    private static final Logger LOG = 
        LoggerFactory.getLogger(TestResultTreeViewContentProvider.class);
    
    /**
     * {@inheritDoc}
     */
    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof TestResultNode) {
            TestResultNode result = (TestResultNode)parentElement;
            return result.getResultNodeList().toArray();
        }

        if (parentElement instanceof TestResult) {
            return new Object[] {
                    ((TestResult)parentElement).getRootResultNode()};
        }

        if (parentElement instanceof TestResultNode[]) {
            // Workaround for Eclipse bug 9262
            TestResultNode[] inputArray = (TestResultNode[])parentElement;
            Object[] elements = new Object[inputArray.length];
            System.arraycopy(inputArray, 0, elements, 0, inputArray.length);
            return elements;
        }
        
        LOG.warn(Messages.ParentElementHasInvalidTypeReturningEmptyArray);
        return new Object[0];
    }

    /**
     * {@inheritDoc}
     */
    public Object getParent(Object element) {
        if (element instanceof TestResultNode) {
            return ((TestResultNode)element).getParent();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean hasChildren(Object element) {
        return getChildren(element).length > 0;
    }

    /**
     * {@inheritDoc}
     */
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof TestResult) {
            return new Object[] {
                    ((TestResult)inputElement).getRootResultNode()};
        }

        if (inputElement instanceof TestResultNode[]) {
            // Workaround for Eclipse bug 9262
            TestResultNode[] inputArray = (TestResultNode[])inputElement;
            Object[] elements = new Object[inputArray.length];
            System.arraycopy(inputArray, 0, elements, 0, inputArray.length);
            return elements;
        }
        
        LOG.warn(Messages.InputElementHasInvalidTypeReturningEmptyArray);
        return new Object[0];
    }

    /**
     * {@inheritDoc}
     */
    public void dispose() {
        // nothing to dispose
    }

    /**
     * {@inheritDoc}
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // nothing to update
    }
}