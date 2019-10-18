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
package org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors;

import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jubula.client.ui.rcp.controllers.ContentAssistCellEditor;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText.IValidator;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * Descriptor for a property that has a value which should be edited with a 
 * text cell editor. This text cell editor may have content assist and/or 
 * validation (both are optional).
 * 
 * @author BREDEX GmbH
 * @created Jul 20, 2010
 */
public class ContentAssistedTextPropertyDescriptor extends PropertyDescriptor {

    /** responsible for content assist for created editor(s) */
    private IContentProposalProvider m_contentProposalProvider;
    
    /** responsible for validation for created editor(s) */
    private IValidator m_validator;

    /**
     * The integer style that indicates how an accepted proposal affects the 
     * control's content. 
     * See {@link org.eclipse.jface.fieldassist.ContentProposalAdapter#setProposalAcceptanceStyle(int)}.
     */
    private int m_proposalAcceptanceStyle;
    
    /**
     * 
     * @param id The ID of the property.
     * @param displayName The name to display for the property.
     * @param contentProposalProvider The content proposal provider to assign 
     *                                to created editor(s). May be 
     *                                <code>null</code>, in which case no 
     *                                content assist support will be added. 
     * @param validator The validator to assign to created editor(s). May be 
     *                  <code>null</code>, in which case no validation will be 
     *                  performed.
     * @param proposalAcceptanceStyle
     *         The integer style that indicates how an accepted proposal 
     *         affects the control's content. See 
     *         {@link org.eclipse.jface.fieldassist.ContentProposalAdapter#setProposalAcceptanceStyle(int)}.
     */
    public ContentAssistedTextPropertyDescriptor(Object id, String displayName, 
            IContentProposalProvider contentProposalProvider, 
            IValidator validator, int proposalAcceptanceStyle) {
        super(id, displayName);
        m_contentProposalProvider = contentProposalProvider;
        m_validator = validator;
        m_proposalAcceptanceStyle = proposalAcceptanceStyle;
    }

    /**
     * {@inheritDoc}
     */
    public CellEditor createPropertyEditor(Composite parent) {
        return new ContentAssistCellEditor(parent, 
                m_contentProposalProvider, m_validator, 
                m_proposalAcceptanceStyle);
    }
}
