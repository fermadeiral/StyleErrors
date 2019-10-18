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
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.eclipse.jubula.rc.common.logger.AutServerLogger;


/**
 * The class containing methods which are needed by all IEventListener.
 * 
 * @author BREDEX GmbH
 * @created 26.08.2004
 */
public abstract class BaseAWTEventListener implements IEventListener {

    /** the logger */
    private static AutServerLogger log = new AutServerLogger(
        BaseAWTEventListener.class); 
    
    /**
     * Returns the component on which the event initially occurred. <br>
     * For JPopupMenu the invoker is returned.
     * 
     * @param event
     *            the occurred event
     * @return the event source or null if the source is not an instance of
     *         <code>Component</code>.
     */
    protected Component getEventSource(AWTEvent event) {
        Component result = null;
        Object source = event.getSource();
        if (log.isDebugEnabled()) {
            log.debug("source:" + source.toString()); //$NON-NLS-1$
        }
        
        if (source instanceof JPopupMenu) {
            result = ((JPopupMenu) source).getInvoker();
        } else if (source instanceof Component) {
            result = (Component) source;
            if (event instanceof MouseEvent) {
                MouseEvent ev = (MouseEvent)event;
                Component mouseOn =
                    SwingUtilities.getDeepestComponentAt(result, 
                        ev.getX(), 
                        ev.getY());
                if (mouseOn != null) {
                    result = mouseOn;
                }
            }
        }
        /*Component parent = ((Component)source).getParent();
        if (parent != null && parent instanceof JPopupMenu) {
            result = parent;
        }*/
        return result;
    }

    /**
     * Returns the class name of the given component, null safe.
     * 
     * @param component
     *            the component to determine the name of the class
     * @return the name of the class or null if the given <code>component</code>
     *         is null.
     */
    protected static Class getComponentClass(Component component) {
        return (component == null) ? null : component.getClass();
    }

    
}
