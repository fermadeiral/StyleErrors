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

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.eclipse.jubula.examples.aut.dvdtool.control.DvdListTransferHandler;
import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This class displays all the 'technical' information for a dvd. <br>
 * Uses <code>DvdTechMainPanel</code> 
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdTechMainPanel extends JPanel {
    /** the list with the languages */
    private JList m_listLanguages = new JList();
    /** the panel with 'technical' information */
    private DvdTechPanel m_techPanel = new DvdTechPanel();

    /**
     * public constructor, initialises the panel
     */
    public DvdTechMainPanel() {
        super();
        init();
    }

    /**
     * private method for initialisation
     */
    private void init() {
        m_listLanguages.setName("languageList"); //$NON-NLS-1$
        m_listLanguages.setListData(Constants.LANGUAGES);
        m_listLanguages.setTransferHandler(new DvdListTransferHandler());
        m_listLanguages.setDragEnabled(true);
        setLayout(new BorderLayout());
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(new JLabel(Resources.getString("languages")), BorderLayout.NORTH); //$NON-NLS-1$
        JScrollPane sp = new JScrollPane(m_listLanguages);
        panel.add(sp, BorderLayout.CENTER);
        add(panel, BorderLayout.EAST);
        add(m_techPanel, BorderLayout.CENTER);
    }

    /** 
     * @return the list containing the languages
     */
    public JList getListLanguages() {
        return m_listLanguages;
    }

    /**
     * @return the inner panel with 'technical' information
     */
    public DvdTechPanel getDvdTechPanel() {
        return m_techPanel;
    }
}