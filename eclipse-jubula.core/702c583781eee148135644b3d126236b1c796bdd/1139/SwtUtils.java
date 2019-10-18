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
package org.eclipse.jubula.rc.swt.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.driver.KeyCodeConverter;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.utils.EnvironmentUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Caret;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.CoolItem;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Item;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ScrollBar;
import org.eclipse.swt.widgets.Scrollable;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.swt.widgets.Tracker;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 19.07.2006
 */
public class SwtUtils {

    /** full qualified class name for TabbedPropertyList*/
    private static final String TABBED_PROPERTY_LIST_FQN = "org.eclipse.ui.internal.views.properties.tabbed.view.TabbedPropertyList"; //$NON-NLS-1$

    /** Constant for identification of win32 graphics toolkit */
    private static final String WIN32 = "win32"; //$NON-NLS-1$
    
    /** Constant for identification of GTK graphics toolkit */
    private static final String GTK = "gtk"; //$NON-NLS-1$
    
    /** name of method to use for retrieving the bounds of a component */
    private static final String METHOD_NAME_GET_BOUNDS = "getBounds"; //$NON-NLS-1$
    
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(SwtUtils.class);
    
    /** 
     * Utility constructor
     */
    private SwtUtils() {
        // do nothing
    }

    /**
     * Gets the Widget under the current mouse position or null.
     * @return the Widget under the current mouse position or null.
     */
    public static Widget getWidgetAtCursorLocation() {
        final Display display = ((SwtAUTServer)AUTServer.getInstance())
            .getAutDisplay();
        if (display == null || display.isDisposed()) {
            return null;
        }
        Control control = display.getCursorControl();
        if (control == null) {
            return null;
        }
        control = checkControlParent(control);

        Widget widget = checkControlChildrenAtCursorLocation(control);
        return widget;
    }

    /**
     * Calls <code>getWidgetAtCursorLocation()</code> in the GUI thread.
     * 
     * @return the result of the called method.
     */
    public static Widget invokeGetWidgetAtCursorLocation() {
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        Widget widget = evThreadQueuer.invokeAndWait("getWidgetAtCursorLocation", //$NON-NLS-1$
            new IRunnable<Widget>() { 
                public Widget run() throws StepExecutionException {
                    return getWidgetAtCursorLocation();
                }
            });
        return widget;
    }
    
    /**
     * @return the Control under the cursor
     */
    public static  Control getCursorControl() {
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        return evThreadQueuer.invokeAndWait("getCursorControl", //$NON-NLS-1$
                new IRunnable<Control>() {
                    public Control run() {
                        Display disp = Display.getCurrent();
                        if (disp == null || disp.isDisposed()) {
                            return null;
                        }

                        return disp.getCursorControl();
                    }
                });
    }

    /**
     * Returns when there are no more events for the display to process.
     * 
     * @param display The display to wait on.
     */
    public static void waitForDisplayIdle(final Display display) {
        display.syncExec(new Runnable() {
            public void run() {
                while (!display.isDisposed() && display.readAndDispatch()) {
                    display.readAndDispatch();
                }
                display.update();
            }
        });
    }
    
    /**
     * @param widget a Widget
     * @return the items of the given Widget or an empty Array 
     * if the given Widget does not contain any Items
     */
    public static Item[] getMappableItems(Widget widget) {
        try {
            if (widget instanceof ToolBar) {
                return ((ToolBar)widget).getItems();
            }
            if (widget instanceof CoolBar) {
                return ((CoolBar)widget).getItems();
            }
        } catch (NullPointerException e) {
            // Do nothing. This occurs when the widget (ToolBar) is currently 
            // being disposed, as the "items" field (ToolBar.items) is null. 
            // See Ticket #2160.
        }
        return new Item[0];
    }

    /**
     * @param widget a Widget
     * @return the Shell of the given Widget or null if no Shell was found
     * @see Control#getShell();
     */
    public static Shell getShell(Widget widget) {
        Widget wdgt = widget;
        while (!(wdgt instanceof Control) && wdgt != null) {
            wdgt = getWidgetParent(wdgt);
        }
        if (wdgt != null && !wdgt.isDisposed()) {
            return ((Control)wdgt).getShell();
        }
        return null;
    }


    /**
     * Checks the given Control if there is a child at the current 
     * Mouseposition which cannot be found
     * via {@link Display#getCursorControl()}, e.g. Tool-/CoolbarItem,
     * disabled Button, etc. and returns this Widget.<br>
     * If the given Control does not contain any other controls, the given 
     * Control will be returned. 
     * @param control the Control to check.
     * @return a Widget.
     */
    private static Widget checkControlChildrenAtCursorLocation(
            final Control control) {
        if (control != null && TABBED_PROPERTY_LIST_FQN.equals(
                control.getClass().getName())) {
            return control;
        }
        Item[] items = getMappableItems(control);
        if (items == null) {
            return control;
        }
        final Point absMousePos = control.getDisplay().getCursorLocation();
        for (int i = 0; i < items.length; i++) {
            Item item = items[i];
            Rectangle itemBounds = getWidgetBounds(item);
            if (itemBounds.contains(absMousePos)) {
                return item;
            }
        }
        if (control instanceof Composite) {
            Composite composite = (Composite)control;
            Control[] children = composite.getChildren();
            for (int i = 0; i < children.length; i++) {
                Control ctrl = children[i];
                if (getWidgetBounds(ctrl).contains(absMousePos)) {
                    return checkControlChildrenAtCursorLocation(ctrl);
                }
            }
        }
        return control;
    }

    /**
     * Checks the given Control if its parent is a composite that should
     * be treated as the actual component. For example, if <code>control</code>
     * is a Button which is part of a <code>CCombo</code>, we treat the
     * <code>CCombo</code> as the component. 
     * If <code>control</code> is not part of such a composite, 
     * <code>control</code> will be returned. 
     * @param control the Control to check.
     * @return the Control to be treated as the actual component.
     */
    public static Control checkControlParent(final Control control) {
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        return evThreadQueuer.invokeAndWait("checkControlParent",  //$NON-NLS-1$
            new IRunnable<Control>() {

                public Control run() throws StepExecutionException {
                        if (control != null && !control.isDisposed()) {
                            Control parent = control.getParent();
                            if (parent != null && (parent instanceof CCombo
                                    || parent instanceof Table
                                    || TABBED_PROPERTY_LIST_FQN.equals(
                                            parent.getClass().getName()))) {
                                // children could be a TableEditor?
                                return parent;
                        }
                    }
                    return control;
                }
            });
    }

    /**
     * This method runs in the GUI-Thread.
     * 
     * @param widget the widget to get the parent for
     * @return the parent widget, or <code>null</code> if the given widget has 
     *         already been disposed.
     */
    public static Widget getWidgetParent(final Widget widget) {
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        Widget parent = evThreadQueuer.invokeAndWait("getWidgetParent", //$NON-NLS-1$
            new IRunnable<Widget>() { 
                public Widget run() throws StepExecutionException {
                    if (widget == null || widget.isDisposed()) {
                        return null;
                    }
                    return getWidgetParentImpl(widget);
                }
            });
        return parent;
    }

    /**
     * Look up the apparent parent of a component. A popup menu's parent is the
     * menu or component that spawned it.
     * 
     * @param widget the widget to get the parent for
     * @return the parent widget
     */
    private static Widget getWidgetParentImpl(Widget widget) {
        
        Widget parent = null;
        
        if (widget == null) {
            log.error("Cannot get parent for null widget."); //$NON-NLS-1$
        }
        if (widget instanceof Control) {
            parent = ((Control)widget).getParent();
        } else if (widget instanceof Caret) {
            parent = ((Caret)widget).getParent();
        } else if (widget instanceof Menu) {
            parent = ((Menu)widget).getParent();
        } else if (widget instanceof ScrollBar) {
            parent = ((ScrollBar)widget).getParent();
        } else if (widget instanceof CoolItem) {
            parent = ((CoolItem)widget).getParent();
        } else if (widget instanceof MenuItem) {
            parent = ((MenuItem)widget).getParent();
        } else if (widget instanceof TabItem) {
            parent = ((TabItem)widget).getParent();
        } else if (widget instanceof TableColumn) {
            parent = ((TableColumn)widget).getParent();
        } else if (widget instanceof TableItem) {
            parent = ((TableItem)widget).getParent();
        } else if (widget instanceof ToolItem) {
            parent = ((ToolItem)widget).getParent();
        } else if (widget instanceof TreeItem) {
            parent = ((TreeItem)widget).getParent();
        } else if (widget instanceof DragSource) {
            parent = ((DragSource)widget).getControl().getParent();
        } else if (widget instanceof DropTarget) {
            parent = ((DropTarget)widget).getControl().getParent();
        } else if (widget instanceof Tracker) {
            log.error("requested the parent of a Tracker- UNFINDABLE"); //$NON-NLS-1$
        }
        return parent;
    }

    /**
     * Returns a point describing the receiver's location in 
     * absolute/display/screen coordinates.
     *
     * @param widget a widget
     * @return the widget's location
     */
    public static Point getWidgetLocation(Widget widget) {
        Rectangle bounds = getWidgetBounds(widget);
        return new Point(bounds.x, bounds.y);
    }

    /**
     * Look up the bounds of <code>widget</code>. The location/origin of these bounds 
     * is relative to the location/origin of <code>relativeTo</code>.
     * @param widget the widget to get the bounds for. If this value is
     *               <code>null</code>, <code>null</code> will be returned.
     * @param relativeTo the Control to which the returned bounds are relative.
     *                   May be <code>null</code>. If this value is 
     *                   <code>null</code>, the returned bounds are "relative"
     *                   to the Display (which means they are essentially 
     *                   absolute).
     * @return the bounds of <code>widget</code> relative to 
     *         <code>relativeTo</code>, or <code>null</code> if the bounds 
     *         cannot be determined (for example, if <code>widget</code> is 
     *         <code>null</code>).
     */
    public static Rectangle getRelativeWidgetBounds(final Widget widget, 
            final Control relativeTo) {
        
        Rectangle absoluteBounds = getWidgetBounds(widget);
        if (absoluteBounds == null || relativeTo == null) {
            return absoluteBounds;
        }
        
        return relativeTo.getDisplay().map(null, relativeTo, absoluteBounds);
    }

    /**
     * Look up the bounds of a component. These bounds are in 
     * absolute/screen/display coordinates, <b>NOT</b> relative/local/component
     * coordinates.
     * @param widget the widget to get the bounds for. If this value is
     *               <code>null</code>, <code>null</code> will be returned.
     * @return the bounds of the widget in display coordinates, or 
     *         <code>null</code> if the bounds cannot be determined 
     *         (for example, if the given widget is null).
     */
    public static Rectangle getWidgetBounds(final Widget widget) {

        Rectangle bounds = null;
        if (widget == null) {
            log.debug("Trying to find bounds for null Widget. Null bounds returned"); //$NON-NLS-1$
            return null;
        }
        if (widget instanceof Shell) {
            bounds = getBounds((Shell)widget);
        } else if (widget instanceof Control) {
            bounds = getBounds((Control)widget);
        } else if (widget instanceof Caret) {
            bounds = getBounds((Caret)widget);
        } else if (widget instanceof Menu) {
            bounds = getBounds((Menu)widget);
        } else if (widget instanceof ScrollBar) {
            bounds = getBounds((ScrollBar)widget);
        } else if (widget instanceof CoolItem) {
            bounds = getBounds((CoolItem)widget);
        } else if (widget instanceof MenuItem) {
            bounds = getBounds((MenuItem)widget);
        } else if (widget instanceof TabItem) {
            bounds = getBounds((TabItem)widget);
        } else if (widget instanceof CTabItem) {
            bounds = getBounds((CTabItem)widget);
        } else if (widget instanceof TableColumn) {
            bounds = getBounds((TableColumn)widget);
        } else if (widget instanceof TableItem) {
            bounds = getBounds((TableItem)widget);
        } else if (widget instanceof ToolItem) {
            bounds = getBounds((ToolItem)widget);
        } else if (widget instanceof TreeItem) {
            bounds = getBounds((TreeItem)widget);
        } else {
            if (log.isDebugEnabled()) {
                log.debug("Tried to find bounds for unknown component type: "  //$NON-NLS-1$
                        + widget.getClass().getName() 
                        + ". Returning null bounds."); //$NON-NLS-1$
            }
        }
        
        return bounds;
    }

    /**
     * This method runs in the GUI-Thread.
     * @param widget the widget to get the children for
     * @param recurse true or false
     * @return a linked list with all children of the given widget
     */
    public static Widget[] getWidgetChildren(final Widget widget,
        final boolean recurse) { 
        
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        Widget[] widgets = evThreadQueuer.invokeAndWait("getWidgetChildren", //$NON-NLS-1$
                new IRunnable<Widget[]>() {
                    public Widget[] run() throws StepExecutionException {
                        if (widget == null || widget.isDisposed()) {
                            return new Widget[] {};
                        }

                        return getWidgetChildrenImpl(widget, recurse);
                    }
                });
        return widgets;
    }

    /**
     * @param widget the widget to get the children for
     * @param recurse true or false
     * @return a linked list with all children of the given widget
     */
    private static Widget[] getWidgetChildrenImpl(final Widget widget, 
        final boolean recurse) {
        List<Widget> objT = new LinkedList<Widget>();
        if (widget == null || widget.isDisposed()) {
            return new Widget[0];
        }
        if (widget instanceof Control && ((Control)widget).getMenu() != null) {
            objT.add(((Control)widget).getMenu());
        }
        if (widget instanceof Scrollable) {
            Scrollable scrollable = (Scrollable) widget;
            if (scrollable.getVerticalBar() != null) {
                objT.add(scrollable.getVerticalBar());
            }
            if (scrollable.getHorizontalBar() != null) {
                objT.add(scrollable.getHorizontalBar());
            }
        }
        if (widget instanceof Decorations 
            && ((Decorations)widget).getMenuBar() != null) {

            objT.add(((Decorations)widget).getMenuBar());
        }
        if (widget instanceof Composite) {
            Widget[] widgets = ((Composite)widget).getChildren();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        if (widget instanceof CoolBar) {
            Widget[] widgets = ((CoolBar)widget).getItems();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        if (widget instanceof TabFolder) {
            Widget[] widgets = ((TabFolder)widget).getItems();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        if (widget instanceof Table) {
            Table table = (Table) widget;
            Widget[] widgets = table.getItems();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
            widgets = table.getColumns();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        if (widget instanceof ToolBar) {
            Widget[] widgets = ((ToolBar)widget).getItems();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        if (widget instanceof Tree) {
            Widget[] widgets = ((Tree)widget).getItems();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        addNonControlChildren(widget, objT);
        if (recurse && objT.size() > 0) {
            List<Widget> extendedFamily = new LinkedList<Widget>();
            for (int i = 0; i < objT.size(); i++) {
                Widget w = objT.get(i);
                extendedFamily.addAll(Arrays.asList(getWidgetChildrenImpl(
                    w, recurse)));  
            }
            objT.addAll(extendedFamily);
        }
        return (objT.toArray(new Widget[objT.size()]));
    }

    /**
     * @param widget the non-Control-widget to get the children for
     * @param objT linkedList to put the children in
     */
    private static void addNonControlChildren(final Widget widget, 
        List<Widget> objT) {
        if (widget instanceof TreeItem) {
            Widget[] widgets = ((TreeItem)widget).getItems();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        if (widget instanceof Menu) {
            Widget[] widgets = ((Menu)widget).getItems();
            if (widgets.length != 0) {
                objT.addAll(Arrays.asList(widgets));
            }
        }
        if (widget instanceof MenuItem) {
            Widget childMenu = ((MenuItem)widget).getMenu();
            if (childMenu != null) {
                objT.add(childMenu);
            }
        }
    }

    /**
     * @param widget the widget to get the shell for
     * @return the component's owning shell. There will <b>always</b> one of these.
     */
    public static Shell getWidgetShell(Widget widget) {
        Widget parent = widget;
        while (!(parent instanceof Shell) && getWidgetParent(parent) != null) {
            parent = getWidgetParent(parent);
        }
        return (Shell)parent;
    }

    /**
     * @param control the control to get bounds for
     * @return the bounds for the given control
     */
    public static Rectangle getBounds(Control control) {
        Rectangle bounds = control.getDisplay().map(
                    control.getParent(), null, control.getBounds());
        bounds = getActiveArea(control, bounds);
        return bounds;
    }

    /**
     * Gets the active area of the given Control with the given bounds.
     * @param control the Control
     * @param bounds the bounds of the given Control.
     * @return a Rectangle, the border of the active area.
     */
    private static Rectangle getActiveArea(Control control, Rectangle bounds) {
        final int borderWidth = control.getBorderWidth();
        if (borderWidth > 0) {
            bounds.x += borderWidth;
            bounds.y += borderWidth;
            final int dimAdjustment = (2 * borderWidth);
            bounds.height -= dimAdjustment;
            bounds.width -= dimAdjustment;
        }
        return bounds;
    }

    /**
     * @param caret the caret to get bounds for
     * @return the bounds for the given caret
     */
    public static Rectangle getBounds(Caret caret) {
        return caret.getDisplay().map(
            caret.getParent(), null, caret.getBounds());
    }

    /**
     * 
     * @param shell the shell to get bounds for
     * @return the bounds for the given shell
     */
    public static Rectangle getBounds(Shell shell) {
        final Rectangle clientArea = shell.getClientArea();
        final Rectangle bounds = shell.getBounds();
        
        final int titleBarHeight = bounds.height - clientArea.height;
        final int widthDiff = bounds.width - clientArea.width;
        
        // set upper left edge:
        clientArea.x = bounds.x + (widthDiff / 2);
        final int borderWidth = shell.getBorderWidth();
        clientArea.y = bounds.y + titleBarHeight - borderWidth * 2;
        
        // adjust height:
        clientArea.height -= borderWidth;
        return clientArea;
    }
    
    /*
     * BEGIN CODE ADAPTED FROM http://eclip.se/38436#c153
     */

    /*************************** COMMON *****************************/  
    /**
     * Tries to use reflection to invoke getBounds() on the given Object and 
     * returns the result.
     * @param object    The Object for which to find the bounds.
     * @return  the bounds, or (0, 0, 0, 0) if the bounds cannot be retrieved.
     */
    private static Rectangle getBounds(Object object) {
        Rectangle result = new Rectangle(0, 0, 0, 0);
        String methodName = "getBounds"; //$NON-NLS-1$
        try {
            Method method = object.getClass().getDeclaredMethod(methodName, 
                null);
            method.setAccessible(true);
            result = (Rectangle) method.invoke(object, null);
        } catch (Exception e) {
            return handleBoundsError(e);
        }
        return result;
    }

    /**
     * Workaround for package scope of MenuItem.getBounds(). Returns a rectangle
     * describing the receiver's size and location relative to the display.
     * @param menuItem the menu item to get bounds for
     * @return the receiver's bounding rectangle
     * 
     * @since 3.1
     */
    public static Rectangle getBounds(MenuItem menuItem) {
        Rectangle itemRect = getBounds((Object)menuItem);
        Rectangle menuRect = getBounds(menuItem.getParent());
        if ((menuItem.getParent().getStyle() & SWT.RIGHT_TO_LEFT) != 0) {
            itemRect.x = menuRect.x + menuRect.width 
                - itemRect.width - itemRect.x;
        } else {
            itemRect.x += menuRect.x;
        }
        itemRect.y += menuRect.y;
        return itemRect;
    }

    /**
     * Workaround for lack of Menu.getBounds(). 
     * 
     * Returns a rectangle describing the receiver's size and location relative
     * to the display.
     * <p>
     * Note that the bounds of a menu or menu item are undefined when the menu
     * is not visible. This is because most platforms compute the bounds of a
     * menu dynamically just before it is displayed.
     * </p>
     * @param menu the menu to get bounds for
     * @return the receiver's bounding rectangle
     */
    public static Rectangle getBounds(Menu menu) {
        return getBounds((Object)menu);
    }

    /**
     * Workaround for lack of ScrollBar.getBounds(). 
     * @param scrollBar the scrollbar to get bounds for.
     * @return the rectagle bounds of the scrollbar, in display coordinates
     */
    public static Rectangle getBounds(ScrollBar scrollBar) {
        Point size = scrollBar.getSize();
        Rectangle bounds = scrollBar.getParent().getBounds();
        if ((scrollBar.getParent().getStyle() & SWT.RIGHT_TO_LEFT) != 0) {
            bounds.x = 0;
            bounds.width = size.x;
        } else {
            bounds.x = bounds.width - size.x;
        }
        bounds.y = bounds.height - size.y;
        // FIXME zeb: coordinate system may change when the API is added to SWT
        return scrollBar.getDisplay().map(
            scrollBar.getParent(), null, bounds);
    }

    /*************************** WIN32 *****************************/
    /**
     * Sends a message to the win32 window manager. This method is only for
     * win32.
     * 
     * @param hWnd      The handle of the component to send the message to.
     * @param msg       The message to send.
     * @param wParam    wParam
     * @param lParam    lParam
     * 
     * @return  the response to the message.
     */
    static int sendMessage(int hWnd, int msg, int wParam, int [] lParam) 
        throws Exception {
        
        int result = 0;
        String methodName = "SendMessage"; //$NON-NLS-1$
        String className = "org.eclipse.swt.internal.win32.OS"; //$NON-NLS-1$

        Class clazz = Class.forName(className);
        Class [] params = new Class [] {
            Integer.TYPE,
            Integer.TYPE,
            Integer.TYPE,
            lParam.getClass(),
        };
        Method method = clazz.getMethod(methodName, params);
        Object [] args = new Object [] {
            new Integer(hWnd),
            new Integer(msg),
            new Integer(wParam),
            lParam,
        };
        result = ((Integer) method.invoke(clazz, args)).intValue();

        return result;
    }

    /**
     * Win32-specific workaround for getting the bounds of a TabItem.
     * 
     * @param tabItem   The TabItem to find the bounds for.
     * @return  the bounding rectangle
     */
    static Rectangle win32getBounds(TabItem tabItem) {
        TabFolder parent = tabItem.getParent();
        int index = parent.indexOf(tabItem);
        if (index == -1) {
            return new Rectangle(0, 0, 0, 0);
        }
        int [] rect = new int [4];
        try {
            sendMessage((int) parent.handle, /*TCM_GETITEMRECT*/ 0x130a,
                    index, rect);
            int width = rect [2] - rect[0];
            int height = rect [3] - rect [1];
            Rectangle bounds = new Rectangle(rect [0], rect [1], width, height);
            return tabItem.getDisplay().map(tabItem.getParent(), null, bounds);
        } catch (Exception e) {
            return handleBoundsError(e);
        }
    }

    /**
     * Win32-specific workaround for getting the bounds of a TableColumn.
     * 
     * @param tableColumn   The TableColumn to find the bounds for.
     * @return  the bounding rectangle
     */
    static Rectangle win32getBounds(TableColumn tableColumn) {
        Table parent = tableColumn.getParent();
        int index = parent.indexOf(tableColumn);
        if (index == -1) {
            return new Rectangle(0, 0, 0, 0); 
        }
        try {
            int hwndHeader = sendMessage((int)parent.handle, 
                /*LVM_GETHEADER*/ 0x101f, 
                0, new int [0]);
            int [] rect = new int [4];
            sendMessage(hwndHeader, 
                /*HDM_GETITEMRECT*/ 0x1200 + 7, index, rect);
            int width = rect [2] - rect[0];
            int height = rect [3] - rect [1];
            Rectangle bounds = new Rectangle(rect [0], rect [1], width, height);
            // FIXME zeb: coordinate system may change when the API is added to SWT
            int hwndTable = (int)parent.handle;
            try {
                parent.handle = hwndHeader;
                return tableColumn.getDisplay().map(parent, null, bounds);
            } finally {
                parent.handle = hwndTable;
            }
        } catch (Exception e) {
            return handleBoundsError(e);
        }
    }

    /**
     * Win32-specific workaround for getting the bounds of a TreeColumn.
     * 
     * @param treeColumn   The TreeColumn to find the bounds for.
     * @return  the bounding rectangle
     */
    static Rectangle win32getBounds(TreeColumn treeColumn) {
        Tree parent = treeColumn.getParent();
        int index = parent.indexOf(treeColumn);
        if (index == -1) {
            return new Rectangle(0, 0, 0, 0); 
        }
        int hwndHeader = 0;
        try {
            Class<? extends Tree> clazz = parent.getClass();
            Field f = clazz.getDeclaredField("hwndHeader"); //$NON-NLS-1$
            f.setAccessible(true);
            hwndHeader = f.getInt(parent);  
            int [] rect = new int [4];
            
            sendMessage(hwndHeader, 
                /*HDM_GETITEMRECT*/ 0x1200 + 7, index, rect);
            int width = rect [2] - rect[0];
            int height = rect [3] - rect [1];
            Rectangle bounds = new Rectangle(rect [0], rect [1], width, height);
            bounds.y -= treeColumn.getParent().getHeaderHeight();
            return treeColumn.getDisplay().map(parent, null, bounds);
        } catch (Exception e) {
            return handleBoundsError(e);
        }
    }

    /*************************** GTK *****************************/

    /**
     * GTK-specific method for getting the bounds of a component. This method
     * is only for GTK.
     * @param handle    The handle of the component to get the bounds for.
     * @param bounds    The bounds will be written to this rectangle since there
     *                  is no return value.
     */
    static void gtkgetBounds(int handle, Rectangle bounds) 
        throws Exception {  
        
        Class clazz = Class.forName("org.eclipse.swt.internal.gtk.OS"); //$NON-NLS-1$
        Class [] params = new Class [] {Integer.TYPE};
        Object [] args = new Object [] {new Integer(handle)};
        try {
            Method method = clazz.getMethod("gtkWIDGET_X", params); //$NON-NLS-1$
            bounds.x = ((Integer) method.invoke(clazz, args)).intValue();
            method = clazz.getMethod("gtkWIDGET_Y", params); //$NON-NLS-1$
            bounds.y = ((Integer) method.invoke(clazz, args)).intValue();
            method = clazz.getMethod("gtkWIDGET_WIDTH", params); //$NON-NLS-1$
            bounds.width = ((Integer) method.invoke(clazz, args)).intValue();
            method = clazz.getMethod("gtkWIDGET_HEIGHT", params); //$NON-NLS-1$
            bounds.height = ((Integer) method.invoke(clazz, args)).intValue();
        } catch (NoSuchMethodException nsme) {
            Method method = clazz.getMethod("GTK_WIDGET_X", params); //$NON-NLS-1$
            bounds.x = ((Integer) method.invoke(clazz, args)).intValue();
            method = clazz.getMethod("GTK_WIDGET_Y", params); //$NON-NLS-1$
            bounds.y = ((Integer) method.invoke(clazz, args)).intValue();
            method = clazz.getMethod("GTK_WIDGET_WIDTH", params); //$NON-NLS-1$
            bounds.width = ((Integer) method.invoke(clazz, args)).intValue();
            method = clazz.getMethod("GTK_WIDGET_HEIGHT", params); //$NON-NLS-1$
            bounds.height = ((Integer) method.invoke(clazz, args)).intValue();

        }
    }

    /**
     * GTK-specific workaround for getting the bounds of a TableColumn.
     * 
     * @param tableColumn   The TableColumn to find the bounds for.
     * @return  the bounding rectangle
     */
    static Rectangle gtkgetBounds(TableColumn tableColumn) {
        Rectangle bounds = new Rectangle(0, 0, 0, 0);
        try {
            Class<? extends TableColumn> clazz = tableColumn.getClass();
            Field f = clazz.getDeclaredField("buttonHandle"); //$NON-NLS-1$
            f.setAccessible(true);
            int handle = f.getInt(tableColumn);         
            gtkgetBounds(handle, bounds);
            bounds.y -= tableColumn.getParent().getHeaderHeight();
            return tableColumn.getDisplay().map(
                tableColumn.getParent(), null, bounds);
        } catch (Exception e) {
            return handleBoundsError(e);
        }
    }

    /**
     * GTK-specific workaround for getting the bounds of a TreeColumn.
     * 
     * @param treeColumn   The TreeColumn to find the bounds for.
     * @return  the bounding rectangle
     */
    static Rectangle gtkgetBounds(TreeColumn treeColumn) {
        Rectangle bounds = new Rectangle(0, 0, 0, 0);
        try {
            Class<? extends TreeColumn> clazz = treeColumn.getClass();
            Field f = clazz.getDeclaredField("buttonHandle"); //$NON-NLS-1$
            f.setAccessible(true);
            int handle = f.getInt(treeColumn);          
            gtkgetBounds(handle, bounds);
            bounds.y -= treeColumn.getParent().getHeaderHeight();
            return treeColumn.getDisplay().map(
                treeColumn.getParent(), null, bounds);
        } catch (Exception e) {
            return handleBoundsError(e);
        }
    }

    /**
     * GTK-specific workaround for getting the bounds of a TabItem.
     * 
     * @param tabItem   The TabItem to find the bounds for.
     * @return  the bounding rectangle
     */
    static Rectangle gtkgetBounds(TabItem tabItem) {
        Rectangle bounds = new Rectangle(0, 0, 0, 0);  
        try {
            Class clazz = Class.forName("org.eclipse.swt.widgets.Widget"); //$NON-NLS-1$
            Field f = clazz.getDeclaredField("handle"); //$NON-NLS-1$
            f.setAccessible(true);
            int handle = f.getInt(tabItem);
            gtkgetBounds(handle, bounds);
            return tabItem.getDisplay().map(tabItem.getParent(), null, bounds);
        } catch (Exception e) {
            return handleBoundsError(e);
        }
    }

    /**
     * Workaround for lack of TabItem.getBounds().
     * 
     * Returns a rectangle describing the TabItem's size and location in display
     * coordinates.
     * @param tabItem the tab item to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(TabItem tabItem) {

        // first try to use getBounds() directly. this should work in most cases, as it is implemented in SWT since 3.4.
        try {
            Method getBoundsMethod = tabItem.getClass().getMethod(
                    METHOD_NAME_GET_BOUNDS, null);
            Object result = getBoundsMethod.invoke(tabItem, null);
            if (result instanceof Rectangle) {
                Rectangle bounds = (Rectangle)result;
                TabFolder parent = tabItem.getParent();
                
                // The Cocoa implementation of SWT seems to deliver bounds with
                // a blatantly incorrect y-value. The fix is to adjust the 
                // y-value based on the style of the parent TabFolder.
                bounds.y = (parent.getStyle() & SWT.BOTTOM) == SWT.BOTTOM 
                    ? parent.getBounds().y - bounds.height : 0;
                return tabItem.getDisplay().map(
                        tabItem.getParent(), null, bounds);
            }
            String className = result != null 
                ? result.getClass().getName() : "null"; //$NON-NLS-1$
            log.error("Expected getBounds() to return an object of type 'Rectangle', but received object of type '"  //$NON-NLS-1$
                    + className + "'."); //$NON-NLS-1$
            // fall through
        } catch (Exception e) {
            log.warn("Could not invoke getBounds() on TabItem. This is expected when using an SWT version lower than 3.4", e); //$NON-NLS-1$
        }
        
        if (SWT.getPlatform().equals(WIN32)) {
            return win32getBounds(tabItem);
        }
        if (SWT.getPlatform().equals(GTK)) {
            return gtkgetBounds(tabItem);
        }
        return null;
    }

    /**
     * Returns a rectangle describing the TableColumn's size and location in 
     * display coordinates.
     * @param tableColumn the TableColumn to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(TableColumn tableColumn) {
        if (SWT.getPlatform().equals(WIN32)) {
            return win32getBounds(tableColumn);
        }
        if (SWT.getPlatform().equals(GTK)) {
            return gtkgetBounds(tableColumn);
        }
        return null;
    }

    /**
     * Returns a rectangle describing the TreeColumn's size and location in 
     * display coordinates.
     * @param treeColumn the TreeColumn to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(TreeColumn treeColumn) {
        if (SWT.getPlatform().equals(WIN32)) {
            return win32getBounds(treeColumn);
        }
        if (SWT.getPlatform().equals(GTK)) {
            return gtkgetBounds(treeColumn);
        }
        return null;
    }

    /**
     * Returns a rectangle describing the TableItem's size and location in 
     * display coordinates.
     * @param item the TableItem to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(TableItem item) {
        // FIXME zeb the method TableItem.getBounds() was introduced in 
        //           SWT 3.2. We currently compile the AUT Server with
        //           SWT 3.1. When we abandon SWT 3.1, we should probably
        //           use getBounds() instead of getBounds(int), as it is 
        //           more accurate.
        Rectangle bounds = item.getDisplay().map(
                item.getParent(), null, item.getBounds(0)); 
        
        return bounds;
    }

    /**
     * Returns a rectangle describing the TreeItem's size and location in 
     * display coordinates.
     * @param item the TreeItem to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(TreeItem item) {
        Rectangle bounds = item.getBounds();
        
        // Works around an issue where
        // the width of the item is computed as "1" by SWT when the 
        // columns are actually the only elements that have width.
        if (item.getParent().getColumnCount() > 0 && bounds.width <= 1) {
            bounds.width = item.getParent().getBounds().width;
        }

        return item.getDisplay().map(
            item.getParent(), null, bounds);
    }

    /**
     * Returns a rectangle describing the TreeItem's size and location in 
     * display coordinates for the given column.
     * @param item the TreeItem to get bounds for
     * @param column the column for the TreeItem
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(TreeItem item, int column) {
        Rectangle bounds = item.getBounds(column);
        
        return item.getDisplay().map(
            item.getParent(), null, bounds);
    }

    /**
     * Returns a rectangle describing the TreeItem's size and location,
     * relative to its parent, for the given column.
     * @param item the TreeItem to get bounds for
     * @param column the column for the TreeItem
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getRelativeBounds(TreeItem item, int column) {
        Rectangle absoluteBounds = getBounds(item, column);
        return item.getDisplay().map(
                null, item.getParent(), absoluteBounds);


    }

    /**
     * Returns a rectangle describing the CTabItem's size and location in 
     * display coordinates.
     * @param item the CTabItem to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(CTabItem item) {
        return item.getDisplay().map(
            item.getParent(), null, item.getBounds());
    }

    /**
     * Returns a rectangle describing the ToolItem's size and location in 
     * display coordinates.
     * @param item the ToolItem to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(ToolItem item) {
        return item.getDisplay().map(
            item.getParent(), null, item.getBounds());
    }

    /**
     * Returns a rectangle describing the CoolItem size and location in 
     * display coordinates.
     * @param item the CoolItem to get bounds for
     * @return the bounding rectangle
     * 
     */
    public static Rectangle getBounds(CoolItem item) {
        return item.getDisplay().map(
            item.getParent(), null, item.getBounds());
    }

    /*
     * END CODE ADAPTED FROM http://eclip.se/38436#c153
     */

    /**
     * Handles exceptions that occur while attempting to find the bounds of a 
     * component. Logs various information about the attempt.
     * 
     * @param e     The exception thrown during the getBounds() attempt.
     * @return  the default bounds to use (0, 0, 0, 0). 
     */
    private static Rectangle handleBoundsError(Exception e) {
        
        Rectangle defaultResult = new Rectangle(0, 0, 0, 0); 
        String message = "getBounds() failed. Returning default bounds result: " //$NON-NLS-1$
                + defaultResult 
                + "."; //$NON-NLS-1$
                
        if (EnvironmentUtils.isMacOS()) {
            log.debug(message, e);
        } else {
            log.error(message, e);            
        }

        return defaultResult;
    }
    
    /**
     * Calls the toString() method on the given Widget in the GUI-Thread.
     * @param widget a Widget.
     * @return the toString-representation of the given Widget.
     */
    public static String toString(final Widget widget) {
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        return evThreadQueuer.invokeAndWait("toString", //$NON-NLS-1$
                new IRunnable<String>() {
                    public String run() throws StepExecutionException {
                        return String.valueOf(widget);
                    }
                });
    }

    /**
     * 
     * @param awtPoint The {@link java.awt.Point} to convert.
     * @return a new {@link org.eclipse.swt.graphics.Point} with the same 
     *         coordinates as <code>awtPoint</code>.
     */
    public static Point convertToSwtPoint(java.awt.Point awtPoint) {
        return new Point(awtPoint.x, awtPoint.y);
    }
    
    /**
     * Determines whether the mouse cursor is currently inside the bounds of the
     * given widget
     * 
     * @param widget the widget to check the mouse position for
     * @return true if mouse cursor is inside the bounds, false otherwise.
     */
    public static boolean isMouseCursorInWidget(final Widget widget) {
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        return evThreadQueuer.invokeAndWait("isInComponent", //$NON-NLS-1$
                new IRunnable<Boolean>() {
                    public Boolean run() throws StepExecutionException {
                        return SwtUtils.getWidgetBounds(widget).contains(
                                widget.getDisplay().getCursorLocation());
                    }
                });
    }
    
    /**
     * Determines whether one widget is completely contained within another.
     * 
     * @param boundsWidget The "owning" widget.
     * @param eventWidget The "child" widget.
     * @return <code>true</code> if <code>eventWidget</code> is completely 
     *         contained within <code>boundsWidget</code>. This method always 
     *         returns <code>false</code> if either or both of the given widgets
     *         have been disposed.
     */
    public static boolean isInBounds(final Widget boundsWidget, 
        final Widget eventWidget) {
        
        IEventThreadQueuer evThreadQueuer = new EventThreadQueuerSwtImpl();
        return evThreadQueuer.invokeAndWait("getWidgetChildren", //$NON-NLS-1$
                new IRunnable<Boolean>() { 
            
                public Boolean run() throws StepExecutionException {
                    if (boundsWidget == null || boundsWidget.isDisposed()
                        || eventWidget == null || eventWidget.isDisposed()) {
                        return Boolean.FALSE;
                    }
                    Rectangle widgetBounds = getWidgetBounds(boundsWidget);
                    boolean isInBounds =  widgetBounds.equals(
                        widgetBounds.union(getWidgetBounds(eventWidget)));
                    return isInBounds;
                }
            });
    }
    
    /**
     * Checks whether the given rectangle contains the given point. This 
     * differs from {@link Rectangle#contains(Point)} in that a point that is 
     * exactly on the border of the rectangle counts as being contained within 
     * the rectangle.
     * 
     * @param rect The rectangle against which to check.
     * @param point The point to check.
     * @return <code>true</code> if <code>point</code> does not lie outside of 
     *         <code>rect</code>. Otherwise <code>false</code>.
     */
    public static boolean containsInclusive(Rectangle rect, Point point) {
        Point treeLocUpperLeft = new Point(rect.x, rect.y);
        Point treeLocLowerRight = new Point(
            rect.width  + treeLocUpperLeft.x, 
            rect.height + treeLocUpperLeft.y); 
        final boolean x1 = point.x >= treeLocUpperLeft.x;
        final boolean x2 = point.x < treeLocLowerRight.x;
        final boolean y1 = point.y >= treeLocUpperLeft.y;
        final boolean y2 = point.y < treeLocLowerRight.y;
        return x1 && x2 && y1 && y2;
    }
    
    /**
     * method for removing mnemonics from string
     * @param text String
     * @return String without mnemonics
     */
    public static final String removeMnemonics(final String text) {
        if (text == null) {
            return null;
        }
        int index = text.indexOf('&');
        if (index == -1) {
            return text;
        }
        int len = text.length();
        StringBuffer sb = new StringBuffer(len);
        int lastIndex = 0;
        while (index != -1) {
            // ignore & at the end
            if (index == len - 1) {
                break;
            }
            // handle the && case
            if (text.charAt(index + 1) == '&') {
                ++index;
            }

            // DBCS languages use "(&X)" format
            if (index > 0 && text.charAt(index - 1) == '('
                    && text.length() >= index + 3
                    && text.charAt(index + 2) == ')') {
                sb.append(text.substring(lastIndex, index - 1));
                index += 3;
            } else {
                sb.append(text.substring(lastIndex, index));
                // skip the &
                ++index;
            }

            lastIndex = index;
            index = text.indexOf('&', index);
        }
        if (lastIndex < len) {
            sb.append(text.substring(lastIndex, len));
        }
        return sb.toString();
    }
    
    /**
     * gives default modifier of the current OS.
     * 
     * @return meta (command) for OSX, control for Windows/Linux etc
     */
    public static int getSystemDefaultModifier() {
        if (EnvironmentUtils.isMacOS()) {
            return KeyCodeConverter
                    .getKeyCode(ValueSets.Modifier.cmd.rcValue());
        }
        return KeyCodeConverter
                .getKeyCode(ValueSets.Modifier.control.rcValue());
    }
    
    /**
     * @return the second system modifier
     */
    public static int getSystemModifier2() {
        return KeyCodeConverter.getKeyCode(ValueSets.Modifier.shift.rcValue());
    }

    /**
     * @return the third system modifier
     */
    public static int getSystemModifier3() {
        return KeyCodeConverter.getKeyCode(ValueSets.Modifier.alt.rcValue());
    }

    /**
     * @return the fourth system modifier; only available on Mac OS X
     */
    public static int getSystemModifier4() {
        return KeyCodeConverter
                .getKeyCode(ValueSets.Modifier.control.rcValue());
    }
    
    /**
     * 
     * @param shell The shell to check. May be <code>null</code>.
     * @return <code>true</code> if the given shell appears to be a dropdown
     *         list (ex. from a CCombo). Returns <code>false</code> if the
     *         given shell is <code>null</code> or disposed, or if the shell
     *         does not meet the necessary criteria to be considered a dropdown
     *         list.
     */
    public static boolean isDropdownListShell(Shell shell) {
        return shell != null 
            && !shell.isDisposed()
            && StringUtils.isBlank(shell.getText()) 
            && (shell.getStyle() & SWT.ON_TOP) != 0
            && shell.getChildren() != null
            && shell.getChildren().length == 1
            && shell.getChildren()[0] instanceof org.eclipse.swt.widgets.List;
    }

    /**
     * Clicks somewhere and checks if an editor was present
     * @param toClick the component to click in
     * @param rect the rectangle where to click
     * @param robot the robot to click with
     * @return the Editor if there was any
     */
    public static Control getEditor(Widget toClick, Rectangle rect,
            IRobot<Rectangle> robot) {
        org.eclipse.swt.graphics.Rectangle swtRect = 
                new org.eclipse.swt.graphics.Rectangle(rect.x, rect.y, 
                        rect.width, rect.height);
        ClickOptions co = ClickOptions.create().
                setClickCount(1).setScrollToVisible(false);
        robot.click(toClick, swtRect, co);
        Control cont = getCursorControl();
        if (cont != null) {
            return cont;
        }
        robot.click(toClick, swtRect, co.setClickCount(2));
        return getCursorControl();
    }
}