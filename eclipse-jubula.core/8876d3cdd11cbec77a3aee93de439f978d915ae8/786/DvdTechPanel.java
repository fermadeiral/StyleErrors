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
package org.eclipse.jubula.examples.aut.dvdtool.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This class displays the 'technical' information for a dvd. 
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdTechPanel extends JPanel {
    /** the label for the length */
    private JLabel m_labelLength = new JLabel(Resources.getString("length_in_minutes")); //$NON-NLS-1$
    /** the component (text field) displaying the length */
    private JTextField m_tfLength = new JTextField(5);
    /** the lab for 'has bonus' */
    private JLabel m_labelBonus = new JLabel(Resources.getString("bonus")); //$NON-NLS-1$
    /** the check box for 'has bonus' */
    private JCheckBox m_checkBoxBonus = new JCheckBox();
    /** the label for the region code */
    private JLabel m_labelRegionCode = new JLabel(Resources.getString("region_code")); //$NON-NLS-1$
    /** the combo box containing all region codes */
    private JComboBox m_comboBoxRegionCode = new JComboBox(
            Constants.REGION_CODES);

    /**
     * public constructor, initialises the panel
     */
    public DvdTechPanel() {
        super();
        init();
    }

    /**
     * private method creating GridBagConstraints, used for the layout
     * @param x the x position
     * @param y the y position
     * @return a new instance of GridBagConstraints
     */
    private GridBagConstraints createGridBagConstraints(int x, int y) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.insets = new Insets(5, 5, 5, 5);
        return gbc;
    }

    /**
     * private method for initialisation
     */    
    private void init() {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = null;

        gbc = createGridBagConstraints(0, 0);
        gbc.anchor = GridBagConstraints.EAST;
        m_labelLength.setName("lengthLabel"); //$NON-NLS-1$
        add(m_labelLength, gbc);

        gbc = createGridBagConstraints(1, 0);
        gbc.anchor = GridBagConstraints.WEST;
        m_tfLength.setName("lengthText"); //$NON-NLS-1$
        add(m_tfLength, gbc);

        gbc = createGridBagConstraints(0, 1);
        gbc.anchor = GridBagConstraints.EAST;
        m_labelBonus.setName("bonusLabel"); //$NON-NLS-1$
        add(m_labelBonus, gbc);

        gbc = createGridBagConstraints(1, 1);
        gbc.anchor = GridBagConstraints.WEST;
        m_checkBoxBonus.setName("bonusCheck"); //$NON-NLS-1$
        add(m_checkBoxBonus, gbc);

        gbc = createGridBagConstraints(0, 2);
        gbc.anchor = GridBagConstraints.EAST;
        m_labelRegionCode.setName("regionCodeLabel"); //$NON-NLS-1$
        add(m_labelRegionCode, gbc);

        gbc = createGridBagConstraints(1, 2);
        gbc.anchor = GridBagConstraints.WEST;
        m_comboBoxRegionCode.setName("regionCodeCombo"); //$NON-NLS-1$
        add(m_comboBoxRegionCode, gbc);
    }

    /**
     * @return the text field displaying the length
     */
    public JTextField getTextFieldLength() {
        return m_tfLength;
    }

    /**
     * @return the check box for 'has bonus'
     */
    public JCheckBox getCheckBoxBonus() {
        return m_checkBoxBonus;
    }

    /**
     * @return the combo box with the region codes
     */
    public JComboBox getComboBoxRegionCode() {
        return m_comboBoxRegionCode;
    }
}
