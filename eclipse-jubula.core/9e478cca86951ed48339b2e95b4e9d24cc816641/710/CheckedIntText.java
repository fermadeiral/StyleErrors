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

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * @author BREDEX GmbH
 * @created 06.03.2006
 */
public class CheckedIntText extends CheckedText {

    /**
     * Implementation of the intger validator with optional check for empty
     * input.
     */
    private static class IntegerValidator implements IValidator {
        /** is an empty text valid? */
        private boolean m_empyAllowed;
        /** lower value */
        private int m_minVal;
        /** upper value */
        private int m_maxVal;

        /**
         * constructor
         * @param emptyAllowed is an empty text filed allowed?
         * @param minVal minimum value considered valid
         * @param maxVal maximun value considered valid
         */
        public IntegerValidator(boolean emptyAllowed, int minVal, int maxVal) {
            m_empyAllowed = emptyAllowed;
            m_minVal = minVal;
            m_maxVal = maxVal;
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
                if (m_empyAllowed) {
                    val = ValidationState.EmptyAccept;
                } else {
                    val = ValidationState.MightMatchAccept;
                }
            } else {
                if (newValue.equals("-")) { //$NON-NLS-1$
                    if (m_minVal < 0) {
                        val = ValidationState.MightMatchAccept;
                    } else {
                        val = resetToOldValue(e, oldValue);
                    }
                } else {
                    try {
                        int n = Integer.parseInt(newValue);
                        if (n >= m_minVal && n <= m_maxVal) {
                            val = ValidationState.OK;
                        } else {
                            if (n <  0 && m_minVal >= 0) {
                                val = resetToOldValue(e, oldValue);
                            } else {
                                val = ValidationState.MightMatchAccept;
                            }
                        }
                    } catch (NumberFormatException exc) {
                        val = resetToOldValue(e, oldValue);
                    }
                }
            }
            return val;
        }
        /**
         * @param e used to change the doit field
         * @param oldValue original field contents
         * @return a state dependend on oldValue
         */
        private ValidationState resetToOldValue(VerifyEvent e,
                final String oldValue) {
            e.doit = false;
            ValidationState val;
            try {
                if (StringUtils.isEmpty(oldValue)) {
                    if (m_empyAllowed) {
                        val = ValidationState.EmptyAccept;
                    } else {
                        val = ValidationState.MightMatchAccept;
                    }
                } else {
                    int n = Integer.parseInt(oldValue);
                    if (n >= m_minVal && n <= m_maxVal) {
                        val = ValidationState.OK;
                    } else {
                        val = ValidationState.MightMatchAccept;
                    }
                }
            } catch (NumberFormatException exc) {
                val = ValidationState.DontMatchReject;
            }
            return val;
        }
        
    }

    /**
     * 
     * @param parent SWT
     * @param style SWT 
     * @param emptyAllowed is an empty field considered valid (implicite 
     * value is 0)
     * @param minValue lower value for this field
     * @param maxValue upper value for this field
     */
    public CheckedIntText(Composite parent, int style, boolean emptyAllowed,
        int minValue, int maxValue) {
        super(parent, style, new IntegerValidator(emptyAllowed, minValue,
            maxValue));
    }
    /**
     * No range restriction
     * @param parent SWT
     * @param style SWT 
     * @param emptyAllowed is an empty field considered valid (implicite 
     * value is 0)
     */
    public CheckedIntText(Composite parent, int style, boolean emptyAllowed) {
        this(parent, style, emptyAllowed, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    /**
     * Empty field allowed and no range restrictions
     * 
     * @param parent parent
     * @param style style
     * 
     */
    public CheckedIntText(Composite parent, int style) {
        this(parent, style, true);
    }
    /**
     * set the value to be displayed
     * @param val any int
     */
    public void setValue(int val) {
        setText(String.valueOf(val));
    }
    
    /**
     * get the displayed value as int
     * @return the int currently display or 0 if the text field is empty
     */
    public int getValue() {
        String txt = getText();
        if (StringUtils.isEmpty(txt)) {
            return 0;
        }
        return Integer.parseInt(txt);
    }
}
