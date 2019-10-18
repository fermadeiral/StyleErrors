/*******************************************************************************
 * Copyright (c) 2016 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.examples.extension.swing.aut;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Calendar;

import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

/**
 * Example AUT for Jubula Extension Mechanism
 * 
 * @author BREDEX GmbH
 */
public class JSpinnerExampleAUT extends JFrame {
    /** version id */
    private static final long serialVersionUID = 1L;
    
    /** constructor */
    @SuppressWarnings("nls")
    public JSpinnerExampleAUT() {
        super("JSpinner Example AUT");
        
        final int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        SpinnerNumberModel yearModel = 
                new SpinnerNumberModel(currentYear, 0, 3000, 1);
        JSpinner spinnerYear = new JSpinner(yearModel); 
        spinnerYear.setEditor(new JSpinner.NumberEditor(spinnerYear, "#"));
        spinnerYear.setName("Year Spinner");
        
        String[] months = new String[] {"January", "February", "March", 
            "April", "May", "June", "July", "August", 
            "September", "October", "November", "December"};
        SpinnerListModel monthModel = new SpinnerListModel(months);
        JSpinner spinnerMonth = new JSpinner(monthModel); 
        spinnerMonth.setName("Month Spinner");
        
        Component spinnerEditor = spinnerMonth.getEditor();
        JFormattedTextField jftf = 
                ((JSpinner.DefaultEditor) spinnerEditor).getTextField();
        jftf.setColumns(7);

        super.getContentPane().setLayout(new FlowLayout());
        super.getContentPane().add(spinnerYear);
        super.getContentPane().add(spinnerMonth);
    }
    
    /**
     * main Method
     * 
     * @param args
     *             cmdline arguments
     */
    public static void main(String[] args) {
        JSpinnerExampleAUT f = new JSpinnerExampleAUT();
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        f.setSize(300, 90);
        f.setVisible(true);
    }
}