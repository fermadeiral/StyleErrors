/*******************************************************************************
 * Copyright (c) 2014 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.aut.adder.rcp.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

/**
 * @author BREDEX GmbH
 */
public class View extends ViewPart {
    /**
     * @author BREDEX GmbH
     */
    private final class SelectionListenerImplementation implements
        SelectionListener {
        /** {@inheritDoc} */
        public void widgetSelected(SelectionEvent e) {
            String val1 = m_value1Field.getText();
            String val2 = m_value2Field.getText();

            String resultText;
            try {
                int ival1 = Integer.valueOf(val1).intValue();
                int ival2 = Integer.valueOf(val2).intValue();

                if (ival1 == 17 && ival2 == 4) {
                    resultText = "jackpot"; //$NON-NLS-1$
                } else {
                    resultText = CLEAR + (ival1 + ival2);
                }
            } catch (NumberFormatException nfe) {
                resultText = ERROR;
            }

            m_sumField.setText(resultText);
        }
        /** {@inheritDoc} */
        public void widgetDefaultSelected(SelectionEvent e) {
            widgetSelected(e);
        }
    }

    /** <code>ERROR</code> */
    private static final String ERROR = "#error"; //$NON-NLS-1$

    /** empty string */
    private static final String CLEAR = ""; //$NON-NLS-1$
    
    /** constant for naming the SWT components */
    private static final String WIDGET_NAME = "TEST_COMP_NAME"; //$NON-NLS-1$
    /** sum field */
    private Text m_sumField;
    /** value2 field */
    private Text m_value2Field;
    /** value1 field */
    private Text m_value1Field;
    /** equals button */
    private Button m_equalsButton;
    /** operator label */
    private Label m_operator;
    
    
    /** {@inheritDoc} */
    public void createPartControl(Composite parent) {
        GridLayout shellLayout = new GridLayout ();
        shellLayout.marginWidth = 100;
        parent.setLayout(shellLayout);
        
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setData(WIDGET_NAME, "SWTAdder.Composite"); //$NON-NLS-1$
        GridLayout compositeLayout = new GridLayout (2, false);
        composite.setLayout (compositeLayout);
        GridData data = new GridData ();
        data.horizontalAlignment = GridData.FILL;
        data.verticalAlignment = GridData.FILL;
        data.grabExcessHorizontalSpace = false;
        data.grabExcessVerticalSpace = false;
        composite.setLayoutData (data);
        
        new Label(composite, SWT.NONE);
        m_value1Field = new Text (composite, SWT.BORDER | SWT.RIGHT);
        GridData value1FieldData = new GridData ();
        value1FieldData.horizontalAlignment = GridData.FILL;
        value1FieldData.verticalAlignment = GridData.BEGINNING;
        value1FieldData.grabExcessHorizontalSpace = true;
        m_value1Field.setLayoutData (value1FieldData);
        
        m_operator = new Label (composite, SWT.NONE);
        m_operator.setData(WIDGET_NAME, "SWTAdder.Operator"); //$NON-NLS-1$
        m_operator.setText ("+");  //$NON-NLS-1$
        GridData operatorData = new GridData ();
        operatorData.horizontalAlignment = GridData.END;
        m_operator.setLayoutData (operatorData);
        
        m_value2Field = new Text (composite, SWT.BORDER | SWT.RIGHT);
        GridData value2FieldData = new GridData ();
        value2FieldData.horizontalAlignment = GridData.FILL;
        value2FieldData.verticalAlignment = GridData.BEGINNING;
        value2FieldData.grabExcessHorizontalSpace = true;
        m_value2Field.setLayoutData (value2FieldData);
        
        m_equalsButton = new Button (composite, SWT.PUSH);
        m_equalsButton.setText ("="); //$NON-NLS-1$
        m_equalsButton.setData(WIDGET_NAME, "SWTAdder.EqualsButton"); //$NON-NLS-1$
        GridData equalsButtonData = new GridData ();
        equalsButtonData.horizontalAlignment = GridData.END;
        m_equalsButton.setLayoutData (equalsButtonData);
        
        m_equalsButton.addSelectionListener(
            new SelectionListenerImplementation());
        
        m_sumField = new Text (composite, SWT.BORDER | SWT.RIGHT);
        m_sumField.setData(WIDGET_NAME, "SWTAdder.SumField"); //$NON-NLS-1$
        m_sumField.setEditable(false);
        GridData sumFieldData = new GridData ();
        sumFieldData.horizontalAlignment = GridData.FILL;
        sumFieldData.verticalAlignment = GridData.BEGINNING;
        sumFieldData.grabExcessHorizontalSpace = true;
        m_sumField.setLayoutData (sumFieldData);
    }

    /** {@inheritDoc} */
    public void setFocus() {
        m_value1Field.setFocus();
    }
}