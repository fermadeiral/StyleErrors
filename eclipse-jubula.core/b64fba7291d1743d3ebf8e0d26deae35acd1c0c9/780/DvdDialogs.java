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
package org.eclipse.jubula.examples.aut.dvdtool.gui;

import java.awt.Component;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JOptionPane;

import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This class is used for displaying a (modal) message dialog.
 *
 * @author BREDEX GmbH
 * @created 13.04.2005
 */
public class DvdDialogs {

    /** constant for yes */
    public static final int YES = JOptionPane.YES_OPTION;

    /** constant for no */
    public static final int NO = JOptionPane.NO_OPTION;

    /** constant for cancel */
    public static final int CANCEL = JOptionPane.CANCEL_OPTION;
    
    /**
     * private constructor, use static methods
     */
    private DvdDialogs() {
        // empty
    }
    
    /**
     * display a confirmation dialog with internationalized messages from <code>keys</code>
     * the dialog display an OK - and a CANCEL - Button
     * @param parent the parent for the dialog
     * @param titleKey the key for the title 
     * @param keys the resource key(s) of the message(s) to display, must be Strings
     * @return true if the user selected ok, false otherwise
     */
    public static boolean confirm2(Component parent, String titleKey, 
        List keys) {
        
        List<String> message = new Vector<String>();
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            message.add(Resources.getString((String) iter.next()));
        }
        
        return JOptionPane.showConfirmDialog(
                parent, 
                message.toArray(new Object[message.size()]),
                Resources.getString(titleKey),
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION;
    }
    
    /**
     * display a confirmation dialog with internationalized messages from <code>keys</code>
     * the dialog display an YES -, NO -, and a CANCEL - Button
     * @param parent the parent for the dialog
     * @param titleKey the key for the title 
     * @param keys the resource key(s) of the message(s) to display, must be Strings
     * @return the chosen button, see constants defined in this class
     */
    public static int confirm3(Component parent, String titleKey, 
        List keys) {
        
        List<String> message = new Vector<String>();
        for (Iterator iter = keys.iterator(); iter.hasNext();) {
            message.add(Resources.getString((String) iter.next()));
        }
        
        return JOptionPane.showConfirmDialog(
                parent, 
                message.toArray(new Object[message.size()]),
                Resources.getString(titleKey),
                JOptionPane.YES_NO_CANCEL_OPTION,
                JOptionPane.QUESTION_MESSAGE);
    }
    /**
     * displays an error message with an internationalized message 
     * @param parent the parent for the dialog
     * @param key the resource key of the message to display 
     */
    public static void showError(Component parent, String key) {
        JOptionPane.showMessageDialog(
                parent, 
                Resources.getString(key),
                Resources.getString("dialog.error.title"), //$NON-NLS-1$
                JOptionPane.ERROR_MESSAGE);
    }
    
    /**
     * displays an information message with an internationalized message 
     * @param parent the parent for the dialog
     * @param key the resource key of the message to display 
     */
    public static void showMessage(Component parent, String key) {
        JOptionPane.showMessageDialog(
                parent, 
                Resources.getString(key),
                Resources.getString("dialog.message.title"), //$NON-NLS-1$
                JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * displays an input dialog with an internationalized message 
     * @param parent the parent for the dialog
     * @param key the resource key of the message to display 
     * @return users input, or <code>null</code> meaning the user
     *          cancelled the input
     */
    public static String getInput(Component parent, String key) {
        return JOptionPane.showInputDialog(parent,
                Resources.getString(key),
                Resources.getString("dialog.input.title"), //$NON-NLS-1$
                JOptionPane.QUESTION_MESSAGE);
    }
}
