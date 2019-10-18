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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.exception.StepVerifyFailedException;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeNodeTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeOperationContext;
import org.eclipse.jubula.rc.common.implclasses.tree.ChildTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.ExpandCollapseTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.INodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.IndexNodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.ParentTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.PathBasedTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.SelectTreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.SiblingTraverser;
import org.eclipse.jubula.rc.common.implclasses.tree.StringNodePath;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperation;
import org.eclipse.jubula.rc.common.implclasses.tree.TreeNodeOperationConstraint;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeComponent;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.StringParsing;


/**
 * General implementation for Trees.
 * 
 * @author BREDEX GmbH
 */
public abstract class AbstractTreeTester extends WidgetTester {

    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        return null;
    }

    /**
     *  This method is only casting the IComponentAdapter to the wanted ITreeAdapter
     * @return The ITreeAdapter out of the stored IComponentAdapter
     */
    protected ITreeComponent getTreeAdapter() {
        return (ITreeComponent)getComponent();
        
    }
    
    /**
     * Verifies the text of the Node at mousePosition
     * @param text the text to check
     * @param operator the operator for the verification
     * @param timeout the maximum amount of time to wait for the text to be
     *          verified at mouse position
     */
    public void rcVerifyTextAtMousePosition(final String text,
            final String operator, int timeout) {
        invokeAndWait("rcVerifyTextAtMousePosition", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        checkNodeText(new Object[] { getNodeAtMousePosition() },
                                text, operator);
                    }
                });
    }

    /**
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     *
     * @param treePath The tree path
     * @return An array of string representing the tree path
     */
    protected String[] splitTextTreePath(String treePath) {
        return StringParsing.splitToArray(treePath,
                TestDataConstants.PATH_CHAR_DEFAULT,
                TestDataConstants.ESCAPE_CHAR_DEFAULT, 
                true);
    }
    
    /**
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     *
     * @param treePath The tree path
     * @return An array of indices (type <code>Integer</code>) representing
     *         the tree path
     * @throws StepExecutionException
     *             If the values of the passed path cannot be parsed
     */
    protected Integer[] splitIndexTreePath(String treePath)
        throws StepExecutionException {
        Integer[] indexPath = null;
        String[] path = splitTextTreePath(treePath);
        if (path != null) {
            indexPath = new Integer[path.length];
            for (int i = 0; i < path.length; i++) {
                indexPath[i] = new Integer(IndexConverter.intValue(path[i]));
            }
        }
        return IndexConverter.toImplementationIndices(indexPath);
    }
    
    /**
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     * 
     * @param treePath The tree path
     * @param operator The operator
     * @return An array of string representing the tree path
     */
    protected INodePath createStringNodePath(String [] treePath, 
            String operator) {
        return new StringNodePath(treePath, operator);
    }
    
    /**
     * Splits the <code>treepath</code> string into an array, one entry for each level in the path
     *
     * @param treePath The tree path
     * @return An array of string representing the tree path
     */
    protected INodePath createIndexNodePath(Integer [] treePath) {
        return new IndexNodePath(treePath);
    }

    
    /**
     * Traverses the tree by searching for the nodes in the tree
     * path entry and calling the given operation on each matching node.
     *
     * @param treePath The tree path.
     * @param pathType For example, "relative" or "absolute".
     * @param preAscend Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param operation The tree node operation.
     * @throws StepExecutionException If the path traversion fails.
     */
    protected void traverseTreeByPath(INodePath treePath, String pathType,
            int preAscend, TreeNodeOperation operation)
        throws StepExecutionException {

        Validate.notNull(treePath);
        Validate.notNull(operation);

        ITreeComponent adapter = getTreeAdapter();
        AbstractTreeOperationContext context = adapter.getContext();

        AbstractTreeNodeTraverser traverser =
            new PathBasedTraverser(context, treePath);

        traverser.traversePath(operation, 
                getStartNode(pathType, preAscend, context));

    }
   
    /**
     * Traverses the tree by searching for the nodes in the tree
     * path entry and calling the given operation on the last element in the path.
     * @param treePath The tree path.
     * @param pathType For example, "relative" or "absolute".
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param operation The tree node operation.
     * @throws StepExecutionException If the path traversion fails.
     */
    protected void traverseLastElementByPath(INodePath treePath, 
            String pathType, int preAscend, TreeNodeOperation operation) 
        throws StepExecutionException {

        Validate.notNull(treePath);
        Validate.notNull(operation);

        AbstractTreeOperationContext context = getTreeAdapter().getContext();
        Object startNode = getStartNode(pathType, preAscend, context);

        AbstractTreeNodeTraverser traverser = new PathBasedTraverser(
                context, treePath, new TreeNodeOperationConstraint());
        
        traverser.traversePath(operation, startNode);
    }
    
    /**
     * @param pathType pathType
     * @param preAscend
     *            Relative traversals will start this many parent nodes 
     *            above the current node. Absolute traversals ignore this 
     *            parameter.
     * @param objectPath objectPath
     * @param co the click options to use
     */
    private void selectByPath(String pathType, int preAscend, 
            INodePath objectPath, ClickOptions co) {

        TreeNodeOperation expOp = new ExpandCollapseTreeNodeOperation(false);
        TreeNodeOperation selectOp = new SelectTreeNodeOperation(co);
        INodePath subPath = objectPath.subPath(0, objectPath.getLength() - 1);

        traverseTreeByPath(subPath, pathType, preAscend, expOp);
        traverseLastElementByPath(objectPath, pathType, preAscend, selectOp);
    }
    
    /**
     * Returns the selected node
     * @param context context
     * @return node
     */
    protected Object getSelectedNode(AbstractTreeOperationContext context) {
        return context.getSelectedNode();
    }
    
    /**
     * Clicks the tree.
     * If the mouse pointer is in the tree no mouse move will be performed.
     * Otherwise, the mouse is first moved to the center of the tree.
     *
     * @param count Number of mouse clicks
     * @param button Pressed button
     */
    public void rcClick(int count, int button) {
        if (getRobot().isMouseInComponent(
                getTreeAdapter().getRealComponent())) {
            getRobot().clickAtCurrentPosition(
                    getTreeAdapter().getRealComponent(), count, button);
        } else {
            getRobot().click(getTreeAdapter().getRealComponent(), null, 
                ClickOptions.create().setClickCount(count)
                    .setMouseButton(button));
        }       
    }
    
    /**
     * Collapses the JTree. The passed tree path is a slash-separated list of
     * nodes that specifies a valid top-down path in the JTree. The last node of
     * the tree path is collapsed if it is currently expanded. Otherwise, the JTree is
     * left unchanged.
     *
     * @param pathType whether the path is relative or absolute
     * @param preAscend Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param treePath The tree path.
     * @param operator
     *            Whether regular expressions are used to determine the tree path.
     *            <code>"matches"</code> for regex, <code>"equals"</code> for simple matching.
     * @throws StepExecutionException
     *             If the tree path is invalid or the double click to collapse
     *             the node fails.
     */
    public void rcCollapse(String pathType, int preAscend,
        String treePath, String operator) throws StepExecutionException {
        traverseLastElementByPath(
                createStringNodePath(splitTextTreePath(treePath), operator),
                pathType, preAscend,
                new ExpandCollapseTreeNodeOperation(true));
    }
    
    /**
     * Collapses the tree. This method works like {@link #rcCollapse(String, int, String, String)},
     * but expects an enumeration of indices representing the top-down tree
     * path. Any index is the node's position at the current tree level.
     *
     * @param pathType whether the path is relative or absolute
     * @param preAscend Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param indexPath  The index path
     * @throws StepExecutionException
     *             If the tree path is invalid or the double-click to collapse
     *             the node fails.
     */
    public void rcCollapseByIndices(String pathType, int preAscend,
        String indexPath) throws StepExecutionException {

        try {
            traverseLastElementByPath(
                    createIndexNodePath(splitIndexTreePath(indexPath)),
                    pathType, preAscend,
                    new ExpandCollapseTreeNodeOperation(true));
        } catch (NumberFormatException e) {
            throw new StepExecutionException(e.getMessage(), EventFactory
                    .createActionError(TestErrorEvent.INVALID_INDEX));
        }
    }
    
    /**
     * <p>
     * Expands the Tree. Any node defined by the passed tree path is expanded,
     * if it is collapsed. The node is expanded by performing a double click
     * onto the node. If the node is already expanded, the JTree is left
     * unchanged. The tree path is a slash-separated list of nodes that specifies
     * a valid top-down path in the JTree.
     * </p>
     *
     * An example: Say the passed tree path is <code>animals/birds/kakadu</code>.
     * To get a valid expansion, the JTree has to look as follows:
     *
     * <pre>
     * animals
     * |
     * - -- birds
     *      |
     *      - -- kakadu
     * </pre>
     *
     * <code>animals</code> is the Tree's root node, if the root node has
     * been set to visible,
     * or it is one of the root node's children, if the root node has been set
     * to invisible.
     *
     * <p>
     * It is important to know that the tree path entries have to match the
     * rendered node texts, but not the underlying user object data etc.
     * </p>
     *
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param treePath
     *            The tree path.
     * @param operator
     *            If regular expressions are used to determine the tree path
     * @throws StepExecutionException
     *             If the tree path is invalid or the double click fails.
     */
    public void rcExpand(String pathType, int preAscend,
        String treePath, String operator) throws StepExecutionException {
        traverseTreeByPath(
                createStringNodePath(splitTextTreePath(treePath), operator),
                pathType, preAscend,
                new ExpandCollapseTreeNodeOperation(false));
    }
    
    /**
     * Expands the tree. This method works like {@link #rcExpand(String, int, String, String)}, but
     * expects an enumeration of indices representing the top-down tree path.
     * Any index is the node's position at the current tree level.
     *
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param indexPath The index path
     * @throws StepExecutionException
     *             If the tree path is invalid or the double-click fails.
     */
    public void rcExpandByIndices(String pathType, int preAscend,
        String indexPath) throws StepExecutionException {

        try {
            traverseTreeByPath(
                    createIndexNodePath(splitIndexTreePath(indexPath)),
                    pathType, preAscend,
                    new ExpandCollapseTreeNodeOperation(false));
        } catch (NumberFormatException e) {
            throw new StepExecutionException(e.getMessage(), EventFactory
                    .createActionError(TestErrorEvent.INVALID_INDEX));
        }
    }   
    
    /**
     * Selects a node relative to the currently selected node.
     * @param direction the direction to move.
     *                  directions:
     *                      UP - Navigates through parents
     *                      DOWN - Navigates through children
     *                      NEXT - Navigates to next sibling
     *                      PREVIOUS - Navigates to previous sibling
     * @param distance the distance to move
     * @param clickCount the click count to select the new cell.
     * @throws StepExecutionException if any error occurs
     */
    public void rcMove(String direction, int distance, int clickCount) 
        throws StepExecutionException {
        
        AbstractTreeOperationContext context = getTreeAdapter().getContext();

        Object selectedNode = getSelectedNode(context);
        
        TreeNodeOperation selectOp = 
            new SelectTreeNodeOperation(
                    ClickOptions.create().setClickCount(clickCount));
        TreeNodeOperationConstraint constraint = 
            new TreeNodeOperationConstraint();

        if (ValueSets.TreeDirection.up.rcValue().equalsIgnoreCase(direction)) {
            AbstractTreeNodeTraverser traverser = 
                new ParentTraverser(context, distance, constraint);
            traverser.traversePath(selectOp, selectedNode);
        } else if (ValueSets.TreeDirection.down.rcValue()
                .equalsIgnoreCase(direction)) {
            TreeNodeOperation expandOp = 
                new ExpandCollapseTreeNodeOperation(false);
            AbstractTreeNodeTraverser expandTraverser = 
                new ChildTraverser(context, distance - 1);
            expandTraverser.traversePath(expandOp, selectedNode);

            AbstractTreeNodeTraverser selectTraverser = 
                new ChildTraverser(context, distance, constraint);
            selectTraverser.traversePath(selectOp, selectedNode);
            
        } else if (ValueSets.TreeDirection.next.rcValue()
                .equalsIgnoreCase(direction)) {
            // Look through siblings
            AbstractTreeNodeTraverser traverser = 
                new SiblingTraverser(context, distance, true, constraint);
            traverser.traversePath(selectOp, selectedNode);
            
        } else if (ValueSets.TreeDirection.previous.rcValue()
                .equalsIgnoreCase(direction)) {
            // Look through siblings
            AbstractTreeNodeTraverser traverser = 
                new SiblingTraverser(context, distance, false, constraint);
            traverser.traversePath(selectOp, selectedNode);
        }

    }
    
    /**
     * Selects the node at the end of the <code>treepath</code>.
     *
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param treePath The tree path.
     * @param operator If regular expressions are used to match the tree path
     * @param clickCount the click count
     * @param button what mouse button should be used
     * @param extendSelection Whether this selection extends a previous 
     *                        selection.
     * @throws StepExecutionException If the tree path is invalid, if the
     * double-click to expand the node fails, or if the selection is invalid.
     */
    public void rcSelect(String pathType, int preAscend, String treePath,
            String operator, int clickCount, int button, 
            final String extendSelection)
        throws StepExecutionException {
        selectByPath(pathType, preAscend,
                createStringNodePath(splitTextTreePath(treePath), operator),
                ClickOptions.create()
                    .setClickCount(clickCount)
                    .setMouseButton(button)
                    .setClickModifier(getClickModifier(extendSelection)));
    }
    /**
     * Selects the last node of the path given by <code>indexPath</code>
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param indexPath the index path
     * @param clickCount the number of times to click
     * @param button what mouse button should be used
     * @param extendSelection Whether this selection extends a previous 
     *                        selection.
     * @throws StepExecutionException if <code>indexPath</code> is not a valid
     * path
     */
    public void rcSelectByIndices(String pathType, int preAscend,
            String indexPath, int clickCount, int button,
            final String extendSelection) 
        throws StepExecutionException {
        
        selectByPath(pathType, preAscend,
                createIndexNodePath(splitIndexTreePath(indexPath)),
                ClickOptions.create()
                .setClickCount(clickCount)
                .setMouseButton(button)
                .setClickModifier(getClickModifier(extendSelection)));
    }
    
    /**
     * Tests whether the given treePath exists or not
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param treePath the path to check
     * @param operator the RegEx operator
     * @param exists if true, the verify succeeds if the path DOES exist.
     *  If false, the verify succeeds if the path DOES NOT exist.
     * @param timeout the maximum amount of time to wait for the path to be
     *          verified
     */
    public void rcVerifyPath(final String pathType, final int preAscend,
            final String treePath, final String operator, final boolean exists,
            int timeout) {
        invokeAndWait("rcVerifyPath", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        try {
                            rcExpand(pathType, preAscend, treePath, operator);
                        } catch (StepExecutionException e) {
                            if (exists) {
                                Verifier.equals(exists, false);
                            }
                            return;
                        }
                        if (!exists) {
                            Verifier.equals(exists, true);
                        }
                    }
                });
    }

    /**
     * Tests whether the given treePath exists or not
     * @param pathType whether the path is relative or absolute
     * @param preAscend
     *            Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param treePath the path to check
     * @param exists if true, the verify succeeds if the path DOES exist.
     *  If false, the verify succeeds if the path DOES NOT exist.
     * @param timeout the maximum amount of time for the path to be verified
     *          by indices
     */
    public void rcVerifyPathByIndices(final String pathType,
            final int preAscend, final String treePath, final boolean exists,
            int timeout) {
        invokeAndWait("rcVerifyIndices", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        try {
                            rcExpandByIndices(pathType, preAscend, treePath);
                        } catch (StepExecutionException e) {
                            if (exists) {
                                Verifier.equals(exists, false);
                            }
                            return;
                        }
                        if (!exists) {
                            Verifier.equals(exists, true);
                        }
                    }
                });
    }
    
    /**
     * {@inheritDoc}
     */
    public String rcStoreSelectedNodeValue(String variable) {
        AbstractTreeOperationContext context = getTreeAdapter().getContext();
        Object selectedNode = 
            getSelectedNode(context);
        return context.getRenderedText(selectedNode);
    }
    
    /**
     * Returns the text from the mouse position.
     * @param variable -
     * @return the text from the node at mouse position
     */
    public String rcStoreValueAtMousePosition(String variable) {
        AbstractTreeOperationContext context = getTreeAdapter().getContext();
        
        return context.getRenderedText(getNodeAtMousePosition());
    }
    /**
     * 
     * @return the tree node at the current mouse position.
     * @throws StepExecutionException If no tree node can be found at the 
     *                                current mouse position.
     */
    protected abstract Object getNodeAtMousePosition() 
        throws StepExecutionException;
    
    /**
     * Verifies the value of the property with the name <code>name</code>
     * of the tree item at the current mouse position.
     * The name of the property has be specified according to the JavaBean
     * specification. The value returned by the property is converted into a
     * string by calling <code>toString()</code> and is compared to the passed
     * <code>value</code>.
     * 
     * @param name The name of the property
     * @param value The value of the property as a string
     * @param operator The operator used to verify
     * @param timeout the maximum amount of time to wait for the property
     *          at mouse position to be checked
     */
    public void rcCheckPropertyAtMousePosition(final String name,
            final String value, final String operator, int timeout) {
        invokeAndWait("rcCheckPropertyAtMousePosition", timeout, //$NON-NLS-1$
            new Runnable() {
                public void run() {
                    final Object cell = getNodeAtMousePosition();
                    final ITreeComponent bean = getTreeAdapter();
                    final String propToStr = 
                            bean.getPropertyValueOfCell(name, cell);
                    Verifier.match(propToStr, value, operator);
                }
            });
    }

    /**
     * Stores the string representation of the value of the property of the
     * given Node
     * 
     * @param variableName
     *            the name of the variable
     * @param propertyName
     *            the name of the property
     * @return string representation of the property value
     */
    public String rcStorePropertyValueAtMousePosition(String variableName,
            final String propertyName) {
        return getTreeAdapter().getPropertyValueOfCell(propertyName,
                getNodeAtMousePosition());
    }

    /**
     * Verifies whether the first selection in the tree has a rendered text that is
     * equal to <code>pattern</code>.
     *
     * @param pattern
     *            The expected text
     * @param operator
     *            The operator to use when comparing the expected and
     *            actual values.
     * @param timeout the maximum amount of time to wait for the selected value
     *          to be verified
     * @throws StepExecutionException
     *             If no node is selected or the verification fails.
     */
    public void rcVerifySelectedValue(final String pattern,
            final String operator, int timeout)
        throws StepExecutionException {
        invokeAndWait("rcVerifySelectedValue", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        AbstractTreeOperationContext context =
                                getTreeAdapter().getContext();
                        checkNodeText(context.getSelectedNodes(), pattern,
                                operator);
                    }
                });
    }
    
    /**
     * Checks the text for the given node against the given pattern and 
     * operator.
     * 
     * @param node The node containing the text to check.
     * @param pattern The expected text.
     * @param operator The operator to use when comparing the expected and
     *          actual values.
     * @throws StepVerifyFailedException If the verification fails.
     */
    protected void checkNodeText(Object[] node, String pattern, String operator)
        throws StepVerifyFailedException {
        Collection<String> nodeTextList = new ArrayList<String>();
        AbstractTreeOperationContext context = getTreeAdapter().getContext();
        for (int i = 0; i < node.length; i++) {
            nodeTextList.addAll(context.getNodeTextList(node[i])); 
        }
        Iterator<String> it = nodeTextList.iterator();
        boolean isMatched = false;
        while (it.hasNext() && !isMatched) {
            try {
                Verifier.match(it.next(), pattern, operator);
                isMatched = true;
            } catch (StepVerifyFailedException svfe) {
                if (!it.hasNext()) {
                    throw svfe;
                }
                // Otherwise just try the next element
            }
        }
    }
    
    /**
     * @param pathType  For example, "relative" or "absolute".
     * @param preAscend Relative traversals will start this many parent nodes
     *            above the current node. Absolute traversals ignore this
     *            parameter.
     * @param context The context of the traversal.
     * @return The node at which to begin the traversal or <code>null</code>
     *         if the traversal should begin at the root of the node.
     */
    protected Object getStartNode(String pathType, int preAscend,
            AbstractTreeOperationContext context) {
        Object startNode;
        ITreeComponent tree = getTreeAdapter();
        if (pathType.equals(
                ValueSets.SearchType.relative.rcValue())) {
            startNode = getSelectedNode(context);
            Object child = startNode;
            for (int i = 0; i < preAscend; ++i) {
                if ((startNode == null) || ((tree.isRootVisible()) 
                        && (tree.getRootNode() == null))) {
                    TestErrorEvent event = EventFactory
                        .createActionError(TestErrorEvent.TREE_NODE_NOT_FOUND);
                    throw new StepExecutionException(
                        "Tree node not found: Parent of " //$NON-NLS-1$
                        + child.toString(), event);
                }
                child = startNode;
                startNode = context.getParent(startNode);
                
            }
         // Extra handling for tree without visible root node
            if ((startNode == null) || ((tree.isRootVisible()) 
                    && (tree.getRootNode() == null))) {
                startNode = null;
            }
        } else if (pathType.equals(
                ValueSets.SearchType.absolute.rcValue())) {
            startNode = null;
        } else {
            throw new StepExecutionException(
                    pathType + " is not a valid Path Type", EventFactory //$NON-NLS-1$
                    .createActionError(TestErrorEvent.INVALID_PARAM_VALUE));

        }
        
        return startNode;
    }
    
    /**
     * @param mouseButton
     *            mouseButton
     * @param modifier
     *            modifier
     * @param pathType
     *            pathType
     * @param preAscend
     *            preAscend
     * @param treeTextPath
     *            treeTextPath
     * @param operator
     *            operator
     */
    public void rcDragByTextPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treeTextPath,
            String operator) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelect(pathType, preAscend, treeTextPath, operator, 0, 1,
                ValueSets.BinaryChoice.no.rcValue());
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        getRobot().mousePress(null, null, dndHelper.getMouseButton());
        dndHelper.setDragMode(true);
    }

    /**
     * @param pathType
     *            pathType
     * @param preAscend
     *            preAscend
     * @param treeTextPath
     *            treeTextPath
     * @param operator
     *            operator
     * @param delayBeforeDrop
     *            delayBeforeDrop
     */
    public void rcDropByTextPath(String pathType, int preAscend,
            String treeTextPath, String operator, int delayBeforeDrop) {
        try {
            rcSelect(pathType, preAscend, treeTextPath, operator, 0, 1,
                    ValueSets.BinaryChoice.no.rcValue());
            waitBeforeDrop(delayBeforeDrop);
            getRobot().shakeMouse();
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
            dndHelper.setDragMode(false);
        }
    }

    /**
     * @param mouseButton
     *            mouseButton
     * @param modifier
     *            modifier
     * @param pathType
     *            pathType
     * @param preAscend
     *            preAscend
     * @param treeIndexPath
     *            treeIndexPath
     */
    public void rcDragByIndexPath(int mouseButton, String modifier,
            String pathType, int preAscend, String treeIndexPath) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setModifier(modifier);
        dndHelper.setMouseButton(mouseButton);
        rcSelectByIndices(pathType, preAscend, treeIndexPath, 0, 1,
                ValueSets.BinaryChoice.no.rcValue());
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        getRobot().mousePress(null, null, dndHelper.getMouseButton());
        dndHelper.setDragMode(true);
    }

    /**
     * @param pathType
     *            pathType
     * @param preAscend
     *            preAscend
     * @param treeIndexPath
     *            treeIndexPath
     * @param delayBeforeDrop
     *            delayBeforeDrop
     */
    public void rcDropByIndexPath(String pathType, int preAscend,
            String treeIndexPath, int delayBeforeDrop) {
        try {
            rcSelectByIndices(pathType, preAscend, treeIndexPath, 0, 1,
                    ValueSets.BinaryChoice.no.rcValue());
            waitBeforeDrop(delayBeforeDrop);
            getRobot().shakeMouse();
        } finally {
            final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
            getRobot().mouseRelease(null, null, dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
            dndHelper.setDragMode(false);
        }
    }
    
}