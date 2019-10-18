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

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * This class contains constants to be used for the layout of all components
 * which will be displayed graphical. (GUI)
 * 
 * @author BREDEX GmbH
 * @created 15.10.2004
 */
public class LayoutUtil {
    /** value for 0, use the detailed constant! */
    public static final int ZERO = 0;
    /** value for SMALL_XXX, use the detailed constant! */
    public static final int SMALL = 10;
    /** value for BIG_XXX, use the detailed constant! */
    public static final int BIG = 15;
    /** value for max. chars-width of a control, use the detailed constant! */
    public static final int WIDTH = 30;
    // constants for all layouts
    /** small margin width, for all layouts */
    public static final int SMALL_MARGIN_WIDTH = SMALL;
    /** big margin width, for all layouts */
    public static final int BIG_MARGIN_WIDTH = BIG;
    /** small margin height, for all layouts */
    public static final int SMALL_MARGIN_HEIGHT = SMALL;
    /** big margin height, for all layouts */
    public static final int BIG_MARGIN_HEIGHT = BIG;
    /** small horizontal spacing */
    public static final int SMALL_HORIZONTAL_SPACING = SMALL;
    /** big horizontal spacing */
    public static final int BIG_HORIZONTAL_SPACING = BIG;
    /** small vertical spacing */
    public static final int SMALL_VERTICAL_SPACING = SMALL;
    /** big vertical spacing */
    public static final int BIG_VERTICAL_SPACING = BIG;
    // constants for alignment
    /** the horizontal alignment for controls, except labels, m_text */
    public static final int HORIZONTAL_ALIGNMENT = SWT.FILL;
    /** the horizontal alignmemt for labels */
    public static final int LABEL_HORIZONTAL_ALIGNMENT = SWT.END;
    /**the horizontal alignmemt for labels in a GridLayout with more than two columns */
    public static final int MULTI_COLUMN_LABEL_HORIZONTAL_ALIGNMENT = 
        SWT.BEGINNING;
    /** the horizontal alignmemt for textfields */
    public static final int TEXT_HORIZONTAL_ALIGNMENT = SWT.BEGINNING;
    /** the horizontal alignmemt for multi line textfields in a GridLayout */
    public static final int MULTI_LINE_TEXT_HORIZONTAL_ALIGNMENT = SWT.FILL;
    /** the vertical algnment for all controls */
    public static final int VERTICAL_ALIGNMENT = SWT.CENTER;
    // style for controls 
    /** the style for a single - line m_text */
    public static final int SINGLE_TEXT_STYLE = SWT.SINGLE | SWT.BORDER;
    /** the style for a multi - line m_text */
    public static final int MULTI_TEXT_STYLE = SWT.MULTI | SWT.BORDER
            | SWT.V_SCROLL | SWT.H_SCROLL;
    /** the style for a multi - line text without border*/
    public static final int MULTI_TEXT = SWT.MULTI | SWT.WRAP;
    /** margin width = 2 */
    public static final int MARGIN_WIDTH = 2;    
    /** margin height = 2 */
    public static final int MARGIN_HEIGHT = 2;
    
    // -------------------------------------------------------------
    // Colors
    // -------------------------------------------------------------
    /** color gray for disabled GUI elements (foreground) */
    public static final Color LIGHT_GRAY_COLOR = new Color(
            Display.getDefault(), new RGB(238, 238, 238));
    /** color gray for disabled GUI elements (foreground) */
    public static final Color GRAY_COLOR = new Color(
            Display.getDefault(), new RGB(100, 100, 100));
    /** color for inactive GUI elements */
    public static final Color INACTIVE_COLOR = Display.getDefault()
        .getSystemColor(SWT.COLOR_DARK_GREEN);
    /** default color (mostly black) for GUI elements */
    public static final Color DEFAULT_OS_COLOR = null;
    
    // -------------------------------------------------------------
    // Font
    // -------------------------------------------------------------
    /**
     * <code>FONT_NAME</code>
     */
    public static final String FONT_NAME = "Tahoma"; //$NON-NLS-1$

    /**
     * <code>FONT_HEIGHT</code>
     */
    public static final int FONT_HEIGHT = 9;

    /** bold tahoma font, size: 9 */
    public static final Font BOLD_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.BOLD);

    /** bold,italic tahoma font, size: 9 */
    public static final Font BOLD_ITALIC_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.BOLD | SWT.ITALIC);

    /** italic tahoma font, size: 9 */
    public static final Font ITALIC_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.ITALIC);

    /** tahoma font, size: 9 */
    public static final Font NORMAL_TAHOMA = new Font(null, FONT_NAME,
            FONT_HEIGHT, SWT.NORMAL);

    /**
     * do not instantiate this class
     */
    private LayoutUtil() {
        super();
    }
    
    /**
     * Sets a character limit (255) for the text field (incl. warning, if limit was reached)
     * @param textField The actual text field.
     */
    public static void setMaxChar(final Text textField) {
        setMaxChar(textField, 255);
    }
    
    /**
     * Sets a character limit for the text field (incl. warning, if limit was reached)
     * @param textField The actual text field.
     * @param maxLength the max size of input
     */
    public static void setMaxChar(final Text textField, final int maxLength) {
        if (textField == null) {
            return;
        }
        textField.setTextLimit(maxLength);
        textField.addModifyListener(new ModifyListener() {
            /**
             * <code>m_oldValue</code> the old value
             */
            private String m_oldValue = textField.getText();
            
            public void modifyText(ModifyEvent e) {
                Text theWidget = ((Text)e.widget);
                if (theWidget.getCharCount() >= maxLength) {
                    theWidget.setText(m_oldValue);
                    ErrorHandlingUtil.createMessageDialog(
                            MessageIDs.W_MAX_CHAR, 
                            new Object[] {maxLength}, null);
                }
                m_oldValue = theWidget.getText();
            }
        });
    }

    /**
     * Defines a constant width for a control and shows a tool tip with the text 
     * of this control, when this text is longer than the constant width.
     * @param gridData The gridData object.
     * @param control The actual SWT control.
     */
    public static void addToolTipAndMaxWidth(GridData gridData, 
            final Control control) {
        
        gridData.widthHint = Dialog.convertWidthInCharsToPixels(
                LayoutUtil.getFontMetrics(control), LayoutUtil.WIDTH);
        
        if (control instanceof Text) {
            final Text textField = (Text)control;
            setVisibilityToolTip(textField, textField.getText());
            textField.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    setVisibilityToolTip(textField, textField.getText());
                }
            });
        } else if (control instanceof Combo) {
            final Combo combo = (Combo)control;
            setVisibilityToolTip(combo, combo.getText());
            combo.addModifyListener(new ModifyListener() {
                public void modifyText(ModifyEvent event) {
                    setVisibilityToolTip(combo, combo.getText());
                }
            });
        }
        
    }
    
    /**
     * Sets the tooltip for a control if its text does not fit within its
     * bounds. Otherwise clears the tooltip for the control.
     * 
     * @param control The control for which to set/clear the tooltip.
     * @param text The text in the control.
     */
    private static void setVisibilityToolTip(Control control, String text) {
        int width = Dialog.convertWidthInCharsToPixels(
                LayoutUtil.getFontMetrics(control), text.length());
        if (width > control.getBounds().width) {
            control.setToolTipText(text);
        } else {
            control.setToolTipText(null);
        }
    }
    
    /**
     * creates a small skip for GridLayout 
     * @param parent the composite to creat the skip in
     * @param numColumns the number of the columns
     * @return a control representing a small skip
     */
    public static Control createGridSmallSkip(Composite parent,
            int numColumns) {
        
        Label result = new Label(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.heightHint = SMALL_VERTICAL_SPACING;
        result.setLayoutData(gd);
        return result;
    }
    
    /**
     * creates a big skip for GridLayout
     * @param parent the composite to creat the skip in
     * @param numColumns the number of the columns
     * @return a control representing a big skip
     */
    public static Control createGridBigSkip(Composite parent, int numColumns) {
        Label result = new Label(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.horizontalSpan = numColumns;
        gd.heightHint = BIG_VERTICAL_SPACING;
        result.setLayoutData(gd);
        return result;
    }
    
    /**
     * creates a GridData for labels with default layout
     * @return a new instance of GridData
     */
    public static GridData createDefaultLabelGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = LABEL_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1; 
        return result;
    }
     
    /**
     * creates a GridData for labels in a GridLayout with more than two columns
     *  @return a new instance of GridData
     */
    public static GridData createMultiColumnLabelGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = MULTI_COLUMN_LABEL_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1; 
        result.horizontalIndent = BIG_HORIZONTAL_SPACING;

        return result;
    }
    
    /**
     * creates a GridData for labels which are aligned at the top of the cell
     * <br>
     * use it for labels for control which take more space as usual, e.g. a multi line textfield.
     * @return a new instance of GridData
     */
    public static GridData createTopLabelGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = LABEL_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = SWT.BEGINNING;
        result.horizontalSpan = 1;
        result.verticalSpan = 1; 
        return result;
    }
    
    /**
     * creates a GridData for textfields with default layout
     * @return a new instance of GridData
     */
    public static GridData createDefaultTextGridData() {
        return createDefaultTextGridData(1);
    }
     
    /**
     * creates a GridData for textfields with default layout (fills one column)
     * @param numColumns number of columns to span
     * @return a new instance of GridData
     */
    public static GridData createDefaultTextGridData(int numColumns) {
        GridData result = createTextGridData();
        result.horizontalSpan = numColumns; 
        return result;
    }
    
    /**
     * creates a GridData for textfields with default layout, give a number of expected characters as a hint
     * @param control the m_text control, used to determine the FontMetrics
     * @param numChars number of characters textfield should contain
     * @return a new instance of GridData
     */
    public static GridData createDefaultTextGridData(Control control,
            int numChars) {
        
        GridData result = new GridData();
        result.horizontalAlignment = TEXT_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1;
        result.widthHint = Dialog.convertWidthInCharsToPixels(
                getFontMetrics(control), numChars); 
        return result;
    } 
    
    /**
     * creates a GridData for multi line textfields, give a number of lines as a hint
     * @param control the m_text control, used to determine the FontMetrics
     * @param numLines number of lines the textfield should contain
     * @return a new instance of GridData
     */
    public static GridData createMultiLineTextGridData(Control control,
            int numLines) {
        GridData result = new GridData();
        result.horizontalAlignment = MULTI_LINE_TEXT_HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.horizontalSpan = 1;
        result.verticalSpan = 1;
        result.heightHint = Dialog.convertHeightInCharsToPixels(
                getFontMetrics(control), numLines); 
        return result;
    }
         
    /** creates a m_text grid data for textfields
     * @return a new instance of GridData
     */
    private static GridData createTextGridData() {
        GridData result = new GridData();
        result.horizontalAlignment = HORIZONTAL_ALIGNMENT;
        result.verticalAlignment = VERTICAL_ALIGNMENT;
        result.verticalSpan = 1;
        return result;
    }
    
    /**
     * returns the FontMetrics of <code>control</code> 
     * @param control the control to get the font metrics from
     * @return the FontMetrics
     */
    public static FontMetrics getFontMetrics(Control control) {
        GC gc = new GC(control);
        gc.setFont(control.getFont());
        FontMetrics fontMetrics = gc.getFontMetrics();
        gc.dispose();
        return fontMetrics;
    }
    
    /**
     * @param area The composite.
     * creates a separator
     */
    public static void createSeparator(Composite area) {
        Label label = new Label(area, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = true;
        gridData.horizontalAlignment = GridData.FILL;
        label.setLayoutData(gridData);
    }
}