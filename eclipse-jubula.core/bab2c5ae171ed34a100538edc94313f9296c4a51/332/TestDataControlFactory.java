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
package org.eclipse.jubula.client.ui.rcp.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.eclipse.jubula.client.core.businessprocess.AbstractParamInterfaceBP;
import org.eclipse.jubula.client.core.businessprocess.CapBP;
import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParamNodePO;
import org.eclipse.jubula.client.core.model.IParamValueSetPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.model.ITcParamDescriptionPO;
import org.eclipse.jubula.client.core.model.ITestDataCubePO;
import org.eclipse.jubula.client.core.model.IValueCommentPO;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.jubula.client.core.utils.StringHelper;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.ParamComboPropertyDescriptor;
import org.eclipse.jubula.client.ui.rcp.controllers.propertydescriptors.ParamTextPropertyDescriptor;
import org.eclipse.jubula.client.ui.rcp.controllers.propertysources.AbstractNodePropertySource.AbstractParamValueController;
import org.eclipse.jubula.client.ui.rcp.i18n.Messages;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamText;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedParamTextContentAssisted;
import org.eclipse.jubula.client.ui.rcp.widgets.ParamProposalProvider;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TestDataConstants;
import org.eclipse.jubula.tools.internal.exception.Assert;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ParamValueSet;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.views.properties.PropertyDescriptor;


/**
 * This class creates swt Controls depending on the parameters of IParamNodePOs
 * to edit the test data of the parameters
 * 
 * @author BREDEX GmbH
 * @created 19.06.2006
 */
public class TestDataControlFactory {

    /**
     * values available for Boolean parameters
     */
    public static final String[] BOOLEAN_VALUES = 
        new String[]{"true", "false"}; //$NON-NLS-1$ //$NON-NLS-2$


    /**
     * private utility constructor
     */
    private TestDataControlFactory() {
        // do nothing
    }
    
    /**
     * Creates Controls depending on the given IParamNodePO and the given
     * parameter name in dataset view.<br>
     * E.g. it returns a Text Control which only accepts Integers.
     * @param paramName the current parameter name
     * @param paramObj the current param object
     * @param parent the parent composite
     * @param style the style of the new Control
     * @return the control to edit test data
     */
    public static Control createControl(IParameterInterfacePO paramObj, 
        String paramName, Composite parent, int style) {
        
        Map<String, String> map = StringHelper.getInstance().getMap();
        String nameOfParam = paramName;
        IParamDescriptionPO paramDesc = paramObj.getParameterForName(
            nameOfParam);
        if (paramDesc == null) {
            nameOfParam = map.get(paramName);
            paramDesc = paramObj.getParameterForName(nameOfParam);
        }
        if (paramObj instanceof ICapPO) {
            ICapPO cap = (ICapPO)paramObj;
            Action action = CapBP.getAction(cap);
            List<String> values = new ArrayList<String>();
            Param param = action.findParam(paramDesc.getUniqueId());
            for (Iterator<ValueSetElement> iter = param.valueSetIterator(); iter
                    .hasNext();) {
                values.add(map.get((iter.next()).getValue()));
            }
            if (!values.isEmpty()) {
                return new CheckedParamText(parent, style, cap, paramDesc,
                        createParamValueValidator(TestDataConstants.STR,
                                param.getValueSet().isCombinable(),
                                values.toArray(new String[values.size()])));
            }
        }
        if (paramObj instanceof IParamNodePO) {
            IParamNodePO paramNode = (IParamNodePO)paramObj;
            ParamValueSet valueSet = 
                ParamTextPropertyDescriptor.getValuesSet(
                        paramNode, paramDesc.getUniqueId());
            String [] values = ParamTextPropertyDescriptor.getValues(valueSet);
            Map<String, String> valuesWithComment = 
                    ParamTextPropertyDescriptor.getValuesWithComment(valueSet);
            if (TestDataConstants.BOOLEAN.equals(paramDesc.getType())) {
                values = BOOLEAN_VALUES;
                valuesWithComment.clear();
                valuesWithComment.put(BOOLEAN_VALUES[0], StringConstants.EMPTY);
                valuesWithComment.put(BOOLEAN_VALUES[1], StringConstants.EMPTY);
            }

            return new CheckedParamTextContentAssisted(parent, style,
                    paramNode, paramDesc, 
                    createParamValueValidator(
                            paramDesc.getType(), 
                            valueSet != null ? valueSet.isCombinable() : false, 
                                    values), 
                    new ParamProposalProvider(
                            valuesWithComment, paramNode, paramDesc));
        }

        if (paramObj instanceof ITestDataCubePO) {
            ITestDataCubePO tdc = (ITestDataCubePO)paramObj;
            Map<String, String> values = getValuesFromValueSet(paramDesc);
            if (TestDataConstants.BOOLEAN.equals(paramDesc.getType())) {
                return new CheckedParamTextContentAssisted(parent, style,
                        tdc, paramDesc, 
                        createParamValueValidator(
                                paramDesc.getType(), 
                                false, BOOLEAN_VALUES), 
                        new ParamProposalProvider(BOOLEAN_VALUES,
                                null, paramDesc));
            }
            return new CheckedParamTextContentAssisted(parent, style, tdc,
                    paramDesc,
                    createParamValueValidator(paramDesc.getType(), false,
                            values.keySet().toArray(new String[values.size()])),
                    new ParamProposalProvider(values, null, paramDesc));
        }
        
        Assert.notReached(Messages.ImplementFor + StringConstants.SPACE 
                + paramObj.getClass().getName());
        return null;
    }

    /**
     * 
     * @param paramDesc the {@link IParamDescriptionPO} should be of type {@link ITcParamDescriptionPO}
     * @return the values from the {@link IParamValueSetPO}
     */
    private static Map<String, String> getValuesFromValueSet(
            IParamDescriptionPO paramDesc) {
        if (paramDesc instanceof ITcParamDescriptionPO) {
            ITcParamDescriptionPO desc = (ITcParamDescriptionPO) paramDesc;
            IParamValueSetPO valueSet = desc.getValueSet();
            List<IValueCommentPO> values = valueSet.getValues();
            if (valueSet != null && values != null && values.size() > 0) {
                return values.stream()
                        .collect(Collectors.toMap(IValueCommentPO::getValue,
                                IValueCommentPO::getComment));
            }
        }
        return new HashMap<>();
    }
    
    /**
     * Creates a PropertyDescriptor depending on the given paramValController, 
     * displayName and values array.<br>
     * The values parameter is to get a ComboBoxPropertyDescriptor if
     * array.length > 0.<br>
     * For all other PropertyDescriptors set an empty String-Array into this
     * parameter.
     * 
     * @param paramValController an AbstractParamValueController.
     * @param displayName the display name of the PropertyDescriptor
     * @param values The values parameter is to get a ComboBoxPropertyDescriptor
     *               if values are not empty<br>
     *               For all other PropertyDescriptors set an empty 
     *               String,String Map into this parameter!
     * @param valuesAreCombinable Whether combinations of the 
     *                            supplied values are allowed.
     *                            
     * @return a PropertyDescriptor
     */
    public static PropertyDescriptor createValuePropertyDescriptor(
            AbstractParamValueController paramValController,
            String displayName, Map<String, String> values,
            boolean valuesAreCombinable) {

        final String paramType = paramValController.getParamDesc().getType();
        if (!values.isEmpty()) {
            return new ParamComboPropertyDescriptor(paramValController,
                    displayName, values, createParamValueValidator(
                            TestDataConstants.STR, valuesAreCombinable, 
                            values.keySet().toArray(new String[0])));
        }
        if (TestDataConstants.BOOLEAN.equals(paramType)) {
            return new ParamTextPropertyDescriptor(paramValController,
                    displayName, createParamValueValidator(
                            TestDataConstants.STR, valuesAreCombinable, 
                            BOOLEAN_VALUES));
        }
        return new ParamTextPropertyDescriptor(paramValController,
                displayName, 
                createParamValueValidator(paramType, valuesAreCombinable));
    }

    /**
     * @param type
     *            type of parameter
     * @param valuesAreCombinable
     *            whether combinations of the supplied values are allowed
     * @param values
     *            list of possible values for a parameter
     * @return validator fit to given type
     */
    private static IParamValueValidator createParamValueValidator(String type, 
            boolean valuesAreCombinable, String... values) {
        return AbstractParamInterfaceBP.createParamValueValidator(
                type, valuesAreCombinable, values);
    }
}