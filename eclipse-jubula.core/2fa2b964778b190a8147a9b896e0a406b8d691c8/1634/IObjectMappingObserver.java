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
package org.eclipse.jubula.client.core.businessprocess;

import java.util.EventListener;

/**
 * @author BREDEX GmbH
 * @created 19.10.2004
 */
public interface IObjectMappingObserver extends EventListener {
    
    /**
     * Event for a recorded step
     */
    public static final int EVENT_STEP_RECORDED = 1;
    
    /**
     * Event for a mapped object
     */
    public static final int EVENT_COMPONENT_MAPPED = 2;

    /**
     * @param event
     *      Occuring Event
     * @param obj
     *      object
     */
    public void update(int event, Object obj);
    
}
