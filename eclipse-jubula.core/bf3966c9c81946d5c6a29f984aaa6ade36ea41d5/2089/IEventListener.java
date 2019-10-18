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
package org.eclipse.jubula.rc.swing.listener;

import java.awt.event.AWTEventListener;

/**
 * The interface for defined AWTEventListener. <br>
 * Extends AWTEventListener by:
 * <lu>
 * <li><code>long getEventMask()</code></li>
 * </lu>
 * 
 * @author BREDEX GmbH
 * @created 23.08.2004
 */
public interface IEventListener extends AWTEventListener {
    // do nothing
}
