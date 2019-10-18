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

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.communication.internal.message.ServerShowDialogMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.commands.ShowDialogResultCommand;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.StringParsing;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;



/**
 * The SWTEventListener for mode OBSERVATION (CHECK_MODE). <br>
 * 
 * The component is marked by calling the method
 * highLight() respectively of the corresponding implementation
 * class. <br>
 * 
 * The key events are tapped for selecting the <code>m_currentComponent</code>
 * to be used for the observation.
 * <br>
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class CheckListener extends AbstractAutSwtEventListener {
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        CheckListener.class);
    
    /** active listener */
    private boolean m_active = true;
    
    /** The RecordHelper */
    private RecordHelperSWT m_recordHelperSWT = new RecordHelperSWT();
    
    
    /**
     * {@inheritDoc}
     */
    public void handleEvent(final Event event) {
        final Display display = ((SwtAUTServer)
                AUTServer.getInstance()).getAutDisplay();
        if (display != null) {
            display.syncExec(new Runnable() {
                public void run() {
                    if (event.equals(getLastEvent())) {
                        return;
                    }
                    if (event.type == SWT.MouseMove) {
                        handleMouseEvent();
                    }
                    if (event.type == SWT.KeyDown) {
                        handleKeyEvent(event); 
                    }
                    setLastEvent(event);
                }
            });
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected void handleKeyEvent(Event event) {        
        synchronized (getComponentLock()) {
            if (event.type == SWT.KeyDown) {
                IComponentIdentifier id = null;
                //int keycode = event.keyCode;
                Widget source = event.widget;

                //deactivates "CheckMode" (Highlighting)
                final int accepted = getAcceptor().accept(event);
                if (accepted == KeyAcceptor.CHECKMODE_KEY_COMB) {
                    changeCheckModeState(ChangeAUTModeMessage.RECORD_MODE);
                    cleanUp();
                }
                
                //open CheckTypeDialog(ChooseActionDialog)
                if (accepted == KeyAcceptor.CHECKCOMP_KEY_COMB
                        && getCurrentComponent() != null
                        && !(getCurrentComponent() instanceof Shell)) {
                    try {
                        id = ComponentHandler
                            .getIdentifier(getCurrentComponent());
                        Map<String, String> checkValues = getCheckValues(
                            getCurrentComponent());
                        
                        String logName = m_recordHelperSWT.generateLogicalName(
                                getCurrentComponent(), id);
                        
                        openCheckDialog(id, checkValues, logName);
                    } catch (NoIdentifierForComponentException nifce) {
                     // no identifier for the component, log this as an error
                        log.error("no identifier for '" + source); //$NON-NLS-1$
                    }
                }
            }            
        }
    }
    
    /**
     * method for handling Mouse-Events (highlighting of components)
     */
    protected void handleMouseEvent() {     
        Widget widget = SwtUtils.getWidgetAtCursorLocation();
        setCurrentComponent(widget);
        setCurrentWidget();
        highlightComponent();
    }
    
    /**
     * opens the Check-Dialog to select the Check-Type
     * @param id IComponentIdentifier
     * @param checkValues List
     * @param logName String of Logical Name
     */
    protected void openCheckDialog(IComponentIdentifier id,
        Map<String, String> checkValues, String logName) {
        AUTServer autserver = AUTServer.getInstance();
        try {            
            // send a message with the identifier of the selected component
            autserver.setMode(ChangeAUTModeMessage.TESTING);
            // set always to true, before showing observing
            // dialog. Changing AUTServer mode sets it to false
            m_active = true;
            org.eclipse.jubula.tools.internal.xml.businessmodell.Component comp;

            comp = AUTServerConfiguration.getInstance()
                .findComponent(id.getSupportedClassName());

            sendMessage(id, comp, checkValues, logName);
            autserver.setObservingDialogOpen(true);
            // m_active could be set to false, by ending observation
            // mode in client
            if (m_active) {
                autserver.setMode(ChangeAUTModeMessage.CHECK_MODE);
            }
        } catch (CommunicationException nifce) {
            autserver.setObservingDialogOpen(false);
            log.error("communication exception: '" + nifce); //$NON-NLS-1$
        }
    }
    
    /**
     * {@inheritDoc}
     */
    protected Color getBorderColor() {
        return new Color(null, Constants.OBSERVING_R,
                    Constants.OBSERVING_G, 
                    Constants.OBSERVING_B);
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
     * @param id the IComponentIdentifier
     * @param comp org.eclipse.jubula.tools.xml.businessmodell.Component
     * @param checkValues List
     * @param logName String of Logical Name
     * @throws CommunicationException in case of communication error
     */
    private void sendMessage(IComponentIdentifier id, 
        org.eclipse.jubula.tools.internal.xml.businessmodell.Component comp,
            Map<String, String> checkValues, String logName)
        throws CommunicationException {
        ServerShowDialogMessage message =
            new ServerShowDialogMessage(comp, id, checkValues);
        message.setAction(ServerShowDialogMessage.ACT_SHOW_CHECK_DIALOG);
     // map from SWT.Point to AWT.Point
        final org.eclipse.swt.graphics.Point compLoc = 
            SwtUtils.getWidgetLocation(getCurrentComponent());
        message.setPoint(new Point(compLoc.x, compLoc.y));
        message.setLogicalName(logName);
        // -------------------------------
        ShowDialogResultCommand command = new ShowDialogResultCommand();
        AUTServer.getInstance().getServerCommunicator().request(message, 
            command, TimeoutConstants.SERVER_CLIENT_TIMEOUT_CAP_RECORDED);
    }
        
    /**
     * @param widget Widget
     * @return the map of values to check
     */
    protected Map<String, String> getCheckValues(Widget widget) {
        Map<String, String> valueMap = new HashMap<String, String>();
        if (widget instanceof Text) {
            valueMap = getTextValues(widget);
        }
        if (widget instanceof Button) {
            valueMap = getButtonValues(widget);
        }
        if (widget instanceof Label) {
            valueMap = getLabelValues(widget);
        }
        if (widget instanceof CLabel) {
            valueMap = getCLabelValues(widget);
        }
        if (widget instanceof Tree) {
            valueMap = getTreeValues(widget);
        }
        if (widget instanceof List) {
            valueMap = getListValues(widget);
        }
        if (widget instanceof Combo) {
            valueMap = getComboValues(widget);
        }
        if (widget instanceof CCombo) {
            valueMap = getCComboValues(widget);
        }
        if (widget instanceof TabFolder) {
            valueMap = getTabFolderValues(widget);
        }
        if (widget instanceof CTabFolder) {
            valueMap = getCTabFolderValues(widget);
        }
        if (widget instanceof Table) {
            valueMap = getTableValues(widget);
        }
        if (widget instanceof ToolItem) {
            valueMap = getToolItemValues(widget);
        }
        // checking menuitems is not realized at the moment
        /*if (widget instanceof MenuItem) {
            valueMap = getMenuItemValues(widget);
        }*/
        return valueMap;
    }
    
    /**
     * @param widget Widget
     * @return text values to check
     */
    protected Map<String, String> getTextValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();    
        Text t = (Text)widget;
        String text = StringParsing.singleQuoteText(t.getText());
        map.put("CompSystem.Text", text); //$NON-NLS-1$
        String hasFocus = StringParsing.boolToString(t.isFocusControl());
        String exists = StringParsing.boolToString(t.isVisible());
        String enabled = StringParsing.boolToString(t.isEnabled());
        String editable = StringParsing.boolToString(t.getEditable());        
        addFocExistEnbl(map, hasFocus, exists, enabled);
        map.put("CompSystem.IsEditable", editable); //$NON-NLS-1$
        return map;
    }
    
    /**
     * @param widget Widget
     * @return button values to check
     */
    protected Map<String, String> getButtonValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        Button ab = (Button)widget;
        String text = StringParsing.singleQuoteText(ab.getText());
        text = SwtUtils.removeMnemonics(text);
        String hasFocus = StringParsing.boolToString(ab.isFocusControl());
        String exists = StringParsing.boolToString(ab.isVisible());
        String enabled = StringParsing.boolToString(ab.isEnabled());
        String selected = StringParsing.boolToString(ab.getSelection());
        map.put("CompSystem.Text", text); //$NON-NLS-1$
        addFocExistEnbl(map, hasFocus, exists, enabled);
        map.put("CompSystem.IsSelected", selected); //$NON-NLS-1$
        return map;
    }
    
    /**
     * @param widget Widget
     * @return Label values to check
     */
    protected Map<String, String> getLabelValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        Label lbl = (Label)widget;
        String text = StringParsing.singleQuoteText(lbl.getText());
        text = SwtUtils.removeMnemonics(text);
        String hasFocus = StringParsing.boolToString(lbl.isFocusControl());
        String exists = StringParsing.boolToString(lbl.isVisible());
        String enabled = StringParsing.boolToString(lbl.isEnabled());
        map.put("CompSystem.Text", text); //$NON-NLS-1$
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param widget Widget
     * @return CLabel values to check
     */
    protected Map<String, String> getCLabelValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        CLabel clbl = (CLabel)widget;
        String text = StringParsing.singleQuoteText(clbl.getText());
        text = SwtUtils.removeMnemonics(text);
        String hasFocus = StringParsing.boolToString(clbl.isFocusControl());
        String exists = StringParsing.boolToString(clbl.isVisible());
        String enabled = StringParsing.boolToString(clbl.isEnabled());
        map.put("CompSystem.Text", text); //$NON-NLS-1$
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
        
    /**
     * @param widget Widget
     * @return tree values to check
     */
    protected Map<String, String> getTreeValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        Tree tre = (Tree)widget;
        if (tre.getSelectionCount() != 0) {
            TreeItem[] entries = tre.getSelection();
            String treText = StringConstants.EMPTY;
            for (int i = 0; i < entries.length; i++) {
                String node = StringParsing.maskAndSingleQuoteText(
                        entries[i].getText(), StringParsing.MENUTREEMASK);
                treText = treText.concat(node);
                if (i < (entries.length - 1)) {
                    treText = treText.concat(","); //$NON-NLS-1$
                }
            }
            String textpath = m_recordHelperSWT.getTreePath(entries[0]);
            m_recordHelperSWT.setTreePath(StringConstants.EMPTY);
            String existNode = "true"; //$NON-NLS-1$
            String column = new Integer(1).toString();
            map.put("CompSystem.Text", treText); //$NON-NLS-1$
            map.put("CompSystem.TextPath", textpath); //$NON-NLS-1$
            map.put("CompSystem.Exists", existNode); //$NON-NLS-1$
            map.put("CompSystem.Column", column); //$NON-NLS-1$
        }
        String preascend = "0"; //$NON-NLS-1$
        String hasFocus = StringParsing.boolToString(tre.isFocusControl());
        String exists = StringParsing.boolToString(tre.isVisible());
        String enabled = StringParsing.boolToString(
                tre.isEnabled());
        map.put("CompSystem.PreAscend", preascend); //$NON-NLS-1$            
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param widget Widget
     * @return List values to check
     */
    protected Map<String, String> getListValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        List lst = (List)widget;         
        if (lst.getSelectionCount() != 0) {
            String[] entries = lst.getSelection();
            String lstText = StringConstants.EMPTY;
            for (int i = 0; i < entries.length; i++) {
                String item = StringParsing.maskAndSingleQuoteText(
                        entries[i].toString(), StringParsing.LISTCOMBOMASK);
                lstText = lstText.concat(item);
                if (i < (entries.length - 1)) {
                    lstText = lstText.concat(","); //$NON-NLS-1$
                }
            }

            String entryExists = "true"; //$NON-NLS-1$
            if (lst.getItemCount() == 0) {
                entryExists = "false"; //$NON-NLS-1$
            }
            String isSel = "true"; //$NON-NLS-1$
            map.put("CompSystem.IsSelected", isSel); //$NON-NLS-1$
            map.put("CompSystem.Text", lstText); //$NON-NLS-1$
            map.put("CompSystem.Exists", entryExists); //$NON-NLS-1$
        }
        String hasFocus = StringParsing.boolToString(lst.isFocusControl());
        String exists = StringParsing.boolToString(lst.isVisible());
        String enabled = StringParsing.boolToString(lst.isEnabled());
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param widget Widget
     * @return Combo values to check
     */
    protected Map<String, String> getComboValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        Combo cbx = (Combo)widget;
        if (cbx.getSelectionIndex() != -1) {
            String cbxText = StringParsing.singleQuoteText(cbx
                    .getItem(cbx.getSelectionIndex()).toString());
            map.put("CompSystem.Text", cbxText); //$NON-NLS-1$
        }            
        String hasFocus = StringParsing.boolToString(cbx.isFocusControl());
        String exists = StringParsing.boolToString(cbx.isVisible());
        String enabled = StringParsing.boolToString(cbx.isEnabled());
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    /**
     * @param widget Widget
     * @return CCombo values to check
     */
    protected Map<String, String> getCComboValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        CCombo ccbx = (CCombo)widget;
        if (ccbx.getSelectionIndex() != -1) {
            String ccbxText = StringParsing.singleQuoteText(ccbx
                    .getItem(ccbx.getSelectionIndex()).toString());
            map.put("CompSystem.Text", ccbxText); //$NON-NLS-1$
        }            
        String hasFocus = StringParsing.boolToString(ccbx.isFocusControl());
        String exists = StringParsing.boolToString(ccbx.isVisible());
        String enabled = StringParsing.boolToString(ccbx.isEnabled());
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param widget Widget
     * @return TabFolder values to check
     */
    protected Map<String, String> getTabFolderValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        TabFolder tp = (TabFolder)widget;
        String title = StringParsing.singleQuoteText(tp
                .getItem(tp.getSelectionIndex()).getText());
        title = SwtUtils.removeMnemonics(title);
        String isSel = "true"; //$NON-NLS-1$
        map.put("CompSystem.IsSelected", isSel); //$NON-NLS-1$
        map.put("CompSystem.Title", title); //$NON-NLS-1$
        map.put("CompSystem.Tab", title); //$NON-NLS-1$
        String hasFocus = StringParsing.boolToString(tp.isFocusControl());
        String exists = StringParsing.boolToString(tp.isVisible());
        String enabled = StringParsing.boolToString(tp.isEnabled());
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param widget Widget
     * @return CTabFolder values to check
     */
    protected Map<String, String> getCTabFolderValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        CTabFolder ctp = (CTabFolder)widget;
        if (ctp.getSelection() != null) {
            String title = StringParsing.singleQuoteText(
                    ctp.getSelection().getText());
            title = SwtUtils.removeMnemonics(title);
            String isSel = "true"; //$NON-NLS-1$
            map.put("CompSystem.IsSelected", isSel); //$NON-NLS-1$
            map.put("CompSystem.Title", title); //$NON-NLS-1$
            map.put("CompSystem.Tab", title); //$NON-NLS-1$
        }            
        String hasFocus = StringParsing.boolToString(ctp.isFocusControl());
        String exists = StringParsing.boolToString(ctp.isVisible());
        String enabled = StringParsing.boolToString(ctp.isEnabled());
        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param widget Widget
     * @return CTabFolder values to check
     */
    protected Map<String, String> getTableValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        Table tbl = (Table)widget;
        if (tbl.getSelectionCount() != 0) { //tbl.SelectedColumnCount()
            Cell cell = TableSelectionTracker
                .getInstance().getSelectedCell(tbl);
            String tblText = StringParsing.singleQuoteText(
                    tbl.getItem(cell.getRow()).getText(cell.getCol()));
            String rowStr = new Integer(cell.getRow() + 1).toString();
            String columnStr = new Integer(cell.getCol() + 1).toString();
            map.put("CompSystem.Text", tblText); //$NON-NLS-1$
            map.put("CompSystem.TextOperator", MatchUtil.EQUALS); //$NON-NLS-1$
            map.put("CompSystem.CellValue", tblText); //$NON-NLS-1$
            map.put("CompSystem.ValueOperator", MatchUtil.EQUALS); //$NON-NLS-1$
            map.put("CompSystem.Row", rowStr); //$NON-NLS-1$
            map.put("CompSystem.RowOperator", MatchUtil.EQUALS); //$NON-NLS-1$
            map.put("CompSystem.Column", columnStr); //$NON-NLS-1$
            map.put("CompSystem.ColumnOperator", MatchUtil.EQUALS); //$NON-NLS-1$
        }
        String hasFocus = StringParsing.boolToString(tbl.isFocusControl());
        String exists = StringParsing.boolToString(tbl.isVisible());
        String enabled = StringParsing.boolToString(tbl.isEnabled());

        addFocExistEnbl(map, hasFocus, exists, enabled);
        return map;
    }
    
    /**
     * @param widget Widget
     * @return ToolItem values to check
     */
    protected Map<String, String> getToolItemValues(Widget widget) {
        Map<String, String> map = new HashMap<String, String>();
        ToolItem ti = (ToolItem)widget;      
        String toolText = StringParsing.singleQuoteText(ti.getText());
        toolText = SwtUtils.removeMnemonics(toolText);
        String selected = StringParsing.boolToString(ti.getSelection());
        map.put("CompSystem.Text", toolText); //$NON-NLS-1$
        map.put("CompSystem.IsSelected", selected); //$NON-NLS-1$
        //String hasFocus = boolToString(ti.isFocusControl());
        //String exists = boolToString(ti.isVisible());
        String enabled = StringParsing.boolToString(ti.isEnabled());
        map.put("CompSystem.IsEnabled", enabled); //$NON-NLS-1$
        //addFocExistEnbl(valueMap, hasFocus, exists, enabled);
        return map;
    }
}