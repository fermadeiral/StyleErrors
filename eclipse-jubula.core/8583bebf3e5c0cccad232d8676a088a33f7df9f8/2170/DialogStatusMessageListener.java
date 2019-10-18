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
package org.eclipse.jubula.client.ui.databinding;

import org.eclipse.core.databinding.observable.value.IValueChangeListener;
import org.eclipse.core.databinding.observable.value.ValueChangeEvent;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.TitleAreaDialog;

/**
 * Sets the error message of a dialog, based on an changes to an 
 * <code>IStatus</code> value.
 *
 * @author BREDEX GmbH
 * @created Dec 16, 2008
 */
public class DialogStatusMessageListener implements IValueChangeListener {
    
    /** the dialog whose error message should be updated */
    private TitleAreaDialog m_dialog;
    
    /**
     * Constrcutor
     * 
     * @param dialog The dialog whose error message should be updated.
     */
    public DialogStatusMessageListener(TitleAreaDialog dialog) {
        m_dialog = dialog;
    }
    
    /**
     * 
     * {@inheritDoc}
     */
    public void handleValueChange(ValueChangeEvent event) {
        IStatus status = (IStatus)event.diff.getNewValue();
        m_dialog.setErrorMessage(null);
        if (status.getSeverity() == IStatus.ERROR) {
            m_dialog.setErrorMessage(status.getMessage());
        }
    }
}
