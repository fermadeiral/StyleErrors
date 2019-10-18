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
package org.eclipse.jubula.client.ui.handlers;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jubula.client.core.model.TestResultNode;
import org.eclipse.jubula.client.ui.utils.TreeViewerIterator;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;


/**
 * Abstract handler for navigating to a test result node.
 *
 * @author BREDEX GmbH
 * @created Jun 3, 2010
 */
public abstract class AbstractGoToTestResultErrorHandler 
        extends AbstractHandler {

    /**
     * {@inheritDoc}
     */
    public final Object execute(ExecutionEvent event) 
        throws ExecutionException {

        List<IWorkbenchPart> listOfPossibleParts =
                new LinkedList<IWorkbenchPart>();
        listOfPossibleParts.add(HandlerUtil.getActivePart(event));
        listOfPossibleParts.add(HandlerUtil.getActiveEditor(event));
        TreeViewer viewer = handleActiveWorkbenchParts(listOfPossibleParts);
        
        IStructuredSelection selection = 
            (IStructuredSelection)viewer.getSelection();
        ITreeContentProvider contentProvider = 
            (ITreeContentProvider)viewer.getContentProvider();
        TestResultNode startingNode = null;
        if (selection.getFirstElement() instanceof TestResultNode) {
            startingNode = (TestResultNode)selection.getFirstElement();
        } else {
            Object[] rootElements = 
                contentProvider.getElements(viewer.getInput());
            for (Object element : rootElements) {
                if (element instanceof TestResultNode) {
                    startingNode = (TestResultNode)element;
                    break;
                }
            }
        }
       
        TestResultNode targetNode = null;
        TreeViewerIterator iter = new TreeViewerIterator(viewer, startingNode,
            isForwardIteration());
        while (iter.hasNext() && targetNode == null) {
            Object nextElement = iter.next();
            if (nextElement instanceof TestResultNode) {
                TestResultNode node = (TestResultNode) nextElement;
                if (isErrorNode(node)) {
                    targetNode = node;
                }
            }
        }
        
        if (targetNode != null) {
            viewer.reveal(targetNode);
            viewer.setSelection(new StructuredSelection(targetNode));
        }

        return null;
    }

    /**
     * 
     * @param node The node to check.
     * @return <code>true</code> if the node is considered an error node.
     */
    private final boolean isErrorNode(TestResultNode node) {
        int status = node.getStatus();
        List<TestResultNode> resultNodeList = node.getResultNodeList();
        if (resultNodeList != null) {
            return (status == TestResultNode.ERROR)
                    || (status == TestResultNode.ABORT) 
                    || status == TestResultNode.CONDITION_FAILED
                    || status == TestResultNode.INFINITE_LOOP
                    || ((status == TestResultNode.TESTING)
                            && (node.getResultNodeList().size() == 0));
        }
        return (status == TestResultNode.ERROR)
                || (status == TestResultNode.ABORT);
    }
    
    /**
     * @return a boolean describing the direction of iteration:
     * true = forwards; false = backwards
     */
    protected abstract boolean isForwardIteration();
    
    /**
     * @param listOfPossibleParts a list of two possible parts to execute the GoTo on
     * @return the TreeViewer of the active part
     */
    protected abstract TreeViewer handleActiveWorkbenchParts(
            List<IWorkbenchPart> listOfPossibleParts);  
}