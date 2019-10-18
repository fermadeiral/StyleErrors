/*******************************************************************************
 * Copyright (c) 2013 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation 
 *******************************************************************************/
package org.eclipse.jubula.rc.swt.tester.adapter;

import org.eclipse.jubula.rc.common.driver.ClickOptions;
import org.eclipse.jubula.rc.common.driver.DragAndDropHelper;
import org.eclipse.jubula.rc.common.driver.IEventThreadQueuer;
import org.eclipse.jubula.rc.common.driver.IRobot;
import org.eclipse.jubula.rc.common.driver.IRunnable;
import org.eclipse.jubula.rc.common.driver.RobotTiming;
import org.eclipse.jubula.rc.common.exception.StepExecutionException;
import org.eclipse.jubula.rc.common.listener.EventLock;
import org.eclipse.jubula.rc.common.tester.AbstractMenuTester;
import org.eclipse.jubula.rc.common.tester.WidgetTester;
import org.eclipse.jubula.rc.swt.driver.EventThreadQueuerSwtImpl;
import org.eclipse.jubula.rc.swt.tester.MenuTester;
import org.eclipse.jubula.rc.swt.tester.adapter.ControlAdapter.PopupShownCondition;
import org.eclipse.jubula.rc.swt.tester.util.CAPUtil;
import org.eclipse.jubula.rc.swt.tester.util.EventListener;
import org.eclipse.jubula.rc.swt.tester.util.SimulatedTooltip;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.toolkit.enums.ValueSets;
import org.eclipse.jubula.tools.internal.constants.TimeoutConstants;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Widget;
/**
 * @author BREDEX GmbH
 * @created 18.02.2013
 */
public class WidgetAdapter extends AbstractComponentAdapter {
    /** the widget */
    private Widget m_component = null;
    
    /**
     * @param objectToAdapt the component
     */
    public WidgetAdapter(Object objectToAdapt) {
        m_component = (Widget) objectToAdapt;
    }
    
    /**
     * {@inheritDoc}
     */
    public Object getRealComponent() {
        return m_component;
    }
    
    /**
     * {@inheritDoc}                  
     */
    public void rcDrop(final int xPos, final String xUnits, final int yPos, 
            final String yUnits, int delayBeforeDrop) {
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        final IRobot robot = getRobot();
        final String modifier = dndHelper.getModifier();
        final int mouseButton = dndHelper.getMouseButton();
        // Note: This method performs the drag AND drop action in one runnable
        // in the GUI-Eventqueue because after the mousePress, the eventqueue
        // blocks!
        try {
            CAPUtil.pressOrReleaseModifiers(modifier, true);

            getEventThreadQueuer().invokeAndWait("startDrag", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() throws StepExecutionException {
                    // drag
                    robot.mousePress(dndHelper.getDragComponent(), null, 
                            mouseButton);

                    robot.shakeMouse();
                    
                    // drop
                    clickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
                    return null;
                }            
            });
            
            WidgetTester.waitBeforeDrop(delayBeforeDrop);
        } finally {
            getRobot().mouseRelease(null, null, mouseButton);
            CAPUtil.pressOrReleaseModifiers(modifier, false);
        }
    }
    
    /**
     * {@inheritDoc}
     */
    public void rcDrag(int mouseButton, String modifier, int xPos, 
            String xUnits, int yPos, String yUnits) {
        // Only store the Drag-Information. Otherwise the GUI-Eventqueue
        // blocks after performed Drag!
        final DragAndDropHelper dndHelper = DragAndDropHelper
            .getInstance();
        dndHelper.setMouseButton(mouseButton);
        dndHelper.setModifier(modifier);
        dndHelper.setDragComponent(null);
        clickDirect(0, mouseButton, xPos, xUnits, yPos, yUnits);
    }
    
    /**
     * clicks into a component. 
     * @param count amount of clicks
     * @param button what button should be clicked
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @throws StepExecutionException error
     */
    protected void clickDirect(int count, int button, int xPos, String xUnits,
            int yPos, String yUnits)
        throws StepExecutionException {

        getRobot().click(
                m_component,
                null,
                ClickOptions.create().setClickCount(count).setMouseButton(
                        button), xPos, xUnits.equalsIgnoreCase(
                            ValueSets.Unit.pixel.rcValue()),
                yPos, yUnits.equalsIgnoreCase(
                        ValueSets.Unit.pixel.rcValue()));
    }


    /**
     * Shows and returns the popup menu
     * @param button MouseButton
     * @return the popup menu
     */
    public AbstractMenuTester showPopup(
            final int button) {
        final Widget component = m_component;
        if (SwtUtils.isMouseCursorInWidget(component)) {
            return showPopup(component, new Runnable() {
                public void run() {
                    RobotTiming.sleepPreShowPopupDelay();
                    
                    getRobot().clickAtCurrentPosition(component, 1, 
                            button);
                }
            });
        }
        return showPopup(50, ValueSets.Unit.percent.rcValue(), 50, 
                ValueSets.Unit.percent.rcValue(), button);
    }

    /**
     * Shows and returns the popup menu
     * 
     * @param xPos what x position
     * @param xUnits should x position be pixel or percent values
     * @param yPos what y position
     * @param yUnits should y position be pixel or percent values
     * @param button MouseButton
     * @return the popup menu
     * @throws StepExecutionException error
     */
    public AbstractMenuTester showPopup(
            final int xPos, final String xUnits,
            final int yPos, final String yUnits, 
            final int button) throws StepExecutionException {
        final Widget component = m_component;
        return showPopup(component, new Runnable() {
            public void run() {
                RobotTiming.sleepPreShowPopupDelay();
                boolean isAbsoluteUnitsX = 
                        ValueSets.Unit.pixel.rcValue().equalsIgnoreCase(
                            xUnits);
                boolean isAbsoluteUnitsY = 
                        ValueSets.Unit.pixel.rcValue().equalsIgnoreCase(
                            yUnits);
                getRobot().click(component, null, 
                    ClickOptions.create().setClickCount(1)
                        .setMouseButton(button), 
                    xPos, isAbsoluteUnitsX, yPos, isAbsoluteUnitsY);
            }
        });
    }

    /**
     * Shows and returns the popup menu
     * 
     * @param component The component for which to open the popup menu.
     * @param showPopup A <code>Runnable</code> that, when run, should display
     *                  a popup menu for the given component.
     * @return the popup menu
     * @throws StepExecutionException error
     */
    private AbstractMenuTester showPopup(final Widget component, 
        final Runnable showPopup) throws StepExecutionException {

        PopupShownCondition cond = new PopupShownCondition();
        EventLock lock = new EventLock();
        final EventListener listener = new EventListener(lock, cond);
        final Display d = component.getDisplay();
        final IEventThreadQueuer queuer = new EventThreadQueuerSwtImpl();
        
        queuer.invokeAndWait("addPopupShownListeners", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    d.addFilter(SWT.Show, listener);
                    return null;
                }
            });
        
        try {
            // showPopup must run in the current thread in order to
            // avoid a race condition.
            showPopup.run();

            synchronized (lock) {
                long timeout = TimeoutConstants.SERVER_TIMEOUT_WAIT_FOR_POPUP;
                long done = System.currentTimeMillis() + timeout; 
                long now;
                while (!lock.isReleased() && (timeout > 0)) {
                    lock.wait(timeout);
                    now = System.currentTimeMillis();
                    timeout = done - now;
                }
            } 
        } catch (InterruptedException e) {
            // ignore
        } finally {
            queuer.invokeAndWait("removePopupShownListeners", new IRunnable<Void>() { //$NON-NLS-1$
                public Void run() {
                    d.removeFilter(SWT.Show, listener);
                    return null;
                }
            });
        }
        if (!lock.isReleased()) {
            throw new StepExecutionException("popup not shown", //$NON-NLS-1$
                    EventFactory.createActionError(
                            TestErrorEvent.POPUP_NOT_FOUND));
        }
        
        MenuTester contextMenu = new MenuTester();
        contextMenu.setComponent(cond.getPopup());
        contextMenu.setContextMenu(true);
        return contextMenu;
    }
    
    /**
     * {@inheritDoc}
     */
    public void showToolTip(final String text, final int textSize, 
        final int timePerWord, final int windowWidth) {

        final Rectangle bounds = getEventThreadQueuer().invokeAndWait(
                "showToolTip.getBounds", new IRunnable<Rectangle>() { //$NON-NLS-1$
                    public Rectangle run() {
                        return SwtUtils.getWidgetBounds(m_component);
                    }
                });

        SimulatedTooltip sp = getEventThreadQueuer().invokeAndWait("showToolTip.initToolTip", new IRunnable<SimulatedTooltip>() { //$NON-NLS-1$
                public SimulatedTooltip run() throws StepExecutionException {
                    return new SimulatedTooltip(timePerWord, text,
                        windowWidth, textSize, bounds);
                }
            });
        sp.start();
        try {
            sp.join();
        } catch (InterruptedException e) {
            throw new StepExecutionException(e);
        }
    }
}
