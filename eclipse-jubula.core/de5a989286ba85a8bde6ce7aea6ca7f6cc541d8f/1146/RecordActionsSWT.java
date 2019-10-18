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

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import org.eclipse.jubula.communication.internal.message.CAPRecordedMessage;
import org.eclipse.jubula.communication.internal.message.MessageCap;
import org.eclipse.jubula.communication.internal.message.MessageParam;
import org.eclipse.jubula.communication.internal.message.RecordActionMessage;
import org.eclipse.jubula.communication.internal.message.ShowObservInfoMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.implclasses.table.Cell;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.common.util.MatchUtil;
import org.eclipse.jubula.rc.swt.utils.SwtKeyCodeConverter;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.toolkit.enums.ValueSets.InteractionMode;
import org.eclipse.jubula.tools.internal.constants.StringConstants;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.utils.StringParsing;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Param;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;




/**
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public class RecordActionsSWT {    
    
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        RecordListener.class);
    
    /** map to store contents of TextComponents */
    private Map<Widget, String> m_map = new HashMap<Widget, String>();
    
    /** map to store logical Names */
    private Map<String, IComponentIdentifier> m_logNameMap = 
        new HashMap<String, IComponentIdentifier>();
    
    /** map to store technical Names */
    private Map<Widget, String> m_techNameMap = new HashMap<Widget, String>();
    
    /** The RecordActions */
    private int m_popupMouseBtn = SWT.BUTTON3;

    /** The RecordHelper */
    private RecordHelperSWT m_recordHelperSWT = new RecordHelperSWT();
    
    /** 
     * default constructor
     */
    public RecordActionsSWT() {
        // do nothing
    }
    
    /**
     * contents of TextComponents
     * @return map 
     */
    public Map<Widget, String> getTextCompContent() {
        return m_map;
    }
    /**
     * contents of TextComponents
     * @param map The message data
     */
    public void setTextCompContent(Map<Widget, String> map) {
        m_map = map;
    }    
    /**
     * stores contents of TextComponents
     * @param source TextComponent
     * @param content content to store
     */
    public void addTextCompContent(Widget source, String content) {
        m_map.put(source, content);
    }
    
    /**
     * popup mouse button
     * @return map 
     */
    public int getPopupMouseButton() {
        return m_popupMouseBtn;
    }
    /**
     * set popup mouse button
     * @param popupMouseBtn popup mouse button
     */
    public void setPopupMouseButton(int popupMouseBtn) {
        m_popupMouseBtn = popupMouseBtn;
    }
    
    /**
     * select item
     * @param lst List
     * @param id IComponentIdentifier
     * @param a Action
     */
    protected void selectListValues(List lst, IComponentIdentifier id,
            Action a) {
        Object[] entries = lst.getSelection();
        String entr = StringConstants.EMPTY;
        for (int i = 0; i < entries.length; i++) {
            String item = StringParsing.maskAndSingleQuoteText(
                    entries[i].toString(), StringParsing.LISTCOMBOMASK);
            entr = entr.concat(item);
            if (i < (entries.length - 1)) {
                entr = entr.concat(","); //$NON-NLS-1$
            }
        }
        java.util.List<String> lstValues = new LinkedList<String>();
        lstValues.add(entr);
        lstValues.add(Constants.REC_OPERATOR);
        lstValues.add(Constants.REC_SEARCH_MODE);
        lstValues.add(Constants.REC_EXT_SELECTION);
        
        String logName = createLogicalName(lst, id);
        
        createCAP(a, id, lstValues, logName, StringConstants.EMPTY);
    }
    
    /**
     * select node by textpath
     * @param tre Tree
     * @param id IComponentIdentifier
     * @param a Action
     * @param clickcount int
     */
    protected void selectNode(Tree tre, IComponentIdentifier id,
        Action a, int clickcount) {
        TreeItem[] entries = tre.getSelection();
        String nodepath = m_recordHelperSWT.getTreePath(entries[0]);
        m_recordHelperSWT.setTreePath(StringConstants.EMPTY);
        int count = clickcount;
        String extraMsg = StringConstants.EMPTY;
        if (count < 1) {
            count = 1;
            extraMsg = Constants.REC_CLICK_MSG;
        }
        String clCount = String.valueOf(count);
        java.util.List<String> treValues = new LinkedList<String>();
        treValues.add(Constants.REC_SEARCH_MODE);
        treValues.add("0"); //$NON-NLS-1$
        treValues.add(nodepath);                
        treValues.add(Constants.REC_OPERATOR);
        treValues.add(clCount);
        treValues.add(String.valueOf(InteractionMode.primary.rcIntValue()));
        treValues.add(Constants.REC_EXT_SELECTION);
        
        String logName = createLogicalName(tre, id);
        
        createCAP(a, id, treValues, logName, extraMsg);
    }
    
    /**
     * select node/cell of Tree with columns (TableTree)
     * @param tre Tree
     * @param id IComponentIdentifier
     * @param a Action
     * @param clickcount int
     */
    protected void selectTableTreeCell(Tree tre, IComponentIdentifier id,
            Action a, int clickcount) {
        TreeItem node = tre.getSelection()[0];
        String nodepath = m_recordHelperSWT.getTreePath(node);
        m_recordHelperSWT.setTreePath(StringConstants.EMPTY);
        int col = m_recordHelperSWT.getSelectedTreeColumn(tre);
        String column = (new Integer(col).toString());
        int count = clickcount;
        String extraMsg = StringConstants.EMPTY;
        if (count < 1) {
            count = 1;
            extraMsg = Constants.REC_CLICK_MSG;
        }
        String clCount = String.valueOf(count);
        java.util.List<String> treValues = new LinkedList<String>();
        treValues.add(Constants.REC_SEARCH_MODE);
        treValues.add("0"); //$NON-NLS-1$
        treValues.add(nodepath);                
        treValues.add(Constants.REC_OPERATOR);
        treValues.add(clCount);
        treValues.add(column);
        treValues.add(String.valueOf(InteractionMode.primary.rcIntValue()));
        
        String logName = createLogicalName(tre, id);
        
        createCAP(a, id, treValues, logName, extraMsg);
    }
    
    /**
     * select tab
     * @param tf TabFolder
     * @param id IComponentIdentifier
     * @param a Action
     */
    protected void selectTab(TabFolder tf, IComponentIdentifier id,
            Action a) {
        java.util.List<String> tpnValues = new LinkedList<String>();
        String tpnTitle = StringParsing.singleQuoteText(tf
                .getItem(tf.getSelectionIndex()).getText());
        tpnTitle = SwtUtils.removeMnemonics(tpnTitle);
        tpnValues.add(tpnTitle);
        tpnValues.add(Constants.REC_OPERATOR);
        
        String logName = createLogicalName(tf, id);
        
        createCAP(a, id, tpnValues, logName, StringConstants.EMPTY);
    }
    
    /**
     * select ctab
     * @param ctf CTabFolder
     * @param id IComponentIdentifier
     * @param a Action
     */
    protected void selectCTab(CTabFolder ctf, IComponentIdentifier id,
            Action a) {
        java.util.List<String> tpnValues = new LinkedList<String>();
        String tpnTitle = StringParsing.singleQuoteText(ctf
                .getSelection().getText());
        tpnTitle = SwtUtils.removeMnemonics(tpnTitle);
        tpnValues.add(tpnTitle);
        tpnValues.add(Constants.REC_OPERATOR);
        
        String logName = createLogicalName(ctf, id);
        
        createCAP(a, id, tpnValues, logName, StringConstants.EMPTY);
    }
    
    /**
     * select value
     * @param cbx Combo
     * @param id IComponentIdentifier
     * @param a Action
     */
    protected void selectCbxValue(Combo cbx, IComponentIdentifier id,
            Action a) {
        String cbxText = StringParsing.singleQuoteText(cbx.getItem(cbx
                .getSelectionIndex()).toString());
        if (cbxText.equals(StringConstants.EMPTY) || cbxText == null) {
            cbxText = Constants.EMPTY_ITEM;
        }
        java.util.List<String> cbxValues = new LinkedList<String>();
        cbxValues.add(cbxText);
        cbxValues.add(Constants.REC_OPERATOR);
        cbxValues.add(Constants.REC_SEARCH_MODE);
        
        String logName = createLogicalName(cbx, id);
        
        createCAP(a, id, cbxValues, logName, StringConstants.EMPTY);
    }
    /**
     * select value
     * @param ccbx CCombo
     * @param id IComponentIdentifier
     * @param a Action
     */
    protected void selectCCbxValue(CCombo ccbx, IComponentIdentifier id,
            Action a) {
        String ccbxText = StringParsing.singleQuoteText(ccbx.getItem(ccbx
                .getSelectionIndex()).toString());
        if (ccbxText.equals(StringConstants.EMPTY) || ccbxText == null) {
            ccbxText = Constants.EMPTY_ITEM;
        }
        java.util.List<String> ccbxValues = new LinkedList<String>();
        ccbxValues.add(ccbxText);
        ccbxValues.add(Constants.REC_OPERATOR);
        ccbxValues.add(Constants.REC_SEARCH_MODE);
        
        String logName = createLogicalName(ccbx, id);
        
        createCAP(a, id, ccbxValues, logName, StringConstants.EMPTY);
    }
    
    /**
     * select cell
     * @param tbl Table
     * @param id IComponentIdentifier
     * @param clickcount int
     * @param a Action
     * @param cell int[]
     */
    protected void selectTableCell(Table tbl, IComponentIdentifier id,
            int clickcount, Action a, int[] cell) {
        String rowStr = new Integer(cell[0]).toString();
        String columnStr = new Integer(cell[1]).toString();
        int count = clickcount;
        String extraMsg = StringConstants.EMPTY;
        if (count < 1) {
            count = 1;
            extraMsg = Constants.REC_CLICK_MSG;
        }
        String clCount = (new Integer(count)
            .toString());
        java.util.List<String> tblValues = new LinkedList<String>();
        tblValues.add(rowStr);
        tblValues.add(MatchUtil.EQUALS);
        tblValues.add(columnStr);
        tblValues.add(MatchUtil.EQUALS);
        tblValues.add(clCount);
        tblValues.add("50"); //$NON-NLS-1$
        tblValues.add("percent"); //$NON-NLS-1$
        tblValues.add("50"); //$NON-NLS-1$
        tblValues.add("percent"); //$NON-NLS-1$
        tblValues.add(Constants.REC_EXT_SELECTION);
        
        String logName = createLogicalName(tbl, id);
        
        createCAP(a, id, tblValues, logName, extraMsg); 
    }
    
    /**
     * select MenuItem
     * @param mni MenuItem
     * @param id IComponentIdentifier
     * @param a Action
     * @param logName String for Logical Name
     */
    protected void selectMenuItem(MenuItem mni, IComponentIdentifier id, 
            Action a, String logName) {
        String pth = m_recordHelperSWT.getMenuPath(mni);
        pth = SwtUtils.removeMnemonics(pth);
        java.util.List<String> parValues = new LinkedList<String>();
        parValues.add(pth);
        parValues.add(Constants.REC_OPERATOR);
        if (a.getName().equals("CompSystem.PopupSelectByTextPathNew")) { //$NON-NLS-1$
            parValues.add((new Integer(m_popupMouseBtn)).toString());
        }
        
        if (logName != null) {
            createCAP(a, id, parValues, logName, StringConstants.EMPTY); 
        } else {
            createCAP(a, id, parValues);
        }
  
    }
    
    /**
     * collapse or Expand Tree
     * @param collOrExp String
     * @param event Event
     */
    protected void collExpTree(String collOrExp, Event event) {
        IComponentIdentifier id = null;
        Widget widget = SwtUtils.getWidgetAtCursorLocation();
        try {
            id = ComponentHandler
                .getIdentifier(widget);
            String nodepath = null;
            nodepath = m_recordHelperSWT.getTreePath(event.item);
            m_recordHelperSWT.setTreePath(StringConstants.EMPTY);
            Action a = m_recordHelperSWT.compSysToAction(id, collOrExp);
            java.util.List<String> treValues = new LinkedList<String>();
            treValues.add(Constants.REC_SEARCH_MODE);
            treValues.add("0"); //$NON-NLS-1$
            treValues.add(nodepath);                
            treValues.add(Constants.REC_OPERATOR);
            
            String logName = createLogicalName(widget, id);
            
            createCAP(a, id, treValues, logName, StringConstants.EMPTY);
        } catch (NoIdentifierForComponentException nifce) {
            // no identifier for the component, log this as an error
            log.error("no identifier for '" + widget); //$NON-NLS-1$
        }
    }
    

    
    /**
     * creates CAP for Click on Graphics Component
     * @param id IComponentIdentifier
     * @param event Event
     * @param src Widget
     */
    protected void clickGraphComp(IComponentIdentifier id, Event event,
            Widget src) {
        if (src instanceof Table
                || src instanceof List                
                //|| src instanceof TabFolder
                //|| src instanceof CTabFolder
                || src instanceof Tree) {
            clickInComponent(id, event, src);
        } else {
            int count = event.count;
            String extraMsg = StringConstants.EMPTY;
            if (count < 1) {
                count = 1;
                extraMsg = Constants.REC_CLICK_MSG;
            }
            String clickcount = (new Integer(count)
                .toString());
            String mbutton = (new Integer(event.button)
                .toString());
            Action a = m_recordHelperSWT.compSysToAction(id, "CompSystem.Click"); //$NON-NLS-1$
            java.util.List<String> parValues = new LinkedList<String>();
            parValues.add(clickcount);
            parValues.add(mbutton);
            
            String logName = createLogicalName(src, id);
            
            createCAP(a, id, parValues, logName, extraMsg);
        }
    }
    
    /**
     * creates CAP for Click In Component
     * @param id IComponentIdentifier
     * @param event Event
     * @param src Widget
     */
    protected void clickInComponent(IComponentIdentifier id, Event event,
            Widget src) {
        int count = event.count;
        String extraMsg = StringConstants.EMPTY;
        if (count < 1) {
            count = 1;
            extraMsg = Constants.REC_CLICK_MSG;
        }
        String clickcount = (new Integer(count)
            .toString());
        String mbutton = (new Integer(event.button)
            .toString());
        Rectangle bounds = SwtUtils.getWidgetBounds(src);
        double x = event.x;
        double y = event.y;
        double width = bounds.width;
        double height = bounds.height;
        double percentX = x / width * 100.0;
        int percentXInt = (int)percentX;
        String percentXString = new Integer(percentXInt).toString();
        double percentY = y / height * 100.0;
        int percentYInt = (int)percentY;
        String percentYString = new Integer(percentYInt).toString();
        Action a = m_recordHelperSWT.compSysToAction(id, "CompSystem.ClickDirect"); //$NON-NLS-1$   
        String units = Constants.REC_UNITS;
        java.util.List<String> parValues = new LinkedList<String>();
        parValues.add(clickcount);
        parValues.add(mbutton);
        parValues.add(percentXString);
        parValues.add(units);
        parValues.add(percentYString);
        parValues.add(units); 
        
        String logName = createLogicalName(src, id);
        
        createCAP(a, id, parValues, logName, extraMsg); 
    }
    
    /**
     * creates CAP for KeyCominations like ENTER, BACKSPACE, SHIFT+TAB etc
     * @param id IComponentIdentifier
     * @param e Event
     * @param keycode int
     */
    protected void keyComboApp(IComponentIdentifier id,
            Event e, int keycode) {
        Action a = new Action();
        a = m_recordHelperSWT.compSysToAction(id, "CompSystem.KeyStroke"); //$NON-NLS-1$
        
        java.util.List<String> parameterValues = 
            new LinkedList<String>();            
        String modifierKey = null;
        if (e.stateMask == 0) {
            modifierKey = "none"; //$NON-NLS-1$
        }  else {
            modifierKey = SwtKeyCodeConverter.getModifierName(e.stateMask);
            if (modifierKey == null) {
                return;
                //char character = m_recordHelperSWT.topKey(e);
                //modifierKey = Character.toString(character);
            }
        }
                
        String baseKey = null;
        baseKey = SwtKeyCodeConverter.getKeyName(keycode);
        if (baseKey == null) {
            char character = RecordHelperSWT.topKey(e);
            baseKey = Character.toString(character).toUpperCase();
            if (baseKey == null) {
                return;
            }
        }
        
        parameterValues.add(modifierKey);
        parameterValues.add(baseKey);

        createCAP(a, id, parameterValues);
    }
    
    /**
     * @param source Component
     */
    protected void replaceText(Widget source) {
        String text = null;
        boolean isEditable = false;
        boolean isCbxValue = false;
        boolean isCCbxChild = false;
        boolean isSupported = true;
        if (source instanceof Text) {
            Text txt = (Text)source;
            text = txt.getText();
            isEditable = txt.getEditable();
            if (((txt.getStyle() & SWT.MULTI) != 0) 
                    && (text.indexOf(SWT.CR) != -1
                            || text.indexOf(SWT.LF) != -1)) {
                isSupported = false;
                ShowObservInfoMessage infoMsg =
                    new ShowObservInfoMessage(Constants.REC_MULTILINE_MSG);
                try {
                    AUTServer.getInstance()
                        .getServerCommunicator().send(infoMsg);
                } catch (CommunicationException e) { 
                    // no log available here
                }
            }
            if (txt.getParent() instanceof CCombo) {
                isCCbxChild = true;
            }
            if (txt.getParent() instanceof Table) {
                Table tbl = (Table)txt.getParent();
                replaceTableText(source, tbl, text);
                return;
            }
        }
        if (source instanceof Combo) {
            Combo cbx = (Combo)source;
            text = cbx.getText();
            isEditable = (cbx.getStyle() & SWT.READ_ONLY) == 0;
            isCbxValue = m_recordHelperSWT.containsValue(cbx, text);      
        }
        if (source instanceof CCombo) {
            CCombo ccbx = (CCombo)source;
            text = ccbx.getText();
            isEditable = ccbx.getEditable();
            isCbxValue = m_recordHelperSWT.containsValue(ccbx, text);
        }
        
        if (m_map.get(source) != null && !(text.equals(
                m_map.get(source).toString())) && isSupported
                && isEditable && !isCbxValue && !isCCbxChild) {
            m_map.put(source, text);
            IComponentIdentifier id = null;
            try {
                id = ComponentHandler
                    .getIdentifier(source);
                Action a = new Action();        
                a = m_recordHelperSWT.compSysToAction(id, "CompSystem.InputText"); //$NON-NLS-1$
                
                text = StringParsing.singleQuoteText(text);

                java.util.List<String> parameterValues = 
                    new LinkedList<String>();
                parameterValues.add(text);
                
                String logName = createLogicalName(source, id);
                
                createCAP(a, id, parameterValues, logName,
                        StringConstants.EMPTY);
                
            } catch (NoIdentifierForComponentException nifce) {
                // no identifier for the component, log this as an error
                log.error("no identifier for '" + source); //$NON-NLS-1$
            }
        }
    }
    
    /**
     * creates CAP for Actions Replace Text (Specified by Cell) on Table
     * @param src Widget 
     * @param tbl Table
     * @param text String
     */
    private void replaceTableText(Widget src, Table tbl, String text) {
        String txt = StringParsing.singleQuoteText(text);
        if (!(txt.equals(m_map.get(src).toString()))) {
            IComponentIdentifier id = null;
            try {
                id = ComponentHandler.getIdentifier(tbl);
                Action a = new Action();
                a = m_recordHelperSWT.compSysToAction(id, "CompSystem.ReplaceTextInTableCellNew"); //$NON-NLS-1$        

                Cell cell = TableSelectionTracker
                    .getInstance().getSelectedCell(tbl);
                int row = cell.getRow();
                int column = cell.getCol();
                String rowStr = (new Integer(row + 1)).toString();
                String columnStr = (new Integer(column + 1)).toString();
                java.util.List<String> parameterValues = 
                    new LinkedList<String>();
                parameterValues.add(txt);
                parameterValues.add(rowStr);
                parameterValues.add(columnStr);
                
                String logName = createLogicalName(tbl, id);
                
                createCAP(a, id, parameterValues, logName,
                        StringConstants.EMPTY);
                m_map.put(src, txt);  
            } catch (NoIdentifierForComponentException nifce) {
                // no identifier for the component, log this as an error
                log.error("no identifier for '" + tbl); //$NON-NLS-1$
            }
        }  
    }
    
    /**
     * select item
     * @param shl Shell
     * @param id IComponentIdentifier
     * @param a Action
     */
    protected void waitForWindow(Shell shl, IComponentIdentifier id, Action a) {
        String title = StringParsing.singleQuoteText(shl.getText());
        String operator = Constants.REC_OPERATOR;
        String delay = new Integer(Constants.REC_WAIT_DELAY).toString();
        
        String timeout = null;
        long timestamp = AUTServer.getInstance().getObservTimestamp();
        
        if (timestamp == 0) {
            timeout = new Integer(Constants.REC_WAIT_TIMEOUT).toString();
        } else {
            long timeoutLong = (System.currentTimeMillis() - timestamp) 
                + 10000;
            double timeoutDouble = (Math.ceil(timeoutLong / 5000.0)) * 5000.0;
            int timeoutInt = (int)timeoutDouble;
            timeout = new Integer(timeoutInt).toString();
        }
        
        java.util.List<String> shlValues = new LinkedList<String>();
        shlValues.add(title);
        shlValues.add(operator);
        shlValues.add(timeout);
        shlValues.add(delay);
        createCAP(a, id, shlValues);
    }
    
    /**
     * records and executes a cap
     * @param a Action
     * @param id IComponentIdentifier
     * @param parValues List of values
     */
    private void createCAP(Action a,
            IComponentIdentifier id, java.util.List<String> parValues) {
        String defaultName = "default"; //$NON-NLS-1$
        String defaultMsg = StringConstants.EMPTY;
        createCAP(a, id, parValues, defaultName, defaultMsg);
    }
    
    /**
     * records and executes a cap
     * @param a Action
     * @param id IComponentIdentifier
     * @param parValues List of values
     * @param logName Logical Name
     * @param extraMsg additonal Message for Observation Console
     */
    private void createCAP(Action a,
            IComponentIdentifier id, java.util.List<String> parValues,
                String logName, String extraMsg) {
        MessageCap messageCap = new MessageCap();        
        
        // setup Action in MessageCap
        messageCap.setMethod(a.getMethod());

        messageCap.setAction(a);
        
        // setup ComponentIdentifier in MessageCap
        messageCap.setCi(id);
        
        // setup parameters in MessageCap
        java.util.List<String> parameterValues = parValues;
        java.util.List params = a.getParams();

        for (int i = 0; i < params.size(); i++) {
            Param param = (Param) params.get(i);
            MessageParam messageParam = new MessageParam();
            messageParam.setType(param.getType());
            String emptyString = StringConstants.EMPTY;
            String value = 
                emptyString.equals(parameterValues.get(i)) ? null 
                    : parameterValues.get(i);            
            messageParam.setValue(value);
            messageCap.addMessageParam(messageParam);
            
            if (!(logName.equals("default"))) { //$NON-NLS-1$
                messageCap.setLogicalName(logName);
            } else {
                messageCap.sethasDefaultMapping(true);
            }
            
            messageCap.setExtraMessage(extraMsg);
        }
        CAPRecordedMessage capRecMessage = new CAPRecordedMessage(messageCap);
                
        try {            
            RecordActionMessage message =
                new RecordActionMessage(capRecMessage);            
            AUTServer.getInstance().getServerCommunicator().send(message);
        } catch (CommunicationException e) { 
            log.error(e.getLocalizedMessage(), e);
        }
        AUTServer.getInstance().setObservTimestamp(System.currentTimeMillis());
    }
    
    /**
     * creates logical name for widget
     * @param w widget
     * @param id IComponentIdentifier
     * @return logical Name
     */
    private String createLogicalName(Widget w, IComponentIdentifier id) {
        
        String logName = m_techNameMap.get(w);
        
        if (logName == null) {
            logName = m_recordHelperSWT.generateLogicalName(w, id);
            
            if (logName != null) {
                IComponentIdentifier id2 = 
                    m_logNameMap.get(logName);
                if (m_logNameMap.containsKey(logName)) {
                    if (!(m_recordHelperSWT.isCiEqual(id, id2))) {
                        Collection<String> col = m_techNameMap.values();
                        Iterator<String> it = col.iterator();
                        int counter = 0;
                        while (it.hasNext()) {
                            String name = it.next();
                            if (name.equals(logName)
                                    || name.equals(
                                            logName + "_" + (counter + 1))) { //$NON-NLS-1$
                                counter++;
                            }
                        }
                        logName = logName + "_" + counter; //$NON-NLS-1$
                    }
                }
                m_logNameMap.put(logName, id);
                m_techNameMap.put(w, logName);
            }
        }   
        
        return logName;
    }
    
} 
