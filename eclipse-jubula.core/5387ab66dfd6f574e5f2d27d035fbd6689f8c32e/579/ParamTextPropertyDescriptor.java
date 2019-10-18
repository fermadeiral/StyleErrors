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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jubula.client.core.businessprocess.TestCaseParamBP;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.ui.rcp.controllers.ContentAssistCellEditor;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.AbstractNodePropertySource.AbstractParamValueController;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamText;
import org.eclipse.jubula.client.ui.rcp.widgets.ParamProposalProvider;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ParamValueSet;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;


/**
 * @author BREDEX GmbH
 * @created 23.11.2007
 */
public class ParamTextPropertyDescriptor extends TextPropertyDescriptor 
    implements IVerifiable {
    
    /** validator for parameter value validation */
    private IParamValueValidator m_dataValidator;

    /**
      * Creates a property descriptor with the given id and display name.
     * 
     * @param id The associated property controller.
     * @param displayName The name to display for the property.
     * @param validator for parameter value validation
     */
    public ParamTextPropertyDescriptor(AbstractParamValueController id,
        String displayName, IParamValueValidator validator) {
        super(id, displayName);
        m_dataValidator = validator;
    }

    /**
     * @return Returns the validator.
     */
    public IParamValueValidator getDataValidator() {
        return m_dataValidator;
    }

    /**
     * 
     * @param valueSet Source for returned values. May be <code>null</code>.
     * @return an array of values contained in <code>valueSet</code>, which is 
     *         an empty array if <code>valueSet</code> is <code>null</code> or 
     *         empty.
     */
    public static String[] getValues(ParamValueSet valueSet) {
        if (valueSet == null) {
            return new String [0];
        }
        List<String> values = new LinkedList<String>();
        Iterator<ValueSetElement> valueSetIter = valueSet.iterator();
        while (valueSetIter.hasNext()) {
            values.add(valueSetIter.next().getValue());
        }

        return values.toArray(new String[values.size()]);
    }
    
    /**
     * @param valueSet alueSet Source for returned values. May be <code>null</code>.
     * @return a {@link Map} with values as key and comments as value
     */
    public static Map<String, String> getValuesWithComment(
            ParamValueSet valueSet) {
        Map<String, String> valueToComment = new HashMap<>();
        if (valueSet == null) {
            return valueToComment;
        }
        Iterator<ValueSetElement> valueSetIter = valueSet.iterator();
        while (valueSetIter.hasNext()) {
            ValueSetElement value = valueSetIter.next();
            valueToComment.put(value.getValue(), value.getComment());
        }
        return valueToComment;
    }

    /**
     * 
     * @param paramNode Node at which the Parameter is being examined.
     * @param paramGUID GUID of the Parameter to examine.
     * @return the value set for the given parameters. Returns 
     *         <code>null</code> if the parameters do not represent 
     *         <em>exactly</em> one value set.
     */
    public static ParamValueSet getValuesSet(IParamNodePO paramNode, 
            String paramGUID) {
        Set<Param> values = TestCaseParamBP.getValuesForParameter(
                paramNode, paramGUID);
        if (values.size() != 1) {
            return null;
        }

        Param p = values.iterator().next();

        return p.getValueSet();
    }

    /**
     * {@inheritDoc}
     */
    public CellEditor createPropertyEditor(Composite parent) {
        AbstractParamValueController contr = 
            (AbstractParamValueController)getId();
        ParamValueSet valuesSet = getValuesSet(contr.getParamNode(), 
                contr.getParamDesc().getUniqueId());
        return new ContentAssistCellEditor(
                parent, new ParamProposalProvider(
                        getValuesWithComment(valuesSet), 
                        contr.getParamNode(), contr.getParamDesc()),
                new CheckedParamText.StringTextValidator(
                        contr.getParamNode(), contr.getParamDesc(), 
                        getDataValidator()), 
                ContentProposalAdapter.PROPOSAL_INSERT);
    }

}
