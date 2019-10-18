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

/**
 * Interface for PO-Classes which have a timestamp.
 *
 * @author BREDEX GmbH
 * @created Oct 12, 2007
 * 
 */
public interface ITimestampPO extends IPersistentObject {
    
    /**
     * Sets the timestamp.
     * @param timestamp the timestamp.
     */
    public void setTimestamp(long timestamp);
    
    /**
     * @return the timestamp
     */
    public long getTimestamp();
    
}
