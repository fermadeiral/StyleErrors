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

import java.util.Map;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.ui.controllers.propertysources.IPropertyController;
import org.eclipse.jubula.client.ui.rcp.controllers.ContentAssistCellEditor;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.AbstractNodePropertySource.AbstractParamValueController;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamText;
import org.eclipse.jubula.client.ui.rcp.widgets.ParamProposalProvider;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 20.11.2007
 */
public class ParamComboPropertyDescriptor extends
    PropertyDescriptor implements IVerifiable {
    
    /** validator for parameter value validation */
    private IParamValueValidator m_dataValidator;

    /** values to use as content proposals */
    private Map<String, String> m_proposals;
    
    /**
     * Constructor
     * @param id The associated property controller
     * @param displayName  the name to display for the property
     * @param valuesArray the list of possible values to display in the combo box
     * @param validator for parameter value validation
     */
    public ParamComboPropertyDescriptor(IPropertyController id,
        String displayName, Map<String, String> valuesArray,
        IParamValueValidator validator) {
        super(id, displayName);
        m_dataValidator = validator;
        m_proposals = valuesArray;
    }
    
    /**
     * {@inheritDoc}
     */
    public CellEditor createPropertyEditor(Composite parent) {
        AbstractParamValueController contr = 
            (AbstractParamValueController)getId();
        return new ContentAssistCellEditor(
                parent, new ParamProposalProvider(m_proposals, 
                        contr.getParamNode(), contr.getParamDesc()), 
                new CheckedParamText.StringTextValidator(
                        contr.getParamNode(), contr.getParamDesc(), 
                        getDataValidator()), 
                ContentProposalAdapter.PROPOSAL_INSERT);
    }
    
    /** {@inheritDoc}
     * @see org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.IVerifiable#getDataValidator()
     */
    public IParamValueValidator getDataValidator() {
        return m_dataValidator;
    }
}
