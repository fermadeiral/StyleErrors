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
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalListener2;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.ui.rcp.utils.ContentAssistUtil;
import org.eclipse.swt.widgets.Composite;


/**
 * @author BREDEX GmbH
 * @created Feb 5, 2009
 */
public class CheckedParamTextContentAssisted extends CheckedParamText {
    /**
     * @author BREDEX GmbH
     * @created Dec 8, 2008
     */
    private final class IContentProposalListener2Implementation implements
            IContentProposalListener2 {
        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void proposalPopupClosed(ContentProposalAdapter adapter) {
            m_popupOpen = false;
        }

        /**
         * {@inheritDoc}
         */
        @SuppressWarnings("synthetic-access")
        public void proposalPopupOpened(ContentProposalAdapter adapter) {
            m_popupOpen = true;
        }
    }

    /** is this content assistent active? */
    private boolean m_popupOpen = false;


    /** controller for popupOpen */
    private IContentProposalListener2Implementation m_popupListener;
    
    /**
     * 
     * @param parent parent
     * @param style style
     * @param node current node, associated with parameter for this value
     * @param desc parameter description associated with current edited value
     * @param dataValidator validator for data validation
     * @param proposalProvider The content proposal provider for this text
     *                         field, or <code>null</code> if no content
     *                         assist is available.
     */
    @SuppressWarnings("synthetic-access")
    public CheckedParamTextContentAssisted(Composite parent, int style,
            IParameterInterfacePO node, IParamDescriptionPO desc,
            IParamValueValidator dataValidator, 
            IContentProposalProvider proposalProvider) {
        super(parent, style, node, desc, dataValidator);
        
        m_popupListener = new IContentProposalListener2Implementation();

        ContentProposalAdapter cpa = new ContentProposalAdapter(this,
                new TextContentAdapter(), 
                proposalProvider, 
                ContentAssistUtil.getTriggerKeyStroke(), 
                ContentAssistUtil.getTriggerChars());
        cpa.setFilterStyle(ContentProposalAdapter.FILTER_NONE);
        cpa.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_INSERT);
        cpa.addContentProposalListener(m_popupListener);
    }

    /**
     * @return the popupOpen
     */
    public boolean isPopupOpen() {
        return m_popupOpen;
    }
}
