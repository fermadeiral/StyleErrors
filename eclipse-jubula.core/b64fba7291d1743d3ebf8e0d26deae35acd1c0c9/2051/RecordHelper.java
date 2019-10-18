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
package org.eclipse.jubula.rc.swing.listener;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.NameValidator;
import org.eclipse.jubula.rc.swing.tester.util.TesterUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.objects.ComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.objects.MappingConstants;
import org.eclipse.jubula.tools.internal.utils.StringParsing;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;


/**
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class RecordHelper {    
    /** The logging. */
    private static AutServerLogger log = 
        new AutServerLogger(RecordHelper.class);
    
    /** 
     * Map for translating keycodes from keyevents to the ITE 
     * Integer <=> String
     */
    private static final Map<Integer, String> KEYCODE_MAP = 
        new HashMap<Integer, String>();

    static {
        // Swing Key Code <=> Value to Enter
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_ENTER), new String("ENTER"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_SPACE), new String("SPACE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_BACK_SPACE), new String("BACK_SPACE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_TAB), new String("TAB"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_ESCAPE), new String("ESCAPE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_PAUSE), new String("PAUSE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_END), new String("END"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_HOME), new String("HOME"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_INSERT), new String("INSERT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_DELETE), new String("DELETE")); //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_UP), new String("UP"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_DOWN), new String("DOWN"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_LEFT), new String("LEFT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_RIGHT), new String("RIGHT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_PAGE_UP), new String("PAGE_UP"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_PAGE_DOWN), new String("PAGE_DOWN"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F1), new String("F1"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F2), new String("F2"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F3), new String("F3"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F4), new String("F4"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F5), new String("F5"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F6), new String("F6"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F7), new String("F7"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F8), new String("F8"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F9), new String("F9"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F10), new String("F10"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F11), new String("F11"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_F12), new String("F12"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD0), new String("NUMPAD0"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD1), new String("NUMPAD1"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD2), new String("NUMPAD2"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD3), new String("NUMPAD3"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD4), new String("NUMPAD4"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD5), new String("NUMPAD5"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD6), new String("NUMPAD6"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD7), new String("NUMPAD7"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD8), new String("NUMPAD8"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMPAD9), new String("NUMPAD9"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_ADD), new String("ADD"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_MULTIPLY), new String("MULTIPLY"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_DIVIDE), new String("DIVIDE"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_SUBTRACT), new String("SUBTRACT"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_SEPARATOR), new String("SEPARATOR"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_DECIMAL), new String("DECIMAL"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_PLUS), new String("PLUS"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_PERIOD), new String("PERIOD"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_COMMA), new String("COMMA"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_MINUS), new String("MINUS"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUMBER_SIGN), new String("NUMBER_SIGN"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_CIRCUMFLEX), new String("CIRCUMFLEX"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_LESS), new String("LESS"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_NUM_LOCK), new String("NUM_LOCK"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_SCROLL_LOCK), new String("SCROLL_LOCK"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_PRINTSCREEN), new String("PRINTSCREEN"));  //$NON-NLS-1$
        KEYCODE_MAP.put(new Integer(KeyEvent.VK_CAPS_LOCK), new String("CAPS_LOCK"));  //$NON-NLS-1$
    }
    
    /** Map for translating modifiers from keyevents to the ITE 
     *  Integer <=> String
     */
    private static final Map<Integer, String> MODIFIER_MAP = 
        new HashMap<Integer, String>();

    static {
        // Swing Modifier <=> Value to Enter
        MODIFIER_MAP.put(new Integer(InputEvent.SHIFT_MASK), new String("shift"));  //$NON-NLS-1$
        MODIFIER_MAP.put(new Integer(InputEvent.CTRL_MASK), new String("control"));  //$NON-NLS-1$
        MODIFIER_MAP.put(new Integer(InputEvent.ALT_MASK), new String("alt"));  //$NON-NLS-1$
        MODIFIER_MAP.put(new Integer(InputEvent.CTRL_MASK | InputEvent.ALT_MASK), new String("control alt"));  //$NON-NLS-1$
        MODIFIER_MAP.put(new Integer(InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK), new String("control shift"));  //$NON-NLS-1$
    }

    
    /** mp */
    private String m_menupath = StringConstants.EMPTY;
    
    /** logicalName number for names longer than 30 chars */
    private int m_logNameNumber = 0;
    
    /** 
     * default constructor
     */
    public RecordHelper() {
        // do nothing
    }    

    /**
     * @return the IComponentIdentifier for the default mapping component Menu
     */
    protected IComponentIdentifier getMenuCompID() {
        IComponentIdentifier id = new ComponentIdentifier();
        id.setComponentClassName(MappingConstants.SWING_MENU_CLASSNAME);
        id.setSupportedClassName(MappingConstants.SWING_MENU_CLASSNAME);
        return id;
    }
    
    /**
     * @return IComponentIdentifier for the default mapping component Application
     */
    protected IComponentIdentifier getApplicationCompID() {
        IComponentIdentifier id = new ComponentIdentifier();
        id.setComponentClassName(MappingConstants
                .SWING_APPLICATION_COMPONENT_IDENTIFIER);
        id.setSupportedClassName(MappingConstants
                        .SWING_APPLICATION_COMPONENT_IDENTIFIER);
        return id;
    }
    
    /**
     * @param keycode keycode of keyevent
     * @return The keyname or <code>null</code>, if the keycode doesn't exist in the <code>KEYCODE_MAP</code>
     * @throws StepExecutionException If the key code cannot be converted to a keycode name due to the reflection call
     */
    public static String getKeyName(int keycode)
        throws StepExecutionException {

        String keyname = KEYCODE_MAP.containsKey(new Integer(keycode)) 
            ? KEYCODE_MAP.get(new Integer(keycode)) : null;

        if (keyname == null && log.isInfoEnabled()) {
            log.info("The keycode '" + keycode //$NON-NLS-1$
                + "' is not a key expression. Returning null."); //$NON-NLS-1$
        }
            
        return keyname;
    }
    
    /**
     * @param modifier modifier of keyevent
     * @return The modifier or <code>null</code>, if the modifier doesn't exist in the <code>MODIFIER_MAP</code>
     * @throws StepExecutionException If the modifier cannot be converted to a modifier name due to the reflection call
     */
    public static String getModifierName(int modifier)
        throws StepExecutionException {

        String modname = MODIFIER_MAP.containsKey(new Integer(modifier)) 
            ? MODIFIER_MAP.get(new Integer(modifier)) : null;

        if (modname == null && log.isInfoEnabled()) {
            log.info("The modifier '" + modifier //$NON-NLS-1$
                + "' is not a key expression. Returning null."); //$NON-NLS-1$
        }
            
        return modname;
    } 
    
    /**
     * get Text of component
     * @param c Component
     * @return text if component has some
     */
    public String getTextOfComponent(Component c) {
        String text = null;
        if (c instanceof AbstractButton) {
            AbstractButton jb = (AbstractButton)c;
            text = jb.getText(); 
        }
        if (c instanceof JLabel) {
            JLabel jl = (JLabel)c;
            text = jl.getText();
        }
        if (c instanceof JTabbedPane) {
            JTabbedPane tp = (JTabbedPane)c;
            text = tp.getTitleAt(tp.getSelectedIndex());
        }
        if (c instanceof JFrame) {
            JFrame jf = (JFrame)c;
            text = jf.getTitle();
        }
        if (c instanceof JInternalFrame) {
            JInternalFrame jif = (JInternalFrame)c;
            text = jif.getTitle();
        }
        if (c instanceof JDialog) {
            JDialog jd = (JDialog)c;
            text = jd.getTitle();
        }
        if (c instanceof JPanel) {
            JPanel jp = (JPanel)c;
            text = getTitleOfBorder(jp.getBorder());           
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
    public Action compSysToAction(IComponentIdentifier id,
            String actionStr) {
        Action a = null;
        List compsList = new LinkedList();
        org.eclipse.jubula.tools.internal.xml.businessmodell.Component comp;
        if (id.getSupportedClassName().equals(MappingConstants
                .SWING_APPLICATION_CLASSNAME) 
                || id.getSupportedClassName().equals(MappingConstants
                        .SWING_APPLICATION_COMPONENT_IDENTIFIER)) {
            compsList = AUTServerConfiguration.getInstance()
                .findComponents(MappingConstants
                        .SWING_APPLICATION_CLASSNAME);
        } else if (id.getSupportedClassName().equals(MappingConstants
                .SWING_MENU_CLASSNAME)) {
            compsList = AUTServerConfiguration.getInstance()
                .findComponents(
                        MappingConstants.SWING_MENU_DEFAULT_MAPPING_CLASSNAME);
        } else {
            compsList = AUTServerConfiguration.getInstance()
                .findComponents(id.getSupportedClassName());
        }
        Iterator compsIt = compsList.iterator();
        while (compsIt.hasNext()) {
            comp = (org.eclipse.jubula.tools.internal
                    .xml.businessmodell.Component)compsIt.next();
            java.util.List actionList = comp.getActions();
            Iterator it = actionList.iterator();
            while (it.hasNext()) {
                Action action = (Action)it.next();
                if (action.getName().equals(actionStr)) {
                    a = action;
                }
            }            
        }
        return a;
    }
    
    /**
     * creates appendix for logical name for CAP like _btn, _cbx etc
     * @param c Component
     * @param id IComponentIdentifier
     * @return append for logical Name
     */
    public String getAbbreviations(Component c, IComponentIdentifier id) {
        String append = null;
        String suppClassName = id.getSupportedClassName();

        if (c instanceof AbstractButton
                || suppClassName.equals(AbstractButton.class.getName())) {
            append = "_btn"; //$NON-NLS-1$
        } else if (c instanceof JLabel
                || suppClassName.equals(JLabel.class.getName())) {
            append = "_lbl"; //$NON-NLS-1$
        } else if (c instanceof JTabbedPane
                || suppClassName.equals(JTabbedPane.class.getName())) {
            append = "_tpn"; //$NON-NLS-1$
        } else if (c instanceof JTree
                || suppClassName.equals(JTree.class.getName())) {
            append = "_tre"; //$NON-NLS-1$
        } else if (c instanceof JList
                || suppClassName.equals(JList.class.getName())) {
            append = "_lst"; //$NON-NLS-1$
        } else if (c instanceof JComboBox
                || suppClassName.equals(JComboBox.class.getName())) {
            append = "_cbx"; //$NON-NLS-1$
        } else if (c instanceof JTextComponent
                || suppClassName.equals(JTextComponent.class.getName())) {
            append = "_txf"; //$NON-NLS-1$
        } else if (c instanceof JTable
                || suppClassName.equals(JTable.class.getName())) {
            append = "_tbl"; //$NON-NLS-1$
        } else {
            append = "_xyz"; //$NON-NLS-1$
        }
        return append;
    }
    
    /**
     * creates logical name for CAP
     * @param c Component
     * @param id IComponentIdentifier
     * @return logical Name
     */
    protected String generateLogicalName(Component c, IComponentIdentifier id) {
        Component comp = c;
        String logName = null;
        String logicalName = null;
        if (comp.getName() == null) {
            if (!(c instanceof JTabbedPane)) {
                logName = getTextOfComponent(c);
            }            
            if (logName == null || logName.equals(StringConstants.EMPTY)) {
                //logName = minimizeCapName(c.getClass().getName());
                logName = minimizeCapName(id.getComponentName());
            }            
        } else {
            logName = c.getName();
        }
        logicalName = logName + getAbbreviations(c, id); 
        
        Component parent = c.getParent();
        while (parent != null) {
            if (parent.getName() == null || parent instanceof JFrame
                    || parent instanceof JDialog
                    || parent instanceof JInternalFrame
                    || parent instanceof JPanel) {
                logName = getTextOfComponent(parent);
                if (logName == null) {
                    logName = minimizeCapName(parent.getClass().getName());
                }            
            } else {
                logName = parent.getName();
            }
            if (parent instanceof JFrame || parent instanceof JDialog 
                    || parent instanceof JTabbedPane
                    || parent instanceof JInternalFrame
                    || (parent instanceof JPanel
                            && getTextOfComponent(parent) != null)) {
                logicalName = logName + "_" + logicalName;  //$NON-NLS-1$
            }
            
            parent = parent.getParent();
        }
        
        return NameValidator.convertToValidLogicalName(logicalName);
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
     * gets the textpath of a Menuitem, e.g. Item in Menubar or PopupMenu
     * @param c Component
     * @return textpath
     */
    public String getPath(Component c) {
        String menupath = StringConstants.EMPTY;
        if (c instanceof JMenuItem) {
            JMenuItem src = (JMenuItem)c;
            Component srcParent = src.getParent();
            String itemText = StringParsing.maskAndSingleQuoteText(
                    src.getText(), StringParsing.MENUTREEMASK);
            if (srcParent != null && srcParent
                    instanceof JPopupMenu) {
                getPath(srcParent);
            }
            
            m_menupath = m_menupath.concat(itemText);
            menupath = m_menupath;
            m_menupath = StringConstants.EMPTY;
        }
        
        if (c instanceof JPopupMenu) {
            JPopupMenu pop = (JPopupMenu)c;
            if (pop.getInvoker() instanceof JMenu) {                
                JMenu jm = (JMenu)pop.getInvoker();
                Component parent = jm.getParent();
                String parentText = StringParsing.maskAndSingleQuoteText(
                        jm.getText(), StringParsing.MENUTREEMASK);
                if (parent != null && parent
                        instanceof JPopupMenu) {
                    getPath(parent);
                    m_menupath = m_menupath.concat(parentText + "/"); //$NON-NLS-1$
                } else {
                    m_menupath = parentText + "/"; //$NON-NLS-1$
                }
            }
        }
        return menupath;
    }
    
    /**
     * converts Treepath to Textpath
     * @param tre Jtree
     * @param tp TreePath
     * @return textpath of treepath
     */
    protected String treepathToTextpath(JTree tre, TreePath tp) {
        String textpath = StringConstants.EMPTY;
        for (int i = 0; i < tp.getPathCount();
            i++) {                    
            Object current = tp.getPathComponent(i);
            String node = StringParsing.maskAndSingleQuoteText(
                    getRenderedTreeNodeText(
                            tre, current), StringParsing.MENUTREEMASK);
            textpath = textpath.concat(node);
            if (i < (tp.getPathCount() - 1)) {
                textpath = textpath.concat("/"); //$NON-NLS-1$
            }
        }
        return textpath;        
    }
    
    /**
     * true if Component is supported, false otherwise
     * @param c Component
     * @return true if Component is supported, false otherwise
     */
    protected boolean isSupportedComponent(Component c) {
        boolean supported = false;
        
        if (c instanceof AbstractButton || c instanceof JLabel
                || c instanceof JTabbedPane || c instanceof JTree
                || c instanceof JList || c instanceof JComboBox
                || c instanceof JTextArea || c instanceof JTextField
                || c instanceof JTextPane || c instanceof JEditorPane
                || c instanceof JTable || c instanceof JFrame
                || c instanceof JDialog) {
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
        List hierarchy = a1.getHierarchyNames();
        List iterHierarchy = a2.getHierarchyNames();
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
     * get the rendered text of a JList
     * @param list JList
     * @return The array of selected values as the renderer shows them
     */
    public String[] getRenderedListValues(final JList list) {
        final int[] indices = list.getSelectedIndices();
        Object[] values = list.getSelectedValues();
        String[] selected = new String[values.length];
        ListCellRenderer renderer = list.getCellRenderer();
        for (int i = 0; i < values.length; i++) {
            Object value = values[i];
            Component c = renderer.getListCellRendererComponent(
                list, value, indices[i], true, false);
            selected[i] = getRenderedText(c);
        }
        return selected;
    }
    
    /**
     * get the rendered text of a JCombo
     * @param cbx JCombo
     * @return the String from the Cell Renderer
     */
    public String getRenderedComboText(final JComboBox cbx) {
        final int selIndex = cbx.getSelectedIndex();
        if (selIndex == -1) {
            return null; // no selection
        }
        final JList jlist = new JList(cbx.getModel());
        Component disp = cbx.getRenderer().getListCellRendererComponent(
                jlist, jlist.getModel().getElementAt(selIndex),
                selIndex, true, cbx.hasFocus());
        return getRenderedText(disp);
    }
    
    /**
     * get the rendered text of all JCombo-Items
     * @param cbx JCombo
     * @return the StringArray of all JCombo-Items
     */
    public String[] getRenderedComboItems(final JComboBox cbx) {
        String[] comboItemsText = new String[cbx.getItemCount()];
        for (int index = 0; index < cbx.getItemCount(); index++) {
            final JList jlist = new JList(cbx.getModel());
            Component disp = cbx.getRenderer().getListCellRendererComponent(
                    jlist, jlist.getModel().getElementAt(index),
                    index, true, cbx.hasFocus());
            comboItemsText[index] = getRenderedText(disp);
        }
        return comboItemsText;
    }
    
    /**
     * get the rendered text of a JTable
     * @param tbl JTable
     * @param row the zero based index of the row
     * @param col the zero based index of the column
     * @return the text of the cell of the given coordinates
     */
    public String getRenderedTableCellText(final JTable tbl, final int row,
            final int col) {
        Object value = tbl.getValueAt(row, col);
        boolean selected = tbl.isCellSelected(row,
                col);
        if (log.isDebugEnabled()) {
            log.debug("Getting cell text:"); //$NON-NLS-1$
            log.debug("Row, col: " + row + ", " + col); //$NON-NLS-1$ //$NON-NLS-2$
            log.debug("Value: " + value); //$NON-NLS-1$
        }
        TableCellRenderer renderer = tbl.getCellRenderer(
                row, col);
        Component c = renderer.getTableCellRendererComponent(
                tbl, value, selected, true, row,
                col);
        return getRenderedText(c);
    }
    
    /**
     * get the rendered text of a JTree
     * @param tre JTree
     * @param value Object
     * @return the rendered text of the node
     */
    public String getRenderedTreeNodeText(final JTree tre, Object value) {
        TreePath path = tre.getSelectionPath();
        boolean selected = true;
        //boolean selected = !(tre.isSelectionEmpty());
        boolean expanded = tre.isExpanded(path);
        boolean hasFocus = tre.hasFocus();
        boolean leaf = tre.getModel().isLeaf(value);
        int row = tre.getRowForPath(path);
        TreeCellRenderer renderer = tre.getCellRenderer();
        Component c = renderer.getTreeCellRendererComponent(
                tre, value, selected, expanded, leaf, row, hasFocus);
        return getRenderedText(c);
    }
    
    /**
     * @param renderer
     *            The component which is used as the renderer
     * @return The string that the renderer displays.
     * @throws StepExecutionException
     *             If the renderer component is not of type <code>JLabel</code>,
     *             <code>JToggleButton</code>, <code>AbstractButton</code>
     *             or <code>JTextComponent</code>
     */
    private String getRenderedText(Component renderer)
        throws StepExecutionException {
        return TesterUtil.getRenderedText(renderer);
    }
        
    /**
     * @param border Border
     * @return the title of the border
     */
    private String getTitleOfBorder(Border border) {
        String title = null;
        if (border instanceof TitledBorder) {
            TitledBorder titBorder = (TitledBorder)border;
            title = titBorder.getTitle();
        } else if (border instanceof CompoundBorder) {
            CompoundBorder compoundBorder = (CompoundBorder)border;
            Border insideBorder = compoundBorder.getInsideBorder();
            Border outsideBorder = compoundBorder.getOutsideBorder();
            if (insideBorder instanceof TitledBorder) {
                TitledBorder titBorderInside = (TitledBorder)insideBorder;
                title = titBorderInside.getTitle();
            }
            if (outsideBorder instanceof TitledBorder) {
                TitledBorder titBorderOutside = (TitledBorder)outsideBorder;
                title = titBorderOutside.getTitle();
            }
        }
        return title;
    }
} 
