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
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.adaptable.AdapterFactoryRegistry;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IListComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextComponent;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.NodeTraverseHelper;
import org.eclipse.jubula.rc.javafx.tester.util.Rounding;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;

import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

/**
 * ListView Adapter
 *
 * @param <T>
 *            (sub)-class of ListView
 *
 * @author BREDEX GmbH
 * @created 14.03.2014
 */
public class ListViewAdapter<T extends ListView<?>> extends
        JavaFXComponentAdapter<T> implements IListComponent<ListCell<?>> {
    /**
     * Creates an object with the adapted Label.
     *
     * @param objectToAdapt
     *            this must be an object of the Type <code>ListView</code>
     */
    public ListViewAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    /** {@inheritDoc} **/
    public String getText() {
        String result = EventThreadQueuerJavaFXImpl.invokeAndWait("getText", //$NON-NLS-1$
                new Callable<String>() {

                    /** {@inheritDoc} **/
                    public String call() throws Exception {
                        ObservableList<?> sItems = getRealComponent()
                                .getSelectionModel().getSelectedItems();
                        if (!sItems.isEmpty()) {
                            return String.valueOf(sItems.get(0));
                        }
                        throw new StepExecutionException("No selection found", //$NON-NLS-1$
                                EventFactory.createActionError(
                                        TestErrorEvent.NO_SELECTION));
                    }
                });
        return result;
    }

    /** {@inheritDoc} **/
    public void clickOnIndex(final Integer index, ClickOptions co) {
        final int actualItemCount = EventThreadQueuerJavaFXImpl
                .invokeAndWait("scrollIndexVisible", //$NON-NLS-1$
                        new Callable<Integer>() {
                            public Integer call() throws Exception {
                                final ObservableList<?> items = 
                                        getRealComponent().getItems();
                                int itemCount = items != null ? items.size()
                                        : -1;
                                return new Integer(itemCount);
                            }
                        })
                .intValue();

        if (index >= actualItemCount || (index < 0)) {
            throw new StepExecutionException(
                    "List index '" + index //$NON-NLS-1$
                            + "' is out of range", //$NON-NLS-1$
                    EventFactory
                            .createActionError(TestErrorEvent.INVALID_INDEX));
        }
        EventThreadQueuerJavaFXImpl.invokeAndWait("scrollIndexVisible", //$NON-NLS-1$
                new Callable<Rectangle>() {
                    public Rectangle call() throws Exception {
                        final T listView = getRealComponent();
                        listView.layout();
                        listView.scrollTo(index.intValue());
                        return null;
                    }
                });
        TimeUtil.delay(100); // wait a little bit for layout to be done
        Rectangle r = scrollToAndGetRectangle(index);

        getRobot().click(getRealComponent(), r,
                co.setClickType(ClickOptions.ClickType.RELEASED));
    }

    /**
     * scrolls to the specified intex and returns the target {@link Rectangle}
     * @param index the index
     * @return the target {@link Rectangle}
     */
    private Rectangle scrollToAndGetRectangle(final Integer index) {
        Rectangle r = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "scrollIndexVisible", //$NON-NLS-1$
                new Callable<Rectangle>() {
                    public Rectangle call() throws Exception {
                        final T listView = getRealComponent();
                        listView.scrollTo(index.intValue());
                        listView.layout();

                        List<ListCell> lCells = NodeTraverseHelper
                                .getInstancesOf(listView, ListCell.class);
                        for (ListCell<?> cell : lCells) {
                            if (cell.getIndex() == index.intValue()
                                    && cell.getListView() == listView) {

                                Rectangle b = NodeBounds
                                        .getAbsoluteBounds(cell);
                                Rectangle tableB = NodeBounds
                                        .getAbsoluteBounds(listView);
                                double widthOfClickableRectangle = b.getWidth();
                                double prefWidth = cell
                                        .prefWidth(cell.getHeight());
                                
                                // If the clickable rectangle wider than the  listview, then the width of the clickable
                                // rectangle will be the width of the listview
                                if (widthOfClickableRectangle > listView
                                        .getWidth()) {
                                    widthOfClickableRectangle = listView
                                            .getWidth();
                                }
                                if (StringUtils.isBlank(cell.getText())) {
                                    widthOfClickableRectangle = b.getWidth();
                                }
                                double bHeight = b.getHeight();
                                if ((b.y - tableB.y) < 0) { 
                                    // calculate height of the cell if its at the beginning of the list
                                    bHeight = b.getHeight() + (tableB.y - b.y);
                                } 
                                return new Rectangle(Math.abs(tableB.x - b.x),
                                        Math.abs(tableB.y - b.y),
                                        Rounding.round(
                                                widthOfClickableRectangle),
                                        Rounding.round(bHeight));

                            }
                        }
                        return null;
                    }
                });
        return r;
    }

    /** {@inheritDoc} **/
    public int[] getSelectedIndices() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getSelectedIndices", //$NON-NLS-1$
                new Callable<int[]>() {
                    /** {@inheritDoc} **/
                    public int[] call() throws Exception {
                        ObservableList<Integer> sIndices = getRealComponent()
                                .getSelectionModel().getSelectedIndices();
                        return ArrayUtils
                                .toPrimitive(sIndices.toArray(new Integer[0]));
                    }
                });
    }

    /** {@inheritDoc} **/
    public String[] getSelectedValues() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getSelectedValues", //$NON-NLS-1$
                new Callable<String[]>() {
                    /** {@inheritDoc} **/
                    public String[] call() throws Exception {
                        final T listView = getRealComponent();
                        ObservableList<Integer> sIndices = listView
                                .getSelectionModel().getSelectedIndices();

                        List<String> selectedValues = new LinkedList<String>();
                        for (Integer i : sIndices) {
                            int index = i.intValue();
                            listView.scrollTo(index);
                            listView.layout();
                            List<ListCell> lCells = NodeTraverseHelper
                                    .getInstancesOf(listView, ListCell.class);
                            for (ListCell<?> cell : lCells) {
                                if (cell.getIndex() == index
                                        && cell.getListView() == listView) {
                                    selectedValues.add(getCellText(cell));
                                    break;
                                }
                            }
                        }
                        return selectedValues.toArray(new String[0]);
                    }
                });
    }

    /** {@inheritDoc} **/
    public String[] getValues() {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getValues", //$NON-NLS-1$
                new Callable<String[]>() {
                    /** {@inheritDoc} **/
                    public String[] call() throws Exception {
                        List<String> values = new LinkedList<String>();
                        final T listView = getRealComponent();
                        ObservableList<?> items = listView.getItems();
                        int itemCount = items != null ? items.size() : -1;
                        for (int i = 0; i < itemCount; i++) {
                            listView.scrollTo(i);
                            listView.layout();
                            List<ListCell> lCells = NodeTraverseHelper
                                    .getInstancesOf(listView, ListCell.class);
                            for (ListCell<?> cell : lCells) {
                                if (cell.getIndex() == i
                                        && cell.getListView() == listView) {
                                    values.add(getCellText(cell));
                                    break;
                                }
                            }
                        }
                        return values.toArray(new String[0]);
                    }
                });
    }

    /**
     * {@inheritDoc}
     */
    public String getPropertyValueOfCell(String name, ListCell<?> cell) {
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
     * Get the rendered cell text
     * 
     * @param cell
     *            the cell
     * @return the rendered text
     */
    private String getCellText(ListCell<?> cell) {
        IComponent adapter = (IComponent) AdapterFactoryRegistry.getInstance()
                .getAdapter(IComponent.class, cell);
        if (adapter != null && adapter instanceof ITextComponent) {
            return ((ITextComponent) adapter).getText();
        }
        return null;
    }

    /**
     * Returns the cell based on the given index, or null if no cell was found
     * 
     * @param index the index of the cell to look for
     * @return the cell or null
     */
    public ListCell<?> getCell(int index) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getCell", //$NON-NLS-1$
                new Callable<ListCell<?>>() {
                    public ListCell<?> call() throws Exception {
                        final T listView = getRealComponent();
                        listView.scrollTo(index);
                        listView.layout();

                        List<ListCell> lCells = NodeTraverseHelper
                                .getInstancesOf(listView, ListCell.class);
                        for (ListCell<?> cell : lCells) {
                            if (cell.getIndex() == index
                                    && cell.getListView() == listView) {
                                return cell;
                            }
                        }
                        return null;
                    }
                });
    }

    /**
     * Returns the cell based on the given value, or null if no cell was found
     * 
     * @param value the value of the cell to look for
     * @return the cell or null
     */
    public ListCell<?> getCell(String value) {
        return EventThreadQueuerJavaFXImpl.invokeAndWait("getCell", //$NON-NLS-1$
                new Callable<ListCell<?>>() {
                    public ListCell<?> call() throws Exception {
                        final T listView = getRealComponent();
                        ObservableList<?> items = listView.getItems();
                        int itemCount = items != null ? items.size() : -1;
                        for (int i = 0; i < itemCount; i++) {
                            listView.scrollTo(i);
                            listView.layout();
                            List<ListCell> lCells = NodeTraverseHelper
                                    .getInstancesOf(listView, ListCell.class);
                            for (ListCell<?> cell : lCells) {
                                if (cell.getIndex() == i
                                        && cell.getListView() == listView
                                        && getCellText(cell).equals(value)) {
                                    return cell;
                                }
                            }
                        }
                        return null;
                    }
                });
    }
}