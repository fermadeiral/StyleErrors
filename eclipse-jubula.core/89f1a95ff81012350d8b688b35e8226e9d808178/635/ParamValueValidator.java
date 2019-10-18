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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.utils.ParamValueConverter;
import org.eclipse.jubula.client.core.utils.TokenError;
import org.eclipse.jubula.client.ui.constants.Constants;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText.IValidator;
import org.eclipse.jubula.client.ui.rcp.widgets.CheckedText.ValidationState;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.messagehandling.MessageIDs;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 01.11.2007
 */
public class ParamValueValidator implements IValidator {
    /** node associated with parameter to validate */
    private IParameterInterfacePO m_node;
    
    /** parameter description associated with value to validate */
    private IParamDescriptionPO m_desc;
    
    /** is an empty text valid? */
    private boolean m_emptyAllowed;
    
    /**
     * @param node
     *            associated with parameter to validate
     * @param desc
     *            parameter description associated with current edited value
     * @param emptyAllowed
     *            is an empty text filed allowed?
     */
    public ParamValueValidator(IParameterInterfacePO node,
            IParamDescriptionPO desc, boolean emptyAllowed) {
        m_node = node;
        m_desc = desc;
        m_emptyAllowed = emptyAllowed;
    }
    
    /**
     * Validate the new text. It is allowed to set members of the
     * VerifyEvent, i.e. e.text and e.doit. To hilight the text field
     * the ConvValidationState is used. Please be aware that the e.doit member
     * is also set depending on the returned ConvValidationState (by anding
     * its new value, therefor setting e.doit=false in this
     * method will prevail).
     * @param e VerifyEvent to inspect
     * @return a ConvValidationState to be used for highlighting the text field.
     */
    public ValidationState validateInput(VerifyEvent e) {
        String newValue = getNewValue(e);
        ValidationState val = ValidationState.OK;
        if (StringUtils.isEmpty(newValue)) {                
            if (m_emptyAllowed) {
                val = ValidationState.EmptyAccept;
            } else {
                val = ValidationState.MightMatchAccept;
            }
        }
        return val;
    }

    /**
     * @param e event
     * @return value to validate
     */
    String getNewValue(VerifyEvent e) {
        Text txt = (Text)e.widget;
        final String oldValue = txt.getText();
        StringBuilder workValue = new StringBuilder(oldValue);
        workValue.replace(e.start, e.end, e.text);
        String newValue = workValue.toString();
        return newValue;
    }

    /**
     * @param control associated widget
     * @param conv ParamValueConverter to use for validation
     * @return state following from result of ParamValueConverter
     */
    public static ValidationState setState(Control control, 
        ParamValueConverter conv) {
        ValidationState val = ValidationState.OK;
        List<TokenError> errors = conv.getErrors();
        if (errors.isEmpty()) {
            val = ValidationState.OK;
        } else {
            for (TokenError error : errors) {
                if (error.getValidationState() == ParamValueConverter.
                    ConvValidationState.invalid) {
                    val = ValidationState.DontMatchAccept;
                    setTooltip(error, control);
                } else if (error.getValidationState() 
                    == ParamValueConverter.ConvValidationState.undecided) {
                    val = ValidationState.MightMatchAccept;
                }
            }
        }
        control.setData(Constants.VALID_STATE, val);
        return val;
    }

    

    /**
     * @param error error to display a message for
     * @param control associated widget
     */
    public static void setTooltip(TokenError error, Control control) {
        if (error.getI18NErrorKey() != null) {
            String tooltip = StringConstants.EMPTY;
            if (error.getI18NErrorKey().
                        equals(MessageIDs.E_SYNTAX_ERROR)) {
                tooltip = MessageIDs.getMessageObject(
                    error.getI18NErrorKey()).getMessage(
                        new Object[] {error.getInput()});
            } else {
                tooltip = MessageIDs.getMessageObject(
                    error.getI18NErrorKey()).getMessage(new Object[]{});
            }
            control.setToolTipText(tooltip);
        }
    }

    /**
     * @return Returns the desc.
     */
    IParamDescriptionPO getDesc() {
        return m_desc;
    }

    /**
     * @return Returns the emptyAllowed.
     */
    boolean isEmptyAllowed() {
        return m_emptyAllowed;
    }

    /**
     * @return Returns the node.
     */
    IParameterInterfacePO getNode() {
        return m_node;
    }
}
