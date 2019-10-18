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
package org.eclipse.jubula.client.ui.rcp.provider;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;


/**
 * HelperClass to decorate controls with control decorations
 *
 * @author BREDEX GmbH
 * @created May 5, 2009
 */
public class ControlDecorator extends ControlDecoration {

    /**
     * @param control The component for attaching the bobble.
     * @param position The position of the bobble.
     */
    public ControlDecorator(Control control, int position) {
        super(control, position);
    }

    /**
     * The following SWTException can be thrown:
     * <ul>
     *    <li>ERROR_WIDGET_DISPOSED - if the receiver has been disposed</li>
     *    <li>ERROR_THREAD_INVALID_ACCESS - if not called from the thread that created the receiver</li>
     * </ul>
     * @param isVisible True, if the control is visible, otherwise the control
     *        is hidden.
     */
    public void setVisible(boolean isVisible) {
        if (isVisible) {
            show();
        } else {
            hide();
        }
    }

    /**
     * @param control The control for attaching the bobble.
     * @param position The position of the bobble.
     * @param message The message to show in the bobble.
     * @param imageID The imageID of the image attached to the control;
     * see FieldDecorationRegistry constants for these ids
     * @param showOnFocus set to true shows the bobble only if control has focus,
     * avoid setting this parameter to true if the control can not gain any focus
     * e.g. SWT.NO_FOCUS
     * @return The created control decoration specified by the given parameters.
     */
    protected static ControlDecorator createDecoration(
            Control control, int position,
            String message, String imageID, boolean showOnFocus) {
        ControlDecorator infoBobbles =
            new ControlDecorator(control, position);
        infoBobbles.setDescriptionText(message);
        infoBobbles.setImage(FieldDecorationRegistry.getDefault()
                .getFieldDecoration(imageID).getImage());
        infoBobbles.setMarginWidth(2);
        infoBobbles.setShowOnlyOnFocus(showOnFocus);
        return infoBobbles;
    }

    /**
     * 
     * @param control that should be decorated with an info-bobble
     * @param message The message to show in the info bobble.
     * @param showfocus set to true shows the info-bobble only if control has focus,
     * avoid setting this parameter to true if the control can not gain any focus
     * e.g. SWT.NO_FOCUS
     */
    public static void createInfo(Control control,
            String message, boolean showfocus) {
        createDecoration(control, SWT.TRAIL, message,
                FieldDecorationRegistry.DEC_INFORMATION, showfocus);
    }

    /**
     * @param control The control for adding the new warning icon for the bobble.
     * @param message The message to show in the warning bobble.
     * @return The created warning control decoration defined by the given parameters.
     */
    public static ControlDecoration createWarning(Control control,
            String message) {
        return createWarning(control, SWT.LEAD, message);
    }

    /**
     * @param control The control for adding the new warning icon for the bobble.
     * @param position The position for the new warning icon: SWT.TRAIL, SWT.LEAD.
     * @param message The message to show in the warning bobble.
     * @return The created warning control decoration defined by the given parameters.
     */
    public static ControlDecorator createWarning(Control control,
            int position, String message) {
        return createDecoration(control, position, message,
                FieldDecorationRegistry.DEC_WARNING, false);
    }

    /**
     * @param control The control.
     * @param message The warning decoration text.
     * @return The warning control decoration added to the given control.
     */
    public static ControlDecorator addWarningDecorator(
            Control control, String message) {
        GridData grid = new GridData(GridData.BEGINNING, GridData.CENTER,
                false, false, 1, 1);
        //grid.horizontalIndent = 10;
        control.setLayoutData(grid);
        ControlDecorator warningDecoration = ControlDecorator.createWarning(
                control, SWT.TRAIL, message);
        return warningDecoration;
    }


}
