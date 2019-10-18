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

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Vector;

import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;

import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdContentPanel;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdMainFrame;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdCategory;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdGuiConfiguration;
import org.eclipse.jubula.examples.aut.dvdtool.persistence.DvdInvalidContentException;
import org.eclipse.jubula.examples.aut.dvdtool.persistence.DvdPersistenceException;
import org.eclipse.jubula.examples.aut.dvdtool.persistence.DvdPersistenceManager;


/**
 * This class is the manager class for all persistent data. It's a singleton.
 * 
 * Usage:
 * call open(File) for an existing File
 * or set the rootCategory via setRootCategory()
 *
 * @author BREDEX GmbH
 * @created 28.02.2008
 */
public class DvdManager  {
    /** the singleton instance reference */
    private static DvdManager manager = null;
    
    /** the root category, will be safed via save() */
    private DvdCategory m_rootCategory = null;
    
    /** the gui configuration of dvd details, will be safed via save() */
    private DvdGuiConfiguration m_guiConfiguration = new DvdGuiConfiguration();
    
    /**
     * private constructor, use method singleton() for an instance of this manager
     */
    private DvdManager() {
        // empty
    }
    
    /**
     * Implementation of the singleton pattern.
     * 
     * @return the single instance of this manager.
     */
    public static DvdManager singleton() {
        if (manager == null) {
            manager = new DvdManager();
        }
        return manager;
    }

    /**
     * saves dvd library and its gui configuration to <code>file</code>
     * @param file the file to save the library to
     * @throws DvdPersistenceException if an io error occurs 
     */
    public void save(File file) throws DvdPersistenceException {
        List list = new Vector();
        list.add(m_rootCategory);
        list.add(m_guiConfiguration);
        DvdPersistenceManager.singleton().save(file, list);
    }
    
    /**
     * opens dvd library and its gui configuration from file <code>file</code> 
     * @param file the file to load the library from
     * @param controller the controller of the main frame
     * @throws DvdPersistenceException if an io error occurs 
     * @throws DvdInvalidContentException if the file does not contain the expected data
     */
    public void open(DvdMainFrameController controller,
                     File file) throws DvdPersistenceException,
        DvdInvalidContentException {

        List list = DvdPersistenceManager.singleton().load(file);
        m_rootCategory = (DvdCategory) list.get(0);
        m_guiConfiguration = (DvdGuiConfiguration) list.get(1);
        
        restoreGuiConfiguration(controller);
    }
    
    /**
     * opens dvd library and its gui configuration from file <code>file</code> 
     * @param is the InputStream to load the library from
     * @param controller the controller of the main frame
     * @throws DvdPersistenceException if an io error occurs 
     * @throws DvdInvalidContentException if the file does not contain the expected data
     */
    public void open(DvdMainFrameController controller,
                     InputStream is) throws DvdPersistenceException,
        DvdInvalidContentException {

        List list = DvdPersistenceManager.singleton().load(is);
        m_rootCategory = (DvdCategory) list.get(0);
        m_guiConfiguration = (DvdGuiConfiguration) list.get(1);
        
        restoreGuiConfiguration(controller);
    }

    /**
     * restores the gui configuration according to gui configuration object
     * @param controller the controller of the main frame
     */
    public void restoreGuiConfiguration(DvdMainFrameController controller) {
        restoreTabPlacement(controller);
        restoreLabelPlacement(controller);
    }
    
    /**
     * @return Returns the rootCategory, may be null
     */
    public DvdCategory getRootCategory() {
        return m_rootCategory;
    }
    
    /**
     * @param rootCategory The rootCategory to set.
     */
    public void setRootCategory(DvdCategory rootCategory) {
        m_rootCategory = rootCategory;
    }
    
    /**
     * changes the tab placement to <code>tabPlacement</code>
     * @param controller the controller of the main frame
     * @param tabPlacement The tab placement to set
     */
    public void changeTabPlacement(DvdMainFrameController controller,
                                   int tabPlacement) {        
        // enable tab placement actions that will have an effect, disable others
        controller.getTabPlacementTopAction()
            .setEnabled(tabPlacement != SwingConstants.TOP);
        controller.getTabPlacementBottomAction()
            .setEnabled(tabPlacement != SwingConstants.BOTTOM);
        controller.getTabPlacementLeftAction()
            .setEnabled(tabPlacement != SwingConstants.LEFT);
        controller.getTabPlacementRightAction()
            .setEnabled(tabPlacement != SwingConstants.RIGHT);
        
        // set tab placement in gui
        DvdMainFrame frame = controller.getDvdMainFrame();
        frame.getDvdDetailTabbedPane().setTabPlacement(tabPlacement);
        
        // set tab placement in persistent object
        m_guiConfiguration.setTabPlacement(tabPlacement);
    }
    
    /**
     * restores the tab placement according to gui configuration object
     * @param controller the controller of the main frame
     */
    private void restoreTabPlacement(DvdMainFrameController controller) {
        // get tab placement from persistent object
        int tabPlacement = m_guiConfiguration.getTabPlacement();
        
        changeTabPlacement(controller, tabPlacement);                
    }
    
    /**
     * changes the radio button label placement to <code>labelPlacement</code>
     * @param controller the controller of the main frame
     * @param labelPlacement The radio button label placement to set
     */
    public void changeLabelPlacement(DvdMainFrameController controller,
                                     int labelPlacement) {
        // enable radio button label placement actions that will have an effect, 
        // disable others
        controller.getLabelPlacementTopAction()
            .setEnabled(labelPlacement != SwingConstants.TOP);
        controller.getLabelPlacementBottomAction()
            .setEnabled(labelPlacement != SwingConstants.BOTTOM);
        controller.getLabelPlacementLeftAction()
            .setEnabled(labelPlacement != SwingConstants.LEFT);
        controller.getLabelPlacementRightAction()
            .setEnabled(labelPlacement != SwingConstants.RIGHT);
        
        // set radio button label placement in gui
        setAllLabelPlacements(controller, labelPlacement);
        
        // set radio button label placement in persistent object
        m_guiConfiguration.setLabelPlacement(labelPlacement);
    }
    
    /**
     * restores the radio button label placement according to gui configuration 
     * object
     * @param controller the controller of the main frame
     */
    private void restoreLabelPlacement(DvdMainFrameController controller) {
        // get radio button label placement from persistent object
        int labelPlacement = m_guiConfiguration.getLabelPlacement();
        
        changeLabelPlacement(controller, labelPlacement);                
    }
    
    /**
     * sets the given horizontal and vertical placement for radio button labels
     * @param controller the controller of the main frame
     * @param labelPlacement The radio button label placement to set
     */
    private void setAllLabelPlacements(DvdMainFrameController controller,
            int labelPlacement) {
        
        // calculate the two constants that are needed for configuring the gui
        final int horizontalPlacement;
        if ((labelPlacement == SwingConstants.LEFT)
            || (labelPlacement == SwingConstants.RIGHT)) {
            
            horizontalPlacement = labelPlacement;
        } else {
            horizontalPlacement = SwingConstants.CENTER;
        }        
        final int verticalPlacement;
        if ((labelPlacement == SwingConstants.TOP)
            || (labelPlacement == SwingConstants.BOTTOM)) {
            
            verticalPlacement = labelPlacement;
        } else {
            verticalPlacement = SwingConstants.CENTER;
        }
        
        DvdContentPanel contentPanel = 
            controller.getDvdMainFrame().getDvdContentPanel();
        JRadioButton rbFsk6 = contentPanel.getRadioButtonFsk6();
        JRadioButton rbFsk16 = contentPanel.getRadioButtonFsk16();
        JRadioButton rbFsk18 = contentPanel.getRadioButtonFsk18();
        JPanel ratingPanel = contentPanel.getRatingPanel();
        
        rbFsk6.setHorizontalTextPosition(horizontalPlacement);
        rbFsk16.setHorizontalTextPosition(horizontalPlacement);
        rbFsk18.setHorizontalTextPosition(horizontalPlacement);
        
        rbFsk6.setVerticalTextPosition(verticalPlacement);
        rbFsk16.setVerticalTextPosition(verticalPlacement);
        rbFsk18.setVerticalTextPosition(verticalPlacement);
        
        // rebuild the radio button panel to avoid layout problems
        ratingPanel.removeAll();
        ratingPanel.add(rbFsk6);
        ratingPanel.add(rbFsk16);
        ratingPanel.add(rbFsk18);        
    }
    
}
