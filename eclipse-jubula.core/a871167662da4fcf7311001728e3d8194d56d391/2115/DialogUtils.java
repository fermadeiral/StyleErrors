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
import org.eclipse.jubula.client.ui.Plugin;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.tools.internal.constants.SwtToolkitConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;


/**
 * @author BREDEX GmbH
 * @created Apr 16, 2008
 */
public final class DialogUtils {
    /**
     * flag to indicate to keep the original size
     */
    public static final float KEEP_ORIG_SIZE = -1;
    
    /**
     * the type of size to adjust
     */
    public enum SizeType {
        /**
         * the actual size 
         */
        SIZE,
        /**
         * the shells bounds
         */
        BOUNDS
    }

    /** SWT modal constant */
    private static final int MODAL = 
        (SWT.PRIMARY_MODAL | SWT.APPLICATION_MODAL | SWT.SYSTEM_MODAL);

    /** private constructor */
    private DialogUtils() {
        // utility class
    }
    
    /**
     * Sets the technical widget name for the given dialog in the corresponding 
     * parent shell, if that shell exists and is modal.
     * Otherwise nothing is done.
     * NOTE: This method must be called after create() was called on the dialog.
     *       The widget name cannot be set in the dialog itself.
     * @param dialog the modal dialog for that the wigdet name has to be set in
     *               the corresponding parent shell
     */
    public static void setWidgetNameForModalDialog(Dialog dialog) {
        final Shell parentShell = dialog.getShell();
        
        if ((parentShell != null) && ((parentShell.getStyle() & MODAL) > 0)) {
            setWidgetName(parentShell, getShortClassName(dialog.getClass()));
        }
    }
    
    /**
     * Sets the technical widget name for the given widget. If the widget 
     * already has a technical name, it will be overwritten. 
     * 
     * @param widget The widget for which to set the name. If <code>null</code>
     *               or disposed, no name will be set.
     * @param name The technical name to use.
     */
    public static void setWidgetName(Widget widget, String name) {
        if (widget != null && !widget.isDisposed()) {
            widget.setData(
                    SwtToolkitConstants.WIDGET_NAME, name);
        }
    }

    /**
     * Returns the short class name (that is everything after the last '.').
     * @param classObj the class for that the short name has to be returned
     * @return the short class name
     */
    private static String getShortClassName(Class<?> classObj) {
        final String longClassName = String.valueOf(classObj);
        final int indexOfLastDot = longClassName.lastIndexOf('.');
        final String shortClassName;
        
        if (indexOfLastDot >= 0) {
            shortClassName = longClassName.substring(indexOfLastDot + 1);
        } else {
            shortClassName = longClassName;
        }
        
        return shortClassName;
    }
    
    /**
     * @param shell
     *            the shell which size to adjust
     * @param relWidth
     *            the relative width factor; must be 0 <= relWidth <= 1
     * @param relHeight
     *            the relative height factor; must be 0 <= relWidth <= 1
     * @param sizeType the size type to adjust
     */
    public static void adjustShellSizeRelativeToClientSize(Shell shell,
            float relWidth, float relHeight, SizeType sizeType) {
        adjustShellSizeRelativeToRectangleSize(shell, relWidth, relHeight,
                PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell()
                    .getBounds(), sizeType);
    }
    
    /**
     * @param shell
     *            the shell which size to adjust
     * @param relWidth
     *            the relative width factor; must be 0 <= relWidth <= 1
     * @param relHeight
     *            the relative height factor; must be 0 <= relWidth <= 1
     */
    public static void adjustShellSizeRelativeToDisplaySize(Shell shell,
            float relWidth, float relHeight) {
        Display display = Display.getCurrent();
        Rectangle r = display.getClientArea();
        adjustShellSizeRelativeToRectangleSize(shell, relWidth, relHeight, r,
                SizeType.SIZE);
    }
    
    /**
     * @param shell
     *            the shell which size to adjust
     * @param relWidth
     *            the relative width factor; must be 0 <= relWidth <= 1
     * @param relHeight
     *            the relative height factor; must be 0 <= relWidth <= 1
     * @param relativeTo
     *            the rectangle the size should be computed relative to
     * @param sizeType the size type to adjust 
     */
    private static void adjustShellSizeRelativeToRectangleSize(Shell shell,
        float relWidth, float relHeight, Rectangle relativeTo, 
        SizeType sizeType) {
        int dWidth = relativeTo.width;
        int dHeight = relativeTo.height;

        int newShellWidth = 0;
        int newShellHeight = 0;
        
        if (relWidth == KEEP_ORIG_SIZE) {
            newShellWidth = shell.getBounds().width;
        } else {
            newShellWidth = Math.round(relWidth * dWidth);
        }
        
        if (relHeight == KEEP_ORIG_SIZE) {
            newShellHeight = shell.getBounds().height;
        } else {
            newShellHeight = Math.round(relHeight * dHeight);
        }
        switch (sizeType) {
            case SIZE:
                shell.setSize(newShellWidth, newShellHeight);
                break;
            case BOUNDS:
                break;
            default:
                break;
        }
        int x = ((dWidth - shell.getSize().x) / 2) + relativeTo.x;
        int y = ((dHeight - shell.getSize().y) / 2) + relativeTo.y;
        switch (sizeType) {
            case SIZE:
                shell.setLocation(x, y);
                break;
            case BOUNDS:
                shell.setBounds(x, y, newShellWidth, newShellHeight);
                break;
            default:
                break;
        }
    }
    
    /**
     * @param parent
     *            the parent to use
     * @param linkText
     *            the link text
     * @return a new link instance
     */
    public static Link createLinkToSecureStoragePreferencePage(
            Composite parent, String linkText) {
        Link l = new Link(parent, SWT.NONE);
        l.setText(linkText);
        l.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                final String prefPageID = 
                        Constants.SECURE_STORAGE_PLUGIN_ID;
                PreferencesUtil.createPreferenceDialogOn(
                        Plugin.getDefault().getWorkbench().getDisplay()
                        .getActiveShell(), prefPageID,
                        new String[] { prefPageID }, null,
                        PreferencesUtil.OPTION_FILTER_LOCKED).open();
            }
            
            public void widgetDefaultSelected(SelectionEvent e) {
                /** do nothing */
            }
        });
        return l;
    }
}
