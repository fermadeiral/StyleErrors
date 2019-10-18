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

import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Widget;

/**
 * The class containing methods which are needed by all IEventListener.
 * 
 * @author BREDEX GmbH
 * @created 19.04.2006
 */
public abstract class BaseSwtEventListener implements Listener {

    /** protected utility constructor. */
    protected BaseSwtEventListener() {
        // do nothing
    }
    
    /**
     * Returns the class name of the given component, null safe.
     * @param component the component to determine the name of the class
     * @return the name of the class or null if the given <code>component</code> is null.
     */
    protected static Class getComponentClass(Widget component) {
        return (component == null) ? null : component.getClass();
    }
}