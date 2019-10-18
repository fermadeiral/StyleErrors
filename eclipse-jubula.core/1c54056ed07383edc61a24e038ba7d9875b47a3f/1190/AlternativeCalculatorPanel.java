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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.text.JTextComponent;

/**
 * The alternative calculator panel
 * 
 * @author BREDEX GmbH
 */
public class AlternativeCalculatorPanel extends AbstractCalculatorPanel {

    /**
     * Addend1 combobox
     */
    private JComboBox m_addend1CB = null;
    
    /**
     * Addend2 combobox
     */
    private JComboBox m_addend2CB = null;
    
    /**
     * Label that displays the result
     */
    private JLabel m_sumLabel = null;
    
    /**
     * Label of the addend1 combobox
     */
    private JLabel m_addend1Label = null;
    
    /**
     * Label of the addend2 combobox
     */
    private JLabel m_addend2Label = null;
    
    /**
     * Label of the sum label
     */
    private JLabel m_sumTextLabel = null;
    
    /**
     * Equal button
     */
    private JButton m_equalBtn = null;
    
    /**
     * operation label
     */
    private JLabel m_operationLabel = null;
    
    /**
     * standard construktor
     */
    public AlternativeCalculatorPanel() {
        initialize();
    }

    /**
     * initializes the panel
     */
    private void initialize() {
        setLayout(new GridBagLayout());
        addFillerComponent(getAddend1Label(), 0, 0);
        addFillerComponent(getAddend2Label(), 2, 0);
        addFillerComponent(getSumTextLabel(), 4, 0);
        addFillerComponent(getAddend1CB(), 0, 1);
        addFixedSizeComponent(getOperationLabel(), 1, 1);
        addFillerComponent(getAddend2CB(), 2, 1);
        addFixedSizeComponent(getEqualBtn(), 3, 1);
        addFillerComponent(getSumLabel(), 4, 1);
    }
    
    /**
     * Adds a component that fills space horizontal
     * @param comp compoent
     * @param x horizontal position
     * @param y vertical position
     */
    private void addFillerComponent(Component comp, int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints(
                x, y,
                1, 1,
                1, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.HORIZONTAL,
                LayoutUtil.DEFAULT_INSETS,
                0, 0);
        add(comp, gbc);
    }
    
    /**
     * Adds a component that has a fixed size
     * @param comp component
     * @param x horizontal position
     * @param y vertical position
     */
    private void addFixedSizeComponent(Component comp, int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints(
                x, y,
                1, 1,
                0, 0,
                GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                LayoutUtil.DEFAULT_INSETS,
                0, 0);
        add(comp, gbc);
    }
    
    /**
     * Addend1 combobox
     * @return Addend1 combobox
     */
    private JComboBox getAddend1CB() {
        if (m_addend1CB == null) {
            m_addend1CB = new JComboBox();
            m_addend1CB.setEditable(true);
            m_addend1CB.setName("value1"); //$NON-NLS-1$
        }
        return m_addend1CB;
    }
    
    /**
     * Addend2 combobox
     * @return Addend2 combobox
     */
    private JComboBox getAddend2CB() {
        if (m_addend2CB == null) {
            m_addend2CB = new JComboBox();
            m_addend2CB.setEditable(true);
            m_addend2CB.setName("value2"); //$NON-NLS-1$
        }
        return m_addend2CB;
    }
    
    /**
     * Label that displays the sum
     * @return Sum label
     */
    private JLabel getSumLabel() {
        if (m_sumLabel == null) {
            m_sumLabel = new JLabel() {
                public Dimension getPreferredSize() {
                    return LayoutUtil
                            .getStringDimension(m_sumLabel, "1234567890"); //$NON-NLS-1$
                }
            };
            m_sumLabel.setHorizontalAlignment(SwingConstants.RIGHT);
            m_sumLabel.setName("sum"); //$NON-NLS-1$
        }
        return m_sumLabel;
    }
    
    /**
     * Addend1 label
     * @return Addend1 label
     */
    private JLabel getAddend1Label() {
        if (m_addend1Label == null) {
            m_addend1Label = new JLabel("Value 1"); //$NON-NLS-1$
            m_addend1Label.setName("label value1"); //$NON-NLS-1$
        }
        return m_addend1Label;
    }
    
    /**
     * Addend2 label
     * @return Addend2 label
     */
    private JLabel getAddend2Label() {
        if (m_addend2Label == null) {
            m_addend2Label = new JLabel("Value 2"); //$NON-NLS-1$
            m_addend2Label.setName("label value2"); //$NON-NLS-1$
        }
        return m_addend2Label;
    }
    
    /**
     * Label of the label that displays the sum
     * @return sum label
     */
    private JLabel getSumTextLabel() {
        if (m_sumTextLabel == null) {
            m_sumTextLabel = new JLabel("Sum"); //$NON-NLS-1$
            m_sumTextLabel.setName("label result"); //$NON-NLS-1$
        }
        return m_sumTextLabel;
    }
    
    /**
     * Equal button
     * @return Equal button
     */
    private JButton getEqualBtn() {
        if (m_equalBtn == null) {
            m_equalBtn = new JButton("="); //$NON-NLS-1$
            m_equalBtn.setName("equal"); //$NON-NLS-1$
        }
        return m_equalBtn;
    }
    
    /**
     * Operation label
     * @return Operation label
     */
    private JLabel getOperationLabel() {
        if (m_operationLabel == null) {
            m_operationLabel = new JLabel("+"); //$NON-NLS-1$
            m_operationLabel.setName("plus"); //$NON-NLS-1$
        }
        return m_operationLabel;
    }

    /**
     * add a listener to the evaluate button
     * @param listener the listener
     */
    public void addEvaluateActionListener(ActionListener listener) {
        m_equalBtn.addActionListener(listener);
    }
    
    /**
     * Addend1 as string
     * @return Addend1 as string
     */
    public String getAddend1() {
        JTextComponent sel = (JTextComponent) 
            getAddend1CB().getEditor().getEditorComponent();
        return (sel == null) ? "" : sel.getText(); //$NON-NLS-1$
    }
    
    /**
     * Addend2 as string
     * @return Addend2 as string
     */
    public String getAddend2() {
        JTextComponent sel = (JTextComponent) 
            getAddend2CB().getEditor().getEditorComponent();
        return (sel == null) ? "" : sel.getText(); //$NON-NLS-1$
    }
    
    /**
     * Sum as string
     * @return Sum as string
     */
    public String getSum() {
        return getSumLabel().getText();
    }

    /**
     * Clears addend1
     */
    public void clearAddend1() {
        getAddend1CB().setSelectedItem(null);
    }
    
    /**
     * Clears addend2
     */
    public void clearAddend2() {
        getAddend2CB().setSelectedItem(null);
    }
    
    /**
     * Clears the sum
     */
    public void clearSum() {
        getSumLabel().setText(""); //$NON-NLS-1$
    }
    
    /**
     * Sets the operation
     * @param op the string representation of the operation
     */
    public void setOperation(String op) {
        getOperationLabel().setText(op);
    }
    
    /**
     * Set sum
     * @param sum Sum
     */
    public void setSum(String sum) {
        getSumLabel().setText(sum);
    }
    
    /**
     * Set the font of the sum field
     * @param font font
     */
    public void setSumFont(Font font) {
        getSumLabel().setFont(font);
    }
}
