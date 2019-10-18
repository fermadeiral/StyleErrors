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

import org.apache.commons.lang.Validate;
import org.eclipse.jubula.client.ui.widgets.JBText;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;

/**
 * This is the base class for all checked text fields. Checks are implemented
 * by subclassing and adding the appropriate implementations for the
 * abstract check methods
 *
 * @author BREDEX GmbH
 * @created 06.03.2006
 */
public abstract class CheckedText extends JBText {

    /** How should the text be validated */
    public interface IValidator {
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
        public ValidationState validateInput(VerifyEvent e);
    }
    /** Validation state of the text input so far */
    public static enum ValidationState {
        /** text completely valid */
        OK,
        /** still empty, can't decide */
        EmptyAccept,
        /** still empty, can't decide */
        EmptyReject,
        /** text might be OK in the future, but reject new input */
        MightMatchReject,
        /** text might be OK in the future, accept new input */
        MightMatchAccept,
        /** text is invalid, reject new input */
        DontMatchReject, 
        /** text is invalid, accept new input evertheless */
        DontMatchAccept
    }
    
    /**
     * Performs validation and tracks the result. Adds visual cues to the 
     * Control in order to make validation more obvious.
     *
     * @author BREDEX GmbH
     * @created Apr 8, 2010
     */
    public static final class ValidationListener implements VerifyListener {

        /** result of the last validation */
        private ValidationState m_validationState = ValidationState.OK;

        /** the validator */
        private IValidator m_validator;

        /**
         * Constructor
         * 
         * @param validator The validator.
         */
        public ValidationListener(IValidator validator) {
            m_validator = validator;
        }
        
        /**
         * {@inheritDoc}
         */
        public void verifyText(VerifyEvent e) {
            m_validationState = m_validator.validateInput(e);
            Validate.isTrue(e.widget instanceof Control);
            Control eventControl = (Control)e.widget;
            boolean doit = false;
            switch (m_validationState) {
                case OK:
                    doit = true;
                    eventControl.setBackground(null);
                    break;
                case EmptyAccept:
                    doit = true;
                    eventControl.setBackground(null);
                    m_validationState = ValidationState.OK;
                    break;
                case EmptyReject:
                    doit = false;
                    eventControl.setBackground(getWarningColor(e.display));
                    break;
                case MightMatchAccept:
                    doit = true;
                    eventControl.setBackground(getWarningColor(e.display));
                    break;
                case MightMatchReject:
                    doit = false;
                    eventControl.setBackground(getWarningColor(e.display));
                    break;
                case DontMatchAccept:
                    doit = true;
                    eventControl.setBackground(getErrorColor(e.display));
                    break;
                case DontMatchReject:
                    doit = false;
                    eventControl.setBackground(getErrorColor(e.display));
                    break;
                default:
                    break;
            }
            e.doit &= doit;
        }
        
        /**
         * Tells if the current input of the text is valid according to its 
         * validator.
         * @return true if the current input is OK.
         */
        public boolean isValid() {
            return m_validationState == ValidationState.OK;
        }

        /**
         * 
         * @param display The display to on which the color will be used.
         * @return the color used for indicating an error.
         */
        private Color getErrorColor(Display display) {
            return display.getSystemColor(SWT.COLOR_RED);
        }

        /**
         * 
         * @param display The display to on which the color will be used.
         * @return the color used for indicating an error.
         */
        protected Color getWarningColor(Display display) {
            return display.getSystemColor(SWT.COLOR_YELLOW);
        }
    }

    /** the listener that performs the validation */
    private ValidationListener m_validationListener;
    
    /**
     * @param parent parent
     * @param style style
     * @param validator validator
     */
    protected CheckedText(Composite parent, int style, 
        IValidator validator) {
        super(parent, style);
        m_validationListener = new ValidationListener(validator);
        addVerifyListener(m_validationListener);
        
        validate();
    }

    /**
     * trigger a validation programmatically
     */
    public void validate() {
        Event dummyEvent = new Event();
        dummyEvent.start = 0;
        dummyEvent.end = 0;
        dummyEvent.text = StringConstants.EMPTY;
        this.notifyListeners(SWT.Verify, dummyEvent);
    }

    /**
     * Tells if the current input of the text is valid according to its 
     * validator.
     * @return true if the current input is OK.
     */
    public boolean isValid() {
        return m_validationListener.isValid();
    }

}
