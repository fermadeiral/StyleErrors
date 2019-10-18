/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.extensions.wizard.utils;

import org.apache.commons.lang.SystemUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;

/**
 * Provides several handy methods that are used throughout the wizard.
 * 
 * @author BREDEX GmbH
 */
public class Tools {
    
    /**
     * Private constructor, Tools should not be instantiated.
     */
    private Tools() {
        // Should not be instantiated
    }

    /**
     * Parses the given string to camel case
     * @param name the string to be parsed
     * @return the camel cased string
     */
    public static String getCamelCase(String name) {
        String[] parts = name.trim().split("\\s"); //$NON-NLS-1$
        StringBuilder sb = new StringBuilder();

        sb.append(parts[0].toLowerCase());
        for (int i = 1; i < parts.length; i++) {
            if (!parts[i].equals("")) { //$NON-NLS-1$
                sb.append(getLaterPartCase(parts[i]));
            }
        }

        return sb.toString();
    }

    /**
     * Capitalizes the string
     * @param string the string to be capitalized
     * @return the capitalized string
     */
    private static String getLaterPartCase(String string) {
        return string.substring(0, 1).toUpperCase() 
                + string.substring(1).toLowerCase();
    }

    /**
     * Checks whether the given string is a valid Java identifier.
     * 
     * @param string
     *            the String whose validity as a Java identifier should be
     *            checked
     * @return <code>true</code> if the given string is a valid Java identifier,
     *         <br>
     *         <code>false</code> otherwise
     */
    public static boolean isJavaIdentifier(String string) {
        char[] splittedString = string.toCharArray();
        if (string.length() == 0 | !Character
                .isJavaIdentifierStart(splittedString[0])) {
            return false;
        }
        for (char c : splittedString) {
            if (!Character.isJavaIdentifierPart(c)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates an info dongle that will be attached to the right side of the
     * given control. On Windows and on macOS, a ControlDecoration will be used.
     * On other OS, a CLabel with an attached Tooltip will be used.
     * OffsetH and OffsetV will be ignored on Windows.
     * 
     * @param attached
     *            the control the dongle should be attached to
     * @param message
     *            the tooltip message of the dongle
     * @param offsetH
     *            the horizontal offset from the right edge of the control
     * @param offsetV
     *            the vertical offset from the control
     * @return the created info dongle
     */
    public static Object createInfo(Control attached, String message, 
            int offsetH, int offsetV) {
        
        if (SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_MAC) {
            return createControlDecorationInfo(attached, message,
                    offsetH, offsetV);
        }
        return createLabelInfo(attached, message, offsetH, offsetV);
    }

    /**
     * Creates a control decoration for Windows and macOS.
     * @param attached
     *            the control the dongle should be attached to
     * @param message
     *            the tooltip message of the dongle
     * @param offsetH
     *            the horizontal offset from the right edge of the control
     * @param offsetV
     *            the vertical offset from the control
     * @return the created control decoration
     */
    private static ControlDecoration createControlDecorationInfo(
            Control attached, String message, int offsetH, int offsetV) {
        
        ControlDecoration decoration = new ControlDecoration(attached,
                SWT.RIGHT);
        decoration.setImage(FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
                .getImage());
        decoration.setDescriptionText(message);
        return decoration;
    }

    /**
     * Creates a CLabel for other OS.
     * @param attached
     *            the control the dongle should be attached to
     * @param message
     *            the tooltip message of the dongle
     * @param offsetH
     *            the horizontal offset from the right edge of the control
     * @param offsetV
     *            the vertical offset from the control
     * @return the created CLabel
     */
    private static CLabel createLabelInfo(Control attached, String message, 
            int offsetH, int offsetV) {
        
        CLabel info = new CLabel(attached.getParent(), SWT.NONE);
        FormData fdInfo = new FormData();
        fdInfo.left = new FormAttachment(attached, offsetH, SWT.RIGHT);
        fdInfo.top = new FormAttachment(attached, offsetV, SWT.TOP);
        info.setLayoutData(fdInfo);
        info.setImage(FieldDecorationRegistry.getDefault()
                .getFieldDecoration(FieldDecorationRegistry.DEC_INFORMATION)
                .getImage());
        
        final ToolTip tooltip = new ToolTip(info.getShell(), SWT.NONE);
        tooltip.setText(message);
        tooltip.setAutoHide(false);
        
        info.addListener(SWT.MouseHover, new Listener() {
            @Override
            public void handleEvent(Event event) {
                tooltip.getDisplay().timerExec(500, new Runnable() {
                    @Override
                    public void run() {
                        tooltip.setVisible(true);
                    }
                });
            }
        });
        
        info.addListener(SWT.MouseExit, new Listener() {
            @Override
            public void handleEvent(Event event) {
                tooltip.getDisplay().timerExec(300, new Runnable() {
                    @Override
                    public void run() {
                        tooltip.setVisible(false);
                    }
                });
            }
        });

        return info;
    }

    /**
     * Opens the given workspace file in the editor or does nothing if the file
     * could not be found.
     * @param filename the file to be opened
     */
    public static void openFileInEditor(String filename) {
        IFile file = (IFile) ResourcesPlugin.getWorkspace().getRoot()
                .findMember(filename);
        if (file != null) {
            IWorkbenchPage page = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getActivePage();
            IEditorDescriptor desc = PlatformUI.getWorkbench()
                    .getEditorRegistry().getDefaultEditor(file.getName());
            try {
                page.openEditor(new FileEditorInput(file), desc.getId());
            } catch (PartInitException e) {
                e.printStackTrace();
            }
        }
    }
}
