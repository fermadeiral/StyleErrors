/*******************************************************************************
 * Copyright (c) 2004, 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.common.implclasses.tree;

import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;

/**
 * This context holds the tree, the tree model and supports access to the Robot.
 * It also implements some general operations on trees.
 * 
 * @author BREDEX GmbH
 * @param <TREE_TYPE>
 *            the tree type
 * @param <NODE_TYPE>
 *            the node type
 */
public abstract class AbstractTreeTableOperationContext<TREE_TYPE, NODE_TYPE>
        extends AbstractTreeOperationContext<TREE_TYPE, NODE_TYPE> {

    /**
     * The column on which to perform the operation
     **/
    private int m_column;

    /**
     * 
     * @param queuer
     *            queuer
     * @param robot
     *            robot
     * @param tree
     *            tree
     */
    public AbstractTreeTableOperationContext(IEventThreadQueuer queuer,
            IRobot robot, TREE_TYPE tree) {
        this(queuer, robot, tree, 0);
    }

    /**
     * 
     * @param queuer
     *            queuer
     * @param robot
     *            robot
     * @param tree
     *            tree
     * @param column
     *            column
     */
    public AbstractTreeTableOperationContext(IEventThreadQueuer queuer,
            IRobot robot, TREE_TYPE tree, int column) {
        super(queuer, robot, tree);
        setColumn(column);
    }

    /**
     * 
     * @return the column
     */
    public int getColumn() {
        return m_column;
    }

    /**
     * 
     * @param column
     *            the column
     */
    public void setColumn(int column) {
        this.m_column = column;
    }

    /**
     * get the bounds of the header of a column
     * 
     * @param col
     *            the column index
     * @return the header bounds
     */
    public abstract Rectangle getHeaderBounds(final int col);

    /**
     * get the text in the header of a column
     * 
     * @param colIdx
     *            the column index
     * @return the header text
     */
    public abstract String getColumnHeaderText(final int colIdx);

    /**
     * gets the column index based on the given string
     * 
     * @param col
     *            the column
     * @param operator
     *            the operator
     * @param leaf
     *            true if the column found has to be a leaf column
     * @return the index or -2 if no column was found
     */
    public abstract int getColumnFromString(final String col,
            final String operator, boolean leaf);

    /**
     * Gets the rendered Text from the cell of the currently set column
     * 
     * @param node
     *            the node
     * @return the rendered text
     * @throws StepExecutionException
     */
    public abstract String getRenderedTextOfColumn(final Object node);

    /**
     * 
     * @return the number of columns
     */
    public abstract int getNumberOfColumns();
    
    /**
     * Getter for the treetable
     * @return Returns the treetable.
     */
    protected TREE_TYPE getTreeTable() {
        return super.getTree();
    }

}
