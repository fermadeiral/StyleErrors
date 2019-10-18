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
package org.eclipse.jubula.rc.common.listener;



/**
 * @author BREDEX GmbH
 * @created 10.07.2006
 */
public interface BaseAUTListener {
    /**
     * @return the event mask this listener listens to 
     */
    public long[] getEventMask();
}