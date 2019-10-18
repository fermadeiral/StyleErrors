/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.rc.javafx.tester.adapter;

import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Control;
import javafx.stage.Window;
import javafx.stage.WindowEvent;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;
import org.eclipse.jubula.rc.common.tester.adapter.interfaces.IWidgetComponent;
import org.eclipse.jubula.rc.common.util.KeyStrokeUtil;
import org.eclipse.jubula.rc.javafx.driver.EventThreadQueuerJavaFXImpl;
import org.eclipse.jubula.rc.javafx.tester.MenuTester;
import org.eclipse.jubula.rc.javafx.tester.util.NodeBounds;
import org.eclipse.jubula.rc.javafx.tester.util.Rounding;
import org.eclipse.jubula.rc.javafx.tester.util.compatibility.WindowsUtil;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.jubula.tools.internal.utils.TimeUtil;

/**
 * Implements the interface for widgets and supports basic methods which are
 * needed for nearly all JavaFX UI components.
 * 
 * @param <T>
 *            type of the Component
 * 
 * @author BREDEX GmbH
 * @created 30.10.2013
 */
public class JavaFXComponentAdapter<T extends Node> extends
        AbstractComponentAdapter<T> implements IWidgetComponent {

    /**
     * The Converter Map.
     */
    private static Map<String, Integer> converterTable = null;

    static {
        converterTable = new HashMap<String, Integer>();
        converterTable.put(ValueSets.Modifier.none.rcValue(), new Integer(-1));
        converterTable.put(ValueSets.Modifier.shift.rcValue(), new Integer(
                KeyEvent.VK_SHIFT));
        converterTable.put(ValueSets.Modifier.control.rcValue(), new Integer(
                KeyEvent.VK_CONTROL));
        converterTable.put(ValueSets.Modifier.alt.rcValue(), new Integer(
                KeyEvent.VK_ALT));
        converterTable.put(ValueSets.Modifier.meta.rcValue(), new Integer(
                KeyEvent.VK_META));
        converterTable.put(ValueSets.Modifier.cmd.rcValue(), new Integer(
                KeyEvent.VK_META));
        converterTable.put(ValueSets.Modifier.mod.rcValue(), new Integer(
                KeyEvent.VK_CONTROL));
    }

    /**
     * Used to store the component into the adapter.
     * 
     * @param objectToAdapt
     *            the object to adapt
     */
    public JavaFXComponentAdapter(T objectToAdapt) {
        super(objectToAdapt);
    }

    @Override
    public boolean isShowing() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isShowing", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        return getRealComponent().isVisible();
                    }
                });

        return result;
    }

    @Override
    public boolean isEnabled() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "isEnabled", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        // because the logic in JavaFX
                        // is switched the return value is inverted
                        return !(getRealComponent().isDisabled());
                    }
                });
        return result;
    }

    @Override
    public boolean hasFocus() {
        boolean result = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "hasFocus", new Callable<Boolean>() { //$NON-NLS-1$

                    @Override
                    public Boolean call() throws Exception {
                        return getRealComponent().isFocused();
                    }
                });
        return result;
    }

    @Override
    public String getPropteryValue(final String propertyname) {
        Object prop = EventThreadQueuerJavaFXImpl.invokeAndWait("getProperty", //$NON-NLS-1$
                new Callable<String>() {

                    @Override
                    public String call() throws Exception {
                        try {
                            return getRobot().getPropertyValue(
                                    getRealComponent(), propertyname);
                        } catch (RobotException e) {
                            throw new StepExecutionException(
                                    e.getMessage(),
                                    EventFactory
                                            .createActionError(TestErrorEvent.
                                                    PROPERTY_NOT_ACCESSABLE));
                        }
                    }
                });
        return String.valueOf(prop);
    }

    @Override
    public AbstractMenuTester showPopup(int xPos, String xUnits, int yPos,
            String yUnits, int button) throws StepExecutionException {
        Node n = getRealComponent();
        if (n instanceof Control && ((Control) n).getContextMenu() != null) {
            return openPropertyContextMenu(xPos, xUnits, yPos, yUnits, button,
                    (Control)n);

        }
        return openContextMenu(xPos, xUnits, yPos, yUnits, button, n);
    }

    /**
     * Opens the context menu of the given node and finds it with
     * Window.impl_getWindows(). Use this method for components which are not a
     * subclass of Control and therefore don't have the context menu property
     * 
     * @param xPos
     *            what x position
     * @param xUnits
     *            should x position be pixel or percent values
     * @param yPos
     *            what y position
     * @param yUnits
     *            should y position be pixel or percent values
     * @param button
     *            MouseButton
     * @param n
     *            the Node
     * @return a MenuTester instance which references the context menu
     */
    protected AbstractMenuTester openContextMenu(int xPos, String xUnits,
            int yPos, String yUnits, int button, Node n) {
        boolean isAbsoluteUnitsX = ValueSets.Unit.pixel.rcValue()
                .equalsIgnoreCase(xUnits);
        boolean isAbsoluteUnitsY = ValueSets.Unit.pixel.rcValue()
                .equalsIgnoreCase(yUnits);
        getRobot().click(
                n,
                null,
                ClickOptions.create().setClickCount(1)
                        .setMouseButton(button), xPos, isAbsoluteUnitsX,
                yPos, isAbsoluteUnitsY);
        return EventThreadQueuerJavaFXImpl.invokeAndWait(
                "showPopup", new Callable<MenuTester>() { //$NON-NLS-1$

                    @Override
                    public MenuTester call() throws Exception {
                        MenuTester menuTester = null;
                        Iterator<Window> iter = WindowsUtil.getWindowIterator();
                        
                        ArrayList<ContextMenu> result = new ArrayList<>();
                        
                        long timeout = TimeoutConstants.
                                SERVER_TIMEOUT_WAIT_FOR_POPUP;
                        long done = System.currentTimeMillis() + timeout;
                        long now;
                        do {
                            if (!iter.hasNext()) {
                                iter = WindowsUtil.getWindowIterator();
                            }
                            Window w = iter.next();
                            if (w instanceof ContextMenu 
                                    && !result.contains(w)) {
                                result.add((ContextMenu) w);
                            }
                            now = System.currentTimeMillis();
                            timeout = done - now;
                        } while (timeout > 0 
                                && !(!iter.hasNext() && result.size() > 0));
                        
                        if (result.size() == 1) {
                            ContextMenu cm = result.get(0);
                            menuTester = new MenuTester();
                            menuTester.setComponent(cm);
                        } else if (result.size() == 0) {
                            throw new StepExecutionException("No Context Menu was found", //$NON-NLS-1$
                                    EventFactory
                                            .createActionError(TestErrorEvent.
                                                    POPUP_NOT_FOUND));
                        } else if (result.size() > 1) {
                            throw new StepExecutionException("Multiple Context Menus were found", //$NON-NLS-1$
                                EventFactory.createActionError(TestErrorEvent.
                                    UNSUPPORTED_OPERATION_IN_TOOLKIT_ERROR));
                        }
                        return menuTester;
                    }
                });
    }

    /**
     * Opens the context menu of the given control, via the context menu
     * property.
     * 
     * @param xPos
     *            what x position 
     * @param xUnits should x position be pixel or percent
     *            values
     * @param yPos
     *            what y position 
     * @param yUnits should y position be pixel or percent
     *            values
     * @param button
     *            MouseButton
     * @param comp
     *            the control
     * @return a MenuTester instance which references the context menu
     */
    private AbstractMenuTester openPropertyContextMenu(int xPos, String xUnits,
            int yPos, String yUnits, int button, Control comp) {
        final EventLock event = new EventLock();
        ContextMenu cotxMenu = comp.getContextMenu();
        EventHandler<WindowEvent> filter = new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent e) {
                synchronized (event) {
                    event.notifyAll();
                }
            }
        };
        cotxMenu.addEventFilter(WindowEvent.WINDOW_SHOWN, filter);
        // RobotTiming.sleepPreShowPopupDelay();
        boolean isAbsoluteUnitsX = ValueSets.Unit.pixel.rcValue()
                .equalsIgnoreCase(xUnits);
        boolean isAbsoluteUnitsY = ValueSets.Unit.pixel.rcValue()
                .equalsIgnoreCase(yUnits);
        getRobot().click(
                comp,
                null,
                ClickOptions.create().setClickCount(1)
                        .setMouseButton(button), xPos, isAbsoluteUnitsX,
                yPos, isAbsoluteUnitsY);

        if (!comp.getContextMenu().isShowing()) {
            try {
                synchronized (event) {
                    event.wait(TimeoutConstants.
                            SERVER_TIMEOUT_WAIT_FOR_POPUP);
                }
            } catch (InterruptedException e) {
                // ignore
            } finally {
                cotxMenu.removeEventFilter(WindowEvent.
                        WINDOW_SHOWN, filter);
            }

        }
        if (comp.getContextMenu().isShowing()) {
            MenuTester tester = new MenuTester();
            tester.setComponent(cotxMenu);
            return tester;
        }
        throw new StepExecutionException("Popup could not be opened", //$NON-NLS-1$
                EventFactory
                        .createActionError(TestErrorEvent.POPUP_NOT_FOUND));
    }

    @Override
    public AbstractMenuTester showPopup(int button) {
        Point currentMousePosition = getRobot().getCurrentMousePosition();
        Point2D mousePos = new Point2D(currentMousePosition.x,
                currentMousePosition.y);
        boolean widgetContainsCurrentPos = 
                EventThreadQueuerJavaFXImpl.invokeAndWait("showPopup", //$NON-NLS-1$
                        new Callable<Boolean>() {
                            @Override
                            public Boolean call() throws Exception {
                                return NodeBounds.checkIfContains(mousePos,
                                        getRealComponent());
                            }
                        });
        if (widgetContainsCurrentPos) {
            Point2D local = EventThreadQueuerJavaFXImpl.invokeAndWait(
                "showPopup", new Callable<Point2D>() { //$NON-NLS-1$
                    @Override
                    public Point2D call() throws Exception {
                        return getRealComponent().screenToLocal(mousePos);
                    }
                });
            return showPopup(Rounding.round(local.getX()),
                    ValueSets.Unit.pixel.rcValue(),
                    Rounding.round(local.getY()),
                    ValueSets.Unit.pixel.rcValue(), button);
        }
        return showPopup(50, ValueSets.Unit.percent.rcValue(), 50,
                ValueSets.Unit.percent.rcValue(), button);
    }

    @Override
    public void showToolTip(String text, int textSize, int timePerWord,
            int windowWidth) {
        StepExecutionException.throwUnsupportedAction();
    }

    @Override
    public void rcDrag(int mouseButton, String modifier, int xPos,
            String xUnits, int yPos, String yUnits) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        final IRobot robot = getRobot();
        clickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
        pressOrReleaseModifiers(modifier, true);
        robot.mousePress(null, null, mouseButton);
    }

    @Override
    public void rcDrop(int xPos, String xUnits, int yPos, String yUnits,
            int delayBeforeDrop) {
        final DragAndDropHelper dndHelper = DragAndDropHelper.getInstance();
        final String modifier = dndHelper.getModifier();
        final int mouseButton = dndHelper.getMouseButton();
        try {
            clickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
            TimeUtil.delay(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, mouseButton);
            pressOrReleaseModifiers(modifier, false);
        }
    }

    /**
     * Presses or releases the given modifier.
     * 
     * @param modifier
     *            the modifier.
     * @param press
     *            if true, the modifier will be pressed. if false, the modifier
     *            will be released.
     */
    private void pressOrReleaseModifiers(String modifier, boolean press) {
        final IRobot robot = getRobot();
        final StringTokenizer modTok = new StringTokenizer(
                KeyStrokeUtil.getModifierString(modifier), " "); //$NON-NLS-1$
        while (modTok.hasMoreTokens()) {
            final String mod = modTok.nextToken();
            final int keyCode = getKeyCode(mod);
            if (press) {
                robot.keyPress(null, keyCode);
            } else {
                robot.keyRelease(null, keyCode);
            }
        }
    }

    /**
     * clicks into the component.
     * 
     * @param count
     *            amount of clicks
     * @param button
     *            what mouse button should be used
     * @param xPos
     *            what x position
     * @param xUnits
     *            should x position be pixel or percent values
     * @param yPos
     *            what y position
     * @param yUnits
     *            should y position be pixel or percent values
     * @throws StepExecutionException
     *             error
     */
    private void clickDirect(int count, int button, int xPos, String xUnits,
            int yPos, String yUnits) throws StepExecutionException {

        getRobot().click(
                getRealComponent(),
                null,
                ClickOptions.create().setClickCount(count)
                        .setMouseButton(button), xPos,
                xUnits.equalsIgnoreCase(ValueSets.Unit.pixel.rcValue()),
                yPos,
                yUnits.equalsIgnoreCase(ValueSets.Unit.pixel.rcValue()));
    }

    @Override
    public int getKeyCode(String key) {
        if (key == null) {
            throw new RobotException("Key is null!", //$NON-NLS-1$
                    EventFactory.createConfigErrorEvent());
        }
        final Integer keyCode = converterTable.get(key.toLowerCase());
        if (keyCode == null) {
            throw new RobotException("No KeyCode found for key '" + key + "'", //$NON-NLS-1$//$NON-NLS-2$
                    EventFactory.createConfigErrorEvent());
        }
        return keyCode.intValue();
    }

    @Override
    public ReadOnlyObjectProperty<Window> getWindow() {
        return getRealComponent().getScene().windowProperty();
    }
}
