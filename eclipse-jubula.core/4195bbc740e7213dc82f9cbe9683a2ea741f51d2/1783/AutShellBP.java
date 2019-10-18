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
package org.eclipse.jubula.examples.aut.adder.swt.businessprocess;

import org.eclipse.jubula.examples.aut.adder.swt.gui.AutShell;
import org.eclipse.jubula.examples.aut.adder.swt.model.IOperator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;



/**
 * This class handles the business process for the AutFrame, concerning the
 * Initialization and the window handling.
 * 
 * @author BREDEX GmbH
 * @created 23.02.2006
 */
public class AutShellBP {

    /**
     * <code>ERROR</code>
     */
    private static final String ERROR = "#error"; //$NON-NLS-1$

    /** empty string */
    private static final String CLEAR = ""; //$NON-NLS-1$

    /** The shell of AUT */
    private AutShell m_autShell = null;

    /**
     * Gets the AutShell
     * 
     * @return a <code>AutShell</code> object
     */
    public AutShell getAutShell() {
        if (getShell() == null) {
            m_autShell = new AutShell("SWT Adder"); //$NON-NLS-1$
            addListeners();
        }
        return m_autShell;
    }

    /**
     * Opens the about dialog.
     */
    void openAboutDialog() {
        final Shell s = new Shell(getShell());
        s.setText("About"); //$NON-NLS-1$
        s.setLayout(new GridLayout());
        Label lbl = new Label(s, SWT.None);
        lbl.setText("Application under Test" + //$NON-NLS-1$
                "\ncopyright by BREDEX Software GmbH"); //$NON-NLS-1$
        Button button = new Button(s, SWT.PUSH);
        button.setText("OK"); //$NON-NLS-1$
        button.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent arg0) {
                widgetSelected(arg0);
            }

            public void widgetSelected(SelectionEvent arg0) {
                s.close();
            }
            
        });
        s.pack();
        s.open();
    }

    /**
     * Adds listeners to the controls.
     */
    private void addListeners() {
        getShell().getEqualsButton().addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent e) {
                        getShell().getSumField().setText(getResult());
                    }
                });
        getShell().getAboutMenuItem().addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent e) {
                        openAboutDialog();
                    }
                });
        getShell().getExitMenuItem().addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent e) {
                        System.exit(0);
                    }
                });
        getShell().getResetMenuItem().addSelectionListener(
                new SelectionAdapter() {

                    public void widgetSelected(SelectionEvent e) {
                        resetTextFields();
                    }
                });
    }

    /** Clears all text fields. */
    void resetTextFields() {
        getShell().getValue1Field().setText(CLEAR);
        getShell().getValue2Field().setText(CLEAR);
        getShell().getSumField().setText(CLEAR);
    }

    /**
     * @return The result of the calculation
     */
    String getResult() {
        String val1 = getShell().getValue1Field().getText();
        String val2 = getShell().getValue2Field().getText();

        
        int result = 0;
        try {
            float fVal1 = Float.valueOf(val1).floatValue();
            float fVal2 = Float.valueOf(val2).floatValue();

            if (fVal1 == 17 && fVal2 == 4) {
                return "jackpot"; //$NON-NLS-1$
            }
            IOperator operator = (IOperator)m_autShell.getOperator().getData(
                    "op"); //$NON-NLS-1$
            result = new Float(operator.calculate(fVal1, fVal2)).intValue();
        } catch (NumberFormatException e) {
            return ERROR;
        }
        return CLEAR + result;
    }

    /**
     * @return Returns the autShell.
     */
    protected AutShell getShell() {
        return m_autShell;
    }
}