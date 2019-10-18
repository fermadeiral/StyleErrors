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
package org.eclipse.jubula.examples.aut.adder.swing.gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * The normal calculator Panel
 * 
 * @author BREDEX GmbH
 */
public class CalculatorPanel extends AbstractCalculatorPanel {
    
    /**
     * The EqualButton
     */
    private JButton m_buttonEqual = null;

    /**
     * textfield for Addend1
     */
    private JTextField m_textFieldAddend1 = null;

    /**
     * textfield for Addend2
     */
    private JTextField m_textFieldAddend2 = null;

    /**
     * label for Addend1
     */
    private JLabel m_labelValue1 = null;

    /**
     * label for Addend2
     */
    private JLabel m_labelValue2 = null;

    /**
     * label for plus
     */
    private JLabel m_labelOperation = null;

    /**
     * label for Sum
     */
    private JLabel m_labelResult = null;

    /**
     * textfield for Sum
     */
    private JTextField m_textFieldSum = null;

    /**
     * standard construktor
     */
    public CalculatorPanel() {
        initialize();
    }
    /**
     * initializes the panel
     */
    private void initialize() {
        setLayout(new GridBagLayout());
        GridBagConstraints gbc;
        // textfield for addend1
        gbc = LayoutUtil.makegbc(1, 0, 1, 1);
        gbc.weightx = 100;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(getTextFieldAddend1(), gbc);
        // label value1
        gbc = LayoutUtil.makegbc(2, 0, 1, 1);
        gbc.anchor = GridBagConstraints.EAST;
        m_labelValue1 = new JLabel("value1"); //$NON-NLS-1$
        m_labelValue1.setName("label value1"); //$NON-NLS-1$
        add(m_labelValue1, gbc);
        // PlusLabel
        gbc = LayoutUtil.makegbc(0, 1, 1, 1);
        gbc.anchor = GridBagConstraints.EAST;
        m_labelOperation = new JLabel();
        m_labelOperation.setName("plus"); //$NON-NLS-1$
        add(m_labelOperation, gbc);
        //textfield for addend2
        gbc = LayoutUtil.makegbc(1, 1, 1, 1);
        gbc.weightx = 100;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(getTextFieldAddend2(), gbc);
        // label value2
        gbc = LayoutUtil.makegbc(2, 1, 1, 1);
        gbc.anchor = GridBagConstraints.EAST;
        m_labelValue2 = new JLabel("value2"); //$NON-NLS-1$
        m_labelValue2.setName("label value2"); //$NON-NLS-1$
        add(m_labelValue2, gbc);
        // Separator
        JSeparator sep = new JSeparator();
        sep.setOrientation(SwingConstants.HORIZONTAL);
        gbc = LayoutUtil.makegbc(0, 2, 3, 1);
        gbc.weightx = 100;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(sep, gbc);
        // sum textfield
        gbc = LayoutUtil.makegbc(1, 3, 1, 1);
        gbc.weightx = 100;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        add(getTextFieldSum(), gbc);
        // label for result
        gbc = LayoutUtil.makegbc(2, 3, 1, 1);
        gbc.anchor = GridBagConstraints.WEST;
        m_labelResult = new JLabel("result"); //$NON-NLS-1$
        m_labelResult.setName("label result"); //$NON-NLS-1$
        add(m_labelResult, gbc);
        // compute button
        JButton action = getButtonEqual();
        action.setActionCommand("add"); //$NON-NLS-1$
        action.setMnemonic(KeyEvent.VK_0);
        gbc = LayoutUtil.makegbc(0, 3, 1, 1);
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        add(action, gbc);
    }

    /**
     * Gets the EqualButton
     * 
     * @return a <code>JButton</code>
     */
    private JButton getButtonEqual() {
        if (m_buttonEqual == null) {
            m_buttonEqual = new JButton("="); //$NON-NLS-1$
            m_buttonEqual.setName("equal"); //$NON-NLS-1$
        }
        return m_buttonEqual;
    }

    /**
     * Get TextField for Addend1
     * 
     * @return TextField for Addend1
     */
    private JTextField getTextFieldAddend1() {
        if (m_textFieldAddend1 == null) {
            m_textFieldAddend1 = new JTextField(null, 6);
            m_textFieldAddend1.setHorizontalAlignment(SwingConstants.RIGHT);
            m_textFieldAddend1.setName("value1"); //$NON-NLS-1$
        }
        return m_textFieldAddend1;
    }

    /**
     * Get TextField for Addend2
     * 
     * @return TextField for Addend2
     */
    private JTextField getTextFieldAddend2() {
        if (m_textFieldAddend2 == null) {
            m_textFieldAddend2 = new JTextField(null, 6);
            m_textFieldAddend2.setHorizontalAlignment(SwingConstants.RIGHT);
            m_textFieldAddend2.setName("value2"); //$NON-NLS-1$
        }
        return m_textFieldAddend2;
    }

    /**
     * Get TextField for Sum
     * 
     * @return TextField for Sum
     */
    private JTextField getTextFieldSum() {
        if (m_textFieldSum == null) {
            m_textFieldSum = new JTextField(null, 6);
            m_textFieldSum.setHorizontalAlignment(SwingConstants.RIGHT);
            m_textFieldSum.setEditable(false);
            m_textFieldSum.setName("sum"); //$NON-NLS-1$
        }
        return m_textFieldSum;
    }

    /**
     * Addend1 as string
     * @return Addend1 as string
     */
    public String getAddend1() {
        return getTextFieldAddend1().getText();
    }
    
    /**
     * Clears addend1
     */
    public void clearAddend1() {
        getTextFieldAddend1().setText(""); //$NON-NLS-1$
    }

    /**
     * Addend2 as string
     * @return Addend2 as string
     */
    public String getAddend2() {
        return getTextFieldAddend2().getText();
    }
    
    /**
     * Clears addend2
     */
    public void clearAddend2() {
        getTextFieldAddend2().setText(""); //$NON-NLS-1$
    }

    /**
     * Sum as string
     * @return Sum as string
     */
    public String getSum() {
        return getTextFieldSum().getText();
    }
    
    /**
     * Set sum
     * @param sum Sum
     */
    public void setSum(String sum) {
        getTextFieldSum().setText(sum);
    }
    
    /**
     * Clears the sum
     */
    public void clearSum() {
        getTextFieldSum().setText(""); //$NON-NLS-1$
    }
    
    /**
     * Set the font of the sum field
     * @param font font
     */
    public void setSumFont(Font font) {
        getTextFieldSum().setFont(font);
        getTextFieldSum().revalidate();
    }
    
    /**
     * Sets the operation
     * @param op the string representation of the operation
     */
    public void setOperation(String op) {
        m_labelOperation.setText(op);
    }

    /**
     * Add a listener to the evaluate button
     * @param listener the listener
     */
    public void addEvaluateActionListener(ActionListener listener) {
        getButtonEqual().addActionListener(listener);
    }
}
