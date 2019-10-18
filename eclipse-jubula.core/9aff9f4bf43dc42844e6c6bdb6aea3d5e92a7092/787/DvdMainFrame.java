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
import java.awt.CardLayout;
import java.awt.Dimension;
import java.io.IOException;

import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import org.eclipse.jubula.examples.aut.dvdtool.DevelopmentState;
import org.eclipse.jubula.examples.aut.dvdtool.control.DvdTableTransferHandler;
import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This class is the main frame of the dvd tool. <br>
 * <br>
 * Layout <br>
 * <br>
 * <table  border="1">
 * <tr>
 * <td colspan="2">Menu</td>
 * </tr>
 * <tr>
 * <td rowspan="2">Tree</td>
 * <td> HTML or Table</td>
 * </tr>
 * <tr>
 * <td>Cards</td>
 * </tr>
 * </table>
 *
 * <br>
 * Between the tree and the right side is a splitter.
 *  
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdMainFrame extends JFrame {
    /** constant for CardLayout, card LOGO */
    public static final String CARD_LOGO = "Logo"; //$NON-NLS-1$
    /** constant for CardLayout, card DATA */
    public static final String CARD_DATA = "Data"; //$NON-NLS-1$
    /** the separator for the title text */
    private static final String TITLESEPARATOR = " - "; //$NON-NLS-1$
    
    /** panel displaying 'technical' information */
    private DvdTechMainPanel m_techMainPanel = new DvdTechMainPanel();
    /** panel displaying 'content' information */
    private DvdContentPanel m_contentPanel = new DvdContentPanel();
    /** tabbed pane containing the dvd details */
    private JTabbedPane m_dvdDetailTabbedPane = new JTabbedPane();
    /** the menu 'File' */
    private JMenu m_menuFile = new JMenu(); 
    /** the menu 'Edit' */
    private JMenu m_menuEdit = new JMenu(); 
    /** the menu 'Config' */
    private JMenu m_menuConfig = new JMenu();
    /** the menu 'Load' */
    private JMenu m_menuLoad = new JMenu();
    /** the menu 'Help' */
    private JMenu m_menuHelp = new JMenu();
    /** the menu entry 'Add category' */
    private JMenuItem m_menuItemAddCategory = new JMenuItem();
    /** the menu entry 'Enable category' */
    private JMenuItem m_menuItemEnableCategory = new JMenuItem();
    /** the menu entry 'Disable category' */
    private JMenuItem m_menuItemDisableCategory = new JMenuItem();
    /** the menu entry 'Remove category' */
    private JMenuItem m_menuItemRemoveCategory = new JMenuItem();
    /** the menu entry 'Add DVD' */
    private JMenuItem m_menuItemAddDvd = new JMenuItem();
    /** the menu entry 'Remove DVD' */
    private JMenuItem m_menuItemRemoveDvd = new JMenuItem();
    /** the menu entry 'Add language' */
    private JMenuItem m_menuItemAddLanguage = new JMenuItem();
    /** the menu entry 'Remove language' */
    private JMenuItem m_menuItemRemoveLanguage = new JMenuItem();
    /** the menu entry 'Show waiting dialog' */
    private JMenuItem m_menuItemShowWaitingDialog = new JMenuItem();
    /** the menu entry 'Exit' */
    private JMenuItem m_menuItemExit = new JMenuItem();
    /** the menu entry 'Info' */
    private JMenuItem m_menuItemInfo = new JMenuItem();
    /** the menu entry 'Save' */
    private JMenuItem m_menuItemSave = new JMenuItem();
    /** the menu entry 'Open' */
    private JMenuItem m_menuItemOpen = new JMenuItem();
    /** the menu entry 'Load' */
    private JMenuItem m_menuItemLoad = new JMenuItem();
    /** the menu 'Dvd details' */
    private JMenu m_menuDvdDetails = new JMenu();
    /** the menu entry 'Dvd details / Tabs to top' */
    private JMenuItem m_menuItemDvdDetailsTabsToTop = new JMenuItem();
    /** the menu entry 'Dvd details / Tabs to bottom' */
    private JMenuItem m_menuItemDvdDetailsTabsToBottom = new JMenuItem();
    /** the menu entry 'Dvd details / Tabs to left' */
    private JMenuItem m_menuItemDvdDetailsTabsToLeft = new JMenuItem();
    /** the menu entry 'Dvd details / Tabs to right' */
    private JMenuItem m_menuItemDvdDetailsTabsToRight = new JMenuItem();
    /** the menu 'Rating panel' */
    private JMenu m_menuRatingPanel = new JMenu();
    /** the menu entry 'Rating panel / Labels to top' */
    private JMenuItem m_menuItemRatingPanelLabelsToTop = new JMenuItem();
    /** the menu entry 'Rating panel / Labels to bottom' */
    private JMenuItem m_menuItemRatingPanelLabelsToBottom = new JMenuItem();
    /** the menu entry 'Rating panel / Labels to left' */
    private JMenuItem m_menuItemRatingPanelLabelsToLeft = new JMenuItem();
    /** the menu entry 'Rating panel / Labels to right' */
    private JMenuItem m_menuItemRatingPanelLabelsToRight = new JMenuItem();
    /** the file chooser for opening and saving libraries */
    private JFileChooser m_fileChooser = new DvdFileChooser();
    
    /** the table displaying an overview */
    private JTable m_table = new JTable();
    /** the tree panel displaying the structure */
    private DvdTreePanel m_treePanel = new DvdTreePanel();
    /** the panel displaying the detail cards */
    private JPanel m_cardPanel = new JPanel();

    /** the split pane */
    private JSplitPane m_splitPane = 
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

    /**
     * constructor, initialises the frame
     */
    public DvdMainFrame() {
        super();
        init();
    }

    /**
     * private method for initialisation
     */
    private void init() {
        // set title and icon 
        setTitle(Resources.getString("application.title")); //$NON-NLS-1$
        setIconImage(Resources.getImageIcon(Resources.APP_ICON).getImage());
        
        // create the menu bar
        createMenuBar();

        // initialize the tabbed pane
        m_dvdDetailTabbedPane.setName("tabbedPane"); //$NON-NLS-1$
        m_dvdDetailTabbedPane
            .addTab(Resources.getString("technical"), m_techMainPanel); //$NON-NLS-1$
        m_dvdDetailTabbedPane
            .addTab(Resources.getString("content"), m_contentPanel); //$NON-NLS-1$

        // the panel containing the table and the tabbed pane
        JPanel dataPanel = new JPanel();
        dataPanel.setLayout(new BorderLayout());

        // add the tabbed pane 
        dataPanel.add(m_dvdDetailTabbedPane, BorderLayout.SOUTH);

        // set the renderer and the selection mode for the table
        EvenOddTableCellRenderer renderer = new EvenOddTableCellRenderer();
        m_table.setName("table"); //$NON-NLS-1$
        m_table.setDefaultRenderer(Object.class, renderer);
        m_table.setDefaultRenderer(String.class, renderer);
        m_table.setDefaultRenderer(Number.class, renderer);
        m_table.setDefaultRenderer(Boolean.class, renderer);
        m_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        m_table.setTransferHandler(new DvdTableTransferHandler());
        m_table.setDragEnabled(true);
        
        // add the table
        dataPanel.add(new JScrollPane(m_table), BorderLayout.CENTER);

        // editor pane for displaying an HTML page
        JEditorPane editorPane = new JEditorPane();
        editorPane.setName("editorPane"); //$NON-NLS-1$
        editorPane.setEditable(false);
        String url = Resources.getFileUrl(Resources.getWelcomeScreenName());
            
        try {
            editorPane.setPage(url);
        } catch (IOException e) {
            // ignore
        }

        // panel with card layout
        m_cardPanel.setLayout(new CardLayout());
        m_cardPanel.add(editorPane, CARD_LOGO);
        m_cardPanel.add(dataPanel, CARD_DATA);
        m_cardPanel.setMinimumSize(new Dimension(0, 0));

        m_treePanel.setMinimumSize(new Dimension(0, 0));

        // create the split pane
        m_splitPane.setOneTouchExpandable(true);
        m_splitPane.setLeftComponent(m_treePanel);
        m_splitPane.setRightComponent(m_cardPanel);
        m_splitPane.setResizeWeight(0.2);
        
        // use an opaque JPanel as content pane
        JPanel panel = new JPanel();
        panel.setOpaque(true);
        panel.setLayout(new BorderLayout());
        panel.add(m_splitPane, BorderLayout.CENTER);
        
        getContentPane().add(panel);
        pack();
    }

    /**
     *  creates the menu bar with all menues
     */
    private void createMenuBar() {
        // simulated development states
        final boolean isVersion1 = DevelopmentState.instance().isV1();
        final boolean isVersion2 = DevelopmentState.instance().isV2();
        final boolean isVersion3 = DevelopmentState.instance().isV3();

        JMenuBar menuBar = new JMenuBar();
        menuBar.setName("menuBar"); //$NON-NLS-1$
        m_menuFile.setText(Resources.getString("menu.file")); //$NON-NLS-1$
        m_menuFile.add(m_menuItemOpen);
        m_menuFile.add(m_menuItemSave);
        m_menuFile.addSeparator();
        if (isVersion2 || isVersion3) {
            m_menuFile.add(m_menuItemLoad);
            m_menuFile.addSeparator();
        }
        m_menuFile.add(m_menuItemExit);
        m_menuEdit.setText(Resources.getString("menu.edit")); //$NON-NLS-1$
        m_menuEdit.add(m_menuItemAddCategory);
        m_menuEdit.add(m_menuItemEnableCategory);
        m_menuEdit.add(m_menuItemDisableCategory);
        m_menuEdit.add(m_menuItemRemoveCategory);
        m_menuEdit.addSeparator();
        m_menuEdit.add(m_menuItemAddDvd);
        m_menuEdit.add(m_menuItemRemoveDvd);
        m_menuEdit.addSeparator();
        m_menuEdit.add(m_menuItemAddLanguage);
        m_menuEdit.add(m_menuItemRemoveLanguage);
        m_menuEdit.addSeparator();
        m_menuEdit.add(m_menuItemShowWaitingDialog);
        m_menuConfig.setText(Resources.getString("menu.config")); //$NON-NLS-1$
        m_menuConfig.add(m_menuDvdDetails);
        if (isVersion1) {
            m_menuLoad.setText(Resources.getString("menu.load")); //$NON-NLS-1$
            m_menuLoad.add(m_menuItemLoad);
        }
        m_menuDvdDetails.setText(Resources.getString(
                "menu.config.dvddetails")); //$NON-NLS-1$        
        m_menuDvdDetails.add(m_menuItemDvdDetailsTabsToTop);
        m_menuDvdDetails.add(m_menuItemDvdDetailsTabsToBottom);
        m_menuDvdDetails.add(m_menuItemDvdDetailsTabsToLeft);
        m_menuDvdDetails.add(m_menuItemDvdDetailsTabsToRight);
        m_menuDvdDetails.add(m_menuRatingPanel);
        m_menuRatingPanel.setText(Resources.getString(
                "menu.config.dvddetails.content")); //$NON-NLS-1$        
        m_menuRatingPanel.add(m_menuItemRatingPanelLabelsToTop);
        m_menuRatingPanel.add(m_menuItemRatingPanelLabelsToBottom);
        m_menuRatingPanel.add(m_menuItemRatingPanelLabelsToLeft);
        m_menuRatingPanel.add(m_menuItemRatingPanelLabelsToRight);
        m_menuHelp.setText(Resources.getString("menu.help")); //$NON-NLS-1$
        m_menuHelp.add(m_menuItemInfo);

        menuBar.add(m_menuFile);
        menuBar.add(m_menuEdit);
        menuBar.add(m_menuConfig);
        if (isVersion1) {
            menuBar.add(m_menuLoad);
        }
        menuBar.add(m_menuHelp);
        setJMenuBar(menuBar);
    }

    /**
     * getter for the menu entry 'Add Category'
     * @return the menu item 'Add Category'
     */
    public JMenuItem getMenuItemAddCategory() {
        return m_menuItemAddCategory;
    }

    /**
     * getter for the menu entry 'Enable category'
     * @return the menu item 'Enable category'
     */
    public JMenuItem getMenuItemEnableCategory() {
        return m_menuItemEnableCategory;
    }
    
    /**
     * getter for the menu entry 'Disable category'
     * @return the menu item 'Disable category'
     */
    public JMenuItem getMenuItemDisableCategory() {
        return m_menuItemDisableCategory;
    }
    
    /**
     * getter for the menu entry 'Remove Category'
     * @return the menu item 'Remove Category'
     */
    public JMenuItem getMenuItemRemoveCategory() {
        return m_menuItemRemoveCategory;
    }
    
    /**
     * getter for the menu entry 'Add DVD'
     * @return the menu item 'Add DVD'
     */
    public JMenuItem getMenuItemAddDvd() {
        return m_menuItemAddDvd;
    }

    /**
     * getter for the menu entry 'Remove DVD'
     * @return the menu item 'Remove DVD'
     */
    public JMenuItem getMenuItemRemoveDvd() {
        return m_menuItemRemoveDvd;
    }
    
    /**
     * getter for the menu entry 'Add language'
     * @return the menu item 'Add language'
     */
    public JMenuItem getMenuItemAddLanguage() {
        return m_menuItemAddLanguage;
    }

    /**
     * getter for the menu entry 'Remove language'
     * @return the menu item 'Remove language'
     */
    public JMenuItem getMenuItemRemoveLanguage() {
        return m_menuItemRemoveLanguage;
    }
    
    /**
     * getter for the menu entry 'Show waiting dialog'
     * @return the menu item 'Show waiting dialog'
     */
    public JMenuItem getMenuItemShowWaitingDialog() {
        return m_menuItemShowWaitingDialog;
    }
    
    /**
     * getter for the menu 'File'
     * @return the menu 'File'
     */
    public JMenu getMenuFile() {
        return m_menuFile;
    }
    
    /**
     * getter for the menu 'Edit'
     * @return the menu 'Edit'
     */
    public JMenu getMenuEdit() {
        return m_menuEdit;
    }
    
    /**
     * getter for the menu 'Config'
     * @return the menu 'Config'
     */
    public JMenu getMenuConfig() {
        return m_menuConfig;
    }
    
    /**
     * getter for the menu 'Help'
     * @return the menu 'Help'
     */
    public JMenu getMenuHelp() {
        return m_menuHelp;
    }
    
    /**
     * getter for the menu entry 'Exit'
     * @return the menu item 'Exit'
     */
    public JMenuItem getMenuItemExit() {
        return m_menuItemExit;
    }
    
    /**
     * getter for the menu entry 'Info'
     * @return the menu item 'Info'
     */
    public JMenuItem getMenuItemInfo() {
        return m_menuItemInfo;
    }

    /**
     * getter for the menu entry 'Save'
     * @return the menu item 'Save'
     */
    public JMenuItem getMenuItemSave() {
        return m_menuItemSave;
    }

    /**
     * getter for the menu entry 'Open'
     * @return the menu item 'Open'
     */
    public JMenuItem getMenuItemOpen() {
        return m_menuItemOpen;
    }
    
    /**
     * getter for the menu entry 'Dvd details / Tabs to top'
     * @return the menu item 'Dvd details / Tabs to top'
     */
    public JMenuItem getMenuItemDvdDetailsTabsToTop() {
        return m_menuItemDvdDetailsTabsToTop;
    }
    
    /**
     * getter for the menu entry 'Dvd details / Tabs to bottom'
     * @return the menu item 'Dvd details / Tabs to bottom'
     */
    public JMenuItem getMenuItemDvdDetailsTabsToBottom() {
        return m_menuItemDvdDetailsTabsToBottom;
    }
    
    /**
     * getter for the menu entry 'Dvd details / Tabs to left'
     * @return the menu item 'Dvd details / Tabs to left'
     */
    public JMenuItem getMenuItemDvdDetailsTabsToLeft() {
        return m_menuItemDvdDetailsTabsToLeft;
    }
    
    /**
     * getter for the menu entry 'Dvd details / Tabs to right'
     * @return the menu item 'Dvd details / Tabs to right'
     */
    public JMenuItem getMenuItemDvdDetailsTabsToRight() {
        return m_menuItemDvdDetailsTabsToRight;
    }
    
    /**
     * getter for the menu entry 'Rating panel / Labels to top'
     * @return the menu item 'Rating panel / Labels to top'
     */
    public JMenuItem getMenuItemRatingPanelLabelsToTop() {
        return m_menuItemRatingPanelLabelsToTop;
    }
    
    /**
     * getter for the menu entry 'Rating panel / Labels to bottom'
     * @return the menu item 'Rating panel / Labels to bottom'
     */
    public JMenuItem getMenuItemRatingPanelLabelsToBottom() {
        return m_menuItemRatingPanelLabelsToBottom;
    }
    
    /**
     * getter for the menu entry 'Rating panel / Labels to left'
     * @return the menu item 'Rating panel / Labels to left'
     */
    public JMenuItem getMenuItemRatingPanelLabelsToLeft() {
        return m_menuItemRatingPanelLabelsToLeft;
    }
    
    /**
     * getter for the menu entry 'Rating panel / Labels to right'
     * @return the menu item 'Rating panel / Labels to right'
     */
    public JMenuItem getMenuItemRatingPanelLabelsToRight() {
        return m_menuItemRatingPanelLabelsToRight;
    }
    
    /**
     * getter for the 'technical' panel
     * @return the panel 'technical'
     */
    public DvdTechMainPanel getDvdTechMainPanel() {
        return m_techMainPanel;
    }

    /**
     * getter for the 'content' panel
     * @return the panel 'content'
     */
    public DvdContentPanel getDvdContentPanel() {
        return m_contentPanel;
    }

    /**
     * getter for the 'dvd details' tabbed pane
     * @return the tabbed pane 'dvd details'
     */
    public JTabbedPane getDvdDetailTabbedPane() {
        return m_dvdDetailTabbedPane;
    }
    
    /**
     * getter for the table
     * @return the table
     */
    public JTable getTable() {
        return m_table;
    }

    /**
     * getter for the panel with the tree
     * @return the tree panel 
     */
    public DvdTreePanel getTreePanel() {
        return m_treePanel;
    }

    /**
     * @return Returns the fileChooser.
     */
    public JFileChooser getFileChooser() {
        return m_fileChooser;
    }
    
    /**
     * makes the card named <code>card</code> visible
     * @param card the name of the card, use the public constant
     * defined by this class
     */
    public void showCard(String card) {
        ((CardLayout)m_cardPanel.getLayout()).show(m_cardPanel, card);
    }
    
    /**
     * adds <code>suffix</code> to the standard title
     * @param suffix additional information, like filename
     */
    public void updateTitle(String suffix) {
        setTitle(Resources.getString("application.title") //$NON-NLS-1$ 
                + TITLESEPARATOR + suffix); 
    }
    
    /**
     * @return the split pane
     */
    public JSplitPane getSplitPane() {
        return m_splitPane;
    }

    /**
     * @return the m_menuItemLoad
     */
    public JMenuItem getMenuItemLoad() {
        return m_menuItemLoad;
    }
}
