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

import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.Constants;
import org.eclipse.jubula.rc.common.exception.NoIdentifierForComponentException;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.Widget;



/**
 * The SWTEventListener for mode OBJECT_MAPPING. <br>
 * 
 * The component is marked by calling the methods
 * highLight() and lowLight() respectively of the corresponding implementation
 * class. <br>
 * 
 * The key events are tapped for selecting the <code>m_currentComponent</code>
 * to be used for the object mapping. The method <code>accept(KeyEvent)</code>
 * from the <code>MappingAcceptor</code> is queried to decide, whether the
 * event suits the active configuration. <br>
 * 
 * A <code>ComponentHandler</code> is used to determine the identifaction of
 * the component. See the <code>ComponentHandler</code> for details.
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
 *
 */
public class RecordListener extends AbstractAutSwtEventListener {
    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        RecordListener.class);
    
    /** widget the popup is occurred on */
    private Widget m_popupSource = null;
    
    /** The RecordActions */
    private int m_popupMouseBtn = SWT.BUTTON3;
    
    /** ccombo with list */
    private Widget m_ccombo = null;
    
    /** clicked or pressed Widget */
    private Widget m_selectedWidget;
    
    /** clicked or pressed WidgetParent */
    private Widget m_selectedWidgetParent;
    
    /** The RecordHelper */
    private RecordHelperSWT m_recordHelperSWT = new RecordHelperSWT();
    
    /** The RecordActions */
    private RecordActionsSWT m_recordActionsSWT = new RecordActionsSWT();
    
    /**
     * {@inheritDoc}
     */
    public long[] getEventMask() {
        return new long[]{
            SWT.MouseMove, SWT.MouseUp, SWT.MouseEnter, SWT.MouseDown,
            SWT.KeyDown, SWT.Collapse, SWT.Expand, SWT.MenuDetect, SWT.Show,
            SWT.Selection, SWT.FocusIn, SWT.FocusOut, SWT.Traverse}; 
            //SWT.Modify, SWT.Arm, SWT.SetData, SWT.UP
    }
    
    /**
     * {@inheritDoc}
     */
    public void handleEvent(final Event event) {
        if (!(event.widget instanceof MenuItem)
                && !(event.widget instanceof Menu)
                && (event.type == SWT.MouseUp
                        || event.type == SWT.MouseDown)) {
            m_popupMouseBtn = event.button;
            m_recordActionsSWT.setPopupMouseButton(m_popupMouseBtn);
        }
        final Display display = ((SwtAUTServer)
                AUTServer.getInstance()).getAutDisplay();
        if (display != null) {
            display.syncExec(new Runnable() {
                public void run() {
                    if (event.equals(getLastEvent()) || event.widget == null
                            || eventShouldBeIgnored(event)
                            || (!(m_recordHelperSWT
                                        .isSupportedWidget(event.widget))
                                    && event.type != SWT.KeyDown
                                    && event.type != SWT.Traverse)) {
                        return;
                    }
                    if (event.type == SWT.Show) {
                        handleShowEvent(event); 
                    }                    
                    if (event.type == SWT.FocusIn
                            || event.type == SWT.FocusOut) {
                        handleFocusEvent(event);
                    }
                    if (event.widget instanceof CCombo) {
                        CCombo ccbx = (CCombo)event.widget;
                        m_ccombo = ccbx;
                    }
                    if ((getSelectedWidget() == event.widget
                            || getSelectedWidgetParent() == event.widget)) {
                        if (event.type == SWT.Expand) {
                            m_recordActionsSWT.collExpTree("CompSystem.ExpandByTextPath", event); //$NON-NLS-1$
                        } else if (event.type == SWT.Collapse) {
                            m_recordActionsSWT.collExpTree("CompSystem.CollapseByTextPath", event); //$NON-NLS-1$
                        }
                    }
                    if (event.type == SWT.Selection) {
                        handleSelectionEvent(event);
                    }
                    if ((event.type == SWT.MouseUp 
                            || event.type == SWT.MouseDown)
                            && getLastEvent().type != SWT.MenuDetect) {
                        Widget src = event.widget;
                        if (src instanceof ToolBar || src instanceof CoolBar) {
                            src = SwtUtils.getWidgetAtCursorLocation();
                        }
                        handleMouseEvent(event, src);
                    }
                    if (event.type == SWT.MouseEnter) {
                        handleMouseEnterEvent(event);
                    }
                    if ((event.type == SWT.KeyDown 
                            && event.keyCode != SWT.ARROW_DOWN
                            && event.keyCode != SWT.ARROW_LEFT
                            && event.keyCode != SWT.ARROW_RIGHT
                            && event.keyCode != SWT.ARROW_UP)
                            && event.keyCode != SWT.ESC
                            && event.keyCode != SWT.CR
                            || (event.type == SWT.Traverse 
                                    && event.detail != SWT.TRAVERSE_MNEMONIC
                                    && event.detail != SWT.TRAVERSE_NONE)) {
                        setSelectedWidget(event.widget);
                        setSelectedWidgetParent(m_recordHelperSWT
                                .getWidgetParent(event.widget));
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
            final int accepted = getAcceptor().accept(event);
            final boolean isSingleTrigger =
                getAcceptor().isSingleLineTrigger(event);
            final boolean isMultiTrigger =
                getAcceptor().isMultiLineTrigger(event);
            IComponentIdentifier id = null;
            int keycode = event.keyCode;
            Widget source = event.widget;
            id = m_recordHelperSWT.getApplicationCompID();
            if (event.type == SWT.Traverse && event.widget instanceof Shell
                    && getLastEvent().type == SWT.Traverse
                    && !(getLastEvent().widget instanceof Shell)) {
                return;
            }
            if (accepted != KeyAcceptor.CHECKMODE_KEY_COMB
                    && accepted != KeyAcceptor.CHECKCOMP_KEY_COMB) {
                if (!(source instanceof Text)
                        && !(source instanceof Combo)
                        && !(source instanceof CCombo)
                        && keycode != SWT.ALT
                        && keycode != SWT.CONTROL
                        && keycode != SWT.CTRL
                        && keycode != SWT.SHIFT
                        && event.stateMask != (SWT.SHIFT | SWT.ALT)
                        && event.stateMask != (
                                SWT.CTRL | SWT.SHIFT | SWT.ALT)) {
                    m_recordActionsSWT.keyComboApp(id, event, keycode);
                }
                if ((source instanceof Combo
                        || source instanceof CCombo)
                        && (isSingleTrigger)) {
                    m_recordActionsSWT.replaceText(source);
                    m_recordActionsSWT.keyComboApp(id, event, keycode);
                }
                if (source instanceof Text && !(belongsToCCombo(source))) {
                    Text txt = (Text)source;
                    if (((txt.getStyle() & SWT.MULTI) != 0
                        && isMultiTrigger) || ((txt.getStyle() & SWT.MULTI) == 0
                        && isSingleTrigger)) {
                        m_recordActionsSWT.replaceText(source);
                        if (event.stateMask != (SWT.SHIFT | SWT.ALT)
                                && event.stateMask != (
                                        SWT.CTRL | SWT.SHIFT | SWT.ALT)) {
                            m_recordActionsSWT.keyComboApp(id, event, keycode);
                        }
                    }
                }
            }
            //activates "CheckMode" (Highlighting)
            if (accepted == KeyAcceptor.CHECKMODE_KEY_COMB) {
                if ((source instanceof Text
                        || source instanceof Combo
                        || source instanceof CCombo)) {
                    m_recordActionsSWT.replaceText(source);
                }
                changeCheckModeState(ChangeAUTModeMessage.CHECK_MODE);
            }
        }
    }
    
        
    /**
     * method for handling Mouse-Events
     * @param event Event
     * @param widget Widget
     */
    protected void handleMouseEvent(Event event, Widget widget) {
        setCurrentComponent(widget);
        boolean isCell = false;
        if (getCurrentComponent() instanceof Text) {
            Text txt = (Text)getCurrentComponent();
            if (txt.getParent() instanceof Table) {
                isCell = true;
            }
        }
        IComponentIdentifier id;
        try {
            if (event.type == SWT.MouseUp 
                    && !(getCurrentComponent() instanceof Shell)
                    && !(getCurrentComponent() instanceof Group)
                    && !isCell && getCurrentComponent() != null) {
                id = ComponentHandler
                    .getIdentifier(getCurrentComponent());
                if (widget != null
                        && !(event.widget instanceof TabFolder)
                        && !(event.widget instanceof CTabFolder)
                        && !(event.widget instanceof List)
                        && !(event.widget instanceof Tree)
                        && !(event.widget instanceof Combo)
                        && !(event.widget instanceof CCombo)
                        && !(event.widget instanceof Text
                                && ((Text)event.widget).getEditable())
                        && !(belongsToCCombo(event.widget))) {
                    if (event.widget instanceof Table) {
                        Table tbl = (Table)event.widget;
                        int[] cell = m_recordHelperSWT.getSelectedCell(tbl);
                        if (cell[0] != -1 && cell[1] != -1) {
                            int clickcount = (event.count != 0) 
                                ? event.count : getLastEvent().count;
                            Action a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectCellNew"); //$NON-NLS-1$
                            m_recordActionsSWT.selectTableCell(
                                    tbl, id, clickcount, a, cell);
                        } else {
                            m_recordActionsSWT.clickGraphComp(
                                    id, event, widget);
                        }
                    } else if (!(belongsToCCombo(getLastEvent().widget)
                            && getLastEvent().widget instanceof Button
                            && event.widget instanceof CCombo)
                            && getLastEvent().type != SWT.Selection
                            && getLastEvent().type != SWT.Expand
                            && getLastEvent().type != SWT.Collapse) {
                        m_recordActionsSWT.clickGraphComp(id, event, widget);
                    }
                }
            }
        } catch (NoIdentifierForComponentException nifce) {
            // no identifier for the component, log this as an error
            log.error("no identifier for '" + getCurrentComponent()); //$NON-NLS-1$
        }
    }
    
    /**
     * method for handling MouseEnter-Events
     * @param event Event
     */
    protected void handleMouseEnterEvent(Event event) {
        if (event.widget instanceof List
                && m_recordHelperSWT.isDropdownList(
                        (List)event.widget, m_ccombo)) {
            setSelectedWidget(m_ccombo);
        } else {
            setSelectedWidget(event.widget);
        }                        
        setSelectedWidgetParent(m_recordHelperSWT
                .getWidgetParent(event.widget));
    }
    
    /**
     * method for handling Show-Events
     * @param event Event
     */
    protected void handleShowEvent(Event event) {
        if ((getLastEvent().type == SWT.MenuDetect
                || getLastEvent().widget instanceof ToolItem
                || getLastEvent().widget instanceof ToolBar
                || getLastEvent().widget instanceof CoolBar)
                || getLastEvent().type == SWT.MouseDown
                || getLastEvent().type == SWT.MouseUp) {
            m_popupSource = getLastEvent().widget;
            if (m_popupSource instanceof ToolBar
                    || m_popupSource instanceof CoolBar) {
                m_popupSource = SwtUtils.getWidgetAtCursorLocation();
            }
        }

        boolean isCCombo = false;
        if (getLastEvent().widget instanceof Button) {
            Button btn = (Button)getLastEvent().widget;
            Widget parent = btn.getParent();
            if (parent instanceof CCombo) {
                isCCombo = true;
                m_ccombo = parent;
            }
        } else if (getLastEvent().widget instanceof Text) {
            Text txt = (Text)getLastEvent().widget;
            Widget parent = txt.getParent();
            if (parent instanceof CCombo) {
                isCCombo = true;
                m_ccombo = parent;
            }
        } else if (getLastEvent().widget instanceof CCombo) {
            CCombo ccbx = (CCombo)getLastEvent().widget;
            isCCombo = true;
            m_ccombo = ccbx;
        }
        if (event.widget instanceof Shell && !isCCombo) {
            final Shell shell = (Shell)event.widget;
            int style = shell.getStyle();
            if ((style & (SWT.TITLE)) == SWT.TITLE
                    || (style & (SWT.CLOSE)) == SWT.CLOSE
                    || (style & (SWT.MIN)) == SWT.MIN
                    || (style & (SWT.MAX)) == SWT.MAX) { //|| SWT.RESIZE
                final IComponentIdentifier id = m_recordHelperSWT
                    .getApplicationCompID();
                Action wfw = m_recordHelperSWT.compSysToAction(
                        id, "CompSystem.WaitForWindow"); //$NON-NLS-1$
                final Action wfwtc = m_recordHelperSWT.compSysToAction(
                        id, "CompSystem.WaitForWindowToClose"); //$NON-NLS-1$
                int [] closeEvents = {SWT.Dispose, SWT.Close, SWT.Hide};
                for (int i = 0; i < closeEvents.length; i++) {
                    shell.addListener(closeEvents[i], new Listener() {
                        public void handleEvent(Event e) {
                            m_recordActionsSWT.waitForWindow(shell, id, wfwtc);
                        }
                    });
                }
                m_recordActionsSWT.waitForWindow(shell, id, wfw);
            }            
        }
    }

    
    /**
     * method for handling Selection-Events
     * @param event Event
     */
    protected void handleSelectionEvent(Event event) {
        Widget widget = event.widget;
        IComponentIdentifier id = null;
        Action a = new Action();
        if (!(widget instanceof MenuItem)
                && (getSelectedWidget() == widget
                        || getSelectedWidgetParent() == widget)) {
            try {
                id = ComponentHandler.getIdentifier(widget);
                if (widget instanceof List) {
                    List lst = (List)widget;
                    if (m_recordHelperSWT.isDropdownList(lst, m_ccombo)) {
                        return;
                    }
                    if (lst.getSelectionCount() != 0) {
                        a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectValues"); //$NON-NLS-1$
                        m_recordActionsSWT.selectListValues(lst, id, a);
                    }
                } else if (widget instanceof Tree) {
                    Tree tre = (Tree)widget;
                    int clickcount = (event.count != 0) 
                        ? event.count : getLastEvent().count;
                    if (tre.getSelectionCount() != 0) {
                        if (tre.getColumnCount() < 1) {
                            a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectByTextPath"); //$NON-NLS-1$
                            m_recordActionsSWT.selectNode(
                                    tre, id, a, clickcount);
                        } else {
                            a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectByTextPathAtColumn"); //$NON-NLS-1$                           
                            m_recordActionsSWT.selectTableTreeCell(
                                    tre, id, a, clickcount);
                        }
                    }                    
                } else if (widget instanceof Combo) {
                    Combo cbx = (Combo)widget;
                    a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectValue"); //$NON-NLS-1$
                    m_recordActionsSWT.selectCbxValue(cbx, id, a);
                } else if (widget instanceof CCombo) {
                    CCombo ccbx = (CCombo)widget;
                    a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectValue"); //$NON-NLS-1$
                    m_recordActionsSWT.selectCCbxValue(ccbx, id, a);
                } else if (widget instanceof CTabFolder) {
                    CTabFolder ctf = (CTabFolder)widget;
                    a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectTab"); //$NON-NLS-1$
                    m_recordActionsSWT.selectCTab(ctf, id, a);
                } else if (widget instanceof TabFolder) {
                    TabFolder tf = (TabFolder)widget;
                    a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectTab"); //$NON-NLS-1$
                    m_recordActionsSWT.selectTab(tf, id, a);
                }
            } catch (NoIdentifierForComponentException
                    nifce) {
                // no identifier for the component, log this as an error
                log.error("no identifier for '" + widget); //$NON-NLS-1$
            } 
        } else if (widget instanceof MenuItem) {
            selectMenuItem(widget);
        }
    }
    
    /**
     * method for handling Focus-Events
     * @param event Event
     */
    protected void handleFocusEvent(Event event) {        
        Widget source = event.widget;

        if (event.type == SWT.FocusIn
                && (source instanceof Text
                        || source instanceof Combo
                        || source instanceof CCombo)) {
            String content = null;
            if (source instanceof Text) {
                Text textComp = (Text)source;
                content = textComp.getText();
            }
            if (source instanceof Combo) {
                Combo cbx = (Combo)source;
                content = cbx.getText();
            }
            if (source instanceof CCombo) {
                CCombo ccbx = (CCombo)source;
                content = ccbx.getText();
            }
            m_recordActionsSWT.addTextCompContent(source, content);
        }
        if (event.type == SWT.FocusOut
                && (source instanceof Text
                        || source instanceof Combo
                        || source instanceof CCombo)) {
            m_recordActionsSWT.replaceText(source);
        }
    }
    
    /**
     * method to choose if event should be ignored
     * @param event Event
     * @return true, if event should be ignored, false otherwise
     */
    private boolean eventShouldBeIgnored(Event event) {
        boolean ignore = false;
        if ((event.type == SWT.FocusOut || event.type == SWT.FocusIn)
                && (!(event.widget instanceof Text)
                        && !(event.widget instanceof Combo)
                        && !(event.widget instanceof CCombo))) {
            ignore = true;
        }
        if (event.type == SWT.Show && !(event.widget instanceof Menu)
                && !(event.widget instanceof Shell)) {
            ignore = true;
        }
        return ignore;
    }
    
    /**
     * select MenuItem
     * @param widget Widget
     */
    private void selectMenuItem(Widget widget) {
        MenuItem mni = (MenuItem)widget;
        if (((mni.getStyle() & SWT.RADIO) == SWT.RADIO)
                && !(mni.getSelection())) {
            return;
        }
        String logName = null;
        IComponentIdentifier id = null;
        Action a = new Action();        
        if (m_recordHelperSWT.isMenuBarItem(mni.getParent())) {
            id = m_recordHelperSWT.getMenuCompID();
            a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectMenuItem"); //$NON-NLS-1$
        } else {
            try {
                if (m_popupSource == null) {
                    return;
                }
                id = ComponentHandler.getIdentifier(m_popupSource);
                
                if (m_popupSource instanceof ToolItem 
                        && ((m_popupSource.getStyle() & SWT.DROP_DOWN) != 0)) {
                    a = m_recordHelperSWT.compSysToAction(id, "CompSystem.SelectMenuItem"); //$NON-NLS-1$
                } else {
                    a = m_recordHelperSWT.compSysToAction(id, "CompSystem.PopupSelectByTextPathNew"); //$NON-NLS-1$
                }
                logName = m_recordHelperSWT.generateLogicalName(
                        m_popupSource, id);
                m_popupSource = null;
            } catch (NoIdentifierForComponentException nifce) {
                // no identifier for the component, log this as an error
                log.error("no identifier for '" + widget); //$NON-NLS-1$
            }
        }
        m_recordActionsSWT.selectMenuItem(mni, id, a, logName);
    }
    
    /**
     * Verifies that the given widget belongs to a ccombo.
     * 
     * @param w Widget
     * @return true if the given widget belongs to a ccombo, false otherwise.
     */
    protected boolean belongsToCCombo(Widget w) {
        boolean isCCbxChild = false;
        if (w instanceof Button) {
            Button btn = (Button)w;
            Widget parent = btn.getParent();
            if (parent instanceof CCombo) {
                isCCbxChild = true;
                m_ccombo = parent;
            }
        } else if (w instanceof Text) {
            Text txt = (Text)w;
            Widget parent = txt.getParent();
            if (parent instanceof CCombo) {
                isCCbxChild = true;
                m_ccombo = parent;
            }
        }
        return isCCbxChild;
    }
    
    /**
     * Verifies that the given widget belongs to a ccombo.
     * 
     * @param w Widget
     * @return true if the given widget belongs to a combo, false otherwise.
     */
    protected boolean belongsToCombo(Widget w) {
        boolean isCbxChild = false;
        if (w instanceof Button) {
            Button btn = (Button)w;
            Widget parent = btn.getParent();
            if (parent instanceof Combo) {
                isCbxChild = true;
                m_ccombo = parent;
            }
        } else if (w instanceof Text) {
            Text txt = (Text)w;
            Widget parent = txt.getParent();
            if (parent instanceof Combo) {
                isCbxChild = true;
                m_ccombo = parent;
            }
        }
        return isCbxChild;
    }
    
    /**
     * @return Returns the selectedWidget.
     */
    protected Widget getSelectedWidget() {
        return m_selectedWidget;
    }

    /**
     * @param selectedWidget The clicked/pressed Widget to set.
     */
    protected void setSelectedWidget(Widget selectedWidget) {
        m_selectedWidget = selectedWidget;
    }
    
    /**
     * @return Returns the selectedWidgetParent.
     */
    protected Widget getSelectedWidgetParent() {
        return m_selectedWidgetParent;
    }

    /**
     * @param selectedWidgetParent The clicked/pressed WidgetParent to set.
     */
    protected void setSelectedWidgetParent(Widget selectedWidgetParent) {
        m_selectedWidgetParent = selectedWidgetParent;
    }
    
    /**
     * {@inheritDoc}
     */
    protected Color getBorderColor() {
        return new Color(null, Constants.OBSERVING_R,
                    Constants.OBSERVING_G, 
                    Constants.OBSERVING_B);
    }
     
    
}