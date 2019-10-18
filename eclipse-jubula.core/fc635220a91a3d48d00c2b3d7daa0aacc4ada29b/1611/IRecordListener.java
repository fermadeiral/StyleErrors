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
package org.eclipse.jubula.client.core.events;

import java.util.EventListener;

import org.eclipse.jubula.client.core.model.ICapPO;
import org.eclipse.jubula.tools.internal.objects.IComponentIdentifier;


/**
 * The interface for listening to events concerning the AUTServer.
 *
 * @author BREDEX GmbH
 * @created 20.09.2005
 *
 */
public interface IRecordListener extends EventListener {
    /**
     * This method will be called when a cap is recorded.
     * The cap will be passed to the listener
     * 
     * @param newCap -
     *      the CapPO. May be <code>null</code> if the test step could not 
     *      successfully be created.
     * @param ci
     *      ComponentIdentifier
     * @param hasDefaultMapping
     *      if the cap which is recorded has not component name
     */
    public void capRecorded(ICapPO newCap, IComponentIdentifier ci,
            boolean hasDefaultMapping);
}
