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
package org.eclipse.jubula.client.ui.rcp.controllers.dnd.objectmapping;



/**
 * Utility class for limiting 
 * {@link org.eclipse.jface.util.LocalSelectionTransfer}s. 
 *
 * @author BREDEX GmbH
 * @created Aug 19, 2010
 */
public class ObjectMappingTransferHelper {

    /** the token for the current DnD operation */
    private static Object dndToken;
    
    /**
     * Private constructor for utility class.
     */
    private ObjectMappingTransferHelper() {
        // Nothing to initialize
    }
    
    /**
     * Sets the token for the current drag operation. Call this method when
     * starting a drag operation that needs to be limited in some way.
     * 
     * @param theDndToken The token to set.
     */
    public static void setDndToken(Object theDndToken) {
        dndToken = theDndToken;
    }

    /**
     * 
     * @return the token set by the most recent drag operation (may be 
     *         <code>null</code>). Call this method when evaluating a drop 
     *         that might be limited in some way.
     */
    public static Object getDndToken() {
        return dndToken;
    }
}
