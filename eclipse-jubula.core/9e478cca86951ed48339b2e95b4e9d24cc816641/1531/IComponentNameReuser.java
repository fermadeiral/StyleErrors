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

import org.eclipse.jubula.client.core.businessprocess.IWritableComponentNameCache;
import org.eclipse.jubula.tools.internal.xml.businessmodell.Component;


/**
 * Encapsulates functionality for model objects that use Component Names.
 *
 * @author BREDEX GmbH
 * @created Feb 6, 2009
 */
public interface IComponentNameReuser {

    /**
     * Updates the receiver to use the Component Name with the new GUID
     * instead of the one with the old GUID.
     * 
     * @param oldGuid The GUID of the Component Name that is no longer reused.
     *                May be <code>null</code>, which indicates that no
     *                Component Name was being used.
     * @param newGuid The GUID of the Component Name that is now to be reused.
     *                May be <code>null</code>, which indicates that no
     *                Component Name will be used.
     */
    public void changeCompName(String oldGuid, String newGuid);
    
    /**
     * 
     * @param compNameCache The cache that can be used to query Component
     *                      Name type information.
     * @param availableComponents .
     * @return the Component Type that this reuser implies.
     */
    public String getComponentType(
            IWritableComponentNameCache compNameCache, 
            Collection<Component> availableComponents);
}
