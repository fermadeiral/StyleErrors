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

import org.eclipse.jubula.rc.common.exception.RobotException;
import org.eclipse.jubula.rc.common.listener.BaseAUTListener;
import org.eclipse.jubula.tools.internal.objects.event.EventFactory;
import org.eclipse.jubula.tools.internal.objects.event.TestErrorEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author BREDEX GmbH
 * @created 20.04.2006
 */
public class FocusTracker extends BaseSwtEventListener 
    implements BaseAUTListener {
    /** the logger */
    private static Logger log = LoggerFactory.getLogger(FocusTracker.class);
        
    /** <code>focusOwner</code> the current tracked focus owning Widget */
    private static Widget focusOwner = null;
  
    /**
     * {@inheritDoc}
     */
    public long[] getEventMask() {
        return new long[]{ SWT.FocusIn, SWT.FocusOut };
    }
    
    /**
     * @return the focus Owning Component known by event tracking
     */
    public static Widget getFocusOwner() {
        if (focusOwner == null || focusOwner.isDisposed()) {
            // Use the display's currently focused control as a fallback.
            Display.getDefault().syncExec(new Runnable() {
                public void run() {
                    focusOwner = Display.getDefault().getFocusControl();
                }
            });
        }
        if (focusOwner == null || focusOwner.isDisposed()) {
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
    public void handleEvent(Event event) {
        ClassLoader originalCL = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(
            this.getClass().getClassLoader());
        try {
            if (log.isDebugEnabled()) {
                log.debug("Event: ", event); //$NON-NLS-1$
            }
            switch (event.type) {
                case SWT.FocusIn:
                    // do we need a synchronized here ? I think not.
                    // Java statements should be atomar (?)
                    focusOwner = event.widget;
                    break;
                case SWT.FocusOut:
                    focusOwner = null;
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
}