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
package org.eclipse.jubula.client.ui.rcp.controllers;

import org.apache.commons.lang.Validate;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jubula.client.ui.rcp.utils.ContentAssistUtil;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText.IValidator;
import org.eclipse.swt.widgets.Composite;


/**
 * Cell editor with content assist and validation (both optional).
 * 
 * Based on JFace Snippet:
 * http://dev.eclipse.org/viewcvs/index.cgi/org.eclipse.jface.snippets/Eclipse%20JFace%20Snippets/org/eclipse/jface/snippets/viewers/Snippet060TextCellEditorWithContentProposal.java?view=markup
 * 
 * @author BREDEX GmbH
 * @created Apr 6, 2010
 */
public class ContentAssistCellEditor extends TextCellEditor {

    /** whether the content proposal popup is currently open */
    private boolean m_popupOpen = false;

    /**
     * Constructor
     * 
     * @param parent The parent control.
     * @param contentProposalProvider The proposal provider for content assist.
     *                          May be <code>null</code>, in which case the
     *                          editor will not support content assist.
     * @param validator The validator to use. May be <code>null</code>, in which
     *                  case the editor will not support validation.
     * @param proposalAcceptanceStyle
     *         The integer style that indicates how an accepted proposal 
     *         affects the control's content. See 
     *         {@link ContentProposalAdapter#setProposalAcceptanceStyle(int)}.
     */
    public ContentAssistCellEditor(Composite parent,
            IContentProposalProvider contentProposalProvider,
            IValidator validator, int proposalAcceptanceStyle) {
        super(parent);
        Validate.notNull(text);
        enableValidation(validator);
        enableContentProposal(contentProposalProvider, proposalAcceptanceStyle);
    }

    /**
     * Activates validation (if it is supported).
     * 
     * @param validator The validator.
     */
    private void enableValidation(final IValidator validator) {
        if (validator != null) {
            text.addVerifyListener(
                    new CheckedText.ValidationListener(validator));
        }
    }

    /**
     * Activates content assist (if it is supported).
     * 
     * @param contentProposalProvider
     *          The proposal provider to use for content assist.
     * @param proposalAcceptanceStyle
     *         The integer style that indicates how an accepted proposal 
     *         affects the control's content. See 
     *         {@link ContentProposalAdapter#setProposalAcceptanceStyle(int)}.
     *          
     */
    private void enableContentProposal(
            IContentProposalProvider contentProposalProvider,
            int proposalAcceptanceStyle) {
        if (contentProposalProvider != null) {
            
            ContentProposalAdapter contentProposalAdapter = 
                new ContentProposalAdapter(text,
                    new TextContentAdapter(), contentProposalProvider, 
                    ContentAssistUtil.getTriggerKeyStroke(),
                    ContentAssistUtil.getTriggerChars());
            
            contentProposalAdapter.setFilterStyle(
                    ContentProposalAdapter.FILTER_NONE);
            contentProposalAdapter.setProposalAcceptanceStyle(
                    proposalAcceptanceStyle);

            // Listen for popup open/close events to be able to handle focus events
            // correctly
            contentProposalAdapter.addContentProposalListener(
                    new IContentProposalListener2() {

                        @SuppressWarnings("synthetic-access")
                        public void proposalPopupClosed(
                                ContentProposalAdapter adapter) {
                            m_popupOpen = false;
                        }

                        @SuppressWarnings("synthetic-access")
                        public void proposalPopupOpened(
                                ContentProposalAdapter adapter) {
                            m_popupOpen = true;
                        }
                    });
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected void focusLost() {
        if (!m_popupOpen) {
            // Focus lost deactivates the cell editor.
            // This must not happen if focus lost was caused by activating
            // the completion proposal popup.
            super.focusLost();
        }
    }

    /**
     * 
     * {@inheritDoc}
     */
    protected boolean dependsOnExternalFocusListener() {
        // Always return false;
        // Otherwise, the ColumnViewerEditor will install an additional focus
        // listener
        // that cancels cell editing on focus lost, even if focus gets lost due
        // to
        // activation of the completion proposal popup. See also bug http://eclip.se/58777.
        return false;
    }

}
