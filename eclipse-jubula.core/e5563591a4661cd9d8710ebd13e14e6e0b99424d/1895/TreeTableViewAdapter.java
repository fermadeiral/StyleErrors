/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.Rectangle;
import java.util.concurrent.Callable;

import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.implclasses.tree.AbstractTreeTableOperationContext;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITreeTableComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.TreeTableOperationContext;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;

import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableView;

/**
 * Implementation of the TreeTable interface as an adapter for
 * <code>TreeTableView</code>.
 *
 * @author BREDEX GmbH
 * @created 23.06.2014
 */
public class TreeTableViewAdapter
        extends JavaFXComponentAdapter<TreeTableView<?>>
        implements ITreeTableComponent<TreeTableCell<?, ?>> {

    /**
     * Creates a new Instance
     * 
     * @param objectToAdapt
     *            the TreeTableView which should be adapted
     *
     */
    public TreeTableViewAdapter(TreeTableView<?> objectToAdapt) {
        super(objectToAdapt);
    }

    /**
     * {@inheritDoc}
     */
    public AbstractTreeTableOperationContext<TreeTableView<?>, Object>
            getContext() {
        return new TreeTableOperationContext(
                getRobotFactory().getEventThreadQueuer(), getRobot(),
                getRealComponent());
    }

    /**
     * {@inheritDoc}
     */
    public AbstractTreeTableOperationContext<TreeTableView<?>, Object>
            getContext(int column) {
        return new TreeTableOperationContext(
                getRobotFactory().getEventThreadQueuer(), getRobot(),
                getRealComponent(), column);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPropertyValueOfCell(String name,
            TreeTableCell<?, ?> cell) {
        Object prop = EventThreadQueuerJavaFXImpl.invokeAndWait("getProperty", //$NON-NLS-1$
                new Callable<String>() {
                    @Override
                    public String call() throws Exception {
                        try {
                            IComponent adapter =
                                    (IComponent) AdapterFactoryRegistry
                                            .getInstance()
                                            .getAdapter(IComponent.class, cell);
                            if (adapter != null
                                    && adapter instanceof ITextComponent) {
                                return ((ITextComponent) adapter)
                                        .getPropteryValue(name);
                            }
                            return null;
                        } catch (RobotException e) {
                            throw new StepExecutionException(e.getMessage(),
                                EventFactory.createActionError(
                                    TestErrorEvent.PROPERTY_NOT_ACCESSABLE));
                        }
                    }
                });
        return String.valueOf(prop);
    }

    /**
     * {@inheritDoc}
     */
    public Object getRootNode() {
        Object result = EventThreadQueuerJavaFXImpl.invokeAndWait("getRootNode", //$NON-NLS-1$
                new Callable<Object>() {
                    @Override
                    public Object call() throws Exception {
                        return getRealComponent().getRoot().getValue();
                    }
                });
        return result;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isRootVisible() {
        boolean result = EventThreadQueuerJavaFXImpl
                .invokeAndWait("isRootVisible", new Callable<Boolean>() { //$NON-NLS-1$
                    @Override
                    public Boolean call() throws Exception {
                        return getRealComponent().showRootProperty().getValue();
                    }
                });
        return result;
    }

    /** {@inheritDoc} */
    public int getColumnCount() {
        return 0;
    }

    /** {@inheritDoc} */
    public int getRowCount() {
        return 0;
    }

    /** {@inheritDoc} */
    public String getCellText(int row, int column) {
        return null;
    }

    /** {@inheritDoc} */
    public String getColumnHeaderText(int column) {
        return null;
    }

    /** {@inheritDoc} */
    public int getColumnFromString(String col, String operator) {
        return 0;
    }

    /** {@inheritDoc} */
    public String getRowText(int row) {
        return null;
    }

    /** {@inheritDoc} */
    public int getRowFromString(String row, String operator) {
        return 0;
    }

    /** {@inheritDoc} */
    public Rectangle getHeaderBounds(int col) {
        return null;
    }

    /** {@inheritDoc} */
    public Cell getSelectedCell() throws StepExecutionException {
        return null;
    }

    /** {@inheritDoc} */
    public boolean isHeaderVisible() {
        return false;
    }

    /** {@inheritDoc} */
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    /** {@inheritDoc} */
    public boolean hasCellSelection() {
        return false;
    }

    /** {@inheritDoc} */
    public Rectangle scrollCellToVisible(int row, int col)
            throws StepExecutionException {
        return null;
    }

    /** {@inheritDoc} */
    public Object getTableHeader() {
        return null;
    }

    /** {@inheritDoc} */
    public String getText() {
        return null;
    }

    /** {@inheritDoc} */
    public boolean doesRowExist(int row) {
        return row >= 0 && row < getRowCount();
    }

    /** {@inheritDoc} */
    public int getTopIndex() {
        throw new UnsupportedOperationException("JavaFX treetable does not implement getTopIndex."); //$NON-NLS-1$
        // and we don't need it currently...
    }

    /** {@inheritDoc} */
    public Rectangle getCellBounds(int row, int col, boolean restr) {
        throw new UnsupportedOperationException("JavaFX tree table adapter does not implement getCellBounds."); //$NON-NLS-1$
        // and we don't need it currently...
    }

}
