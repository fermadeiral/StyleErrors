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
package org.eclipse.jubula.client.ui.rcp.dialogs;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jubula.client.ui.rcp.Plugin;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Shell;


/**
 * @author BREDEX GmbH
 * @created Apr 29, 2008
 */
public final class NagDialog extends MessageDialogWithToggle {

    
    /** reference to help */
    private String m_helpId;
    
    /**
     * create a user info dialog
     * 
     * @param msgText I18N text of the message
     * @param helpId references the specific help instance
     * @param parentShell the shell to be used as a parent
     */
    private NagDialog(String msgText, String helpId, Shell parentShell) {
        super(parentShell, 
                Messages.InfoNaggerDialogTitle,
                null, 
                msgText, MessageDialog.INFORMATION,
                new String[] { IDialogConstants.HELP_LABEL,
                    IDialogConstants.PROCEED_LABEL 
                }, 
                0, 
                Messages.InfoNaggerDialogToggleMsg,
                false);
        m_helpId = helpId;
        this.setShellStyle(SWT.TITLE | SWT.BORDER | SWT.APPLICATION_MODAL);
        // setBlockOnOpen has to be true for using NagDialog in 
        // AUTConfigPropertiesDialog and NewProjectWizard
        this.setBlockOnOpen(true);        
    }
    
    /**
     * create a user info dialog
     * 
     * @param parentShell the shell to be used as a parent
     * @param msgKey I18N key of the message
     * @param helpId references the specific help instance
     */
    private NagDialog(Shell parentShell, String msgKey, String helpId) {
        this(I18n.getString(msgKey), helpId, parentShell);
    }

    /**
     * create a user info dialog
     * 
     * @param parentShell the shell to be used as a parent or null for the
     * plug-ins default shell.
     * @param msgKey I18N key of the message
     * @param helpId references the specific help instance
     */
    public static void runNagDialog(Shell parentShell,
            final String msgKey, final String helpId) {        
        String prefKey = "InfoNagger." + msgKey; //$NON-NLS-1$
        IPreferenceStore preferenceStore = 
            Plugin.getDefault().getPreferenceStore();
        if (!preferenceStore.getString(prefKey).equals(
                MessageDialogWithToggle.ALWAYS)) {
            NagDialog dialog = new NagDialog(parentShell, msgKey, helpId);
            dialog.setPrefStore(preferenceStore);
            dialog.setPrefKey(prefKey);
            dialog.open();
        }
    }

    /**
     * {@inheritDoc}
     */
    protected void buttonPressed(int buttonId) {        
        if (buttonId == IDialogConstants.HELP_ID) {
            Plugin.getHelpSystem().displayHelp(m_helpId);
        } else {            
            super.buttonPressed(buttonId);
        }
    }

    /**
     * {@inheritDoc}
     */
    public void create() {
        super.create();
        Plugin.getHelpSystem().setHelp(this.getButton(0), m_helpId);
    }
}
