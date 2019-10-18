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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import org.eclipse.jubula.communication.internal.Communicator;
import org.eclipse.jubula.communication.internal.message.ChangeAUTModeMessage;
import org.eclipse.jubula.rc.common.AUTServer;
import org.eclipse.jubula.rc.common.AUTServerConfiguration;
import org.eclipse.jubula.rc.common.commands.ChangeAUTModeCommand;
import org.eclipse.jubula.rc.common.exception.UnsupportedComponentException;
import org.eclipse.jubula.rc.common.listener.AUTEventListener;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.rc.swing.tester.util.TesterUtil;
import org.eclipse.jubula.tools.internal.exception.CommunicationException;


/**
 * Abstract superclass for Listeners to be added to the AUT-Toolkit<br>
 * Listens to: <br>
 * AWTEvent.MOUSE_EVENT_MASK, <br>
 * AWTEvent.MOUSE_MOTION_EVENT_MASK, <br>
 * AWTEvent.KEY_EVENT_MASK <br>
 * The mouse event ENTERED, EXITED, moved and click are used to determine 
 * the component under the mouse (the m_currentComponent).
 * 
 * Known subclasses are: <br>
 * MappingListener, RecordListener, CheckListener
 *
 * @author BREDEX GmbH
 * @created 17.01.2006
 */
public abstract class AbstractAutSwingEventListener 
    extends BaseAWTEventListener 
    implements IEventListener, AUTEventListener {

    /**
     * delay for high lighting in case of a mouse event PRESSED, RELEASED, which
     * may changes the appearances temporary, e.g. the 3D effect of JButton
     */
    protected static final int REPAINT_DELAY = 5;
    
    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(AbstractAutSwingEventListener.class);
    
    
    /** the event mask for the events this listener is interesting in*/
    private static final long[] EVENT_MASK = new long[] {
        AWTEvent.MOUSE_EVENT_MASK,
        AWTEvent.MOUSE_MOTION_EVENT_MASK,
        AWTEvent.KEY_EVENT_MASK,
        AWTEvent.ITEM_EVENT_MASK,
        AWTEvent.ACTION_EVENT_MASK,
        AWTEvent.TEXT_EVENT_MASK,
        AWTEvent.FOCUS_EVENT_MASK,
        AWTEvent.COMPONENT_EVENT_MASK,
        AWTEvent.WINDOW_EVENT_MASK,
        AWTEvent.WINDOW_FOCUS_EVENT_MASK,
        AWTEvent.WINDOW_STATE_EVENT_MASK};
    
    
    /** the lock object for m_currentComponent */
    private Object m_componentLock = new Object();
    
    
    /**
     * the component under the mouse, for top-level components this is a
     * Component and not a JComponent.
     */
    private Component m_currentComponent = null;
    
    
    /**
     * the object deciding whether a KeyEvent is used for selecting a component
     * to the object map
     */
    private KeyAcceptor m_acceptor = new KeyAcceptor();
    
    
    /**
     * last event for not double firing events
     */
    private AWTEvent m_lastEvent;
    
    /** Flag if  m_currentComponent is highlighted or not*/
    private boolean m_isHighLighted = false;
    
    
    /** 
     * {@inheritDoc}
     */
    public void eventDispatched(final AWTEvent event) {
        ClassLoader oldCL = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(
                this.getClass().getClassLoader());
            handleEvent(event);
        } finally {
            Thread.currentThread().setContextClassLoader(oldCL);
        }
    }
    
    /**
     * Handles the given AWTEvent
     * @param event the event to handle.
     */
    protected abstract void handleEvent(AWTEvent event);
    
    /**
     * This method is called by the AUTServer AFTER this instance is removed as
     * AWTEventListener for cleaning up purpose. <br>
     * 
     * low lights the last hight lighted component.
     */
    public void cleanUp() {
        // does not sychronize on component lock, because this method is called
        // after removing as AWTEventListener
        synchronized (m_componentLock) {
            if (getCurrentComponent() != null) {
                try {
                    AUTServerConfiguration.getInstance()
                            .getImplementationClass(
                                    getComponentClass(
                                            getCurrentComponent()));
                    
                    TesterUtil.lowLight(m_currentComponent);

                } catch (IllegalArgumentException iae) {
                    log.error(iae);
                } catch (UnsupportedComponentException uce) {
                    log.warn(uce);
                }
            }
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
            Communicator clientCommunicator =
                AUTServer.getInstance().getCommunicator();
            if (clientCommunicator != null 
                    && clientCommunicator.getConnection() != null) {
                AUTServer.getInstance().getCommunicator().send(
                        cmd.execute());
            }
        } catch (CommunicationException e) {
            e.printStackTrace();
        }
    }
    
   /**
    * {@inheritDoc}
    */
    public long[] getEventMask() {
        long[] eventMask = EVENT_MASK; // see findBugs
        return eventMask;
    }


    /**
     * @return Returns the lastEvent.
     */
    protected AWTEvent getLastEvent() {
        return m_lastEvent;
    }


    /**
     * @param lastEvent The lastEvent to set.
     */
    protected void setLastEvent(AWTEvent lastEvent) {
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
    protected Component getCurrentComponent() {
        return m_currentComponent;
    }


    /**
     * @param currentComponent The currentComponent to set.
     */
    protected void setCurrentComponent(Component currentComponent) {
        m_currentComponent = currentComponent;
    }


    /**
     * @return Returns the acceptor.
     */
    protected KeyAcceptor getAcceptor() {
        return m_acceptor;
    }

    
    /**
     * Handles the High-/Lowlighting.<br>
     * When High-/LowLighting afer a click use highlightClicked.
     * @param source Component
     * @param implClass IImplementationClass
     * @param highlightColor highlight clolor
     */
    protected void highlight(Component source, final Object implClass, 
        final Color highlightColor) {
        synchronized (getComponentLock()) {
            if (getCurrentComponent() != null 
                && getCurrentComponent() != source) {
                TesterUtil.lowLight(getCurrentComponent());
                setHighLighted(false);
            }
            setCurrentComponent(source);
            Window windowAncestor = 
                SwingUtilities.getWindowAncestor(getCurrentComponent());
            if (windowAncestor != null 
                    && windowAncestor.getFocusOwner() != null) {
                try {
                    AUTServerConfiguration.getInstance()
                        .getImplementationClass(
                            getComponentClass(getCurrentComponent()));
 
                    TesterUtil.highLight(getCurrentComponent(), highlightColor);
                    setHighLighted(true);
                } catch (IllegalArgumentException e) {
                    log.error("unexpected exception", e); //$NON-NLS-1$
                } catch (UnsupportedComponentException e) {
                    /* This means that the component that we wish to highlight is 
                     * not supported.
                     * The component will not be highlighted
                     */
                }
            }
        }
    }

    /**
     * Use this to handle the Highlighting after a Click.
     * @param implClass IImplementationClass
     * @param highlightColor highlight clolor
     */
    protected void highlightClicked(final Object implClass, 
        final Color highlightColor) {
        
        // start a thread, to hightligth AFTER the component, e.g. 
        // JButton has processed the event and possible new painted
        Timer timer = new Timer(REPAINT_DELAY, 
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    synchronized (getComponentLock()) {
                        if (getCurrentComponent() != null
                                && getCurrentComponent().isShowing()
                                && SwingUtilities.getWindowAncestor(
                                        getCurrentComponent()) != null
                                && SwingUtilities.getWindowAncestor(
                                        getCurrentComponent()).getFocusOwner() 
                                            != null) {
                            try {
                                AUTServerConfiguration.getInstance()
                                    .getImplementationClass(
                                            getComponentClass(
                                                    getCurrentComponent()));
                                TesterUtil.highLight(
                                        getCurrentComponent(), highlightColor);
                            } catch (IllegalArgumentException iae) {
                                log.error("unexpected exception", iae); //$NON-NLS-1$
                            } catch (UnsupportedComponentException uce) {
                                /* This means that the component that we wish to highlight is 
                                 * not supported.
                                 * The component will not be highlighted
                                 */
                            }
     
                        }
                    }
                }
            });
        timer.setRepeats(false);
        timer.start();
    }
    
    
    /**
     * Updates the highlighting
     * @param source Component
     * @param implClass IImplementationClass
     * @param highlightColor highlight color
     */
    protected void updateHighlighting(Component source, final Object implClass,
        final Color highlightColor) {
        
        if (isHighLighted()) {
            highlight(source, implClass, highlightColor);
        }
    }
    
    
    /**
     * @return if the getCurrentComponent() is highlighted or not
     */
    public boolean isHighLighted() {
        return m_isHighLighted;
    }

    /**
     * @param isHighLighted true for highlighted otherwise false.
     */
    protected void setHighLighted(boolean isHighLighted) {
        m_isHighLighted = isHighLighted;
    }

}
