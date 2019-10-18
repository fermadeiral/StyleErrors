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
package org.eclipse.jubula.rc.swt.listener;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.util.IndexConverter;
import org.eclipse.jubula.rc.common.util.NameValidator;
import org.eclipse.jubula.rc.swt.components.FindSWTComponentBP;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.MappingConstants;
import org.eclipse.jubula.tools.internal.utils.StringParsing;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;

/**
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class RecordHelperSWT {    
    /** counter */
    private int m_counter = 0;
    
    /** Menupath */
    private String m_menupath = StringConstants.EMPTY;
    
    /** Treepath */
    private String m_treepath = StringConstants.EMPTY;
    
    /** logicalName number for names longer than 30 chars */
    private int m_logNameNumber = 0;
    
    /** 
     * default constructor
     */
    public RecordHelperSWT() {
        // do nothing
    } 

    
    /**
     * gets the textpath of a Menuitem, e.g. Item in Menubar or PopupMenu
     * @param w Widget
     * @return path of Item
     */
    public String getMenuPath(Widget w) {
        String menupath = StringConstants.EMPTY;
        if (w instanceof MenuItem) {
            MenuItem item = (MenuItem)w;
            Menu parent = item.getParent();
            String itemText = StringParsing.maskAndSingleQuoteText(
                    item.getText(), StringParsing.MENUTREEMASK);
            if (parent.getParentItem() != null) {
                getMenuPath(parent);
            }            
            m_menupath = m_menupath.concat(itemText);
            menupath = m_menupath;
            m_menupath = StringConstants.EMPTY;
        }        
        if (w instanceof Menu) {
            Menu parent = (Menu)w;
            MenuItem parentItem = parent.getParentItem();
            Menu grandParent = parentItem.getParent();
            String parentText = StringParsing.maskAndSingleQuoteText(
                    parentItem.getText(), StringParsing.MENUTREEMASK);
            if (grandParent.getParentItem() != null) {
                getMenuPath(grandParent);
                m_menupath = m_menupath.concat(parentText + "/"); //$NON-NLS-1$
            } else {
                m_menupath = parentText + "/"; //$NON-NLS-1$
            }
        }
        return menupath;
    }
    
    /**
     * gets the treepath of a TreeItem
     * @param w Widget
     * @return path of Item
     */
    public String getTreePath(Widget w) {
        TreeItem item = (TreeItem)w;
        if (item.getParentItem() != null) {
            m_counter++;
            getTreePath(item.getParentItem());
            m_counter--;
        }        
        String itemText = StringParsing.maskAndSingleQuoteText(
                item.getText(), StringParsing.MENUTREEMASK);
        m_treepath = m_treepath.concat(itemText);
        if (m_counter > 0) {
            m_treepath = m_treepath.concat("/"); //$NON-NLS-1$
        }
        return m_treepath;
    }
    /**
     * sets Stringthe treepath of a TreeItem
     * @param treepath String
     */
    protected void setTreePath(String treepath) {
        m_treepath = treepath;
    }
    
    /**
     * @return the IComponentIdentifier for the default mapping component Menu
     */
    protected IComponentIdentifier getMenuCompID() {
        IComponentIdentifier id = null;
        id = new ComponentIdentifier();
        id.setSupportedClassName(MappingConstants.SWT_MENU_CLASSNAME);
        id.setComponentClassName(MappingConstants.SWT_MENU_CLASSNAME);
        return id;
    }
    
    
    /**
     * @return IComponentIdentifier for the default mapping component Application
     */
    protected IComponentIdentifier getApplicationCompID() {
        IComponentIdentifier id = null;
        id = new ComponentIdentifier();
        id.setComponentClassName(MappingConstants
                .SWT_APPLICATION_COMPONENT_IDENTIFIER);
        id.setSupportedClassName(MappingConstants.SWT_APPLICATION_CLASSNAME);
        return id;
    }
        
    /**
     * get Text of component
     * @param w Widget
     * @return text if component has some
     */
    public String getTextOfComponent(Widget w) {
        String text = null;
        if (w instanceof Button) {
            Button b = (Button)w;
            text = b.getText();
            text = SwtUtils.removeMnemonics(text);
        }
        if (w instanceof Label) {
            Label l = (Label)w;
            text = l.getText();
            text = SwtUtils.removeMnemonics(text);
        }
        if (w instanceof CLabel) {
            CLabel cl = (CLabel)w;
            text = cl.getText();
            text = SwtUtils.removeMnemonics(text);
        }
        if (w instanceof TabFolder) {
            TabFolder tf = (TabFolder)w;
            text = tf.getItem(tf.getSelectionIndex()).getText();
            text = SwtUtils.removeMnemonics(text);
        }
        if (w instanceof CTabFolder) {
            CTabFolder ctf = (CTabFolder)w;
            text = ctf.getSelection().getText();
            text = SwtUtils.removeMnemonics(text);
        }
        if (w instanceof ToolItem) {
            ToolItem tbi = (ToolItem)w;
            text = tbi.getText();
            text = SwtUtils.removeMnemonics(text);
        }
        if (w instanceof Group) {
            Group grp = (Group)w;
            text = grp.getText();
            text = SwtUtils.removeMnemonics(text);
        }
        if (w instanceof Shell) {
            Shell s = (Shell)w;
            text = s.getText();
        }
        
        if (text != null && text.length() > 30) {
            text = text.substring(0, 29) + m_logNameNumber++;
        }
        
        return text;
    }
    
    
    /**
     * @param id IComponentIdentifier
     * @param actionStr String
     * @return the action to the name
     */
    public Action compSysToAction(IComponentIdentifier id, String actionStr) {
        Action a = null;
        org.eclipse.jubula.tools.internal.xml.businessmodell.Component comp;
        java.util.List compsList = new LinkedList();
        if (id.getSupportedClassName().equals(MappingConstants
                .SWT_MENU_CLASSNAME)) {
            compsList = AUTServerConfiguration.getInstance()
                .findComponents(
                    MappingConstants.SWT_MENU_DEFAULT_MAPPING_CLASSNAME);
        } else if (id.getSupportedClassName().equals(
                MappingConstants.SWT_APPLICATION_CLASSNAME)) {
            compsList = AUTServerConfiguration.getInstance().findComponents(
                    MappingConstants.SWT_APPLICATION_CLASSNAME);
        } else {
            compsList = AUTServerConfiguration.getInstance()
                .findComponents(id.getSupportedClassName());            
        }
        Iterator compsIt = compsList.iterator();
        while (compsIt.hasNext()) {
            comp = (org.eclipse.jubula.tools.internal.xml.
                businessmodell.Component) compsIt.next();
            java.util.List actionList = comp.getActions();
            Iterator it = actionList.iterator();
            while (it.hasNext()) {
                Action action = (Action) it.next();
                if (action.getName().equals(actionStr)) {
                    a = action;
                }
            }
        }
        return a;
    }
    
    /**
     * creates appendix for logical name for CAP like _btn, _cbx etc
     * @param w Widget
     * @return append for logical Name
     */
    public String getAbbreviations(Widget w) {
        String append = null;
        if (w instanceof Button) {
            append = "_btn"; //$NON-NLS-1$
        } else if (w instanceof Label || w instanceof CLabel) {
            append = "_lbl"; //$NON-NLS-1$
        } else if (w instanceof TabFolder || w instanceof CTabFolder) {
            append = "_tpn"; //$NON-NLS-1$
        } else if (w instanceof Tree) {
            append = "_tre"; //$NON-NLS-1$
        } else if (w instanceof List) {
            append = "_lst"; //$NON-NLS-1$
        } else if (w instanceof Combo || w instanceof CCombo) {
            append = "_cbx"; //$NON-NLS-1$
        } else if (w instanceof Text) {
            append = "_txf"; //$NON-NLS-1$
        } else if (w instanceof Table) {
            append = "_tbl"; //$NON-NLS-1$
        } else if (w instanceof ToolItem) {
            append = "_tbi"; //$NON-NLS-1$
        } else if (w instanceof ToolBar) {
            append = "_tb"; //$NON-NLS-1$
        } else {
            append = "_xyz"; //$NON-NLS-1$
        }
        return append;
    }
    
    /**
     * creates logical name for CAP
     * @param w Widget
     * @param id IComponentIdentifier
     * @return logical Name
     */
    protected String generateLogicalName(Widget w, IComponentIdentifier id) {
        String logName = null;
        String logicalName = null;
        String compName = FindSWTComponentBP.getComponentName(w);
        if (compName == null) {
            if (!(w instanceof TabFolder) && !(w instanceof CTabFolder)) {
                logName = getTextOfComponent(w);
            }            
            if (logName == null || logName.equals(StringConstants.EMPTY)) {
                logName = minimizeCapName(id.getComponentName());
            }
        } else {
            logName = compName;
        }
        
        logicalName = logName + getAbbreviations(w);
        
        Widget parent = getWidgetParent(w);
        while (parent != null) {
            logName = getTextOfComponent(parent); //if(parent instOf Shell){
            if (logName == null) {
                logName = minimizeCapName(parent.getClass().getName());
            }
            if ((parent instanceof Shell || parent instanceof TabFolder
                    || parent instanceof CTabFolder
                    || parent instanceof Group)
                    && !(logName.equals(StringConstants.EMPTY))) {
                logicalName = logName + "_" + logicalName; //$NON-NLS-1$
            }
            parent = getWidgetParent(parent);
        }
        
        return NameValidator.convertToValidLogicalName(logicalName);
    }
    
    /**
     * gets parent of a widget
     * @param w Widget
     * @return parent of widget
     */
    public Widget getWidgetParent(Widget w) {
        Widget wid = null;        
        if (w instanceof Button) {
            Button btn = (Button)w;
            wid = btn.getParent();
        } else if (w instanceof Label) {
            Label lbl = (Label)w;
            wid = lbl.getParent();
        } else if (w instanceof CLabel) {
            CLabel cl = (CLabel)w;
            wid = cl.getParent();
        } else if (w instanceof TabFolder) {
            TabFolder tf = (TabFolder)w;
            wid = tf.getParent();
        } else if (w instanceof CTabFolder) {
            CTabFolder ctf = (CTabFolder)w;
            wid = ctf.getParent();
        } else if (w instanceof Tree) {
            Tree tre = (Tree)w;
            wid = tre.getParent();
        } else if (w instanceof List) {
            List lst = (List)w;
            wid = lst.getParent();
        } else if (w instanceof Combo) {
            Combo cb = (Combo)w;
            wid = cb.getParent();
        } else if (w instanceof CCombo) {
            CCombo ccb = (CCombo)w;
            wid = ccb.getParent();
        } else if (w instanceof Text) {
            Text txt = (Text)w;
            wid = txt.getParent();
        } else if (w instanceof Table) {
            Table tbl = (Table)w;
            wid = tbl.getParent();
        } else if (w instanceof ToolItem) {
            ToolItem tbi = (ToolItem)w;
            wid = tbi.getParent();
        } else if (w instanceof ToolBar) {
            ToolBar tb = (ToolBar)w;
            wid = tb.getParent();
        } else if (w instanceof Group) {
            Group gp = (Group)w;
            wid = gp.getParent();
        } else if (w instanceof Composite) {
            Composite ct = (Composite)w;
            wid = ct.getParent();
        } else if (w instanceof Shell) {
            Shell sh = (Shell)w;
            wid = sh.getParent();
        }
        return wid;
    }
    
    /**
     * minimzes the component name, e.g. javax.swing.JButton_1 to JButton_1
     * @param capName String
     * @return the minimized CapName
     */
    private String minimizeCapName(String capName) {
        String minCapName = capName;

        if (minCapName.lastIndexOf(".") > -1 //$NON-NLS-1$
                && minCapName.length() > (minCapName.lastIndexOf(".") + 1)) { //$NON-NLS-1$
            minCapName = minCapName.substring(
                    minCapName.lastIndexOf(".") + 1); //$NON-NLS-1$
        }
        return minCapName;
    }
    
    /**
     * get Characters in ctrl-key-combinations
     * @param event Event
     * @return character
     */
    public static final char topKey(final Event event) {
        char character = event.character;
        boolean ctrlDown = (event.stateMask & SWT.CTRL) != 0;

        if (ctrlDown && event.character != event.keyCode
                && event.character < 0x20 
                && (event.keyCode & SWT.KEYCODE_BIT) == 0) {
            character += 0x40;
        }

        return character;
    }
    
    /**
     * check if Combo contains item
     * @param w Widget
     * @param item String
     * @return true if Combo contains Item, false otherwise
     */
    public boolean containsValue(Widget w, String item) {
        boolean contains = false;        
        String[] values = null;
        if (w instanceof Combo) {
            Combo cbx = (Combo)w;
            values = cbx.getItems();
        }
        if (w instanceof CCombo) {
            CCombo ccbx = (CCombo)w;
            values = ccbx.getItems();
        }
        
        for (int i = 0; i < values.length; i++) {
            String val = values[i];
            if (val.equals(item)) {
                contains = true; 
            }
        }
        return contains;
    }
    
    /**
     * Verifies that the given list is the dropdown list for this combo box.
     * 
     * @param list  The list to verify.
     * @param w Widget
     * @return true if list is the dropdown list for this combo box false otherwise.
     */
    protected boolean isDropdownList(List list, Widget w) {
        /*
         * Verify that the list is close enough to the combo box.
         */
        if (w instanceof CCombo) {
            CCombo ccbx = (CCombo)w;
            Rectangle comboBounds = 
                SwtUtils.getWidgetBounds(ccbx);
            Rectangle listBounds = SwtUtils.getWidgetBounds(list);
            
            // Expand the bounding rectangle for the combo box by a small amount
            int posFuzz = 5;
            int dimFuzz = posFuzz * 2;
            comboBounds.x -= posFuzz;
            comboBounds.width += dimFuzz;
            comboBounds.y -= posFuzz;
            comboBounds.height += dimFuzz;
            
            return comboBounds.intersects(listBounds);
        }
        return false;
    }
       
    /**
     * true if Widget is supported, false otherwise
     * @param w Widget
     * @return true if Widget is supported, false otherwise
     */
    protected boolean isSupportedWidget(Widget w) {
        boolean supported = false;
        
        if (w instanceof Button || w instanceof Label
                || w instanceof CLabel || w instanceof TabFolder
                || w instanceof CTabFolder || w instanceof Tree
                || w instanceof List || w instanceof Combo
                || w instanceof CCombo || w instanceof Text
                || w instanceof Table || w instanceof ToolItem
                || w instanceof ToolBar || w instanceof MenuItem
                || w instanceof Menu || w instanceof Shell
                || w instanceof Group) {
            supported = true;
        }
        
        return supported;
    }
        
    /**
     * checks if 2 ComponentIdentifier are equal by hierarchy
     * @param a1
     *      ComponentIdentifier
     * @param a2
     *      ComponentIdentifier
     * @return
     *      boolean
     */
    public boolean isCiEqual(IComponentIdentifier a1, 
        IComponentIdentifier a2) {
        java.util.List hierarchy = a1.getHierarchyNames();
        java.util.List iterHierarchy = a2.getHierarchyNames();
        if (hierarchy.size() == iterHierarchy.size()) {
            boolean match = true;
            for (int i = 0; i < hierarchy.size(); i++) {
                if (hierarchy.get(i) == null
                    && iterHierarchy.get(i) != null) {
                    match = false;
                } else if (hierarchy.get(i) != null
                    && iterHierarchy.get(i) == null) {
                    match = false;
                } else if (hierarchy.get(i) == null
                    && iterHierarchy.get(i) == null) {
                    iterHierarchy.get(i);
                } else if (!((String)hierarchy.get(i)).
                        equals(iterHierarchy.get(i))) {
                    match = false;
                }
            }
            if (match) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * @param tree Tree
     * @return the column the selected column of a tree with columns (TableTree).
     */
    public int getSelectedTreeColumn(Tree tree) {
        
        final Tree treeTable = tree;
        int column = -1;
        Rectangle treeTableBounds = 
            SwtUtils.getWidgetBounds(treeTable);
        Point cursorPosition = 
            treeTable.getDisplay().getCursorLocation();
        boolean isCursorInBounds = 
            treeTableBounds.contains(cursorPosition);
        if (isCursorInBounds) {
            Rectangle columnBounds = new Rectangle(
                treeTableBounds.x, treeTableBounds.y, 
                0, treeTableBounds.height);
            for (int i = 0; 
                i < treeTable.getColumnCount(); i++) {
                
                columnBounds.x += columnBounds.width;
                columnBounds.width = 
                    treeTable.getColumn(i).getWidth();
                if (columnBounds.contains(cursorPosition)) {
                    return IndexConverter.toUserIndex(i);
                }
            }
        }
        
        return column;
    }
    
    /**
     * @param tbl Table
     * @return the selected cell
     */
    public int[] getSelectedCell(Table tbl) {
        final Table table = tbl;
        int[] cell = {-1, -1};

        Rectangle tableBounds = 
            SwtUtils.getWidgetBounds(table);
        tableBounds.y += table.getHeaderHeight();
        Point cursorPosition = 
            table.getDisplay().getCursorLocation();
        boolean isCursorInBounds = 
            tableBounds.contains(cursorPosition);
        if (isCursorInBounds) {            
            Rectangle rowBounds = new Rectangle(
                    tableBounds.x, tableBounds.y,
                    tableBounds.width, 0);
            
            for (int k = 0; k < table.getItemCount(); k++) {                
                rowBounds.y += rowBounds.height;
                rowBounds.height = table.getItemHeight();
                if (rowBounds.contains(cursorPosition)) {
                    cell[0] = IndexConverter.toUserIndex(k);
                    break;
                }
            }

            Rectangle columnsBounds = new Rectangle(
                tableBounds.x, tableBounds.y,
                0, tableBounds.height);
            for (int i = 0; i < table.getColumnCount(); i++) {                
                columnsBounds.x += columnsBounds.width;
                columnsBounds.width = 
                    table.getColumn(i).getWidth();
                if (columnsBounds.contains(cursorPosition)) {
                    cell[1] = IndexConverter.toUserIndex(i);
                    break;
                }
            }                        
        }
        
        return cell;
    }
    
    /**
     * @param tre Tree
     * @return the selected TreeTable Node
     */
    public TreeItem getSelectedTreeTableNode(Tree tre) {
        final Tree tree = tre;
        TreeItem node = null;
        Rectangle treeBounds = 
            SwtUtils.getWidgetBounds(tree);
        treeBounds.y += tree.getHeaderHeight();
        Point cursorPosition = 
            tree.getDisplay().getCursorLocation();
        boolean isCursorInBounds = 
            treeBounds.contains(cursorPosition);
        if (isCursorInBounds) {
            TreeItem[] ti = tree.getItems();
            Rectangle rowBounds = new Rectangle(
                    treeBounds.x, treeBounds.y,
                    treeBounds.width, 0);
            for (int k = 0; k < tree.getItemCount(); k++) {                
                TreeItem treeItem = ti[k];
                rowBounds.y += rowBounds.height;
                rowBounds.height = treeItem.getBounds().height;
                if (rowBounds.contains(cursorPosition)) {
                    node = treeItem;
                    break;
                }
            }
        }
        //node = tree.getSelection()[0];
        return node;
    }
    
    /**
     * Convenience method for removing any optional accelerator text from the
     * given string. The accelerator text appears at the end of the text, and is
     * separated from the main part by a single tab character <code>'\t'</code>.
     * 
     * @param text
     *            the text
     * @return the text sans accelerator
     */
    public final String removeAcceleratorText(final String text) {
        int index = text.lastIndexOf('\t');
        if (index == -1) {
            index = text.lastIndexOf('@');
        }
        if (index >= 0) {
            return text.substring(0, index);
        }
        return text;
    }
    
    /**
     * check if menu belongs to menubar
     * @param menu Menu
     * @return true, if menu belongs to menubar, false otherwise
     */
    public boolean isMenuBarItem(Menu menu) {
        Menu mnu = menu;
        boolean isMenuBarItem = false;
        if ((mnu.getStyle() & SWT.BAR) != 0) {
            isMenuBarItem = true;
        } else {
            while (mnu.getParentMenu() != null) {
                Menu parent = mnu.getParentMenu();
                if ((parent.getStyle() & SWT.BAR) != 0) {
                    isMenuBarItem = true;
                    break;
                }                
                mnu = parent;                
            }
        }
        return isMenuBarItem;
    }
} 
