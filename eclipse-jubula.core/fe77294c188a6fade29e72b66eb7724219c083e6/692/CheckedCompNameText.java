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

import org.eclipse.jubula.client.ui.rcp.databinding.validators.ComponentNameValidator;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;



/**
 * @author BREDEX GmbH
 * @created 06.03.2006
 */
public class CheckedCompNameText extends CheckedText {

    /**
     * Check for valid names
     */
    public static class CompNameValidator implements IValidator {
       /**
         * 
         */
        public CompNameValidator() {
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

            if (ComponentNameValidator.isValidComponentNameString(newValue)
                    .isOK()) {
                val = ValidationState.OK;
            } else {
                val = ValidationState.MightMatchAccept;
            }
            return val;
        }
        
    }

    /**
     * 
     * @param parent SWT
     * @param style SWT 
     */
    public CheckedCompNameText(Composite parent, int style) {
        super(parent, style, new CompNameValidator());
    }
}
