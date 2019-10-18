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
package org.eclipse.jubula.examples.aut.dvdtool.control;

import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdContentPanel;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdTableModel;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdTechMainPanel;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdTechPanel;
import org.eclipse.jubula.examples.aut.dvdtool.model.Dvd;


/**
 * This is the controller class for the table.
 * 
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdTableController implements ListSelectionListener {
    /** empty string */
    private static final String EMPTY_STRING = ""; //$NON-NLS-1$

    /** the main frame contoller, holding the actions, etc. */
    private final DvdMainFrameController m_controller;

    /** the table, set by the constructor */
    private final JTable m_table;

    /** a dvd model instance, see valueChanged() */
    private Dvd m_dvd = null;

    /**
     * public constructor
     * 
     * @param mainFrameController
     *            the controller of the main frame
     */
    public DvdTableController(DvdMainFrameController mainFrameController) {
        m_controller = mainFrameController;
        m_table = mainFrameController.getDvdMainFrame().getTable();

        init();
    }

    /**
     * private method for initialisation
     */
    private void init() {
        m_table.getSelectionModel().addListSelectionListener(this);
        m_table.addMouseListener(new PopupListener(createPopupMenu()));
        
        enablePanels(false);
    }

    /**
     * updates the panels to display <code>dvd</code>
     * 
     * @param dvd
     *            an instance of the model, will not be changed
     */
    private void updatePanels(Dvd dvd) {
        DvdContentPanel contentPanel = m_controller.getDvdMainFrame()
                .getDvdContentPanel();
        DvdTechMainPanel techMainPanel = m_controller.getDvdMainFrame()
                .getDvdTechMainPanel();
        DvdTechPanel techPanel = techMainPanel.getDvdTechPanel();

        if (dvd != null) {
            contentPanel.getTextFieldChapters().setText(
                    String.valueOf(dvd.getChapters()));
            contentPanel.getTextAreaDescription().setText(dvd.getDescription());
            switch (dvd.getFsk()) {
                case Dvd.FSK_6:
                    contentPanel.getRadioButtonFsk6().setSelected(true);
                    break;
                case Dvd.FSK_16:
                    contentPanel.getRadioButtonFsk16().setSelected(true);
                    break;
                case Dvd.FSK_18:
                    contentPanel.getRadioButtonFsk18().setSelected(true);
                    break;
                default:
                    // do nothing
            }

            techMainPanel.getListLanguages().setSelectedIndices(
                    dvd.getLanguages());

            techPanel.getTextFieldLength().setText(
                    String.valueOf(dvd.getLength()));
            techPanel.getCheckBoxBonus().setSelected(dvd.hasBonus());
            techPanel.getComboBoxRegionCode().setSelectedIndex(
                    dvd.getRegionCode());
            
            enablePanels(true);
        } else {
            clearPanels();
            enablePanels(false);
        }
    }

    /**
     * enables or disables the panels
     * 
     * @param enable
     *            flag foe enable or disable
     */
    private void enablePanels(boolean enable) {
        DvdContentPanel contentPanel = m_controller.getDvdMainFrame()
                .getDvdContentPanel();
        DvdTechMainPanel techMainPanel = m_controller.getDvdMainFrame()
                .getDvdTechMainPanel();
        DvdTechPanel techPanel = techMainPanel.getDvdTechPanel();

        contentPanel.getTextFieldChapters().setEditable(enable);
        contentPanel.getTextAreaDescription().setEditable(enable);
        contentPanel.getRadioButtonFsk6().setEnabled(enable);
        contentPanel.getRadioButtonFsk16().setEnabled(enable);
        contentPanel.getRadioButtonFsk18().setEnabled(enable);
        
        techMainPanel.getListLanguages().setEnabled(enable);

        techPanel.getTextFieldLength().setEditable(enable);
        techPanel.getCheckBoxBonus().setEnabled(enable);
        techPanel.getComboBoxRegionCode().setEnabled(enable);
    }
    
    /**
     * clears the fields of the detail panels
     */
    private void clearPanels() {
        DvdContentPanel contentPanel = m_controller.getDvdMainFrame()
                .getDvdContentPanel();
        DvdTechMainPanel techMainPanel = m_controller.getDvdMainFrame()
                .getDvdTechMainPanel();
        DvdTechPanel techPanel = techMainPanel.getDvdTechPanel();

        contentPanel.getTextFieldChapters().setText(EMPTY_STRING);
        contentPanel.getTextAreaDescription().setText(EMPTY_STRING);
        contentPanel.getRadioButtonDummy().setSelected(true);

        techMainPanel.getListLanguages().clearSelection();

        techPanel.getTextFieldLength().setText(EMPTY_STRING);
        techPanel.getCheckBoxBonus().setSelected(false);
        techPanel.getComboBoxRegionCode().setSelectedIndex(0);
    }
    
    /**
     * puts the current displayed dvd to the model
     */
    public void updateModel() {
        updateModel(m_dvd);
    }
    
    /**
     * puts the diplayed dvd to the given model <code>dvd</code>
     * 
     * @param dvd
     *            an instance of the model, will holding the displayed data
     */
    private void updateModel(Dvd dvd) {
        if (m_table.isEditing() && !m_table.getCellEditor().stopCellEditing()) {
            m_table.getCellEditor().cancelCellEditing();
        }
        if (dvd != null) {
            DvdContentPanel contentPanel = m_controller.getDvdMainFrame()
                    .getDvdContentPanel();
            DvdTechMainPanel techMainPanel = m_controller.getDvdMainFrame()
                    .getDvdTechMainPanel();
            DvdTechPanel techPanel = techMainPanel.getDvdTechPanel();

            try {
                dvd.setChapters(Integer.parseInt(contentPanel
                        .getTextFieldChapters().getText()));
            } catch (NumberFormatException nfe) { // NOPMD by zeb on 10.04.07 14:08
                // Do nothing: This keeps the previous value of chapters
            }
            dvd.setDescription(contentPanel.getTextAreaDescription().getText());
            if (contentPanel.getRadioButtonFsk6().isSelected()) {
                dvd.setFsk(Dvd.FSK_6);
            } else if (contentPanel.getRadioButtonFsk16().isSelected()) {
                dvd.setFsk(Dvd.FSK_16);
            } else {
                dvd.setFsk(Dvd.FSK_18);
            }

            dvd.setLanguages(techMainPanel.getListLanguages()
                    .getSelectedIndices());

            try {
                dvd.setLength(Integer.parseInt(techPanel.getTextFieldLength()
                        .getText()));
            } catch (NumberFormatException nfe) { // NOPMD by zeb on 10.04.07 14:08
                // Do nothing: This keeps the previous value of text field length
            }
            dvd.setBonus(techPanel.getCheckBoxBonus().isSelected());
            dvd.setRegionCode(techPanel.getComboBoxRegionCode()
                    .getSelectedIndex());
            
            m_controller.setChanged(true);
        }
    }
    
    /**
     * enables / disable the actions, depending on the selection of dvd table
     * @param enable indicates whether actions should be enabled or disabled
     */
    private void updateActions(boolean enable) {
        m_controller.getRemoveDvdAction().setEnabled(enable);
        m_controller.getAddLanguageAction().setEnabled(enable);
        m_controller.getClearDescriptionAction().setEnabled(enable);
        m_controller.getCopyChapterTextAction().setEnabled(enable);
        m_controller.getCopyChapterLabelAction().setEnabled(enable);
        m_controller.getCopyBonusValueAction().setEnabled(enable);
        m_controller.getCopyFsk6ButtonAction().setEnabled(enable);
        m_controller.getCopyFsk16ButtonAction().setEnabled(enable);
        m_controller.getCopyFsk18ButtonAction().setEnabled(enable);
        m_controller.getChangeTabSelectionAction().setEnabled(enable);
         
        // the remove language action depends on language list selection
    }

    /**
     * {@inheritDoc}
     */
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            // it's the dvd, which was selected before !
            updateModel(m_dvd);
            if (!m_table.getSelectionModel().isSelectionEmpty()) {
                DvdTableModel tm = (DvdTableModel) m_table.getModel();
                m_dvd = tm.getDvd(m_table.getSelectedRow());
                updateActions(true);
            } else {
                updateActions(false);
                m_dvd = null;
            }
            updatePanels(m_dvd);
        }
    }
    
    /**
     * @return Returns the dvd.
     */
    public Dvd getDvd() {
        return m_dvd;
    }

    /**
     * sets the enable state for all dvds
     * @param enableState the enable state to be set for all dvds
     */
    public void setAllDvdsEnableState(boolean enableState) {
        if (!enableState) {
            m_table.getSelectionModel().clearSelection();
        }
        m_table.setEnabled(enableState);
    }
    
    /**
     * Creates the popupmenu
     * @return the popupmenu
     */
    private JPopupMenu createPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_controller.getAddDvdAction());
        pm.add(m_controller.getRemoveDvdAction());
        return pm;
    }

}
