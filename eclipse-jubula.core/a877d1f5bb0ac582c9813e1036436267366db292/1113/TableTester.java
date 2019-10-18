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
package org.eclipse.jubula.rc.swt.tester;

import static org.eclipse.jubula.rc.common.driver.CheckWithTimeoutQueuer.invokeAndWait;

import java.awt.Point;
import java.awt.Rectangle;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.tester.AbstractTableTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITableComponent;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.ITextInputComponent;
import org.eclipse.jubula.rc.common.util.Verifier;
import org.eclipse.jubula.rc.swt.components.ISWTTableComponent;
import org.eclipse.jubula.rc.swt.components.SWTCell;
import org.eclipse.jubula.rc.swt.tester.adapter.StyledTextAdapter;
import org.eclipse.jubula.rc.swt.tester.adapter.TableAdapter;
import org.eclipse.jubula.rc.swt.tester.adapter.TextComponentAdapter;
import org.eclipse.jubula.rc.swt.tester.adapter.TreeAdapter;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;
/**
 * Toolkit specific commands for the <code>Table</code>
 *
 * @author BREDEX GmbH
 */
public class TableTester extends AbstractTableTester {
    /**
     * Simple constructor
     */
    public TableTester() {
        super();
    }
    /**
     * Used in emulating multiple inheritance for SWT Tree Tables
     * @param adapter the (Tree)Table adapter
     */
    public TableTester(IComponent adapter) {
        super();
        setAdapter(adapter);
    }

    /**
     * @return the Tree and Table adapter
     */
    private ISWTTableComponent getSWTAdapter() {
        return (ISWTTableComponent) getComponent();
    }
    /**
     * {@inheritDoc}
     */
    public String[] getTextArrayFromComponent() {
        final String[] componentTextArray;
        Item[] itemArray = getSWTAdapter().getColumnItems();
        componentTextArray = getTextArrayFromItemArray(itemArray);         
        return componentTextArray;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Object setEditorToReplaceMode(Object editor, boolean replace) {

        if (replace) {
            ITextInputComponent textEditor = null;
            if (editor instanceof Text) {
                textEditor = new TextComponentAdapter(editor);
            }
            if (editor instanceof StyledText) {
                textEditor = new StyledTextAdapter(editor);
            }
            if (EnvironmentUtils.isMacOS()) {
                getRobot().clickAtCurrentPosition(editor, 3, 
                        InteractionMode.primary.rcIntValue());
            } else {
                getRobot().keyStroke(getRobot().getSystemModifierSpec() + " A"); //$NON-NLS-1$
            }
            if (textEditor != null) {
                if (!textEditor.getSelectionText()
                        .equals(textEditor.getText())) {
                    // if the whole text is not selected, select programmatic
                    textEditor.selectAll();
                }
            }
        } else {
            getRobot().clickAtCurrentPosition(editor, 2, 
                    InteractionMode.primary.rcIntValue());
        }
        return editor;
    }

    /**
     * {@inheritDoc}
     */
    protected Object activateEditor(Cell cell, Rectangle rectangle) {
        TableAdapter table = (TableAdapter) getComponent();
        return table.activateEditor(cell);
    }

    /**
     * {@inheritDoc}
     */
    protected int getExtendSelectionModifier() {
        return SWT.MOD1;
    }

    /**
     * {@inheritDoc}
     */
    protected Cell getCellAtMousePosition() throws StepExecutionException {
        final Point awtMousePos = getRobot().getCurrentMousePosition();
        Cell returnvalue = getEventThreadQueuer().invokeAndWait("getCellAtMousePosition", new IRunnable<Cell>() { //$NON-NLS-1$
            public Cell run() throws StepExecutionException {
                ITableComponent adapter = (ITableComponent) getComponent();
                Control comp = (Control) adapter.getRealComponent();
                Cell cell = null;
                final int itemCount = adapter.getRowCount();
                for (int rowCount = adapter.getTopIndex(); 
                        rowCount < itemCount; rowCount++) {
                    if (cell != null) {
                        break;
                    }
                    final int columnCount = adapter.getColumnCount() == 0
                            ? 1 : adapter.getColumnCount();
                    for (int col = 0; col < columnCount; col++) {
                        checkRowColBounds(rowCount, col);
                        final Rectangle itemBounds = adapter.getCellBounds(
                                rowCount, col, false);
                        if (col == 0
                                && (comp.getStyle() & SWT.CHECK) == SWT.CHECK) {
                            itemBounds.width += itemBounds.x;
                            itemBounds.x = 0;
                        }
                        final org.eclipse.swt.graphics.Point 
                            absItemBounds = comp
                                .toDisplay(itemBounds.x, itemBounds.y);
                        final Rectangle absRect = new Rectangle(
                                absItemBounds.x, absItemBounds.y,
                                itemBounds.width,
                                itemBounds.height);
                        if (absRect.contains(awtMousePos)) {
                            // not very nice, but this was the simplest...
                            if (comp instanceof Table) {
                                cell = new SWTCell(rowCount, col,
                                    ((Table) comp).getItem(rowCount));
                            } else if (comp instanceof Tree) {
                                cell = new SWTCell(rowCount, col,
                                    ((TreeAdapter) getComponent()).
                                    getRow(rowCount));
                            }
                            break;
                        }
                    }
                }
                if (cell == null) {
                    throw new StepExecutionException(
                            "No cell under mouse position found!", //$NON-NLS-1$
                            EventFactory.createActionError(
                                            TestErrorEvent.NOT_FOUND));
                }
                return cell;
            }
        });
        return returnvalue;
    }

    /**
     * {@inheritDoc}
     */
    protected boolean isMouseOnHeader() {
        final ITableComponent adapter = (ITableComponent)getComponent();
        if (!adapter.isHeaderVisible()) {
            return false;
        }
        
        Boolean isOnHeader = new Boolean(false);
        isOnHeader = getEventThreadQueuer().invokeAndWait(
                "isMouseOnHeader", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() {
                        final Point awtMousePos = getRobot()
                            .getCurrentMousePosition();
                        org.eclipse.swt.graphics.Point mousePos =
                            new org.eclipse.swt.graphics.Point(
                                awtMousePos.x, awtMousePos.y);

                        for (int j = 0; j < adapter.getColumnCount(); j++) {
                            final Rectangle constraints = 
                                    adapter.getHeaderBounds(j);
                            
                            org.eclipse.swt.graphics.Rectangle bounds = 
                                    SwtUtils.getWidgetBounds(
                                    (Control) adapter.getRealComponent());
                            
                            if (constraints != null) {
                                // Use SWT's mapping function, if possible, as it is more
                                // multi-platform than simply adding the x and y values.
                                org.eclipse.swt.graphics.Point
                                    convertedLocation = getConvertedLocation(
                                        constraints);
                                bounds.x = convertedLocation.x;
                                bounds.y = convertedLocation.y;
                                
                                bounds.height = constraints.height;
                                bounds.width = constraints.width;
                            }

                            if (bounds.contains(mousePos)) {
                                return true;
                            }
                        }      
                        return false;
                    }
                });                  
        
        return isOnHeader.booleanValue();
    }
    
    /**
     * Returns an array of representation strings that corresponds to the given
     * array of items or null if the given array is null;
     * @param itemArray the item array whose item texts have to be read
     * @return array of item texts corresponding to the given item array
     */
    protected final String[] getTextArrayFromItemArray(Item[] itemArray) {
        final String[] itemTextArray;
        if (itemArray == null) {
            itemTextArray = null;
        } else {
            itemTextArray = new String[itemArray.length];
            for (int i = 0; i < itemArray.length; i++) {
                Item item = itemArray[i];
                if (item == null) {
                    itemTextArray[i] = null;
                } else {
                    String fallback = SwtUtils.removeMnemonics(item.getText());
                    itemTextArray[i] = CAPUtil.getWidgetText(item, fallback);
                }
            }
        }
        
        return itemTextArray;
    }
        
    /**
     * @param constraints
     *            Rectangle
     * @return converted Location of table
     */
    private org.eclipse.swt.graphics.Point getConvertedLocation(
            final Rectangle constraints) {
        return getEventThreadQueuer().invokeAndWait("toDisplay", new IRunnable<org.eclipse.swt.graphics.Point>() { //$NON-NLS-1$
            public org.eclipse.swt.graphics.Point run()
                    throws StepExecutionException {
                Control cont = (Control) getComponent().getRealComponent();
                return cont.toDisplay(constraints.x, constraints.y);
            }
        });
    }

    /**
     * {@inheritDoc}
     */
    protected Object getSpecificRectangle(Rectangle rectangle) {
        return new org.eclipse.swt.graphics.Rectangle(rectangle.x, rectangle.y,
                rectangle.width, rectangle.height);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcClickDirect(int count, int button, int xPos, String xUnits, 
        int yPos, String yUnits) throws StepExecutionException {
        
        int correctedYPos = correctYPos(yPos, yUnits);
        super.rcClickDirect(count, button, xPos, xUnits, correctedYPos, yUnits);
    }
    
    /**
     * Corrects the given Y position based on the height of the table's header.
     * This ensures, for example, that test steps don't try to click within the
     * table header (where we receive no confirmation events).
     * 
     * @param pos The Y position to correct.
     * @param units The units used for the Y position.
     * @return The corrected Y position.
     */
    private int correctYPos(int pos, String units) {
        int correctedPos = pos;
        int headerHeight = getEventThreadQueuer().invokeAndWait(
                "getHeaderHeight", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() throws StepExecutionException {
                        return ((Table) getComponent().getRealComponent())
                                .getHeaderHeight();
                    }
                });

        if (ValueSets.Unit.pixel.rcValue().equalsIgnoreCase(units)) {
            // Pixel units
            correctedPos += headerHeight;
        } else {
            // Percentage units
            int totalHeight = getEventThreadQueuer().invokeAndWait(
                    "getWidgetBounds", new IRunnable<Integer>() { //$NON-NLS-1$
                        public Integer run() throws StepExecutionException {
                            return SwtUtils.getWidgetBounds(
                                    (Widget) getComponent().
                                        getRealComponent()).height;
                        }
            
                    });
            long targetHeight = totalHeight - headerHeight;
            long targetPos = Math.round((double)targetHeight * (double)pos
                / 100.0);
            targetPos += headerHeight;
            double heightPercentage = 
                (double)targetPos / (double)totalHeight * 100.0;
            correctedPos = (int)Math.round(heightPercentage);
            if (correctedPos > 100) { // rounding error
                correctedPos = 100;
            }
        }
        return correctedPos;
    }
    
    /**
     * Drags the cell of the Table.<br>
     * With the xPos, yPos, xunits and yUnits the click position inside the 
     * cell can be defined.
     * 
     * @param mouseButton the mouseButton.
     * @param modifier the modifier.
     * @param row The row of the cell.
     * @param rowOperator the row header operator
     * @param col The column of the cell.
     * @param colOperator the column header operator
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException
     *             If the row or the column is invalid
     */
    public void rcDragCell(final int mouseButton, final String modifier, 
            final String row, String rowOperator, final String col,
            final String colOperator, final int xPos, final String xUnits,
            final int yPos, final String yUnits) 
        throws StepExecutionException {
        
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        
        rcSelectCell(row, rowOperator, col, colOperator, 0, xPos,
                xUnits, yPos, yUnits, 
                ValueSets.BinaryChoice.no.rcValue(), 1);
    }
    
    /**
     * Drops on the cell of the JTable.<br>
     * With the xPos, yPos, xunits and yUnits the click position inside the 
     * cell can be defined.
     * 
     * @param row The row of the cell.
     * @param rowOperator The row operator
     * @param col The column of the cell.
     * @param colOperator The column operator
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     * @throws StepExecutionException
     *             If the row or the column is invalid
     */
    public void rcDropCell(final String row, final String rowOperator,
            final String col, final String colOperator, final int xPos, 
            final String xUnits, final int yPos, final String yUnits,
            int delayBeforeDrop) throws StepExecutionException {
        
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        final IRobot robot = getRobot();
        
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        try {
            getEventThreadQueuer().invokeAndWait("rcDropCell", new IRunnable<Void>() { //$NON-NLS-1$

                public Void run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());

                    robot.shakeMouse();

                    // drop
                    rcSelectCell(row, rowOperator, col, colOperator, 0, xPos,
                            xUnits, yPos, yUnits, 
                            ValueSets.BinaryChoice.no.rcValue(), 1);
                    return null;
                }            
            });

            waitBeforeDrop(delayBeforeDrop);
            
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and drags this row.
     * 
     * @param mouseButton the mouse button
     * @param modifier the modifier
     * @param col the column
     * @param colOperator the column header operator
     * @param value the value
     * @param regexOp the regex operator
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void rcDragRowByValue(int mouseButton, String modifier, String col,
            String colOperator, final String value, final String regexOp,
            final String searchType) {
        
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        
        rcSelectRowByValue(col, colOperator, value, regexOp, 1, 
                ValueSets.BinaryChoice.no.rcValue(),
                searchType, 1);
    }
    
    /**
     * Finds the first row which contains the value <code>value</code>
     * in column <code>col</code> and drops on this row.
     * 
     * @param col the column
     * @param colOperator the column operator
     * @param value the value
     * @param regexOp the regex operator
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     */
    public void rcDropRowByValue(final String col, final String colOperator,
            final String value, final String regexOp, final String searchType, 
            int delayBeforeDrop) {
        
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        final IRobot robot = getRobot();
        pressOrReleaseModifiers(dndHelper.getModifier(), true);
        
        try {
            getEventThreadQueuer().invokeAndWait("rcDropRowByValue", new IRunnable<Void>() { //$NON-NLS-1$

                public Void run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());

                    robot.shakeMouse();

                    // drop
                    selectRowByValue(col, colOperator, value, regexOp,
                            ValueSets.BinaryChoice.no.rcValue(), 
                            searchType, 
                            ClickOptions.create().setClickCount(0));
                    return null;
                }            
            });
            
            waitBeforeDrop(delayBeforeDrop);
            
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and drags the cell.
     * 
     * @param mouseButton the mouse button
     * @param modifier the modifiers
     * @param row the row
     * @param rowOperator the row header operator
     * @param value the value
     * @param regex search using regex
     * @param searchType Determines where the search begins ("relative" or "absolute")
     */
    public void rcDragCellByColValue(int mouseButton, String modifier,
            String row, String rowOperator, final String value,
            final String regex, final String searchType) {
        
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        selectCellByColValue(row, rowOperator, value, regex, 
                ValueSets.BinaryChoice.no.rcValue(),
                searchType, ClickOptions.create().setClickCount(0));
    }
    
    /**
     * Finds the first column which contains the value <code>value</code>
     * in the given row and drops on the cell.
     * 
     * @param row the row
     * @param rowOperator the row header operator
     * @param value the value
     * @param regex search using regex
     * @param searchType Determines where the search begins ("relative" or "absolute")
     * @param delayBeforeDrop the amount of time (in milliseconds) to wait
     *                        between moving the mouse to the drop point and
     *                        releasing the mouse button                       
     */
    public void rcDropCellByColValue(final String row, final String rowOperator,
            final String value, final String regex, final String searchType,
            int delayBeforeDrop) {
        
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        final IRobot robot = getRobot();
        pressOrReleaseModifiers(dndHelper.getModifier(), true);

        try {
            getEventThreadQueuer().invokeAndWait("rcDropCellByColValue", new IRunnable<Void>() { //$NON-NLS-1$

                public Void run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            dndHelper.getMouseButton());

                    robot.shakeMouse();

                    // drop
                    selectCellByColValue(row, rowOperator, value, regex,
                            ValueSets.BinaryChoice.no.rcValue(), 
                            searchType, 
                            ClickOptions.create().setClickCount(0));
                    return null;
                }            
            });

            waitBeforeDrop(delayBeforeDrop);
        
        } finally {
            robot.mouseRelease(dndHelper.getDragComponent(), null, 
                    dndHelper.getMouseButton());
            pressOrReleaseModifiers(dndHelper.getModifier(), false);
        }
    }
    
    /**
     * Verifies whether the checkbox in the row of the selected cell 
     * is checked
     * 
     * @param checked true if checkbox in cell should be selected, false otherwise
     * @param timeout the maximum amount of time to wait to verify whether the
     *          checkbox in the row is checked
     * @throws StepExecutionException If no cell is selected or the verification fails.
     */
    public void rcVerifyCheckboxInSelectedRow(final boolean checked,
            int timeout) throws StepExecutionException {
        invokeAndWait("rcVerifyCheckBoxInSelectedRow", timeout, new Runnable() { //$NON-NLS-1$
            public void run() {
                int row = ((ITableComponent) getComponent()).getSelectedCell()
                        .getRow();
                verifyCheckboxInRow(checked, row);
            }
        });
    }
    
    /**
     * Verifies whether the checkbox in the row under the mouse pointer is checked
     * 
     * @param checked true if checkbox in cell is selected, false otherwise
     * @param timeout the maximum amount of time to wait to verify whether the
     *          checkbox in the row under the mouse pointer is checked
     */
    public void rcVerifyCheckboxInRowAtMousePosition(final boolean checked,
            int timeout) {
        invokeAndWait("rcVerifyCheckboxInRowAtMousePosition", timeout, //$NON-NLS-1$
                new Runnable() {
                    public void run() {
                        int row = getCellAtMousePosition().getRow();
                        verifyCheckboxInRow(checked, row);
                    }
                });
    }
    
    /**
     * Verifies whether the checkbox in the row with the given
     * <code>index</code> is checked
     * 
     * @param checked true if checkbox in cell is selected, false otherwise
     * @param row the row-index of the cell in which the checkbox-state should be verified
     */
    private void verifyCheckboxInRow(boolean checked, final int row) {
        Boolean checkIndex = getEventThreadQueuer().invokeAndWait(
                "rcVerifyTableCheckboxIndex", new IRunnable<Boolean>() { //$NON-NLS-1$
                    public Boolean run() throws StepExecutionException {
                        Control cont = (Control) getComponent().
                                getRealComponent();
                        if ((cont.getStyle() & SWT.CHECK) == 0) {
                            throw new StepExecutionException(
                                    "No checkbox found", //$NON-NLS-1$
                                    EventFactory.createActionError(
                                            TestErrorEvent.CHECKBOX_NOT_FOUND));
                        }
                        return getSWTAdapter().isChecked(row);
                    }
                });
        Verifier.equals(checked, checkIndex.booleanValue());
    }

    /**
     * Toggles the checkbox in the row under the Mouse Pointer 
     */
    public void rcToggleCheckboxInRowAtMousePosition() {
        toggleCheckboxInRow(getCellAtMousePosition().getRow());
    }
    
    /**
     * Toggles the checkbox in the selected row
     */
    public void rcToggleCheckboxInSelectedRow() {
        int row = getEventThreadQueuer().invokeAndWait(
                "get Selection index", new IRunnable<Integer>() { //$NON-NLS-1$
                    public Integer run() throws StepExecutionException {
                        return getSWTAdapter().getSelectionIndex();
                    }
                });
        toggleCheckboxInRow(row);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcReplaceText(String text) throws StepExecutionException {
        super.rcReplaceText(text);
        getRobot().keyType(null, SWT.CR);
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcReplaceText(String text, String row, String rowOperator,
            String col, String colOperator) {
        super.rcReplaceText(text, row, rowOperator, col, colOperator);
        getRobot().keyType(null, SWT.CR);
    }
    /**
     * Toggles the checkbox in the row with the given index
     * @param row the index
     */
    private void toggleCheckboxInRow(final int row) {

        if (row == -1) {
            getEventThreadQueuer().invokeAndWait(
                "No Selection", new IRunnable<Void>() { //$NON-NLS-1$
                    public Void run() throws StepExecutionException {
                        throw new StepExecutionException(
                            "No Selection found ", //$NON-NLS-1$
                            EventFactory.createActionError(
                                TestErrorEvent.NO_SELECTION));
                    }
                });
        }
        Rectangle rect = ((ITableComponent) getComponent()).
                scrollCellToVisible(row, 0);

        org.eclipse.swt.graphics.Rectangle itemBounds =
                getEventThreadQueuer().invokeAndWait("getTableItem", //$NON-NLS-1$
                        new IRunnable<org.eclipse.swt.graphics.Rectangle>() {
                            public org.eclipse.swt.graphics.Rectangle run()
                                    throws StepExecutionException {
                                Control comp = (Control) getRealComponent();
                                if (comp instanceof Table) {
                                    return ((Table) comp).getItem(row)
                                            .getBounds();
                                }
                                if (comp instanceof Tree) {
                                    return ((Tree) comp).getItem(row)
                                            .getBounds();
                                }
                                return null;
                            }
                        });

        // Creates a Rectangle with bounds around the checkbox of the row
        org.eclipse.swt.graphics.Rectangle cbxBounds = 
                new org.eclipse.swt.graphics.Rectangle(0,
                        itemBounds.y, rect.x, itemBounds.height);
        
        // Performs a click in the middle of the Rectangle
        getRobot().click(getComponent().getRealComponent(), cbxBounds,
                ClickOptions.create().left().setScrollToVisible(false),
                rect.x / 2, true, itemBounds.height / 2, true);
    }

    /** {@inheritDoc} */
    protected Object getNodeAtMousePosition() throws StepExecutionException {
        return getCellAtMousePosition();
    }

    /**
     * Used in emulating multiple inheritance for SWT TreeTables
     * @param adapter the (Tree)Table adapter
     */
    public void setAdapter(TableAdapter adapter) {
        setAdapter(adapter);
    }
}
