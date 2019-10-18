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
package org.eclipse.jubula.client.ui.rcp.editors;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jubula.client.ui.constants.IconConstants;
import org.eclipse.jubula.client.ui.i18n.Messages;
import org.eclipse.jubula.client.ui.utils.ErrorHandlingUtil;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;


/**
 * Read-only text viewer.
 *
 * @author BREDEX GmbH
 * @created Feb 9, 2007
 */
public class LogViewer extends EditorPart {
    /**
     * The ID of this viewer
     */
    public static final String ID = "org.eclipse.jubula.client.ui.rcp.editors.LogViewer"; //$NON-NLS-1$
    
    /** the text field for this viewer */
    private Text m_text = null;
    
    /**
     * {@inheritDoc}
     */
    public void doSave(IProgressMonitor monitor) {
        doSaveAs();
    }

    /**
     * {@inheritDoc}
     */
    public void doSaveAs() {
        // Not supported, but could be added later
    }

    /**
     * {@inheritDoc}
     */
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        if (input != null) {
            setSite(site);
            setInput(input);
            setPartName(input.getName());
        } else {
            String msg = Messages.EditorInitCreateError;
            throw new PartInitException(msg);
        }

    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirty() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isSaveAsAllowed() {
        // Not supported, but could be added later
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        if (getEditorInput() instanceof ISimpleEditorInput) {
            ISimpleEditorInput input = (ISimpleEditorInput)getEditorInput();
            if (input instanceof ClientLogInput) {
                setTitleImage(IconConstants.ITE_LOG_VIEW);
            } else if (input instanceof ServerLogInput) {
                setTitleImage(IconConstants.RC_LOG_VIEW);
            }
            try {
                m_text = new Text(
                    parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL);
                m_text.setText(input.getContent());
            } catch (CoreException ce) {
                ErrorHandlingUtil.createMessageDialog(
                        MessageIDs.E_CANNOT_OPEN_EDITOR);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public void setFocus() {
        if (m_text != null) {
            m_text.setFocus();
        }
    }

}
