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
package org.eclipse.jubula.autagent.common.remote.dialogs;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.eclipse.jubula.tools.internal.i18n.CompSystemI18n;
import org.eclipse.jubula.tools.internal.i18n.I18n;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.jubula.tools.internal.xml.businessmodell.ValueSetElement;


/**
 * @author BREDEX GmbH
 * @created 20.06.2005
 * 
 */
public class ChooseCheckModeDialog extends JFrame {
    
    /**
     * okAndCheckOnButton Button
     */
    private JButton m_okAndCheckOnButton = new JButton();
    
    /**
     * okButStopCheckButton Button
     */
    private JButton m_okButStopCheckButton = new JButton();

    /**
     * cancel Button
     */
    private JButton m_cancelButton = new JButton();

    /**
     * nameText
     */
    private JLabel m_nameLabelText = new JLabel();
    
    /**
     * name
     */
    private JLabel m_nameLabel = new JLabel();

    /**
     * actionLabel
     */
    private JLabel m_actionLabel = new JLabel();

    /**
     * actionCombo
     */
    private JComboBox<String> m_actionCombo = new JComboBox<String>();
    
    /**
     * middle Panel for content
     */
    private JPanel m_middlePanel = new JPanel();

    /**
     * list of parameter TextFields
     */
    private List<JComponent> m_parameter = new ArrayList<JComponent>();
    
    /**
     * values to check
     */
    private Map m_checkValues = null;
    
    /**
     * default Operator for Observation
     */
    private String m_operator = "equals"; //$NON-NLS-1$
    
    /**
     * default PathType for Observation
     */
    private String m_pathType = "absolute"; //$NON-NLS-1$
    
    /**
     * default mouseButton for checking Context Menu Entries
     */
    private String m_mouseButtonRight = "3"; //$NON-NLS-1$

    /**
     * constructor
     *      Jframe
     */
    public ChooseCheckModeDialog() {
        super();
        createPart();
    }

    /**
     * 
     */
    private void createPart() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        southPanel.add(m_okAndCheckOnButton);
        southPanel.add(m_okButStopCheckButton);
        southPanel.add(m_cancelButton);
        contentPane.add(southPanel, "South"); //$NON-NLS-1$
        //middlePanel.setSize(100, 200);
        contentPane.add(m_middlePanel, "Center"); //$NON-NLS-1$
        createParameterPanel(new Action());
        Border bd1 = BorderFactory.createEtchedBorder();
        contentPane.setBorder(bd1);

        m_nameLabelText.setText(
            I18n.getString("ChooseCheckTypeDialog.nameLabel")); //$NON-NLS-1$
        m_cancelButton.setText(
            I18n.getString("ChooseCheckTypeDialog.cancel")); //$NON-NLS-1$
        m_okAndCheckOnButton.setText(
            I18n.getString("ChooseCheckTypeDialog.okAndCheckOn")); //$NON-NLS-1$
        m_okButStopCheckButton.setText(
                I18n.getString("ChooseCheckTypeDialog.okButStopCheck")); //$NON-NLS-1$
        m_actionLabel.setText(
            I18n.getString("ChooseCheckTypeDialog.action")); //$NON-NLS-1$
        
        setTitle(I18n.getString("ChooseCheckTypeDialog.title")); //$NON-NLS-1$
        setContentPane(contentPane);
    }

    
    /**
     * creates the parameterPanel
     * @param action the action with parameters
     */
    public void createParameterPanel(Action action) {
        List params = null;
        if (action !=  null) {
            params = action.getParams();            
        } else {
            params = new ArrayList(0);
        }
        m_middlePanel.removeAll();
        m_middlePanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        //m_middlePanel.setLayout(new GridLayout(2 + params.size(), 2, 16, 16));
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 10, 5);
        c.gridx = 0;
        c.gridy = 0;
        m_middlePanel.add(m_nameLabelText, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 10, 5);
        c.gridx = 1;
        c.gridy = 0;
        m_middlePanel.add(getNameLabel(), c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 10, 5);
        c.gridx = 0;
        c.gridy = 1;
        m_middlePanel.add(m_actionLabel, c);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 5, 10, 5);
        c.gridx = 1;
        c.gridy = 1;
        m_middlePanel.add(m_actionCombo, c);
        m_parameter.clear();
        int index = 1;
        int gridcount = 2;
        for (Iterator paramIter = params.iterator(); paramIter.hasNext();) {
            Param param = (Param)paramIter.next();
            if (param != null) {
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = new Insets(10, 5, 10, 5);
                c.gridx = 0;
                c.gridy = gridcount;
                m_middlePanel.add(new JLabel(CompSystemI18n.getString(
                    param.getName())), c);
                JComponent paramComp = createParamComponent(param);
                m_parameter.add(paramComp);
                c.fill = GridBagConstraints.HORIZONTAL;
                c.insets = new Insets(10, 5, 10, 5);
                c.gridx = 1;
                c.gridy = gridcount;
                m_middlePanel.add(paramComp, c);
                gridcount++;
            } else {
                c.gridx = 0;
                c.gridy = gridcount;
                m_middlePanel.add(new JLabel(I18n.getString("ChooseCheckTypeDialog.parameter") + index), c); //$NON-NLS-1$
                index++;
                gridcount++;
            }
        }
        pack();
    }
    
    
    /**
     * creates the JComponents for the given Param
     * @param param the Param whose JComponent to create
     * @return a JComponent for the given Param
     */
    private JComponent createParamComponent(Param param) {
        Object value = m_checkValues.get(param.getName());
        
        if (param.hasValueSet()) {
            JComboBox<String> combo = new JComboBox<String>();
            for (Iterator valueIter = param.valueSetIterator(); 
                valueIter.hasNext();) {
                
                ValueSetElement valElem = (ValueSetElement)valueIter.next();
                combo.addItem(valElem.getValue());
            }
            if (value != null) {
                combo.setSelectedItem(value);
            } else if (param.getName().equals("CompSystem.Operator")) { //$NON-NLS-1$
                combo.setSelectedItem(m_operator);
            } else if (param.getName().equals("CompSystem.ValueOperator")) { //$NON-NLS-1$
                combo.setSelectedItem(m_operator);
            } else if (param.getName().equals("CompSystem.PathType")) { //$NON-NLS-1$
                combo.setSelectedItem(m_pathType);
            } else if (param.getName().equals("CompSystem.SearchType")) { //$NON-NLS-1$
                combo.setSelectedItem(m_pathType);
            } else if (param.getName().equals("CompSystem.MouseButton")) { //$NON-NLS-1$
                combo.setSelectedItem(m_mouseButtonRight);
            } else {
                combo.setSelectedIndex(0);                
            }
            return combo;
        }

        JTextField textField;
        if (param.getType().equals("java.lang.Integer")) { //$NON-NLS-1$
            NumberFormat nf = NumberFormat.getIntegerInstance();
            nf.setGroupingUsed(false);
            textField = new JFormattedTextField(nf);
        } else {
            textField = new JTextField();
        }
        textField.setColumns(25);
        if (m_checkValues != null) {
            if (value != null) {
                String val = value.toString();
                textField.setText(val);
            }
        }
        return textField;
    }

    /**
     * @return Returns the nameLabel.
     */
    public JLabel getNameLabel() {
        return m_nameLabel;
    }
    /**
     * @param nameLabel The nameLabel to set.
     */
    public void setNameLabel(String nameLabel) {
        m_nameLabel.setText(nameLabel);
    }
    /**
     * @return Returns the cancelButton.
     */
    public JButton getCancelButton() {
        return m_cancelButton;
    }
    /**
     * @return Returns the okButton.
     */
    public JButton getOkAndCheckOnButton() {
        return m_okAndCheckOnButton;
    }
    /**
     * @return Returns the okButton.
     */
    public JButton getOkButStopCheckButton() {
        return m_okButStopCheckButton;
    }
    
    /**
     * sets the values of the combobox
     * @param l
     *      List
     */
    public void setActions(List<String> l) {
        Iterator<String> iter = l.iterator();
        while (iter.hasNext()) {
            m_actionCombo.addItem(iter.next());
        }
    }
    
    /**
     * @return Returns the values to check.
     */
    public Map getCheckValues() {
        return m_checkValues;
    }
    
    /**
     * sets the checkValue
     * @param checkValues Map
     */
    public void setCheckValues(Map checkValues) {
        m_checkValues = checkValues;
    }

    /**
     * @return Returns the parameterTextField.
     */
    public List getParameter() {
        Iterator iter = m_parameter.iterator();
        List<Object> result = new ArrayList<Object>();
        while (iter.hasNext()) {
            Object component = iter.next();
            if (component instanceof JTextField) {
                result.add(((JTextField)component).getText());                
            } else if (component instanceof JComboBox) {
                result.add(((JComboBox)component).getSelectedItem());
            }
        }
        return result;
    }
    
    /**
     * @return the textFields.
     */
    public List getTextFields() {
        Iterator iter = m_parameter.iterator();
        List<Object> result = new ArrayList<Object>();
        while (iter.hasNext()) {
            Object component = iter.next();
            if (component instanceof JTextField) {
                result.add(component);                
            }
        }
        return result;
    }
    
    /**
     * @return Returns the actionCombo.
     */
    public int getAction() {
        return m_actionCombo.getSelectedIndex();
    }
    /**
     * @return Returns the actionCombo.
     */
    public JComboBox getActionCombo() {
        return m_actionCombo;
    }
}
