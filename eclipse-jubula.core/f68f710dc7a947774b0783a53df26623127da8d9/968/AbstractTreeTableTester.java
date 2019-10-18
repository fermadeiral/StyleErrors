/*******************************************************************************
 * Copyright (c) 2012 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.common.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.awt.Rectangle;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeTableOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.PathBasedTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.SelectTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperationConstraint;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeTableComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

/**
 * General implementation for TreeTables.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractTreeTableTester extends AbstractTreeTester {

    /**
     * Checks if a given column exists, respectively does not exist
     * 
     * @param column
     *            the column
     * @param columnOperator
     *            the operator to find the column
     * @param exists
     *            true when the column should be found         
     * @param timeout the maximum amount of time to wait for the check
     */
    public void rcCheckExistenceOfColumn(final String column,
            final String columnOperator, final boolean exists, int timeout) {
        invokeAndWait("rcCheckExistenceOfColumn", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        checkExistenceOfColumn(column, columnOperator, exists);
                    }
                });

    }

    /**
     * Selects the item at the end of the <code>treepath</code> at column
     * <code>column</code>.
     * 
     * @param pathType
     *            whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes above
     *            the current node. Absolute traversals ignore this parameter.
     * @param treePath
     *            The tree path.
     * @param operator
     *            If regular expressions are used to match the tree path
     * @param clickCount
     *            the click count
     * @param column
     *            the column of the item to select
     * @param button
     *            what mouse button should be used
     * @throws StepExecutionException
     *             If the tree path is invalid, if the double-click to expand
     *             the node fails, or if the selection is invalid.
     */
    public void rcSelect(String pathType, int preAscend, String treePath,
            String operator, int clickCount, int column, int button)
            throws StepExecutionException {
        final int implCol = IndexConverter.toImplementationIndex(column);
        checkColumnIndex(implCol);
        selectByPath(pathType, preAscend,
                createStringNodePath(splitTextTreePath(treePath), operator),
                ClickOptions.create().setClickCount(clickCount)
                        .setMouseButton(button),
                implCol);

    }

    /**
     * Selects the last node of the path given by <code>indexPath</code> at
     * column <code>column</code>.
     * 
     * @param pathType
     *            whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes above
     *            the current node. Absolute traversals ignore this parameter.
     * @param indexPath
     *            the index path
     * @param clickCount
     *            the number of times to click
     * @param column
     *            the column of the item to select
     * @param button
     *            what mouse button should be used
     * @throws StepExecutionException
     *             if <code>indexPath</code> is not a valid path
     */
    public void rcSelectByIndices(String pathType, int preAscend,
            String indexPath, int clickCount, int column, int button)
            throws StepExecutionException {
        final int implCol = IndexConverter.toImplementationIndex(column);
        checkColumnIndex(implCol);
        selectByPath(pathType, preAscend,
                createIndexNodePath(splitIndexTreePath(indexPath)),
                ClickOptions.create().setClickCount(clickCount)
                        .setMouseButton(button),
                implCol);
    }

    /**
     * Verifies whether the first selection in the tree has a rendered text at
     * column <code>column</code> that is equal to <code>pattern</code>.
     * 
     * @param pattern
     *            The pattern
     * @param column
     *            The column containing the text to verify
     * @throws StepExecutionException
     *             If no node is selected or the verification fails.
     * @param timeout
     *            the maximum amount of time to wait for the check
     */
    public void rcVerifySelectedValue(final String pattern, final int column,
            int timeout) throws StepExecutionException {
        invokeAndWait("rcVerifySelectedValue", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        rcVerifySelectedValue(pattern,
                                MatchUtil.DEFAULT_OPERATOR, column);
                    }
                });
    }

    /**
     * Verifies if the selected node underneath <code>treePath</code> at column
     * <code>column</code> has a rendered text which is equal to
     * <code>selection</code>.
     * 
     * @param pattern
     *            the pattern
     * @param operator
     *            The operator to use when comparing the expected and actual
     *            values.
     * @param column
     *            The column containing the text to verify
     * @param timeout
     *            the maximum amount of time to wait for the check
     * @throws StepExecutionException
     *             If there is no tree node selected, the tree path contains no
     *             selection or the verification fails
     */
    public void rcVerifySelectedValue(final String pattern,
            final String operator, final int column, int timeout)
            throws StepExecutionException {
        invokeAndWait("rcVerifySelectedValue", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        final int implCol = IndexConverter
                                .toImplementationIndex(column);
                        checkColumnIndex(implCol);
                        Verifier.match(getRenderedTextOfColumn(implCol),
                                pattern, operator);
                    }
                });
    }

    /**
     * 
     * @param index
     *            The 0-based column index to check.
     * @throws StepExecutionException
     *             if the column index is invalid.
     */
    protected void checkColumnIndex(final int index)
            throws StepExecutionException {
        final AbstractTreeTableOperationContext context =
                getTreeTableAdapter().getContext(0);
        int numColumns =
                getEventThreadQueuer().invokeAndWait("checkColumnIndex", //$NON-NLS-1$
                        new IRunnable<Integer>() {
                            public Integer run() {
                                return new Integer(
                                        context.getNumberOfColumns());
                            }
                        });
        if (index < 0 || index >= numColumns) {
            throw new StepExecutionException(
                    "Invalid column: " //$NON-NLS-1$
                            + IndexConverter.toUserIndex(index),
                    EventFactory
                            .createActionError(TestErrorEvent.INVALID_INDEX));
        }
    }

    /**
     * 
     * @param column
     *            column
     * @param columnOperator
     *            columnOperator
     * @param exists
     *            exists
     */
    protected void checkExistenceOfColumn(String column, String columnOperator,
            boolean exists) {
        AbstractTreeTableOperationContext context =
                getTreeTableAdapter().getContext(0);
        int index = -2;
        try {
            index = context.getColumnFromString(column, columnOperator, true);
        } catch (StepExecutionException see) {
            // If a column can not be found, an exception is thrown. Because
            // this is a valid outcome for this method in this context, we
            // catch the exception.
            if (exists) {
                throw see;
            }
        }
        if (index >= 0) {
            Rectangle bounds = context.getHeaderBounds(index);
            if (bounds == null || bounds.getWidth() <= 0) {
                index = -2;
            }
        }
        Verifier.equals(exists, index >= 0);
    }

    /**
     * 
     * @param column
     *            column
     * @return return
     * @throws StepExecutionException
     */
    protected String getRenderedTextOfColumn(int column)
            throws StepExecutionException {
        AbstractTreeTableOperationContext context =
                getTreeTableAdapter().getContext(column);
        return context.getRenderedTextOfColumn(context.getSelectedNode());
    }

    /**
     * This method is only casting the IComponentAdapter to the wanted
     * ITreeTableAdapter
     * 
     * @return The ITreeTableAdapter out of the stored IComponentAdapter
     */
    protected ITreeTableComponent getTreeTableAdapter() {
        return (ITreeTableComponent) getComponent();
    }

    /**
     * @param pathType
     *            pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes above
     *            the current node. Absolute traversals ignore this parameter.
     * @param objectPath
     *            objectPath
     * @param co
     *            the click options to use
     * @param column
     *            the column
     */
    protected void selectByPath(String pathType, int preAscend,
            INodePath objectPath, ClickOptions co, int column) {
        TreeNodeOperation expOp = new ExpandCollapseTreeNodeOperation(false);
        TreeNodeOperation selectOp = new SelectTreeNodeOperation(co);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);
        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(objectPath, pathType, preAscend, selectOp,
                column);
    }

    /**
     * Traverses the tree by searching for the nodes in the tree path entry and
     * calling the given operation on the last element in the path.
     * 
     * @param treePath
     *            The tree path.
     * @param pathType
     *            For example, "relative" or "absolute".
     * @param preAscend
     *            Relative traversals will start this many parent nodes above
     *            the current node. Absolute traversals ignore this parameter.
     * @param operation
     *            The tree node operation.
     * @param column
     *            The target column for the operation.
     * @throws StepExecutionException
     *             If the path traversion fails.
     */
    protected void traverseLastElementByPath(INodePath treePath,
            String pathType, int preAscend, TreeNodeOperation operation,
            int column) throws StepExecutionException {
        Validate.notNull(treePath);
        Validate.notNull(operation);
        AbstractTreeTableOperationContext context =
                getTreeTableAdapter().getContext(column);
        AbstractTreeNodeTraverser traverser = new PathBasedTraverser(context,
                treePath, new TreeNodeOperationConstraint());
        traverser.traversePath(operation,
                getStartNode(pathType, preAscend, context));
    }

    /*
     * Methods from treeTableView (JavaFX)
     */

    /**
     * Selects the last node of the path given by <code>indexPath</code> at the
     * column given by the column path
     * 
     * @param pathType
     *            whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes above
     *            the current node. Absolute traversals ignore this parameter.
     * @param indexPath
     *            the index path
     * @param clickCount
     *            the number of times to click
     * @param column
     *            the column or column path of the item to select
     * @param operator
     *            for the column path
     * @param button
     *            what mouse button should be used
     * @throws StepExecutionException
     *             if <code>indexPath</code> is not a valid path
     */
    public void rcSelectByColumnPath(String pathType, int preAscend,
            String indexPath, int clickCount, String column, String operator,
            int button) throws StepExecutionException {
        final int implCol = getTreeTableAdapter().getContext(0)
                .getColumnFromString(column, operator, true);
        checkColumnIndex(implCol);
        selectByPath(pathType, preAscend,
                createIndexNodePath(splitIndexTreePath(indexPath)),
                ClickOptions.create().setClickCount(clickCount)
                        .setMouseButton(button),
                implCol);
    }

    /**
     * Selects the item at the end of the <code>treepath</code> at column
     * <code>column</code>.
     * 
     * @param pathType
     *            whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes above
     *            the current node. Absolute traversals ignore this parameter.
     * @param treePath
     *            The tree path.
     * @param operator
     *            If regular expressions are used to match the tree path
     * @param clickCount
     *            the click count
     * @param column
     *            the column or column path of the item to select
     * @param colOperator
     *            the operator for the column path
     * @param button
     *            what mouse button should be used
     * @throws StepExecutionException
     *             If the tree path is invalid, if the double-click to expand
     *             the node fails, or if the selection is invalid.
     */
    public void rcSelectColumnPath(String pathType, int preAscend,
            String treePath, String operator, int clickCount, String column,
            String colOperator, int button) throws StepExecutionException {
        final int implCol = getTreeTableAdapter().getContext(0)
                .getColumnFromString(column, colOperator, true);
        checkColumnIndex(implCol);
        selectByPath(pathType, preAscend,
                createStringNodePath(splitTextTreePath(treePath), operator),
                ClickOptions.create().setClickCount(clickCount)
                        .setMouseButton(button),
                implCol);
    }

    /**
     * Verifies if the selected node underneath <code>treePath</code> at column
     * <code>column</code> has a rendered text which is equal to
     * <code>selection</code>.
     * 
     * @param pattern
     *            the pattern
     * @param operator
     *            The operator to use when comparing the expected and actual
     *            values.
     * @param column
     *            the column or column path of the item to select
     * @param colOperator
     *            The operator to use when comparing the
     *            column or column path of the item to select
     * @param timeout
     *            the maximum amount of time to wait for the check
     * @throws StepExecutionException
     *             If there is no tree node selected, the tree path contains no
     *             selection or the verification fails
     */
    public void rcVerifySelectedValueAtPath(final String pattern,
            final String operator, final String column,
            final String colOperator, int timeout)
            throws StepExecutionException {
        invokeAndWait("rcVerifySelectedValueAtPath", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        AbstractTreeTableOperationContext context = 
                                getTreeTableAdapter()
                                .getContext(0);
                        final int implCol = context.getColumnFromString(column,
                                colOperator, true);
                        checkColumnIndex(implCol);
                        context.setColumn(implCol);
                        String text = context.getRenderedTextOfColumn(
                                context.getSelectedNode());
                        Verifier.match(text, pattern, operator);
                    }
                });
    }

}
