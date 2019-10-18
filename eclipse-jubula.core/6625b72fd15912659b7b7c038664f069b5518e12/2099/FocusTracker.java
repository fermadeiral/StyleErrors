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
import java.awt.Component;
import java.awt.event.FocusEvent;

import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.rc.common.logger.AutServerLogger;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;


/**
 * @author BREDEX GmbH
 * @created 24.06.2005
 */
public class FocusTracker extends BaseAWTEventListener 
    implements BaseAUTListener {
    /** the logger */
    private static AutServerLogger log = 
        new AutServerLogger(FocusTracker.class);
    
    /** the event mask for the events this listener is interesting in */
    private static final long[] EVENT_MASK = new long[] {
        AWTEvent.FOCUS_EVENT_MASK};
    
    /**
     * <code>focusOwner</code> the current tracked focus owning Component
     */
    private static Component focusOwner = null;
  
    /**
     * {@inheritDoc}
     */
    public long[] getEventMask() {
        long[] eventMask = EVENT_MASK; // see findBugs
        return eventMask;
    }
    /**
     * @return the focus Owning Component known by event tracking
     */
    public static Component getFocusOwner() {
        if (focusOwner == null) {
            log.error("No Focus-Owner found!"); //$NON-NLS-1$
            throw new RobotException("No Focus-Owner found!",  //$NON-NLS-1$
                EventFactory.createActionError(
                    TestErrorEvent.COMP_NOT_FOUND));
        }
        return focusOwner;
    }
    /**
     * {@inheritDoc}
     */
    public void  eventDispatched(AWTEvent event) {
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(this.getClass()
            .getClassLoader());
        try {
            if (log.isDebugEnabled()) {
                log.debug(event);
            }
            switch (event.getID()) {
                case FocusEvent.FOCUS_GAINED:
                    // do we need a synchronized here ? I think not.
                    // Java statements should be atomar (?)
                    setFocusOwner(((FocusEvent)event).getComponent());
                    break;
                case FocusEvent.FOCUS_LOST:
                    setFocusOwner(null);
                    break;
                default:
                    // ignore
                    break;
            }
        } catch (Throwable t) {
            log.error("exception during FocusTracker", t); //$NON-NLS-1$
        } finally {
            Thread.currentThread().setContextClassLoader(originalCL);
        }
    }
    
    /**
     * @param owner set focus owner  // see findBugs
     */
    private void setFocusOwner(Component owner) { // see findBugs
        focusOwner = owner;
    }
}