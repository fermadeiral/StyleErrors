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
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.Border;

import org.eclipse.jubula.tools.internal.constants.StringConstants;

/**
 * @author BREDEX GmbH
 * @created 05.08.2008
 * 
 */
public class ObservationConsole extends JFrame {
    
    /**
     * CheckMode Label
     */
    private JLabel m_checkModeLabel = new JLabel();
    
    /**
     * CheckMode on/off
     */
    private JLabel m_checkOnOffLabel = new JLabel();
    
    /**
     * recorded ActionsLabel
     */
    private JLabel m_recActionLabel = new JLabel();
    
    /**
     * ActionConsole
     */
    private JTextArea m_textArea = new JTextArea();

    /**
     * constructor
     *      Jframe
     */
    public ObservationConsole() {
        super();
        createPart();
    }

    /**
     * 
     */
    private void createPart() {
        JPanel contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        
        JPanel northPanel = new JPanel();
        northPanel.add(m_recActionLabel);
        
        JPanel southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout());
        southPanel.add(m_checkModeLabel);
        southPanel.add(m_checkOnOffLabel);
        
        //center area
        m_textArea = new JTextArea(8, 42);
        m_textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(m_textArea);
        
        contentPane.add(northPanel, "North"); //$NON-NLS-1$;
        contentPane.add(southPanel, "South"); //$NON-NLS-1$;
        contentPane.add(scrollPane, "Center"); //$NON-NLS-1$;
        
        Border bd1 = BorderFactory.createEtchedBorder();
        contentPane.setBorder(bd1);

        m_checkModeLabel.setText("CheckMode: "); //$NON-NLS-1$
        m_checkOnOffLabel.setText("off"); //$NON-NLS-1$
        m_recActionLabel.setText("Observed Actions: "); //$NON-NLS-1$        
        
        setTitle("Observation Console"); //$NON-NLS-1$
        setContentPane(contentPane);
    }


    /**
     * @return Returns the checkModeLabel.
     */
    public JLabel getCheckLabel() {
        return m_checkOnOffLabel;
    }
    
    /**
     * @param check boolean
     */
    public void setCheckLabel(boolean check) {
        if (check) {
            m_checkOnOffLabel.setText("on"); //$NON-NLS-1$
            m_checkOnOffLabel.setForeground(Color.red);
        } else {
            m_checkOnOffLabel.setText("off"); //$NON-NLS-1$
            m_checkOnOffLabel.setForeground(Color.black);
        }

    }
    
    /**
     * @return Returns the checkModeLabel.
     */
    public JLabel getTextArea() {
        return m_checkOnOffLabel;
    }
        
    /**
     * @param recAction String
     */
    public void appendTextArea(String recAction) {
        m_textArea.append(recAction);
        m_textArea.append("\n"); //$NON-NLS-1$
        m_textArea.setCaretPosition(m_textArea.getDocument().getLength());
    }
    
    /**
     */
    public void clearTextArea() {
        m_textArea.setText(StringConstants.EMPTY);
    }

}
