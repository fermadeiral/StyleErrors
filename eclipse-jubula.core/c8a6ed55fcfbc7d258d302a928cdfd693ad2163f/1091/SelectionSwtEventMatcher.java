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
package org.eclipse.jubula.rc.swt.driver;

import java.util.Iterator;
import java.util.List;

import org.eclipse.jubula.rc.swt.listener.ComponentHandler;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Widget;


/**
 * This event matcher checks whether a mouse click event matches the
 * requested properties. The properties are defined by a
 * <code>ClickOptions</code> instance.
 *
 * @author BREDEX GmbH
 * @created 26.07.2006
 */
public class SelectionSwtEventMatcher extends DefaultSwtEventMatcher {
       
    /**
     * Creates a new matcher which checks SWT events against a mouse event type
     * that is determined from the given ClickOptions.
     */
    public SelectionSwtEventMatcher() {
        super(SWT.Selection);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFallBackEventMatching(List eventObjects, 
        Object graphicsComponent) {
            
        // Checks whether we've received a selection event
        Iterator eventIt = eventObjects.iterator();
        while (eventIt.hasNext()) {
            Event event = (Event)eventIt.next();
            if (event.widget == graphicsComponent
                && event.type == SWT.Selection) {
                
                return true;
            }
        }
        
        try {
            // checks if the component is visible (= in hierarchy container)
            if (ComponentHandler.getAutHierarchy().getHierarchyContainer(
                    (Widget)graphicsComponent) == null) {
                    
                return true;
            
            }
        } catch (IllegalArgumentException e) {
            //do nothing
        }
        return false;
    }
}