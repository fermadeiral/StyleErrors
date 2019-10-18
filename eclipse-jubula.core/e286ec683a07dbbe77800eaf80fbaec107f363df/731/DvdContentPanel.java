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

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;

import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This class displays the 'content' information for a dvd.
 *
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdContentPanel extends JPanel {
    /** the label for the chapter */
    private JLabel m_labelChapters = new JLabel(Resources.getString("chapter")); //$NON-NLS-1$
    /** the component (text field) displaying the chapter */
    private JTextField m_tfChapters = new JTextField(5);
    /** the label for the description */
    private JLabel m_labelDescription = new JLabel(Resources.getString("description")); //$NON-NLS-1$
    /** the component (text area) displaying the description */
    private JTextArea m_taDescription = new JTextArea();
    /**  the panel displaying the rating radio buttons */
    private JPanel m_fskPanel = new JPanel();
    /** the radio button for fsk6 */
    private JRadioButton m_rbFsk6 = new JRadioButton(); 
    /** the radio button for fsk16 */
    private JRadioButton m_rbFsk16 = new JRadioButton(); 
    /** the radio button for fsk18 */
    private JRadioButton m_rbFsk18 = new JRadioButton(); 
    /** a non displayed radio button for clearing the other radio buttons */
    private JRadioButton m_rbDummy = new JRadioButton();

    /**
     * public constructor, initialises the panel
     */
    public DvdContentPanel() {
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
        // set the layout 
        setLayout(new GridBagLayout());

        m_rbFsk6.setText(Resources.getString("Rating1")); //$NON-NLS-1$
        m_rbFsk16.setText(Resources.getString("Rating2")); //$NON-NLS-1$
        m_rbFsk18.setText(Resources.getString("Rating3")); //$NON-NLS-1$
        // put the radio buttons into a ButtonGroup 
        ButtonGroup bg = new ButtonGroup();
        bg.add(m_rbFsk6);
        bg.add(m_rbFsk16);
        bg.add(m_rbFsk18);
        bg.add(m_rbDummy);

        // use a separate panel with a border for the radio buttons
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(), Resources.getString("Rating")); //$NON-NLS-1$
        m_fskPanel.setBorder(border);
        m_fskPanel.add(m_rbFsk6);
        m_fskPanel.add(m_rbFsk16);
        m_fskPanel.add(m_rbFsk18);

        m_tfChapters.setDragEnabled(true);
        m_taDescription.setDragEnabled(true);

        // put all together
        GridBagConstraints gbc = null;
        
        gbc = createGridBagConstraints(0, 0);
        m_labelChapters.setName("chapterLabel"); //$NON-NLS-1$
        add(m_labelChapters, gbc);

        gbc = createGridBagConstraints(1, 0);
        gbc.anchor = GridBagConstraints.WEST;
        m_tfChapters.setName("chapterText"); //$NON-NLS-1$
        add(m_tfChapters, gbc);

        gbc = createGridBagConstraints(0, 1);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.gridwidth = 2;
        add(m_fskPanel, gbc);

        gbc = createGridBagConstraints(2, 0);
        gbc.anchor = GridBagConstraints.WEST;
        add(m_labelDescription, gbc);

        gbc = createGridBagConstraints(2, 1);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        add(new JScrollPane(m_taDescription), gbc);
    }
    
    /**
     * getter for the rating panel
     * @return the rating panel
     */
    public JPanel getRatingPanel() {
        return m_fskPanel;
    }
    
    /**
     * getter for the radio button fsk6
     * @return the radio button fsk6
     */
    public JRadioButton getRadioButtonFsk6() {
        return m_rbFsk6;
    }
    
    /**
     * getter for the radio button fsk16
     * @return the radio button fsk16
     */
    public JRadioButton getRadioButtonFsk16() {
        return m_rbFsk16;
    }

    /**
     * getter for the radio button fsk18
     * @return the radio button fsk18
     */
    public JRadioButton getRadioButtonFsk18() {
        return m_rbFsk18;
    }

    /**
     * getter for the component displaying the description
     * @return the component displaying the description
     */
    public JTextArea getTextAreaDescription() {
        return m_taDescription;
    }

    /**
     * getter for the component displaying the chapter
     * @return the component displaying the chapter
     */
    public JTextField getTextFieldChapters() {
        return m_tfChapters;
    }

    /**
     * getter for the chapter label
     * @return the chapter label
     */
    public JLabel getLabelChapters() {
        return m_labelChapters;
    }

    /**
     * getter for the not displayed radio button  
     * @return the dummy radio button
     */
    public JRadioButton getRadioButtonDummy() {
        return m_rbDummy;
    }
}
