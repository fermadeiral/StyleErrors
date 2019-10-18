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
package org.eclipse.jubula.client.core.model;

import java.util.Collection;

import org.eclipse.jubula.tools.internal.exception.InvalidDataException;


/**
 * Interface for model classes which can contain 
 * EventExecTestCasePO objects (EventHandler).<br>
 * The methods in this interface allow the access to the EventHandlers.
 *
 * @author BREDEX GmbH
 * @created 27.04.2005
 *
 */
public interface IEventHandlerContainer {
    /**
     * <code>MAX_NUMBER_OF_EVENT_HANDLER</code> the currently maximum no of eh;
     * there are currently only 4 valid event types
     */
    public static final int MAX_NUMBER_OF_EVENT_HANDLER = 4;
    
    /**
     * @param eventTC eventExecTestCase to add
     * @throws InvalidDataException if an eventTestCase is already 
     * existent for the associated event
     */
    public abstract void addEventTestCase(IEventExecTestCasePO eventTC)
        throws InvalidDataException;


    /**
     * get the eventExecTC for a given eventType
     * @param eventType eventType for wanted eventExecTC
     * @return the eventExecTC for given eventType or null
     */
    public abstract IEventExecTestCasePO getEventExecTC(String eventType);

    /**
     * @return all EventExecTestCases of this SpecTestCasePO.
     */
    public abstract Collection <IEventExecTestCasePO> getAllEventEventExecTC();
}
