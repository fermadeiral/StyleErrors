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
package org.eclipse.jubula.autagent.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.UIManager;

import org.eclipse.jubula.autagent.gui.utils.AgentOMKeyProperitesUtils;
import org.eclipse.jubula.autagent.i18n.Messages;
import org.eclipse.jubula.client.core.constants.InputCodeHelper;
import org.eclipse.jubula.client.core.constants.InputCodeHelper.UserInput;

/**
 * This class is the UI for loading and saving the object mapping key combination.
 * @author BREDEX GmbH
 *
 */
public class ObjectMappingSettingsFrame extends JFrame {

    /**
     * loads the data from the file
     */
    {
        AgentOMKeyProperitesUtils.loadPropertiesFromFile();
    }

    /**
     * the settings frame
     */
    public ObjectMappingSettingsFrame() {
        this.setTitle(Messages.OMSettingsTitle);
        this.setResizable(false);
        final JFrame frame = this;
        this.setSize(300, 200);

        createContent(frame);
    }
    
    /**
     * The content of the frame
     * @param frame the frame to put the content
     */
    private void createContent(final JFrame frame) {
        final InputCodeHelper inputCodeHelper = InputCodeHelper.getInstance();
        int modifierIndex = inputCodeHelper
                .getIndexOfModifier(AgentOMKeyProperitesUtils.getModifier());
        int inputIndex = getInputIndex(inputCodeHelper);
        
        JPanel panel = new JPanel();
        GridBagLayout bagLayout = new GridBagLayout();
        GridBagConstraints constraints = createDefaultConstraints();
        JTextArea label = new JTextArea(Messages.OMSettingsText);
        label.setWrapStyleWord(true);
        label.setLineWrap(true);
        label.setEditable(false);
        label.setFocusable(false);
        label.setBackground(UIManager.getColor("Label.background")); //$NON-NLS-1$
        label.setFont(UIManager.getFont("Label.font")); //$NON-NLS-1$
        label.setBorder(UIManager.getBorder("Label.border")); //$NON-NLS-1$

        panel.setLayout(bagLayout);
        String[] keyStrings = inputCodeHelper.getModifierString();
        final JComboBox<String> modifier = new JComboBox<String>(keyStrings);
        modifier.setSelectedIndex(modifierIndex);
        String[] inPutStrings = inputCodeHelper.getInputStrings();
        
        JLabel plus = new JLabel("+"); //$NON-NLS-1$
        final JComboBox<String> inputs = new JComboBox<String>(inPutStrings);
        inputs.setSelectedIndex(inputIndex);
        JButton closeButton = new JButton(Messages.OMSettingsClose);
        closeButton.addActionListener(new ActionListener() {
            
            /** performcs close of the windows */
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });
        JButton saveButton = new JButton(Messages.OMSettingsSave);
        saveButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int modifierValue = inputCodeHelper.getModifier()[modifier
                        .getSelectedIndex()];
                UserInput inputUserInput =
                        inputCodeHelper.getInputs()[inputs.getSelectedIndex()];
                AgentOMKeyProperitesUtils.setModifier(modifierValue);
                AgentOMKeyProperitesUtils.setInput(inputUserInput);
                AgentOMKeyProperitesUtils.writePropertiesToFile();
                frame.dispose();
            }
        });
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.gridwidth = 3;
        panel.add(label, constraints);
        constraints.gridwidth = 1;
        constraints.gridy = 1;
        panel.add(modifier, constraints);
        constraints.gridx = 1;
        panel.add(plus, constraints);
        constraints.gridx = 2;
        panel.add(inputs, constraints);
        constraints.gridy = 2;
        constraints.gridx = 0;
        constraints.gridwidth = 2;
        panel.add(saveButton, constraints);
        constraints.gridx = 2;
        constraints.anchor = GridBagConstraints.EAST;
        panel.add(closeButton, constraints);
        frame.add(panel);
    }

    /**
     * gets the index for the input keys
     * @param inputCodeHelper {@link InputCodeHelper}
     * @return the index from the {@link InputCodeHelper#getInputs()}
     */
    private int getInputIndex(final InputCodeHelper inputCodeHelper) {
        UserInput input = AgentOMKeyProperitesUtils.getInput();
        UserInput[] inputKeys = inputCodeHelper.getInputs();
        int inputIndex = 0;
        for (int i = 0; i < inputKeys.length; i++) {
            UserInput arrayelement = inputKeys[i];
            if (arrayelement.equals(input)) {
                inputIndex = i;
                break;
            }
        }
        return inputIndex;
    }
    
    /**
     * @return default {@link GridBagConstraints} for the frame
     */
    private GridBagConstraints createDefaultConstraints() {
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(2, 10, 10, 10);
        constraints.anchor = GridBagConstraints.WEST;
        return constraints;
    }
}
