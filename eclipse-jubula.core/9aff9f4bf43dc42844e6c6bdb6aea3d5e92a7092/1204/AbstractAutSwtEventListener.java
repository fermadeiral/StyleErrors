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
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.commands.ChangeAUTModeCommand;
import org.eclipse.jubula.rc.common.exception.ComponentNotFoundException;
import org.eclipse.jubula.rc.common.exception.UnsupportedComponentException;
import org.eclipse.jubula.rc.common.listener.AUTEventListener;
import org.eclipse.jubula.rc.swt.SwtAUTServer;
import org.eclipse.jubula.rc.swt.utils.SwtUtils;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.graphics.Region;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Abstract superclass for Listeners to be added to the AUT-Toolkit<br>
 * Listens to: <br>
 * SWT.MouseMove | SWT.MouseEnter |SWT.Arm | SWT.MouseExit </br>
 * The mouse event ENTERED, moved are used to determine 
 * the component under the mouse (the m_currentComponent).
 * 
 * Known subclasses are: <br>
 * MappingListener, RecordListener, CheckListener
 *
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public abstract class AbstractAutSwtEventListener extends BaseSwtEventListener
    implements AUTEventListener {
    
    /** the logger */
    private static final Logger LOG = LoggerFactory.getLogger(
            AbstractAutSwtEventListener.class);
    /** the lock object for m_currentComponent */
    private Object m_componentLock = new Object();
    
    /** the widget under the mouse */
    private Widget m_currentComponent = null;
    
    /** the object deciding whether a KeyEvent is used for selecting a component to the object map */
    private KeyAcceptor m_acceptor = new KeyAcceptor();
    
    /** last event for not double firing events */
    private Event m_lastEvent;
    
    /** the gc to paint the border */
    private Shell m_borderShell;

    /** the old widget */
    private Widget m_oldWidget;
    
    /**
     * protected constructor
     */
    protected AbstractAutSwtEventListener() {
        super();
    }
    
    /**
     * {@inheritDoc}
     */
    public long[] getEventMask() {
        return new long[]{
            SWT.MouseMove, SWT.MouseUp, SWT.MouseDown, SWT.MouseEnter,
            SWT.KeyDown, SWT.Collapse, SWT.Expand, SWT.MenuDetect, SWT.Show,
            SWT.Selection, SWT.FocusIn, SWT.FocusOut, SWT.Traverse}; 
            //SWT.Modify, SWT.Arm, SWT.SetData, SWT.UP, SWT.MouseEnter
    }

    /**
     * @return Returns the lastEvent.
     */
    protected Event getLastEvent() {
        return m_lastEvent;
    }

    /**
     * @param lastEvent The lastEvent to set.
     */
    protected void setLastEvent(Event lastEvent) {
        m_lastEvent = lastEvent;
    }
    
    /**
     * @return Returns the componentLock.
     */
    protected Object getComponentLock() {
        return m_componentLock;
    }

    /**
     * @return Returns the currentComponent.
     */
    protected Widget getCurrentComponent() {
        return m_currentComponent;
    }

    /**
     * @param currentComponent The currentComponent to set.
     */
    protected void setCurrentComponent(Widget currentComponent) {
        m_currentComponent = currentComponent;
    }

    /**
     * @return Returns the acceptor.
     */
    protected KeyAcceptor getAcceptor() {
        return m_acceptor;
    }

    /**
     * Refreshes the aut.
     */
    public void cleanUp() {
        final Display d = ((SwtAUTServer)AUTServer.getInstance())
            .getAutDisplay();
        if (d != null && !d.isDisposed()) {
            d.syncExec(new Runnable() {
                public void run() {
                    if (m_borderShell != null) {
                        m_borderShell.getRegion().dispose();
                        m_borderShell.close();
                        m_borderShell.dispose();
                        m_borderShell = null;
                    }
                }
            });
        }
    }

    /**
     * Handles the given Event
     * @param event the event to handle.
     */
    public abstract void handleEvent(final Event event);
    
    /**
     * draws a rectangle on the selected widget
     */
    protected void highlightComponent() {
        
        try {
            final Class componentClass = getComponentClass(
                getCurrentComponent());
            if (componentClass != null) {
                AUTServerConfiguration.getInstance().getImplementationClass(
                    componentClass);
            }
        } catch (IllegalArgumentException e) {
            LOG.warn(e.getLocalizedMessage(), e);
        } catch (UnsupportedComponentException uce) {
            closeBorderShell();
            return;
        }
        final Widget widget = getCurrentComponent();
        if (widget == null || widget == m_oldWidget) {
            return;
        }
        final Rectangle widgetBounds = SwtUtils.getWidgetBounds(widget);
        if (widgetBounds == null) {
            return;
        }
        setCurrentComponent(widget);
        closeBorderShell();

        // define a rectangular region around the widget
        final int b = 2;
        final int wX = widgetBounds.x;
        final int wY = widgetBounds.y;
        final int wW = widgetBounds.width;
        final int wH = widgetBounds.height;
        final Region region = new Region();
        region.add(new Rectangle(0, 0, wW + b * 2, wH + b * 2));
        // define transparent rectangular region
        region.subtract(new Rectangle(b, b, wW, wH));
        final Display display = ((SwtAUTServer)AUTServer.getInstance())
                .getAutDisplay();
        m_borderShell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP
                | SWT.NO_FOCUS);
        m_borderShell.setBackground(getBorderColor());
        // define the shape of the shell using setRegion
        m_borderShell.setRegion(region);
        final Rectangle size = region.getBounds();
        m_borderShell.setSize(size.width, size.height);
        m_borderShell.setLocation(wX - b, wY - b);
        m_borderShell.setVisible(true);
        m_oldWidget = widget;
    }

    /**
     * closes the m_borderShell.
     */
    private void closeBorderShell() {
        if (m_borderShell != null) {
            m_borderShell.getRegion().dispose();
            m_borderShell.close();
            m_borderShell.dispose();
            m_borderShell = null;
            setCurrentComponent(null);
            m_oldWidget = null;
        }
    }
    
    /**
     * change CheckModeState
     * @param mode int
     */
    protected void changeCheckModeState(int mode) {
        ChangeAUTModeMessage msg = new ChangeAUTModeMessage();
        msg.setMode(mode);
        msg.setMappingKey(AUTServerConfiguration.getInstance().getMappingKey());
        msg.setMappingWithParentsKey(AUTServerConfiguration
                .getInstance().getMappingWithParentsKey());
        msg.setMappingKeyModifier(AUTServerConfiguration
                .getInstance().getMappingKeyMod());
        msg.setMappingWithParentsKeyModifier(AUTServerConfiguration
                .getInstance().getMappingWithParentsKeyMod());
        msg.setKey2(AUTServerConfiguration.getInstance().getKey2());
        msg.setKey2Modifier(
                AUTServerConfiguration.getInstance().getKey2Mod());
        msg.setCheckModeKey(AUTServerConfiguration.getInstance()
                .getCheckModeKey());
        msg.setCheckModeKeyModifier(
                AUTServerConfiguration.getInstance().getCheckModeKeyMod());
        msg.setCheckCompKey(AUTServerConfiguration.getInstance()
                .getCheckCompKey());
        msg.setCheckCompKeyModifier(
                AUTServerConfiguration.getInstance().getCheckCompKeyMod());
        
        msg.setSingleLineTrigger(
                AUTServerConfiguration.getInstance().getSingleLineTrigger());
        msg.setMultiLineTrigger(
                AUTServerConfiguration.getInstance().getMultiLineTrigger());

        ChangeAUTModeCommand cmd = new ChangeAUTModeCommand();
        cmd.setMessage(msg);
        try {
            AUTServer.getInstance().getCommunicator().send(
                    cmd.execute());
        } catch (CommunicationException e) {
            LOG.error(e.getLocalizedMessage(), e);
        }
    }

        
    /**
     * Checks the Widget at the current mouse pointer location and calls 
     * setCurrentComponent(Widget currentComponent)
     */
    protected void setCurrentWidget() {
        final Widget widget = SwtUtils.getWidgetAtCursorLocation();
        setCurrentComponent(widget);
    }
    
    
    /**
     * {@inheritDoc}
     */
    protected abstract void handleKeyEvent(final Event event);
    
        
    /**
     * {@inheritDoc}
     */
    public void update() {
        // FIXME Clemens
    }

    /**
     * {@inheritDoc}
     * @param comp
     * @return
     */
    public boolean highlightComponent(IComponentIdentifier comp) {
        Widget component = null;
        try {
            component = ComponentHandler.findComponent(comp, true, 10000);
            setCurrentComponent(component);
            ((SwtAUTServer)AUTServer.getInstance()).getAutDisplay().syncExec(
                new Runnable() {
                    public void run() {
                        highlightComponent();
                    }
                });
        } catch (ComponentNotFoundException e) {
            LOG.debug("Component with IComponentIdentifier '" //$NON-NLS-1$
                + String.valueOf(comp) + "' not found!", e); //$NON-NLS-1$
            return false;
        } catch (IllegalArgumentException e) {
            LOG.debug(e.getLocalizedMessage(), e);
            return false;
        }
        return component != null;
    }

    /**
     * overwrite it in sub classes
     * @return the border color
     */
    protected abstract Color getBorderColor();
}