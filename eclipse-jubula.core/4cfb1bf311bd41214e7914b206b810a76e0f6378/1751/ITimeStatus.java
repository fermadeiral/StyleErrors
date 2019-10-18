/*******************************************************************************
 * Copyright (c) 2015 BREDEX GmbH.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     BREDEX GmbH - initial API and implementation and/or initial documentation
 *******************************************************************************/
package org.eclipse.jubula.client.core.status;

/** Marker Interface for a Status with time stamp 
 * @author BREDEX GmbH
 * @created 06.08.2015
 **/
public interface ITimeStatus {

    /**
     * get the time in ms 
     * @return time in ms
     */
    public long getTime();
}
