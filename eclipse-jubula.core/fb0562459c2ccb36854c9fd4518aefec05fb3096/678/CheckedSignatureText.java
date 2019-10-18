/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.ui.rcp.widgets;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 29.10.2013
 */
public class CheckedSignatureText extends CheckedText {

    /**
     * Check for valid names
     */
    public static class SignatureValidator implements IValidator {
       /**
         * 
         */
        public SignatureValidator() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        public ValidationState validateInput(VerifyEvent e) {
            ValidationState val;
            
            Text txt = (Text)e.widget;
            
            final String oldValue = txt.getText();
            StringBuilder workValue = new StringBuilder(oldValue);
            workValue.replace(e.start, e.end, e.text);
            String newValue = workValue.toString();

            if (StringUtils.isEmpty(newValue)) {                
                val = ValidationState.OK;                
            } else {
                if (EnvironmentUtils.getProcessOrSystemProperty(newValue) 
                        != null) {
                    val = ValidationState.OK;                    
                } else {
                    val = ValidationState.DontMatchAccept;
                }
            }
            return val;
        }
        
    }

    /**
     * 
     * @param parent SWT
     * @param style SWT 
     */
    public CheckedSignatureText(Composite parent, int style) {
        super(parent, style, new SignatureValidator());
    }
}
