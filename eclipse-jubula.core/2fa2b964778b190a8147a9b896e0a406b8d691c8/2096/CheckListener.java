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

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.PaintEvent;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ComboBoxEditor;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.text.JTextComponent;
import javax.swing.tree.TreePath;

import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.ServerShowDialogMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.commands.ShowDialogResultCommand;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.exception.UnsupportedComponentException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.StringParsing;



/**
 * The AWTEventListener for check-mode during OBSERVATION. <br>
 * 
 * The component is marked by calling the method
 * highLight() respectively of the corresponding implementation
 * class. <br>
 * 
 * The key events are tapped for selecting the <code>m_currentComponent</code>
 * to be used for observation. 
 * <br>
 * 
 * @author BREDEX GmbH
 * @created 09.07.2008
 */
public class CheckListener extends AbstractAutSwingEventListener {    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        CheckListener.class);
    
    /** active listener */
    private boolean m_active = true;
    
    /** The RecordHelper */
    private RecordHelper m_recordHelper = new RecordHelper();
            
    /**
     * method for event-handling
     * @param event AWTEvent
     */
    protected void handleEvent(AWTEvent event) {
        if (event.equals(getLastEvent())) {
            return;
        }
        if (getEventSource(event) == null) {
            return;
        }
        setLastEvent(event);
        
        Component source = getEventSource(event);
        Component parent = source.getParent();
        Object implClass = null;
        
        if ((event instanceof KeyEvent) && (source instanceof JComponent)) {
            KeyEvent keyEvent = (KeyEvent)event;
            handleKeyEvent(keyEvent);            
        } else {
         // First check parent
            if (parent != null) {
                try {
                    implClass = AUTServerConfiguration.getInstance()
                        .getImplementationClass(parent.getClass());
                    source = parent;
                } catch (UnsupportedComponentException uce) { // NOPMD by zeb on 10.04.07 12:32
                    /* 
                     * This means that the parent of the source of the
                     * event is not a supported component. The original
                     * source is used, rather than the parent component.
                     */
                }
            }
            if (implClass == null) {
                try {
                    implClass = AUTServerConfiguration.getInstance()
                        .getImplementationClass(source.getClass());
                } catch (UnsupportedComponentException uce2) {
//                    log.warn("unsupported component: '" //$NON-NLS-1$
//                        + source.getClass().getName() + "'", uce2); //$NON-NLS-1$
                    return;
                }
            }
            if ((event instanceof MouseEvent) 
                    && (source instanceof JComponent)) {
                switchEvent(event, source, implClass);
            }
        }                       
        
    }
    
    /**
     * method for handling Key-Events
     * @param keyEvent KeyEvent
     */
    private void handleKeyEvent(KeyEvent keyEvent) {
        synchronized (getComponentLock()) {
            if (keyEvent.getID() == KeyEvent.KEY_PRESSED) {
                IComponentIdentifier id = null;          
                
                // activates/deactivates "CheckMode" (Highlighting)
                final int accepted = getAcceptor().accept(keyEvent);
                if (accepted == KeyAcceptor.CHECKMODE_KEY_COMB) {
                    changeCheckModeState(ChangeAUTModeMessage.RECORD_MODE);
                    cleanUp();
                }
                
                //open CheckTypeDialog(ChooseActionDialog)
                if (accepted == KeyAcceptor.CHECKCOMP_KEY_COMB) {
                    try {
                        if (getCurrentComponent() instanceof JMenuItem) {
                            id = m_recordHelper.getMenuCompID();
                        } else {
                            id = ComponentHandler
                            .getIdentifier(getCurrentComponent());
                        }
                        
                        Map<String, String> checkValues = getCheckValues(
                            getCurrentComponent());
                        String logName = m_recordHelper.generateLogicalName(
                                getCurrentComponent(), id);
                        openCheckDialog(id, checkValues, logName);
                    } catch (NoIdentifierForComponentException nifce) {
                     // no identifier for the component, log this as an error
                        log.error("no identifier for '" + getCurrentComponent()); //$NON-NLS-1$
                    }
                }
            }
        }
    }
    
    /**
     * opens the Check-Dialog to select the Check-Type
     * @param id IComponentIdentifier
     * @param checkValues List
     * @param logName logical Name
     */
    protected void openCheckDialog(IComponentIdentifier id,
            Map<String, String> checkValues, String logName) {
        AUTServer autsever = AUTServer.getInstance();
        try {            
            // send a message with the identifier of the selected component
            autsever.setMode(ChangeAUTModeMessage.TESTING);
            // set always to true, before showing observing
            // dialog. Changing AUTServer mode sets it to false
            m_active = true;
            org.eclipse.jubula.tools.internal.xml.businessmodell.Component comp;

            comp = AUTServerConfiguration.getInstance()
                .findComponent(id.getSupportedClassName());

            sendMessage(id, comp, checkValues, logName);
            autsever.setObservingDialogOpen(true);
            // m_active could be set to false, by ending observation
            // mode in client
            if (m_active) {
                autsever.setMode(ChangeAUTModeMessage.CHECK_MODE);
            }
        } catch (CommunicationException nifce) {
            autsever.setObservingDialogOpen(false);
            log.error("communication exception: '" + nifce); //$NON-NLS-1$
        }
    }
    

    
    /**
     * adds focus-, existence- and enablement-information to the valueMap
     * @param valueMap Map
     * @param hasFocus String
     * @param exists String
     * @param enabled String
     * @return the map with added focus-, existence- and enablement-information
     */
    protected Map<String, String> addFocExistEnbl(Map<String, String> valueMap, 
        String hasFocus, String exists, String enabled) {
        valueMap.put("CompSystem.HasFocus", hasFocus); //$NON-NLS-1$
        valueMap.put("CompSystem.IsExisting", exists); //$NON-NLS-1$
        valueMap.put("CompSystem.IsEnabled", enabled); //$NON-NLS-1$
        return valueMap;
    }        
    
    /**
     * @param component Component
     * @return the map of values to check
     */
    protected Map<String, String> getCheckValues(Component component) {
        Map<String, String> valueMap = new HashMap<String, String>();
        if (component instanceof JTextComponent) {
            valueMap = getTextCompValues(component);
        }
        if (component instanceof AbstractButton) {
            valueMap = getButtonValues(component);
        }
        if (component instanceof JLabel) {
            valueMap = getLabelValues(component);
        }
        if (component instanceof JTree) {
            valueMap = getTreeValues(component);
        }
        if (component instanceof JList) {
            valueMap = getListValues(component);
        }
        if (component instanceof JComboBox) {
            valueMap = getComboValues(component);
        }
        if (component instanceof JTabbedPane) {
            valueMap = getTpnValues(component);
        }
        if (component instanceof JTable) {
            valueMap = getTableValues(component);
        }
        if (component instanceof JMenuItem) {
            valueMap = getMenuItemValues(component);
        }
        return valueMap;
    }
    
    /**
     * @param c Component
     * @return text values to check
     */
    private Map<String, String> getTextCompValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JTextComponent t = (JTextComponent)c;
        
        String text = StringParsing.singleQuoteText(t.getText());
        map.put("CompSystem.Text", text); //$NON-NLS-1$
        
        String hasFocus = StringParsing.boolToString(t.hasFocus());
        String exists = StringParsing.boolToString(t.isShowing());
        String enabled = StringParsing.boolToString(t.isEnabled());
        String editable = StringParsing.boolToString(t.isEditable());        
        addFocExistEnbl(map, hasFocus, exists, enabled);
        map.put("CompSystem.IsEditable", editable); //$NON-NLS-1$
        return map;
    }
    
    /**
     * @param c Component
     * @return button values to check
     */
    private Map<String, String> getButtonValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        AbstractButton ab = (AbstractButton)c;
        String text = StringParsing.singleQuoteText(ab.getText());
        String hasFocus = StringParsing.boolToString(ab.hasFocus());
        String exists = StringParsing.boolToString(ab.isShowing());
        String enabled = StringParsing.boolToString(ab.isEnabled());
        String selected = StringParsing.boolToString(ab.isSelected());
        map.put("CompSystem.Text", text); //$NON-NLS-1$
        addFocExistEnbl(map, hasFocus, exists, enabled);
        map.put("CompSystem.IsSelected", selected); //$NON-NLS-1$
        return map;
    }
    
    /**
     * @param c Component
     * @return label values to check
     */
    private Map<String, String> getLabelValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JLabel lbl = (JLabel)c;
        String text = StringParsing.singleQuoteText(lbl.getText());
        String hasFocus = StringParsing.boolToString(lbl.hasFocus());
        String exists = StringParsing.boolToString(lbl.isShowing());
        String enabled = StringParsing.boolToString(lbl.isEnabled());
        map.put("CompSystem.Text", text); //$NON-NLS-1$
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param c Component
     * @return tree values to check
     */
    private Map<String, String> getTreeValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JTree tre = (JTree)c;
        if (tre.getSelectionCount() != 0) {
            TreePath[] selNodes = tre.getSelectionPaths();
            String treText = StringConstants.EMPTY;
            for (int i = 0; i < selNodes.length; i++) {
                Object value = selNodes[i].getLastPathComponent();
                String node = StringParsing.maskAndSingleQuoteText(
                        m_recordHelper.getRenderedTreeNodeText(
                                tre, value), StringParsing.MENUTREEMASK);
                treText = treText.concat(node);
                if (i < (selNodes.length - 1)) {
                    treText = treText.concat(","); //$NON-NLS-1$
                }
            }
            TreePath tp = tre.getSelectionPath();
            String textpath = m_recordHelper.treepathToTextpath(tre, tp);
            String existNode = "true"; //$NON-NLS-1$
            map.put("CompSystem.Text", treText); //$NON-NLS-1$
            map.put("CompSystem.TextPath", textpath); //$NON-NLS-1$
            map.put("CompSystem.Exists", existNode); //$NON-NLS-1$
        }
        String preascend = "0"; //$NON-NLS-1$
        String hasFocus = StringParsing.boolToString(tre.hasFocus());
        String exists = StringParsing.boolToString(tre.isShowing());
        String enabled = StringParsing.boolToString(tre.isEnabled());

        map.put("CompSystem.PreAscend", preascend); //$NON-NLS-1$            
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param c Component
     * @return list values to check
     */
    private Map<String, String> getListValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JList lst = (JList)c;            
        if (lst.getSelectedIndices().length != 0) {
            String[] entries = m_recordHelper.getRenderedListValues(lst);
            String lstText = StringConstants.EMPTY;
            for (int i = 0; i < entries.length; i++) {
                String item = StringParsing.maskAndSingleQuoteText(
                        entries[i], StringParsing.LISTCOMBOMASK);
                lstText = lstText.concat(item);
                if (i < (entries.length - 1)) {
                    lstText = lstText.concat(","); //$NON-NLS-1$
                }
            }
            String isSel = StringParsing.boolToString(
                    lst.isSelectedIndex(lst.getSelectedIndex()));            
            map.put("CompSystem.IsSelected", isSel); //$NON-NLS-1$
            map.put("CompSystem.Text", lstText); //$NON-NLS-1$
            map.put("CompSystem.Exists", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String hasFocus = StringParsing.boolToString(lst.hasFocus());
        String exists = StringParsing.boolToString(lst.isShowing());
        String enabled = StringParsing.boolToString(lst.isEnabled());
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param c Component
     * @return combo values to check
     */
    private Map<String, String> getComboValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JComboBox cbx = (JComboBox)c;
        String cbxText = StringConstants.EMPTY;
        boolean isCbxItem = true;
        if (cbx.isEditable()) {
            isCbxItem = false;
            ComboBoxEditor cbxEditor = cbx.getEditor();
            cbxText = cbxEditor.getItem().toString();
            String[] cbxItems = m_recordHelper.getRenderedComboItems(cbx);
            for (int i = 0; i < cbxItems.length; i++) {
                String item = cbxItems[i];
                if (item.equals(cbxText)) {
                    isCbxItem = true;
                }
            }
            String isComboItem = StringParsing.boolToString(isCbxItem);
            map.put("CompSystem.Text", cbxText); //$NON-NLS-1$
            map.put("CompSystem.Exists", isComboItem); //$NON-NLS-1$
        } else if (cbx.getSelectedItem() != null && isCbxItem) {
            cbxText = StringParsing.singleQuoteText(m_recordHelper
                    .getRenderedComboText(cbx));
            map.put("CompSystem.Text", cbxText); //$NON-NLS-1$
            map.put("CompSystem.Exists", "true"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        String hasFocus = StringParsing.boolToString(cbx.hasFocus());
        String exists = StringParsing.boolToString(cbx.isShowing());
        String enabled = StringParsing.boolToString(cbx.isEnabled());
        String editable = StringParsing.boolToString(cbx.isEditable());
        addFocExistEnbl(map, hasFocus, exists, enabled);            
        map.put("CompSystem.IsEditable", editable); //$NON-NLS-1$
        return map;
    }
    
    /**
     * @param c Component
     * @return tabbedpane values to check
     */
    private Map<String, String> getTpnValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JTabbedPane tp = (JTabbedPane)c;
        String hasFocus = StringParsing.boolToString(tp.hasFocus());
        String exists = StringParsing.boolToString(tp.isShowing());
        String enabled = StringParsing.boolToString(tp.isEnabled());
        String title = StringParsing.singleQuoteText(tp.getTitleAt(tp
                .getSelectedIndex()));
        String enabledTab = StringParsing.boolToString(tp.isEnabledAt(tp
                .indexOfTab(tp.getTitleAt(tp.getSelectedIndex()))));
        String isSel = "true"; //$NON-NLS-1$
        map.put("CompSystem.IsSelected", isSel); //$NON-NLS-1$
        map.put("CompSystem.Title", title); //$NON-NLS-1$
        map.put("CompSystem.Tab", title); //$NON-NLS-1$
        addFocExistEnbl(map, hasFocus, exists, enabled);
        map.put("CompSystem.VerifyEnabledTab", enabledTab); //$NON-NLS-1$
        return map;
    }
    
    /**
     * @param c Component
     * @return table values to check
     */
    private Map<String, String> getTableValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JTable tbl = (JTable)c;
        int row = tbl.getSelectedRow();
        int column = tbl.getSelectedColumn();
        if (tbl.getSelectedColumnCount() != 0) {
            String tblText = StringParsing.singleQuoteText(
                    m_recordHelper.getRenderedTableCellText(tbl, row, column));
            String rowStr = new Integer(row + 1).toString();
            String columnStr = new Integer(column + 1)
                .toString();
            map.put("CompSystem.Text", tblText); //$NON-NLS-1$
            map.put("CompSystem.TextOperator", MatchUtil.EQUALS); //$NON-NLS-1$
            map.put("CompSystem.CellValue", tblText); //$NON-NLS-1$
            map.put("CompSystem.ValueOperator", MatchUtil.EQUALS); //$NON-NLS-1$
            map.put("CompSystem.Row", rowStr); //$NON-NLS-1$
            map.put("CompSystem.RowOperator", MatchUtil.EQUALS); //$NON-NLS-1$
            map.put("CompSystem.Column", columnStr); //$NON-NLS-1$
            map.put("CompSystem.ColumnOperator", MatchUtil.EQUALS); //$NON-NLS-1$
        }
        String hasFocus = StringParsing.boolToString(tbl.hasFocus());
        String exists = StringParsing.boolToString(tbl.isShowing());
        String enabled = StringParsing.boolToString(tbl.isEnabled());
        String cellEditable = StringParsing.boolToString(
                tbl.isCellEditable(row, column));
        addFocExistEnbl(map, hasFocus, exists, enabled);
        map.put("CompSystem.IsEditable", cellEditable); //$NON-NLS-1$
        return map;
    }
    
    /**
     * @param c Component
     * @return text values to check
     */
    private Map<String, String> getMenuItemValues(Component c) {
        Map<String, String> map = new HashMap<String, String>();
        JMenuItem m = (JMenuItem)c;

        String menupath = m_recordHelper.getPath(m);
        map.put("CompSystem.MenuPath", menupath); //$NON-NLS-1$
        
        String exists = StringParsing.boolToString(m.isShowing());
        String enabled = StringParsing.boolToString(m.isEnabled());
        String selected = StringParsing.boolToString(m.isSelected());
        map.put("CompSystem.IsExisting", exists); //$NON-NLS-1$
        map.put("CompSystem.IsEnabled", enabled); //$NON-NLS-1$
        map.put("CompSystem.IsSelected", selected); //$NON-NLS-1$
        
        
        return map;
    }
        
    /**
     * @param id the IComponentIdentifier
     * @param comp org.eclipse.jubula.tools.xml.businessmodell.Component
     * @param checkValues List
     * @param logName logical Name
     * @throws CommunicationException in case of communication error
     */
    private void sendMessage(IComponentIdentifier id, 
        org.eclipse.jubula.tools.internal.xml.businessmodell.Component comp,
            Map<String, String> checkValues, String logName) 
        throws CommunicationException {
        ServerShowDialogMessage message =
            new ServerShowDialogMessage(comp, id, checkValues);
        message.setAction(ServerShowDialogMessage.ACT_SHOW_CHECK_DIALOG);
        message.setPoint(getCurrentComponent().getLocationOnScreen());
        message.setLogicalName(logName);
        ShowDialogResultCommand command = new ShowDialogResultCommand();
        AUTServer.getInstance().getServerCommunicator().request(message, 
            command, TimeoutConstants.SERVER_CLIENT_TIMEOUT_CAP_RECORDED);
    }
    
    /**
     * @param event     AWTEvent
     * @param source    Component
     * @param implClass IImplementationClass
     */
    protected void switchEvent(AWTEvent event, Component source, 
        final Object implClass) {
        final Color highlightColor = new Color (Constants.OBSERVING_R,
            Constants.OBSERVING_G, Constants.OBSERVING_B);
        switch (event.getID()) {
            case MouseEvent.MOUSE_RELEASED:
            case MouseEvent.MOUSE_PRESSED:
                highlightClicked(implClass, highlightColor);
                break;
            case MouseEvent.MOUSE_ENTERED:
            case MouseEvent.MOUSE_MOVED:
                highlight(source, implClass, highlightColor);
                break;
            default:
                if (log.isDebugEnabled()) {
                    log.debug("event occurred: " + event.paramString());  //$NON-NLS-1$
                }
        }
        final int eventId = event.getID();
        if ((eventId >= ComponentEvent.COMPONENT_FIRST 
                    && eventId <= ComponentEvent.COMPONENT_LAST)
                    || (eventId >= PaintEvent.PAINT_FIRST 
                    && eventId <= PaintEvent.PAINT_LAST)
                    || (eventId >= WindowEvent.WINDOW_FIRST 
                    && eventId <= WindowEvent.WINDOW_LAST)) {
                
            updateHighlighting(source, implClass, highlightColor);
        }
    }
        
    /**
     * {@inheritDoc}
     */
    public void update() {
        // do nothing
    }    
    /**
     * {@inheritDoc}
     */
    public boolean highlightComponent(IComponentIdentifier comp) {
        // do nothing
        return true;
    }
} 
