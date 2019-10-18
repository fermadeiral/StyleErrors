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

import org.eclipse.jubula.client.core.model.IParamDescriptionPO;
import org.eclipse.jubula.client.core.model.IParameterInterfacePO;
import org.eclipse.jubula.client.core.utils.GuiParamValueConverter;
import org.eclipse.jubula.client.core.utils.IParamValueValidator;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 11.09.2007
 */
public class CheckedParamText extends CheckedText {
    /** validator to validate the complete string in ParamValueConverter */
    private IParamValueValidator m_dataValidator;

    /**
     * @param parent parent
     * @param style style
     * @param node current node, associated with parameter for this value
     * @param desc parameter description associated with current edited value
     * @param dataValidator validator for data validation
     */
    public CheckedParamText(Composite parent, int style, 
        IParameterInterfacePO node, IParamDescriptionPO desc, 
        IParamValueValidator dataValidator) {
        super(parent, style, new StringTextValidator(
            node, desc, dataValidator));
        m_dataValidator = dataValidator;
       
    }
    
    /**
     * @return Returns the dataValidator.
     */
    public IParamValueValidator getDataValidator() {
        return m_dataValidator;
    }
    
    /**
     * Implementation of the intger validator with optional check for empty
     * input.
     */
    public static class StringTextValidator extends ParamValueValidator {
        /** validator to validate the complete string in ParamValueConverter */
        private IParamValueValidator m_dataValidator;
        
        /**
         * @param node associated with parameter to validate
         * @param desc parameter description associated with current edited value
         * @param dataValidator validator for data validation
         */
        public StringTextValidator(IParameterInterfacePO node, 
            IParamDescriptionPO desc, IParamValueValidator dataValidator) {
            super(node, desc, true);
            m_dataValidator = dataValidator;
        }
        
        /** {@inheritDoc}
         * @see org.eclipse.jubula.client.ui.rcp.widgets.ParamValueValidator#validateInput(org.eclipse.swt.events.VerifyEvent)
         */
        public ValidationState validateInput(VerifyEvent e) {
            ValidationState state = super.validateInput(e);
            if (state == ValidationState.OK) {
                GuiParamValueConverter conv = new GuiParamValueConverter(
                    getNewValue(e), getNode(), getDesc(), m_dataValidator);
                state = setState((Text)e.getSource(), conv);
            }

            return state;
        }
        
    }
}
