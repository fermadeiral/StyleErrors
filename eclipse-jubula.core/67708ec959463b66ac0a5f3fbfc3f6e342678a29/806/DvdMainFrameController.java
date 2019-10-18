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

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.ListModel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.examples.aut.dvdtool.DevelopmentState;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdContentPanel;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdMainFrame;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdTableModel;
import org.eclipse.jubula.examples.aut.dvdtool.gui.DvdTechPanel;
import org.eclipse.jubula.examples.aut.dvdtool.model.Dvd;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdCategory;
import org.eclipse.jubula.examples.aut.dvdtool.model.DvdDataObject;
import org.eclipse.jubula.examples.aut.dvdtool.resources.Resources;


/**
 * This is the controller class for the main frame.
 * 
 * @author BREDEX GmbH
 * @created 11.04.2005
 */
public class DvdMainFrameController {
    /** flag if some changes was made */
    private boolean m_changed;
    
    /** the frame */
    private DvdMainFrame m_frame = new DvdMainFrame();
    
    /** the tree controller */
    private DvdTreeController m_treeController;

    /** the table controller */
    private DvdTableController m_tableController;

    /** the save action */
    private DvdSaveAction m_saveAction = 
        new DvdSaveAction(Resources.getString("menu.file.save"), this); //$NON-NLS-1$

    /** the open action */
    private DvdOpenAction m_openAction =
        new DvdOpenAction(Resources.getString("menu.file.open"), this); //$NON-NLS-1$
    
    /** the exit action */
    private DvdExitAction m_exitAction = 
        new DvdExitAction(Resources.getString("menu.file.exit"), this); //$NON-NLS-1$

    /** the load action */
    private DvdLoadAction m_loadAction =
        new DvdLoadAction(Resources.getString("menu.load.load"), this); //$NON-NLS-1$
    /** the info action */
    private DvdInfoAction m_infoAction =
        new DvdInfoAction(Resources.getString("menu.help.about"), this); //$NON-NLS-1$

    /** the action for adding a category */
    private DvdAddCategoryAction m_addCategoryAction = 
        new DvdAddCategoryAction(Resources.getString("menu.edit.add.category"), this); //$NON-NLS-1$ 
    
    /** the action for enabling a category */
    private DvdDisableOrEnableCategoryAction m_enableCategoryAction = 
        new DvdDisableOrEnableCategoryAction(
                Resources.getString("menu.edit.enable.category"), //$NON-NLS-1$
                    this, true);
    
    /** the action for disabling a category */
    private DvdDisableOrEnableCategoryAction m_disableCategoryAction = 
        new DvdDisableOrEnableCategoryAction(
                Resources.getString("menu.edit.disable.category"), //$NON-NLS-1$
                    this, false);
    
    /** the action for removing a category */
    private DvdRemoveCategoryAction m_removeCategoryAction = 
        new DvdRemoveCategoryAction(Resources.getString("menu.edit.remove.category"), this); //$NON-NLS-1$ 

    /** the action for adding a dvd */
    private DvdAddDvdAction m_addDvdAction = 
        new DvdAddDvdAction(Resources.getString("menu.edit.add.dvd"), this); //$NON-NLS-1$

    /** the action for removing a dvd */
    private DvdRemoveDvdAction m_removeDvdAction = 
        new DvdRemoveDvdAction(Resources.getString("menu.edit.remove.dvd"), this); //$NON-NLS-1$
    
    /** the action for adding a language */
    private DvdAddLanguageAction m_addLanguageAction = 
        new DvdAddLanguageAction(
                Resources.getString("menu.edit.add.language"), //$NON-NLS-1$
                this);

    /** the action for removing a language */
    private DvdRemoveLanguageAction m_removeLanguageAction = 
        new DvdRemoveLanguageAction(
                Resources.getString("menu.edit.remove.language"), //$NON-NLS-1$
                this);
    
    /** the action for setting the dvd detail tab placement to top */
    private DvdTabPlacementAction m_tabPlacementTopAction = 
        new DvdTabPlacementAction(
                Resources.getString("menu.config.dvddetails.tabstotop"), //$NON-NLS-1$
                    this, SwingConstants.TOP);
    
    /** the action for setting the dvd detail tab placement to bottom */
    private DvdTabPlacementAction m_tabPlacementBottomAction = 
        new DvdTabPlacementAction(
                Resources.getString("menu.config.dvddetails.tabstobottom"), //$NON-NLS-1$
                    this, SwingConstants.BOTTOM);
    
    /** the action for setting the dvd detail tab placement to left */
    private DvdTabPlacementAction m_tabPlacementLeftAction = 
        new DvdTabPlacementAction(
                Resources.getString("menu.config.dvddetails.tabstoleft"), //$NON-NLS-1$
                    this, SwingConstants.LEFT);
    
    /** the action for setting the dvd detail tab placement to right */
    private DvdTabPlacementAction m_tabPlacementRightAction = 
        new DvdTabPlacementAction(
                Resources.getString("menu.config.dvddetails.tabstoright"), //$NON-NLS-1$
                    this, SwingConstants.RIGHT);
    
    /** the action for setting the rating panel label placement to top */
    private DvdLabelPlacementAction m_labelPlacementTopAction = 
        new DvdLabelPlacementAction(
                Resources.getString(
                    "menu.config.dvddetails.content.labelstotop"), //$NON-NLS-1$
                    this, SwingConstants.TOP);
    
    /** the action for setting the rating panel label placement to bottom */
    private DvdLabelPlacementAction m_labelPlacementBottomAction = 
        new DvdLabelPlacementAction(
                Resources.getString(
                    "menu.config.dvddetails.content.labelstobottom"), //$NON-NLS-1$
                    this, SwingConstants.BOTTOM);
    
    /** the action for setting the rating panel label placement to left */
    private DvdLabelPlacementAction m_labelPlacementLeftAction = 
        new DvdLabelPlacementAction(
                Resources.getString(
                    "menu.config.dvddetails.content.labelstoleft"), //$NON-NLS-1$
                    this, SwingConstants.LEFT);
    
    /** the action for setting the rating panel label placement to right */
    private DvdLabelPlacementAction m_labelPlacementRightAction = 
        new DvdLabelPlacementAction(
                Resources.getString(
                    "menu.config.dvddetails.content.labelstoright"), //$NON-NLS-1$
                    this, SwingConstants.RIGHT);
    
    /** the action for clearing the description text area */
    private DvdClearDescriptionAction m_clearDescriptionAction = 
        new DvdClearDescriptionAction(
                Resources.getString("action.clear.description"), //$NON-NLS-1$
                this);

    /** the action for copying of text from the chapter field to clipboard */
    private DvdCopyChapterTextToClipboardAction m_copyChapterTextAction =
        new DvdCopyChapterTextToClipboardAction(
                Resources.getString("action.copy.to.clipboard"), //$NON-NLS-1$
                this);

    /** the action for copying of text from the chapter label to clipboard */
    private DvdCopyChapterLabelToClipboardAction m_copyChapterLabelAction =
        new DvdCopyChapterLabelToClipboardAction(
                Resources.getString("action.copy.to.clipboard"), //$NON-NLS-1$
                this);

    /** the action for copying of value from bonus checkbox to clipboard */
    private DvdCopyBonusValueToClipboardAction m_copyBonusValueAction =
        new DvdCopyBonusValueToClipboardAction(
                Resources.getString("action.copy.to.clipboard"), //$NON-NLS-1$
                this);

    /** the action for copying of fsk6 radiobutton to clipboard */
    private DvdCopyFsk6RadioButtonToClipboardAction m_copyFsk6ButtonAction =
        new DvdCopyFsk6RadioButtonToClipboardAction(
                Resources.getString("action.copy.to.clipboard"), //$NON-NLS-1$
                this);

    /** the action for copying of fsk16 radiobutton to clipboard */
    private DvdCopyFsk16RadioButtonToClipboardAction m_copyFsk16ButtonAction =
        new DvdCopyFsk16RadioButtonToClipboardAction(
                Resources.getString("action.copy.to.clipboard"), //$NON-NLS-1$
                this);

    /** the action for copying of fsk18 radiobutton to clipboard */
    private DvdCopyFsk18RadioButtonToClipboardAction m_copyFsk18ButtonAction =
        new DvdCopyFsk18RadioButtonToClipboardAction(
                Resources.getString("action.copy.to.clipboard"), //$NON-NLS-1$
                this);

    /** the action for changing of tab selection */
    private DvdChangeTabSelectionAction m_changeTabSelectionAction =
        new DvdChangeTabSelectionAction(
                Resources.getString("action.change.tab.selection"), //$NON-NLS-1$
                this);

    /** the action for showing of a waiting dialog */
    private DvdShowWaitingDialogAction m_showWaitingDialogAction =
        new DvdShowWaitingDialogAction(
                Resources.getString("action.show.waiting.dialog"), //$NON-NLS-1$
                this);

    /**
     * inner class listening for window closing events
     */
    private class MyWindowListener extends WindowAdapter {
        /** 
         * activates a DvdExitAction
         * @param e the raised WindowEvent
         */
        public void windowClosing(WindowEvent e) {
            m_frame.getMenuItemExit().doClick();
        }
    }
    
    /**
     * inner class used as ListSelectionListener for language list component
     */
    private class LanguageListSelectionListener 
        implements ListSelectionListener {
        
        /**
         * {@inheritDoc}
         */
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting()) {
                boolean removeLanguage = isRemoveLanguageAllowed();
                getRemoveLanguageAction().setEnabled(removeLanguage);
            }
        }
    }
    
    /**
     * constructor, initializes this controller
     */
    public DvdMainFrameController() {
        if (DevelopmentState.instance().isV2()) {
            m_addCategoryAction = new DvdAddCategoryAction(
                    Resources.getString("menu.edit.add.category"), this); //$NON-NLS-1$ 

        }
        init();
    }

    /**
     * private method for initialisation, initialisise the actions and then
     * creates the other controller
     */
    private void init() {
        // no changes at program start
        setChanged(false);
        
        // initialize the actions
        initActions();
        
        // initialize the menu items
        initMenuItems();
        
        // initialize the shortcuts
        initShortcuts();
        
        // initialize the listeners
        initListeners();
        
        // set an empty model for the table
        m_frame.getTable().setModel(new DvdTableModel());

        // create controller for the table
        m_tableController = new DvdTableController(this);

        // create controller for the tree
        m_treeController = new DvdTreeController(this);
    }

    /**
     * private method for initialisation of the actions
     */
    private void initActions() {
        // the default enable state of the actions 
        m_addCategoryAction.setEnabled(false);
        m_enableCategoryAction.setEnabled(false);
        m_disableCategoryAction.setEnabled(false);
        m_removeCategoryAction.setEnabled(false);
        m_addDvdAction.setEnabled(false);
        m_removeDvdAction.setEnabled(false);
        m_addLanguageAction.setEnabled(false);
        m_removeLanguageAction.setEnabled(false);
        m_saveAction.setEnabled(true);
        m_openAction.setEnabled(true);
        m_exitAction.setEnabled(true);
        m_infoAction.setEnabled(true);
        m_clearDescriptionAction.setEnabled(false);
        m_copyChapterTextAction.setEnabled(false);
        m_copyChapterLabelAction.setEnabled(false);
        m_copyBonusValueAction.setEnabled(false);
        m_copyFsk6ButtonAction.setEnabled(false);
        m_copyFsk16ButtonAction.setEnabled(false);
        m_copyFsk18ButtonAction.setEnabled(false);
        m_changeTabSelectionAction.setEnabled(false);
        m_showWaitingDialogAction.setEnabled(true);
        
        // set default configuration for tab placement and
        // the enable state of the corresponding actions
        DvdManager.singleton().changeTabPlacement(this, SwingConstants.TOP);
        
        // set default configuration for radio button label placement and
        // the enable state of the corresponding actions
        DvdManager.singleton().changeLabelPlacement(this, SwingConstants.RIGHT);
    }

    /**
     * private method for initialisation of the menu items
     */
    private void initMenuItems() {
        // simulated development states
        final boolean isVersion1 = DevelopmentState.instance().isV1();
        final boolean isVersion2 = DevelopmentState.instance().isV2();
        final boolean isVersion3 = DevelopmentState.instance().isV3();

        // set the actions
        m_frame.getMenuItemAddCategory().setAction(m_addCategoryAction);
        m_frame.getMenuItemEnableCategory().setAction(m_enableCategoryAction);
        m_frame.getMenuItemDisableCategory().setAction(m_disableCategoryAction);
        m_frame.getMenuItemRemoveCategory().setAction(m_removeCategoryAction);
        m_frame.getMenuItemAddDvd().setAction(m_addDvdAction);
        m_frame.getMenuItemRemoveDvd().setAction(m_removeDvdAction);
        m_frame.getMenuItemAddLanguage().setAction(m_addLanguageAction);
        m_frame.getMenuItemRemoveLanguage().setAction(m_removeLanguageAction);
        m_frame.getMenuItemSave().setAction(m_saveAction);
        m_frame.getMenuItemOpen().setAction(m_openAction);
        m_frame.getMenuItemExit().setAction(m_exitAction);
        m_frame.getMenuItemInfo().setAction(m_infoAction);
        if (isVersion1 || isVersion2 || isVersion3) {
            m_frame.getMenuItemLoad().setAction(m_loadAction);
        }
        
        m_frame.getMenuItemDvdDetailsTabsToTop()
            .setAction(m_tabPlacementTopAction);
        m_frame.getMenuItemDvdDetailsTabsToBottom()
            .setAction(m_tabPlacementBottomAction);
        m_frame.getMenuItemDvdDetailsTabsToLeft()
            .setAction(m_tabPlacementLeftAction);
        m_frame.getMenuItemDvdDetailsTabsToRight()
            .setAction(m_tabPlacementRightAction);
        
        m_frame.getMenuItemRatingPanelLabelsToTop()
            .setAction(m_labelPlacementTopAction);
        m_frame.getMenuItemRatingPanelLabelsToBottom()
            .setAction(m_labelPlacementBottomAction);
        m_frame.getMenuItemRatingPanelLabelsToLeft()
            .setAction(m_labelPlacementLeftAction);
        m_frame.getMenuItemRatingPanelLabelsToRight()
            .setAction(m_labelPlacementRightAction);
        
        m_frame.getMenuItemShowWaitingDialog()
            .setAction(m_showWaitingDialogAction);
    }

    /**
     * private method for initialisation of the shortcuts
     */
    private void initShortcuts() {
        // set mnemonics
        m_frame.getMenuFile().setMnemonic(
                Resources.getString("menu.file.mnemonic").charAt(0)); //$NON-NLS-1$
        m_frame.getMenuEdit().setMnemonic(
                Resources.getString("menu.edit.mnemonic").charAt(0)); //$NON-NLS-1$
        m_frame.getMenuConfig().setMnemonic(
                Resources.getString("menu.config.mnemonic").charAt(0)); //$NON-NLS-1$
        m_frame.getMenuHelp().setMnemonic(
                Resources.getString("menu.help.mnemonic").charAt(0)); //$NON-NLS-1$
        
        // set hotkeys
        m_frame.getMenuItemOpen().setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        m_frame.getMenuItemSave().setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        m_frame.getMenuItemInfo().setAccelerator(
                KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
    }

    /**
     * private method for initialisation of the listeners
     */
    private void initListeners() {
        // handling window events
        m_frame.addWindowListener(new MyWindowListener());
        m_frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);

        // add listeners to language list component
        JList languageList = m_frame.getDvdTechMainPanel().getListLanguages();
        languageList.addListSelectionListener(
                new LanguageListSelectionListener());
        languageList.addMouseListener(
                new PopupListener(createLanguageListPopupMenu()));
        
        // add listener to description text area
        DvdContentPanel contentPanel = m_frame.getDvdContentPanel();
        JTextArea descriptionTextArea = contentPanel.getTextAreaDescription();
        descriptionTextArea.addMouseListener(
                new PopupListener(createDescriptionTextAreaPopupMenu()));
        
        // add listener to chapter field on content panel of dvd details 
        JTextField chapterTF = contentPanel.getTextFieldChapters();
        chapterTF.addMouseListener(
                new PopupListener(createChapterFieldPopupMenu()));
        
        // add listener to chapter label on content panel of dvd details 
        JLabel chapterLabel = contentPanel.getLabelChapters(); 
        chapterLabel.addMouseListener(
                new PopupListener(createChapterLabelPopupMenu()));
        
        // add listener to bonus checkbox on tech panel of dvd details 
        DvdTechPanel techPanel = 
            m_frame.getDvdTechMainPanel().getDvdTechPanel();
        JCheckBox bonusCheckBox = techPanel.getCheckBoxBonus();
        bonusCheckBox.addMouseListener(
                new PopupListener(createBonusCheckBoxPopupMenu()));
        
        // add listener to fsk6 radiobutton on content panel of dvd details 
        JRadioButton fsk6Button = contentPanel.getRadioButtonFsk6();
        fsk6Button.addMouseListener(
                new PopupListener(createFsk6ButtonPopupMenu()));
        
        // add listener to fsk16 radiobutton on content panel of dvd details 
        JRadioButton fsk16Button = contentPanel.getRadioButtonFsk16();
        fsk16Button.addMouseListener(
                new PopupListener(createFsk16ButtonPopupMenu()));
        
        // add listener to fsk18 radiobutton on content panel of dvd details 
        JRadioButton fsk18Button = contentPanel.getRadioButtonFsk18();
        fsk18Button.addMouseListener(
                new PopupListener(createFsk18ButtonPopupMenu()));
        
        // add listener to tabbed pane in dvd details 
        m_frame.getDvdDetailTabbedPane().addMouseListener(
                new PopupListener(createTabbedPanePopupMenu()));
    }

    /**
     * creates and adds a new category with the <code>name</code> to each 
     * selected category, via treeController
     * @param name the name for the category to create and add
     */
    public void addCategory(String name) {
        m_tableController.updateModel();
        m_treeController.addNewCategoryToSelectedCategories(name);
    }
    
    /**
     * adds a <code>dvd</code> to the currently selected category, via tree controller 
     * @param dvd the dvd to add
     */
    public void addDvd(Dvd dvd) {
        m_tableController.updateModel();
        m_treeController.addDvd(dvd);
    }
    
    /**
     * remove the current selected DVD, via the table controller
     */
    public void removeCurrentDvd() {
        m_tableController.updateModel();
        m_treeController.removeDvd(m_tableController.getDvd());
    }
    
    /**
     * sets the enable state of the current selected category, via tree 
     * controller and table controller
     * @param enable the enable state to set
     */
    public void setCurrentCategoryEnableState(boolean enable) {
        m_tableController.updateModel();
        m_tableController.setAllDvdsEnableState(enable);
        m_treeController.setCurrentCategoryEnableState(enable);        
    }
    
    /**
     * removes the current selected category, via tree controller
     */
    public void removeCurrentCategory() {
        m_tableController.updateModel();
        m_treeController.removeCurrentCategory();
    }

    /**
     * adds a new language with the <code>newLanguageName</code> to the 
     * language list
     * @param newLanguageName the name for the language to create and add
     */
    public void addLanguage(String newLanguageName) {
        m_tableController.updateModel();        
        addLanguageInDvdDetails(newLanguageName);
    }

    /**
     * adds a new language with the <code>newLanguageName</code> to the 
     * language list
     * @param newLanguageName the name for the language to create and add
     */
    private void addLanguageInDvdDetails(String newLanguageName) {
        JList languageList = m_frame.getDvdTechMainPanel().getListLanguages();
        int selectedIndex = languageList.getSelectedIndex();
        ListModel currentModel = languageList.getModel();
        
        // build new model
        String[] newModel = new String[currentModel.getSize() + 1];
        for (int i = 0; i < currentModel.getSize(); i++) {
            newModel[i] = (String) currentModel.getElementAt(i);
        }
        newModel[newModel.length - 1] = newLanguageName;
        
        // update model of languageList
        languageList.setListData(newModel);
        languageList.setSelectedIndex(selectedIndex);
    }
    
    /**
     * removes the selected language from the language list, if there are
     * at least two languages; otherwise nothing happens
     */
    public void removeLanguage() {
        m_tableController.updateModel();        
        removeLanguageInDvdDetails();
    }

    /**
     * removes the selected language from the language list, if there are
     * at least two languages; otherwise nothing happens
     */
    private void removeLanguageInDvdDetails() {
        JList languageList = m_frame.getDvdTechMainPanel().getListLanguages();
        int selectedIndex = languageList.getSelectedIndex();
        ListModel currentModel = languageList.getModel();
        
        if (isRemoveLanguageAllowed()) {
            // build new model
            String[] newModel = new String[currentModel.getSize() - 1];
            for (int i = 0; i < newModel.length; i++) {
                if (i < selectedIndex) {
                    newModel[i] = (String) currentModel.getElementAt(i);
                } else {
                    newModel[i] = (String) currentModel.getElementAt(i + 1);
                }
            }
            
            // update model of languageList
            languageList.setListData(newModel);
        } else {
            throw new RuntimeException(
                "The action must be disabled under these circumstances!"); //$NON-NLS-1$
        }
    }
    
    /**
     * update the model, after a call to this method all editable properties are
     * stored in the model (currently the displayed Dvd instance)
     */
    public void updateModel() {
        m_tableController.updateModel();
    }

    /**
     * updates the enable state of the actions that depend on the current 
     * selected category
     */
    public void updateDisableOrEnableActions() {
        JTree tree = getDvdMainFrame().getTreePanel().getTree();
        TreePath selectionPath = 
            tree.getSelectionModel().getLeadSelectionPath();
        boolean multipleTreeSelections = (tree.getSelectionCount() > 1);

        if ((selectionPath == null) || multipleTreeSelections) {
            m_enableCategoryAction.setEnabled(false);
            m_disableCategoryAction.setEnabled(false);
            m_addDvdAction.setEnabled(false);            
        } else {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) selectionPath
                    .getLastPathComponent();
            DvdDataObject data = (DvdDataObject) node.getUserObject();
            boolean categoryEnabled = data.getCategory().isEnabled();
            
            if (data.hasDvds()) { 
                m_enableCategoryAction.setEnabled(!categoryEnabled);
                m_disableCategoryAction.setEnabled(categoryEnabled);
                m_addDvdAction.setEnabled(categoryEnabled);            
            } else {
                m_enableCategoryAction.setEnabled(false);
                m_disableCategoryAction.setEnabled(false);
                m_addDvdAction.setEnabled(true);            
            }
        }
    }

    /**
     * getter for the frame
     * @return the frame
     */
    public DvdMainFrame getDvdMainFrame() {
        return m_frame;
    }
    
    /**
     * @return Returns the exitAction.
     */
    public DvdExitAction getExitAction() {
        return m_exitAction;
    }
    
    /**
     * @return Returns the infoAction.
     */
    public DvdInfoAction getInfoAction() {
        return m_infoAction;
    }
    
    /**
     * @return Returns the openAction.
     */
    public DvdOpenAction getOpenAction() {
        return m_openAction;
    }
    
    /**
     * @return Returns the saveAction.
     */
    public DvdSaveAction getSaveAction() {
        return m_saveAction;
    }
    
    /**
     * @return Returns the addCategoryAction.
     */
    public DvdAddCategoryAction getAddCategoryAction() {
        return m_addCategoryAction;
    }
    
    /**
     * @return Returns the enableCategoryAction
     */
    public DvdDisableOrEnableCategoryAction getEnableCategoryAction() {
        return m_enableCategoryAction;
    }
    
    /**
     * @return Returns the disableCategoryAction.
     */
    public DvdDisableOrEnableCategoryAction getDisableCategoryAction() {
        return m_disableCategoryAction;
    }
    
    /**
     * @return Returns the removeCategoryAction.
     */
    public DvdRemoveCategoryAction getRemoveCategoryAction() {
        return m_removeCategoryAction;
    }
    
    /**
     * @return Returns the addDvdAction.
     */
    public DvdAddDvdAction getAddDvdAction() {
        return m_addDvdAction;
    }
    
    /**
     * @return Returns the removeDvdAction.
     */
    public DvdRemoveDvdAction getRemoveDvdAction() {
        return m_removeDvdAction;
    }
    
    /**
     * @return Returns the addLanguageAction.
     */
    public DvdAddLanguageAction getAddLanguageAction() {
        return m_addLanguageAction;
    }
    
    /**
     * @return Returns the removeLanguageAction.
     */
    public DvdRemoveLanguageAction getRemoveLanguageAction() {
        return m_removeLanguageAction;
    }
    
    /**
     * @return Returns the tabPlacementTopAction.
     */
    public DvdTabPlacementAction getTabPlacementTopAction() {
        return m_tabPlacementTopAction;
    }
    
    /**
     * @return Returns the tabPlacementBottomAction.
     */
    public DvdTabPlacementAction getTabPlacementBottomAction() {
        return m_tabPlacementBottomAction;
    }
    
    /**
     * @return Returns the tabPlacementLeftAction.
     */
    public DvdTabPlacementAction getTabPlacementLeftAction() {
        return m_tabPlacementLeftAction;
    }
    
    /**
     * @return Returns the tabPlacementRightAction.
     */
    public DvdTabPlacementAction getTabPlacementRightAction() {
        return m_tabPlacementRightAction;
    }
    
    /**
     * @return Returns the labelPlacementTopAction.
     */
    public DvdLabelPlacementAction getLabelPlacementTopAction() {
        return m_labelPlacementTopAction;
    }
    
    /**
     * @return Returns the labelPlacementBottomAction.
     */
    public DvdLabelPlacementAction getLabelPlacementBottomAction() {
        return m_labelPlacementBottomAction;
    }
    
    /**
     * @return Returns the labelPlacementLeftAction.
     */
    public DvdLabelPlacementAction getLabelPlacementLeftAction() {
        return m_labelPlacementLeftAction;
    }
    
    /**
     * @return Returns the labelPlacementRightAction.
     */
    public DvdLabelPlacementAction getLabelPlacementRightAction() {
        return m_labelPlacementRightAction;
    }
    
    /**
     * @return Returns the clearDescriptionAction.
     */
    public DvdClearDescriptionAction getClearDescriptionAction() {
        return m_clearDescriptionAction;
    }
    
    /**
     * @return Returns the copyChapterTextAction.
     */
    public DvdCopyChapterTextToClipboardAction getCopyChapterTextAction() {
        return m_copyChapterTextAction;
    }
    
    /**
     * @return Returns the copyChapterLabelAction.
     */
    public DvdCopyChapterLabelToClipboardAction getCopyChapterLabelAction() {
        return m_copyChapterLabelAction;
    }
    
    /**
     * @return Returns the copyBonusValueAction.
     */
    public DvdCopyBonusValueToClipboardAction getCopyBonusValueAction() {
        return m_copyBonusValueAction;
    }
    
    /**
     * @return Returns the copyFsk6ButtonAction.
     */
    public DvdCopyFsk6RadioButtonToClipboardAction getCopyFsk6ButtonAction() {
        return m_copyFsk6ButtonAction;
    }
    
    /**
     * @return Returns the copyFsk16ButtonAction.
     */
    public DvdCopyFsk16RadioButtonToClipboardAction getCopyFsk16ButtonAction() {
        return m_copyFsk16ButtonAction;
    }
    
    /**
     * @return Returns the copyFsk18ButtonAction.
     */
    public DvdCopyFsk18RadioButtonToClipboardAction getCopyFsk18ButtonAction() {
        return m_copyFsk18ButtonAction;
    }
    
    /**
     * @return Returns the changeTabSelectionAction.
     */
    public DvdChangeTabSelectionAction getChangeTabSelectionAction() {
        return m_changeTabSelectionAction;
    }
    
    /**
     * @return Returns the showWaitingDialogAction.
     */
    public DvdShowWaitingDialogAction getShowWaitingDialogAction() {
        return m_showWaitingDialogAction;
    }
    
    /**
     * @return Returns the tableController.
     */
    public DvdTableController getTableController() {
        return m_tableController;
    }
    
    /**
     * @return Returns the treeController.
     */
    public DvdTreeController getTreeController() {
        return m_treeController;
    }
    
    /**
     * @return returns the flag wheter changes was made.
     */
    public boolean isChanged() {
        return m_changed;
    }
    
    /**
     * sets the flag wheter changes was made
     * @param changed The changed to set.
     */
    public void setChanged(boolean changed) {
        m_changed = changed;
    }
    
    /**
     * inform this controller, that the library was saved
     * @param name the name of the saved library
     */
    public void saved(String name) {
        updateTitle(name);
        setChanged(false);
    }
    
    /**
     * inform this controller that a library was opened
     * @param name the name of the opened library
     */
    public void opened(String name) {
        createTree(DvdManager.singleton().getRootCategory());
        setChanged(false);
        updateTitle(name);
        m_frame.getSplitPane().resetToPreferredSizes();
    }
    
    /**
     * creates the tree (via treeConroller)
     * @param root the category which is used for the root node
     */
    private void createTree(DvdCategory root) {
        m_treeController.createTree(root);
    }
    
    /**
     * adds <code>suffix</code> to the title
     * @param suffix additional information
     */
    private void updateTitle(String suffix) {
        m_frame.updateTitle(suffix);
    }
    
    /**
     * Returns a boolean that indicates whether removing the selected language
     * from the language list is allowed.
     * @return boolean that indicates whether removing is allowed
     */
    private boolean isRemoveLanguageAllowed() {
        JList languageList = m_frame.getDvdTechMainPanel().getListLanguages();
        int selectedIndex = languageList.getSelectedIndex();
        ListModel currentModel = languageList.getModel();
        int modelSize = currentModel.getSize();
        
        boolean removeLanguage = ((selectedIndex >= 0) 
                && (selectedIndex < modelSize) && (modelSize >= 2));
        return removeLanguage;
    }

    /**
     * Creates the popupmenu for the language list component
     * @return the popupmenu for the language list component
     */
    private JPopupMenu createLanguageListPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_addLanguageAction);
        pm.add(m_removeLanguageAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the description text area
     * @return the popupmenu for the description text area
     */
    private JPopupMenu createDescriptionTextAreaPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_clearDescriptionAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the chapter field
     * @return the popupmenu for the chapter field
     */
    private JPopupMenu createChapterFieldPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_copyChapterTextAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the chapter label
     * @return the popupmenu for the chapter label
     */
    private JPopupMenu createChapterLabelPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_copyChapterLabelAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the bonus checkbox
     * @return the popupmenu for the bonus checkbox
     */
    private JPopupMenu createBonusCheckBoxPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_copyBonusValueAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the fsk6 radiobutton
     * @return the popupmenu for the fsk6 radiobutton
     */
    private JPopupMenu createFsk6ButtonPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_copyFsk6ButtonAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the fsk16 radiobutton
     * @return the popupmenu for the fsk16 radiobutton
     */
    private JPopupMenu createFsk16ButtonPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_copyFsk16ButtonAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the fsk18 radiobutton
     * @return the popupmenu for the fsk18 radiobutton
     */
    private JPopupMenu createFsk18ButtonPopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_copyFsk18ButtonAction);
        return pm;
    }

    /**
     * Creates the popupmenu for the tabbed pane
     * @return the popupmenu for the tabbed pane
     */
    private JPopupMenu createTabbedPanePopupMenu() {
        JPopupMenu pm = new JPopupMenu();
        pm.add(m_changeTabSelectionAction);
        return pm;
    }

}